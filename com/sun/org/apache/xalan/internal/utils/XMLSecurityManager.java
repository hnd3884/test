package com.sun.org.apache.xalan.internal.utils;

import org.xml.sax.SAXException;
import java.util.concurrent.CopyOnWriteArrayList;

public final class XMLSecurityManager
{
    private final int[] values;
    private State[] states;
    private boolean[] isSet;
    private final int indexEntityCountInfo = 10000;
    private String printEntityCountInfo;
    private static final CopyOnWriteArrayList<String> printedWarnings;
    
    public XMLSecurityManager() {
        this(false);
    }
    
    public XMLSecurityManager(final boolean secureProcessing) {
        this.printEntityCountInfo = "";
        this.values = new int[Limit.values().length];
        this.states = new State[Limit.values().length];
        this.isSet = new boolean[Limit.values().length];
        for (final Limit limit : Limit.values()) {
            if (secureProcessing) {
                this.values[limit.ordinal()] = limit.secureValue();
                this.states[limit.ordinal()] = State.FSP;
            }
            else {
                this.values[limit.ordinal()] = limit.defaultValue();
                this.states[limit.ordinal()] = State.DEFAULT;
            }
        }
        this.readSystemProperties();
    }
    
    public void setSecureProcessing(final boolean secure) {
        for (final Limit limit : Limit.values()) {
            if (secure) {
                this.setLimit(limit.ordinal(), State.FSP, limit.secureValue());
            }
            else {
                this.setLimit(limit.ordinal(), State.FSP, limit.defaultValue());
            }
        }
    }
    
    public boolean setLimit(final String propertyName, final State state, final Object value) {
        final int index = this.getIndex(propertyName);
        if (index > -1) {
            this.setLimit(index, state, value);
            return true;
        }
        return false;
    }
    
    public void setLimit(final Limit limit, final State state, final int value) {
        this.setLimit(limit.ordinal(), state, value);
    }
    
    public void setLimit(final int index, final State state, final Object value) {
        if (index == 10000) {
            this.printEntityCountInfo = (String)value;
        }
        else {
            int temp = 0;
            try {
                temp = Integer.parseInt((String)value);
                if (temp < 0) {
                    temp = 0;
                }
            }
            catch (final NumberFormatException ex) {}
            this.setLimit(index, state, temp);
        }
    }
    
    public void setLimit(final int index, final State state, final int value) {
        if (index == 10000) {
            this.printEntityCountInfo = "yes";
        }
        else if (state.compareTo(this.states[index]) >= 0) {
            this.values[index] = value;
            this.states[index] = state;
            this.isSet[index] = true;
        }
    }
    
    public String getLimitAsString(final String propertyName) {
        final int index = this.getIndex(propertyName);
        if (index > -1) {
            return this.getLimitValueByIndex(index);
        }
        return null;
    }
    
    public String getLimitValueAsString(final Limit limit) {
        return Integer.toString(this.values[limit.ordinal()]);
    }
    
    public int getLimit(final Limit limit) {
        return this.values[limit.ordinal()];
    }
    
    public int getLimitByIndex(final int index) {
        return this.values[index];
    }
    
    public String getLimitValueByIndex(final int index) {
        if (index == 10000) {
            return this.printEntityCountInfo;
        }
        return Integer.toString(this.values[index]);
    }
    
    public State getState(final Limit limit) {
        return this.states[limit.ordinal()];
    }
    
    public String getStateLiteral(final Limit limit) {
        return this.states[limit.ordinal()].literal();
    }
    
    public int getIndex(final String propertyName) {
        for (final Limit limit : Limit.values()) {
            if (limit.equalsAPIPropertyName(propertyName)) {
                return limit.ordinal();
            }
        }
        if (propertyName.equals("http://www.oracle.com/xml/jaxp/properties/getEntityCountInfo")) {
            return 10000;
        }
        return -1;
    }
    
    public boolean isSet(final int index) {
        return this.isSet[index];
    }
    
    public boolean printEntityCountInfo() {
        return this.printEntityCountInfo.equals("yes");
    }
    
    private void readSystemProperties() {
        for (final Limit limit : Limit.values()) {
            if (!this.getSystemProperty(limit, limit.systemProperty())) {
                for (final NameMap nameMap : NameMap.values()) {
                    final String oldName = nameMap.getOldName(limit.systemProperty());
                    if (oldName != null) {
                        this.getSystemProperty(limit, oldName);
                    }
                }
            }
        }
    }
    
