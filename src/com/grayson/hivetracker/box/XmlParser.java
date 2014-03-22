package com.grayson.hivetracker.box;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.grayson.hivetracker.database.Category;
import com.grayson.hivetracker.database.Hive;
import com.grayson.hivetracker.database.ListValue;
import com.grayson.hivetracker.database.Location;
import com.grayson.hivetracker.database.LogEntry;
import com.grayson.hivetracker.database.Task;
import com.grayson.hivetracker.tools.Tools;

public class XmlParser {
	public static Category parseCategoryXml(ByteArrayOutputStream xmlStream) {

		XPath xpath = XPathFactory.newInstance().newXPath();
		String expression = "/category";

		try {
			InputSource inputSource = new InputSource(new StringReader(xmlStream.toString()));
			NodeList nodes = (NodeList) xpath.evaluate(expression, inputSource, XPathConstants.NODESET);
			if (nodes.getLength() > 0) {
				Category cat = new Category();

				Element node = (Element) nodes.item(0);

				long id = XmlTools.getLong(node, "id", -1);

				if (id >= 0) {
					cat.setId(id);
					cat.setName(XmlTools.getString(node, "name", ""));
					cat.setDeleted(XmlTools.getBoolean(node, "deleted", false));
				}

				return cat;
			}
		} catch (XPathExpressionException e) {
			Tools.logError(null, "Import", "Error parsing category XML.", e);
		}

		return null;
	}

	public static Location parseLocationXml(ByteArrayOutputStream xmlStream) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		String expression = "/location";

		try {
			InputSource inputSource = new InputSource(new StringReader(xmlStream.toString()));
			NodeList nodes = (NodeList) xpath.evaluate(expression, inputSource, XPathConstants.NODESET);
			if (nodes.getLength() > 0) {
				Location loc = new Location();

				Element node = (Element) nodes.item(0);

				long id = XmlTools.getLong(node, "id", -1);

				if (id >= 0) {
					loc.setId(id);
					loc.setName(XmlTools.getString(node, "name", ""));
					loc.setMafId(XmlTools.getLong(node, "maf_id", 0));
					loc.setLatitude(XmlTools.getDouble(node, "latitude", 0));
					loc.setLongitude(XmlTools.getDouble(node, "longitude", 0));
					loc.setCategoryId(XmlTools.getLong(node, "category_id", -1));
					loc.setDeleted(XmlTools.getBoolean(node, "deleted", false));
				}

				return loc;
			}
		} catch (XPathExpressionException e) {
			Tools.logError(null, "Import", "Error parsing location XML.", e);
		}

