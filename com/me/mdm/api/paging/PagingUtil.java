package com.me.mdm.api.paging;

import java.util.Arrays;
import java.util.logging.Level;
import java.net.URL;
import com.me.mdm.api.paging.model.PagingResponse;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import com.me.mdm.api.error.APIHTTPException;
import org.bouncycastle.util.encoders.Base64;
import java.util.List;
import java.util.logging.Logger;

public class PagingUtil
{
    private int limit;
    private int startIndex;
    private int tokenType;
    private Long timestamp;
    private String requestURL;
    private Integer totalCount;
    private String orderby;
    private String sortorder;
    private String searchField;
    private String searchKey;
    private static Logger logger;
    public static final int NEXT_TOKEN = 1;
    public static final int PREVIOUS_TOKEN = 2;
    public static List<String> ignoreParams;
    
    public PagingUtil(final String cryptText, final String requestUrl) {
        this.requestURL = null;
        this.totalCount = null;
        final String text = new String(Base64.decode(cryptText));
        final String[] values = text.split(":");
        if (values.length != 4) {
            throw new APIHTTPException("COM0003", new Object[0]);
        }
        this.limit = Integer.valueOf(values[0]);
        this.startIndex = Integer.valueOf(values[1]);
        this.tokenType = Integer.valueOf(values[2]);
        this.timestamp = Long.valueOf(values[3]);
        this.requestURL = requestUrl;
    }
    
    public PagingUtil(final Integer limit, final Integer offset, final String orderby, final String sortorder, final String searchField, final String searchKey, final String requestURL) {
        this.requestURL = null;
        this.totalCount = null;
        if (limit == null || limit < 1) {
            this.limit = 50;
        }
        else if (limit > 200) {
            this.limit = 200;
        }
        else {
            this.limit = limit;
        }
        this.startIndex = offset + 1;
        this.timestamp = System.currentTimeMillis();
        this.requestURL = requestURL;
        this.orderby = orderby;
        this.sortorder = sortorder;
        this.searchField = searchField;
        this.searchKey = searchKey;
    }
    
    public String getNextToken(final int totalCount) {
        this.totalCount = totalCount;
        if (this.startIndex + this.limit <= totalCount) {
            final String text = String.valueOf(this.limit) + ":" + String.valueOf(this.startIndex + this.limit) + ":" + String.valueOf(1) + ":" + String.valueOf(this.timestamp);
            try {
                return URLEncoder.encode(Base64.toBase64String(text.getBytes()), "UTF-8");
            }
            catch (final UnsupportedEncodingException e) {
                throw new APIHTTPException("COM0004", new Object[0]);
            }
        }
        return null;
    }
    
    public String getNextToken() {
        if (this.startIndex + this.limit <= this.totalCount) {
            final String text = String.valueOf(this.limit) + ":" + String.valueOf(this.startIndex + this.limit) + ":" + String.valueOf(1) + ":" + String.valueOf(this.timestamp);
            try {
                return URLEncoder.encode(Base64.toBase64String(text.getBytes()), "UTF-8");
            }
            catch (final UnsupportedEncodingException e) {
                throw new APIHTTPException("COM0004", new Object[0]);
            }
        }
        return null;
    }
    
    public String getPreviousToken() {
        if (this.startIndex != 1) {
            final String text = String.valueOf(this.limit) + ":" + String.valueOf(this.startIndex - this.limit) + ":" + String.valueOf(2) + ":" + String.valueOf(System.currentTimeMillis());
            try {
                return URLEncoder.encode(Base64.toBase64String(text.getBytes()), "UTF-8");
            }
            catch (final UnsupportedEncodingException e) {
                throw new APIHTTPException("COM0004", new Object[0]);
            }
        }
        return null;
    }
    
    public int getStartIndex() {
        return this.startIndex;
    }
    
    public int getLimit() {
        return this.limit;
    }
    
    public int getTokenType() {
        return this.tokenType;
    }
    
