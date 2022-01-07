package com.example.firetable.Models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Projects {
    public String name;
    @Exclude
    public String key;

    public Map<String, Boolean> members = new HashMap<>();

    public Projects() {
        // Default constructor required for calls to DataSnapshot.getValue(Projects.class)
    }

    public Projects(String name, String key, Map<String, Boolean> members){
        this.name = name;
        this.key = key;
        this.members = members;
    }
}
