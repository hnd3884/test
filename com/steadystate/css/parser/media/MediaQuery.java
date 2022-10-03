package com.steadystate.css.parser.media;

import java.util.Iterator;
import com.steadystate.css.format.CSSFormat;
import java.util.ArrayList;
import com.steadystate.css.dom.Property;
import java.util.List;
import java.io.Serializable;
import com.steadystate.css.format.CSSFormatable;
import com.steadystate.css.parser.LocatableImpl;

public class MediaQuery extends LocatableImpl implements CSSFormatable, Serializable
{
    private static final long serialVersionUID = 456776383828897471L;
    private String media_;
    private List<Property> properties_;
    private boolean isOnly_;
    private boolean isNot_;
    
    public MediaQuery(final String media) {
        this(media, false, false);
    }
    
    public MediaQuery(final String media, final boolean isOnly, final boolean isNot) {
        this.setMedia(media);
        this.properties_ = new ArrayList<Property>(10);
        this.isOnly_ = isOnly;
        this.isNot_ = isNot;
    }
    
    public String getMedia() {
        return this.media_;
    }
    
    public void setMedia(final String media) {
        this.media_ = media;
    }
    
    public List<Property> getProperties() {
        return this.properties_;
    }
    
    public void addMediaProperty(final Property mp) {
        this.properties_.add(mp);
    }
    
    public boolean isOnly() {
        return this.isOnly_;
    }
    
    public boolean isNot() {
        return this.isNot_;
    }
    
    public String getCssText(final CSSFormat format) {
        final StringBuilder sb = new StringBuilder();
        if (this.isOnly_) {
            sb.append("only ");
        }
        if (this.isNot_) {
            sb.append("not ");
        }
        sb.append(this.getMedia());
        for (final Property prop : this.properties_) {
            sb.append(" and (").append(prop.getCssText(format)).append(')');
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return this.getCssText(null);
    }
}
