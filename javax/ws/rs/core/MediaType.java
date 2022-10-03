package javax.ws.rs.core;

import java.util.Collections;
import java.util.Iterator;
import java.util.Comparator;
import java.util.TreeMap;
import javax.ws.rs.ext.RuntimeDelegate;
import java.util.Map;

public class MediaType
{
    private String type;
    private String subtype;
    private Map<String, String> parameters;
    public static final String CHARSET_PARAMETER = "charset";
    public static final String MEDIA_TYPE_WILDCARD = "*";
    public static final String WILDCARD = "*/*";
    public static final MediaType WILDCARD_TYPE;
    public static final String APPLICATION_XML = "application/xml";
    public static final MediaType APPLICATION_XML_TYPE;
    public static final String APPLICATION_ATOM_XML = "application/atom+xml";
    public static final MediaType APPLICATION_ATOM_XML_TYPE;
    public static final String APPLICATION_XHTML_XML = "application/xhtml+xml";
    public static final MediaType APPLICATION_XHTML_XML_TYPE;
    public static final String APPLICATION_SVG_XML = "application/svg+xml";
    public static final MediaType APPLICATION_SVG_XML_TYPE;
    public static final String APPLICATION_JSON = "application/json";
    public static final MediaType APPLICATION_JSON_TYPE;
    public static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final MediaType APPLICATION_FORM_URLENCODED_TYPE;
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final MediaType MULTIPART_FORM_DATA_TYPE;
    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    public static final MediaType APPLICATION_OCTET_STREAM_TYPE;
    public static final String TEXT_PLAIN = "text/plain";
    public static final MediaType TEXT_PLAIN_TYPE;
    public static final String TEXT_XML = "text/xml";
    public static final MediaType TEXT_XML_TYPE;
    public static final String TEXT_HTML = "text/html";
    public static final MediaType TEXT_HTML_TYPE;
    public static final String SERVER_SENT_EVENTS = "text/event-stream";
    public static final MediaType SERVER_SENT_EVENTS_TYPE;
    public static final String APPLICATION_JSON_PATCH_JSON = "application/json-patch+json";
    public static final MediaType APPLICATION_JSON_PATCH_JSON_TYPE;
    
    public static MediaType valueOf(final String type) {
        return RuntimeDelegate.getInstance().createHeaderDelegate(MediaType.class).fromString(type);
    }
    
    private static TreeMap<String, String> createParametersMap(final Map<String, String> initialValues) {
        final TreeMap<String, String> map = new TreeMap<String, String>(new Comparator<String>() {
            @Override
            public int compare(final String o1, final String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });
        if (initialValues != null) {
            for (final Map.Entry<String, String> e : initialValues.entrySet()) {
                map.put(e.getKey().toLowerCase(), e.getValue());
            }
        }
        return map;
    }
    
    public MediaType(final String type, final String subtype, final Map<String, String> parameters) {
        this(type, subtype, null, createParametersMap(parameters));
    }
    
    public MediaType(final String type, final String subtype) {
        this(type, subtype, null, null);
    }
    
    public MediaType(final String type, final String subtype, final String charset) {
        this(type, subtype, charset, null);
    }
    
    public MediaType() {
        this("*", "*", null, null);
    }
    
    private MediaType(final String type, final String subtype, final String charset, Map<String, String> parameterMap) {
        this.type = ((type == null) ? "*" : type);
        this.subtype = ((subtype == null) ? "*" : subtype);
        if (parameterMap == null) {
            parameterMap = new TreeMap<String, String>(new Comparator<String>() {
                @Override
                public int compare(final String o1, final String o2) {
                    return o1.compareToIgnoreCase(o2);
                }
            });
        }
        if (charset != null && !charset.isEmpty()) {
            parameterMap.put("charset", charset);
        }
        this.parameters = Collections.unmodifiableMap((Map<? extends String, ? extends String>)parameterMap);
    }
    
    public String getType() {
        return this.type;
    }
    
    public boolean isWildcardType() {
        return this.getType().equals("*");
    }
    
    public String getSubtype() {
        return this.subtype;
    }
    
    public boolean isWildcardSubtype() {
        return this.getSubtype().equals("*");
    }
    
    public Map<String, String> getParameters() {
        return this.parameters;
    }
    
    public MediaType withCharset(final String charset) {
        return new MediaType(this.type, this.subtype, charset, createParametersMap(this.parameters));
    }
    
    public boolean isCompatible(final MediaType other) {
        return other != null && (this.type.equals("*") || other.type.equals("*") || (this.type.equalsIgnoreCase(other.type) && (this.subtype.equals("*") || other.subtype.equals("*"))) || (this.type.equalsIgnoreCase(other.type) && this.subtype.equalsIgnoreCase(other.subtype)));
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof MediaType)) {
            return false;
        }
        final MediaType other = (MediaType)obj;
        return this.type.equalsIgnoreCase(other.type) && this.subtype.equalsIgnoreCase(other.subtype) && this.parameters.equals(other.parameters);
    }
    
    @Override
    public int hashCode() {
        return (this.type.toLowerCase() + this.subtype.toLowerCase()).hashCode() + this.parameters.hashCode();
    }
    
    @Override
    public String toString() {
        return RuntimeDelegate.getInstance().createHeaderDelegate(MediaType.class).toString(this);
    }
    
    static {
        WILDCARD_TYPE = new MediaType();
        APPLICATION_XML_TYPE = new MediaType("application", "xml");
        APPLICATION_ATOM_XML_TYPE = new MediaType("application", "atom+xml");
        APPLICATION_XHTML_XML_TYPE = new MediaType("application", "xhtml+xml");
        APPLICATION_SVG_XML_TYPE = new MediaType("application", "svg+xml");
        APPLICATION_JSON_TYPE = new MediaType("application", "json");
        APPLICATION_FORM_URLENCODED_TYPE = new MediaType("application", "x-www-form-urlencoded");
        MULTIPART_FORM_DATA_TYPE = new MediaType("multipart", "form-data");
        APPLICATION_OCTET_STREAM_TYPE = new MediaType("application", "octet-stream");
        TEXT_PLAIN_TYPE = new MediaType("text", "plain");
        TEXT_XML_TYPE = new MediaType("text", "xml");
        TEXT_HTML_TYPE = new MediaType("text", "html");
        SERVER_SENT_EVENTS_TYPE = new MediaType("text", "event-stream");
        APPLICATION_JSON_PATCH_JSON_TYPE = new MediaType("application", "json-patch+json");
    }
}
