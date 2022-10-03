package net.oauth;

import java.util.Collections;
import java.util.HashMap;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.io.UnsupportedEncodingException;

public class OAuth
{
    public static final String VERSION_1_0 = "1.0";
    public static final String ENCODING = "UTF-8";
    public static final String FORM_ENCODED = "application/x-www-form-urlencoded";
    public static final String OAUTH_CONSUMER_KEY = "oauth_consumer_key";
    public static final String OAUTH_TOKEN = "oauth_token";
    public static final String OAUTH_TOKEN_SECRET = "oauth_token_secret";
    public static final String OAUTH_SIGNATURE_METHOD = "oauth_signature_method";
    public static final String OAUTH_SIGNATURE = "oauth_signature";
    public static final String OAUTH_TIMESTAMP = "oauth_timestamp";
    public static final String OAUTH_NONCE = "oauth_nonce";
    public static final String OAUTH_VERSION = "oauth_version";
    public static final String OAUTH_CALLBACK = "oauth_callback";
    public static final String OAUTH_CALLBACK_CONFIRMED = "oauth_callback_confirmed";
    public static final String OAUTH_VERIFIER = "oauth_verifier";
    public static final String HMAC_SHA1 = "HMAC-SHA1";
    public static final String RSA_SHA1 = "RSA-SHA1";
    private static String characterEncoding;
    
    public static void setCharacterEncoding(final String encoding) {
        OAuth.characterEncoding = encoding;
    }
    
    public static String decodeCharacters(final byte[] from) {
        if (OAuth.characterEncoding != null) {
            try {
                return new String(from, OAuth.characterEncoding);
            }
            catch (final UnsupportedEncodingException e) {
                System.err.println(e + "");
            }
        }
        return new String(from);
    }
    
    public static byte[] encodeCharacters(final String from) {
        if (OAuth.characterEncoding != null) {
            try {
                return from.getBytes(OAuth.characterEncoding);
            }
            catch (final UnsupportedEncodingException e) {
                System.err.println(e + "");
            }
        }
        return from.getBytes();
    }
    
    public static boolean isFormEncoded(String contentType) {
        if (contentType == null) {
            return false;
        }
        final int semi = contentType.indexOf(";");
        if (semi >= 0) {
            contentType = contentType.substring(0, semi);
        }
        return "application/x-www-form-urlencoded".equalsIgnoreCase(contentType.trim());
    }
    
    public static String formEncode(final Iterable<? extends Map.Entry> parameters) throws IOException {
        final ByteArrayOutputStream b = new ByteArrayOutputStream();
        formEncode(parameters, b);
        return decodeCharacters(b.toByteArray());
    }
    
    public static void formEncode(final Iterable<? extends Map.Entry> parameters, final OutputStream into) throws IOException {
        if (parameters != null) {
            boolean first = true;
            for (final Map.Entry parameter : parameters) {
                if (first) {
                    first = false;
                }
                else {
                    into.write(38);
                }
                into.write(encodeCharacters(percentEncode(toString(parameter.getKey()))));
                into.write(61);
                into.write(encodeCharacters(percentEncode(toString(parameter.getValue()))));
            }
        }
    }
    
    public static List<Parameter> decodeForm(final String form) {
        final List<Parameter> list = new ArrayList<Parameter>();
        if (!isEmpty(form)) {
            for (final String nvp : form.split("\\&")) {
                final int equals = nvp.indexOf(61);
                String name;
                String value;
                if (equals < 0) {
                    name = decodePercent(nvp);
                    value = null;
                }
                else {
                    name = decodePercent(nvp.substring(0, equals));
                    value = decodePercent(nvp.substring(equals + 1));
                }
                list.add(new Parameter(name, value));
            }
        }
        return list;
    }
    
    public static String percentEncode(final Iterable values) {
        final StringBuilder p = new StringBuilder();
        for (final Object v : values) {
            if (p.length() > 0) {
                p.append("&");
            }
            p.append(percentEncode(toString(v)));
        }
        return p.toString();
    }
    
    public static String percentEncode(final String s) {
        if (s == null) {
            return "";
        }
        try {
            return URLEncoder.encode(s, "UTF-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
        }
        catch (final UnsupportedEncodingException wow) {
            throw new RuntimeException(wow.getMessage(), wow);
        }
    }
    
    public static String decodePercent(final String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        }
        catch (final UnsupportedEncodingException wow) {
            throw new RuntimeException(wow.getMessage(), wow);
        }
    }
    
    public static Map<String, String> newMap(final Iterable<? extends Map.Entry> from) {
        final Map<String, String> map = new HashMap<String, String>();
        if (from != null) {
            for (final Map.Entry f : from) {
                final String key = toString(f.getKey());
                if (!map.containsKey(key)) {
                    map.put(key, toString(f.getValue()));
                }
            }
        }
        return map;
    }
    
    public static List<Parameter> newList(final String... parameters) {
        final List<Parameter> list = new ArrayList<Parameter>(parameters.length / 2);
        for (int p = 0; p + 1 < parameters.length; p += 2) {
            list.add(new Parameter(parameters[p], parameters[p + 1]));
        }
        return list;
    }
    
