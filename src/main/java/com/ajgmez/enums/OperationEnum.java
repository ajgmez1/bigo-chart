package com.ajgmez.enums;

import java.util.Comparator;

/**
 * Created by gumz11 on 6/17/17.
 */
public enum OperationEnum {
    Access,
    Search,
    Insertion,
    Removal;

    public static final Comparator<? super String> sort = (a, b) -> 
        OperationEnum.valueOf(a).ordinal() - OperationEnum.valueOf(b).ordinal();
}
