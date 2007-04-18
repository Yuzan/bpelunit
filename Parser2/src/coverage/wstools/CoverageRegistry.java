package coverage.wstools;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

import coverage.instrumentation.bpelxmltools.BasicActivity;
import coverage.instrumentation.metrics.IMetric;
import coverage.instrumentation.metrics.MetricHandler;
import coverage.instrumentation.metrics.branchcoverage.BranchMetric;
import coverage.instrumentation.metrics.statementcoverage.Statementmetric;
import coverage.result.IStatistic;
import coverage.result.Statistic;

public class CoverageRegistry {

	private static CoverageRegistry instance = null;

	private Hashtable allMetricsTable;

	private Logger logger;

	public static CoverageRegistry getInstance() {

		if (instance == null) {
			instance = new CoverageRegistry();
		}
		return instance;
	}

	public void initialize() {
		allMetricsTable = new Hashtable();
	}

	private CoverageRegistry() {
		logger = Logger.getLogger(getClass());
	}

	public void addMetric(IMetric metric) {
		if (metric instanceof Statementmetric) {
			createDataStructure((Statementmetric) metric);
		} else if (metric instanceof BranchMetric) {
			createDataStructure((BranchMetric) metric);
		}
	}

	private void createDataStructure(BranchMetric metric) {
		allMetricsTable.put(BranchMetric.BRANCH_LABEL, new Hashtable());

		allMetricsTable.put(BranchMetric.POSITIV_LINK_LABEL, new Hashtable());
		allMetricsTable.put(BranchMetric.NEGATIV_LINK_LABEL, new Hashtable());
	}

	private void createDataStructure(Statementmetric statementmetric) {
		for (Iterator<String> iter = statementmetric.getBasisActivities()
				.iterator(); iter.hasNext();) {
			String name = iter.next();
			System.out.println("!!!!--" + name);
			allMetricsTable.put(name, new Hashtable());
		}

	}

	public void addMarker(String marker) {
		logger.info("---Es wird auf Marker " + marker + " registriert.");
		String prefix = marker.substring(0, marker.indexOf('_'));
		CoverageLabelStatus status = new CoverageLabelStatus();
		// if(prefix.equals(BranchMetric.NEGATIV_LINK_LABEL)){
		// status.setStatus(true, "");
		// }
		((Hashtable) allMetricsTable.get(prefix)).put(marker, status);
	}

	public void setCoverageStatusForAllMarker(String marker, String testCase) {
		logger.info("---------!!!!!!--------" + marker);
		Scanner scanner = new Scanner(marker);
		scanner.useDelimiter(MetricHandler.SEPARATOR);
		String marke;
		while (scanner.hasNext()) {
			marke = scanner.next().trim();
			 if (marke.length() > 0)
			 setCoverageStatusForMarker(marke, testCase);
		}

	}

	private void setCoverageStatusForMarker(String string, String testCase) {
		logger.info("!_!_!_!_!__-------!!!-11-1-1-!_!" + string + "testCase="
				+ testCase);
		String prefix = string.substring(0, string.indexOf('_'));
		CoverageLabelStatus status = (CoverageLabelStatus) ((Hashtable) allMetricsTable
				.get(prefix)).get(string);
		status.setStatus(true, testCase);
	}

	public IStatistic getStatistic(String metricName) {
		IStatistic statistic = null;
		if (metricName.equals(Statementmetric.METRIC_NAME)) {
			statistic = getStatementmetricResults();
		} else if (metricName.equals(BranchMetric.METRIC_NAME)) {
			statistic = getBranchmetricResults();
		}
		return statistic;
	}

	public List<IStatistic> getStatistics() {
		List<IStatistic> statistics = new ArrayList<IStatistic>();
		IStatistic statistic = getStatementmetricResults();
		if (statistic != null)
			statistics.add(statistic);
		statistic = getBranchmetricResults();
		if (statistic != null)
			statistics.add(statistic);
		return statistics;
	}

	private IStatistic getBranchmetricResults() {
		IStatistic statistic = new Statistic(BranchMetric.BRANCH_LABEL);

		int[] numbers = getNumbers(BranchMetric.BRANCH_LABEL);
		if (numbers != null) {
			statistic.addSubStatistik(new Statistic(numbers[0], numbers[1],
					BranchMetric.BRANCH_LABEL));
		}
		numbers = getNumbers(BranchMetric.POSITIV_LINK_LABEL);
		if (numbers != null) {
			statistic.addSubStatistik(new Statistic(numbers[0], numbers[1],
					BranchMetric.POSITIV_LINK_LABEL));
		}
		numbers = getNumbers(BranchMetric.NEGATIV_LINK_LABEL);
		if (numbers != null) {
			statistic.addSubStatistik(new Statistic(numbers[0], numbers[1],
					BranchMetric.NEGATIV_LINK_LABEL));
		}

		return statistic;
	}