    private static final String toString(final Object from) {
        return (from == null) ? null : from.toString();
    }
    
    public static String addParameters(final String url, final String... parameters) throws IOException {
        return addParameters(url, newList(parameters));
    }
    
    public static String addParameters(final String url, final Iterable<? extends Map.Entry<String, String>> parameters) throws IOException {
        final String form = formEncode(parameters);
        if (form == null || form.length() <= 0) {
            return url;
        }
        return url + ((url.indexOf("?") < 0) ? '?' : '&') + form;
    }
    
    public static boolean isEmpty(final String str) {
        return str == null || str.length() == 0;
    }
    
    static {
        OAuth.characterEncoding = "UTF-8";
    }
    
    public static class Problems
    {
        public static final String VERSION_REJECTED = "version_rejected";
        public static final String PARAMETER_ABSENT = "parameter_absent";
        public static final String PARAMETER_REJECTED = "parameter_rejected";
        public static final String TIMESTAMP_REFUSED = "timestamp_refused";
        public static final String NONCE_USED = "nonce_used";
        public static final String SIGNATURE_METHOD_REJECTED = "signature_method_rejected";
        public static final String SIGNATURE_INVALID = "signature_invalid";
        public static final String CONSUMER_KEY_UNKNOWN = "consumer_key_unknown";
        public static final String CONSUMER_KEY_REJECTED = "consumer_key_rejected";
        public static final String CONSUMER_KEY_REFUSED = "consumer_key_refused";
        public static final String TOKEN_USED = "token_used";
        public static final String TOKEN_EXPIRED = "token_expired";
        public static final String TOKEN_REVOKED = "token_revoked";
        public static final String TOKEN_REJECTED = "token_rejected";
        public static final String ADDITIONAL_AUTHORIZATION_REQUIRED = "additional_authorization_required";
        public static final String PERMISSION_UNKNOWN = "permission_unknown";
        public static final String PERMISSION_DENIED = "permission_denied";
        public static final String USER_REFUSED = "user_refused";
        public static final String OAUTH_ACCEPTABLE_VERSIONS = "oauth_acceptable_versions";
        public static final String OAUTH_ACCEPTABLE_TIMESTAMPS = "oauth_acceptable_timestamps";
        public static final String OAUTH_PARAMETERS_ABSENT = "oauth_parameters_absent";
        public static final String OAUTH_PARAMETERS_REJECTED = "oauth_parameters_rejected";
        public static final String OAUTH_PROBLEM_ADVICE = "oauth_problem_advice";
        public static final Map<String, Integer> TO_HTTP_CODE;
        
        private static Map<String, Integer> mapToHttpCode() {
            final Integer badRequest = new Integer(400);
            final Integer unauthorized = new Integer(401);
            final Integer serviceUnavailable = new Integer(503);
            final Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("version_rejected", badRequest);
            map.put("parameter_absent", badRequest);
            map.put("parameter_rejected", badRequest);
            map.put("timestamp_refused", badRequest);
            map.put("signature_method_rejected", badRequest);
            map.put("nonce_used", unauthorized);
            map.put("token_used", unauthorized);
            map.put("token_expired", unauthorized);
            map.put("token_revoked", unauthorized);
            map.put("token_rejected", unauthorized);
            map.put("token_not_authorized", unauthorized);
            map.put("signature_invalid", unauthorized);
            map.put("consumer_key_unknown", unauthorized);
            map.put("consumer_key_rejected", unauthorized);
            map.put("additional_authorization_required", unauthorized);
            map.put("permission_unknown", unauthorized);
            map.put("permission_denied", unauthorized);
            map.put("user_refused", serviceUnavailable);
            map.put("consumer_key_refused", serviceUnavailable);
            return Collections.unmodifiableMap((Map<? extends String, ? extends Integer>)map);
        }
        
        static {
            TO_HTTP_CODE = mapToHttpCode();
        }
    }
    
    public static class Parameter implements Map.Entry<String, String>
    {
        private final String key;
        private String value;
        
        public Parameter(final String key, final String value) {
            this.key = key;
            this.value = value;
        }
        
        public String getKey() {
            return this.key;
        }
        
        public String getValue() {
            return this.value;
        }
        
        public String setValue(final String value) {
            try {
                return this.value;
            }
            finally {
                this.value = value;
            }
        }
        
        @Override
        public String toString() {
            return OAuth.percentEncode(this.getKey()) + '=' + OAuth.percentEncode(this.getValue());
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = 31 * result + ((this.key == null) ? 0 : this.key.hashCode());
            result = 31 * result + ((this.value == null) ? 0 : this.value.hashCode());
            return result;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final Parameter that = (Parameter)obj;
            if (this.key == null) {
                if (that.key != null) {
                    return false;
                }
            }
            else if (!this.key.equals(that.key)) {
                return false;
            }
            if (this.value == null) {
                if (that.value != null) {
                    return false;
                }
            }
            else if (!this.value.equals(that.value)) {
                return false;
            }
            return true;
        }
    }
}
