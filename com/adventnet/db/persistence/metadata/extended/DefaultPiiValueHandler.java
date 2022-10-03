package com.adventnet.db.persistence.metadata.extended;

import com.adventnet.persistence.PersistenceInitializer;
import java.util.Iterator;
import com.zoho.framework.utils.crypto.CryptoUtil;
import java.util.List;
import java.util.Collections;
import java.util.Arrays;

public class DefaultPiiValueHandler implements PiiValueHandler
{
    private static final String ENCRYPT = "ENCRYPT";
    private static final String EMAIL = "EMAIL";
    private static final String SHUFFLE = "SHUFFLE";
    private static final String PREFIX = "PREFIX";
    private static final String SUFFIX = "SUFFIX";
    private static final String PHONE = "PHONE";
    private static final String HIDE = "HIDE";
    private static final String MASK = "MASK";
    private static final String NONE = "NONE";
    private static final String DEFAULT = "DEFAULT";
    protected static String defaultPiiValue;
    protected static final boolean IS_PII_ENABLED;
    
    public DefaultPiiValueHandler() {
        if (DefaultPiiValueHandler.defaultPiiValue == null) {
            DefaultPiiValueHandler.defaultPiiValue = "NONE";
        }
    }
    
    @Override
    public String getMaskedValue(final Object value, String type, final Object additionalParam) {
        if (!DefaultPiiValueHandler.IS_PII_ENABLED) {
            return String.valueOf(value);
        }
        if (value == null) {
            return null;
        }
        final String data = String.valueOf(value);
        int offset;
        if (additionalParam == null || !(additionalParam instanceof Integer)) {
            offset = 4;
        }
        else {
            offset = (int)additionalParam;
        }
        if (type.equals("DEFAULT")) {
            type = DefaultPiiValueHandler.defaultPiiValue;
        }
        if (type.equals("SHUFFLE")) {
            final List<String> valueAsList = Arrays.asList(data.split(""));
            Collections.shuffle(valueAsList);
            final StringBuilder shuffledData = new StringBuilder();
            final Iterator<String> dataIterator = valueAsList.iterator();
            while (dataIterator.hasNext()) {
                shuffledData.append(dataIterator.next());
            }
            return shuffledData.toString();
        }
        if (type.equals("PREFIX")) {
            if (data.length() > offset) {
                return data.substring(0, offset).replaceAll(".", "*") + data.substring(offset);
            }
            return this.mask(data);
        }
        else if (type.equals("SUFFIX")) {
            if (data.length() > offset) {
                return data.substring(0, offset) + data.substring(offset).replaceAll(".", "*");
            }
            return this.mask(data);
        }
        else {
            if (type.equals("EMAIL")) {
                return data.replaceAll("\\b(\\w{2})[^@]+@(\\w{2})\\S+(\\.[^\\s.]+)", "$1***@$2****$3");
            }
            if (type.equals("PHONE")) {
                final int length = data.length();
                if (length > offset) {
                    return data.substring(0, length - offset).replaceAll(".", "*") + data.substring(length - offset);
                }
                return this.mask(data);
            }
            else {
                if (type.equals("HIDE")) {
                    return "";
                }
                if (type.equals("ENCRYPT")) {
                    return CryptoUtil.encrypt(data);
                }
                if (type.equals("MASK")) {
                    return this.mask(data);
                }
                if (type.equals("NONE")) {
                    return data;
                }
                return this.mask(data);
            }
        }
    }
    
    private String mask(final String data) {
        return "*****";
    }
    
    @Override
    public String getMaskedValue(final Object value, final String type) {
        return this.getMaskedValue(value, type, null);
    }
    
    static {
        DefaultPiiValueHandler.defaultPiiValue = PersistenceInitializer.getConfigurationValue("pii.default");
        IS_PII_ENABLED = Boolean.valueOf(PersistenceInitializer.getConfigurationValue("pii.enable"));
    }
}
