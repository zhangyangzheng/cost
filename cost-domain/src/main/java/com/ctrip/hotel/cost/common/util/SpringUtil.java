package com.ctrip.hotel.cost.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringUtil implements ApplicationContextAware {

    static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static void staticSetContext(ApplicationContext applicationContext){
        context = applicationContext;
    }

    public static <T> T getBeanByClass(Class<T> cls){
        return context.getBean(cls);
    }
}

