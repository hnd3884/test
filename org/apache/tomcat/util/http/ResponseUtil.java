package org.apache.tomcat.util.http;

import java.util.List;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.IOException;
import java.util.Collection;
import java.io.Reader;
import org.apache.tomcat.util.http.parser.TokenList;
import java.io.StringReader;
import java.util.LinkedHashSet;
import javax.servlet.http.HttpServletResponse;

public class ResponseUtil
{
    private static final String VARY_HEADER = "vary";
    private static final String VARY_ALL = "*";
    
    private ResponseUtil() {
    }
    
    public static void addVaryFieldName(final MimeHeaders headers, final String name) {
        addVaryFieldName(new HeaderAdapter(headers), name);
    }
    
    public static void addVaryFieldName(final HttpServletResponse response, final String name) {
        addVaryFieldName(new ResponseAdapter(response), name);
    }
    
    private static void addVaryFieldName(final Adapter adapter, final String name) {
        final Collection<String> varyHeaders = adapter.getHeaders("vary");
        if (varyHeaders.size() == 1 && varyHeaders.iterator().next().trim().equals("*")) {
            return;
        }
        if (varyHeaders.size() == 0) {
            adapter.addHeader("vary", name);
            return;
        }
        if ("*".equals(name.trim())) {
            adapter.setHeader("vary", "*");
            return;
        }
        final LinkedHashSet<String> fieldNames = new LinkedHashSet<String>();
        for (final String varyHeader : varyHeaders) {
            final StringReader input = new StringReader(varyHeader);
            try {
                TokenList.parseTokenList(input, fieldNames);
            }
            catch (final IOException ex) {}
        }
        if (fieldNames.contains("*")) {
            adapter.setHeader("vary", "*");
            return;
        }
        fieldNames.add(name);
        final StringBuilder varyHeader2 = new StringBuilder();
        final Iterator<String> iter = fieldNames.iterator();
        varyHeader2.append(iter.next());
        while (iter.hasNext()) {
            varyHeader2.append(',');
            varyHeader2.append(iter.next());
        }
        adapter.setHeader("vary", varyHeader2.toString());
    }
    
    private static final class HeaderAdapter implements Adapter
    {
        private final MimeHeaders headers;
        
        public HeaderAdapter(final MimeHeaders headers) {
            this.headers = headers;
        }
        
        @Override
        public Collection<String> getHeaders(final String name) {
            final Enumeration<String> values = this.headers.values(name);
            final List<String> result = new ArrayList<String>();
            while (values.hasMoreElements()) {
                result.add(values.nextElement());
            }
            return result;
        }
        
        @Override
        public void setHeader(final String name, final String value) {
            this.headers.setValue(name).setString(value);
        }
        
        @Override
        public void addHeader(final String name, final String value) {
            this.headers.addValue(name).setString(value);
        }
    }
    
    private static final class ResponseAdapter implements Adapter
    {
        private final HttpServletResponse response;
        
        public ResponseAdapter(final HttpServletResponse response) {
            this.response = response;
        }
        
        @Override
        public Collection<String> getHeaders(final String name) {
            return this.response.getHeaders(name);
        }
        
        @Override
        public void setHeader(final String name, final String value) {
            this.response.setHeader(name, value);
        }
        
        @Override
        public void addHeader(final String name, final String value) {
            this.response.addHeader(name, value);
        }
    }
    
    private interface Adapter
    {
        Collection<String> getHeaders(final String p0);
        
        void setHeader(final String p0, final String p1);
        
        void addHeader(final String p0, final String p1);
    }
}
