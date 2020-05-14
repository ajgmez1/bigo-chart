package com.ajgmez.model;

import java.lang.reflect.Method;

/**
 * Created by gumz11 on 6/23/17.
 */
public class MethodAndParams {

    private String type;
    private String name;
    private Class<?>[] params;
    private Method method;

    public MethodAndParams(String type, String name, Class<?>[] params) {
        this.type = type;
        this.name = name;
        this.params = params;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Class<?>[] getParams() {
        return params;
    }

    public void setMethod(Method m) {
        method = m;
    }

    public Method getMethod() {
        return method;
    }
}
