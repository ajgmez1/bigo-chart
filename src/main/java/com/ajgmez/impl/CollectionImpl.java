package com.ajgmez.impl;

import java.util.Map;

import com.ajgmez.model.Collection;
import com.ajgmez.model.MethodAndParams;
import com.ajgmez.utils.CollectionUtils;

import org.json.JSONObject;

/**
 * Created by gumz11 on 6/23/17.
 */
public class CollectionImpl {
    private Map<String, Collection> collections;

    public CollectionImpl() throws Exception {
        this.collections = CollectionUtils.getCollections();
    }

    public JSONObject getCollections() {
        JSONObject j = new JSONObject();
        
        return j.put("Collections", collections.keySet().toArray());
    }

	public JSONObject getOperations() {
        JSONObject j = new JSONObject();
        
		for (Map.Entry<String, Collection> entry : collections.entrySet()) {
            Map<String, MethodAndParams> methods = entry.getValue().getMethods();
            JSONObject m = new JSONObject();
            for (MethodAndParams methodAndParams : methods.values()) {
                JSONObject mp = new JSONObject();
                mp.put("name", methodAndParams.getName());
                mp.put("description", methodAndParams.getDescription());

                m.put(methodAndParams.getType(), mp);
            }
            j.put(entry.getKey(), m);
        }
        
        return j;
	}
}
