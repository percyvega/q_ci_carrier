package com.percyvega.model;

import java.util.Arrays;

/**
 * Created by pevega on 2/20/2015.
 */
public enum Carrier {
    ATT("ATT"),
    SPR("SPR"),
    TMO("TMO"),
    SMO("SMO"),
    VZW("VZW");

    private String name;

    Carrier(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Carrier getByName(String name) {
        for (Carrier status : Arrays.asList(Carrier.values())) {
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
