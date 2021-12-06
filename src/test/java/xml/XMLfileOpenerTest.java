package xml;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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


    /**
     * Method to test:
     * accept()
     * <p>
     * What it does:
     * Check either a file can be open or not
     */
    @Test
    @DisplayName("Test on accept()")
    void accept() {
        File wrongFile = new File("../../resources/test.xnl");
        boolean firstTest = XMLFileOpener.getInstance().accept(wrongFile);
        assertFalse(firstTest, "accept function does not filter only XML");
        File file2 = new File("../../resources/testMap.xml");
        boolean secondTest = XMLFileOpener.getInstance().accept(file2);
        assertTrue(secondTest, "accept function does not accept XML");
    }
}