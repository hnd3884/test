package com.google.android.gcm.server;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.URL;
import java.io.OutputStream;
import java.io.Closeable;
import java.net.HttpURLConnection;
import org.json.simple.JSONValue;
import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import org.json.simple.parser.ParseException;
import java.util.ArrayList;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.util.HashMap;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;

public class Sender
{
    protected static final String UTF8 = "UTF-8";
    protected static final int BACKOFF_INITIAL_DELAY = 1000;
    protected static final int MAX_BACKOFF_DELAY = 1024000;
    protected final Random random;
    protected static final Logger logger;
    private final String key;
    private String endpoint;
    private int connectTimeout;
    private int readTimeout;
    
    public Sender(final String key) {
        this(key, "https://fcm.googleapis.com/fcm/send");
    }
    
    public Sender(final String key, final String endpoint) {
        this.random = new Random();
        this.key = nonNull(key);
        this.endpoint = nonNull(endpoint);
    }
    
    public String getEndpoint() {
        return this.endpoint;
    }
    
    public final void setConnectTimeout(final int connectTimeout) {
        if (connectTimeout < 0) {
            throw new IllegalArgumentException("timeout can not be negative");
        }
        this.connectTimeout = connectTimeout;
    }
    
    public final void setReadTimeout(final int readTimeout) {
        if (readTimeout < 0) {
            throw new IllegalArgumentException("timeout can not be negative");
        }
        this.readTimeout = readTimeout;
    }
    
    public Result send(final Message message, final String to, final int retries) throws IOException {
        int attempt = 0;
        int backoff = 1000;
        boolean tryAgain;
        Result result;
        do {
            ++attempt;
            if (Sender.logger.isLoggable(Level.FINE)) {
                Sender.logger.fine("Attempt #" + attempt + " to send message " + message + " to regIds " + to);
            }
            result = this.sendNoRetry(message, to);
            tryAgain = (result == null && attempt <= retries);
            if (tryAgain) {
                final int sleepTime = backoff / 2 + this.random.nextInt(backoff);
                this.sleep(sleepTime);
                if (2 * backoff >= 1024000) {
                    continue;
                }
                backoff *= 2;
            }
        } while (tryAgain);
        if (result == null) {
            throw new IOException("Could not send message after " + attempt + " attempts");
        }
        return result;
    }
    
    public Result sendNoRetry(final Message message, final String to) throws IOException {
        nonNull(to);
        final Map<Object, Object> jsonRequest = new HashMap<Object, Object>();
        this.messageToMap(message, jsonRequest);
        jsonRequest.put("to", to);
        final String responseBody = this.makeGcmHttpRequest(jsonRequest);
        if (responseBody == null) {
            return null;
        }
        final JSONParser parser = new JSONParser();
        try {
            final JSONObject jsonResponse = (JSONObject)parser.parse(responseBody);
            final Result.Builder resultBuilder = new Result.Builder();
            if (jsonResponse.containsKey((Object)"results")) {
                final JSONArray jsonResults = (JSONArray)jsonResponse.get((Object)"results");
                if (jsonResults.size() != 1) {
                    Sender.logger.log(Level.WARNING, "Found null or " + jsonResults.size() + " results, expected one");
                    return null;
                }
                final JSONObject jsonResult = (JSONObject)jsonResults.get(0);
                final String messageId = (String)jsonResult.get((Object)"message_id");
                final String canonicalRegId = (String)jsonResult.get((Object)"registration_id");
                final String error = (String)jsonResult.get((Object)"error");
                resultBuilder.messageId(messageId).canonicalRegistrationId(canonicalRegId).errorCode(error);
            }
            else if (to.startsWith("/topics/")) {
                if (jsonResponse.containsKey((Object)"message_id")) {
                    final Long messageId2 = (Long)jsonResponse.get((Object)"message_id");
                    resultBuilder.messageId(messageId2.toString());
                }
                else {
                    if (!jsonResponse.containsKey((Object)"error")) {
                        Sender.logger.log(Level.WARNING, "Expected message_id or error found: " + responseBody);
                        return null;
                    }
                    final String error2 = (String)jsonResponse.get((Object)"error");
                    resultBuilder.errorCode(error2);
                }
            }
            else {
                if (!jsonResponse.containsKey((Object)"success") || !jsonResponse.containsKey((Object)"failure")) {
                    Sender.logger.warning("Unrecognized response: " + responseBody);
                    throw this.newIoException(responseBody, new Exception("Unrecognized response."));
                }
                final int success = this.getNumber((Map<?, ?>)jsonResponse, "success").intValue();
                final int failure = this.getNumber((Map<?, ?>)jsonResponse, "failure").intValue();
                List<String> failedIds = null;
                if (jsonResponse.containsKey((Object)"failed_registration_ids")) {
                    final JSONArray jFailedIds = (JSONArray)jsonResponse.get((Object)"failed_registration_ids");
                    failedIds = new ArrayList<String>();
                    for (int i = 0; i < jFailedIds.size(); ++i) {
                        failedIds.add((String)jFailedIds.get(i));
                    }
                }
                resultBuilder.success(success).failure(failure).failedRegistrationIds(failedIds);
            }
            return resultBuilder.build();
        }
        catch (final ParseException e) {
            throw this.newIoException(responseBody, (Exception)e);
        }
        catch (final CustomParserException e2) {
            throw this.newIoException(responseBody, e2);
        }
    }
    
