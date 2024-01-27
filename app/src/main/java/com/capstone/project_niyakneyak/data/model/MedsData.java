package com.capstone.project_niyakneyak.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;

public class MedsData implements Serializable {
    public enum ConsumeDate{SUNDAY,MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY}
    public enum ConsumeTime {MORNING,AFTERNOON,EVENING,MIDNIGHT}

    //Field
    private long ID;
    private String meds_name;
    private String meds_detail;



    private String meds_start_date;
    private String meds_end_date;
    private boolean[] dailyConsumeDate;
    private boolean[] activated;


    //Constructor
    public MedsData(@NonNull long ID, @NonNull String meds_name, @Nullable String meds_detail){
        this.ID = ID; this.meds_name = meds_name; this.meds_detail = meds_detail;
        this.activated = new boolean[4];
        this.dailyConsumeDate = new boolean[7];
    }
    public MedsData(@NonNull long ID, @NonNull String meds_name, @Nullable String meds_detail,
                    @Nullable String meds_start_date, @Nullable String meds_end_date){
        this.ID = ID; this.meds_name = meds_name;
        this.meds_detail = meds_detail; this.meds_start_date = meds_start_date; this.meds_end_date = meds_end_date;
        this.activated = new boolean[4];
        this.dailyConsumeDate = new boolean[7];
    }

    //Setter
    public void setID(long ID) {this.ID = ID;}
    public void setMeds_name(String meds_name){this.meds_name = meds_name;}
    public void setMeds_detail(String meds_detail){this.meds_detail = meds_detail;}
    public void setMeds_start_date(String meds_start_date){this.meds_start_date = meds_start_date;}
    public void setMeds_end_date(String meds_end_date) {this.meds_end_date = meds_end_date;}
    public void setDailyConsume(ConsumeDate consumeDate, boolean checked){
        this.dailyConsumeDate[consumeDate.ordinal()] = checked;
    }
    public void setActivation(ConsumeTime consumetime, boolean activated){
        this.activated[consumetime.ordinal()] = activated;
    }

    //Getter
    public long getID() {return ID;}
    public String getMeds_name(){return meds_name;}
    public String getMeds_detail(){return meds_detail;}
    public String getMeds_start_date() {return meds_start_date;}
    public String getMeds_end_date() {return meds_end_date;}
    public boolean isActivated(ConsumeTime consumetime){
        return this.activated[consumetime.ordinal()];
    }
    public boolean isConsumeDate(ConsumeDate consumeDate){
        return this.dailyConsumeDate[consumeDate.ordinal()];
    }
    public boolean[] getActivatedList(){return activated;}
    public boolean[] getDailyConsumeDateList(){return dailyConsumeDate;}

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof MedsData){
            return ID == ((MedsData) obj).getID();
        }
        return false;
    }
}
