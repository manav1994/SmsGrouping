package com.manav.smsgrouping;

public class DateItem extends Lister {

  private String date;

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  @Override
  public int getType()
  {
    return 1;
  }
}