package com.example.healthmate;

public class PillIntake {
    private String medicationName;
    private boolean taken;


    public PillIntake(String medicationName, boolean taken) {
        this.medicationName = medicationName;
        this.taken = taken;
    }
    public PillIntake() {}
    public String getMedicationName() {
        return medicationName;
    }

    public boolean isTaken() {
        return taken;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }
}
