package com.example.dan.ted.TED.model;

/**
 * Created by Dan on 8/29/2015.
 */
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ListModel {

    @SerializedName("file_list")
    @Expose
    private List<String> fileList = new ArrayList<String>();

    /**
     *
     * @return
     * The fileList
     */
    public List<String> getFileList() {
        return fileList;
    }

    /**
     *
     * @param fileList
     * The file_list
     */
    public void setFileList(List<String> fileList) {
        this.fileList = fileList;
    }

}