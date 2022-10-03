package com.me.devicemanagement.framework.server.customreport;

import java.util.LinkedHashMap;
import java.io.Serializable;

public class CRCriteria implements Serializable
{
    public Long columnID;
    public String operatorValue;
    public String searchValue;
    public String logicalOperatorValue;
    public boolean caseSensitive;
    public String columnName;
    public String operatorValue_i18n;
    public String logicalOperatorValue_i18n;
    public String columnDataType;
    public LinkedHashMap<Object, String> browseValues;
}
