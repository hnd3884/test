package com.me.mdm.api;

import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.iam.security.SecurityUtil;
import java.util.Map;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONException;
import java.util.Enumeration;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import java.io.InputStream;
import com.me.mdm.files.FileFacade;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.UUID;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import java.util.HashMap;

public class APIRequest
{
    public HashMap parameterList;
    public RequestMapper.Entity.Request request;
    public String urlStartKey;
    public final byte[] requestBody;
    public final String pathInfo;
    public final String servletPath;
    public final String method;
    public final JSONObject headers;
    public final HttpServletResponse httpServletResponse;
    public final Boolean caseInsensitiveRequest;
    public final HttpServletRequest httpServletRequest;
    private static final Logger LOGGER;
    public static final String MSG_HEADER = "msg_header";
    public static final String MSG_BODY = "msg_body";
    public static final String RESOURCE_IDENTIFIER = "resource_identifier";
    public static final String FILTERS = "filters";
    public static final String CUSTOMER_ID = "customer_id";
    public static final String LOGIN_ID = "login_id";
    public static final String USER_NAME = "user_name";
    public static final String USER_ID = "user_id";
    public static final String REQUEST_URL = "request_url";
    public final String tempFilePath;
    
    APIRequest(final HttpServletRequest request, final HttpServletResponse response, final RequestMapper.Entity.Request requestMapper) throws Exception {
        this.parameterList = null;
        this.request = null;
        this.urlStartKey = null;
        byte[] byteBuf = null;
        final String endpointInputContentType = requestMapper.getInputContentType();
        if (endpointInputContentType != null && endpointInputContentType.equalsIgnoreCase("file")) {
            final UUID randomid = UUID.randomUUID();
            final String serverHome = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
            final String folderPath = serverHome + File.separator + "api_temp_downloads" + File.separator + randomid;
            ApiFactoryProvider.getFileAccessAPI().createDirectory(folderPath);
            final String completedFileName = folderPath + File.separator + "temp.file";
            FileFacade.getInstance().writeFile(completedFileName, (InputStream)request.getInputStream());
            this.tempFilePath = completedFileName;
        }
        else {
            this.tempFilePath = null;
            final String input = request.getParameter("zoho-inputstream");
            if (input != null) {
                byteBuf = input.getBytes();
            }
            else if (!request.getInputStream().isFinished()) {
                try {
                    byteBuf = IOUtils.toByteArray((InputStream)request.getInputStream());
                }
                catch (final IOException ex) {
                    throw ex;
                }
            }
        }
        this.requestBody = byteBuf;
        this.pathInfo = request.getPathInfo();
        this.servletPath = request.getServletPath();
        this.method = request.getMethod();
        this.headers = this.storeHeaders(request);
        this.httpServletRequest = request;
        this.httpServletResponse = response;
        this.caseInsensitiveRequest = Boolean.valueOf(requestMapper.getCaseInsensitiveRequest());
        this.request = requestMapper;
    }
    
    public HashMap getParameterList() {
        return this.parameterList;
    }
    
    public void setParameterList(final HashMap parameterList) {
        this.parameterList = parameterList;
    }
    
    public Object getRequestBody() {
        return this.requestBody;
    }
    
    private JSONObject storeHeaders(final HttpServletRequest request) throws JSONException {
        final JSONObject headerValueMap = new JSONObject();
        final Enumeration enume = request.getHeaderNames();
        while (enume.hasMoreElements()) {
            final String headerName = enume.nextElement();
            headerValueMap.put(headerName.toLowerCase(), (Object)request.getHeader(headerName));
        }
        return headerValueMap;
    }
    
    public final String getHeader(final String headerName) throws JSONException {
        return String.valueOf(this.headers.get(headerName));
    }
    
    public final void setContentType(final String contentType) {
        this.httpServletResponse.setContentType(contentType);
    }
    
    public final void setCharacterEncoding(final String characterEncoding) {
        this.httpServletResponse.setCharacterEncoding(characterEncoding);
    }
    
    public JSONObject toJSONObject() {
        final JSONObject requestJSON = new JSONObject();
        try {
            final JSONObject idJSON = new JSONObject();
            final JSONObject headerJSON = new JSONObject();
            final String[] paths = this.getPaths(this.pathInfo);
            final ArrayList<String> pathList = new ArrayList<String>(Arrays.asList(paths));
            pathList.remove(0);
            if (paths != null) {
                Iterator<String> finalPathIterator = null;
                if (this.urlStartKey != null) {
                    for (final String key : new ArrayList<String>(pathList)) {
                        if (key.equals(this.urlStartKey)) {
                            break;
                        }
                        pathList.remove(key);
                    }
                }
                finalPathIterator = pathList.iterator();
                while (finalPathIterator.hasNext()) {
                    final String key2 = finalPathIterator.next();
                    if (finalPathIterator.hasNext()) {
                        final String value = finalPathIterator.next();
                        if (value.equalsIgnoreCase("zuid") || value.equalsIgnoreCase("udid")) {
                            final String v = finalPathIterator.next();
                            idJSON.put(value, (Object)v);
                        }
                        else {
                            idJSON.put(key2.substring(0, key2.length() - 1) + "_id", (Object)value);
                        }
                    }
                }
            }
            headerJSON.put("resource_identifier", (Object)idJSON);
            headerJSON.put("filters", (Object)JSONUtil.mapToJSON(this.parameterList));
            String param = "";
            if (this.httpServletRequest.getQueryString() != null) {
                param = "?" + this.httpServletRequest.getQueryString();
            }
            StringBuffer requestUrl = new StringBuffer(SecurityUtil.getRequestPath(this.httpServletRequest));
            if (requestUrl.toString().equalsIgnoreCase(this.httpServletRequest.getRequestURI())) {
                requestUrl = this.httpServletRequest.getRequestURL();
            }
            headerJSON.put("request_url", (Object)requestUrl.append(param));
            requestJSON.put("msg_header", (Object)headerJSON);
            if (this.caseInsensitiveRequest != null && this.caseInsensitiveRequest && this.requestBody != null && this.requestBody.length > 0) {
                requestJSON.put("msg_body", (Object)new APIUtil().wrapUserJSONToCaseInsensitiveServerJSON(new JSONObject(new String(this.requestBody, "UTF-8"))));
            }
            else if (this.requestBody != null && this.requestBody.length > 0) {
                requestJSON.put("msg_body", (Object)new APIUtil().wrapUserJSONToServerJSON(new JSONObject(new String(this.requestBody, "UTF-8"))));
            }
        }
        catch (final Exception ex) {
            APIRequest.LOGGER.log(Level.SEVERE, "Exception while converting api request to json object", ex);
        }
        return requestJSON;
    }
    
    private String[] getPaths(final String pathInfo) {
        String[] paths = null;
        if (pathInfo != null) {
            paths = pathInfo.split("/");
        }
        return paths;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMAPILogger");
    }
}