    public static void printWarning(final String parserClassName, final String propertyName, final SAXException exception) {
        final String key = parserClassName + ":" + propertyName;
        if (XMLSecurityManager.printedWarnings.addIfAbsent(key)) {
            System.err.println("Warning: " + parserClassName + ": " + exception.getMessage());
        }
    }
    
    private boolean getSystemProperty(final Limit limit, final String sysPropertyName) {
        try {
            String value = SecuritySupport.getSystemProperty(sysPropertyName);
            if (value != null && !value.equals("")) {
                this.values[limit.ordinal()] = Integer.parseInt(value);
                this.states[limit.ordinal()] = State.SYSTEMPROPERTY;
                return true;
            }
            value = SecuritySupport.readJAXPProperty(sysPropertyName);
            if (value != null && !value.equals("")) {
                this.values[limit.ordinal()] = Integer.parseInt(value);
                this.states[limit.ordinal()] = State.JAXPDOTPROPERTIES;
                return true;
            }
        }
        catch (final NumberFormatException e) {
            throw new NumberFormatException("Invalid setting for system property: " + limit.systemProperty());
        }
        return false;
    }
    
    static {
        printedWarnings = new CopyOnWriteArrayList<String>();
    }
    
    public enum State
    {
        DEFAULT("default"), 
        FSP("FEATURE_SECURE_PROCESSING"), 
        JAXPDOTPROPERTIES("jaxp.properties"), 
        SYSTEMPROPERTY("system property"), 
        APIPROPERTY("property");
        
        final String literal;
        
        private State(final String literal) {
            this.literal = literal;
        }
        
        String literal() {
            return this.literal;
        }
    }
    
    public enum Limit
    {
        ENTITY_EXPANSION_LIMIT("EntityExpansionLimit", "http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit", "jdk.xml.entityExpansionLimit", 0, 64000), 
        MAX_OCCUR_NODE_LIMIT("MaxOccurLimit", "http://www.oracle.com/xml/jaxp/properties/maxOccurLimit", "jdk.xml.maxOccurLimit", 0, 5000), 
        ELEMENT_ATTRIBUTE_LIMIT("ElementAttributeLimit", "http://www.oracle.com/xml/jaxp/properties/elementAttributeLimit", "jdk.xml.elementAttributeLimit", 0, 10000), 
        TOTAL_ENTITY_SIZE_LIMIT("TotalEntitySizeLimit", "http://www.oracle.com/xml/jaxp/properties/totalEntitySizeLimit", "jdk.xml.totalEntitySizeLimit", 0, 50000000), 
        GENERAL_ENTITY_SIZE_LIMIT("MaxEntitySizeLimit", "http://www.oracle.com/xml/jaxp/properties/maxGeneralEntitySizeLimit", "jdk.xml.maxGeneralEntitySizeLimit", 0, 0), 
        PARAMETER_ENTITY_SIZE_LIMIT("MaxEntitySizeLimit", "http://www.oracle.com/xml/jaxp/properties/maxParameterEntitySizeLimit", "jdk.xml.maxParameterEntitySizeLimit", 0, 1000000), 
        MAX_ELEMENT_DEPTH_LIMIT("MaxElementDepthLimit", "http://www.oracle.com/xml/jaxp/properties/maxElementDepth", "jdk.xml.maxElementDepth", 0, 0), 
        MAX_NAME_LIMIT("MaxXMLNameLimit", "http://www.oracle.com/xml/jaxp/properties/maxXMLNameLimit", "jdk.xml.maxXMLNameLimit", 1000, 1000), 
        ENTITY_REPLACEMENT_LIMIT("EntityReplacementLimit", "http://www.oracle.com/xml/jaxp/properties/entityReplacementLimit", "jdk.xml.entityReplacementLimit", 0, 3000000);
        
        final String key;
        final String apiProperty;
        final String systemProperty;
        final int defaultValue;
        final int secureValue;
        
        private Limit(final String key, final String apiProperty, final String systemProperty, final int value, final int secureValue) {
            this.key = key;
            this.apiProperty = apiProperty;
            this.systemProperty = systemProperty;
            this.defaultValue = value;
            this.secureValue = secureValue;
        }
        
        public boolean equalsAPIPropertyName(final String propertyName) {
            return propertyName != null && this.apiProperty.equals(propertyName);
        }
        
        public boolean equalsSystemPropertyName(final String propertyName) {
            return propertyName != null && this.systemProperty.equals(propertyName);
        }
        
        public String key() {
            return this.key;
        }
        
        public String apiProperty() {
            return this.apiProperty;
        }
        
        String systemProperty() {
            return this.systemProperty;
        }
        
        public int defaultValue() {
            return this.defaultValue;
        }
        
        int secureValue() {
            return this.secureValue;
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
