package com.capstone.project_niyakneyak.data.model;

import java.util.ArrayList;
import java.util.List;

public class MedsData {
    //Field
    private String ID;
    private String meds_name;
    private String meds_detail;
    private String meds_date;
    private List<TimeData> meds_time;


    //Constructor
    public MedsData(String ID, String meds_name, String meds_detail){
        this.ID = ID; this.meds_name = meds_name; this.meds_detail = meds_detail;
    }
    public MedsData(String ID, String meds_name, String meds_detail, String meds_date){
        this.ID = ID; this.meds_name = meds_name; this.meds_detail = meds_detail;
        this.meds_date = meds_date; meds_time = new ArrayList<>();
    }
    public MedsData(String ID, String meds_name, String meds_detail, String meds_date, List<TimeData> meds_time){
        this.ID = ID; this.meds_name = meds_name; this.meds_detail = meds_detail;
        this.meds_date = meds_date; this.meds_time = meds_time;
    }

    //Setter
    public void setID(String ID) {this.ID = ID;}
    public void setMeds_name(String meds_name){this.meds_name = meds_name;}
    public void setMeds_detail(String meds_detail){this.meds_detail = meds_detail;}
    public void setMeds_date(String meds_date) {this.meds_date = meds_date;}
    public void setMeds_time(List<TimeData> meds_time){this.meds_time = meds_time;}

    //Getter
    public String getID() {return ID;}
    public String getMeds_name(){return meds_name;}
    public String getMeds_detail(){return meds_detail;}
    public String getMeds_date() {return meds_date;}
    public List<TimeData> getMeds_time(){return meds_time;}


    //Add, Modify, Delete Time data
    //Addition
    public void addTime(String meds_time, boolean flag){this.meds_time.add(new TimeData(meds_time, flag));}
    public void addTime(TimeData data){this.meds_time.add(data);}

    //Modification
    public void modifyTime(TimeData origin, TimeData changed){
        if(this.meds_time.contains(origin)){
            origin.setTime(changed.getTime());
            origin.setState(changed.getState());
        }
    }

    //Deletion
    public void deleteTime(TimeData target){
        if(this.meds_time.contains(target)){
            meds_time.remove(target);
        }
    }
}
