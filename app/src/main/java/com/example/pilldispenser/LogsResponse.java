package com.example.pilldispenser;

import java.util.List;

public class LogsResponse {
    private List<Log> logs;
    private String status;

    public List<Log> getLogs() {
        return logs;
    }

    public void setLogs(List<Log> logs) {
        this.logs = logs;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
