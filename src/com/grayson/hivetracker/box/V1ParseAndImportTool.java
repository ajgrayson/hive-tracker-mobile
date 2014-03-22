package com.grayson.hivetracker.box;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.grayson.hivetracker.database.AppDataSource;
import com.grayson.hivetracker.database.Category;
import com.grayson.hivetracker.database.Hive;
import com.grayson.hivetracker.database.Location;
import com.grayson.hivetracker.database.LogEntry;
import com.grayson.hivetracker.database.Task;
import com.grayson.hivetracker.tools.Tools;

public class V1ParseAndImportTool {

	public static boolean importCategoriesXML(ByteArrayOutputStream xmlStream) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		String expression = "/Backup/Categories/Category";
		
		AppDataSource db = AppDataSource.getInstance();
		
		try {
			InputSource inputSource = new InputSource(new StringReader(xmlStream.toString()));
			NodeList nodes = (NodeList) xpath.evaluate(expression, inputSource, XPathConstants.NODESET);
			if (nodes.getLength() > 0) {
				// do stuff
				for (int i = 0; i < nodes.getLength(); i++) {
					Category cat = new Category();

					Element node = (Element) nodes.item(i);

					long id = XmlTools.getLong(node, "ID", -1);

					if (id >= 0) {
						cat.setId(id);
						cat.setName(XmlTools.getString(node, "Name", ""));
						cat.setDeleted(XmlTools.getBoolean(node, "Deleted", false));
						
						db.importCategory(cat);
					}
				}
			}
		} catch (XPathExpressionException e) {
			Tools.logError(null, "Import", "Error importing XML.", e);
		}
		return true;
	}

	public static boolean importLocationsXML(ByteArrayOutputStream xmlStream) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		String expression = "/Backup/Locations/Location";

		AppDataSource db = AppDataSource.getInstance();
		
		try {
			InputSource inputSource = new InputSource(new StringReader(xmlStream.toString()));
			NodeList nodes = (NodeList) xpath.evaluate(expression, inputSource, XPathConstants.NODESET);
			if (nodes.getLength() > 0) {
				// do stuff
				for (int i = 0; i < nodes.getLength(); i++) {
					Location loc = new Location();

					Element node = (Element) nodes.item(i);

					long id = XmlTools.getLong(node, "ID", -1);

					if (id >= 0) {
						loc.setId(id);
						loc.setName(XmlTools.getString(node, "Name", ""));
						loc.setMafId(XmlTools.getLong(node, "MafID", 0));
						loc.setLatitude(XmlTools.getDouble(node, "Latitude", 0));
						loc.setLongitude(XmlTools.getDouble(node, "Longitude", 0));
						loc.setCategoryId(XmlTools.getLong(node, "CategoryID", 0));
						loc.setDeleted(XmlTools.getBoolean(node, "Deleted", false));
						
						db.importLocation(loc);
					}
				}
			}
		} catch (XPathExpressionException e) {
			Tools.logError(null, "Import", "Error importing XML.", e);
		}

		return true;
	}

	public static boolean importHivesXML(ByteArrayOutputStream xmlStream) {
		XPath xpathVersion = XPathFactory.newInstance().newXPath();
		XPath xpathHives = XPathFactory.newInstance().newXPath();
		String versionExpression = "/Backup/DatabaseInfo";
		String hiveExpression = "/Backup/Hives/Hive";

		AppDataSource db = AppDataSource.getInstance();
		
		try {
			InputSource inputSource1 = new InputSource(new StringReader(xmlStream.toString()));

			int dbVersion = 1;
			NodeList versionNodes = (NodeList) xpathVersion.evaluate(versionExpression, inputSource1, XPathConstants.NODESET);
			if (versionNodes.getLength() > 0) {
				Element node = (Element) versionNodes.item(0);
				dbVersion = XmlTools.getInt(node, "Version", 1);
			}

			InputSource inputSource2 = new InputSource(new StringReader(xmlStream.toString()));
			
			NodeList nodes = (NodeList) xpathHives.evaluate(hiveExpression, inputSource2, XPathConstants.NODESET);
			if (nodes.getLength() > 0) {
				// do stuff
				for (int i = 0; i < nodes.getLength(); i++) {
					Hive hive = new Hive();

					Element node = (Element) nodes.item(i);

					long id = Long.parseLong(XmlTools.getValue(node, "ID"));

					if (id >= 0) {
						hive.setId(id);
						hive.setQrCode(XmlTools.getString(node, "QRCode", ""));
						hive.setLocationId(XmlTools.getLong(node, "LocationID", 0));

						if (dbVersion == 1) {
							hive.setQueenType(XmlTools.getString(node, "Queen", ""));
						} else if (dbVersion >= 2) {
							hive.setQueenType(XmlTools.getString(node, "QueenType", ""));
						}
						
						if (dbVersion < 3) {
							hive.setSplitType(XmlTools.getString(node, "Split", ""));
						} else { // Version >= 3
							hive.setSplitType(XmlTools.getString(node, "SplitType", ""));
						}
						
						hive.setBeeId(XmlTools.getAttributeLong(node, "Bee", "BeeID", 0));
						hive.setBroodId(XmlTools.getAttributeLong(node, "Brood", "BroodID", 0));
						hive.setHealthId(XmlTools.getAttributeLong(node, "Health", "HealthID", 0));
						hive.setFoodId(XmlTools.getAttributeLong(node, "Food", "FoodID", 0));
						hive.setVarroaId(XmlTools.getAttributeLong(node, "Varroa", "VarroaID", 0));
						hive.setVirusId(XmlTools.getAttributeLong(node, "Virus", "VirusID", 0));
						hive.setPollenId(XmlTools.getAttributeLong(node, "Pollen", "PollenID", 0));
						hive.setTreatmentId(XmlTools.getAttributeLong(node, "Treatment", "TreatmentID", 0));
						
						// fix -1 values in previous implementations
						// also adjust to 0 based foreign key tables
						if (hive.getBeeId() <= 0) {
							hive.setBeeId(0);
						} else {
							hive.setBeeId(hive.getBeeId()-1);
						}
						
						if (hive.getBroodId() <= 0) {
							hive.setBroodId(0);
						} else {
							hive.setBroodId(hive.getBroodId()-1);
						}
						
						if (hive.getVarroaId() <= 0) {
							hive.setVarroaId(0);
						} else {
							hive.setVarroaId(hive.getVarroaId()-1);
						}
						
						if (hive.getVirusId() <= 0) {
							hive.setVirusId(0);
						} else {
							hive.setVirusId(hive.getVirusId()-1);
						}
						
						if (hive.getTreatmentId() <= 0) {
							hive.setTreatmentId(0);
						} else {
							hive.setTreatmentId(hive.getTreatmentId()-1);
						}
						
						// 
						// pollen, health and food are not reduced by
						// 1 because a default 'unknown' entry was added
						// to the table rather than decrementing all the
						// primary keys
						//
						if (hive.getHealthId() <= 0) {
							hive.setHealthId(0);
						} else {
							//hive.setHealthId(hive.getHealthId()-1);
						}
						
						if (hive.getFoodId() <= 0) {
							hive.setFoodId(0);
						} else {
							//hive.setFoodId(hive.getFoodId()-1);
						}
						
						if (hive.getPollenId() <= 0) {
							hive.setPollenId(0);
						} else {
							//hive.setPollenId(hive.getPollenId()-1);
						}
						
						
						hive.setMoving(XmlTools.getBoolean(node, "Moving", false));

						// harvesting, treating, feeding and wintering wasn't possible before 
						// V2 so all these dates can be defaulted as it was set incorrectly previously.
						hive.setTreatmentDate(0);
						hive.setHarvestedDate(0);
						hive.setFedDate(0);
						hive.setWinteredDate(0);
						
						hive.setDeleted(XmlTools.getBoolean(node, "Deleted", false));
						
						db.importHive(hive);
					}
				}
			}
		} catch (XPathExpressionException e) {
			Tools.logError(null, "Import", "Error importing XML.", e);
		}

		return true;
	}

	public static boolean importTasksXML(ByteArrayOutputStream xmlStream) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		String expression = "/Backup/Tasks/Task";

		AppDataSource db = AppDataSource.getInstance();
		
		try {
			InputSource inputSource = new InputSource(new StringReader(xmlStream.toString()));
			NodeList nodes = (NodeList) xpath.evaluate(expression, inputSource, XPathConstants.NODESET);
			if (nodes.getLength() > 0) {
				// do stuff
				for (int i = 0; i < nodes.getLength(); i++) {
					Task task = new Task();

					Element node = (Element) nodes.item(i);

					long id = XmlTools.getLong(node, "ID", -1);

					if (id >= 0) {
						task.setId(id);
						task.setDescription(XmlTools.getString(node, "Description", ""));
						task.setHiveId(XmlTools.getLong(node, "HiveID", 0));
						task.setDate(XmlTools.getLong(node, "Date", 0));
						task.setDone(XmlTools.getBoolean(node, "Done", false));

						db.importTask(task);
					}
				}
			}
		} catch (XPathExpressionException e) {
			Tools.logError(null, "Import", "Error importing XML.", e);
		}

		return true;
	}

	public static boolean importLogEntryXML(ByteArrayOutputStream xmlStream) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		String expression = "/Backup/LogEntries/LogEntry";

		AppDataSource db = AppDataSource.getInstance();
		
		try {
			InputSource inputSource = new InputSource(new StringReader(xmlStream.toString()));
			NodeList nodes = (NodeList) xpath.evaluate(expression, inputSource, XPathConstants.NODESET);
			if (nodes.getLength() > 0) {
				// do stuff
				for (int i = 0; i < nodes.getLength(); i++) {
					LogEntry logEntry = new LogEntry();

					Element node = (Element) nodes.item(i);

					long id = XmlTools.getLong(node, "ID", -1);

					if (id >= 0) {
						logEntry.setId(id);
						logEntry.setDescription(XmlTools.getString(node, "Description", ""));
						logEntry.setDate(XmlTools.getLong(node, "Date", 0));

						db.importLogEntry(logEntry);
					}
				}
			}
		} catch (XPathExpressionException e) {
			Tools.logError(null, "Import", "Error importing XML.", e);
		}

		return true;
	}

}
