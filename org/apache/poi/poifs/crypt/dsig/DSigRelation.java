package org.apache.poi.poifs.crypt.dsig;

import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ooxml.POIXMLRelation;

public class DSigRelation extends POIXMLRelation
{
    private static final Map<String, DSigRelation> _table;
    public static final DSigRelation ORIGIN_SIGS;
    public static final DSigRelation SIG;
    
    private DSigRelation(final String type, final String rel, final String defaultName) {
        super(type, rel, defaultName);
        DSigRelation._table.put(rel, this);
    }
    
    public static DSigRelation getInstance(final String rel) {
        return DSigRelation._table.get(rel);
    }
    
    static {
        _table = new HashMap<String, DSigRelation>();
        ORIGIN_SIGS = new DSigRelation("application/vnd.openxmlformats-package.digital-signature-origin", "http://schemas.openxmlformats.org/package/2006/relationships/digital-signature/origin", "/_xmlsignatures/origin.sigs");
        SIG = new DSigRelation("application/vnd.openxmlformats-package.digital-signature-xmlsignature+xml", "http://schemas.openxmlformats.org/package/2006/relationships/digital-signature/signature", "/_xmlsignatures/sig#.xml");
    }
}
