package jdk.xml.internal;

public class JdkXmlFeatures
{
    public static final String ORACLE_JAXP_PROPERTY_PREFIX = "http://www.oracle.com/xml/jaxp/properties/";
    public static final String XML_FEATURE_MANAGER = "http://www.oracle.com/xml/jaxp/properties/XmlFeatureManager";
    public static final String ORACLE_FEATURE_SERVICE_MECHANISM = "http://www.oracle.com/feature/use-service-mechanism";
    public static final String ORACLE_ENABLE_EXTENSION_FUNCTION = "http://www.oracle.com/xml/jaxp/properties/enableExtensionFunctions";
    public static final String SP_ENABLE_EXTENSION_FUNCTION = "javax.xml.enableExtensionFunctions";
    public static final String SP_ENABLE_EXTENSION_FUNCTION_SPEC = "jdk.xml.enableExtensionFunctions";
    private final boolean[] featureValues;
    private final State[] states;
    boolean secureProcessing;
    
    public JdkXmlFeatures(final boolean secureProcessing) {
        this.featureValues = new boolean[XmlFeature.values().length];
        this.states = new State[XmlFeature.values().length];
        this.secureProcessing = secureProcessing;
        for (final XmlFeature f : XmlFeature.values()) {
            if (secureProcessing && f.enforced()) {
                this.featureValues[f.ordinal()] = f.enforcedValue();
                this.states[f.ordinal()] = State.FSP;
            }
            else {
                this.featureValues[f.ordinal()] = f.defaultValue();
                this.states[f.ordinal()] = State.DEFAULT;
            }
        }
        this.readSystemProperties();
    }
    
    public void update() {
        this.readSystemProperties();
    }
    
    public boolean setFeature(final String propertyName, final State state, final Object value) {
        final int index = this.getIndex(propertyName);
        if (index > -1) {
            this.setFeature(index, state, value);
            return true;
        }
        return false;
    }
    
    public void setFeature(final XmlFeature feature, final State state, final boolean value) {
        this.setFeature(feature.ordinal(), state, value);
    }
    
    public boolean getFeature(final XmlFeature feature) {
        return this.featureValues[feature.ordinal()];
    }
    
    public boolean getFeature(final int index) {
        return this.featureValues[index];
    }
    
    public void setFeature(final int index, final State state, final Object value) {
        boolean temp;
        if (Boolean.class.isAssignableFrom(value.getClass())) {
            temp = (boolean)value;
        }
        else {
            temp = Boolean.parseBoolean((String)value);
        }
        this.setFeature(index, state, temp);
    }
    
    public void setFeature(final int index, final State state, final boolean value) {
        if (state.compareTo(this.states[index]) >= 0) {
            this.featureValues[index] = value;
            this.states[index] = state;
        }
    }
    
    public int getIndex(final String propertyName) {
        for (final XmlFeature feature : XmlFeature.values()) {
            if (feature.equalsPropertyName(propertyName)) {
                return feature.ordinal();
            }
        }
        return -1;
    }
    
    private void readSystemProperties() {
        for (final XmlFeature feature : XmlFeature.values()) {
            if (!this.getSystemProperty(feature, feature.systemProperty())) {
                final String oldName = feature.systemPropertyOld();
                if (oldName != null) {
                    this.getSystemProperty(feature, oldName);
                }
            }
        }
    }
    
    private boolean getSystemProperty(final XmlFeature feature, final String sysPropertyName) {
        try {
            String value = SecuritySupport.getSystemProperty(sysPropertyName);
            if (value != null && !value.equals("")) {
                this.setFeature(feature, State.SYSTEMPROPERTY, Boolean.parseBoolean(value));
                return true;
            }
            value = SecuritySupport.readJAXPProperty(sysPropertyName);
            if (value != null && !value.equals("")) {
                this.setFeature(feature, State.JAXPDOTPROPERTIES, Boolean.parseBoolean(value));
                return true;
            }
        }
        catch (final NumberFormatException e) {
            throw new NumberFormatException("Invalid setting for system property: " + feature.systemProperty());
        }
        return false;
    }
    
    public enum XmlFeature
    {
        ENABLE_EXTENSION_FUNCTION("http://www.oracle.com/xml/jaxp/properties/enableExtensionFunctions", "jdk.xml.enableExtensionFunctions", "http://www.oracle.com/xml/jaxp/properties/enableExtensionFunctions", "javax.xml.enableExtensionFunctions", true, false, true, true), 
        JDK_OVERRIDE_PARSER("jdk.xml.overrideDefaultParser", "jdk.xml.overrideDefaultParser", "http://www.oracle.com/feature/use-service-mechanism", "http://www.oracle.com/feature/use-service-mechanism", false, false, true, false);
        
        private final String name;
        private final String nameSP;
        private final String nameOld;
        private final String nameOldSP;
        private final boolean valueDefault;
        private final boolean valueEnforced;
        private final boolean hasSystem;
        private final boolean enforced;
        
        private XmlFeature(final String name, final String nameSP, final String nameOld, final String nameOldSP, final boolean value, final boolean valueEnforced, final boolean hasSystem, final boolean enforced) {
            this.name = name;
            this.nameSP = nameSP;
            this.nameOld = nameOld;
            this.nameOldSP = nameOldSP;
            this.valueDefault = value;
            this.valueEnforced = valueEnforced;
            this.hasSystem = hasSystem;
            this.enforced = enforced;
        }
        
        boolean equalsPropertyName(final String propertyName) {
            return this.name.equals(propertyName) || (this.nameOld != null && this.nameOld.equals(propertyName));
        }
        
        public String apiProperty() {
            return this.name;
        }
        
        String systemProperty() {
            return this.nameSP;
        }
        
        String systemPropertyOld() {
            return this.nameOldSP;
        }
        
        public boolean defaultValue() {
            return this.valueDefault;
        }
        
        public boolean enforcedValue() {
            return this.valueEnforced;
        }
        
        boolean hasSystemProperty() {
            return this.hasSystem;
        }
        
        boolean enforced() {
            return this.enforced;
        }
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
}
