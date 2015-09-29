package com.example.dan.ted.TED.model;

import com.google.gson.annotations.Expose;

public class TimestampModel {

    @Expose
    private String timestamp;

    /**
     *
     * @return
     * The timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     *
     * @param timestamp
     * The timestamp
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}