	private IStatistic getStatementmetricResults() {
		IStatistic statistic = null;
		int[] numbers = getNumbers(BasicActivity.EMPTY_ACTIVITY);
		if (numbers != null) {
			if (statistic == null)
				statistic = new Statistic(Statementmetric.METRIC_NAME);
			statistic.addSubStatistik(new Statistic(numbers[0], numbers[1],
					BasicActivity.EMPTY_ACTIVITY));
		}
		numbers = getNumbers(BasicActivity.ASSIGN_ACTIVITY);
		if (numbers != null) {
			if (statistic == null)
				statistic = new Statistic(Statementmetric.METRIC_NAME);
			statistic.addSubStatistik(new Statistic(numbers[0], numbers[1],
					BasicActivity.ASSIGN_ACTIVITY));
		}
		numbers = getNumbers(BasicActivity.COMPENSATE_ACTIVITY);
		if (numbers != null) {
			if (statistic == null)
				statistic = new Statistic(Statementmetric.METRIC_NAME);
			statistic.addSubStatistik(new Statistic(numbers[0], numbers[1],
					BasicActivity.COMPENSATE_ACTIVITY));
		}
		numbers = getNumbers(BasicActivity.COMPENSATESCOPE_ACTIVITY);
		if (numbers != null) {
			if (statistic == null)
				statistic = new Statistic(Statementmetric.METRIC_NAME);
			statistic.addSubStatistik(new Statistic(numbers[0], numbers[1],
					BasicActivity.COMPENSATESCOPE_ACTIVITY));
		}
		numbers = getNumbers(BasicActivity.EXIT_ACTIVITY);
		if (numbers != null) {
			if (statistic == null)
				statistic = new Statistic(Statementmetric.METRIC_NAME);
			statistic.addSubStatistik(new Statistic(numbers[0], numbers[1],
					BasicActivity.EXIT_ACTIVITY));
		}
		numbers = getNumbers(BasicActivity.INVOKE_ACTIVITY);
		if (numbers != null) {
			if (statistic == null)
				statistic = new Statistic(Statementmetric.METRIC_NAME);
			statistic.addSubStatistik(new Statistic(numbers[0], numbers[1],
					BasicActivity.INVOKE_ACTIVITY));
		}
		numbers = getNumbers(BasicActivity.RECEIVE_ACTIVITY);
		if (numbers != null) {
			if (statistic == null)
				statistic = new Statistic(Statementmetric.METRIC_NAME);
			statistic.addSubStatistik(new Statistic(numbers[0], numbers[1],
					BasicActivity.RECEIVE_ACTIVITY));
		}
		numbers = getNumbers(BasicActivity.REPLY_ACTIVITY);
		if (numbers != null) {
			if (statistic == null)
				statistic = new Statistic(Statementmetric.METRIC_NAME);
			statistic.addSubStatistik(new Statistic(numbers[0], numbers[1],
					BasicActivity.REPLY_ACTIVITY));
		}
		numbers = getNumbers(BasicActivity.RETHROW_ACTIVITY);
		if (numbers != null) {
			if (statistic == null)
				statistic = new Statistic(Statementmetric.METRIC_NAME);
			statistic.addSubStatistik(new Statistic(numbers[0], numbers[1],
					BasicActivity.RETHROW_ACTIVITY));
		}
		numbers = getNumbers(BasicActivity.THROW_ACTIVITY);
		if (numbers != null) {
			if (statistic == null)
				statistic = new Statistic(Statementmetric.METRIC_NAME);
			statistic.addSubStatistik(new Statistic(numbers[0], numbers[1],
					BasicActivity.THROW_ACTIVITY));
		}
		numbers = getNumbers(BasicActivity.WAIT_ACTIVITY);
		if (numbers != null) {
			if (statistic == null)
				statistic = new Statistic(Statementmetric.METRIC_NAME);
			statistic.addSubStatistik(new Statistic(numbers[0], numbers[1],
					BasicActivity.WAIT_ACTIVITY));
		}
		return statistic;
	}

	private int[] getNumbers(String activity) {
		int totalNumber;
		int testedNumber = 0;
		int[] numbers = null;
		if (allMetricsTable.get(activity) != null) {
			Hashtable activityTable = (Hashtable) allMetricsTable.get(activity);
			totalNumber = activityTable.size();
			Enumeration e = activityTable.elements();
			CoverageLabelStatus status;
			while (e.hasMoreElements()) {
				status = (CoverageLabelStatus) e.nextElement();
				if (status.isTested()) {
					testedNumber++;
				}
			}
			numbers = new int[] { totalNumber, testedNumber };
		}
		return numbers;
	}

	public void addMarkerForEach(String content) {
		logger.info("---CONTENT " + content + " registriert.");
		content = content.substring(content
				.indexOf(IMetric.DYNAMIC_COVERAGE_LABEL_IDENTIFIER)
				+ IMetric.DYNAMIC_COVERAGE_LABEL_IDENTIFIER.length());
		int start, stop, index1, index2;
		String prefix;
		index1 = content.indexOf(MetricHandler.SEPARATOR);
		start = Integer.parseInt(content.substring(0, index1));
		index2 = content.indexOf(MetricHandler.SEPARATOR, index1 + 1);
		stop = Integer.parseInt(content.substring(index1
				+ MetricHandler.SEPARATOR.length(), index2));
		prefix = content.substring(index2
				+ MetricHandler.SEPARATOR.length());
		for (; start < stop + 1; start++) {
			addMarker(prefix + start);
		}
	}

}