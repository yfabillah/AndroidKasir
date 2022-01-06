package com.example.firetable.Models;

public class Projects {
    public String name;
    public String key;

    public Projects() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Projects(String name, String key){
        this.name = name;
        this.key = key;
    }
}
