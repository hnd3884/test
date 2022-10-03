package org.owasp.esapi.reference.accesscontrol.policyloader;

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import org.apache.commons.configuration.XMLConfiguration;

public final class ACRParameterLoaderHelper
{
    public static Object getParameterValue(final XMLConfiguration config, final int currentRule, final int currentParameter, final String parameterType) throws Exception {
        final String key = "AccessControlRules.AccessControlRule(" + currentRule + ").Parameters.Parameter(" + currentParameter + ")[@value]";
        Object parameterValue;
        if ("String".equalsIgnoreCase(parameterType)) {
            parameterValue = config.getString(key);
        }
        else if ("StringArray".equalsIgnoreCase(parameterType)) {
            parameterValue = config.getStringArray(key);
        }
        else if ("Boolean".equalsIgnoreCase(parameterType)) {
            parameterValue = config.getBoolean(key);
        }
        else if ("Byte".equalsIgnoreCase(parameterType)) {
            parameterValue = config.getByte(key);
        }
        else if ("Int".equalsIgnoreCase(parameterType)) {
            parameterValue = config.getInt(key);
        }
        else if ("Long".equalsIgnoreCase(parameterType)) {
            parameterValue = config.getLong(key);
        }
        else if ("Float".equalsIgnoreCase(parameterType)) {
            parameterValue = config.getFloat(key);
        }
        else if ("Double".equalsIgnoreCase(parameterType)) {
            parameterValue = config.getDouble(key);
        }
        else if ("BigDecimal".equalsIgnoreCase(parameterType)) {
            parameterValue = config.getBigDecimal(key);
        }
        else if ("BigInteger".equalsIgnoreCase(parameterType)) {
            parameterValue = config.getBigInteger(key);
        }
        else if ("Date".equalsIgnoreCase(parameterType)) {
            parameterValue = DateFormat.getDateInstance().parse(config.getString(key));
        }
        else {
            if (!"Time".equalsIgnoreCase(parameterType)) {
                throw new IllegalArgumentException("Unable to load the key \"" + key + "\", because " + "the type \"" + parameterType + "\" was not recognized.");
            }
            final SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
            parameterValue = sdf.parseObject(config.getString(key));
        }
        return parameterValue;
    }
}
