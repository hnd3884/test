package com.unboundid.ldap.sdk.unboundidds.examples;

import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GenericFilter implements Serializable
{
    private static final long serialVersionUID = -7875317078624475546L;
    private final int hashCode;
    private final String filterString;
    
    public GenericFilter(final Filter f) {
        final StringBuilder b = new StringBuilder();
        b.append('(');
        switch (f.getFilterType()) {
            case -96:
            case -95: {
                appendComponents(f, b);
                break;
            }
            case -94: {
                b.append('!');
                b.append(new GenericFilter(f.getNOTComponent()).toString());
                break;
            }
            case -93: {
                b.append(StaticUtils.toLowerCase(f.getAttributeName()));
                b.append("=?");
                break;
            }
            case -92: {
                b.append(StaticUtils.toLowerCase(f.getAttributeName()));
                b.append('=');
                if (f.getRawSubInitialValue() != null) {
                    b.append('?');
                }
                for (int i = 0; i < f.getRawSubAnyValues().length; ++i) {
                    b.append("*?");
                }
                b.append('*');
                if (f.getRawSubFinalValue() != null) {
                    b.append('?');
                    break;
                }
                break;
            }
            case -91: {
                b.append(StaticUtils.toLowerCase(f.getAttributeName()));
                b.append(">=?");
                break;
            }
            case -90: {
                b.append(StaticUtils.toLowerCase(f.getAttributeName()));
                b.append("<=?");
                break;
            }
            case -121: {
                b.append(StaticUtils.toLowerCase(f.getAttributeName()));
                b.append("=*");
                break;
            }
            case -88: {
                b.append(StaticUtils.toLowerCase(f.getAttributeName()));
                b.append("~=?");
                break;
            }
            case -87: {
                final String attrName = StaticUtils.toLowerCase(f.getAttributeName());
                final String mrID = StaticUtils.toLowerCase(f.getMatchingRuleID());
                if (attrName != null) {
                    b.append(attrName);
                }
                if (f.getDNAttributes()) {
                    b.append(":dn");
                }
                if (mrID != null) {
                    b.append(':');
                    b.append(mrID);
                }
                b.append(":=?");
                break;
            }
        }
        b.append(')');
        this.filterString = b.toString();
        this.hashCode = this.filterString.hashCode();
    }
    
    private static void appendComponents(final Filter f, final StringBuilder b) {
        if (f.getFilterType() == -96) {
            b.append('&');
        }
        else {
            b.append('|');
        }
        final TreeSet<Filter> compSet = new TreeSet<Filter>(FilterComparator.getInstance());
        compSet.addAll(Arrays.asList(f.getComponents()));
        for (final Filter fc : compSet) {
            b.append(new GenericFilter(fc).toString());
        }
    }
    
    @Override
    public int hashCode() {
        return this.hashCode;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && (o == this || (o instanceof GenericFilter && this.filterString.equals(((GenericFilter)o).filterString)));
    }
    
    @Override
    public String toString() {
        return this.filterString;
    }
}
