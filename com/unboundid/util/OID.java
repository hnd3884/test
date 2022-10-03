package com.unboundid.util;

import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class OID implements Serializable, Comparable<OID>
{
    private static final long serialVersionUID = -4542498394670806081L;
    private final List<Integer> components;
    private final String oidString;
    
    public OID(final String oidString) {
        if (oidString == null) {
            this.oidString = "";
        }
        else {
            this.oidString = oidString;
        }
        this.components = parseComponents(oidString);
    }
    
    public OID(final int... components) {
        this(toList(components));
    }
    
    public OID(final List<Integer> components) {
        if (components == null || components.isEmpty()) {
            this.components = null;
            this.oidString = "";
        }
        else {
            this.components = Collections.unmodifiableList((List<? extends Integer>)new ArrayList<Integer>(components));
            final StringBuilder buffer = new StringBuilder();
            for (final Integer i : components) {
                if (buffer.length() > 0) {
                    buffer.append('.');
                }
                buffer.append(i);
            }
            this.oidString = buffer.toString();
        }
    }
    
    private static List<Integer> toList(final int... components) {
        if (components == null) {
            return null;
        }
        final ArrayList<Integer> compList = new ArrayList<Integer>(components.length);
        for (final int i : components) {
            compList.add(i);
        }
        return compList;
    }
    
    public static List<Integer> parseComponents(final String oidString) {
        if (oidString == null || oidString.isEmpty() || oidString.startsWith(".") || oidString.endsWith(".") || oidString.indexOf("..") > 0) {
            return null;
        }
        final StringTokenizer tokenizer = new StringTokenizer(oidString, ".");
        final ArrayList<Integer> compList = new ArrayList<Integer>(10);
        while (tokenizer.hasMoreTokens()) {
            final String token = tokenizer.nextToken();
            try {
                compList.add(Integer.parseInt(token));
            }
            catch (final Exception e) {
                Debug.debugException(e);
                return null;
            }
        }
        return Collections.unmodifiableList((List<? extends Integer>)compList);
    }
    
    public static boolean isValidNumericOID(final String s) {
        return new OID(s).isValidNumericOID();
    }
    
    public boolean isValidNumericOID() {
        return this.components != null;
    }
    
    public static boolean isStrictlyValidNumericOID(final String s) {
        return new OID(s).isStrictlyValidNumericOID();
    }
    
    public boolean isStrictlyValidNumericOID() {
        if (this.components == null || this.components.size() < 2) {
            return false;
        }
        final int firstComponent = this.components.get(0);
        final int secondComponent = this.components.get(1);
        switch (firstComponent) {
            case 0:
            case 1: {
                return secondComponent <= 39;
            }
            case 2: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public List<Integer> getComponents() {
        return this.components;
    }
    
    @Override
    public int hashCode() {
        if (this.components == null) {
            return this.oidString.hashCode();
        }
        int hashCode = 0;
        for (final int i : this.components) {
            hashCode += i;
        }
        return hashCode;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof OID)) {
            return false;
        }
        final OID oid = (OID)o;
        if (this.components == null) {
            return this.oidString.equals(oid.oidString);
        }
        return this.components.equals(oid.components);
    }
    
    @Override
    public int compareTo(final OID oid) {
        if (this.components == null) {
            if (oid.components == null) {
                return this.oidString.compareTo(oid.oidString);
            }
            return 1;
        }
        else {
            if (oid.components == null) {
                return -1;
            }
            for (int i = 0; i < Math.min(this.components.size(), oid.components.size()); ++i) {
                final int thisValue = this.components.get(i);
                final int thatValue = oid.components.get(i);
                if (thisValue < thatValue) {
                    return -1;
                }
                if (thisValue > thatValue) {
                    return 1;
                }
            }
            if (this.components.size() < oid.components.size()) {
                return -1;
            }
            if (this.components.size() > oid.components.size()) {
                return 1;
            }
            return 0;
        }
    }
    
    @Override
    public String toString() {
        return this.oidString;
    }
}
