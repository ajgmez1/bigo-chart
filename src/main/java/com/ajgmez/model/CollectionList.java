package com.ajgmez.model;

import java.util.ArrayList;
import java.util.List;

import com.ajgmez.enums.CollectionEnum;

public class CollectionList {
    private List<Collection> list;
    
    public CollectionList() {
        this.list = new ArrayList<>();
    }

    public Collection get(String n) {
        for (Collection c : list) {
            if (c.getName().equals(n)) {
                return c;
            }
        }
        return null;
    }

    public List<Collection> getList() {
        return list;
    }

    public void add(Collection c) {
        list.add(c);
    }

    public Object[] sorted() {
        return list.stream().map((c) -> c.getName()).sorted(CollectionEnum.sort).toArray();
    }
}
