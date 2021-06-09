package com.example.claimit.ui.home;

public class ClaimModel {
    private String claimId;
    private String make;
    private String model;
    private String vehicle_year;
    private String created_date;

    public ClaimModel(String claimId, String make, String model, String vehicle_year, String created_date) {
        this.claimId = claimId;
        this.make = make;
        this.model = model;
        this.vehicle_year = vehicle_year;
        this.created_date = created_date;
    }

    @Override
    public String toString() {
        return "ClaimModel{" +
                "claimId='" + claimId + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", vehicle_year='" + vehicle_year + '\'' +
                ", created_date='" + created_date + '\'' +
                '}';
    }

    public String getClaimId() {
        return claimId;
    }

    public void setClaimId(String claimId) {
        this.claimId = claimId;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVehicle_year() {
        return vehicle_year;
    }

    public void setVehicle_year(String vehicle_year) {
        this.vehicle_year = vehicle_year;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

}
