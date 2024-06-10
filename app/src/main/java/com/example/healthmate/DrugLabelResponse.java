package com.example.healthmate;

import com.google.gson.annotations.SerializedName;


import java.util.List;

public class DrugLabelResponse {
    @SerializedName("results")
    public List<DrugLabelResult> results;

    public class DrugLabelResult {
        @SerializedName("openfda")
        public OpenFda openfda;
    }

    public class OpenFda {
        @SerializedName("generic_name")
        public String[] generic_name;
    }
}
