package com.youmu.win.m2repo.utils;

import org.aopalliance.aop.Advice;
import org.springframework.aop.framework.ProxyFactory;

/**
 * @Author: YOUMU
 * @Description:
 * @Date: 2017/11/10
 */
public class AopUtils {

    public static <T> T getProxy(T target, Advice[] advices) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(target);
        if (null != advices && advices.length > 0) {
            for (int i = 0; i < advices.length; i++) {
                Advice advice = advices[i];
                proxyFactory.addAdvice(advice);
            }
        }
        return (T) proxyFactory.getProxy();
    }
}
