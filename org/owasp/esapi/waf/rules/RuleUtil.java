package org.owasp.esapi.waf.rules;

import java.util.Enumeration;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class RuleUtil
{
    public static boolean isInList(final Map m, final String s) {
        for (final String key : m.keySet()) {
            if (key.equals(s)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isInList(final Collection c, final String s) {
        for (final Object o : c) {
            if (o instanceof String) {
                if (s.equals(o)) {
                    return true;
                }
                continue;
            }
            else if (o instanceof Integer) {
                try {
                    if (Integer.parseInt(s) == (int)o) {
                        return true;
                    }
                    continue;
                }
                catch (final Exception e) {}
            }
            else if (o instanceof Long) {
                try {
                    if (Long.parseLong(s) == (long)o) {
                        return true;
                    }
                    continue;
                }
                catch (final Exception e) {}
            }
            else {
                if (!(o instanceof Double)) {
                    continue;
                }
                try {
                    if (Double.compare(Double.parseDouble(s), (double)o) == 0) {
                        return true;
                    }
                    continue;
                }
                catch (final Exception ex) {}
            }
        }
        return false;
    }
    
    public static boolean isInList(final Enumeration en, final String s) {
        while (en.hasMoreElements()) {
            final Object o = en.nextElement();
            if (o instanceof String) {
                if (s.equals(o)) {
                    return true;
                }
                continue;
            }
            else if (o instanceof Integer) {
                try {
                    if (Integer.parseInt(s) == (int)o) {
                        return true;
                    }
                    continue;
                }
                catch (final Exception e) {}
            }
            else if (o instanceof Long) {
                try {
                    if (Long.parseLong(s) == (long)o) {
                        return true;
                    }
                    continue;
                }
                catch (final Exception e) {}
            }
            else {
                if (!(o instanceof Double)) {
                    continue;
                }
                try {
                    if (Double.compare(Double.parseDouble(s), (double)o) == 0) {
                        return true;
                    }
                    continue;
                }
                catch (final Exception ex) {}
            }
        }
        return false;
    }
    
    public static boolean testValue(final String s, final String test, final int operator) {
        switch (operator) {
            case 0: {
                return test.equals(s);
            }
            case 1: {
                return test.contains(s);
            }
            default: {
                return false;
            }
        }
    }
}
