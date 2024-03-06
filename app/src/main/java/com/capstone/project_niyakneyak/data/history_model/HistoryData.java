package com.capstone.project_niyakneyak.data.history_model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "history_table")
public class HistoryData implements Parcelable {
    @NonNull
    @PrimaryKey
    private long ID;
    private String meds_name;
    private String meds_detail;
    private String meds_start_date;
    private String meds_end_date;
    private int hour, min;
    private String title;

    public HistoryData(long ID, String meds_name, String meds_detail, String meds_start_date, String meds_end_date,
                       int hour, int min, String title){
        this.ID = ID; this.meds_name = meds_name; this.meds_detail = meds_detail;
        this.meds_start_date = meds_start_date; this.meds_end_date = meds_end_date;
        this.hour = hour; this.min = min;  this.title = title;
    }

    protected HistoryData(Parcel in) {
        ID = in.readLong();
        meds_name = in.readString();
        meds_detail = in.readString();
        meds_start_date = in.readString();
        meds_end_date = in.readString();
        hour = in.readInt();
        min = in.readInt();
        title = in.readString();
    }

    public static final Creator<HistoryData> CREATOR = new Creator<HistoryData>() {
        @Override
        public HistoryData createFromParcel(Parcel in) {
            return new HistoryData(in);
        }

        @Override
        public HistoryData[] newArray(int size) {
            return new HistoryData[size];
        }
    };

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getMeds_name() {
        return meds_name;
    }

    public void setMeds_name(String meds_name) {
        this.meds_name = meds_name;
    }

    public String getMeds_detail() {
        return meds_detail;
    }

    public void setMeds_detail(String meds_detail) {
        this.meds_detail = meds_detail;
    }

    public String getMeds_start_date() {
        return meds_start_date;
    }

    public void setMeds_start_date(String meds_start_date) {
        this.meds_start_date = meds_start_date;
    }

    public String getMeds_end_date() {
        return meds_end_date;
    }

    public void setMeds_end_date(String meds_end_date) {
        this.meds_end_date = meds_end_date;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(ID);
        dest.writeString(meds_name);
        dest.writeString(meds_detail);
        dest.writeString(meds_start_date);
        dest.writeString(meds_end_date);
        dest.writeInt(hour);
        dest.writeInt(min);
        dest.writeString(title);
    }
}
