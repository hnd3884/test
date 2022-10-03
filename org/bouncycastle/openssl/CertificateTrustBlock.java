package org.bouncycastle.openssl;

import org.bouncycastle.asn1.DERTaggedObject;
import java.util.Iterator;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import java.util.Collections;
import java.util.HashSet;
import java.util.Enumeration;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Sequence;

public class CertificateTrustBlock
{
    private ASN1Sequence uses;
    private ASN1Sequence prohibitions;
    private String alias;
    
    public CertificateTrustBlock(final Set<ASN1ObjectIdentifier> set) {
        this(null, set, null);
    }
    
    public CertificateTrustBlock(final String s, final Set<ASN1ObjectIdentifier> set) {
        this(s, set, null);
    }
    
    public CertificateTrustBlock(final String alias, final Set<ASN1ObjectIdentifier> set, final Set<ASN1ObjectIdentifier> set2) {
        this.alias = alias;
        this.uses = this.toSequence(set);
        this.prohibitions = this.toSequence(set2);
    }
    
    CertificateTrustBlock(final byte[] array) {
        final Enumeration objects = ASN1Sequence.getInstance((Object)array).getObjects();
        while (objects.hasMoreElements()) {
            final ASN1Encodable asn1Encodable = objects.nextElement();
            if (asn1Encodable instanceof ASN1Sequence) {
                this.uses = ASN1Sequence.getInstance((Object)asn1Encodable);
            }
            else if (asn1Encodable instanceof ASN1TaggedObject) {
                this.prohibitions = ASN1Sequence.getInstance((ASN1TaggedObject)asn1Encodable, false);
            }
            else {
                if (!(asn1Encodable instanceof DERUTF8String)) {
                    continue;
                }
                this.alias = DERUTF8String.getInstance((Object)asn1Encodable).getString();
            }
        }
    }
    
    public String getAlias() {
        return this.alias;
    }
    
    public Set<ASN1ObjectIdentifier> getUses() {
        return this.toSet(this.uses);
    }
    
    public Set<ASN1ObjectIdentifier> getProhibitions() {
        return this.toSet(this.prohibitions);
    }
    
    private Set<ASN1ObjectIdentifier> toSet(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence != null) {
            final HashSet set = new HashSet(asn1Sequence.size());
            final Enumeration objects = asn1Sequence.getObjects();
            while (objects.hasMoreElements()) {
                set.add(ASN1ObjectIdentifier.getInstance(objects.nextElement()));
            }
            return set;
        }
        return Collections.EMPTY_SET;
    }
    
    private ASN1Sequence toSequence(final Set<ASN1ObjectIdentifier> set) {
        if (set == null || set.isEmpty()) {
            return null;
        }
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            asn1EncodableVector.add((ASN1Encodable)iterator.next());
        }
        return (ASN1Sequence)new DERSequence(asn1EncodableVector);
    }
    
    ASN1Sequence toASN1Sequence() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.uses != null) {
            asn1EncodableVector.add((ASN1Encodable)this.uses);
        }
        if (this.prohibitions != null) {
            asn1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.prohibitions));
        }
        if (this.alias != null) {
            asn1EncodableVector.add((ASN1Encodable)new DERUTF8String(this.alias));
        }
        return (ASN1Sequence)new DERSequence(asn1EncodableVector);
    }
}
