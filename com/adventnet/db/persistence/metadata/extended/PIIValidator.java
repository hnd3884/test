package com.adventnet.db.persistence.metadata.extended;

import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.HashMap;
import java.util.Map;

public class PIIValidator implements CustomAttributeValidator
{
    protected Map<String, Integer> piiLevel;
    private Integer defaultPiiLevel;
    private static final String NONE = "NONE";
    private static final String MASK = "MASK";
    private static final String ENCRYPT = "ENCRYPT";
    private static final String HIDE = "HIDE";
    private static final String DEFAULT = "DEFAULT";
    private static final String EMAIL = "EMAIL";
    private static final String SHUFFLE = "SHUFFLE";
    private static final String PREFIX = "PREFIX";
    private static final String SUFFIX = "SUFFIX";
    private static final String PHONE = "PHONE";
    private final boolean isPiiEnabled;
    
    public PIIValidator() {
        this.piiLevel = new HashMap<String, Integer>();
        this.defaultPiiLevel = null;
        this.isPiiEnabled = Boolean.valueOf(PersistenceInitializer.getConfigurationValue("pii.enable"));
        this.piiLevel.put("NONE", 0);
        this.piiLevel.put("MASK", 1);
        this.piiLevel.put("EMAIL", 1);
        this.piiLevel.put("SHUFFLE", 1);
        this.piiLevel.put("PREFIX", 1);
        this.piiLevel.put("SUFFIX", 1);
        this.piiLevel.put("PHONE", 1);
        this.piiLevel.put("ENCRYPT", 2);
        this.piiLevel.put("HIDE", 3);
        if (this.defaultPiiLevel == null) {
            this.defaultPiiLevel = this.piiLevel.get(PersistenceInitializer.getConfigurationValue("pii.default"));
            if (this.defaultPiiLevel == null) {
                this.defaultPiiLevel = 0;
            }
        }
    }
    
    @Override
    public boolean validateStaticAttribute(final String key, final String existingAttrValue, final String newAttrValue) throws MetaDataException {
        if (!this.isPiiEnabled) {
            return false;
        }
        if (newAttrValue == null || newAttrValue.isEmpty()) {
            throw new MetaDataException("PII level of column [" + key.substring(0, key.lastIndexOf(".")) + "] cannot be null/empty and can only have the following values :: " + this.piiLevel.keySet());
        }
        Integer requiredLevel = this.piiLevel.get(newAttrValue);
        if (newAttrValue.equals("DEFAULT")) {
            requiredLevel = this.defaultPiiLevel;
        }
        if (requiredLevel == null) {
            throw new MetaDataException("PII level of column [" + key.substring(0, key.lastIndexOf(".")) + "] cannot be null/empty and can only have the following values :: " + this.piiLevel.keySet());
        }
        if (existingAttrValue == null) {
            return true;
        }
        final Integer existingLevel = this.piiLevel.get(existingAttrValue);
        if (existingLevel <= requiredLevel) {
            return true;
        }
        throw new MetaDataException("PII level of column [" + key.substring(0, key.lastIndexOf(".")) + "] is already defined as [" + existingAttrValue + "] and cannot be decreased to [" + newAttrValue + "]");
    }
    
    @Override
    public boolean validateDynamicAttribute(final String key, final String existingAttrValue, final String newAttrValue) throws MetaDataException {
        if (!this.isPiiEnabled) {
            return false;
        }
        if (newAttrValue != null && (this.piiLevel.get(newAttrValue) != null || newAttrValue.equals("DEFAULT"))) {
            return true;
        }
        throw new MetaDataException("PII level of column [" + key.substring(0, key.lastIndexOf(".")) + "] cannot be null/empty and can only have the following values :: " + this.piiLevel.keySet());
    }
}
