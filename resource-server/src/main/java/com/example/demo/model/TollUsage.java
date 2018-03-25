package com.example.demo.model;

public class TollUsage {
	
	public String Id;
	public String stationId;
	public String licensePlate;
	public String timestamp;
	
	public TollUsage() {}
	
	public TollUsage(String id, String stationid, String licenseplate, String timestamp){
		this.Id = id;
		this.stationId = stationid;
		this.licensePlate = licenseplate;
		this.timestamp = timestamp;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	public String getLicensePlate() {
		return licensePlate;
	}

	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	

}
