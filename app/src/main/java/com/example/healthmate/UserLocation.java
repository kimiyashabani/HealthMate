package com.example.healthmate;

public class UserLocation {
    private double latitude;
    private double longitude;
    private String city;
    private String country;
     public UserLocation(double latitude, double longitude, String city, String country) {
         this.latitude = latitude;
         this.longitude = longitude;
         this.city = city;
         this.country = country;
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
    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }
}
