package com.unboundid.ldap.sdk.transformations;

import com.unboundid.ldap.sdk.Entry;

final class PreEncodedLDIFEntry extends Entry
{
    private static final long serialVersionUID = 6342345192453693575L;
    private final byte[] ldifBytes;
    
    PreEncodedLDIFEntry(final Entry entry, final byte[] ldifBytes) {
        super(entry);
        this.ldifBytes = ldifBytes;
    }
    
    byte[] getLDIFBytes() {
        return this.ldifBytes;
    }
}
