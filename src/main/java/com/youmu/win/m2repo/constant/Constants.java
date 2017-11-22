package com.youmu.win.m2repo.constant;

import com.youmu.win.m2repo.ProcessService;
import com.youmu.win.m2repo.ProcessServiceImpl;
import com.youmu.win.m2repo.aop.ProcessServiceAdvice;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/09
 */
public class Constants {
    public static final String baseUrl = "http://mvnrepository.com";

    public static final ProcessServiceAdvice processServiceAdvice = new ProcessServiceAdvice(new ProcessServiceImpl());
}
