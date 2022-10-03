package org.apache.poi.hpsf;

import org.apache.poi.util.POILogFactory;
import java.nio.charset.Charset;
import java.io.UnsupportedEncodingException;
import org.apache.poi.util.CodePageUtil;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Date;
import java.math.BigInteger;
import org.apache.commons.collections4.bidimap.TreeBidiMap;
import java.util.HashMap;
import org.apache.poi.util.POILogger;
import java.util.Map;

public class CustomProperties implements Map<String, Object>
{
    private static final POILogger LOG;
    private final HashMap<Long, CustomProperty> props;
    private final TreeBidiMap<Long, String> dictionary;
    private boolean isPure;
    private int codepage;
    
    public CustomProperties() {
        this.props = new HashMap<Long, CustomProperty>();
        this.dictionary = (TreeBidiMap<Long, String>)new TreeBidiMap();
        this.isPure = true;
        this.codepage = -1;
    }
    
    public CustomProperty put(final String name, final CustomProperty cp) {
        if (name == null) {
            this.isPure = false;
            return null;
        }
        if (!name.equals(cp.getName())) {
            throw new IllegalArgumentException("Parameter \"name\" (" + name + ") and custom property's name (" + cp.getName() + ") do not match.");
        }
        this.checkCodePage(name);
        this.props.remove(this.dictionary.getKey((Object)name));
        this.dictionary.put((Comparable)cp.getID(), (Comparable)name);
        return this.props.put(cp.getID(), cp);
    }
    
    @Override
    public Object put(final String key, final Object value) {
        int variantType;
        if (value instanceof String) {
            variantType = 30;
        }
        else if (value instanceof Short) {
            variantType = 2;
        }
        else if (value instanceof Integer) {
            variantType = 3;
        }
        else if (value instanceof Long) {
            variantType = 20;
        }
        else if (value instanceof Float) {
            variantType = 4;
        }
        else if (value instanceof Double) {
            variantType = 5;
        }
        else if (value instanceof Boolean) {
            variantType = 11;
        }
        else if (value instanceof BigInteger && ((BigInteger)value).bitLength() <= 64 && ((BigInteger)value).compareTo(BigInteger.ZERO) >= 0) {
            variantType = 21;
        }
        else {
            if (!(value instanceof Date)) {
                throw new IllegalStateException("unsupported datatype - currently String,Short,Integer,Long,Float,Double,Boolean,BigInteger(unsigned long),Date can be processed.");
            }
            variantType = 64;
        }
        final Property p = new Property(-1L, variantType, value);
        return this.put(new CustomProperty(p, key));
    }
    
    @Override
    public Object get(final Object key) {
        final Long id = (Long)this.dictionary.getKey(key);
        final CustomProperty cp = this.props.get(id);
        return (cp != null) ? cp.getValue() : null;
    }
    
    @Override
    public CustomProperty remove(final Object key) {
        final Long id = (Long)this.dictionary.removeValue(key);
        return this.props.remove(id);
    }
    
    @Override
    public int size() {
        return this.props.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.props.isEmpty();
    }
    
    @Override
    public void clear() {
        this.props.clear();
    }
    
    @Override
    public int hashCode() {
        return this.props.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof CustomProperties && this.props.equals(((CustomProperties)obj).props);
    }
    
    @Override
    public void putAll(final Map<? extends String, ?> m) {
        for (final Entry<? extends String, ?> me : m.entrySet()) {
            this.put((String)me.getKey(), (Object)me.getValue());
        }
    }
    
    public List<CustomProperty> properties() {
        final List<CustomProperty> list = new ArrayList<CustomProperty>(this.props.size());
        for (final Long l : this.dictionary.keySet()) {
            list.add(this.props.get(l));
        }
        return Collections.unmodifiableList((List<? extends CustomProperty>)list);
    }
    
    @Override
    public Collection<Object> values() {
        final List<Object> list = new ArrayList<Object>(this.props.size());
        for (final Long l : this.dictionary.keySet()) {
            list.add(this.props.get(l).getValue());
        }
        return Collections.unmodifiableCollection((Collection<?>)list);
    }
    
    @Override
    public Set<Entry<String, Object>> entrySet() {
        final Map<String, Object> set = new LinkedHashMap<String, Object>(this.props.size());
        for (final Entry<Long, String> se : this.dictionary.entrySet()) {
            set.put(se.getValue(), this.props.get(se.getKey()).getValue());
        }
        return Collections.unmodifiableSet((Set<? extends Entry<String, Object>>)set.entrySet());
    }
    
    @Override
    public Set keySet() {
        return Collections.unmodifiableSet((Set<?>)this.dictionary.values());
    }
    
    public Set<String> nameSet() {
        return Collections.unmodifiableSet((Set<? extends String>)this.dictionary.values());
    }
    
    public Set<Long> idSet() {
        return Collections.unmodifiableSet((Set<? extends Long>)this.dictionary.keySet());
    }
    
    public void setCodepage(final int codepage) {
        this.codepage = codepage;
    }
    
    public int getCodepage() {
        return this.codepage;
    }
    
    Map<Long, String> getDictionary() {
        return (Map<Long, String>)this.dictionary;
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return (key instanceof Long && this.dictionary.containsKey(key)) || this.dictionary.containsValue(key);
    }
    
    @Override
    public boolean containsValue(final Object value) {
        if (value instanceof CustomProperty) {
            return this.props.containsValue(value);
        }
        for (final CustomProperty cp : this.props.values()) {
            if (cp.getValue() == value) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isPure() {
        return this.isPure;
    }
    
    public void setPure(final boolean isPure) {
        this.isPure = isPure;
    }
    
    private Object put(final CustomProperty customProperty) throws ClassCastException {
        final String name = customProperty.getName();
        final Long oldId = (name == null) ? null : ((Long)this.dictionary.getKey((Object)name));
        if (oldId != null) {
            customProperty.setID(oldId);
        }
        else {
            final long lastKey = (long)(this.dictionary.isEmpty() ? 0L : this.dictionary.lastKey());
            final long nextKey = Math.max(lastKey, 31L) + 1L;
            customProperty.setID(nextKey);
        }
        return this.put(name, customProperty);
    }
    
    private void checkCodePage(final String value) {
        int cp = this.getCodepage();
        if (cp == -1) {
            cp = 1252;
        }
        if (cp == 1200) {
            return;
        }
        String cps = "";
        try {
            cps = CodePageUtil.codepageToEncoding(cp, false);
        }
        catch (final UnsupportedEncodingException e) {
            CustomProperties.LOG.log(7, "Codepage '" + cp + "' can't be found.");
        }
        if (!cps.isEmpty() && Charset.forName(cps).newEncoder().canEncode(value)) {
            return;
        }
        CustomProperties.LOG.log(1, "Charset '" + cps + "' can't encode '" + value + "' - switching to unicode.");
        this.setCodepage(1200);
    }
    
    static {
        LOG = POILogFactory.getLogger(CustomProperties.class);
    }
}
