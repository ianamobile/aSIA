package com.iana.sia.model;

/**
 * Created by Saumil on 6/17/2018.
 */

public class FieldInfo {

    String title;
    String value;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "FieldInfo{" +
                "title='" + title + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
