package xml;
import java.io.File;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("XMLfileOpener test case")
class XMLfileOpenerTest {


    static Instant startedAt;

    @BeforeAll
    static public void initStartingTime() {
        startedAt = Instant.now();
    }

    @AfterAll
    static public void showTestDuration() {
        Instant endedAt = Instant.now();
        long duration = Duration.between(startedAt, endedAt).toMillis();
        System.out.println(MessageFormat.format("Test duration : {0} ms", duration));
    }


    XMLFileOpener xmlOpener = new XMLFileOpener();
    File file = new File("../../resources/testMap.xml");

    /*
     * Method to test:
     * accept()
     *
     * What it does:
     * Check either a file can be open or not
     *
     */
    @Test
    @DisplayName("Test on accept()")
    void accept() {
        File wrongFile = new File("../../resources/test.xnl");
        boolean firstTest = xmlOpener.accept(wrongFile);
        assertFalse(firstTest,"accept function does not filter only XML");
        boolean secondTest = xmlOpener.accept(file);
        assertTrue(secondTest, "accept function does not accept XML");
    }
}