package com.example.healthmate;

public class HealthData {
    private String type;
    private String value;
    //Default constructor:
    public HealthData(){}
    public HealthData(String type, String value){
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
