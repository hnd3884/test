package javax.xml.crypto.dsig.spec;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Collections;
import java.util.Map;

public class XPathType
{
    private final String expression;
    private final Filter filter;
    private Map nsMap;
    
    public XPathType(final String expression, final Filter filter) {
        if (expression == null) {
            throw new NullPointerException("expression cannot be null");
        }
        if (filter == null) {
            throw new NullPointerException("filter cannot be null");
        }
        this.expression = expression;
        this.filter = filter;
        this.nsMap = Collections.EMPTY_MAP;
    }
    
    public XPathType(final String s, final Filter filter, final Map map) {
        this(s, filter);
        if (map == null) {
            throw new NullPointerException("namespaceMap cannot be null");
        }
        this.nsMap = new HashMap(map);
        final Iterator iterator = this.nsMap.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry entry = (Map.Entry)iterator.next();
            if (!(entry.getKey() instanceof String) || !(entry.getValue() instanceof String)) {
                throw new ClassCastException("not a String");
            }
        }
        this.nsMap = Collections.unmodifiableMap((Map<?, ?>)this.nsMap);
    }
    
    public String getExpression() {
        return this.expression;
    }
    
    public Filter getFilter() {
        return this.filter;
    }
    
    public Map getNamespaceMap() {
        return this.nsMap;
    }
    
    public static class Filter
    {
        private final String operation;
        public static final Filter INTERSECT;
        public static final Filter SUBTRACT;
        public static final Filter UNION;
        
        private Filter(final String operation) {
            this.operation = operation;
        }
        
        public String toString() {
            return this.operation;
        }
        
        static {
            INTERSECT = new Filter("intersect");
            SUBTRACT = new Filter("subtract");
            UNION = new Filter("union");
        }
    }
}
