package com.example.healthmate;

public class UserLocation {
    private double latitude;
    private double longitude;
     public UserLocation(double latitude, double longitude) {
         this.latitude = latitude;
         this.longitude = longitude;
     }
    public UserLocation() {}
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }
}
