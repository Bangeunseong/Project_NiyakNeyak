package com.capstone.project_niyakneyak.data.patient_model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.capstone.project_niyakneyak.data.alarm_model.Alarm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MedsData implements Parcelable {

    //Field
    private long ID;
    private String meds_name;
    private String meds_detail;
    private String meds_start_date;
    private String meds_end_date;
    private ArrayList<Integer> alarms;


    //Constructor
    public MedsData(@NonNull long ID, @NonNull String meds_name, @Nullable String meds_detail){
        this.ID = ID; this.meds_name = meds_name; this.meds_detail = meds_detail;
        alarms = new ArrayList<>();
    }
    public MedsData(@NonNull long ID, @NonNull String meds_name, @Nullable String meds_detail,
                    @Nullable String meds_start_date, @Nullable String meds_end_date){
        this.ID = ID; this.meds_name = meds_name;
        this.meds_detail = meds_detail; this.meds_start_date = meds_start_date; this.meds_end_date = meds_end_date;
        alarms = new ArrayList<>();
    }


    protected MedsData(Parcel in) {
        ID = in.readLong();
        meds_name = in.readString();
        meds_detail = in.readString();
        meds_start_date = in.readString();
        meds_end_date = in.readString();
        alarms = in.readArrayList(ClassLoader.getSystemClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(ID);
        dest.writeString(meds_name);
        dest.writeString(meds_detail);
        dest.writeString(meds_start_date);
        dest.writeString(meds_end_date);
        dest.writeArray(alarms.toArray());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MedsData> CREATOR = new Creator<MedsData>() {
        @Override
        public MedsData createFromParcel(Parcel in) {
            return new MedsData(in);
        }

        @Override
        public MedsData[] newArray(int size) {
            return new MedsData[size];
        }
    };

    //Setter
    public void setID(long ID) {this.ID = ID;}
    public void setMeds_name(String meds_name){this.meds_name = meds_name;}
    public void setMeds_detail(String meds_detail){this.meds_detail = meds_detail;}
    public void setMeds_start_date(String meds_start_date){this.meds_start_date = meds_start_date;}
    public void setMeds_end_date(String meds_end_date) {this.meds_end_date = meds_end_date;}

    //Getter
    public long getID() {return ID;}
    public String getMeds_name(){return meds_name;}
    public String getMeds_detail(){return meds_detail;}
    public String getMeds_start_date() {return meds_start_date;}
    public String getMeds_end_date() {return meds_end_date;}

    //Useful Functions for AlarmData Configuration
    public void setAlarms(ArrayList<Integer> alarms){this.alarms = alarms;}
    public ArrayList<Integer> getAlarms(){return alarms;}
    public boolean searchAlarm(int alarmCode){return alarms.contains(alarmCode);}
    public void addAlarm(int alarmCode){alarms.add(alarmCode);}
    public boolean deleteAlarm(int alarmCode){return alarms.remove((Integer)alarmCode);}

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof MedsData) {
            return ID == ((MedsData) obj).getID();
        }
        return false;
    }
}
