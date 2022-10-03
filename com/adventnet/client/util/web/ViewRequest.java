package com.adventnet.client.util.web;

import java.util.Collection;
import java.util.Vector;
import java.util.Enumeration;
import org.apache.catalina.util.ParameterMap;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import javax.servlet.http.HttpServletRequestWrapper;

public class ViewRequest extends HttpServletRequestWrapper
{
    private Map overrideingParamMap;
    HttpServletRequest origRequest;
    
    public ViewRequest(final HttpServletRequest request, final Map overrideingParamMap) {
        super(request);
        this.overrideingParamMap = null;
        this.origRequest = null;
        this.origRequest = request;
        this.overrideingParamMap = overrideingParamMap;
    }
    
    public ViewRequest(final HttpServletRequest request, final String params) {
        super(request);
        this.overrideingParamMap = null;
        this.origRequest = null;
        this.origRequest = request;
        this.overrideingParamMap = this.splitParams(params);
    }
    
    private Map<String, List<String>> splitParams(final String url) {
        try {
            final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
            final String[] pairs = url.split("&");
            int idx = 0;
            String key = null;
            String value = null;
            for (final String pair : pairs) {
                idx = pair.indexOf("=");
                key = ((idx > 0) ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair);
                if (!query_pairs.containsKey(key)) {
                    query_pairs.put(key, new LinkedList<String>());
                }
                value = ((idx > 0 && pair.length() > idx + 1) ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null);
                query_pairs.get(key).add(value);
            }
            return query_pairs;
        }
        catch (final UnsupportedEncodingException uee) {
            throw new RuntimeException(uee);
        }
    }
    
    public String getParameter(final String name) {
        final String[] arr = this.getParameterValues(name);
        if (arr != null) {
            return arr[0];
        }
        return null;
    }
    
    public Map getParameterMap() {
        final ParameterMap paramMap = new ParameterMap();
        paramMap.putAll(super.getParameterMap());
        paramMap.putAll(this.overrideingParamMap);
        paramMap.setLocked(true);
        return (Map)paramMap;
    }
    
    public Enumeration<String> getParameterNames() {
        final Map paramMap = this.getParameterMap();
        final Vector<String> paramVector = new Vector<String>(paramMap.keySet());
        return paramVector.elements();
    }
    
    public String[] getParameterValues(final String name) {
        final List<String> paramsValues = this.overrideingParamMap.get(name);
        if (paramsValues == null || paramsValues.size() < 1) {
            return super.getParameterValues(name);
        }
        return paramsValues.toArray(new String[paramsValues.size()]);
    }
    
    public HttpServletRequest getOriginalRequest() {
        return this.origRequest;
    }
}
