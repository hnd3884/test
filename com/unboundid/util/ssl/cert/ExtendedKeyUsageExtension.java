package com.unboundid.util.ssl.cert;

import java.util.Iterator;
import com.unboundid.asn1.ASN1ObjectIdentifier;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Sequence;
import java.util.Collections;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import com.unboundid.util.OID;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ExtendedKeyUsageExtension extends X509CertificateExtension
{
    public static final OID EXTENDED_KEY_USAGE_OID;
    private static final long serialVersionUID = -8208115548961483723L;
    private final Set<OID> keyPurposeIDs;
    
    ExtendedKeyUsageExtension(final boolean isCritical, final List<OID> keyPurposeIDs) throws CertException {
        super(ExtendedKeyUsageExtension.EXTENDED_KEY_USAGE_OID, isCritical, encodeValue(keyPurposeIDs));
        this.keyPurposeIDs = Collections.unmodifiableSet((Set<? extends OID>)new LinkedHashSet<OID>(keyPurposeIDs));
    }
    
    ExtendedKeyUsageExtension(final X509CertificateExtension extension) throws CertException {
        super(extension);
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(extension.getValue()).elements();
            final LinkedHashSet<OID> ids = new LinkedHashSet<OID>(StaticUtils.computeMapCapacity(elements.length));
            for (final ASN1Element e : elements) {
                ids.add(e.decodeAsObjectIdentifier().getOID());
            }
            this.keyPurposeIDs = Collections.unmodifiableSet((Set<? extends OID>)ids);
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new CertException(CertMessages.ERR_EXTENDED_KEY_USAGE_EXTENSION_CANNOT_PARSE.get(String.valueOf(extension), StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    private static byte[] encodeValue(final List<OID> keyPurposeIDs) throws CertException {
        try {
            final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(keyPurposeIDs.size());
            for (final OID oid : keyPurposeIDs) {
                elements.add(new ASN1ObjectIdentifier(oid));
            }
            return new ASN1Sequence(elements).encode();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new CertException(CertMessages.ERR_EXTENDED_KEY_USAGE_EXTENSION_CANNOT_ENCODE.get(String.valueOf(keyPurposeIDs), StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public Set<OID> getKeyPurposeIDs() {
        return this.keyPurposeIDs;
    }
    
    @Override
    public String getExtensionName() {
        return CertMessages.INFO_EXTENDED_KEY_USAGE_EXTENSION_NAME.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ExtendedKeyUsageExtension(oid='");
        buffer.append(this.getOID());
        buffer.append("', isCritical=");
        buffer.append(this.isCritical());
        buffer.append(", keyPurposeIDs={");
        final Iterator<OID> oidIterator = this.keyPurposeIDs.iterator();
        while (oidIterator.hasNext()) {
            buffer.append('\'');
            buffer.append(ExtendedKeyUsageID.getNameOrOID(oidIterator.next()));
            buffer.append('\'');
            if (oidIterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("})");
    }
    
    static {
        EXTENDED_KEY_USAGE_OID = new OID("2.5.29.37");
    }
}
