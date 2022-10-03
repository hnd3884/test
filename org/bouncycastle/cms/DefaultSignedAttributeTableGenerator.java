package org.bouncycastle.cms;

import java.util.Enumeration;
import org.bouncycastle.asn1.cms.CMSAlgorithmProtection;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.Time;
import java.util.Date;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.CMSAttributes;
import java.util.Map;
import org.bouncycastle.asn1.cms.AttributeTable;
import java.util.Hashtable;

public class DefaultSignedAttributeTableGenerator implements CMSAttributeTableGenerator
{
    private final Hashtable table;
    
    public DefaultSignedAttributeTableGenerator() {
        this.table = new Hashtable();
    }
    
    public DefaultSignedAttributeTableGenerator(final AttributeTable attributeTable) {
        if (attributeTable != null) {
            this.table = attributeTable.toHashtable();
        }
        else {
            this.table = new Hashtable();
        }
    }
    
    protected Hashtable createStandardAttributeTable(final Map map) {
        final Hashtable copyHashTable = copyHashTable(this.table);
        if (!copyHashTable.containsKey(CMSAttributes.contentType)) {
            final ASN1ObjectIdentifier instance = ASN1ObjectIdentifier.getInstance(map.get("contentType"));
            if (instance != null) {
                final Attribute attribute = new Attribute(CMSAttributes.contentType, (ASN1Set)new DERSet((ASN1Encodable)instance));
                copyHashTable.put(attribute.getAttrType(), attribute);
            }
        }
        if (!copyHashTable.containsKey(CMSAttributes.signingTime)) {
            final Attribute attribute2 = new Attribute(CMSAttributes.signingTime, (ASN1Set)new DERSet((ASN1Encodable)new Time(new Date())));
            copyHashTable.put(attribute2.getAttrType(), attribute2);
        }
        if (!copyHashTable.containsKey(CMSAttributes.messageDigest)) {
            final Attribute attribute3 = new Attribute(CMSAttributes.messageDigest, (ASN1Set)new DERSet((ASN1Encodable)new DEROctetString((byte[])map.get("digest"))));
            copyHashTable.put(attribute3.getAttrType(), attribute3);
        }
        if (!copyHashTable.contains(CMSAttributes.cmsAlgorithmProtect)) {
            final Attribute attribute4 = new Attribute(CMSAttributes.cmsAlgorithmProtect, (ASN1Set)new DERSet((ASN1Encodable)new CMSAlgorithmProtection((AlgorithmIdentifier)map.get("digestAlgID"), 1, (AlgorithmIdentifier)map.get("signatureAlgID"))));
            copyHashTable.put(attribute4.getAttrType(), attribute4);
        }
        return copyHashTable;
    }
    
    public AttributeTable getAttributes(final Map map) {
        return new AttributeTable(this.createStandardAttributeTable(map));
    }
    
    private static Hashtable copyHashTable(final Hashtable hashtable) {
        final Hashtable hashtable2 = new Hashtable();
        final Enumeration keys = hashtable.keys();
        while (keys.hasMoreElements()) {
            final Object nextElement = keys.nextElement();
            hashtable2.put(nextElement, hashtable.get(nextElement));
        }
        return hashtable2;
    }
}
