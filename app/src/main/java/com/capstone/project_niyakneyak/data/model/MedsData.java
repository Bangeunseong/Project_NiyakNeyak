package com.capstone.project_niyakneyak.data.model;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MedsData implements Serializable {
    //Field
    private long ID;
    private String meds_name;
    private String meds_detail;
    private String meds_date;
    private List<TimeData> meds_time;


    //Constructor
    public MedsData(long ID, String meds_name, String meds_detail){
        this.ID = ID; this.meds_name = meds_name; this.meds_detail = meds_detail;
    }
    public MedsData(long ID, String meds_name, String meds_detail, String meds_date){
        this.ID = ID; this.meds_name = meds_name; this.meds_detail = meds_detail;
        this.meds_date = meds_date; meds_time = new ArrayList<>();
    }
    public MedsData(long ID, String meds_name, String meds_detail, String meds_date, List<TimeData> meds_time){
        this.ID = ID; this.meds_name = meds_name; this.meds_detail = meds_detail;
        this.meds_date = meds_date; this.meds_time = meds_time;
    }

    //Setter
    public void setID(long ID) {this.ID = ID;}
    public void setMeds_name(String meds_name){this.meds_name = meds_name;}
    public void setMeds_detail(String meds_detail){this.meds_detail = meds_detail;}
    public void setMeds_date(String meds_date) {this.meds_date = meds_date;}
    public void setMeds_time(List<TimeData> meds_time){this.meds_time = meds_time;}

    //Getter
    public long getID() {return ID;}
    public String getMeds_name(){return meds_name;}
    public String getMeds_detail(){return meds_detail;}
    public String getMeds_date() {return meds_date;}
    public List<TimeData> getMeds_time(){return meds_time;}

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof MedsData){
            return ID == ((MedsData) obj).getID();
        }
        return false;
    }
}
