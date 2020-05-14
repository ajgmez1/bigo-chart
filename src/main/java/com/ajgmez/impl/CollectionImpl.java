package com.ajgmez.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ajgmez.enums.CollectionEnum;
import com.ajgmez.enums.OperationEnum;
import com.ajgmez.model.Collection;
import com.ajgmez.model.MethodAndParams;
import com.ajgmez.utils.CollectionUtils;

import org.json.JSONArray;
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

        return j.put("Collections", collections.keySet().stream().sorted(CollectionEnum.sort).toArray());
    }

	public JSONObject getOperations() {
        JSONObject j = new JSONObject();
        
		for (Map.Entry<String, Collection> entry : collections.entrySet()) {
            Collection collection = entry.getValue();
            Map<String, MethodAndParams> methods = collection.getMethods();
            Object[] sortedMethods = methods.keySet().stream().sorted(OperationEnum.sort).toArray();
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
            j.put(entry.getKey(), c);
        }
        
        return j;
	}
}
