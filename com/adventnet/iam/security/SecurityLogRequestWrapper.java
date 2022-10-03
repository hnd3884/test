package com.adventnet.iam.security;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.List;
import javax.servlet.http.HttpServletRequestWrapper;

public class SecurityLogRequestWrapper extends HttpServletRequestWrapper
{
    boolean maskAllParamValues;
    private List<String> secretParamNames;
    private List<String> piiParamNames;
    private Map<String, List<String>> secretParamValuesMap;
    private List<String> secretHeaderNames;
    private Map<String, List<String>> secretHeaderValuesMap;
    private List<String> extraParamList;
    private List<String> ignoredExtraParamList;
    
    public SecurityLogRequestWrapper(final HttpServletRequest request) {
        super(request);
        this.maskAllParamValues = false;
        this.secretParamNames = new ArrayList<String>();
        this.piiParamNames = new ArrayList<String>();
        this.secretHeaderNames = new ArrayList<String>();
        this.extraParamList = new ArrayList<String>();
    }
    
    public String getParameter(final String paramName) {
        final String[] paramValues = this.getParameterValues(paramName);
        if (paramValues != null) {
            return paramValues[0];
        }
        return null;
    }
    
    public Enumeration<String> getParameterNames() {
        final SecurityRequestWrapper securedRequest = (SecurityRequestWrapper)this.getAttribute(SecurityRequestWrapper.class.getName());
        if (securedRequest != null) {
            return securedRequest.getParameterNames();
        }
        return super.getParameterNames();
    }
    
    public String[] getParameterValues(final String paramName) {
        final SecurityRequestWrapper securedRequest = (SecurityRequestWrapper)this.getAttribute(SecurityRequestWrapper.class.getName());
        final String[] values = (securedRequest != null) ? securedRequest.getParameterValuesForLogging(paramName) : super.getParameterValues(paramName);
        return this.getMaskedParameterValues(paramName, values);
    }
    
    private String[] getMaskedParameterValues(final String paramName, final String[] values) {
        if (values != null) {
            if (this.maskAllParamValues || this.secretParamNames.contains(paramName) || this.extraParamList.contains(paramName) || this.isIgnoredExtraParameter(paramName)) {
                return this.constructMultipleMaskedValues(values);
            }
            if (this.secretParamValuesMap != null && this.secretParamValuesMap.containsKey(paramName)) {
                return (String[])this.secretParamValuesMap.get(paramName).toArray(new String[0]);
            }
            if (!this.maskAllParamValues) {
                this.maskUsingPiiDetector(values, paramName);
            }
        }
        return values;
    }
    
    public void maskUsingPiiDetector(final String[] values, final String paramName) {
        if (SecurityFilterProperties.getPiiDetector() != null && !this.secretParamNames.contains(paramName) && this.piiParamNames.contains(paramName)) {
            for (int i = 0; i < values.length; ++i) {
                values[i] = SecurityFilterProperties.getPiiDetector().detect(values[i]).toString();
            }
        }
    }
    
    public Map<String, String[]> getParameterMap() {
        final Enumeration<String> paramNames = this.getParameterNames();
        if (paramNames == null) {
            return new HashMap<String, String[]>();
        }
        final Map<String, String[]> map = new HashMap<String, String[]>(super.getParameterMap().size());
        final SecurityRequestWrapper securedRequest = (SecurityRequestWrapper)this.getAttribute(SecurityRequestWrapper.class.getName());
        final boolean isSecuredRequest = securedRequest != null;
        while (paramNames.hasMoreElements()) {
            final String paramName = paramNames.nextElement();
            final String[] paramValues = isSecuredRequest ? securedRequest.getParameterValuesForLogging(paramName) : super.getParameterValues(paramName);
            map.put(paramName, this.getMaskedParameterValues(paramName, paramValues));
        }
        return map;
    }
    
    public String getHeader(final String headerName) {
        if (this.secretHeaderNames.contains(headerName)) {
            return "*****";
        }
        if (this.secretHeaderValuesMap != null && this.secretHeaderValuesMap.containsKey(headerName)) {
            return this.secretHeaderValuesMap.get(headerName).get(0);
        }
        return super.getHeader(headerName);
    }
    
    public Enumeration<String> getHeaders(final String headerName) {
        if (this.secretHeaderNames.contains(headerName)) {
            final List<String> headerValues = new ArrayList<String>();
            headerValues.add("*****");
            return Collections.enumeration(headerValues);
        }
        if (this.secretHeaderValuesMap != null && this.secretHeaderValuesMap.containsKey(headerName)) {
            return Collections.enumeration(this.secretHeaderValuesMap.get(headerName));
        }
        return super.getHeaders(headerName);
    }
    
    private String[] constructMultipleMaskedValues(final String[] values) {
        final String[] maskedValues = new String[values.length];
        for (int i = 0; i < values.length; ++i) {
            final String value = "*****";
            maskedValues[i] = value;
        }
        return maskedValues;
    }
    
    void addJSONParamValues(final String requestParam, final List<String> maskedValues) {
        if (this.secretParamValuesMap == null) {
            this.secretParamValuesMap = new HashMap<String, List<String>>();
        }
        this.secretParamValuesMap.put(requestParam, maskedValues);
    }
    
    boolean isHeaderExist(final String headerName) {
        return super.getHeader(headerName) != null;
    }
    
    public void addPiiParams(final String requestParam) {
        this.piiParamNames.add(requestParam);
    }
    
    void addPartiallyMaskedParameter(final String param, final List<String> values) {
        if (this.secretParamValuesMap == null) {
            this.secretParamValuesMap = new HashMap<String, List<String>>();
        }
        this.secretParamValuesMap.put(param, values);
    }
    
    List<String> getPartiallyMaskedParameter(final String param) {
        return this.secretParamValuesMap.get(param);
    }
    
    public boolean isSecretParameter(final String paramName) {
        return this.secretParamNames.contains(paramName) || (this.secretParamValuesMap != null && this.secretParamValuesMap.containsKey(paramName));
    }
    
    void addSecretParameter(final String paramName) {
        this.secretParamNames.add(paramName);
    }
    
    public void addSecretHeader(final String headerName) {
        this.secretHeaderNames.add(headerName);
    }
    
    public void addSecretHeader(final String headerName, final List<String> headerValues) {
        if (this.secretHeaderValuesMap == null) {
            this.secretHeaderValuesMap = new HashMap<String, List<String>>();
        }
        this.secretHeaderValuesMap.put(headerName, headerValues);
    }
    
    void addExtraParameter(final String paramName) {
        this.extraParamList.add(paramName);
    }
    
    boolean isExtraParameter(final String paramName) {
        return this.extraParamList.contains(paramName);
    }
    
    void addIgnoredExtraParameters(final List<String> unvalidatedExtraParams) {
        if (this.ignoredExtraParamList == null) {
            this.ignoredExtraParamList = new ArrayList<String>();
        }
        this.ignoredExtraParamList = unvalidatedExtraParams;
    }
    
    void addIgnoredExtraParameter(final String paramName) {
        if (this.ignoredExtraParamList == null) {
            this.ignoredExtraParamList = new ArrayList<String>();
        }
        this.ignoredExtraParamList.add(paramName);
    }
    
    boolean isIgnoredExtraParameter(final String paramName) {
        return this.ignoredExtraParamList != null && this.ignoredExtraParamList.contains(paramName);
    }
}
