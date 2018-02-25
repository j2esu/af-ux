package ru.uxapps.af.base;

public class Signal {

    private static final Signal INSTANCE = new Signal() {};

    public static Signal get() {
        return INSTANCE;
    }

}
