package com.capstone.project_niyakneyak.data.model;

public class TimeData {
    //Field
    private String time;
    private MedsData.ConsumeTime consumeTime;

    //Constructor
    public TimeData(){this.time = null;}
    public TimeData(String time){this.time = time;}
    public TimeData(String time, MedsData.ConsumeTime consumeTime){
        this.time = time; this.consumeTime = consumeTime;
    }

    //Setter
    public void setTime(String time) {this.time = time;}
    public void setConsumeTime(MedsData.ConsumeTime consumeTime) {
        this.consumeTime = consumeTime;
    }

    //Getter
    public String getTime() {return time;}
    public MedsData.ConsumeTime getConsumeTime() {
        return consumeTime;
    }


}
