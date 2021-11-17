package xml;
import java.io.File;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("XMLfileOpener test case")
class XMLfileOpenerTest {

    XMLfileOpener xmlOpener;
    File file = new File("testMap.xml");

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
        File wrongFile = new File("test.xnl");
        boolean firstTest = xmlOpener.accept(wrongFile);
        assertFalse(firstTest,"accept function does not filter only XML");
        boolean secondTest = xmlOpener.accept(file);
        assertTrue(secondTest, "accept function does not accept XML");
    }
}