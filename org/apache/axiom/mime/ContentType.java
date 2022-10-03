package org.apache.axiom.mime;

import java.util.Locale;
import java.util.Iterator;
import java.util.Map;
import java.text.ParseException;
import java.util.List;
import java.util.ArrayList;

public final class ContentType
{
    private final MediaType mediaType;
    private final String[] parameters;
    
    public ContentType(final MediaType mediaType, final String[] parameters) {
        this.mediaType = mediaType;
        this.parameters = parameters.clone();
    }
    
    public ContentType(final String type) throws ParseException {
        final ContentTypeTokenizer tokenizer = new ContentTypeTokenizer(type);
        final String primaryType = tokenizer.requireToken();
        tokenizer.require('/');
        final String subType = tokenizer.requireToken();
        this.mediaType = new MediaType(primaryType, subType);
        final List<String> parameters = new ArrayList<String>();
        while (tokenizer.expect(';')) {
            final String name = tokenizer.expectToken();
            if (name == null) {
                break;
            }
            parameters.add(name);
            tokenizer.require('=');
            parameters.add(tokenizer.requireTokenOrQuotedString());
        }
        this.parameters = parameters.toArray(new String[parameters.size()]);
    }
    
    ContentType(final MediaType mediaType, final Map<String, String> parameters) {
        this.mediaType = mediaType;
        this.parameters = new String[parameters.size() * 2];
        int i = 0;
        for (final Map.Entry<String, String> entry : parameters.entrySet()) {
            this.parameters[i++] = entry.getKey();
            this.parameters[i++] = entry.getValue();
        }
    }
    
    public MediaType getMediaType() {
        return this.mediaType;
    }
    
    public String getParameter(final String name) {
        for (int i = 0; i < this.parameters.length; i += 2) {
            if (name.equalsIgnoreCase(this.parameters[i])) {
                return this.parameters[i + 1];
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(this.mediaType.getPrimaryType());
        buffer.append('/');
        buffer.append(this.mediaType.getSubType());
        int i = 0;
        while (i < this.parameters.length) {
            buffer.append("; ");
            buffer.append(this.parameters[i++]);
            buffer.append("=\"");
            final String value = this.parameters[i++];
            for (int j = 0, l = value.length(); j < l; ++j) {
                final char c = value.charAt(j);
                if (c == '\"' || c == '\\') {
                    buffer.append('\\');
                }
                buffer.append(c);
            }
            buffer.append('\"');
        }
        return buffer.toString();
    }
    
    void getParameters(final Map<String, String> map) {
        for (int i = 0; i < this.parameters.length; i += 2) {
            map.put(this.parameters[i].toLowerCase(Locale.ENGLISH), this.parameters[i + 1]);
        }
    }
}
