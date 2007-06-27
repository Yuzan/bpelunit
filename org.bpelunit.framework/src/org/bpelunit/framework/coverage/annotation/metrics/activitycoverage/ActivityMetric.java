package org.bpelunit.framework.coverage.annotation.metrics.activitycoverage;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.bpelunit.framework.coverage.annotation.MetricsManager;
import org.bpelunit.framework.coverage.annotation.metrics.IMetric;
import org.bpelunit.framework.coverage.annotation.metrics.IMetricHandler;
import org.bpelunit.framework.coverage.exceptions.BpelException;
import org.bpelunit.framework.coverage.receiver.MarkerStatus;
import org.bpelunit.framework.coverage.receiver.MarkersRegisterForArchive;
import org.bpelunit.framework.coverage.result.statistic.IStatistic;
import org.bpelunit.framework.coverage.result.statistic.impl.Statistic;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;

public class ActivityMetric implements IMetric {

	private Logger logger=Logger.getLogger(getClass());
	
	public static final String METRIC_NAME = "ActivityCoverage";

	private List<String> activities_to_respekt;

	private IMetricHandler metricHandler;

	private List<Element> elementsOfBPEL = null;

	public ActivityMetric(List<String> activitesToRespect, MarkersRegisterForArchive markersRegistry) {
		activities_to_respekt = new ArrayList<String>();
		if (activitesToRespect != null) {
			for (Iterator<String> iter = activitesToRespect.iterator(); iter
					.hasNext();) {
				String basicActivity = iter.next();
				activities_to_respekt.add(basicActivity);
			}
		}
		metricHandler = new ActivityMetricHandler(markersRegistry);
	}

	/**
	 * 
	 */
	public String getName() {
		return METRIC_NAME;
	}

	/**
	 * Liefert Pr�fixe von allen Marken dieser Metrik. Sie erm�glichen die
	 * Zuordnung der empfangenen Marken einer Metrik
	 * 
	 * @return Pr�fixe von allen Marken dieser Metrik
	 */
	public List<String> getMarkersId() {
		return activities_to_respekt;
	}


	/**
	 * Erzeugt Statistiken
	 * 
	 * @param allMarkers
	 *            alle einegf�gten Marken (von allen Metriken), nach dem Testen
	 * @return Statistik
	 */
	public IStatistic createStatistic(
			Hashtable<String, Hashtable<String, MarkerStatus>> allLabels) {
		IStatistic statistic = new Statistic(METRIC_NAME);
		IStatistic subStatistic;
		String label;
		for (Iterator<String> iter = activities_to_respekt.iterator(); iter
				.hasNext();) {
			label = iter.next();
			subStatistic = new Statistic(METRIC_NAME + ": " + label);
			List<MarkerStatus> statusListe = MetricsManager.getStatus(label,
					allLabels);
			subStatistic.setStatusListe(statusListe);
			statistic.addSubStatistik(subStatistic);
		}
		return statistic;
	}


	/**
	 * Erh�lt die noch nicht modifizierte Beschreibung des BPELProzesses als
	 * XML-Element. Alle f�r die Instrumentierung ben�tigten Elemente der
	 * Prozessbeschreibung werden gespeichert
	 * 
	 * @param process
	 *            noch nicht modifiziertes BPEL-Prozess
	 */
	public void setOriginalBPELProcess(Element process_element) {
		ElementFilter filter = new ElementFilter(process_element.getNamespace());
		elementsOfBPEL = new ArrayList<Element>();
		for (Iterator<Element> iter = process_element.getDescendants(filter); iter
				.hasNext();) {
			Element basicActivity = iter.next();
			if (activities_to_respekt.contains(basicActivity.getName()))
				elementsOfBPEL.add(basicActivity);
		}	
	}

	/**
	 * delegiert die Instrumentierungsaufgabe an eigenen Handler
	 * 
	 * @throws BpelException
	 */
	public void insertMarkers() throws BpelException {
		if (elementsOfBPEL != null) {
			metricHandler.insertMarkersForMetric(elementsOfBPEL);
			elementsOfBPEL = null;
		}
	}
}