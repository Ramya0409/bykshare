package com.example.bykshare;

public class ListingBikeClass {

    String useremailid, nameofbike, biketype, riderheight, hourlyrate, dailyrate, weeklyrate, location;

    public ListingBikeClass(String nameofbike, String hourlyrate, String biketype, String riderheight) {
        this.nameofbike = nameofbike;
        this.biketype = biketype;
        this.riderheight = riderheight;
        this.hourlyrate = hourlyrate;
    }

    public ListingBikeClass(String nameofbike, String biketype, String riderheight, String hourlyrate, String dailyrate, String weeklyrate, String location) {
        this.nameofbike = nameofbike;
        this.biketype = biketype;
        this.riderheight = riderheight;
        this.hourlyrate = hourlyrate;
        this.dailyrate = dailyrate;
        this.weeklyrate = weeklyrate;
        this.location = location;
    }

    public ListingBikeClass(String nameofbike, String biketype, String riderheight, String hourlyrate, String location) {
        this.nameofbike = nameofbike;
        this.biketype = biketype;
        this.riderheight = riderheight;
        this.hourlyrate = hourlyrate;
        this.location = location;
    }

    public ListingBikeClass(String useremail,String nameofbike, String biketype, String riderheight, String hourlyrate, String dailyrate, String weeklyrate, String location) {
        this.useremailid = useremail;
        this.nameofbike = nameofbike;
        this.biketype = biketype;
        this.riderheight = riderheight;
        this.hourlyrate = hourlyrate;
        this.dailyrate = dailyrate;
        this.weeklyrate = weeklyrate;
        this.location = location;
    }

    public String getUseremailid() {
        return useremailid;
    }

    public void setUseremailid(String useremailid) {
        this.useremailid = useremailid;
    }

    public String getNameofbike() {
        return nameofbike;
    }

    public void setNameofbike(String nameofbike) {
        this.nameofbike = nameofbike;
    }

    public String getBiketype() {
        return biketype;
    }

    public void setBiketype(String biketype) {
        this.biketype = biketype;
    }

    public String getRiderheight() {
        return riderheight;
    }

    public void setRiderheight(String riderheight) {
        this.riderheight = riderheight;
    }

    public String getHourlyrate() {
        return hourlyrate;
    }

    public void setHourlyrate(String hourlyrate) {
        this.hourlyrate = hourlyrate;
    }

    public String getDailyrate() {
        return dailyrate;
    }

    public void setDailyrate(String dailyrate) {
        this.dailyrate = dailyrate;
    }

    public String getWeeklyrate() {
        return weeklyrate;
    }

    public void setWeeklyrate(String weeklyrate) {
        this.weeklyrate = weeklyrate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
