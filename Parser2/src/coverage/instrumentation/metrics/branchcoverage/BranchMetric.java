package coverage.instrumentation.metrics.branchcoverage;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.jdom.Comment;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;

import coverage.instrumentation.bpelxmltools.BpelXMLTools;
import coverage.instrumentation.exception.BpelException;
import coverage.instrumentation.metrics.IMetric;

public class BranchMetric implements IMetric {

	public static final String METRIC_NAME = "Branchmetric";

	private static final String BRANCH_LABEL = "branch";

	private static final String LINK_LABEL = "link";

	private static int count = 0;

	private static BranchMetric instance = null;

	public static String getNextLabel() {
		return BRANCH_LABEL + (count++);
	}

	public static String getNextLinkLabel() {
		return LINK_LABEL + getNextLabel();
	}

	public static BranchMetric getInstance() {
		if (instance == null) {
			instance = new BranchMetric();
		}
		return instance;
	}

	public static void insertMarkerForBranch(Element activity,
			String additionalInfo) {
		insertMarkerBevorAllActivities(activity, additionalInfo);
		insertMarkerAfterAllActivities(activity, additionalInfo);
	}

	public static void insertMarkerBevorAllActivities(Element activity,
			String additionalInfo) {
		if (!BpelXMLTools.isSequence(activity)) {
			activity = BpelXMLTools.ensureElementIsInSequence(activity);
		}
		activity.addContent(0, new Comment(BranchMetric.getNextLabel()
				+ additionalInfo));
	}

	public static void insertMarkerAfterAllActivities(Element activity,
			String additionalInfo) {
		if (!BpelXMLTools.isSequence(activity)) {
			activity = BpelXMLTools.ensureElementIsInSequence(activity);
		}
		activity.addContent(new Comment(BranchMetric.getNextLabel()
				+ additionalInfo));
	}

	/**
	 * 
	 * @param activity
	 *            muss innerhalb Sequence sein
	 */
	public static void insertMarkerAfterActivity(Element activity) {
		Element parent = activity.getParentElement();
		parent.addContent(parent.indexOf(activity) + 1, new Comment(
				getNextLabel()));
	}

	private Hashtable<String, IStructuredActivity> structured_activity_handler = new Hashtable<String, IStructuredActivity>();

	private List<Element> elements_to_log;

	private BranchMetric() {
		elements_to_log = new ArrayList<Element>();
		structured_activity_handler.put("flow", new FlowActivityHandler());
		structured_activity_handler.put("sequence",
				new SequenceActivityHandler());
		structured_activity_handler.put("if", new IfActivityHandler());
		structured_activity_handler.put("while", new WhileActivityHandler());
		structured_activity_handler.put("repeatUntil",
				new RepeatUntilActivityHandler());
		structured_activity_handler
				.put("forEach", new ForEachActivityHandler());
		structured_activity_handler.put("pick", new PickActivityHandler());
	}

	public void insertMarker(Element element) throws BpelException {
		Element next_element;
		Iterator iterator2 = element.getDescendants(new ElementFilter());
		while (iterator2.hasNext()) {
			next_element = (Element) iterator2.next();
			if (BpelXMLTools.isStructuredActivity(next_element)) {
				elements_to_log.add(next_element);
			}
		}
		for (Iterator<Element> iter = elements_to_log.iterator(); iter
				.hasNext();) {
			next_element = iter.next();
			String next_element_name = next_element.getName();
			if (structured_activity_handler.containsKey(next_element_name)) {
				structured_activity_handler.get(next_element_name)
						.insertMarkerForBranchCoverage(next_element);
			}
		}
	}

	@Override
	public String toString() {
		return METRIC_NAME;
	}

}