    public MulticastResult send(final Message message, final List<String> regIds, final int retries) throws IOException {
        int attempt = 0;
        int backoff = 1000;
        final Map<String, Result> results = new HashMap<String, Result>();
        List<String> unsentRegIds = new ArrayList<String>(regIds);
        final List<Long> multicastIds = new ArrayList<Long>();
        boolean tryAgain;
        do {
            MulticastResult multicastResult = null;
            ++attempt;
            if (Sender.logger.isLoggable(Level.FINE)) {
                Sender.logger.fine("Attempt #" + attempt + " to send message " + message + " to regIds " + unsentRegIds);
            }
            try {
                multicastResult = this.sendNoRetry(message, unsentRegIds);
            }
            catch (final IOException e) {
                Sender.logger.log(Level.FINEST, "IOException on attempt " + attempt, e);
            }
            if (multicastResult != null) {
                final long multicastId = multicastResult.getMulticastId();
                Sender.logger.fine("multicast_id on attempt # " + attempt + ": " + multicastId);
                multicastIds.add(multicastId);
                unsentRegIds = this.updateStatus(unsentRegIds, results, multicastResult);
                tryAgain = (!unsentRegIds.isEmpty() && attempt <= retries);
            }
            else {
                tryAgain = (attempt <= retries);
            }
            if (tryAgain) {
                final int sleepTime = backoff / 2 + this.random.nextInt(backoff);
                this.sleep(sleepTime);
                if (2 * backoff >= 1024000) {
                    continue;
                }
                backoff *= 2;
            }
        } while (tryAgain);
        if (multicastIds.isEmpty()) {
            throw new IOException("Could not post JSON requests to GCM after " + attempt + " attempts");
        }
        int success = 0;
        int failure = 0;
        int canonicalIds = 0;
        for (final Result result : results.values()) {
            if (result.getMessageId() != null) {
                ++success;
                if (result.getCanonicalRegistrationId() == null) {
                    continue;
                }
                ++canonicalIds;
            }
            else {
                ++failure;
            }
        }
        final long multicastId2 = multicastIds.remove(0);
        final MulticastResult.Builder builder = new MulticastResult.Builder(success, failure, canonicalIds, multicastId2).retryMulticastIds(multicastIds);
        for (final String regId : regIds) {
            final Result result2 = results.get(regId);
            builder.addResult(result2);
        }
        return builder.build();
    }
    
    private List<String> updateStatus(final List<String> unsentRegIds, final Map<String, Result> allResults, final MulticastResult multicastResult) {
        final List<Result> results = multicastResult.getResults();
        if (results.size() != unsentRegIds.size()) {
            throw new RuntimeException("Internal error: sizes do not match. currentResults: " + results + "; unsentRegIds: " + unsentRegIds);
        }
        final List<String> newUnsentRegIds = new ArrayList<String>();
        for (int i = 0; i < unsentRegIds.size(); ++i) {
            final String regId = unsentRegIds.get(i);
            final Result result = results.get(i);
            allResults.put(regId, result);
            final String error = result.getErrorCodeName();
            if (error != null && (error.equals("Unavailable") || error.equals("InternalServerError"))) {
                newUnsentRegIds.add(regId);
            }
        }
        return newUnsentRegIds;
    }
    
