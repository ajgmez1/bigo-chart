package com.ajgmez.model;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gumz11 on 6/23/17.
 */
public class MethodAndParams {

    private String type;
    private String name;
    private String desc;
    private Method method;
    private List<Class<?>> paramsList;

    public MethodAndParams() {
        this.paramsList = new ArrayList<>();
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addParam(String p) {
        Class<?> c = null;
        try {
            c = (Class<?>) Class.forName(p).getField("TYPE").get(null);
        } catch (Exception e) {
            try {
                c = Class.forName(p);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        paramsList.add(c);
    }

    public Class<?>[] getParams() {
        return paramsList.stream().toArray(Class<?>[]::new);
    }

    public List<Class<?>> getParamsList() {
        return paramsList;
    }

    public void setMethod(Collection c) {
        try {
            method = c.getObject().getClass().getDeclaredMethod(name, this.getParams());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Method getMethod() {
        return method;
    }

    public void setDescription(String desc) {
        this.desc = desc;
    }

    public String getDescription() {
        return desc;
    }
}
