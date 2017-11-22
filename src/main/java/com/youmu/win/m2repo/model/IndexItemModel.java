package com.youmu.win.m2repo.model;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/09
 */
public class IndexItemModel {
    private String name;
    private String subUrl;
    private String usage;

    public IndexItemModel() {
    }

    public IndexItemModel(String name, String subUrl, String usage) {
        this.name = name;
        this.subUrl = subUrl;
        this.usage = usage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubUrl() {
        return subUrl;
    }

    public void setSubUrl(String subUrl) {
        this.subUrl = subUrl;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    @Override
    public String toString() {
        return "IndexItemModel{" +
                "name='" + name + '\'' +
                ", subUrl='" + subUrl + '\'' +
                ", usage='" + usage + '\'' +
                '}';
    }


}
