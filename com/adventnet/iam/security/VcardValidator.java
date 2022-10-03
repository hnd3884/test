package com.adventnet.iam.security;

import java.util.Collection;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.LinkedList;
import com.adventnet.iam.parser.Parser;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class VcardValidator extends DataFormatValidator
{
    private static final Logger LOGGER;
    private Map<String, List<String>> requestVcardMap;
    private Map<String, List<String>> resultVcardMap;
    
    public VcardValidator() {
        this.requestVcardMap = null;
        this.resultVcardMap = null;
    }
    
    List<Map<String, List<String>>> parseAndValidateVcardFormat(final String paramName, final String parameterValue, final TemplateRule templateRule, final ParameterRule paramRule) {
        final String lineDelimeter = "\n";
        final String keyValueDelimeter = ":";
        final int splitLimit = 2;
        final List<VcardProperty> vcardPropertyList = Parser.parseVcard(parameterValue, lineDelimeter, keyValueDelimeter, 2);
        return this.validateVcardArray(paramName, vcardPropertyList, templateRule, paramRule);
    }
    
    List<Map<String, List<String>>> validateVcardArray(final String paramName, final List<VcardProperty> vcardArrayList, final TemplateRule templateRule, final ParameterRule paramRule) {
        final List<VcardProperty> vcardObjectList = new LinkedList<VcardProperty>();
        final List<Map<String, List<String>>> validVcardArrayList = new LinkedList<Map<String, List<String>>>();
        boolean validVcardContent = false;
        int noOfVCards = 0;
        for (final VcardProperty vcardArrayProperty : vcardArrayList) {
            vcardObjectList.add(vcardArrayProperty);
            if ("BEGIN".equals(vcardArrayProperty.getKey())) {
                if (validVcardContent) {
                    vcardObjectList.clear();
                    VcardValidator.LOGGER.log(Level.SEVERE, " Valid Vcard Array must End with the END:VCARD before starting another vcard(BEGIN:VCARD) \n");
                    throw new IAMSecurityException("VCARD/VCARDARRAY PARSE ERROR");
                }
                validVcardContent = true;
            }
            else {
                if (!validVcardContent) {
                    VcardValidator.LOGGER.log(Level.SEVERE, " extra Key-value pairs {0}:{1}  must be within BEGIN:VCARD and END:VCARD properties \n", new Object[] { vcardArrayProperty.getKey(), vcardArrayProperty.getValue() });
                    throw new IAMSecurityException("VCARD/VCARDARRAY PARSE ERROR");
                }
                if (!"END".equals(vcardArrayProperty.getKey()) || !validVcardContent) {
                    continue;
                }
                if (++noOfVCards > paramRule.getArraysizeInRange().getUpperLimit()) {
                    VcardValidator.LOGGER.log(Level.SEVERE, "Number of vcards present in the parameter \"{0}\" are more than the configured size", paramName);
                    throw new IAMSecurityException("ARRAY_SIZE_OUT_OF_RANGE");
                }
                this.requestVcardMap = new LinkedHashMap<String, List<String>>();
                this.resultVcardMap = new LinkedHashMap<String, List<String>>();
                this.requestVcardMap = this.convertListToMap(vcardObjectList);
                validVcardArrayList.add(this.resultVcardMap = this.validateVcardObject(paramName, templateRule));
                validVcardContent = false;
                vcardObjectList.clear();
            }
        }
        if (validVcardArrayList.size() <= 0) {
            VcardValidator.LOGGER.log(Level.SEVERE, " Valid Vcard Object/Array must start with BEGIN:VCARD and End with the END:VCARD \n");
            throw new IAMSecurityException("VCARD/VCARDARRAY PARSE ERROR");
        }
        if (paramRule.getArraysizeInRange().getLowerLimit() > noOfVCards) {
            VcardValidator.LOGGER.log(Level.SEVERE, "Number of vcards present in the parameter \"{0}\" are less than the configured size", paramName);
            throw new IAMSecurityException("ARRAY_SIZE_OUT_OF_RANGE");
        }
        return validVcardArrayList;
    }
    
    Map<String, List<String>> validateVcardObject(final String paramName, final TemplateRule templateRule) {
        final HttpServletRequest request = SecurityUtil.getCurrentRequest();
        templateRule.validateDataFormat(request, this);
        return this.reArrangeVcardMap();
    }
    
    private Map<String, List<String>> reArrangeVcardMap() {
        final Map<String, List<String>> reArrangedVcardMap = new LinkedHashMap<String, List<String>>();
        for (final String vcardKey : this.requestVcardMap.keySet()) {
            if (this.resultVcardMap.containsKey(vcardKey)) {
                reArrangedVcardMap.put(vcardKey, this.resultVcardMap.get(vcardKey));
            }
        }
        return reArrangedVcardMap;
    }
    
    Map<String, List<String>> convertListToMap(final List<VcardProperty> vcardObjectPropertyList) {
        final Map<String, List<String>> vcardMap = new LinkedHashMap<String, List<String>>();
        for (final VcardProperty vcardProperty : vcardObjectPropertyList) {
            if (!vcardMap.containsKey(vcardProperty.getKey())) {
                final List<String> values = new LinkedList<String>();
                values.add(vcardProperty.getValue());
                vcardMap.put(vcardProperty.getKey(), values);
            }
            else {
                if (vcardMap.get(vcardProperty.getKey()).contains(vcardProperty.getValue())) {
                    continue;
                }
                vcardMap.get(vcardProperty.getKey()).add(vcardProperty.getValue());
            }
        }
        return vcardMap;
    }
    
    private String formatVcardProperty(final String vcardKey, final List<String> vcardValuesAsList) {
        final StringBuilder returnValue = new StringBuilder();
        for (final String values : vcardValuesAsList) {
            returnValue.append(vcardKey + ":" + values + "\n");
        }
        return returnValue.toString();
    }
    
    public String getVcardFormatAsString(final List<Map<String, List<String>>> validVcardArray) {
        final StringBuilder sb = new StringBuilder();
        for (final Map<String, List<String>> vcardObjectMap : validVcardArray) {
            for (final String vcardObjectKey : vcardObjectMap.keySet()) {
                sb.append(this.formatVcardProperty(vcardObjectKey, vcardObjectMap.get(vcardObjectKey)));
            }
        }
        return sb.toString();
    }
    
    @Override
    List<String> getKeySet() {
        return new ArrayList<String>(this.requestVcardMap.keySet());
    }
    
    @Override
    List<String> getList(final String key) {
        return this.requestVcardMap.get(key);
    }
    
    @Override
    void setList(final String key, final List<String> value) {
        this.resultVcardMap.put(key, value);
    }
    
    @Override
    boolean hasValidated(final String key) {
        return this.resultVcardMap.containsKey(key);
    }
    
    @Override
    ZSecConstants.DataType getDataFormatType() {
        return ZSecConstants.DataType.Vcard;
    }
    
    static {
        LOGGER = Logger.getLogger(VcardValidator.class.getName());
    }
}
