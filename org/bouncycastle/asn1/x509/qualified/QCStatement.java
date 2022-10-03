package org.bouncycastle.asn1.x509.qualified;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class QCStatement extends ASN1Object implements ETSIQCObjectIdentifiers, RFC3739QCObjectIdentifiers
{
    ASN1ObjectIdentifier qcStatementId;
    ASN1Encodable qcStatementInfo;
    
    public static QCStatement getInstance(final Object o) {
        if (o instanceof QCStatement) {
            return (QCStatement)o;
        }
        if (o != null) {
            return new QCStatement(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private QCStatement(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        this.qcStatementId = ASN1ObjectIdentifier.getInstance(objects.nextElement());
        if (objects.hasMoreElements()) {
            this.qcStatementInfo = (ASN1Encodable)objects.nextElement();
        }
    }
    
    public QCStatement(final ASN1ObjectIdentifier qcStatementId) {
        this.qcStatementId = qcStatementId;
        this.qcStatementInfo = null;
    }
    
    public QCStatement(final ASN1ObjectIdentifier qcStatementId, final ASN1Encodable qcStatementInfo) {
        this.qcStatementId = qcStatementId;
        this.qcStatementInfo = qcStatementInfo;
    }
    
    public ASN1ObjectIdentifier getStatementId() {
        return this.qcStatementId;
    }
    
    public ASN1Encodable getStatementInfo() {
        return this.qcStatementInfo;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.qcStatementId);
        if (this.qcStatementInfo != null) {
            asn1EncodableVector.add(this.qcStatementInfo);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
