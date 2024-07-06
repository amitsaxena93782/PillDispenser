package com.example.pilldispenser;

import com.google.gson.annotations.SerializedName;

public class Pill {

    @SerializedName("pill_name")
    private String pillName;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("schedule_id")
    private int scheduleId;

    @SerializedName("tank")
    private String tank;

    @SerializedName("time")
    private String time;

    // Getters and setters
    public String getPillName() {
        return pillName;
    }

    public void setPillName(String pillName) {
        this.pillName = pillName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getTank() {
        return tank;
    }

    public void setTank(String tank) {
        this.tank = tank;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
