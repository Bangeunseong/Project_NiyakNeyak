package com.capstone.project_niyakneyak.data.patient_model;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.capstone.project_niyakneyak.data.alarm_model.Alarm;

import java.util.ArrayList;
import java.util.List;

public class PatientData {
    //Field
    private String patientName;
    private String patientAge;
    private List<MedsData> medsData;

    //Constructor
    public PatientData(){
        this.patientName = "Guest"; this.patientAge = null;
        medsData = new ArrayList<>();
    }
    public PatientData(@Nullable String patientName, @Nullable String patientAge){
        this.patientName = patientName; this.patientAge = patientAge;
        medsData = new ArrayList<>();
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
        if(!medsData.contains(target)) return false;

        MedsData data = medsData.get(medsData.indexOf(target));
        data.setID(changed.getID());data.setMeds_name(changed.getMeds_name());
        data.setMeds_detail(changed.getMeds_detail());
        data.setMeds_start_date(changed.getMeds_start_date());
        data.setMeds_end_date(changed.getMeds_end_date());
        data.setAlarms(changed.getAlarms());
        return true;
    }
    public boolean deleteMedsData(MedsData target){
        if(!medsData.contains(target)) return false;
        this.medsData.remove(target);
        return true;
    }
}
