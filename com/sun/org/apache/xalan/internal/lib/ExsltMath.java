package com.sun.org.apache.xalan.internal.lib;

import com.sun.org.apache.xpath.internal.NodeSet;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ExsltMath extends ExsltBase
{
    private static String PI;
    private static String E;
    private static String SQRRT2;
    private static String LN2;
    private static String LN10;
    private static String LOG2E;
    private static String SQRT1_2;
    
    public static double max(final NodeList nl) {
        if (nl == null || nl.getLength() == 0) {
            return Double.NaN;
        }
        double m = -1.7976931348623157E308;
        for (int i = 0; i < nl.getLength(); ++i) {
            final Node n = nl.item(i);
            final double d = ExsltBase.toNumber(n);
            if (Double.isNaN(d)) {
                return Double.NaN;
            }
            if (d > m) {
                m = d;
            }
        }
        return m;
    }
    
    public static double min(final NodeList nl) {
        if (nl == null || nl.getLength() == 0) {
            return Double.NaN;
        }
        double m = Double.MAX_VALUE;
        for (int i = 0; i < nl.getLength(); ++i) {
            final Node n = nl.item(i);
            final double d = ExsltBase.toNumber(n);
            if (Double.isNaN(d)) {
                return Double.NaN;
            }
            if (d < m) {
                m = d;
            }
        }
        return m;
    }
    
    public static NodeList highest(final NodeList nl) {
        final double maxValue = max(nl);
        final NodeSet highNodes = new NodeSet();
        highNodes.setShouldCacheNodes(true);
        if (Double.isNaN(maxValue)) {
            return highNodes;
        }
        for (int i = 0; i < nl.getLength(); ++i) {
            final Node n = nl.item(i);
            final double d = ExsltBase.toNumber(n);
            if (d == maxValue) {
                highNodes.addElement(n);
            }
        }
        return highNodes;
    }
    
    public static NodeList lowest(final NodeList nl) {
        final double minValue = min(nl);
        final NodeSet lowNodes = new NodeSet();
        lowNodes.setShouldCacheNodes(true);
        if (Double.isNaN(minValue)) {
            return lowNodes;
        }
        for (int i = 0; i < nl.getLength(); ++i) {
            final Node n = nl.item(i);
            final double d = ExsltBase.toNumber(n);
            if (d == minValue) {
                lowNodes.addElement(n);
            }
        }
        return lowNodes;
    }
    
    public static double abs(final double num) {
        return Math.abs(num);
    }
    
    public static double acos(final double num) {
        return Math.acos(num);
    }
    
    public static double asin(final double num) {
        return Math.asin(num);
    }
    
    public static double atan(final double num) {
        return Math.atan(num);
    }
    
    public static double atan2(final double num1, final double num2) {
        return Math.atan2(num1, num2);
    }
    
    public static double cos(final double num) {
        return Math.cos(num);
    }
    
    public static double exp(final double num) {
        return Math.exp(num);
    }
    
    public static double log(final double num) {
        return Math.log(num);
    }
    
    public static double power(final double num1, final double num2) {
        return Math.pow(num1, num2);
    }
    
    public static double random() {
        return Math.random();
    }
    
    public static double sin(final double num) {
        return Math.sin(num);
    }
    
    public static double sqrt(final double num) {
        return Math.sqrt(num);
    }
    
    public static double tan(final double num) {
        return Math.tan(num);
    }
    
    public static double constant(final String name, final double precision) {
        String value = null;
        if (name.equals("PI")) {
            value = ExsltMath.PI;
        }
        else if (name.equals("E")) {
            value = ExsltMath.E;
        }
        else if (name.equals("SQRRT2")) {
            value = ExsltMath.SQRRT2;
        }
        else if (name.equals("LN2")) {
            value = ExsltMath.LN2;
        }
        else if (name.equals("LN10")) {
            value = ExsltMath.LN10;
        }
        else if (name.equals("LOG2E")) {
            value = ExsltMath.LOG2E;
        }
        else if (name.equals("SQRT1_2")) {
            value = ExsltMath.SQRT1_2;
        }
        if (value != null) {
            final int bits = new Double(precision).intValue();
            if (bits <= value.length()) {
                value = value.substring(0, bits);
            }
            return Double.parseDouble(value);
        }
        return Double.NaN;
    }
    
    static {
        ExsltMath.PI = "3.1415926535897932384626433832795028841971693993751";
        ExsltMath.E = "2.71828182845904523536028747135266249775724709369996";
        ExsltMath.SQRRT2 = "1.41421356237309504880168872420969807856967187537694";
        ExsltMath.LN2 = "0.69314718055994530941723212145817656807550013436025";
        ExsltMath.LN10 = "2.302585092994046";
        ExsltMath.LOG2E = "1.4426950408889633";
        ExsltMath.SQRT1_2 = "0.7071067811865476";
    }
}
