package com.example.dan.ted.TED.model;

/**
 * Created by Dan on 8/29/2015.
 */
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class listModel {

    @SerializedName("img_list")
    @Expose
    private List<String> imgList = new ArrayList<String>();

    /**
     *
     * @return
     * The imgList
     */
    public List<String> getImgList() {
        return imgList;
    }

    /**
     *
     * @param imgList
     * The img_list
     */
    public void setImgList(List<String> imgList) {
        this.imgList = imgList;
    }

}