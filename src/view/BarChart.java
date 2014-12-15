package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategorySeriesLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RefineryUtilities;

public class BarChart extends JFrame {

	private static final long serialVersionUID = 1L;

	public BarChart(String title, Map<String, Integer> data) {
		super(title);
		JPanel localJPanel = createDemoPanel(title, data);
		localJPanel.setPreferredSize(new Dimension(500, 270));
		setContentPane(localJPanel);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		RefineryUtilities.centerFrameOnScreen(this);
		setVisible(true);
	}

	private static CategoryDataset createDataset(Map<String, Integer> donnees) {

		String str5 = "Algo";

		DefaultCategoryDataset localDefaultCategoryDataset = new DefaultCategoryDataset();
		for (Entry<String, Integer> entry : donnees.entrySet()) {
			localDefaultCategoryDataset.addValue(entry.getValue(),
					entry.getKey(), str5);

		}

		return localDefaultCategoryDataset;
	}

	private static JFreeChart createChart(String title,
			CategoryDataset paramCategoryDataset) {
		JFreeChart localJFreeChart = ChartFactory.createBarChart(title, "Algo",
				"NbErreurs (%)", paramCategoryDataset);
		CategoryPlot localCategoryPlot = (CategoryPlot) localJFreeChart
				.getPlot();
		localCategoryPlot.setDomainGridlinesVisible(true);
		localCategoryPlot.setRangeCrosshairVisible(true);
		localCategoryPlot.setRangeCrosshairPaint(Color.blue);
		NumberAxis localNumberAxis = (NumberAxis) localCategoryPlot
				.getRangeAxis();
		localNumberAxis.setStandardTickUnits(NumberAxis
				.createIntegerTickUnits());
		BarRenderer localBarRenderer = (BarRenderer) localCategoryPlot
				.getRenderer();
		localBarRenderer.setDrawBarOutline(false);
		GradientPaint localGradientPaint1 = new GradientPaint(0.0F, 0.0F,
				Color.blue, 0.0F, 0.0F, new Color(0, 0, 64));
		GradientPaint localGradientPaint2 = new GradientPaint(0.0F, 0.0F,
				Color.green, 0.0F, 0.0F, new Color(0, 64, 0));
		GradientPaint localGradientPaint3 = new GradientPaint(0.0F, 0.0F,
				Color.red, 0.0F, 0.0F, new Color(64, 0, 0));
		localBarRenderer.setSeriesPaint(0, localGradientPaint1);
		localBarRenderer.setSeriesPaint(1, localGradientPaint2);
		localBarRenderer.setSeriesPaint(2, localGradientPaint3);
		localBarRenderer
				.setLegendItemToolTipGenerator(new StandardCategorySeriesLabelGenerator(
						"Tooltip: {0}"));
		CategoryAxis localCategoryAxis = localCategoryPlot.getDomainAxis();
		localCategoryAxis.setCategoryLabelPositions(CategoryLabelPositions
				.createUpRotationLabelPositions(0.5235987755982988D));
		return localJFreeChart;
	}

	public static JPanel createDemoPanel(String title,
			Map<String, Integer> donnees) {
		JFreeChart localJFreeChart = createChart(title, createDataset(donnees));
		return new ChartPanel(localJFreeChart);
	}

	public static void main(String[] paramArrayOfString) {
		Map<String, Integer> data = new HashMap<String, Integer>();
		data.put("test", 15);
		new BarChart("test", data);
	}
}
