package com.ajgmez.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by gumz11 on 6/23/17.
 */
public class Collection {

    private String name;
    private Object instance;
    private MethodAndParamsList methods;

    public Collection() throws Exception {
        this.methods = new MethodAndParamsList();
    }

    private Object newInstance() throws Exception {
        Class<?> cls = Class.forName("java.util." + name);
        Constructor<?> constructor = cls.getConstructor();
        return constructor.newInstance();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Object getInstance() {
        return instance;
    }

    public Object getObject() throws Exception {
        return this.newInstance();
    }

    public MethodAndParamsList getMethods() {
        return methods;
    }

    public Method getMethod(String m) {
        return methods.get(m).getMethod();
    }
}
