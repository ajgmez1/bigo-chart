package com.ajgmez.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.ajgmez.enums.Operation;
import com.ajgmez.model.Collection;
import com.ajgmez.utils.CollectionUtils;

/**
 * Created by gumz11 on 6/23/17.
 */
public class PerformanceTestImpl {
    private Map<String, Collection> collections;

    public PerformanceTestImpl() throws Exception {
        this.collections = CollectionUtils.getCollections();
    }

    public Map<Integer, Long> test(String collection, String operation, int inputSize) throws Exception {
        Map<Integer, Long> results = new HashMap<>();

        Collection c = collections.get(collection);
        Object o = c.getObject();
        Method method = c.getMethodAndParams(operation).getMethod();
        Method insert = c.getMethodAndParams(Operation.Insertion.name()).getMethod();
        
        int step = Math.round(inputSize / 100);
        
        for (int i = 1; i <= inputSize; i++) {
            // populate collection before testing
            if (!operation.equals(Operation.Insertion.name())) {
                this.run(o, insert, i);
            }
            // run performance test
            if (i % step == 0) {
                results.put(i, this.run(o, method, i));
            }
        }

        return results;
    }

    private long run(Object collection, Method method, int inputSize) throws InvocationTargetException, IllegalAccessException {
        Random random = new Random();
        int params = method.getParameterCount();
        Object []args = new Object[params];

        for (int p = 0; p < params; p++) {
            args[p] = random.nextInt(inputSize);
        }
    
        long start = System.nanoTime();
        method.invoke(collection, args);
        return System.nanoTime() - start;
    }
}
