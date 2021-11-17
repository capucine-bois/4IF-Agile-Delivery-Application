package xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import model.Circle;
import model.Plan;
import model.Point;
import model.PointFactory;
import model.Rectangle;


public class XMLdeserializer {
	/**
	 * Open an XML file and create plan from this file
	 * @param plan the plan to create from the file
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ExceptionXML
	 */
	public static void load(Plan plan) throws ParserConfigurationException, SAXException, IOException, ExceptionXML{
		File xml = XMLfileOpener.getInstance().open(true);
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();	
        Document document = docBuilder.parse(xml);
        Element root = document.getDocumentElement();
        if (root.getNodeName().equals("plan")) {
           buildFromDOMXML(root, plan);
        }
        else
        	throw new ExceptionXML("Wrong format");
	}

    private static void buildFromDOMXML(Element noeudDOMRacine, Plan plan) throws ExceptionXML, NumberFormatException{
    	int height = Integer.parseInt(noeudDOMRacine.getAttribute("height"));
        if (height <= 0)
        	throw new ExceptionXML("Error when reading file: The plan height must be positive");
        int width = Integer.parseInt(noeudDOMRacine.getAttribute("width"));
        if (width <= 0)
        	throw new ExceptionXML("Error when reading file: The plan width must be positive");
       	plan.reset(width,height);
       	NodeList circleList = noeudDOMRacine.getElementsByTagName("circle");
       	for (int i = 0; i < circleList.getLength(); i++) {
        	plan.add(createCircle((Element) circleList.item(i)));
       	}
       	NodeList rectangleList = noeudDOMRacine.getElementsByTagName("rectangle");
       	for (int i = 0; i < rectangleList.getLength(); i++) {
          	plan.add(createRectangle((Element) rectangleList.item(i)));
       	}
    }
    
    private static Circle createCircle(Element elt) throws ExceptionXML{
   		int x = Integer.parseInt(elt.getAttribute("x"));
   		int y = Integer.parseInt(elt.getAttribute("y"));
   		Point p = PointFactory.createPoint(x, y);
   		if (p == null)
   			throw new ExceptionXML("Error when reading file: Point coordinates must belong to the plan");
   		int radius = Integer.parseInt(elt.getAttribute("radius"));
   		if (radius <= 0)
   			throw new ExceptionXML("Error when reading file: Radius must be positive");
   		return new Circle(p, radius);
    }
    
    private static Rectangle createRectangle(Element elt) throws ExceptionXML{
   		int x = Integer.parseInt(elt.getAttribute("x"));
   		int y = Integer.parseInt(elt.getAttribute("y"));
   		Point p = PointFactory.createPoint(x, y);
   		if (p == null)
   			throw new ExceptionXML("Error when reading file: Point coordinates must belong to the plan");
      	int rectangleWidth = Integer.parseInt(elt.getAttribute("width"));
   		if (rectangleWidth <= 0)
   			throw new ExceptionXML("Error when reading file: Rectangle width must be positive");
      	int rectangleHeight = Integer.parseInt(elt.getAttribute("height"));
   		if (rectangleHeight <= 0)
   			throw new ExceptionXML("Error when reading file: Rectangle height must be positive");
   		return new Rectangle(p, rectangleWidth, rectangleHeight);
    }
 
}
