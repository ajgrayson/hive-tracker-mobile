package com.grayson.hivetracker.box;

import java.io.StringWriter;

import org.xmlpull.v1.XmlSerializer;

import android.database.Cursor;
import android.util.Xml;

import com.grayson.hivetracker.database.AppSQLiteHelper;
import com.grayson.hivetracker.tools.Tools;

public class XmlGenerator {
	public static XmlFile generateCategoryXML(Cursor cursor) {
		if(cursor == null)
			return null;
		
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		
		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "category");
			
			serializer.attribute("", "dbversion", String.valueOf(AppSQLiteHelper.DATABASE_VERSION));
			serializer.attribute("", "dbtable", AppSQLiteHelper.TABLE_CATEGORY);
			
			long id = cursor.getLong(0);
			String name = cursor.getString(1);
			String deleted = cursor.getInt(3) > 0 ? "true" : "false"; 
			
			serializer.startTag("", "id");
			serializer.text(String.valueOf(id));
			serializer.endTag("", "id");
			
			serializer.startTag("", "name");
			serializer.text(name);
			serializer.endTag("", "name");
			
			serializer.startTag("", "deleted");
			serializer.text(deleted);
			serializer.endTag("", "deleted");
			
			serializer.endTag("", "category");
			serializer.endDocument();
			
