package org.bouncycastle.cms;

import java.util.Enumeration;
import org.bouncycastle.asn1.cms.CMSAlgorithmProtection;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.CMSAttributes;
import java.util.Map;
import org.bouncycastle.asn1.cms.AttributeTable;
import java.util.Hashtable;

public class DefaultAuthenticatedAttributeTableGenerator implements CMSAttributeTableGenerator
{
    private final Hashtable table;
    
    public DefaultAuthenticatedAttributeTableGenerator() {
        this.table = new Hashtable();
    }
    
    public DefaultAuthenticatedAttributeTableGenerator(final AttributeTable attributeTable) {
        if (attributeTable != null) {
            this.table = attributeTable.toHashtable();
        }
        else {
            this.table = new Hashtable();
        }
    }
    
    protected Hashtable createStandardAttributeTable(final Map map) {
        final Hashtable hashtable = new Hashtable();
        final Enumeration keys = this.table.keys();
        while (keys.hasMoreElements()) {
            final Object nextElement = keys.nextElement();
            hashtable.put(nextElement, this.table.get(nextElement));
        }
        if (!hashtable.containsKey(CMSAttributes.contentType)) {
            final Attribute attribute = new Attribute(CMSAttributes.contentType, (ASN1Set)new DERSet((ASN1Encodable)ASN1ObjectIdentifier.getInstance(map.get("contentType"))));
            hashtable.put(attribute.getAttrType(), attribute);
        }
        if (!hashtable.containsKey(CMSAttributes.messageDigest)) {
            final Attribute attribute2 = new Attribute(CMSAttributes.messageDigest, (ASN1Set)new DERSet((ASN1Encodable)new DEROctetString((byte[])map.get("digest"))));
            hashtable.put(attribute2.getAttrType(), attribute2);
        }
        if (!hashtable.contains(CMSAttributes.cmsAlgorithmProtect)) {
            final Attribute attribute3 = new Attribute(CMSAttributes.cmsAlgorithmProtect, (ASN1Set)new DERSet((ASN1Encodable)new CMSAlgorithmProtection((AlgorithmIdentifier)map.get("digestAlgID"), 2, (AlgorithmIdentifier)map.get("macAlgID"))));
            hashtable.put(attribute3.getAttrType(), attribute3);
        }
        return hashtable;
    }
    
    public AttributeTable getAttributes(final Map map) {
        return new AttributeTable(this.createStandardAttributeTable(map));
    }
}
