package com.me.ems.summaryserver.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import org.json.JSONArray;
import java.util.HashMap;
import org.json.JSONException;
import java.util.Map;
import org.json.JSONObject;
import java.util.Properties;
import java.io.FileInputStream;
import org.apache.commons.io.FilenameUtils;
import java.util.logging.Level;
import java.io.DataOutputStream;
import java.util.logging.Logger;

public abstract class APIRedirectHandler
{
    private Logger logger;
    
    public APIRedirectHandler() {
        this.logger = Logger.getLogger("probeActionsLogger");
    }
    
    protected boolean addMultiPartData(final DataOutputStream dataOutStream, final String field, final String value, final String boundary) {
        boolean status = false;
        final String LINE_FEED = "\r\n";
        final String SEPARATOR = "--";
        try {
            dataOutStream.writeBytes(SEPARATOR + boundary + LINE_FEED);
            dataOutStream.writeBytes("content-disposition: form-data; name = \"" + field + "\"" + LINE_FEED);
            dataOutStream.writeBytes("content-type: application/octet-stream" + LINE_FEED + LINE_FEED);
            dataOutStream.writeBytes(value);
            dataOutStream.writeBytes(LINE_FEED + SEPARATOR + boundary + LINE_FEED);
            status = true;
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in addMultiPartData", e);
        }
        return status;
    }
    
    protected boolean addFilePart(final DataOutputStream dataOutStream, final String fileName, final String boundary) {
        this.logger.log(Level.INFO, "Inside writeFile method");
        boolean status = false;
        final byte[] buffer = new byte[1024];
        FileInputStream fileInStream = null;
        final String LINE_FEED = "\r\n";
        final String SEPARATOR = "--";
        try {
            dataOutStream.writeBytes("content-disposition: form-data; name = \"filename\"; filename=\"" + FilenameUtils.getName(fileName) + "\"" + LINE_FEED);
            dataOutStream.writeBytes("content-type: application/octet-stream" + LINE_FEED + LINE_FEED);
            fileInStream = new FileInputStream(fileName);
            while (true) {
                synchronized (buffer) {
                    final int amountRead = fileInStream.read(buffer);
                    if (amountRead == -1) {
                        break;
                    }
                    dataOutStream.write(buffer, 0, amountRead);
                }
            }
            dataOutStream.writeBytes(LINE_FEED + SEPARATOR + boundary + SEPARATOR);
            dataOutStream.flush();
            dataOutStream.close();
            status = true;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in writing file content", e);
            try {
                if (fileInStream != null) {
                    fileInStream.close();
                }
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Exception in finally block", e);
            }
        }
        finally {
            try {
                if (fileInStream != null) {
                    fileInStream.close();
                }
            }
            catch (final Exception e2) {
                this.logger.log(Level.SEVERE, "Exception in finally block", e2);
            }
        }
        return status;
    }
    
    public abstract String doAPICall(final Properties p0, final JSONObject p1) throws Exception;
    
    public abstract String doAPICall(final Properties p0, final JSONObject p1, final JSONObject p2) throws Exception;
    
    public <T> T extractResponse(final String response, T out, final Object nullReplacement) throws Exception {
        if (response == null) {
            return null;
        }
        if (out instanceof String) {
            out = (T)response;
        }
        else if (out instanceof JSONObject) {
            out = (T)new JSONObject(response);
        }
        else if (out instanceof Properties) {
            final JSONObject responseJSON = new JSONObject(response);
            final Map resultMap = this.toMap(responseJSON);
            final Properties resProps = new Properties();
            resProps.putAll(resultMap);
            out = (T)resProps;
        }
        else if (out instanceof Map) {
            final JSONObject responseJSON = new JSONObject(response);
            final Map resultMap = (Map)(out = (T)this.toMap(responseJSON, nullReplacement));
        }
        else if (out instanceof StringBuilder) {
            ((StringBuilder)out).append(response);
        }
        else {
            if (!(out instanceof StringBuffer)) {
                throw new Exception("Unsupported Return type requested");
            }
            ((StringBuffer)out).append(response);
        }
        return out;
    }
    
    public <T> T extractResponse(final String response, final T out) throws Exception {
        return this.extractResponse(response, out, null);
    }
    
    public Map<String, Object> toMap(final JSONObject jsonObject) throws JSONException {
        return this.toMap(jsonObject, null);
    }
    
    public Map<String, Object> toMap(final JSONObject jsonObject, final Object nullReplacement) {
        final Map<String, Object> map = new HashMap<String, Object>();
        try {
            if (jsonObject != null) {
                final Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    final String key = keys.next();
                    Object value = jsonObject.get(key);
                    if (value instanceof JSONArray) {
                        value = this.toList((JSONArray)value);
                    }
                    else if (value instanceof JSONObject) {
                        value = this.toMap((JSONObject)value);
                    }
                    else if (value == JSONObject.NULL) {
                        value = nullReplacement;
                    }
                    map.put(key, value);
                }
            }
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception while converting to Map", (Throwable)ex);
        }
        return map;
    }
    
    private List<Object> toList(final JSONArray array) throws JSONException {
        final List<Object> list = new ArrayList<Object>();
        if (array != null) {
            for (int i = 0; i < array.length(); ++i) {
                Object value = array.get(i);
                if (value instanceof JSONArray) {
                    value = this.toList((JSONArray)value);
                }
                else if (value instanceof JSONObject) {
                    value = this.toMap((JSONObject)value);
                }
                list.add(value);
            }
        }
        return list;
    }
    
    public Map<String, Properties> convertToMapOfProperties(final String data) throws Exception {
        final Map<String, Properties> result = new HashMap<String, Properties>();
        final JSONObject jsonObject = new JSONObject(data);
        final Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            final String key = keys.next();
            Properties props = null;
            if (jsonObject.getJSONObject(key).length() > 0) {
                props = new Properties();
                props = this.extractResponse(String.valueOf(jsonObject.get(key)), props);
            }
            result.put(key, props);
        }
        return result;
    }
}
