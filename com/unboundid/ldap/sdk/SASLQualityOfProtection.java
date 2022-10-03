package com.unboundid.ldap.sdk;

import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum SASLQualityOfProtection
{
    AUTH("auth"), 
    AUTH_INT("auth-int"), 
    AUTH_CONF("auth-conf");
    
    private final String qopString;
    
    private SASLQualityOfProtection(final String qopString) {
        this.qopString = qopString;
    }
    
    public static SASLQualityOfProtection forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "auth": {
                return SASLQualityOfProtection.AUTH;
            }
            case "authint":
            case "auth-int":
            case "auth_int": {
                return SASLQualityOfProtection.AUTH_INT;
            }
            case "authconf":
            case "auth-conf":
            case "auth_conf": {
                return SASLQualityOfProtection.AUTH_CONF;
            }
            default: {
                return null;
            }
        }
    }
    
    public static List<SASLQualityOfProtection> decodeQoPList(final String s) throws LDAPException {
        final ArrayList<SASLQualityOfProtection> qopValues = new ArrayList<SASLQualityOfProtection>(3);
        if (s == null || s.isEmpty()) {
            return qopValues;
        }
        final StringTokenizer tokenizer = new StringTokenizer(s, ",");
        while (tokenizer.hasMoreTokens()) {
            final String token = tokenizer.nextToken().trim();
            final SASLQualityOfProtection qop = forName(token);
            if (qop == null) {
                throw new LDAPException(ResultCode.PARAM_ERROR, LDAPMessages.ERR_SASL_QOP_DECODE_LIST_INVALID_ELEMENT.get(token, SASLQualityOfProtection.AUTH.qopString, SASLQualityOfProtection.AUTH_INT.qopString, SASLQualityOfProtection.AUTH_CONF.qopString));
            }
            qopValues.add(qop);
        }
        return qopValues;
    }
    
    @Override
    public String toString() {
        return this.qopString;
    }
    
    public static String toString(final List<SASLQualityOfProtection> qopValues) {
        if (qopValues == null || qopValues.isEmpty()) {
            return SASLQualityOfProtection.AUTH.qopString;
        }
        final StringBuilder buffer = new StringBuilder(23);
        final Iterator<SASLQualityOfProtection> iterator = qopValues.iterator();
        while (iterator.hasNext()) {
            buffer.append(iterator.next().qopString);
            if (iterator.hasNext()) {
                buffer.append(',');
            }
        }
        return buffer.toString();
    }
}