		return null;
	}

	public static Hive parseHiveXml(ByteArrayOutputStream xmlStream) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		String expression = "/hive";

		try {
			InputSource inputSource = new InputSource(new StringReader(xmlStream.toString()));
			NodeList nodes = (NodeList) xpath.evaluate(expression, inputSource, XPathConstants.NODESET);
			if (nodes.getLength() > 0) {
				Hive hive = new Hive();

				Element node = (Element) nodes.item(0);

				long id = XmlTools.getLong(node, "id", -1);

				if (id >= 0) {
					hive.setId(id);
					hive.setQrCode(XmlTools.getString(node, "qr_code", ""));
					hive.setQueenType(XmlTools.getString(node, "queen_type", ""));
					hive.setSplitType(XmlTools.getString(node, "split_type", ""));
					
					// foreign keys
					hive.setLocationId(XmlTools.getLong(node, "location_id", 0));
					hive.setBeeId(XmlTools.getLong(node, "bee_id", 0));
					hive.setBroodId(XmlTools.getLong(node, "brood_id", 0));
					hive.setHealthId(XmlTools.getLong(node, "health_id", 0));
					hive.setFoodId(XmlTools.getLong(node, "food_id", 0));
					hive.setVarroaId(XmlTools.getLong(node, "varroa_id", 0));
					hive.setVirusId(XmlTools.getLong(node, "virus_id", 0));
					hive.setPollenId(XmlTools.getLong(node, "pollen_id", 0));
					hive.setTreatmentId(XmlTools.getLong(node, "treatment_id", 0));
					
					hive.setMoving(XmlTools.getBoolean(node, "moving", false));
					hive.setIsVarroaSample(XmlTools.getBoolean(node, "is_varroa_sample", false));
					hive.setVarroaCount(XmlTools.getLong(node, "varroa_count", 0));
					hive.setIsGoodProducer(XmlTools.getBoolean(node, "is_good_producer", false));
					hive.setTreatmentIn(XmlTools.getBoolean(node, "treatment_in", false));
					
					hive.setTreatmentDate(XmlTools.getLong(node, "treatment_date", 0));
					hive.setHarvestedDate(XmlTools.getLong(node, "harvest_date", 0));
					hive.setFedDate(XmlTools.getLong(node, "fed_date", 0));
					hive.setWinteredDate(XmlTools.getLong(node, "wintered_date", 0));
					
					hive.setDeleted(XmlTools.getBoolean(node, "deleted", false));
				}

				return hive;
			}
		} catch (XPathExpressionException e) {
			Tools.logError(null, "Import", "Error parsing hive XML.", e);
		}

		return null;
	}

	public static LogEntry parseLogEntryXml(ByteArrayOutputStream xmlStream) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		String expression = "/logentry";

		try {
			InputSource inputSource = new InputSource(new StringReader(xmlStream.toString()));
			NodeList nodes = (NodeList) xpath.evaluate(expression, inputSource, XPathConstants.NODESET);
			if (nodes.getLength() > 0) {
				LogEntry log = new LogEntry();

				Element node = (Element) nodes.item(0);

				long id = XmlTools.getLong(node, "id", -1);

				if (id >= 0) {
					log.setId(id);
					log.setDescription(XmlTools.getString(node, "description", ""));
					log.setDate(XmlTools.getLong(node, "date", 0));
				}

				return log;
			}
		} catch (XPathExpressionException e) {
			Tools.logError(null, "Import", "Error parsing task XML.", e);
		}

		return null;
	}

	public static Task parseTaskXml(ByteArrayOutputStream xmlStream) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		String expression = "/task";

		try {
			InputSource inputSource = new InputSource(new StringReader(xmlStream.toString()));
			NodeList nodes = (NodeList) xpath.evaluate(expression, inputSource, XPathConstants.NODESET);
			if (nodes.getLength() > 0) {
				Task task = new Task();

				Element node = (Element) nodes.item(0);

				long id = XmlTools.getLong(node, "id", -1);

				if (id >= 0) {
					task.setId(id);
					task.setDescription(XmlTools.getString(node, "description", ""));
					task.setHiveId(XmlTools.getLong(node, "hive_id", 0));
					task.setDate(XmlTools.getLong(node, "date", 0));
					task.setDone(XmlTools.getBoolean(node, "done", false));
				}

				return task;
			}
		} catch (XPathExpressionException e) {
			Tools.logError(null, "Import", "Error parsing log XML.", e);
		}

		return null;
	}

	/*
	 * <list_values dbversion="4" tablename="floral_type">
	 * 		<list_value>
	 * 			<id></id>
	 * 			<name></name>
	 * 		</list_value>
	 * </list_values>
	 */
	
	public static ConfigFile parseConfigFileXml(ByteArrayOutputStream xmlStream) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		String infoExpression = "/list_values";
		String expression = "/list_values/list_value";

		try {
			
			ConfigFile configFile = new ConfigFile();
			List<ListValue> values = new ArrayList<ListValue>();
			
			InputSource inputSource = new InputSource(new StringReader(xmlStream.toString()));
			NodeList rootNodes = (NodeList) xpath.evaluate(infoExpression, inputSource, XPathConstants.NODESET);
			if(rootNodes != null && rootNodes.getLength() > 0) {
				Element rootNode = (Element) rootNodes.item(0);
				configFile.setTablename(rootNode.getAttribute("dbtable"));
			}
			
			InputSource inputSource2 = new InputSource(new StringReader(xmlStream.toString()));
			NodeList nodes = (NodeList) xpath.evaluate(expression, inputSource2, XPathConstants.NODESET);
			
			for(int i = 0; i < nodes.getLength(); i++) {
				ListValue value = new ListValue();

				Element node = (Element) nodes.item(i);

				long id = XmlTools.getLong(node, "id", -1);
				String name = XmlTools.getString(node, "name", "");
				
				value.setId(id);
				value.setName(name);
				values.add(value);
				
			}
			
			configFile.setValues(values);
			
			return configFile;
			
		} catch (XPathExpressionException e) {
			Tools.logError(null, "Import", "Error parsing log XML.", e);
		}

		return null;
	}
	
}
