package com.capstone.project_niyakneyak.data.model;

public class MedsData {
    private String meds_name;
    private String meds_detail;

    public MedsData(String meds_name, String meds_detail){
        this.meds_name = meds_name; this.meds_detail = meds_detail;
    }

    public void setMeds_name(String meds_name){this.meds_name = meds_name;}
    public void setMeds_detail(String meds_detail){this.meds_detail = meds_detail;}
    public String getMeds_name(){return meds_name;}
    public String getMeds_detail(){return meds_detail;}
}
