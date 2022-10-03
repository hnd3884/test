package com.me.devicemanagement.framework.webclient.api.util;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.logging.Level;
import com.me.devicemanagement.framework.utils.XMLUtils;
import java.io.File;
import java.util.HashMap;
import com.me.devicemanagement.framework.webclient.api.mapper.RequestMapper;
import java.util.Map;
import java.util.logging.Logger;

public class APIRequestMapper
{
    private static final Logger LOGGER;
    private static Map<String, Map<String, Map<String, RequestMapper.Entity.Request>>> requestEntityMap;
    public static String apiSupportedVersion;
    
    public static void initializeRequest() throws Exception {
        APIRequestMapper.requestEntityMap = new HashMap<String, Map<String, Map<String, RequestMapper.Entity.Request>>>();
        final String fileName = getAPIFileName();
        final File file = new File(fileName);
        if (file.exists()) {
            final RequestMapper reqMapper = (RequestMapper)XMLUtils.getJAXBUnmarshalledObjectFromFile(fileName, "com.me.devicemanagement.framework.webclient.api.mapper");
            APIRequestMapper.apiSupportedVersion = reqMapper.getVersion();
            constructEntityMap(reqMapper);
        }
        else {
            APIRequestMapper.LOGGER.log(Level.WARNING, "File not found in API Directory {0} ", file);
        }
        APIRequestMapper.requestEntityMap = Collections.unmodifiableMap((Map<? extends String, ? extends Map<String, Map<String, RequestMapper.Entity.Request>>>)APIRequestMapper.requestEntityMap);
        APIRequestMapper.LOGGER.log(Level.FINE, " RequestURLs Map after constructing from RequestXMLs {0} ", APIRequestMapper.requestEntityMap);
    }
    
    public static void createRequestMapper() {
        try {
            if (APIRequestMapper.requestEntityMap == null || APIRequestMapper.requestEntityMap.size() == 0) {
                initializeRequest();
            }
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static RequestMapper.Entity.Request getRequestForUri(final String module, final String requestURIStr, final String operation) throws Exception {
        try {
            if (APIRequestMapper.requestEntityMap.containsKey(module)) {
                final Map<String, Map<String, RequestMapper.Entity.Request>> requestMap = APIRequestMapper.requestEntityMap.get(module);
                if (requestMap.containsKey(requestURIStr)) {
                    final Map<String, RequestMapper.Entity.Request> requestsMap = requestMap.get(requestURIStr);
                    if (requestsMap.containsKey(operation)) {
                        return requestsMap.get(operation);
                    }
                    if (requestsMap.containsKey(requestURIStr)) {
                        return requestsMap.get(requestURIStr);
                    }
                }
            }
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    
    private static String getFormattedURI(String requestURI) {
        requestURI = getRequestURI(requestURI);
        if (requestURI.contains(".*?")) {
            requestURI = requestURI.substring(3);
        }
        return requestURI;
    }
    
    private static String getRequestURI(final String requestURI) {
        if (requestURI.endsWith("/")) {
            final int length = requestURI.length();
            return requestURI.substring(0, length - 1);
        }
        return requestURI;
    }
    
    private static void constructEntityMap(final RequestMapper requestMapper) {
        final List<RequestMapper.Entity> entities = requestMapper.getEntity();
        for (final RequestMapper.Entity entity : entities) {
            final Map<String, Map<String, RequestMapper.Entity.Request>> requestURLMap = constructRequestMap(entity);
            final String name = entity.getName().toLowerCase();
            APIRequestMapper.requestEntityMap.put(name, requestURLMap);
        }
    }
    
    private static Map<String, Map<String, RequestMapper.Entity.Request>> constructRequestMap(final RequestMapper.Entity entity) {
        final Map<String, Map<String, RequestMapper.Entity.Request>> requestURLMap = new HashMap<String, Map<String, RequestMapper.Entity.Request>>();
        final List<RequestMapper.Entity.Request> requests = entity.getRequest();
        for (final RequestMapper.Entity.Request request : requests) {
            final String uri = request.getUri().toLowerCase();
            final Object existingReq = requestURLMap.get(uri);
            Map<String, RequestMapper.Entity.Request> existReq = new HashMap<String, RequestMapper.Entity.Request>();
            if (existingReq != null) {
                existReq = (Map)existingReq;
            }
            String operationName = request.getOperationName();
            if (operationName == null) {
                operationName = uri;
            }
            existReq.put(operationName.toLowerCase(), request);
            requestURLMap.put(uri, existReq);
        }
        return requestURLMap;
    }
    
    public static String getAPIFileName() {
        return ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "conf" + File.separator + "APIRequestMapper.xml";
    }
    
    static {
        LOGGER = Logger.getLogger(APIRequestMapper.class.getName());
        APIRequestMapper.requestEntityMap = null;
    }
}
