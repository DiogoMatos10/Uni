package com.roytuts.spring.boot.security.form.based.jdbc.userdetailsservice.auth.model;

import java.util.Date;

public class Event {

    private Long id;
    private String name;
    private String description;
    private Date date;
    private double value;

    public Event() {
    }

    public Event(String name, String description, Date date, double value) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public double getValue() {
        return value;
    }


}
