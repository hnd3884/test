package org.apache.commons.httpclient;

import org.apache.commons.logging.LogFactory;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.httpclient.util.ParameterParser;
import org.apache.commons.logging.Log;

public class HeaderElement extends NameValuePair
{
    private static final Log LOG;
    private NameValuePair[] parameters;
    
    public HeaderElement() {
        this(null, null, null);
    }
    
    public HeaderElement(final String name, final String value) {
        this(name, value, null);
    }
    
    public HeaderElement(final String name, final String value, final NameValuePair[] parameters) {
        super(name, value);
        this.parameters = null;
        this.parameters = parameters;
    }
    
    public HeaderElement(final char[] chars, final int offset, final int length) {
        this();
        if (chars == null) {
            return;
        }
        final ParameterParser parser = new ParameterParser();
        final List params = parser.parse(chars, offset, length, ';');
        if (params.size() > 0) {
            final NameValuePair element = params.remove(0);
            this.setName(element.getName());
            this.setValue(element.getValue());
            if (params.size() > 0) {
                this.parameters = params.toArray(new NameValuePair[params.size()]);
            }
        }
    }
    
    public HeaderElement(final char[] chars) {
        this(chars, 0, chars.length);
    }
    
    public NameValuePair[] getParameters() {
        return this.parameters;
    }
    
    public static final HeaderElement[] parseElements(final char[] headerValue) {
        HeaderElement.LOG.trace((Object)"enter HeaderElement.parseElements(char[])");
        if (headerValue == null) {
            return new HeaderElement[0];
        }
        final List elements = new ArrayList();
        int i = 0;
        int from = 0;
        final int len = headerValue.length;
        boolean qouted = false;
        while (i < len) {
            final char ch = headerValue[i];
            if (ch == '\"') {
                qouted = !qouted;
            }
            HeaderElement element = null;
            if (!qouted && ch == ',') {
                element = new HeaderElement(headerValue, from, i);
                from = i + 1;
            }
            else if (i == len - 1) {
                element = new HeaderElement(headerValue, from, len);
            }
            if (element != null && element.getName() != null) {
                elements.add(element);
            }
            ++i;
        }
        return elements.toArray(new HeaderElement[elements.size()]);
    }
    
    public static final HeaderElement[] parseElements(final String headerValue) {
        HeaderElement.LOG.trace((Object)"enter HeaderElement.parseElements(String)");
        if (headerValue == null) {
            return new HeaderElement[0];
        }
        return parseElements(headerValue.toCharArray());
    }
    
    public static final HeaderElement[] parse(final String headerValue) throws HttpException {
        HeaderElement.LOG.trace((Object)"enter HeaderElement.parse(String)");
        if (headerValue == null) {
            return new HeaderElement[0];
        }
        return parseElements(headerValue.toCharArray());
    }
    
    public NameValuePair getParameterByName(final String name) {
        HeaderElement.LOG.trace((Object)"enter HeaderElement.getParameterByName(String)");
        if (name == null) {
            throw new IllegalArgumentException("Name may not be null");
        }
        NameValuePair found = null;
        final NameValuePair[] parameters = this.getParameters();
        if (parameters != null) {
            for (int i = 0; i < parameters.length; ++i) {
                final NameValuePair current = parameters[i];
                if (current.getName().equalsIgnoreCase(name)) {
                    found = current;
                    break;
                }
            }
        }
        return found;
    }
    
    static {
        LOG = LogFactory.getLog(HeaderElement.class);
    }
}
