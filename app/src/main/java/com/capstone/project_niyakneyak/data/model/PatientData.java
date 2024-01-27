package com.capstone.project_niyakneyak.data.model;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PatientData {
    //Field
    private String patientName;
    private String patientAge;
    private List<MedsData> medsData;
    private List<TimeData> timeData;

    //Constructor
    public PatientData(){
        this.patientName = "Guest"; this.patientAge = null;
        medsData = new ArrayList<>(); timeData = new ArrayList<>();
    }
    public PatientData(@Nullable String patientName, @Nullable String patientAge){
        this.patientName = patientName; this.patientAge = patientAge;
        medsData = new ArrayList<>(); timeData = new ArrayList<>();
    }


    //Getter and Setter for patient Info.
    public void setPatientName(String patientName) {this.patientName = patientName;}
    public void setPatientAge(String patientAge) {this.patientAge = patientAge;}
    public String getPatientName() {return patientName;}
    public String getPatientAge() {return patientAge;}


    //Useful Functions for MedsData Configuration
    public void setMedsData(List<MedsData> medsData){this.medsData = medsData;}
    public List<MedsData> getMedsData(){return medsData;}
    public boolean searchMedsData(MedsData target){return medsData.contains(target);}
    public void addMedsData(MedsData data){this.medsData.add(data);}
    public boolean modifyMedsData(MedsData target, MedsData changed){
        if(medsData.contains(target)){
            MedsData data = medsData.get(medsData.indexOf(target));
            data.setID(changed.getID());data.setMeds_name(changed.getMeds_name());
            data.setMeds_detail(changed.getMeds_detail());
            data.setMeds_start_date(changed.getMeds_start_date());
            data.setMeds_end_date(changed.getMeds_end_date());

            for(MedsData.ConsumeDate date : MedsData.ConsumeDate.values())
                data.setDailyConsume(date, changed.isConsumeDate(date));
            for(MedsData.ConsumeTime time : MedsData.ConsumeTime.values())
                data.setActivation(time, changed.isActivated(time));
            return true;
        }
        return false;
    }
    public boolean deleteMedsData(MedsData target){
        if(medsData.contains(target)){
            this.medsData.remove(target); return true;
        }
        return false;
    }


    //Useful Functions for TimeData Configuration
    public void setTimeData(List<TimeData> timeData){this.timeData = timeData;}
    public List<TimeData> getTimeData(){return timeData;}
    public void addTimeData(TimeData timeData){this.timeData.add(timeData);}
    public boolean modifyTimeData(TimeData target, TimeData changed){
        if(timeData.contains(target)){
            TimeData data = timeData.get(timeData.indexOf(target));
            data.setTime(changed.getTime());
            return true;
        }
        return false;
    }
    public boolean deleteTimeData(TimeData target){
        if(timeData.contains(target)){
            timeData.remove(target);
            return true;
        }
        return false;
    }
}
