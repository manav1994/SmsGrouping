package com.manav.smsgrouping;

public class GeneralItem extends Lister{

    private ListItem pojoOfJsonArray;

    public ListItem getPojoOfJsonArray() {
        return pojoOfJsonArray;
    }

    public void setPojoOfJsonArray(ListItem pojoOfJsonArray) {
        this.pojoOfJsonArray = pojoOfJsonArray;
    }

    @Override
    public int getType() {
      return 0;
    }
  }