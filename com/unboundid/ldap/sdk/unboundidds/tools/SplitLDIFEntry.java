package com.unboundid.ldap.sdk.unboundidds.tools;

import java.util.Set;
import com.unboundid.ldap.sdk.Entry;

final class SplitLDIFEntry extends Entry
{
    static final String SET_NAME_OUTSIDE_SPLIT = ".outside-split";
    static final String SET_NAME_ERRORS = ".errors";
    private static final long serialVersionUID = 3082656046595242989L;
    private final byte[] ldifBytes;
    private final Set<String> sets;
    
    SplitLDIFEntry(final Entry e, final byte[] ldifBytes, final Set<String> sets) {
        super(e);
        this.ldifBytes = ldifBytes;
        this.sets = sets;
    }
    
    byte[] getLDIFBytes() {
        return this.ldifBytes;
    }
    
    Set<String> getSets() {
        return this.sets;
    }
}
