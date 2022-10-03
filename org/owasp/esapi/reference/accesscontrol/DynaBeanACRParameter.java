package org.owasp.esapi.reference.accesscontrol;

import java.util.Iterator;
import java.util.Date;
import java.math.BigInteger;
import java.math.BigDecimal;
import org.apache.commons.beanutils.LazyDynaMap;
import org.owasp.esapi.reference.accesscontrol.policyloader.PolicyParameters;

public class DynaBeanACRParameter implements PolicyParameters
{
    protected LazyDynaMap policyProperties;
    
    public DynaBeanACRParameter() {
        this.policyProperties = new LazyDynaMap();
    }
    
    @Override
    public Object get(final String key) {
        return this.policyProperties.get(key);
    }
    
    public boolean getBoolean(final String key) {
        return (boolean)this.get(key);
    }
    
    public byte getByte(final String key) {
        return (byte)this.get(key);
    }
    
    public char getChar(final String key) {
        return (char)this.get(key);
    }
    
    public int getInt(final String key) {
        return (int)this.get(key);
    }
    
    public long getLong(final String key) {
        return (long)this.get(key);
    }
    
    public float getFloat(final String key) {
        return (float)this.get(key);
    }
    
    public double getDouble(final String key) {
        return (double)this.get(key);
    }
    
    public BigDecimal getBigDecimal(final String key) {
        return (BigDecimal)this.get(key);
    }
    
    public BigInteger getBigInteger(final String key) {
        return (BigInteger)this.get(key);
    }
    
    public Date getDate(final String key) {
        return (Date)this.get(key);
    }
    
    public Date getTime(final String key) {
        return (Date)this.get(key);
    }
    
    public String getString(final String key) {
        return (String)this.get(key);
    }
    
    public String getString(final String key, final String defaultValue) {
        return (String)((this.get(key) == null) ? defaultValue : this.get(key));
    }
    
    public String[] getStringArray(final String key) {
        return (String[])this.get(key);
    }
    
    public Object getObject(final String key) {
        return this.get(key);
    }
    
    @Override
    public void set(final String key, final Object value) throws IllegalArgumentException {
        this.policyProperties.set(key, value);
    }
    
    @Override
    public void put(final String key, final Object value) throws IllegalArgumentException {
        this.set(key, value);
    }
    
    @Override
    public void lock() {
        this.policyProperties.setRestricted(true);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final Iterator keys = this.policyProperties.getMap().keySet().iterator();
        while (keys.hasNext()) {
            final String currentKey = keys.next();
            sb.append(currentKey);
            sb.append("=");
            sb.append(this.policyProperties.get(currentKey));
            if (keys.hasNext()) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}
