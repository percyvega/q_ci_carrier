package com.percyvega.model;

import java.util.Arrays;

/**
 * Created by pevega on 2/20/2015.
 */
public enum Status {
    QUEUED("QUEUED"),
    PICKED_UP("PICKED_UP"),
    PROCESSING("PROCESSING"),
    PROCESSED("PROCESSED"),
    ERROR("ERROR");

    private String name;

    Status(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Status getByName(String name) {
        for (Status status : Arrays.asList(Status.values())) {
            if (status.getName().equalsIgnoreCase(name))
                return status;
        }

        return null;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
