package com.youmu.win.m2repo.model;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/09
 */
public class VersionItemModel {

    private String name;
    private String subUrl;
    private String usage;
    private String date;
	private String dependency;

    public VersionItemModel() {
    }

	public VersionItemModel(String name, String subUrl, String usage, String date, String dependency) {
		this.name = name;
		this.subUrl = subUrl;
		this.usage = usage;
		this.date = date;
		this.dependency = dependency;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


	public String getDependency() {
		return dependency;
	}

	public void setDependency(String dependency) {
		this.dependency = dependency;
	}

	@Override
	public String toString() {
		return "VersionItemModel{" +
				"name='" + name + '\'' +
				", subUrl='" + subUrl + '\'' +
				", usage='" + usage + '\'' +
				", date='" + date + '\'' +
				", dependency='" + dependency + '\'' +
				'}';
	}
}
