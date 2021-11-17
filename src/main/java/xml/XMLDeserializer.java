package xml;
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class XMLDeserializer {

    public static void main(String[] args) throws Exception {
        File file= new File("src/main/resources/smallMap.xml");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(file);
        NodeList nodeIntersection = document.getElementsByTagName("intersection");
        //************* INTERSECTION **************
        for(int x=0,size= nodeIntersection.getLength(); x<size; x++) {
            System.out.print("id : ");
            System.out.print(nodeIntersection.item(x).getAttributes().getNamedItem("id").getNodeValue());
            System.out.print("    latitude :    ");
            System.out.print(nodeIntersection.item(x).getAttributes().getNamedItem("latitude").getNodeValue());
            System.out.print("    longitude :    ");
            System.out.print(nodeIntersection.item(x).getAttributes().getNamedItem("longitude").getNodeValue());
            System.out.println();

        }
        System.out.println();
        //************* SEGMENT **************
        NodeList nodeSegment = document.getElementsByTagName("segment");
        for(int x=0,size= nodeSegment.getLength(); x<size; x++) {
            System.out.print("destination : ");
            System.out.print(nodeSegment.item(x).getAttributes().getNamedItem("destination").getNodeValue());
            System.out.print("    length :    ");
            System.out.print(nodeSegment.item(x).getAttributes().getNamedItem("length").getNodeValue());
            System.out.print("    name :    ");
            System.out.print(nodeSegment.item(x).getAttributes().getNamedItem("name").getNodeValue());
            System.out.print("    origin :    ");
            System.out.println(nodeSegment.item(x).getAttributes().getNamedItem("origin").getNodeValue());
            System.out.println();

        }
    }
}
