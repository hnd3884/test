package org.glassfish.jersey.message.internal;

import java.util.Locale;
import java.text.ParseException;

public class LanguageTag
{
    String tag;
    String primaryTag;
    String subTags;
    
    protected LanguageTag() {
    }
    
    public static LanguageTag valueOf(final String s) throws IllegalArgumentException {
        final LanguageTag lt = new LanguageTag();
        try {
            lt.parse(s);
        }
        catch (final ParseException pe) {
            throw new IllegalArgumentException(pe);
        }
        return lt;
    }
    
    public LanguageTag(final String primaryTag, final String subTags) {
        if (subTags != null && subTags.length() > 0) {
            this.tag = primaryTag + "-" + subTags;
        }
        else {
            this.tag = primaryTag;
        }
        this.primaryTag = primaryTag;
        this.subTags = subTags;
    }
    
    public LanguageTag(final String header) throws ParseException {
        this(HttpHeaderReader.newInstance(header));
    }
    
    public LanguageTag(final HttpHeaderReader reader) throws ParseException {
        reader.hasNext();
        this.tag = reader.nextToken().toString();
        if (reader.hasNext()) {
            throw new ParseException("Invalid Language tag", reader.getIndex());
        }
        this.parse(this.tag);
    }
    
    public final boolean isCompatible(final Locale tag) {
        if (this.tag.equals("*")) {
            return true;
        }
        if (this.subTags == null) {
            return this.primaryTag.equalsIgnoreCase(tag.getLanguage());
        }
        return this.primaryTag.equalsIgnoreCase(tag.getLanguage()) && this.subTags.equalsIgnoreCase(tag.getCountry());
    }
    
    public final Locale getAsLocale() {
        return (this.subTags == null) ? new Locale(this.primaryTag) : new Locale(this.primaryTag, this.subTags);
    }
    
    protected final void parse(final String languageTag) throws ParseException {
        if (!this.isValid(languageTag)) {
            throw new ParseException("String, " + languageTag + ", is not a valid language tag", 0);
        }
        final int index = languageTag.indexOf(45);
        if (index == -1) {
            this.primaryTag = languageTag;
            this.subTags = null;
        }
        else {
            this.primaryTag = languageTag.substring(0, index);
            this.subTags = languageTag.substring(index + 1, languageTag.length());
        }
    }
    
    private boolean isValid(final String tag) {
        int alphanumCount = 0;
        int dash = 0;
        for (int i = 0; i < tag.length(); ++i) {
            final char c = tag.charAt(i);
            if (c == '-') {
                if (alphanumCount == 0) {
                    return false;
                }
                alphanumCount = 0;
                ++dash;
            }
            else {
                if (('A' > c || c > 'Z') && ('a' > c || c > 'z') && (dash <= 0 || '0' > c || c > '9')) {
                    return false;
                }
                if (++alphanumCount > 8) {
                    return false;
                }
            }
        }
        return alphanumCount != 0;
    }
    
    public final String getTag() {
        return this.tag;
    }
    
    public final String getPrimaryTag() {
        return this.primaryTag;
    }
    
    public final String getSubTags() {
        return this.subTags;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LanguageTag) || o.getClass() != this.getClass()) {
            return false;
        }
        final LanguageTag that = (LanguageTag)o;
        Label_0065: {
            if (this.primaryTag != null) {
                if (this.primaryTag.equals(that.primaryTag)) {
                    break Label_0065;
                }
            }
            else if (that.primaryTag == null) {
                break Label_0065;
            }
            return false;
        }
        Label_0098: {
            if (this.subTags != null) {
                if (this.subTags.equals(that.subTags)) {
                    break Label_0098;
                }
            }
            else if (that.subTags == null) {
                break Label_0098;
            }
            return false;
        }
        if (this.tag != null) {
            if (!this.tag.equals(that.tag)) {
                return false;
            }
        }
        else if (that.tag != null) {
            return false;
        }
        return true;
        b = false;
        return b;
    }
    
    @Override
    public int hashCode() {
        int result = (this.tag != null) ? this.tag.hashCode() : 0;
        result = 31 * result + ((this.primaryTag != null) ? this.primaryTag.hashCode() : 0);
        result = 31 * result + ((this.subTags != null) ? this.subTags.hashCode() : 0);
        return result;
    }
    
    @Override
    public String toString() {
        return this.primaryTag + ((this.subTags == null) ? "" : this.subTags);
    }
}
