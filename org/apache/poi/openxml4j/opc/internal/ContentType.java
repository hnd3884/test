package org.apache.poi.openxml4j.opc.internal;

import java.util.Objects;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.Collections;
import java.util.HashMap;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.util.regex.Pattern;
import java.util.Map;

public final class ContentType
{
    private final String type;
    private final String subType;
    private final Map<String, String> parameters;
    private static final Pattern patternTypeSubType;
    private static final Pattern patternTypeSubTypeParams;
    private static final Pattern patternParams;
    
    public ContentType(final String contentType) throws InvalidFormatException {
        Matcher mMediaType = ContentType.patternTypeSubType.matcher(contentType);
        if (!mMediaType.matches()) {
            mMediaType = ContentType.patternTypeSubTypeParams.matcher(contentType);
        }
        if (!mMediaType.matches()) {
            throw new InvalidFormatException("The specified content type '" + contentType + "' is not compliant with RFC 2616: malformed content type.");
        }
        if (mMediaType.groupCount() >= 2) {
            this.type = mMediaType.group(1);
            this.subType = mMediaType.group(2);
            this.parameters = new HashMap<String, String>();
            if (mMediaType.groupCount() >= 5) {
                final Matcher mParams = ContentType.patternParams.matcher(contentType.substring(mMediaType.end(2)));
                while (mParams.find()) {
                    this.parameters.put(mParams.group(1), mParams.group(2));
                }
            }
        }
        else {
            this.type = "";
            this.subType = "";
            this.parameters = Collections.emptyMap();
        }
    }
    
    @Override
    public final String toString() {
        return this.toString(true);
    }
    
    public final String toString(final boolean withParameters) {
        final StringBuilder retVal = new StringBuilder(64);
        retVal.append(this.getType());
        retVal.append('/');
        retVal.append(this.getSubType());
        if (withParameters) {
            for (final Map.Entry<String, String> me : this.parameters.entrySet()) {
                retVal.append(';');
                retVal.append(me.getKey());
                retVal.append('=');
                retVal.append(me.getValue());
            }
        }
        return retVal.toString();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return !(obj instanceof ContentType) || this.toString().equalsIgnoreCase(obj.toString());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.subType, this.parameters);
    }
    
    public String getSubType() {
        return this.subType;
    }
    
    public String getType() {
        return this.type;
    }
    
    public boolean hasParameters() {
        return this.parameters != null && !this.parameters.isEmpty();
    }
    
    public String[] getParameterKeys() {
        if (this.parameters == null) {
            return new String[0];
        }
        return this.parameters.keySet().toArray(new String[0]);
    }
    
    public String getParameter(final String key) {
        return this.parameters.get(key);
    }
    
    static {
        final String token = "[\\x21-\\x7E&&[^()<>@,;:\\\\/\"\\[\\]?={}\\x20\\x09]]";
        final String parameter = "(" + token + "+)=(\"?" + token + "+\"?)";
        patternTypeSubType = Pattern.compile("^(" + token + "+)/(" + token + "+)$");
        patternTypeSubTypeParams = Pattern.compile("^(" + token + "+)/(" + token + "+)(;" + parameter + ")*$");
        patternParams = Pattern.compile(";" + parameter);
    }
}
