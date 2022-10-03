package net.oauth;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

public class OAuthProblemException extends OAuthException
{
    public static final String OAUTH_PROBLEM = "oauth_problem";
    public static final String HTTP_REQUEST = "HTTP request";
    public static final String HTTP_RESPONSE = "HTTP response";
    public static final String HTTP_STATUS_CODE = "HTTP status";
    public static final String HTTP_LOCATION = "Location";
    public static final String SIGNATURE_BASE_STRING = "oauth_signature base string";
    public static final String URL = "URL";
    private final Map<String, Object> parameters;
    private static final long serialVersionUID = 1L;
    
    public OAuthProblemException() {
        this.parameters = new HashMap<String, Object>();
    }
    
    public OAuthProblemException(final String problem) {
        super(problem);
        this.parameters = new HashMap<String, Object>();
        if (problem != null) {
            this.parameters.put("oauth_problem", problem);
        }
    }
    
    @Override
    public String getMessage() {
        String msg = super.getMessage();
        if (msg != null) {
            return msg;
        }
        msg = this.getProblem();
        if (msg != null) {
            return msg;
        }
        Object response = this.getParameters().get("HTTP response");
        if (response != null) {
            msg = response.toString();
            int eol = msg.indexOf("\n");
            if (eol < 0) {
                eol = msg.indexOf("\r");
            }
            if (eol >= 0) {
                msg = msg.substring(0, eol);
            }
            msg = msg.trim();
            if (msg.length() > 0) {
                return msg;
            }
        }
        response = this.getHttpStatusCode();
        if (response != null) {
            return "HTTP status " + response;
        }
        return null;
    }
    
    public void setParameter(final String name, final Object value) {
        this.getParameters().put(name, value);
    }
    
    public Map<String, Object> getParameters() {
        return this.parameters;
    }
    
    public String getProblem() {
        return this.getParameters().get("oauth_problem");
    }
    
    public int getHttpStatusCode() {
        final Object code = this.getParameters().get("HTTP status");
        if (code == null) {
            return 200;
        }
        if (code instanceof Number) {
            return ((Number)code).intValue();
        }
        return Integer.parseInt(code.toString());
    }
    
    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder(super.toString());
        try {
            final String eol = System.getProperty("line.separator", "\n");
            final Map<String, Object> parameters = this.getParameters();
            for (final String key : new String[] { "oauth_problem_advice", "URL", "oauth_signature base string" }) {
                final Object value = parameters.get(key);
                if (value != null) {
                    s.append(eol + key + ": " + value);
                }
            }
            Object msg = parameters.get("HTTP request");
            if (msg != null) {
                s.append(eol + ">>>>>>>> " + "HTTP request" + ":" + eol + msg);
            }
            msg = parameters.get("HTTP response");
            if (msg != null) {
                s.append(eol + "<<<<<<<< " + "HTTP response" + ":" + eol + msg);
            }
            else {
                for (final Map.Entry<String, Object> parameter : parameters.entrySet()) {
                    final String key = parameter.getKey();
                    if (!"oauth_problem_advice".equals(key) && !"URL".equals(key) && !"oauth_signature base string".equals(key) && !"HTTP request".equals(key)) {
                        if ("HTTP response".equals(key)) {
                            continue;
                        }
                        s.append(eol + key + ": " + parameter.getValue());
                    }
                }
            }
        }
        catch (final Exception ex) {}
        return s.toString();
    }
}
