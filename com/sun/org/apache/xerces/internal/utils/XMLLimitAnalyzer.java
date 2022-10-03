package com.sun.org.apache.xerces.internal.utils;

import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

public final class XMLLimitAnalyzer
{
    private final int[] values;
    private final String[] names;
    private final int[] totalValue;
    private final Map[] caches;
    private String entityStart;
    private String entityEnd;
    
    public XMLLimitAnalyzer() {
        this.values = new int[XMLSecurityManager.Limit.values().length];
        this.totalValue = new int[XMLSecurityManager.Limit.values().length];
        this.names = new String[XMLSecurityManager.Limit.values().length];
        this.caches = new Map[XMLSecurityManager.Limit.values().length];
    }
    
    public void addValue(final XMLSecurityManager.Limit limit, final String entityName, final int value) {
        this.addValue(limit.ordinal(), entityName, value);
    }
    
    public void addValue(final int index, final String entityName, final int value) {
        if (index == XMLSecurityManager.Limit.ENTITY_EXPANSION_LIMIT.ordinal() || index == XMLSecurityManager.Limit.MAX_OCCUR_NODE_LIMIT.ordinal() || index == XMLSecurityManager.Limit.ELEMENT_ATTRIBUTE_LIMIT.ordinal() || index == XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT.ordinal() || index == XMLSecurityManager.Limit.ENTITY_REPLACEMENT_LIMIT.ordinal()) {
            final int[] totalValue = this.totalValue;
            totalValue[index] += value;
            return;
        }
        if (index == XMLSecurityManager.Limit.MAX_ELEMENT_DEPTH_LIMIT.ordinal() || index == XMLSecurityManager.Limit.MAX_NAME_LIMIT.ordinal()) {
            this.values[index] = value;
            this.totalValue[index] = value;
            return;
        }
        Map<String, Integer> cache;
        if (this.caches[index] == null) {
            cache = new HashMap<String, Integer>(10);
            this.caches[index] = cache;
        }
        else {
            cache = this.caches[index];
        }
        int accumulatedValue = value;
        if (cache.containsKey(entityName)) {
            accumulatedValue += cache.get(entityName);
            cache.put(entityName, accumulatedValue);
        }
        else {
            cache.put(entityName, value);
        }
        if (accumulatedValue > this.values[index]) {
            this.values[index] = accumulatedValue;
            this.names[index] = entityName;
        }
        if (index == XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT.ordinal() || index == XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT.ordinal()) {
            final int[] totalValue2 = this.totalValue;
            final int ordinal = XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT.ordinal();
            totalValue2[ordinal] += value;
        }
    }
    
    public int getValue(final XMLSecurityManager.Limit limit) {
        return this.getValue(limit.ordinal());
    }
    
    public int getValue(final int index) {
        if (index == XMLSecurityManager.Limit.ENTITY_REPLACEMENT_LIMIT.ordinal()) {
            return this.totalValue[index];
        }
        return this.values[index];
    }
    
    public int getTotalValue(final XMLSecurityManager.Limit limit) {
        return this.totalValue[limit.ordinal()];
    }
    
    public int getTotalValue(final int index) {
        return this.totalValue[index];
    }
    
    public int getValueByIndex(final int index) {
        return this.values[index];
    }
    
    public void startEntity(final String name) {
        this.entityStart = name;
    }
    
    public boolean isTracking(final String name) {
        return this.entityStart != null && this.entityStart.equals(name);
    }
    
    public void endEntity(final XMLSecurityManager.Limit limit, final String name) {
        this.entityStart = "";
        final Map<String, Integer> cache = this.caches[limit.ordinal()];
        if (cache != null) {
            cache.remove(name);
        }
    }
    
    public void reset(final XMLSecurityManager.Limit limit) {
        if (limit.ordinal() == XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT.ordinal()) {
            this.totalValue[limit.ordinal()] = 0;
        }
        else if (limit.ordinal() == XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT.ordinal()) {
            this.names[limit.ordinal()] = null;
            this.values[limit.ordinal()] = 0;
            this.caches[limit.ordinal()] = null;
            this.totalValue[limit.ordinal()] = 0;
        }
    }
    
    public void debugPrint(final XMLSecurityManager securityManager) {
        Formatter formatter = new Formatter();
        System.out.println(formatter.format("%30s %15s %15s %15s %30s", "Property", "Limit", "Total size", "Size", "Entity Name"));
        for (final XMLSecurityManager.Limit limit : XMLSecurityManager.Limit.values()) {
            formatter = new Formatter();
            System.out.println(formatter.format("%30s %15d %15d %15d %30s", limit.name(), securityManager.getLimit(limit), this.totalValue[limit.ordinal()], this.values[limit.ordinal()], this.names[limit.ordinal()]));
        }
    }
    
    public enum NameMap
    {
        ENTITY_EXPANSION_LIMIT("jdk.xml.entityExpansionLimit", "entityExpansionLimit"), 
        MAX_OCCUR_NODE_LIMIT("jdk.xml.maxOccurLimit", "maxOccurLimit"), 
        ELEMENT_ATTRIBUTE_LIMIT("jdk.xml.elementAttributeLimit", "elementAttributeLimit");
        
        final String newName;
        final String oldName;
        
        private NameMap(final String newName, final String oldName) {
            this.newName = newName;
            this.oldName = oldName;
        }
        
        String getOldName(final String newName) {
            if (newName.equals(this.newName)) {
                return this.oldName;
            }
            return null;
        }
    }
}
