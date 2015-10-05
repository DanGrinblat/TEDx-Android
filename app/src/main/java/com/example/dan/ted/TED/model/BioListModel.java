package com.example.dan.ted.TED.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BioListModel {

    @SerializedName("bio")
    @Expose
    private String bio;
    @SerializedName("name")
    @Expose
    private String name;

    /**
     *
     * @return
     * The bio
     */
    public String getBio() {
        return bio;
    }

    /**
     *
     * @param bio
     * The bio
     */
    public void setBio(String bio) {
        this.bio = bio;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

}