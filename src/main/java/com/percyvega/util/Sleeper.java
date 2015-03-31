package com.percyvega.util;

/**
 * Created by pevega on 2/25/2015.
 */
public abstract class Sleeper {

    public static void sleep(int mils) {
        try {
            Thread.sleep(mils);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}