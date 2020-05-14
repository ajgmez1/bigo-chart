package com.ajgmez.model;

import java.lang.reflect.Method;

import org.w3c.dom.NodeList;

/**
 * Created by gumz11 on 6/23/17.
 */
public class MethodAndParams {

    private String type;
    private String name;
    private Class<?>[] params;
    private Method method;

    public MethodAndParams(String type, String name, NodeList params) throws Exception {
        this.type = type;
        this.name = name;
        this.params = new Class<?>[params.getLength()];

        for (int k = 0; k < params.getLength(); k++) {
            try {
                this.params[k] = (Class<?>) Class.forName(params.item(k).getTextContent()).getField("TYPE").get(null);
            } catch (Exception e) {
                this.params[k] = Class.forName(params.item(k).getTextContent());
            }
        }
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

    public String getDescription() {
        return null;
    }
}