    public MulticastResult sendNoRetry(final Message message, final List<String> registrationIds) throws IOException {
        if (nonNull(registrationIds).isEmpty()) {
            throw new IllegalArgumentException("registrationIds cannot be empty");
        }
        final Map<Object, Object> jsonRequest = new HashMap<Object, Object>();
        this.messageToMap(message, jsonRequest);
        jsonRequest.put("registration_ids", registrationIds);
        final String responseBody = this.makeGcmHttpRequest(jsonRequest);
        if (responseBody == null) {
            return null;
        }
        final JSONParser parser = new JSONParser();
        try {
            final JSONObject jsonResponse = (JSONObject)parser.parse(responseBody);
            final int success = this.getNumber((Map<?, ?>)jsonResponse, "success").intValue();
            final int failure = this.getNumber((Map<?, ?>)jsonResponse, "failure").intValue();
            final int canonicalIds = this.getNumber((Map<?, ?>)jsonResponse, "canonical_ids").intValue();
            final long multicastId = this.getNumber((Map<?, ?>)jsonResponse, "multicast_id").longValue();
            final MulticastResult.Builder builder = new MulticastResult.Builder(success, failure, canonicalIds, multicastId);
            final List<Map<String, Object>> results = (List<Map<String, Object>>)jsonResponse.get((Object)"results");
            if (results != null) {
                for (final Map<String, Object> jsonResult : results) {
                    final String messageId = jsonResult.get("message_id");
                    final String canonicalRegId = jsonResult.get("registration_id");
                    final String error = jsonResult.get("error");
                    final Result result = new Result.Builder().messageId(messageId).canonicalRegistrationId(canonicalRegId).errorCode(error).build();
                    builder.addResult(result);
                }
            }
            return builder.build();
        }
        catch (final ParseException e) {
            throw this.newIoException(responseBody, (Exception)e);
        }
        catch (final CustomParserException e2) {
            throw this.newIoException(responseBody, e2);
        }
    }
    
    private String makeGcmHttpRequest(final Map<Object, Object> jsonRequest) throws InvalidRequestException {
        final String requestBody = JSONValue.toJSONString((Object)jsonRequest);
        Sender.logger.finest("JSON request: " + requestBody);
        HttpURLConnection conn;
        int status;
        try {
            conn = this.post(this.getEndpoint(), "application/json", requestBody);
            status = conn.getResponseCode();
        }
        catch (final IOException e) {
            Sender.logger.log(Level.FINE, "IOException posting to GCM", e);
            return null;
        }
        if (status != 200) {
            String responseBody;
            try {
                responseBody = getAndClose(conn.getErrorStream());
                Sender.logger.finest("JSON error response: " + responseBody);
            }
            catch (final IOException e2) {
                responseBody = "N/A";
                Sender.logger.log(Level.FINE, "Exception reading response: ", e2);
            }
            throw new InvalidRequestException(status, responseBody);
        }
        String responseBody;
        try {
            responseBody = getAndClose(conn.getInputStream());
        }
        catch (final IOException e2) {
            Sender.logger.log(Level.WARNING, "IOException reading response", e2);
            return null;
        }
        Sender.logger.finest("JSON response: " + responseBody);
        return responseBody;
    }
    
    private void messageToMap(final Message message, final Map<Object, Object> mapRequest) {
        if (message == null || mapRequest == null) {
            return;
        }
        this.setJsonField(mapRequest, "priority", message.getPriority());
        this.setJsonField(mapRequest, "content_available", message.getContentAvailable());
        this.setJsonField(mapRequest, "time_to_live", message.getTimeToLive());
        this.setJsonField(mapRequest, "collapse_key", message.getCollapseKey());
        this.setJsonField(mapRequest, "restricted_package_name", message.getRestrictedPackageName());
        this.setJsonField(mapRequest, "delay_while_idle", message.isDelayWhileIdle());
        this.setJsonField(mapRequest, "dry_run", message.isDryRun());
        final Map<String, String> payload = message.getData();
        if (!payload.isEmpty()) {
            mapRequest.put("data", payload);
        }
        if (message.getNotification() != null) {
            final Notification notification = message.getNotification();
            final Map<Object, Object> nMap = new HashMap<Object, Object>();
            if (notification.getBadge() != null) {
                this.setJsonField(nMap, "badge", notification.getBadge().toString());
            }
            this.setJsonField(nMap, "body", notification.getBody());
            this.setJsonField(nMap, "body_loc_args", notification.getBodyLocArgs());
            this.setJsonField(nMap, "body_loc_key", notification.getBodyLocKey());
            this.setJsonField(nMap, "click_action", notification.getClickAction());
            this.setJsonField(nMap, "color", notification.getColor());
            this.setJsonField(nMap, "icon", notification.getIcon());
            this.setJsonField(nMap, "sound", notification.getSound());
            this.setJsonField(nMap, "tag", notification.getTag());
            this.setJsonField(nMap, "title", notification.getTitle());
            this.setJsonField(nMap, "title_loc_args", notification.getTitleLocArgs());
            this.setJsonField(nMap, "title_loc_key", notification.getTitleLocKey());
            mapRequest.put("notification", nMap);
        }
    }
    
