package com.capstone.project_niyakneyak.data.model;

public class TimeData {
    //Field
    private String time;
    private boolean state;

    //Constructor
    public TimeData(String time, boolean state){this.time = time; this.state = state;}

    //Setter
    public void setTime(String time) {this.time = time;}
    public void setState(boolean state) {this.state = state;}

    //Getter
    public String getTime() {return time;}
    public boolean getState() {return state;}
}
