package xml;

import model.CityMap;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("XMLdeserializerTest test case")
class XMLdeserializerTest {

    static Instant startedAt;

    @BeforeAll
    static public void initStartingTime() {
        System.out.println("Appel avant tous les tests");
        startedAt = Instant.now();
    }

    @AfterAll
    static public void showTestDuration() {
        System.out.println("Appel après tous les tests");
        Instant endedAt = Instant.now();
        long duration = Duration.between(startedAt, endedAt).toMillis();
        System.out.println(MessageFormat.format("Durée des tests : {0} ms", duration));
    }



    /*
     * Method to test:
     * parseXMLSegments()
     *
     * What it does:
     * Read XML to convert in segment
     */
    @Test
    @DisplayName("Test on parseXMLSegments")
    void parseXMLSegmentsTest() {
        // Create input of parseXMLSegments
        CityMap cityMap = new CityMap();
        Document document = extractDocument(file);
        XMLDeserializer.deserializeMap(cityMap, document);
        boolean firstTest = xmlOpener.accept(wrongFile);
        assertFalse(firstTest,"accept function does not filter only XML");
        boolean secondTest = xmlOpener.accept(file);
        assertTrue(secondTest, "accept function does not accept XML");
    }

    /*
     * Method to test:
     * parseXMLIntersections()
     *
     * What it does:
     * Read XML to convert in intersections
     */
    @Test
    @DisplayName("Test on parseXMLIntersections")
    void parseXMLIntersectionsTest() throws ParserConfigurationException, IOException, SAXException {
        File file = new File("../../resources/testMap.xml");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(file);


        XMLDeserializer.parseXMLIntersections(document);
    }





}