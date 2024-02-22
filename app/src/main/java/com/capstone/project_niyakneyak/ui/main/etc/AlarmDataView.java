package com.capstone.project_niyakneyak.ui.main.etc;

public class AlarmDataView {
    private boolean sunday, monday, tuesday, wednesday, thursday, friday, saturday;

    public AlarmDataView(){}
    public AlarmDataView(boolean sunday, boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday) {
        this.sunday = sunday;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
    }

    public boolean isSunday() {return sunday;}

    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }

    public boolean isMonday() {
        return monday;
    }

    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    public boolean isFriday() {
        return friday;
    }

    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }

    public String getDisplayData(){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < 7; i++){
            switch(i){
                case 0->{if(sunday) builder.append("Sun");}
                case 1->{if(monday) {if(!builder.toString().isEmpty()) builder.append(","); builder.append("Mon");}}
                case 2->{if(tuesday) {if(!builder.toString().isEmpty()) builder.append(","); builder.append("Tue");}}
                case 3->{if(wednesday) {if(!builder.toString().isEmpty()) builder.append(","); builder.append("Wed");}}
                case 4->{if(thursday) {if(!builder.toString().isEmpty()) builder.append(","); builder.append("Thu");}}
                case 5->{if(friday) {if(!builder.toString().isEmpty()) builder.append(","); builder.append("Fri");}}
                case 6->{if(saturday) {if(!builder.toString().isEmpty()) builder.append(","); builder.append("Sat");}}
            }
        }
        return builder.toString();
    }
}
