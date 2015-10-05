package com.example.dan.ted.TED.model;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bio {

    @SerializedName("file_list")
    @Expose
    private List<BioListModel> bioList = new ArrayList<BioListModel>();

    /**
     *
     * @return
     * The fileList
     */
    public List<BioListModel> getFileList() {
        return bioList;
    }

    /**
     *
     * @param bioList
     * The bio_list
     */
    public void setFileList(List<BioListModel> bioList) {
        this.bioList = bioList;
    }

}