    public JSONObject getPagingJSON(final int totalCount) throws APIHTTPException {
        try {
            JSONObject pagingJSON = null;
            final String next = this.getNextToken(totalCount);
            String params = "";
            final StringBuffer paramURL = new StringBuffer();
            if (this.requestURL.contains("?")) {
                params = this.requestURL.split("\\?")[1];
                String[] queryStrings;
                int i;
                String query;
                String[] queryValues;
                String paramName;
                for (queryStrings = params.split("&"), i = 0, i = 0; i < queryStrings.length; ++i) {
                    query = queryStrings[i];
                    queryValues = query.split("=");
                    if (queryValues.length == 2) {
                        paramName = queryValues[0].toLowerCase();
                        if (!PagingUtil.ignoreParams.contains(paramName)) {
                            paramURL.append(query);
                            paramURL.append("&");
                        }
                    }
                }
                if (!paramURL.toString().isEmpty()) {
                    this.requestURL = this.requestURL.split("\\?")[0] + "?" + (Object)paramURL;
                }
                else {
                    this.requestURL = this.requestURL.split("\\?")[0];
                }
            }
            if (next != null) {
                pagingJSON = new JSONObject();
                if (this.requestURL.contains("?")) {
                    pagingJSON.put("next", (Object)(this.requestURL + "skip-token=" + next));
                }
                else {
                    pagingJSON.put("next", (Object)(this.requestURL + "?skip-token=" + next));
                }
            }
            final String prev = this.getPreviousToken();
            if (prev != null) {
                if (pagingJSON == null) {
                    pagingJSON = new JSONObject();
                }
                if (this.requestURL.contains("?")) {
                    pagingJSON.put("previous", (Object)(this.requestURL + "skip-token=" + prev));
                }
                else {
                    pagingJSON.put("previous", (Object)(this.requestURL + "?skip-token=" + prev));
                }
            }
            return pagingJSON;
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public PagingResponse getPagingResponse(final int totalCount) throws Exception {
        try {
            PagingResponse pagingResponse = null;
            final String next = this.getNextToken(totalCount);
            String params = "";
            final StringBuffer paramURL = new StringBuffer();
            final URL url = new URL(this.requestURL);
            if (url.getQuery() != null) {
                params = url.getQuery();
                String[] queryStrings;
                int i;
                String query;
                String[] queryValues;
                String paramName;
                for (queryStrings = params.split("&"), i = 0, i = 0; i < queryStrings.length; ++i) {
                    query = queryStrings[i];
                    queryValues = query.split("=");
                    if (queryValues.length == 2) {
                        paramName = queryValues[0].toLowerCase();
                        if (!PagingUtil.ignoreParams.contains(paramName)) {
                            paramURL.append(query);
                            if (i < queryStrings.length - 1) {
                                paramURL.append("&");
                            }
                        }
                    }
                }
                if (!paramURL.toString().isEmpty()) {
                    this.requestURL = this.requestURL.split("\\?")[0] + "?" + (Object)paramURL;
                }
                else {
                    this.requestURL = this.requestURL.split("\\?")[0];
                }
            }
            if (next != null) {
                pagingResponse = new PagingResponse();
                if (this.requestURL.contains("?")) {
                    pagingResponse.setNext(this.requestURL + "skip-token=" + next);
                }
                else {
                    pagingResponse.setNext(this.requestURL + "?skip-token=" + next);
                }
            }
            final String prev = this.getPreviousToken();
            if (prev != null) {
                if (pagingResponse == null) {
                    pagingResponse = new PagingResponse();
                }
                if (this.requestURL.contains("?")) {
                    pagingResponse.setPrevious(this.requestURL + "skip-token=" + prev);
                }
                else {
                    pagingResponse.setPrevious(this.requestURL + "?skip-token=" + prev);
                }
            }
            return pagingResponse;
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getOrderByJSON() throws APIHTTPException {
        try {
            JSONObject orderByJSON = null;
            if (this.orderby != null) {
                orderByJSON = new JSONObject();
                orderByJSON.put("orderby", (Object)this.orderby);
                orderByJSON.put("sortorder", (Object)"asc");
                if (this.sortorder != null && this.sortorder.equalsIgnoreCase("desc")) {
                    orderByJSON.put("sortorder", (Object)"desc");
                }
            }
            return orderByJSON;
        }
        catch (final JSONException ex) {
            PagingUtil.logger.log(Level.SEVERE, "JSONException : {0} ", ex.getMessage());
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void setSearchBy(final String searchField, final String searchKey) {
        this.searchField = searchField;
        this.searchKey = searchKey;
    }
    
    public JSONObject getSearchJSON() throws APIHTTPException {
        try {
            JSONObject searchByJSON = null;
            if (this.searchField != null) {
                searchByJSON = new JSONObject();
                searchByJSON.put("searchfield", (Object)this.searchField);
                searchByJSON.put("searchkey", (Object)this.searchKey);
            }
            return searchByJSON;
        }
        catch (final JSONException ex) {
            PagingUtil.logger.log(Level.SEVERE, "JSONException : {0} ", ex.getMessage());
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    static {
        PagingUtil.logger = Logger.getLogger(PagingUtil.class.getName());
        PagingUtil.ignoreParams = Arrays.asList("limit", "offset", "skip-token", "service", "subrequest");
    }
}
