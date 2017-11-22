package com.youmu.win.m2repo.utils;

import com.youmu.win.m2repo.constant.Constants;
import com.youmu.win.m2repo.model.IndexQueryModel;

import java.net.URISyntaxException;
import java.util.Map;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/09
 */
public class NetUtils {

    public static String queryUrl() {
        return new StringBuilder(Constants.baseUrl).append("/search?").toString();
    }

	public static String querySubUrl(String subUrl) {
		return new StringBuilder(Constants.baseUrl).append(subUrl).toString();
	}
}
