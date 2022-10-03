package com.adventnet.iam.security;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class ParamInfo<T>
{
    private String name;
    private ParameterRule rule;
    private List<String> valueList;
    private List<T> objectList;
    private List<String> source;
    private Map<String, Object> infoMap;
    
    public ParamInfo(final String paramName, final T valueObj, final ParameterRule parameterRule, final String source) {
        this.valueList = new ArrayList<String>();
        this.objectList = new ArrayList<T>();
        this.source = new ArrayList<String>();
        this.name = paramName;
        this.rule = parameterRule;
        this.valueList.add(valueObj.toString());
        this.objectList.add(valueObj);
        this.source.add(source);
    }
    
    public void addParam(final T valueObj, final String source) {
        this.valueList.add(valueObj.toString());
        this.objectList.add(valueObj);
        this.source.add(source);
    }
    
    void updateValueList(final String value) {
        final int lastIndex = this.valueList.size() - 1;
        this.valueList.set(lastIndex, value);
    }
    
    List<String> getValueList() {
        return this.valueList;
    }
    
    List<T> getObjectList() {
        return this.objectList;
    }
    
    String getValue() {
        return this.valueList.get(0);
    }
    
    Object getObject() {
        return this.objectList.get(0);
    }
    
    public String[] getValueListToArray() {
        return this.valueList.toArray(new String[0]);
    }
    
    public Map<String, Object> toMap() {
        if (this.name.equals("zoho-inputstream")) {
            return null;
        }
        if (this.infoMap == null) {
            (this.infoMap = new HashMap<String, Object>()).put("_c_name", this.name);
            if (this.rule.isSecret()) {
                this.infoMap.put("secret", this.rule.isSecret());
            }
            final HttpServletRequest request = SecurityUtil.getCurrentRequest();
            if (request != null && SecurityFilterProperties.getInstance(request).isDevelopmentMode()) {
                this.infoMap.put("value", this.valueList);
            }
            this.infoMap.put("source", this.source);
            if (this.rule.isExtraParamRule()) {
                this.infoMap.put("isEP", true);
            }
            this.infoMap.toString();
        }
        return this.infoMap;
    }
    
    boolean isSecret() {
        return this.rule.isSecret();
    }
    
    List<String> getSource() {
        return this.source;
    }
    
    String getName() {
        return this.name;
    }
}
