package view;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.RefineryUtilities;

public class PieChart extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PieChart(String title, Map<String, Float> donnees) {
		super(title);
		JPanel localJPanel = createDemoPanel(title, donnees);
		localJPanel.setPreferredSize(new Dimension(500, 270));
		setContentPane(localJPanel);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		pack();
		RefineryUtilities.centerFrameOnScreen(this);
		setVisible(true);
	}

	private static PieDataset createDataset(Map<String, Float> donnees) {
		DefaultPieDataset localDefaultPieDataset = new DefaultPieDataset();

		for (Map.Entry<String, Float> entry : donnees.entrySet()) {
			localDefaultPieDataset.setValue(entry.getKey(), entry.getValue());
		}
		return localDefaultPieDataset;
	}

	private static JFreeChart createChart(String title,
			PieDataset paramPieDataset) {
		JFreeChart localJFreeChart = ChartFactory.createPieChart(title,
				paramPieDataset, true, true, false);
		PiePlot localPiePlot = (PiePlot) localJFreeChart.getPlot();
		localPiePlot.setSectionPaint("One", new Color(160, 160, 255));
		localPiePlot.setSectionPaint("Two", new Color(128, 128, 223));
		localPiePlot.setSectionPaint("Three", new Color(96, 96, 191));
		localPiePlot.setSectionPaint("Four", new Color(64, 64, 159));
		localPiePlot.setSectionPaint("Five", new Color(32, 32, 127));
		localPiePlot.setSectionPaint("Six", new Color(0, 0, 111));
		localPiePlot.setNoDataMessage("No data available");
		localPiePlot.setExplodePercent("Two", 0.2D);
		localPiePlot.setLabelGenerator(new StandardPieSectionLabelGenerator(
				"{0} ({2} percent)"));
		localPiePlot.setLabelBackgroundPaint(new Color(220, 220, 220));
		localPiePlot
				.setLegendLabelToolTipGenerator(new StandardPieSectionLabelGenerator(
						"Tooltip for legend item {0}"));
		localPiePlot.setSimpleLabels(true);
		localPiePlot.setInteriorGap(0.0D);
		return localJFreeChart;
	}

	public static JPanel createDemoPanel(String title,
			Map<String, Float> donnees) {
		JFreeChart localJFreeChart = createChart(title, createDataset(donnees));
		ChartPanel localChartPanel = new ChartPanel(localJFreeChart);
		localChartPanel.setMouseWheelEnabled(true);
		return localChartPanel;
	}

}
