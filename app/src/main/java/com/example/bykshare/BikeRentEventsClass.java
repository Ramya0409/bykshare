package com.example.bykshare;

public class BikeRentEventsClass {
    String rentereid, bikename, pickup, dropoff, price, approved, location;

    public BikeRentEventsClass(String rentereid, String bikename, String pickup, String dropoff, String price, String approved) {
        this.rentereid = rentereid;
        this.bikename = bikename;
        this.pickup = pickup;
        this.dropoff = dropoff;
        this.price = price;
        this.approved = approved;
    }

    public BikeRentEventsClass(String rentereid, String bikename, String pickup, String dropoff, String price, String approved, String location) {
        this.rentereid = rentereid;
        this.bikename = bikename;
        this.pickup = pickup;
        this.dropoff = dropoff;
        this.price = price;
        this.approved = approved;
        this.location = location;
    }

    public BikeRentEventsClass(String bikename, String pickup, String dropoff, String price, String location) {
        this.bikename = bikename;
        this.pickup = pickup;
        this.dropoff = dropoff;
        this.price = price;
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRentereid() {
        return rentereid;
    }

    public void setRentereid(String rentereid) {
        this.rentereid = rentereid;
    }

    public String getBikename() {
        return bikename;
    }

    public void setBikename(String bikename) {
        this.bikename = bikename;
    }

    public String getPickup() {
        return pickup;
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    public String getDropoff() {
        return dropoff;
    }

    public void setDropoff(String dropoff) {
        this.dropoff = dropoff;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getApproved() {
        return approved;
    }

    public void setApproved(String approved) {
        this.approved = approved;
    }
}