    private IOException newIoException(final String responseBody, final Exception e) {
        final String msg = "Error parsing JSON response (" + responseBody + ")";
        Sender.logger.log(Level.WARNING, msg, e);
        return new IOException(msg + ":" + e);
    }
    
    private static void close(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            }
            catch (final IOException e) {
                Sender.logger.log(Level.FINEST, "IOException closing stream", e);
            }
        }
    }
    
    private void setJsonField(final Map<Object, Object> json, final String field, final Object value) {
        if (value != null) {
            json.put(field, value);
        }
    }
    
    private Number getNumber(final Map<?, ?> json, final String field) {
        final Object value = json.get(field);
        if (value == null) {
            throw new CustomParserException("Missing field: " + field);
        }
        if (!(value instanceof Number)) {
            throw new CustomParserException("Field " + field + " does not contain a number: " + value);
        }
        return (Number)value;
    }
    
    protected HttpURLConnection post(final String url, final String body) throws IOException {
        return this.post(url, "application/x-www-form-urlencoded;charset=UTF-8", body);
    }
    
    protected HttpURLConnection post(final String url, final String contentType, final String body) throws IOException {
        if (url == null || contentType == null || body == null) {
            throw new IllegalArgumentException("arguments cannot be null");
        }
        if (!url.startsWith("https://")) {
            Sender.logger.warning("URL does not use https: " + url);
        }
        Sender.logger.fine("Sending POST to " + url);
        Sender.logger.finest("POST body: " + body);
        final byte[] bytes = body.getBytes("UTF-8");
        final HttpURLConnection conn = this.getConnection(url);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setFixedLengthStreamingMode(bytes.length);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", contentType);
        conn.setRequestProperty("Authorization", "key=" + this.key);
        final OutputStream out = conn.getOutputStream();
        try {
            out.write(bytes);
        }
        finally {
            close(out);
        }
        return conn;
    }
    
    protected static final Map<String, String> newKeyValues(final String key, final String value) {
        final Map<String, String> keyValues = new HashMap<String, String>(1);
        keyValues.put(nonNull(key), nonNull(value));
        return keyValues;
    }
    
    protected static StringBuilder newBody(final String name, final String value) {
        return new StringBuilder(nonNull(name)).append('=').append(nonNull(value));
    }
    
    protected static void addParameter(final StringBuilder body, final String name, final String value) {
        nonNull(body).append('&').append(nonNull(name)).append('=').append(nonNull(value));
    }
    
    protected HttpURLConnection getConnection(final String url) throws IOException {
        final HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
        conn.setConnectTimeout(this.connectTimeout);
        conn.setReadTimeout(this.readTimeout);
        return conn;
    }
    
    protected static String getString(final InputStream stream) throws IOException {
        if (stream == null) {
            return "";
        }
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        final StringBuilder content = new StringBuilder();
        String newLine;
        do {
            newLine = reader.readLine();
            if (newLine != null) {
                content.append(newLine).append('\n');
            }
        } while (newLine != null);
        if (content.length() > 0) {
            content.setLength(content.length() - 1);
        }
        return content.toString();
    }
    
    private static String getAndClose(final InputStream stream) throws IOException {
        try {
            return getString(stream);
        }
        finally {
            if (stream != null) {
                close(stream);
            }
        }
    }
    
    static <T> T nonNull(final T argument) {
        if (argument == null) {
            throw new IllegalArgumentException("argument cannot be null");
        }
        return argument;
    }
    
    void sleep(final long millis) {
        try {
            Thread.sleep(millis);
        }
        catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    static {
        logger = Logger.getLogger(Sender.class.getName());
    }
    
    class CustomParserException extends RuntimeException
    {
        CustomParserException(final String message) {
            super(message);
        }
    }
}
