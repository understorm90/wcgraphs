package it.unifi.simonesantarsiero.wcgraphs;

import it.unifi.simonesantarsiero.wcgraphs.commons.AlgorithmEnum;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Chart {

    public static final String WINDOW_TITLE = "Algorithms Comparison";
    public static final String PANEL_TITLE = "Graphical Representation";
    public static final String X_AXIS_LABEL = "n (# vertices)";
    public static final String Y_AXIS_TIME_LABEL = "time (seconds)";
    public static final String Y_AXIS_BFS_LABEL = "#BFS";
    public static final String TITLE_LABEL = "Comparison between %s algorithms";
    private final List<AlgorithmResults> algorithmsResults;
    private final boolean isTimeValueOnYAxis;

    public Chart(List<AlgorithmResults> algorithmsResults, boolean withTime) {
        isTimeValueOnYAxis = withTime;

        List<String> visibleAlgorithms = new ArrayList<>();
        visibleAlgorithms.add(AlgorithmEnum.NEWSUMSWEEP.getValue());
        visibleAlgorithms.add(AlgorithmEnum.SUMSWEEP.getValue());
        visibleAlgorithms.add(AlgorithmEnum.WEBGRAPH.getValue());
        visibleAlgorithms.add(AlgorithmEnum.AKIBA_JAVA.getValue());
        visibleAlgorithms.add(AlgorithmEnum.AKIBA_CPP.getValue());

        this.algorithmsResults = algorithmsResults
                .stream()
                .filter(result -> isVisible(visibleAlgorithms, result.getAlgorithmName()))
                .collect(Collectors.toList());

        JFrame frame = new JFrame(WINDOW_TITLE);

        XYDataset dataset = createDataset();
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

    private boolean isVisible(List<String> visibleAlgorithms, String algorithmName) {
        return visibleAlgorithms.stream().anyMatch(str -> str.trim().equals(algorithmName));
    }

    private XYDataset createDataset() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (AlgorithmResults algo : algorithmsResults) {
            XYSeries series = createSeries(algo);
            dataset.addSeries(series);
        }
        return dataset;
    }

    private XYSeries createSeries(AlgorithmResults results) {
        XYSeries series = new XYSeries(results.getAlgorithmName());
        for (int i = 0; i < results.size(); i++) {
            Map<String, Object> result = results.get(i);
            if (isTimeValueOnYAxis) {
                series.add((Integer) result.get(VALUE_VERTICES), (Double) result.get(VALUE_TIME));
            } else {
                series.add((Integer) result.get(VALUE_VERTICES), (Integer) result.get(VALUE_NUM_OF_BFS));

            }
        }
        return series;
    }

    private JFreeChart createChart(XYDataset dataset) {

        String title = String.format(TITLE_LABEL, dataset.getSeriesCount());

        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                X_AXIS_LABEL,
                isTimeValueOnYAxis ? Y_AXIS_TIME_LABEL : Y_AXIS_BFS_LABEL,
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
        plot.setRenderer(createRenderer());

        return chart;
    }

    private XYLineAndShapeRenderer createRenderer() {
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        for (int i = 0; i < algorithmsResults.size(); i++) {
            renderer.setSeriesPaint(i, getColor(algorithmsResults.get(i).getAlgorithmName()));
            renderer.setSeriesStroke(i, new BasicStroke(2.0f));
        }
        renderer.setBaseToolTipGenerator(createTooltip());
        return renderer;
    }

    private Paint getColor(String algorithmName) {
        switch (AlgorithmEnum.valueOf(algorithmName)) {
            case AKIBA_CPP:
                return Color.GREEN;
            case AKIBA_JAVA:
                return Color.BLUE;
            case WEBGRAPH:
                return Color.MAGENTA;
            case SUMSWEEP:
                return Color.ORANGE;
            case NEWSUMSWEEP:
                return Color.RED;
            default:
                return Color.CYAN;
        }
    }

    private XYToolTipGenerator createTooltip() {
        List<Map<String, String>> names = new ArrayList<>();
        for (int i = 0; i < algorithmsResults.size(); i++) {
            names.add(getDatasetNamesMap(i));
        }
        return (dataset, series, item) -> {
            int x1 = dataset.getX(series, item).intValue();
            double y1 = dataset.getY(series, item).doubleValue();
            String datasetName = findDatasetName(names.get(series), x1, y1);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<html>");
            stringBuilder.append(String.format("<p style='color:#000000;'>Serie: <b>%s</b></p>", dataset.getSeriesKey(series)));
            stringBuilder.append(String.format("<p style='color:#000000;'>Dataset: <b>%s</b></p>", datasetName));
            stringBuilder.append(String.format("vertices: %d<br/>", x1));
            stringBuilder.append(String.format("seconds: %f", y1));
            stringBuilder.append("</html>");
            return stringBuilder.toString();
        };
    }

    private String findDatasetName(Map<String, String> datasetNames, int x1, double y1) {
        return datasetNames.get(x1 + " ," + y1);
    }

    private Map<String, String> getDatasetNamesMap(int series) {
        Map<String, String> datasetNames = new HashMap<>();
        AlgorithmResults algorithmResults = algorithmsResults.get(series);
        for (int i = 0; i < algorithmResults.size(); i++) {
            Map<String, Object> result = algorithmResults.get(i);
            Integer nVertices = (Integer) result.get(VALUE_VERTICES);
            String datasetName = (String) result.get(VALUE_DATASET);
            if (isTimeValueOnYAxis) {
                Double time = (Double) result.get(VALUE_TIME);
                datasetNames.put(nVertices + " ," + time, datasetName);
            } else {
                Integer nBFS = (Integer) result.get(VALUE_NUM_OF_BFS);
                datasetNames.put(nVertices + " ," + nBFS, datasetName);
            }
        }
        return datasetNames;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            AlgorithmResults mStatsAkibaCPP = new AlgorithmResults(AlgorithmEnum.AKIBA_CPP.getValue());
            mStatsAkibaCPP.add(getSampleResultsFromDataset("dataset1", 10879, 26, 38, 0.095744));
            mStatsAkibaCPP.add(getSampleResultsFromDataset("dataset2", 8846, 22, 26, 0.054159));
            mStatsAkibaCPP.add(getSampleResultsFromDataset("dataset3", 555, 22, 26, 0.3));

            AlgorithmResults mStatsAkiba = new AlgorithmResults(AlgorithmEnum.AKIBA_JAVA.getValue());
            mStatsAkiba.add(getSampleResultsFromDataset("dataset1", 10879, 26, 118, 0.549));
            mStatsAkiba.add(getSampleResultsFromDataset("dataset2", 8846, 22, 26, 0.11));
            mStatsAkiba.add(getSampleResultsFromDataset("dataset3", 555, 22, 26, 0.31));

            List<AlgorithmResults> algorithmsResults = Arrays.asList(mStatsAkibaCPP, mStatsAkiba);

            new Chart(algorithmsResults, true);
            new Chart(algorithmsResults, false);
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
