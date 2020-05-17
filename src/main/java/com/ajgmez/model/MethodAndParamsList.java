package com.ajgmez.model;

import java.util.ArrayList;
import java.util.List;

import com.ajgmez.enums.OperationEnum;

/**
 * Created by gumz11 on 6/23/17.
 */
public class MethodAndParamsList {

    private List<MethodAndParams> list;

    public MethodAndParamsList() {
        this.list = new ArrayList<>();
    }

    public MethodAndParams get(String type) {
        for (MethodAndParams m : list) {
            if (m.getType().equals(type)) {
                return m;
            }
        }
        return null;
    }

    public void add(MethodAndParams mp) {
        list.add(mp);
    }

    public Object[] sorted() {
        return list.stream().map((m) -> m.getType()).sorted(OperationEnum.sort).toArray();
    }

}
