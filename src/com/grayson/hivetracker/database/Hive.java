package com.grayson.hivetracker.database;

public class Hive  {
	private long id;
	private String qrCode;
	
	private long locationId;
	private String locationName;
	
	private String queenType;
	
	private long beeId;
	private String beeName;
	
	private long broodId;
	private String broodName;
	
	private long foodId;
	private String foodName;
	
	private long healthId;
	private String healthName;
	
	private long varroaId;
	private String varroaName;
	
	private long virusId;
	private String virusName;
	
	private long pollenId;
	private String pollenName;
	
	//private long splitId;
	//private String splitName;
	
	private String splitType;

	private long treatmentId;
	private String treatmentName;
	
	private long treatmentDate;
	private long winteredDate;
	private long fedDate;
	private long harvestedDate;
	
	private boolean moving;
	
	private boolean isVarroaSample;
	
	private long varroaCount;
	
	private boolean isGoodProducer;
	
	private boolean treatmentIn;
	
	private boolean deleted;
	
	public Hive() {
		this.locationId = 0;
		this.beeId = 1;
		this.broodId = 1;
		this.foodId = 1;
		this.healthId = 1;
		this.varroaId = 1;
		this.virusId = 1;
		this.pollenId = 1;
		this.treatmentId = 1;
		this.isGoodProducer = false;
		this.isVarroaSample = false;
		this.moving = false;
		this.qrCode = "";
		this.queenType = "";
		this.splitType = "";
		this.treatmentDate = 0;
		this.fedDate = 0;
		this.winteredDate = 0;
		this.harvestedDate = 0;
		this.treatmentIn = false;
		this.varroaCount = 0;
		this.deleted = false;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String getQueenType() {
		return queenType;
	}

	public void setQueenType(String queenType) {
		this.queenType = queenType;
	}
	
	public long getLocationId() {
		return locationId;
	}

	public void setLocationId(long locationId) {
		this.locationId = locationId;
	}
	
	public String getQrCode() {
		return qrCode;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}
	
	public long getBeeId() {
		return beeId;
	}

	public void setBeeId(long beeId) {
		this.beeId = beeId;
	}
	
	public long getBroodId() {
		return broodId;
	}

	public void setBroodId(long broodId) {
		this.broodId = broodId;
	}
	
	public long getFoodId() {
		return foodId;
	}

	public void setFoodId(long foodId) {
		this.foodId = foodId;
	}
	
	public long getHealthId() {
		return healthId;
	}

	public void setHealthId(long healthId) {
		this.healthId = healthId;
	}
	
	public long getVarroaId() {
		return varroaId;
	}

	public void setVarroaId(long varroaId) {
		this.varroaId = varroaId;
	}
	
	public long getVirusId() {
		return virusId;
	}

	public void setVirusId(long virusId) {
		this.virusId = virusId;
	}
	
	public long getPollenId() {
		return pollenId;
	}

	public void setPollenId(long pollenId) {
		this.pollenId = pollenId;
	}
	
//	public long getSplitId() {
//		return splitId;
//	}
//
//	public void setSplitId(long splitId) {
//		this.splitId = splitId;
//	}
	
	public String getSplitType() {
		return splitType;
	}
	
	public void setSplitType(String splitType) {
		this.splitType = splitType;
	}
	
	public long getTreatmentId() {
		return treatmentId;
	}

	public void setTreatmentId(long treatmentId) {
		this.treatmentId = treatmentId;
	}
	
	public long getTreatmentDate() {
		return treatmentDate;
	}

	public void setTreatmentDate(long treatmentDate) {
		this.treatmentDate = treatmentDate;
	}
	
	public long getWinteredDate() {
		return winteredDate;
	}

	public void setWinteredDate(long winteredDate) {
		this.winteredDate = winteredDate;
	}
	
	public long getFedDate() {
		return fedDate;
	}

	public void setFedDate(long fedDate) {
		this.fedDate = fedDate;
	}

	public long getHarvestedDate() {
		return harvestedDate;
	}

	public void setHarvestedDate(long harvestedDate) {
		this.harvestedDate = harvestedDate;
	}
	
	public String toString() {
		return "Hive #: " + qrCode;
	}
	
	
	// NAMES
	
	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	
	public String getBeeName() {
		return beeName;
	}

	public void setBeeName(String beeName) {
		this.beeName = beeName;
	}
	
	public String getBroodName() {
		return broodName;
	}

	public void setBroodName(String broodName) {
		this.broodName = broodName;
	}
	
	public String getFoodName() {
		return foodName;
	}

	public void setFoodName(String foodName) {
		this.foodName = foodName;
	}
	
	public String getHealthName() {
		return healthName;
	}

	public void setHealthName(String healthName) {
		this.healthName = healthName;
	}
	
	public String getVarroaName() {
		return varroaName;
	}

	public void setVarroaName(String varroaName) {
		this.varroaName = varroaName;
	}
	
	public String getVirusName() {
		return virusName;
	}

	public void setVirusName(String virusName) {
		this.virusName = virusName;
	}
	
	public String getPollenName() {
		return pollenName;
	}

	public void setPollenName(String pollenName) {
		this.pollenName = pollenName;
	}
	
//	public String getSplitName() {
//		return splitName;
//	}
//
//	public void setSplitName(String splitName) {
//		this.splitName = splitName;
//	}
	
	public String getTreatmentName() {
		return treatmentName;
	}

	public void setTreatmentName(String treatmentName) {
		this.treatmentName = treatmentName;
	}
	
	public Boolean getMoving() {
		return moving;
	}

	public void setMoving(Boolean moving) {
		this.moving = moving;
	}
	
	public Boolean getIsVarroaSample() {
		return isVarroaSample;
	}

	public void setIsVarroaSample(Boolean isVarroaSample) {
		this.isVarroaSample = isVarroaSample;
	}
	
	public long getVarroaCount() {
		return varroaCount;
	}

	public void setVarroaCount(long varroaCount) {
		this.varroaCount = varroaCount;
		if(this.varroaCount > 300) {
			this.varroaCount = 300;
		}
	}
	
	public Boolean getIsGoodProducer() {
		return isGoodProducer;
	}

	public void setIsGoodProducer(Boolean isGoodProducer) {
		this.isGoodProducer = isGoodProducer;
	}

	public boolean isTreatmentIn() {
		return treatmentIn;
	}

	public void setTreatmentIn(boolean treatmentIn) {
		this.treatmentIn = treatmentIn;
	}
	
	public boolean isDeleted() {
		return deleted;
	}
	
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}
