package org.glassfish.jersey.message.internal;

import java.util.Locale;
import java.util.HashMap;
import java.util.Collections;
import java.util.Map;
import java.util.Comparator;

public final class Quality
{
    public static final Comparator<Qualified> QUALIFIED_COMPARATOR;
    public static final Comparator<Integer> QUALITY_VALUE_COMPARATOR;
    public static final String QUALITY_PARAMETER_NAME = "q";
    public static final String QUALITY_SOURCE_PARAMETER_NAME = "qs";
    public static final int MINIMUM = 0;
    public static final int MAXIMUM = 1000;
    public static final int DEFAULT = 1000;
    
    private Quality() {
        throw new AssertionError((Object)"Instantiation not allowed.");
    }
    
    static Map<String, String> enhanceWithQualityParameter(final Map<String, String> parameters, final String qualityParamName, final int quality) {
        if (quality == 1000 && (parameters == null || parameters.isEmpty() || !parameters.containsKey(qualityParamName))) {
            return parameters;
        }
        if (parameters == null || parameters.isEmpty()) {
            return Collections.singletonMap(qualityParamName, qualityValueToString((float)quality));
        }
        try {
            parameters.put(qualityParamName, qualityValueToString((float)quality));
            return parameters;
        }
        catch (final UnsupportedOperationException uoe) {
            final Map<String, String> result = new HashMap<String, String>(parameters);
            result.put(qualityParamName, qualityValueToString((float)quality));
            return result;
        }
    }
    
    private static int compare(final int x, final int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }
    
    private static String qualityValueToString(final float quality) {
        final StringBuilder qsb = new StringBuilder(String.format(Locale.US, "%3.3f", quality / 1000.0f));
        int lastIndex;
        while ((lastIndex = qsb.length() - 1) > 2 && qsb.charAt(lastIndex) == '0') {
            qsb.deleteCharAt(lastIndex);
        }
        return qsb.toString();
    }
    
    static {
        QUALIFIED_COMPARATOR = new Comparator<Qualified>() {
            @Override
            public int compare(final Qualified o1, final Qualified o2) {
                return compare(o2.getQuality(), o1.getQuality());
            }
        };
        QUALITY_VALUE_COMPARATOR = new Comparator<Integer>() {
            @Override
            public int compare(final Integer q1, final Integer q2) {
                return compare(q2, q1);
            }
        };
    }
}
