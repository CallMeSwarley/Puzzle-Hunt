package com.socialgaming.androidtutorial.Models;

import android.graphics.Bitmap;
//
public class Weather {
    public Location location;
    public CurrentCondition currentCondition = new CurrentCondition();

    public Bitmap iconData;

    public  class CurrentCondition {
        private int weatherId;
        private String condition;
        private String descr;
        private String icon;

        public int getWeatherId() {
            return weatherId;
        }
        public void setWeatherId(int weatherId) {
            this.weatherId = weatherId;
        }
        public String getCondition() {
            return condition;
        }
        public void setCondition(String condition) {
            this.condition = condition;
        }
        public String getDescr() {
            return descr;
        }
        public void setDescr(String descr) {
            this.descr = descr;
        }
        public String getIcon() {
            return icon;
        }
        public void setIcon(String icon) {
            this.icon = icon;
        }
    }
}
