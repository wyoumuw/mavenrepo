package com.youmu;

import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.framework.ProxyFactory;

import java.lang.reflect.Method;

public class SimpleBeforeAdvice implements MethodBeforeAdvice {
	public static void main(String[] args) {
        MyService target = new MyService();
  
        ProxyFactory pf = new ProxyFactory();
        pf.setTarget(target);
        pf.addAdvice(new SimpleBeforeAdvice());
        MyService proxy = (MyService) pf.getProxy();
   
        proxy.say();
    }  
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("Before method: " + method.getName());  
    }  
}  