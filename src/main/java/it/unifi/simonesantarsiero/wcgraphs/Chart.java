package it.unifi.simonesantarsiero.wcgraphs;

import it.unifi.simonesantarsiero.wcgraphs.commons.AlgorithmEnum;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Chart {

    public static final String WINDOW_TITLE = "Algorithms Comparison";
    public static final String PANEL_TITLE = "Graphical Representation";

    public Chart(List<AlgorithmResults> algorithmsResults) {
        JFrame frame = new JFrame(WINDOW_TITLE);

        XYDataset dataset = createDataset(algorithmsResults);
        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(PANEL_TITLE),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        chartPanel.setBackground(Color.white);

        frame.add(chartPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private XYDataset createDataset(List<AlgorithmResults> algorithmsResults) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (AlgorithmResults algo : algorithmsResults) {
            XYSeries series = createSeries(algo);
            dataset.addSeries(series);
        }
        return dataset;
    }

    private XYSeries createSeries(AlgorithmResults dataset) {
        XYSeries series = new XYSeries(dataset.getAlgorithmName());
        for (int i = 0; i < dataset.size(); i++) {
            Map<String, Object> result = dataset.get(i);
            series.add((Integer) result.get(VALUE_VERTICES), (Double) result.get(VALUE_TIME));
        }
        return series;
    }

    private JFreeChart createChart(XYDataset dataset) {

        String title = "Comparison between " + dataset.getSeriesCount() + " algorithms";
        String xAxisLabel = "n (# vertices)";
        String yAxisLabel = "time (seconds)";

        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                xAxisLabel,
                yAxisLabel,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        );
        chart.getLegend().setFrame(BlockBorder.NONE);
        chart.setTitle(new TextTitle(title,
                new Font("Futura", Font.BOLD, 18)
        ));

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setRangeGridlinesVisible(true);
        plot.setDomainGridlinesVisible(true);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesPaint(1, Color.BLUE);
        renderer.setSeriesStroke(1, new BasicStroke(2.0f));
        renderer.setSeriesPaint(2, Color.MAGENTA);
        renderer.setSeriesStroke(2, new BasicStroke(2.0f));
        renderer.setSeriesPaint(3, Color.ORANGE);
        renderer.setSeriesStroke(3, new BasicStroke(2.0f));
        renderer.setSeriesPaint(4, Color.GREEN);
        renderer.setSeriesStroke(4, new BasicStroke(2.0f));
        plot.setRenderer(renderer);

        return chart;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            AlgorithmResults mStatsAkibaCPP = new AlgorithmResults(AlgorithmEnum.AKIBA_CPP.getValue());
            mStatsAkibaCPP.add(getSampleResultsFromDataset("dataset1", 10879, 26, 38, 0.095744));
            mStatsAkibaCPP.add(getSampleResultsFromDataset("dataset2", 8846, 22, 26, 0.054159));

            AlgorithmResults mStatsAkiba = new AlgorithmResults(AlgorithmEnum.AKIBA_JAVA.getValue());
            mStatsAkiba.add(getSampleResultsFromDataset("dataset1", 10879, 26, 118, 0.549));
            mStatsAkiba.add(getSampleResultsFromDataset("dataset2", 8846, 22, 26, 0.11));

            List<AlgorithmResults> algorithmsResults = Arrays.asList(mStatsAkibaCPP, mStatsAkiba);

            new Chart(algorithmsResults);
        });
    }

    private static Map<String, Object> getSampleResultsFromDataset(String datasetName, int nn, int diameter, int bfs, double time) {
        Map<String, Object> mapResult = new HashMap<>();
        mapResult.put(VALUE_DATASET, datasetName);
        mapResult.put(VALUE_VERTICES, nn);
        mapResult.put(VALUE_DIAMETER, diameter);
        mapResult.put(VALUE_NUM_OF_BFS, bfs);
        mapResult.put(VALUE_TIME, time);
        return mapResult;
    }
}
