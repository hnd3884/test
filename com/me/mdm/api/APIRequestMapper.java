package com.me.mdm.api;

import java.util.Hashtable;
import org.json.JSONException;
import java.util.List;
import com.me.mdm.api.error.APIHTTPException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.Collections;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.stream.XMLInputFactory;
import javax.xml.bind.JAXBContext;
import java.io.Reader;
import java.io.FileReader;
import java.util.Properties;
import java.util.Collection;
import java.util.Arrays;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class APIRequestMapper
{
    protected static final Logger LOGGER;
    protected static Map<String, Map<String, RequestMapper.Entity.Request>> requestEntityMap;
    public static String apiSupportedVersion;
    
    private static void initializeRequest() throws Exception {
        APIRequestMapper.requestEntityMap = new HashMap<String, Map<String, RequestMapper.Entity.Request>>();
        final String conf = System.getProperty("server.home") + File.separator + "conf";
        final ArrayList<File> files = new ArrayList<File>();
        final FilenameFilter directoryFilter = new FilenameFilter() {
            @Override
            public boolean accept(final File current, final String name) {
                return new File(current, name).isDirectory();
            }
        };
        final FilenameFilter routesFilter = new FilenameFilter() {
            @Override
            public boolean accept(final File current, final String name) {
                return name.equals("api-routes.properties") || name.equals("api-routes-cloud.properties") || name.equals("api-routes-msp.properties") || name.equals("api-routes-dc.properties");
            }
        };
        final File[] listFiles;
        final File[] modules = listFiles = new File(conf).listFiles(directoryFilter);
        for (final File module : listFiles) {
            files.addAll(Arrays.asList(module.listFiles(routesFilter)));
        }
        final Properties props = new Properties();
        for (final File file : files) {
            FileReader fr = null;
            final Properties moduleprops = new Properties();
            try {
                fr = new FileReader(file);
                moduleprops.load(fr);
            }
            finally {
                if (fr != null) {
                    fr.close();
                }
            }
            for (final Map.Entry<Object, Object> entry : moduleprops.entrySet()) {
                final String fileName = file.getParent() + File.separator + entry.getValue().toString().replace("/", File.separator);
                ((Hashtable<Object, String>)props).put(entry.getKey(), fileName);
            }
        }
        final Iterator<Map.Entry<Object, Object>> entries2 = props.entrySet().iterator();
        RequestMapper reqMapper = null;
        final HashMap<String, RequestMapper.Entity> entityMap = new HashMap<String, RequestMapper.Entity>();
        while (entries2.hasNext()) {
            final File requestMapperFile = new File(String.valueOf(entries2.next().getValue()));
            if (requestMapperFile.isFile()) {
                final JAXBContext context = JAXBContext.newInstance("com.me.mdm.api");
                final XMLInputFactory xif = XMLInputFactory.newFactory();
                xif.setProperty("javax.xml.stream.supportDTD", false);
                xif.setProperty("javax.xml.stream.isReplacingEntityReferences", false);
                xif.setProperty("javax.xml.stream.isSupportingExternalEntities", false);
                final XMLStreamReader xsr = xif.createXMLStreamReader(new StreamSource(requestMapperFile));
                final Unmarshaller un = context.createUnmarshaller();
                final RequestMapper tempMapper = (RequestMapper)un.unmarshal(xsr);
                if (reqMapper == null) {
                    reqMapper = tempMapper;
                    for (final RequestMapper.Entity entity : tempMapper.getEntity()) {
                        entityMap.put(entity.getName(), entity);
                    }
                }
                else {
                    for (final RequestMapper.Entity entity : tempMapper.getEntity()) {
                        if (entityMap.containsKey(entity.getName())) {
                            final RequestMapper.Entity thisEntity = entityMap.get(entity.getName());
                            thisEntity.getRequest().addAll(entity.getRequest());
                            entityMap.put(entity.getName(), thisEntity);
                        }
                        else {
                            entityMap.put(entity.getName(), entity);
                        }
                    }
                }
            }
        }
        reqMapper.getEntity().clear();
        reqMapper.getEntity().addAll(new ArrayList<RequestMapper.Entity>(entityMap.values()));
        APIRequestMapper.apiSupportedVersion = reqMapper.getVersion();
        constructEntityMap(reqMapper);
        APIRequestMapper.requestEntityMap = Collections.unmodifiableMap((Map<? extends String, ? extends Map<String, RequestMapper.Entity.Request>>)APIRequestMapper.requestEntityMap);
        APIRequestMapper.LOGGER.log(Level.FINE, " RequestURLs Map after constructing from RequestXMLs {0} ", APIRequestMapper.requestEntityMap);
    }
    
    public static RequestMapper.Entity.Request getRequestForUri(final String module, final String requestURIStr, final String method) throws Exception {
        try {
            if (APIRequestMapper.requestEntityMap.containsKey(module)) {
                final Map<String, RequestMapper.Entity.Request> requestMap = APIRequestMapper.requestEntityMap.get(module);
                if (containsByRegex(requestMap, requestURIStr)) {
                    final RequestMapper.Entity.Request request = (RequestMapper.Entity.Request)getByRegex(requestMap, requestURIStr);
                    if (request != null) {
                        return request;
                    }
                    throw new APIHTTPException("COM0001", new Object[0]);
                }
            }
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new RuntimeException(e2);
        }
        return null;
    }
    
    protected static void constructEntityMap(final RequestMapper requestMapper) {
        final List<RequestMapper.Entity> entities = requestMapper.getEntity();
        for (final RequestMapper.Entity entity : entities) {
            final Map<String, RequestMapper.Entity.Request> requestURLMap = constructRequestMap(entity);
            final String name = entity.getName().toLowerCase();
            APIRequestMapper.requestEntityMap.put(name, requestURLMap);
        }
    }
    
    protected static Map<String, RequestMapper.Entity.Request> constructRequestMap(final RequestMapper.Entity entity) {
        final Map<String, RequestMapper.Entity.Request> requestURLMap = new HashMap<String, RequestMapper.Entity.Request>();
        final List<RequestMapper.Entity.Request> requests = entity.getRequest();
        for (final RequestMapper.Entity.Request request : requests) {
            final String uri = request.getUri();
            requestURLMap.put(uri, request);
        }
        return requestURLMap;
    }
    
    public static void createRequestMapper() {
        try {
            if (APIRequestMapper.requestEntityMap == null || APIRequestMapper.requestEntityMap.isEmpty()) {
                initializeRequest();
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    private static boolean containsByRegex(final Map json, final String key) {
        final Iterator keyIter = json.keySet().iterator();
        while (keyIter.hasNext()) {
            if (key.matches(keyIter.next())) {
                return true;
            }
        }
        return false;
    }
    
    private static Object getByRegex(final Map json, final String key) throws JSONException {
        for (final String keyRegex : json.keySet()) {
            if (key.matches(keyRegex)) {
                return json.get(keyRegex);
            }
        }
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger(APIRequestMapper.class.getName());
        APIRequestMapper.requestEntityMap = null;
    }
}
