package com.adventnet.client.util;

import java.util.Collections;
import java.util.Arrays;
import java.util.List;

public class StaticLists
{
    public static final List<String> CUSTOMVIEW;
    public static final List<String> VIEWCONFIGURATIONPERS;
    public static final List<String> UICOMPONENTPERS;
    public static final List<String> COLUMNCONFIGURATIONPERS;
    public static final List<String> PERSVIEWMAP;
    public static final List<String> ACUSER;
    public static final List<String> ACRELCRITERIA;
    public static final List<String> FILTER;
    
    static {
        CUSTOMVIEW = Collections.unmodifiableList((List<? extends String>)Arrays.asList("CustomViewConfiguration", "SelectQuery"));
        VIEWCONFIGURATIONPERS = Collections.unmodifiableList((List<? extends String>)Arrays.asList("ViewConfiguration"));
        UICOMPONENTPERS = Collections.unmodifiableList((List<? extends String>)Arrays.asList("UIComponent"));
        COLUMNCONFIGURATIONPERS = Collections.unmodifiableList((List<? extends String>)Arrays.asList("ColumnConfiguration"));
        PERSVIEWMAP = Collections.unmodifiableList((List<? extends String>)Arrays.asList("PersonalizedViewMap"));
        ACUSER = Collections.unmodifiableList((List<? extends String>)Arrays.asList("ACUserClientState", "ACUserPreference"));
        ACRELCRITERIA = Collections.unmodifiableList((List<? extends String>)Arrays.asList("ACCriteria", "ACRelationalCriteria"));
        FILTER = Collections.unmodifiableList((List<? extends String>)Arrays.asList("ACFilter", "ACFilterGroup", "ACFilterList"));
    }
}
