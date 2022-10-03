package com.adventnet.iam.security;

import java.util.Hashtable;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.iam.parser.Parser;
import java.util.Properties;

public class PropertiesValidator extends DataFormatValidator
{
    Properties parsedProperties;
    Properties validatedProperties;
    
    public PropertiesValidator() {
        this.parsedProperties = null;
        this.validatedProperties = null;
    }
    
    Properties parseAndValidatePropertiesFormat(final String paramName, final String parameterValue, final TemplateRule templateRule, final boolean allowEmptyValue) throws IOException {
        this.validatedProperties = new Properties();
        this.parsedProperties = Parser.parseProperties(parameterValue);
        final HttpServletRequest request = SecurityUtil.getCurrentRequest();
        templateRule.validateDataFormat(request, this);
        return this.validatedProperties;
    }
    
    @Override
    List<String> getKeySet() {
        return new ArrayList<String>(this.parsedProperties.stringPropertyNames());
    }
    
    @Override
    String get(final String key) {
        return ((Hashtable<K, String>)this.parsedProperties).get(key);
    }
    
    @Override
    void set(final String key, final String value) {
        ((Hashtable<String, String>)this.validatedProperties).put(key, value);
    }
    
    @Override
    boolean hasValidated(final String key) {
        return this.validatedProperties.containsKey(key);
    }
    
    @Override
    ZSecConstants.DataType getDataFormatType() {
        return ZSecConstants.DataType.Property;
    }
    
    public Properties getProperties() {
        return this.validatedProperties;
    }
}
