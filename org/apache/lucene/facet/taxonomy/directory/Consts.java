package org.apache.lucene.facet.taxonomy.directory;

import org.apache.lucene.util.BytesRef;

abstract class Consts
{
    static final String FULL = "$full_path$";
    static final String FIELD_PAYLOADS = "$payloads$";
    static final String PAYLOAD_PARENT = "p";
    static final BytesRef PAYLOAD_PARENT_BYTES_REF;
    
    static {
        PAYLOAD_PARENT_BYTES_REF = new BytesRef((CharSequence)"p");
    }
}
