package it.unifi.simonesantarsiero.wcgraphs;

import ch.qos.logback.classic.Logger;
import it.unifi.simonesantarsiero.wcgraphs.commons.AlgorithmEnum;
import it.unifi.simonesantarsiero.wcgraphs.commons.DatasetLogger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.*;

public class DatasetResultsImporter {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(DatasetResultsImporter.class);
    private static final String FILE_EXTENSION = ".txt";
    private static final int HOW_MANY_ALGO_IN_IMPORT_FILE = 5;

    public static void main(String[] args) {
        new DatasetResultsImporter();
    }

    public DatasetResultsImporter() {
        List<String> list = new ArrayList<>(
                getPathsOfGraphsAvailableInDirectory(
                        System.getProperty("user.dir") + FILE_SEPARATOR + IMPORT_PATH, FILE_EXTENSION));
        compareAlgorithms(list);
    }

    private void compareAlgorithms(List<String> list) {

        List<String> lines = readFile(list.get(0) + FILE_EXTENSION);

        Map<String, AlgorithmResults> algorithmResultsMap = new HashMap<>();
        List<AlgorithmEnum> algorithmEnumsList = Arrays.asList(AlgorithmEnum.values());
        algorithmEnumsList.forEach(algorithm -> algorithmResultsMap.put(algorithm.getValue(), new AlgorithmResults(algorithm.getValue())));

        DatasetLogger loader = new DatasetLogger(LOGGER);
        String[] headers = lines.get(0).split(",");
        for (int i = 1; i < lines.size(); i++) {
            Map<String, Object> result = buildResults(headers, lines.get(i));
            algorithmResultsMap.get(getAlgorithmName(headers, lines.get(i))).add(result);

            loader.printFilename(getGraphName(headers, lines.get(i)) + " [" + getAlgorithmName(headers, lines.get(i)) + "]");
            loader.printValues(result);
            loader.printEmptyRow();
        }
        LOGGER.info("\n\n");

        // PRINT FOR LATEX
        String s = printForLatex(headers, lines, HOW_MANY_ALGO_IN_IMPORT_FILE, "#BFS");
        System.out.println("PRINT FOR LATEX - #BFS \n" + s);

        // PRINT FOR LATEX
        String s2 = printForLatex(headers, lines, HOW_MANY_ALGO_IN_IMPORT_FILE, "Time (s)");
        System.out.println("PRINT FOR LATEX - TIME(s)\n" + s2);

        new Chart(new ArrayList<>(algorithmResultsMap.values()), true);
        new Chart(new ArrayList<>(algorithmResultsMap.values()), false);
    }

    private String printForLatex(String[] headers, List<String> lines, int howManyAlgo, String valueToPrint) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\\hline Graph \t ");
        for (int k = 0; k < howManyAlgo; k++) {
            stringBuilder.append(" & \t " + getPropertyName(headers, lines.get(k + 1), "algorithmName"));
        }
        stringBuilder.append(" \\\\ \n");
        for (int i = 1; i < lines.size(); i++) {
            stringBuilder.append("\\hline " + getGraphName(headers, lines.get(i)));
            stringBuilder.append("\t");
            int j;
            for (j = 0; j < howManyAlgo; j++) {
                stringBuilder.append(" & \t ");
                stringBuilder.append(getPropertyName(headers, lines.get(i + j), valueToPrint));
            }
            i += j - 1;
            stringBuilder.append(" \\\\ \n");
        }
        stringBuilder.append("\\hline \n ");
        return stringBuilder.toString();
    }

    private String getPropertyName(String[] headers, String line, String property) {
        int indexAlgoName = -1;
        for (int i = 0; i < headers.length; i++) {
            if (property.equals(headers[i])) {
                indexAlgoName = i;
            }
        }
        String[] splittedString = line.split(",");
        return splittedString[indexAlgoName];
    }

    private String getGraphName(String[] headers, String line) {
        int indexAlgoName = -1;
        for (int i = 0; i < headers.length; i++) {
            if ("graphName".equals(headers[i])) {
                indexAlgoName = i;
            }
        }
        String[] splittedString = line.split(",");
        return splittedString[indexAlgoName];
    }

    private String getAlgorithmName(String[] headers, String line) {
        int indexAlgoName = -1;
        for (int i = 0; i < headers.length; i++) {
            if ("algorithmName".equals(headers[i])) {
                indexAlgoName = i;
            }
        }
        String[] splittedString = line.split(",");
        return splittedString[indexAlgoName];
    }

    public Map<String, Object> buildResults(String[] headers, String line) {
        String[] splittedString = line.split(",");
        Map<String, Object> mapResult = new HashMap<>();
        for (int i = 0; i < splittedString.length; i++) {
            if (splittedString[i].equals("null") || i == 0 || i == 1) {
                mapResult.put(headers[i], splittedString[i]);
            } else if (i == 2 || i == 3 || i == 5 || i == 6) {
                mapResult.put(headers[i], Integer.parseInt(splittedString[i]));
            } else if (i == 4 || i == 7) {
                mapResult.put(headers[i], Double.parseDouble(splittedString[i]));
            } else {
                mapResult.put(headers[i], splittedString[i]);
            }
        }
        return mapResult;
    }

    private List<String> readFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            return readAllLines(reader);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private List<String> readAllLines(BufferedReader reader) throws IOException {
        List<String> content = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null) {
            content.add(line);
        }
        return content;
    }
}
