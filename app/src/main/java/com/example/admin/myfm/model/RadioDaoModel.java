package com.example.admin.myfm.model;

/**
 * Created by Admin on 2017/7/20.
 */

public class RadioDaoModel {
    private int radioId;
    private String name;
    private String programName;
    private String coverSmall;
    private String ts24Url;
    private String ts64Url;
    private String aac24Url;
    private String aac64Url;
    private int collect;

    public int getCollect() {
        return collect;
    }

    public void setCollect(int collect) {
        this.collect = collect;
    }

    public int getRadioId() {
        return radioId;
    }

    public void setRadioId(int radioId) {
        this.radioId = radioId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getCoverSmall() {
        return coverSmall;
    }

    public void setCoverSmall(String coverSmall) {
        this.coverSmall = coverSmall;
    }

    public String getTs24Url() {
        return ts24Url;
    }

    public void setTs24Url(String ts24Url) {
        this.ts24Url = ts24Url;
    }

    public String getTs64Url() {
        return ts64Url;
    }

    public void setTs64Url(String ts64Url) {
        this.ts64Url = ts64Url;
    }

    public String getAac24Url() {
        return aac24Url;
    }

    public void setAac24Url(String aac24Url) {
        this.aac24Url = aac24Url;
    }

    public String getAac64Url() {
        return aac64Url;
    }

    public void setAac64Url(String aac64Url) {
        this.aac64Url = aac64Url;
    }
}
