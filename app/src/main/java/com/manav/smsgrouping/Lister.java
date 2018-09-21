package com.manav.smsgrouping;

public abstract class Lister {

    public static final int TYPE_DATE = 0;
    public static final int TYPE_GENERAL = 1;

    abstract public int getType();
}