			return new XmlFile(id, String.format("%s_%s", name, id), writer.toString());
		} catch (Exception e) {
			Tools.logError(null, "XML", "Error generating XML", e);
			return null;
		}
	}
	
	public static XmlFile generateLocationXML(Cursor cursor) {
		if(cursor == null)
			return null;
		
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		
		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "location");
			
			serializer.attribute("", "dbversion", String.valueOf(AppSQLiteHelper.DATABASE_VERSION));
			serializer.attribute("", "dbtable", AppSQLiteHelper.TABLE_LOCATION);
			
			long id = cursor.getLong(0);
			String categoryId = String.valueOf(cursor.getLong(1));
			String name = cursor.getString(2);
			String mafId = String.valueOf(cursor.getLong(3));
			String latitude = String.valueOf(cursor.getDouble(4));
			String longitude = String.valueOf(cursor.getDouble(5));
			String deleted = cursor.getInt(7) > 0 ? "true" : "false"; 
			
			serializer.startTag("", "id");
			serializer.text(String.valueOf(id));
			serializer.endTag("", "id");
			
			serializer.startTag("", "name");
			serializer.text(name);
			serializer.endTag("", "name");
			
			serializer.startTag("", "category_id");
			serializer.text(categoryId);
			serializer.endTag("", "category_id");

			serializer.startTag("", "maf_id");
			serializer.text(mafId);
			serializer.endTag("", "maf_id");
			
			serializer.startTag("", "latitude");
			serializer.text(latitude);
			serializer.endTag("", "latitude");

			serializer.startTag("", "longitude");
			serializer.text(longitude);
			serializer.endTag("", "longitude");
			
			serializer.startTag("", "deleted");
			serializer.text(deleted);
			serializer.endTag("", "deleted");
			
			serializer.endTag("", "location");
			serializer.endDocument();
			
			return new XmlFile(id, String.format("%s_%s", name, id), writer.toString());
		} catch (Exception e) {
			Tools.logError(null, "XML", "Error generating XML", e);
			return null;
		}
	}
	
	public static XmlFile generateHiveXML(Cursor cursor) {
		if(cursor == null)
			return null;
		
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		
		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "hive");
			
			serializer.attribute("", "dbversion", String.valueOf(AppSQLiteHelper.DATABASE_VERSION));
			serializer.attribute("", "dbtable", AppSQLiteHelper.TABLE_HIVE);
			
			long id = cursor.getLong(0);
			String locationId = String.valueOf(cursor.getLong(1));
			String qrCode = Tools.getStringNotNull(cursor.getString(2));
			String queenType = Tools.getStringNotNull(cursor.getString(3)); //possibly this needs to be NOT NULL
			String beeId = String.valueOf(cursor.getLong(4));
			String broodId = String.valueOf(cursor.getLong(5));
			String healthId = String.valueOf(cursor.getLong(6));
			String foodId = String.valueOf(cursor.getLong(7));
			String varroaId = String.valueOf(cursor.getLong(8));
			String virusId = String.valueOf(cursor.getLong(9));
			String pollenId = String.valueOf(cursor.getLong(10));
			String splitType = Tools.getStringNotNull(cursor.getString(11));
			String treatmentId = String.valueOf(cursor.getLong(12));
			String treatmentDate = String.valueOf(cursor.getLong(13));
			String winteredDate = String.valueOf(cursor.getLong(14));
			String fedDate = String.valueOf(cursor.getLong(15));
			String harvestedDate = String.valueOf(cursor.getLong(16));
			String moving = cursor.getLong(17) > 0 ? "true" : "false";
			String isVarroaSample = cursor.getLong(20) > 0 ? "true" : "false";
			String varroaCount = String.valueOf(cursor.getLong(21));
			String isGoodProducer = cursor.getLong(22) > 0 ? "true" : "false";
			String treatmentIn = cursor.getLong(23) > 0 ? "true" : "false";
			String deleted = cursor.getInt(19) > 0 ? "true" : "false"; 
			
			serializer.startTag("", "id");
			serializer.text(String.valueOf(id));
			serializer.endTag("", "id");
			
			serializer.startTag("", "location_id");
			serializer.text(locationId);
			serializer.endTag("", "location_id");
			
			serializer.startTag("", "qr_code");
			serializer.text(qrCode);
			serializer.endTag("", "qr_code");
			
			serializer.startTag("", "bee_id");
			serializer.text(beeId);
			serializer.endTag("", "bee_id");
			
			serializer.startTag("", "brood_id");
			serializer.text(broodId);
			serializer.endTag("", "brood_id");
			
			serializer.startTag("", "food_id");
			serializer.text(foodId);
			serializer.endTag("", "food_id");
			
			serializer.startTag("", "health_id");
			serializer.text(healthId);
			serializer.endTag("", "health_id");
			
			serializer.startTag("", "pollen_id");
			serializer.text(pollenId);
			serializer.endTag("", "pollen_id");
			
			serializer.startTag("", "queen_type");
			serializer.text(queenType);
			serializer.endTag("", "queen_type");
			
			serializer.startTag("", "split_type");
			serializer.text(splitType);
			serializer.endTag("", "split_type");
			
			serializer.startTag("", "varroa_id");
			serializer.text(varroaId);
			serializer.endTag("", "varroa_id");
			
			serializer.startTag("", "virus_id");
			serializer.text(virusId);
			serializer.endTag("", "virus_id");
			
			serializer.startTag("", "treatment_id");
			serializer.text(treatmentId);
			serializer.endTag("", "treatment_id");
			
			serializer.startTag("", "treatment_date");
			serializer.text(treatmentDate);
			serializer.endTag("", "treatment_date");
			
			serializer.startTag("", "wintered_date");
			serializer.text(winteredDate);
			serializer.endTag("", "wintered_date");
			
			serializer.startTag("", "fed_date");
			serializer.text(fedDate);
			serializer.endTag("", "fed_date");
			
			serializer.startTag("", "harvested_date");
			serializer.text(harvestedDate);
			serializer.endTag("", "harvested_date");
			
			serializer.startTag("", "moving");
			serializer.text(moving);
			serializer.endTag("", "moving");
			
			serializer.startTag("", "is_varroa_sample");
			serializer.text(isVarroaSample);
			serializer.endTag("", "is_varroa_sample");
			
			serializer.startTag("", "varroa_count");
			serializer.text(varroaCount);
			serializer.endTag("", "varroa_count");
			
			serializer.startTag("", "is_good_producer");
			serializer.text(isGoodProducer);
			serializer.endTag("", "is_good_producer");
			
			serializer.startTag("", "treatment_in");
			serializer.text(treatmentIn);
			serializer.endTag("", "treatment_in");
			
			serializer.startTag("", "deleted");
			serializer.text(deleted);
			serializer.endTag("", "deleted");
			
			serializer.endTag("", "hive");
			serializer.endDocument();
			
			return new XmlFile(id, qrCode, writer.toString());
		} catch (Exception e) {
			Tools.logError(null, "XML", "Error generating XML", e);
			return null;
		}
	}
	
	public static XmlFile generateTaskXML(Cursor cursor) {
		if(cursor == null)
			return null;
		
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		
		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "task");
			
			serializer.attribute("", "dbversion", String.valueOf(AppSQLiteHelper.DATABASE_VERSION));
			serializer.attribute("", "dbtable", AppSQLiteHelper.TABLE_TASK);
			
			long id = cursor.getLong(0);
			String date = String.valueOf(cursor.getLong(1));
			String description = Tools.getStringNotNull(cursor.getString(2));
			String done = cursor.getLong(3) > 0 ? "true" : "false";
			String hiveId = Tools.getStringNotNull(cursor.getString(4));
			
			serializer.startTag("", "id");
			serializer.text(String.valueOf(id));
			serializer.endTag("", "id");
			
			serializer.startTag("", "date");
			serializer.text(date);
			serializer.endTag("", "date");
			
			serializer.startTag("", "description");
			serializer.text(description);
			serializer.endTag("", "description");
			
			serializer.startTag("", "done");
			serializer.text(done);
			serializer.endTag("", "done");
			
			serializer.startTag("", "hive_id");
			serializer.text(hiveId);
			serializer.endTag("", "hive_id");
			
			serializer.endTag("", "task");
			serializer.endDocument();
			
			return new XmlFile(id, String.valueOf(id), writer.toString());
		} catch (Exception e) {
			Tools.logError(null, "XML", "Error generating XML", e);
			return null;
		}
	}
	
	public static XmlFile generateLogXML(Cursor cursor) {
		if(cursor == null)
			return null;
		
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		
		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "logentry");
			
			serializer.attribute("", "dbversion", String.valueOf(AppSQLiteHelper.DATABASE_VERSION));
			serializer.attribute("", "dbtable", AppSQLiteHelper.TABLE_CATEGORY);
			
			long id = cursor.getLong(0);
			String date = String.valueOf(cursor.getLong(1));
			String description = Tools.getStringNotNull(cursor.getString(2));
			
			serializer.startTag("", "id");
			serializer.text(String.valueOf(id));
			serializer.endTag("", "id");
			
			serializer.startTag("", "date");
			serializer.text(date);
			serializer.endTag("", "date");
			
			serializer.startTag("", "description");
			serializer.text(description);
			serializer.endTag("", "description");
			
			serializer.endTag("", "logentry");
			serializer.endDocument();
			
			return new XmlFile(id, String.valueOf(id), writer.toString());
		} catch (Exception e) {
			Tools.logError(null, "XML", "Error generating XML", e);
			return null;
		}
	}
	
	public static XmlFile generateCategoryHistoryXML(Cursor cursor) {
		if(cursor == null)
			return null;
		
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		
		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "category_history");
			
			serializer.attribute("", "dbversion", String.valueOf(AppSQLiteHelper.DATABASE_VERSION));
			serializer.attribute("", "dbtable", AppSQLiteHelper.TABLE_CATEGORY_HISTORY);
			
			while (!cursor.isAfterLast()) {
				serializer.startTag("", "record");

				serializer.startTag("", "id");
				serializer.text(cursor.getLong(0) + "");
				serializer.endTag("", "id");

				serializer.startTag("", "name");
				serializer.text(cursor.getString(1));
				serializer.endTag("", "name");
				
				serializer.startTag("", "timestamp");
				serializer.text(cursor.getLong(2) + "");
				serializer.endTag("", "timestamp");
				
				serializer.startTag("", "deleted");
				serializer.text(cursor.getLong(3) > 0 ? "true" : "false");
				serializer.endTag("", "deleted");
				
				serializer.startTag("", "username");
				serializer.text(cursor.getString(4));
				serializer.endTag("", "username");
				
				serializer.endTag("", "record");
				
				cursor.moveToNext();
			}
			
			serializer.endTag("", "category_history");
			serializer.endDocument();
			
			// -1 as ID because all records will be deleted
			return new XmlFile(-1, String.format("category_history_%s", Tools.getNowAsTimestamp()), writer.toString());
		} catch (Exception e) {
			Tools.logError(null, "XML", "Error generating XML", e);
			return null;
		}
	}
	
	public static XmlFile generateLocationHistoryXML(Cursor cursor) {
		if(cursor == null)
			return null;
		
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		
		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "location_history");
			
			serializer.attribute("", "dbversion", String.valueOf(AppSQLiteHelper.DATABASE_VERSION));
			serializer.attribute("", "dbtable", AppSQLiteHelper.TABLE_LOCATION_HISTORY);
			
			while (!cursor.isAfterLast()) {
				serializer.startTag("", "record");

				serializer.startTag("", "id");
				serializer.text(cursor.getLong(0) + "");
				serializer.endTag("", "id");

				serializer.startTag("", "category_id");
				serializer.text(cursor.getLong(1) + "");
				serializer.endTag("", "category_id");
				
				serializer.startTag("", "name");
				serializer.text(cursor.getString(2));
				serializer.endTag("", "name");
				
				serializer.startTag("", "maf_id");
				serializer.text(cursor.getLong(3) + "");
				serializer.endTag("", "maf_id");
				
				serializer.startTag("", "latitude");
				serializer.text(cursor.getLong(4) + "");
				serializer.endTag("", "latitude");
				
				serializer.startTag("", "longitude");
				serializer.text(cursor.getString(5) + "");
				serializer.endTag("", "longitude");
				
				serializer.startTag("", "timestamp");
				serializer.text(cursor.getLong(6) + "");
				serializer.endTag("", "timestamp");
				
				serializer.startTag("", "deleted");
				serializer.text(cursor.getLong(7) > 0 ? "true" : "false");
				serializer.endTag("", "deleted");
				
				serializer.startTag("", "username");
				serializer.text(cursor.getString(8));
				serializer.endTag("", "username");
				
				serializer.endTag("", "record");
				
				cursor.moveToNext();
			}
			
			serializer.endTag("", "location_history");
			serializer.endDocument();
			
			// -1 as ID because all records will be deleted
			return new XmlFile(-1, String.format("location_history_%s", Tools.getNowAsTimestamp()), writer.toString());
		} catch (Exception e) {
			Tools.logError(null, "XML", "Error generating XML", e);
			return null;
		}
	}
	
	public static XmlFile generateHiveHistoryXML(Cursor cursor) {
		if(cursor == null)
			return null;
		
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		
		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "hive_history");
			
			serializer.attribute("", "dbversion", String.valueOf(AppSQLiteHelper.DATABASE_VERSION));
			serializer.attribute("", "dbtable", AppSQLiteHelper.TABLE_HIVE_HISTORY);
			
			while (!cursor.isAfterLast()) {
				serializer.startTag("", "record");

				serializer.startTag("", "id");
				serializer.text(cursor.getLong(0) + "");
				serializer.endTag("", "id");

				serializer.startTag("", "location_id");
				serializer.text(cursor.getLong(1) + "");
				serializer.endTag("", "location_id");
				
				serializer.startTag("", "qr_code");
				serializer.text(cursor.getString(2));
				serializer.endTag("", "qr_code");
				
				serializer.startTag("", "queen_type");
				serializer.text(cursor.getString(3) + "");
				serializer.endTag("", "queen_type");
				
				serializer.startTag("", "bee_id");
				serializer.text(cursor.getLong(4) + "");
				serializer.endTag("", "bee_id");
				
				serializer.startTag("", "brood_id");
				serializer.text(cursor.getLong(5) + "");
				serializer.endTag("", "brood_id");
				
				serializer.startTag("", "health_id");
				serializer.text(cursor.getLong(6) + "");
				serializer.endTag("", "health_id");
				
				serializer.startTag("", "food_id");
				serializer.text(cursor.getLong(7) + "");
				serializer.endTag("", "food_id");
				
				serializer.startTag("", "varroa_id");
				serializer.text(cursor.getLong(8) + "");
				serializer.endTag("", "varroa_id");
				
				serializer.startTag("", "virus_id");
				serializer.text(cursor.getLong(9) + "");
				serializer.endTag("", "virus_id");
				
				serializer.startTag("", "pollen_id");
				serializer.text(cursor.getLong(10) + "");
				serializer.endTag("", "pollen_id");
				
				serializer.startTag("", "treatment_id");
				serializer.text(cursor.getLong(11) + "");
				serializer.endTag("", "treatment_id");
				
				serializer.startTag("", "split_type");
				serializer.text(cursor.getString(12));
				serializer.endTag("", "split_type");
				
				serializer.startTag("", "treatment_date");
				serializer.text(cursor.getLong(13) + "");
				serializer.endTag("", "treatment_date");
				
				serializer.startTag("", "wintered_date");
				serializer.text(cursor.getLong(14) + "");
				serializer.endTag("", "wintered_date");
				
				serializer.startTag("", "fed_date");
				serializer.text(cursor.getLong(15) + "");
				serializer.endTag("", "fed_date");
				
				serializer.startTag("", "harvested_date");
				serializer.text(cursor.getLong(16) + "");
				serializer.endTag("", "harvested_date");
				
				serializer.startTag("", "moving");
				serializer.text(cursor.getLong(17) > 0 ? "true" : "false" );
				serializer.endTag("", "moving");
				
				serializer.startTag("", "timestamp");
				serializer.text(cursor.getLong(18) + "");
				serializer.endTag("", "timestamp");
				
				serializer.startTag("", "deleted");
				serializer.text(cursor.getLong(19) > 0 ? "true" : "false");
				serializer.endTag("", "deleted");
				
				serializer.startTag("", "username");
				serializer.text(cursor.getString(20));
				serializer.endTag("", "username");
				
				serializer.startTag("", "is_varroa_sample");
				serializer.text(cursor.getLong(21) > 0 ? "true" : "false");
				serializer.endTag("", "is_varroa_sample");
				
				serializer.startTag("", "varroa_count");
				serializer.text(cursor.getLong(22) + "");
				serializer.endTag("", "varroa_count");
				
				serializer.startTag("", "is_good_producer");
				serializer.text(cursor.getLong(23) > 0 ? "true" : "false");
				serializer.endTag("", "is_good_producer");
				
				serializer.startTag("", "treatment_in");
				serializer.text(cursor.getLong(24) > 0 ? "true" : "false");
				serializer.endTag("", "treatment_in");
				
				serializer.endTag("", "record");
				
				cursor.moveToNext();
			}
			
			serializer.endTag("", "hive_history");
			serializer.endDocument();
			
			// -1 as ID because all records will be deleted
			return new XmlFile(-1, String.format("hive_history_%s", Tools.getNowAsTimestamp()), writer.toString());
		} catch (Exception e) {
			Tools.logError(null, "XML", "Error generating XML", e);
			return null;
		}
	}
	
	public static XmlFile generateHarvestRecordsXML(Cursor cursor) {
		if(cursor == null)
			return null;
		
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		
		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "harvest_records");
			
			serializer.attribute("", "dbversion", String.valueOf(AppSQLiteHelper.DATABASE_VERSION));
			serializer.attribute("", "dbtable", AppSQLiteHelper.TABLE_HARVEST_RECORD);
			
			while (!cursor.isAfterLast()) {
				serializer.startTag("", "record");

				serializer.startTag("", "id");
				serializer.text(cursor.getLong(0) + "");
				serializer.endTag("", "id");
				
				serializer.startTag("", "category_id");
				serializer.text(cursor.getLong(1) + "");
				serializer.endTag("", "category_id");

				serializer.startTag("", "location_id");
				serializer.text(cursor.getLong(2) + "");
				serializer.endTag("", "location_id");
				
				serializer.startTag("", "date");
				serializer.text(cursor.getLong(3) + "");
				serializer.endTag("", "date");
				
				serializer.startTag("", "disease");
				serializer.text(cursor.getLong(4) > 0 ? "true" : "false");
				serializer.endTag("", "disease");
				
				serializer.startTag("", "supers");
				serializer.text(cursor.getLong(5) + "");
				serializer.endTag("", "supers");
				
				serializer.startTag("", "floral_type_id");
				serializer.text(cursor.getLong(6) + "");
				serializer.endTag("", "floral_type_id");
				
				serializer.endTag("", "record");
				
				cursor.moveToNext();
			}
			
			serializer.endTag("", "harvest_records");
			serializer.endDocument();
			
			// -1 as ID because all records will be deleted
			return new XmlFile(-1, String.format("harvest_records_%s", Tools.getNowAsTimestamp()), writer.toString());
		} catch (Exception e) {
			Tools.logError(null, "XML", "Error generating XML", e);
			return null;
		}
	}
}
