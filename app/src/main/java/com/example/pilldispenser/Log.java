package com.example.pilldispenser;

public class Log {
    public String date;
    public int id;
    public String intake_status;
    public String pill_name;
    public int schedule_id;
    public String time;

    public Log (String date, String time, String pill_name, String intake_status) {
        this.date = date;
        this.time = time;
        this.pill_name = pill_name;
        this.intake_status = intake_status;
    }

    // Getters and Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIntake_status() {
        return intake_status;
    }

    public void setIntake_status(String intake_status) {
        this.intake_status = intake_status;
    }

    public String getPill_name() {
        return pill_name;
    }

    public void setPill_name(String pill_name) {
        this.pill_name = pill_name;
    }

    public int getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(int schedule_id) {
        this.schedule_id = schedule_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
