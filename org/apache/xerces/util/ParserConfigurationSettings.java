package org.apache.xerces.util;

import org.apache.xerces.xni.parser.XMLConfigurationException;
import java.util.HashMap;
import java.util.ArrayList;
import org.apache.xerces.xni.parser.XMLComponentManager;

public class ParserConfigurationSettings implements XMLComponentManager
{
    protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
    protected ArrayList fRecognizedProperties;
    protected HashMap fProperties;
    protected ArrayList fRecognizedFeatures;
    protected HashMap fFeatures;
    protected XMLComponentManager fParentSettings;
    
    public ParserConfigurationSettings() {
        this(null);
    }
    
    public ParserConfigurationSettings(final XMLComponentManager fParentSettings) {
        this.fRecognizedFeatures = new ArrayList();
        this.fRecognizedProperties = new ArrayList();
        this.fFeatures = new HashMap();
        this.fProperties = new HashMap();
        this.fParentSettings = fParentSettings;
    }
    
    public void addRecognizedFeatures(final String[] array) {
        for (int n = (array != null) ? array.length : 0, i = 0; i < n; ++i) {
            final String s = array[i];
            if (!this.fRecognizedFeatures.contains(s)) {
                this.fRecognizedFeatures.add(s);
            }
        }
    }
    
    public void setFeature(final String s, final boolean b) throws XMLConfigurationException {
        this.checkFeature(s);
        this.fFeatures.put(s, b ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void addRecognizedProperties(final String[] array) {
        for (int n = (array != null) ? array.length : 0, i = 0; i < n; ++i) {
            final String s = array[i];
            if (!this.fRecognizedProperties.contains(s)) {
                this.fRecognizedProperties.add(s);
            }
        }
    }
    
    public void setProperty(final String s, final Object o) throws XMLConfigurationException {
        this.checkProperty(s);
        this.fProperties.put(s, o);
    }
    
    public boolean getFeature(final String s) throws XMLConfigurationException {
        final Boolean b = this.fFeatures.get(s);
        if (b == null) {
            this.checkFeature(s);
            return false;
        }
        return b;
    }
    
    public Object getProperty(final String s) throws XMLConfigurationException {
        final Object value = this.fProperties.get(s);
        if (value == null) {
            this.checkProperty(s);
        }
        return value;
    }
    
    protected void checkFeature(final String s) throws XMLConfigurationException {
        if (!this.fRecognizedFeatures.contains(s)) {
            if (this.fParentSettings == null) {
                throw new XMLConfigurationException((short)0, s);
            }
            this.fParentSettings.getFeature(s);
        }
    }
    
    protected void checkProperty(final String s) throws XMLConfigurationException {
        if (!this.fRecognizedProperties.contains(s)) {
            if (this.fParentSettings == null) {
                throw new XMLConfigurationException((short)0, s);
            }
            this.fParentSettings.getProperty(s);
        }
    }
}
