package com.zoho.clustering.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Properties;

public class MyProperties
{
    private Properties properties;
    private boolean mutable;
    
    public MyProperties() {
        this.properties = new Properties();
        this.mutable = true;
    }
    
    public MyProperties(final String filePath) {
        this.properties = new Properties();
        this.mutable = true;
        this.loadAllFromFile(filePath);
    }
    
    public MyProperties(final File file) {
        this.properties = new Properties();
        this.mutable = true;
        this.loadAllFromFile(file);
    }
    
    public void makeImmutable() {
        this.mutable = false;
    }
    
    private void assertMutable() {
        if (!this.mutable) {
            throw new IllegalStateException("This MyProperties object is Not mutable");
        }
    }
    
    public String addEntry(final String key, final String value) {
        this.assertMutable();
        return (String)this.properties.setProperty(key, value);
    }
    
    public void loadAllFromFile(final String filePath) {
        this.loadAllFromFile(new File(filePath));
    }
    
    public void loadAllFromFile(final File file) {
        this.assertMutable();
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
            this.properties.load(fin);
        }
        catch (final IOException exp) {
            throw new RuntimeException("Properties loading failed", exp);
        }
        finally {
            FileUtil.Close(fin);
        }
    }
    
    public void loadAllFromStream(final InputStream inStream) {
        this.assertMutable();
        try {
            this.properties.load(inStream);
        }
        catch (final IOException exp) {
            throw new RuntimeException("Properties loading failed", exp);
        }
    }
    
    @Override
    public String toString() {
        return this.properties.toString();
    }
    
    public String optionalValue(final String key) {
        String value = System.getProperty(key);
        if (value == null) {
            value = this.properties.getProperty(key);
        }
        return (value != null) ? value.trim() : null;
    }
    
    public String value(final String key) {
        final String value = this.optionalValue(key);
        if (value == null || value.length() == 0) {
            throw new RuntimeException("The mandatory-property [" + key + "] is not configured");
        }
        return value;
    }
    
    public String value(final String key, final String defaultValue) {
        final String value = this.optionalValue(key);
        return (value != null && value.length() > 0) ? value : defaultValue;
    }
    
    public int intValue(final String key) {
        final String value = this.value(key);
        try {
            return Integer.parseInt(value);
        }
        catch (final NumberFormatException exp) {
            throw new RuntimeException("Expecting integer value for property [" + key + "].But the configured value is [" + value + "]");
        }
    }
    
    public int intValue(final String key, final int defaultValue) {
        final String value = this.optionalValue(key);
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        try {
            return (value.length() == 0) ? defaultValue : Integer.parseInt(value);
        }
        catch (final NumberFormatException exp) {
            throw new RuntimeException("Expecting integer value for property [" + key + "].But the configured value is [" + value + "]");
        }
    }
    
    public boolean boolValue(final String key) {
        final String value = this.optionalValue(key);
        if (value == null) {
            throw new RuntimeException("The mandatory-property [" + key + "] is not configured");
        }
        return value.length() == 0 || Boolean.valueOf(value.trim());
    }
    
    public boolean boolValue(final String key, final boolean defaultValue) {
        String value = this.optionalValue(key);
        if (value == null) {
            return defaultValue;
        }
        value = value.toLowerCase();
        if (value.length() == 0 || "true".equals(value)) {
            return true;
        }
        if ("false".equals(value)) {
            return false;
        }
        throw new RuntimeException("Expecting boolean value for property [" + key + "].But the configured value is [" + value + "]");
    }
}
