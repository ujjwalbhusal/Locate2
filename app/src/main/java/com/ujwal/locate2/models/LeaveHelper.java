package com.ujwal.locate2.models;

public class LeaveHelper {

    String desc;
    String date;
    String time;

    String from;
    String to;

    Boolean isLeave;


    public LeaveHelper(String desc, String date, String time, String from, String to, Boolean isLeave) {
        this.desc = desc;
        this.date = date;
        this.time = time;
        this.from = from;
        this.to = to;
        this.isLeave = isLeave;

    }

    public Boolean getLeave() {
        return isLeave;
    }

    public void setLeave(Boolean leave) {
        isLeave = leave;
    }


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }


}
