package it.unifi.simonesantarsiero.wcgraphs.webgraph;

import ch.qos.logback.classic.Logger;
import it.unifi.simonesantarsiero.wcgraphs.commons.DatasetLogger;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.DATASETS_PATH;
import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.EXT_GRAPH;

public class WebGraphDecoderTest {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(WebGraphDecoderTest.class);

    @Before
    public void setUp() {
        WebGraphDecoder.disableLogger();
    }

    @Test
    public void testWebGraphDecoder() {
        String workingDirectory = System.getProperty("user.dir");
        String datasetsPath = workingDirectory + DATASETS_PATH;
        List<String> list = DatasetLogger.getListOfGraphsAvailableInDirectory(datasetsPath, EXT_GRAPH);

        List<String> headersList = Collections.emptyList();
        DatasetLogger loader = new DatasetLogger(headersList, LOGGER);
        for (String filename : list) {
            loader.printFilename(filename);

            WebGraphDecoder.decode(datasetsPath + filename);
        }
        LOGGER.info("\n\n");
    }
}