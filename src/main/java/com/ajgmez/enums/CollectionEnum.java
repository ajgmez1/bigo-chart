package com.ajgmez.enums;

import java.util.Comparator;

/**
 * Created by gumz11 on 6/17/17.
 */
public enum CollectionEnum {
    ArrayList,
    LinkedList,
    ArrayDeque,
    PriorityQueue,
    HashSet,
    TreeSet,
    HashMap,
    TreeMap;

    public static final Comparator<? super String> sort = (a, b) -> 
        CollectionEnum.valueOf(a).ordinal() - CollectionEnum.valueOf(b).ordinal();
}
