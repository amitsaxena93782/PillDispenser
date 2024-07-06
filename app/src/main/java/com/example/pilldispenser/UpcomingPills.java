package com.example.pilldispenser;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UpcomingPills {

    @SerializedName("status")
    private String status;

    @SerializedName("upcoming_pills")
    private List<Pill> upcomingPills;

    // Getters and setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Pill> getUpcomingPills() {
        return upcomingPills;
    }

    public void setUpcomingPills(List<Pill> upcomingPills) {
        this.upcomingPills = upcomingPills;
    }
}
