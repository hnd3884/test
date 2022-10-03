package org.jscep.message;

import org.jscep.transaction.TransactionId;
import org.jscep.transaction.MessageType;
import org.bouncycastle.asn1.DEROctetString;
import org.jscep.transaction.Nonce;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERPrintableString;
import org.jscep.transaction.FailInfo;
import org.jscep.transaction.PkiStatus;
import org.jscep.asn1.ScepObjectIdentifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Hashtable;
import org.bouncycastle.asn1.cms.AttributeTable;

class AttributeTableFactory
{
    public AttributeTable fromPkiMessage(final PkiMessage<?> message) {
        final Hashtable<ASN1ObjectIdentifier, Attribute> table = new Hashtable<ASN1ObjectIdentifier, Attribute>();
        final List<Attribute> attributes = this.getMessageAttributes(message);
        if (message instanceof CertRep) {
            attributes.addAll(this.getResponseAttributes((CertRep)message));
        }
        for (final Attribute attribute : attributes) {
            table.put(attribute.getAttrType(), attribute);
        }
        return new AttributeTable((Hashtable)table);
    }
    
    private List<Attribute> getMessageAttributes(final PkiMessage<?> message) {
        final List<Attribute> attributes = new ArrayList<Attribute>();
        attributes.add(this.toAttribute(message.getTransactionId()));
        attributes.add(this.toAttribute(message.getMessageType()));
        attributes.add(this.toAttribute(message.getSenderNonce(), ScepObjectIdentifier.SENDER_NONCE.id()));
        return attributes;
    }
    
    private List<Attribute> getResponseAttributes(final CertRep message) {
        final List<Attribute> attributes = new ArrayList<Attribute>();
        attributes.add(this.toAttribute(message.getPkiStatus()));
        attributes.add(this.toAttribute(message.getRecipientNonce(), ScepObjectIdentifier.RECIPIENT_NONCE.id()));
        if (message.getPkiStatus() == PkiStatus.FAILURE) {
            attributes.add(this.toAttribute(message.getFailInfo()));
        }
        return attributes;
    }
    
    private Attribute toAttribute(final FailInfo failInfo) {
        final ASN1ObjectIdentifier oid = this.toOid(ScepObjectIdentifier.FAIL_INFO.id());
        return new Attribute(oid, (ASN1Set)new DERSet((ASN1Encodable)new DERPrintableString(Integer.toString(failInfo.getValue()))));
    }
    
    private Attribute toAttribute(final PkiStatus pkiStatus) {
        final ASN1ObjectIdentifier oid = this.toOid(ScepObjectIdentifier.PKI_STATUS.id());
        return new Attribute(oid, (ASN1Set)new DERSet((ASN1Encodable)new DERPrintableString(Integer.toString(pkiStatus.getValue()))));
    }
    
    private Attribute toAttribute(final Nonce nonce, final String id) {
        final ASN1ObjectIdentifier oid = this.toOid(id);
        return new Attribute(oid, (ASN1Set)new DERSet((ASN1Encodable)new DEROctetString(nonce.getBytes())));
    }
    
    private Attribute toAttribute(final MessageType messageType) {
        final ASN1ObjectIdentifier oid = this.toOid(ScepObjectIdentifier.MESSAGE_TYPE.id());
        return new Attribute(oid, (ASN1Set)new DERSet((ASN1Encodable)new DERPrintableString(Integer.toString(messageType.getValue()))));
    }
    
    private Attribute toAttribute(final TransactionId transId) {
        final ASN1ObjectIdentifier oid = this.toOid(ScepObjectIdentifier.TRANS_ID.id());
        return new Attribute(oid, (ASN1Set)new DERSet((ASN1Encodable)new DERPrintableString(transId.toString())));
    }
    
    private ASN1ObjectIdentifier toOid(final String oid) {
        return new ASN1ObjectIdentifier(oid);
    }
}
