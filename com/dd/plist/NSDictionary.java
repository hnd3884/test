package com.dd.plist;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Map;

public class NSDictionary extends NSObject implements Map<String, NSObject>
{
    private final HashMap<String, NSObject> dict;
    
    public NSDictionary() {
        this.dict = new LinkedHashMap<String, NSObject>();
    }
    
    public HashMap<String, NSObject> getHashMap() {
        return this.dict;
    }
    
    public NSObject objectForKey(final String key) {
        return this.dict.get(key);
    }
    
    @Override
    public int size() {
        return this.dict.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.dict.isEmpty();
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return this.dict.containsKey(key);
    }
    
    @Override
    public boolean containsValue(final Object value) {
        if (value == null) {
            return false;
        }
        final NSObject wrap = NSObject.fromJavaObject(value);
        return this.dict.containsValue(wrap);
    }
    
    @Override
    public NSObject get(final Object key) {
        return this.dict.get(key);
    }
    
    @Override
    public void putAll(final Map<? extends String, ? extends NSObject> values) {
        for (final Object object : values.entrySet()) {
            final Entry<String, NSObject> entry = (Entry<String, NSObject>)object;
            this.put(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public NSObject put(final String key, final NSObject obj) {
        if (key == null) {
            return null;
        }
        if (obj == null) {
            return this.dict.get(key);
        }
        return this.dict.put(key, obj);
    }
    
    public NSObject put(final String key, final Object obj) {
        return this.put(key, NSObject.fromJavaObject(obj));
    }
    
    public NSObject remove(final String key) {
        return this.dict.remove(key);
    }
    
    @Override
    public NSObject remove(final Object key) {
        return this.dict.remove(key);
    }
    
    @Override
    public void clear() {
        this.dict.clear();
    }
    
    @Override
    public Set<String> keySet() {
        return this.dict.keySet();
    }
    
    @Override
    public Collection<NSObject> values() {
        return this.dict.values();
    }
    
    @Override
    public Set<Entry<String, NSObject>> entrySet() {
        return this.dict.entrySet();
    }
    
    public boolean containsKey(final String key) {
        return this.dict.containsKey(key);
    }
    
    public boolean containsValue(final NSObject val) {
        return val != null && this.dict.containsValue(val);
    }
    
    public boolean containsValue(final String val) {
        for (final NSObject o : this.dict.values()) {
            if (o.getClass().equals(NSString.class)) {
                final NSString str = (NSString)o;
                if (str.getContent().equals(val)) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    public boolean containsValue(final long val) {
        for (final NSObject o : this.dict.values()) {
            if (o.getClass().equals(NSNumber.class)) {
                final NSNumber num = (NSNumber)o;
                if (num.isInteger() && num.intValue() == val) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    public boolean containsValue(final double val) {
        for (final NSObject o : this.dict.values()) {
            if (o.getClass().equals(NSNumber.class)) {
                final NSNumber num = (NSNumber)o;
                if (num.isReal() && num.doubleValue() == val) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    public boolean containsValue(final boolean val) {
        for (final NSObject o : this.dict.values()) {
            if (o.getClass().equals(NSNumber.class)) {
                final NSNumber num = (NSNumber)o;
                if (num.isBoolean() && num.boolValue() == val) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    public boolean containsValue(final Date val) {
        for (final NSObject o : this.dict.values()) {
            if (o.getClass().equals(NSDate.class)) {
                final NSDate dat = (NSDate)o;
                if (dat.getDate().equals(val)) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    public boolean containsValue(final byte[] val) {
        for (final NSObject o : this.dict.values()) {
            if (o.getClass().equals(NSData.class)) {
                final NSData dat = (NSData)o;
                if (Arrays.equals(dat.bytes(), val)) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    public int count() {
        return this.dict.size();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj.getClass().equals(this.getClass()) && ((NSDictionary)obj).dict.equals(this.dict);
    }
    
    public String[] allKeys() {
        return this.dict.keySet().toArray(new String[this.count()]);
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + ((this.dict != null) ? this.dict.hashCode() : 0);
        return hash;
    }
    
    @Override
    void toXML(final StringBuilder xml, final int level) {
        this.indent(xml, level);
        xml.append("<dict>");
        xml.append(NSObject.NEWLINE);
        for (final String key : this.dict.keySet()) {
            final NSObject val = this.objectForKey(key);
            this.indent(xml, level + 1);
            xml.append("<key>");
            if (key.contains("&") || key.contains("<") || key.contains(">")) {
                xml.append("<![CDATA[");
                xml.append(key.replaceAll("]]>", "]]]]><![CDATA[>"));
                xml.append("]]>");
            }
            else {
                xml.append(key);
            }
            xml.append("</key>");
            xml.append(NSObject.NEWLINE);
            val.toXML(xml, level + 1);
            xml.append(NSObject.NEWLINE);
        }
        this.indent(xml, level);
        xml.append("</dict>");
    }
    
    @Override
    void assignIDs(final BinaryPropertyListWriter out) {
        super.assignIDs(out);
        for (final Entry<String, NSObject> entry : this.dict.entrySet()) {
            new NSString(entry.getKey()).assignIDs(out);
        }
        for (final Entry<String, NSObject> entry : this.dict.entrySet()) {
            entry.getValue().assignIDs(out);
        }
    }
    
    @Override
    void toBinary(final BinaryPropertyListWriter out) throws IOException {
        out.writeIntHeader(13, this.dict.size());
        final Set<Entry<String, NSObject>> entries = this.dict.entrySet();
        for (final Entry<String, NSObject> entry : entries) {
            out.writeID(out.getID(new NSString(entry.getKey())));
        }
        for (final Entry<String, NSObject> entry : entries) {
            out.writeID(out.getID(entry.getValue()));
        }
    }
    
    public String toASCIIPropertyList() {
        final StringBuilder ascii = new StringBuilder();
        this.toASCII(ascii, 0);
        ascii.append(NSDictionary.NEWLINE);
        return ascii.toString();
    }
    
    public String toGnuStepASCIIPropertyList() {
        final StringBuilder ascii = new StringBuilder();
        this.toASCIIGnuStep(ascii, 0);
        ascii.append(NSDictionary.NEWLINE);
        return ascii.toString();
    }
    
    @Override
    protected void toASCII(final StringBuilder ascii, final int level) {
        this.indent(ascii, level);
        ascii.append('{');
        ascii.append(NSDictionary.NEWLINE);
        final String[] allKeys;
        final String[] keys = allKeys = this.allKeys();
        for (final String key : allKeys) {
            final NSObject val = this.objectForKey(key);
            this.indent(ascii, level + 1);
            ascii.append('\"');
            ascii.append(NSString.escapeStringForASCII(key));
            ascii.append("\" =");
            final Class<?> objClass = val.getClass();
            if (objClass.equals(NSDictionary.class) || objClass.equals(NSArray.class) || objClass.equals(NSData.class)) {
                ascii.append(NSDictionary.NEWLINE);
                val.toASCII(ascii, level + 2);
            }
            else {
                ascii.append(' ');
                val.toASCII(ascii, 0);
            }
            ascii.append(';');
            ascii.append(NSDictionary.NEWLINE);
        }
        this.indent(ascii, level);
        ascii.append('}');
    }
    
    @Override
    protected void toASCIIGnuStep(final StringBuilder ascii, final int level) {
        this.indent(ascii, level);
        ascii.append('{');
        ascii.append(NSDictionary.NEWLINE);
        final String[] array;
        final String[] keys = array = this.dict.keySet().toArray(new String[this.dict.size()]);
        for (final String key : array) {
            final NSObject val = this.objectForKey(key);
            this.indent(ascii, level + 1);
            ascii.append('\"');
            ascii.append(NSString.escapeStringForASCII(key));
            ascii.append("\" =");
            final Class<?> objClass = val.getClass();
            if (objClass.equals(NSDictionary.class) || objClass.equals(NSArray.class) || objClass.equals(NSData.class)) {
                ascii.append(NSDictionary.NEWLINE);
                val.toASCIIGnuStep(ascii, level + 2);
            }
            else {
                ascii.append(' ');
                val.toASCIIGnuStep(ascii, 0);
            }
            ascii.append(';');
            ascii.append(NSDictionary.NEWLINE);
        }
        this.indent(ascii, level);
        ascii.append('}');
    }
}
