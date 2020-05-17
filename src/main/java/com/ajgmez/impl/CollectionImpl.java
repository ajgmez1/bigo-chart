package com.ajgmez.impl;

import com.ajgmez.model.Collection;
import com.ajgmez.model.CollectionList;
import com.ajgmez.model.MethodAndParams;
import com.ajgmez.model.MethodAndParamsList;
import com.ajgmez.utils.CollectionUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by gumz11 on 6/23/17.
 */
public class CollectionImpl {
    private CollectionList collections;

    public CollectionImpl() throws Exception {
        this.collections = CollectionUtils.getCollections();
    }

    public JSONObject getCollections() {
        JSONObject j = new JSONObject();

        return j.put("Collections", collections.sorted());
    }

	public JSONObject getOperations() {
        JSONObject j = new JSONObject();
        
		for (Collection entry : collections.getList()) {
            MethodAndParamsList methods = entry.getMethods();
            Object[] sortedMethods = methods.sorted();
            JSONObject c = new JSONObject();
            JSONArray m = new JSONArray();

            for (int i = 0; i < sortedMethods.length; i++) {
                MethodAndParams methodAndParams = methods.get(sortedMethods[i].toString());
                JSONObject mp = new JSONObject();
                mp.put("type", methodAndParams.getType());
                mp.put("name", methodAndParams.getName());
                mp.put("description", methodAndParams.getDescription());

                m.put(mp);
            }
            
            c.put("methods", m);
            j.put(entry.getName(), c);
        }
        
        return j;
	}
}
