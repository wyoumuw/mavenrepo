package com.youmu.win.m2repo.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/09
 */
public class IndexQueryModel {

    private String query;
    private int page;

    public IndexQueryModel() {
    }

    public IndexQueryModel(String query, int page) {
        this.query = query;
        this.page = page;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public Map<String, String> getMap() {
        Map<String, String> map = Maps.newHashMap();
        if (StringUtils.isBlank(query)) {
            query = "";
        }
        map.put("q", query);
        if (page >= 0) {
            map.put("p", String.valueOf(page));
        }
        return map;
    }
}
