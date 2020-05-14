package com.ajgmez.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gumz11 on 6/23/17.
 */
public class Collection {

    private String name;
    private Object instance;
    private Map<String, MethodAndParams> methods;

    public Collection(String name, List<MethodAndParams> methods) throws Exception {
        this.methods = new HashMap<>();
        this.name = name;
        this.instance = this.newInstance();

        for (MethodAndParams m : methods) {
            m.setMethod(this.instance.getClass().getDeclaredMethod(m.getName(), m.getParams()));
            this.methods.put(m.getType(), m);
        }
    }

    private Object newInstance() throws Exception {
        Class<?> cls = Class.forName("java.util." + name);
        Constructor<?> constructor = cls.getConstructor();
        return constructor.newInstance();
    }

    public Object getObject() throws Exception {
        return this.newInstance();
    }

    public MethodAndParams getMethodAndParams(String m) {
        return methods.get(m);
    }
}
