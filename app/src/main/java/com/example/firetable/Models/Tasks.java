package com.example.firetable.Models;

import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.Map;

public class Tasks {
    @Exclude
    public String key;

    public String taskName;
    public Date startDate;
    public Date dueDate;
    public Map<String, String> assignedTo;
    public String progressStatus;

    public Tasks(){}

    public Tasks(String key, String taskName, Date startDate, Date dueDate, Map<String, String> assignTo, String progressStatus){
        this.key = key;
        this.taskName = taskName;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.assignedTo = assignTo;
        this.progressStatus = progressStatus;
    }
}
