package com.grayson.hivetracker.box;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlTools {

	// /
	// http://p-xr.com/android-tutorial-how-to-parseread-xml-data-into-android-listview/
	public static Document XMLfromString(String xml) {

		Document doc = null;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {

			DocumentBuilder db = dbf.newDocumentBuilder();

			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			doc = db.parse(is);

		} catch (ParserConfigurationException e) {
			System.out.println("XML parse error: " + e.getMessage());
			return null;
		} catch (SAXException e) {
			System.out.println("Wrong XML file structure: " + e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println("I/O exeption: " + e.getMessage());
			return null;
		}

		return doc;

	}
	
	
	public static long getAttributeLong(Element item, String nodeName, String attributeName, long defaultValue) {
		String val = XmlTools.getAttributeValue(item, nodeName, attributeName);
		long retVal = defaultValue;
		if (val!=null && val!="")
			retVal = Long.parseLong(val);
		return retVal;
	}
	
	public static String getString(Element item, String str, String defaultValue) {
		String val = XmlTools.getValue(item,str);
		String retVal = defaultValue;
		if(val!=null && val!="")
			retVal = val;
		return retVal;
	}
	
	public static Boolean getBoolean(Element item, String str, Boolean defaultValue) {
		String val = XmlTools.getValue(item,str);
		Boolean retVal = defaultValue;
		if(val!=null && val!="")
			retVal = Boolean.parseBoolean(val);
		return retVal;
	}
	
	public static double getDouble(Element item, String str, double defaultValue) {
		String val = XmlTools.getValue(item,str);
		double retVal = defaultValue;
		if(val!=null && val!="")
			retVal = Double.parseDouble(val);
		return retVal;
	}
	
	public static int getInt(Element item, String str, int defaultValue) {
		String val = XmlTools.getValue(item,str);
		int retVal = defaultValue;
		if(val!=null && val!="")
			retVal = Integer.parseInt(val);
		return retVal;
	}

	public static long getLong(Element item, String str, long defaultValue) {
		String val = XmlTools.getValue(item,str);
		long retVal = defaultValue;
		if(val!=null && val!="")
			retVal = Long.parseLong(val);
		return retVal;
	}
	
	public static String getAttributeValue(Element item, String nodeName, String attributeName) {
		NodeList n = item.getElementsByTagName(nodeName);
		Element node = (Element) n.item(0);
		String val = null;
		if(node != null) {
			val = node.getAttribute(attributeName);
		}
		return val;
	}
	
	public static String getValue(Element item, String str) {
		NodeList n = item.getElementsByTagName(str);
		String val = XmlTools.getElementValue(n.item(0));
		return val;
	}
	
	public final static String getElementValue(Node elem) {
		Node kid;
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for (kid = elem.getFirstChild(); kid != null; kid = kid
						.getNextSibling()) {
					if (kid.getNodeType() == Node.TEXT_NODE) {
						return kid.getNodeValue();
					}
				}
			}
		}
		return "";
	}
	
	public final static String getElement(Node elem) {
		Node kid;
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for (kid = elem.getFirstChild(); kid != null; kid = kid
						.getNextSibling()) {
					if (kid.getNodeType() == Node.TEXT_NODE) {
						return kid.getNodeValue();
					}
				}
			}
		}
		return "";
	}

}