package com.unboundid.util.ssl.cert;

import java.util.Iterator;
import java.util.Collection;
import com.unboundid.asn1.ASN1Set;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1UTF8String;
import com.unboundid.asn1.ASN1ObjectIdentifier;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.schema.AttributeTypeDefinition;
import com.unboundid.util.OID;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.asn1.ASN1Element;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import com.unboundid.ldap.sdk.RDN;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class CRLDistributionPoint implements Serializable
{
    private static final byte TYPE_DISTRIBUTION_POINT = -96;
    private static final byte TYPE_REASONS = -127;
    private static final byte TYPE_CRL_ISSUER = -94;
    private static final byte TYPE_FULL_NAME = -96;
    private static final byte TYPE_NAME_RELATIVE_TO_CRL_ISSUER = -95;
    private static final long serialVersionUID = -8461308509960278714L;
    private final GeneralNames crlIssuer;
    private final GeneralNames fullName;
    private final RDN nameRelativeToCRLIssuer;
    private final Set<CRLDistributionPointRevocationReason> revocationReasons;
    
    CRLDistributionPoint(final GeneralNames fullName, final Set<CRLDistributionPointRevocationReason> revocationReasons, final GeneralNames crlIssuer) {
        this.fullName = fullName;
        this.crlIssuer = crlIssuer;
        this.nameRelativeToCRLIssuer = null;
        if (revocationReasons == null) {
            this.revocationReasons = Collections.unmodifiableSet((Set<? extends CRLDistributionPointRevocationReason>)EnumSet.allOf(CRLDistributionPointRevocationReason.class));
        }
        else {
            this.revocationReasons = Collections.unmodifiableSet((Set<? extends CRLDistributionPointRevocationReason>)revocationReasons);
        }
    }
    
    CRLDistributionPoint(final RDN nameRelativeToCRLIssuer, final Set<CRLDistributionPointRevocationReason> revocationReasons, final GeneralNames crlIssuer) {
        this.nameRelativeToCRLIssuer = nameRelativeToCRLIssuer;
        this.crlIssuer = crlIssuer;
        this.fullName = null;
        if (revocationReasons == null) {
            this.revocationReasons = Collections.unmodifiableSet((Set<? extends CRLDistributionPointRevocationReason>)EnumSet.allOf(CRLDistributionPointRevocationReason.class));
        }
        else {
            this.revocationReasons = Collections.unmodifiableSet((Set<? extends CRLDistributionPointRevocationReason>)revocationReasons);
        }
    }
    
    CRLDistributionPoint(final ASN1Element element) throws CertException {
        try {
            GeneralNames dpFullName = null;
            GeneralNames issuer = null;
            RDN dpRDN = null;
            Set<CRLDistributionPointRevocationReason> reasons = EnumSet.allOf(CRLDistributionPointRevocationReason.class);
            for (final ASN1Element e : element.decodeAsSequence().elements()) {
                Label_0342: {
                    switch (e.getType()) {
                        case -96: {
                            final ASN1Element innerElement = ASN1Element.decode(e.getValue());
                            switch (innerElement.getType()) {
                                case -96: {
                                    dpFullName = new GeneralNames(innerElement);
                                    break Label_0342;
                                }
                                case -95: {
                                    final Schema schema = Schema.getDefaultStandardSchema();
                                    final ASN1Element[] attributeSetElements = innerElement.decodeAsSet().elements();
                                    final String[] attributeNames = new String[attributeSetElements.length];
                                    final byte[][] attributeValues = new byte[attributeSetElements.length][];
                                    for (int j = 0; j < attributeSetElements.length; ++j) {
                                        final ASN1Element[] attributeTypeAndValueElements = attributeSetElements[j].decodeAsSequence().elements();
                                        final OID attributeTypeOID = attributeTypeAndValueElements[0].decodeAsObjectIdentifier().getOID();
                                        final AttributeTypeDefinition attributeType = schema.getAttributeType(attributeTypeOID.toString());
                                        if (attributeType == null) {
                                            attributeNames[j] = attributeTypeOID.toString();
                                        }
                                        else {
                                            attributeNames[j] = attributeType.getNameOrOID().toUpperCase();
                                        }
                                        attributeValues[j] = attributeTypeAndValueElements[1].decodeAsOctetString().getValue();
                                    }
                                    dpRDN = new RDN(attributeNames, attributeValues, schema);
                                    break Label_0342;
                                }
                                default: {
                                    throw new CertException(CertMessages.ERR_CRL_DP_UNRECOGNIZED_NAME_ELEMENT_TYPE.get(StaticUtils.toHex(innerElement.getType())));
                                }
                            }
                            break;
                        }
                        case -127: {
                            reasons = CRLDistributionPointRevocationReason.getReasonSet(e.decodeAsBitString());
                            break;
                        }
                        case -94: {
                            issuer = new GeneralNames(e);
                            break;
                        }
                    }
                }
            }
            this.fullName = dpFullName;
            this.nameRelativeToCRLIssuer = dpRDN;
            this.revocationReasons = Collections.unmodifiableSet((Set<? extends CRLDistributionPointRevocationReason>)reasons);
            this.crlIssuer = issuer;
        }
        catch (final CertException e2) {
            Debug.debugException(e2);
            throw e2;
        }
        catch (final Exception e3) {
            Debug.debugException(e3);
            throw new CertException(CertMessages.ERR_CRL_DP_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e3)), e3);
        }
    }
    
    ASN1Element encode() throws CertException {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(3);
        ASN1Element distributionPointElement = null;
        if (this.fullName != null) {
            distributionPointElement = new ASN1Element((byte)(-96), this.fullName.encode().getValue());
        }
        else if (this.nameRelativeToCRLIssuer != null) {
            Schema schema;
            try {
                schema = Schema.getDefaultStandardSchema();
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new CertException(CertMessages.ERR_CRL_DP_ENCODE_CANNOT_GET_SCHEMA.get(this.toString(), String.valueOf(this.nameRelativeToCRLIssuer), StaticUtils.getExceptionMessage(e)), e);
            }
            final String[] names = this.nameRelativeToCRLIssuer.getAttributeNames();
            final String[] values = this.nameRelativeToCRLIssuer.getAttributeValues();
            final ArrayList<ASN1Element> rdnElements = new ArrayList<ASN1Element>(names.length);
            for (int i = 0; i < names.length; ++i) {
                final AttributeTypeDefinition at = schema.getAttributeType(names[i]);
                if (at == null) {
                    throw new CertException(CertMessages.ERR_CRL_DP_ENCODE_UNKNOWN_ATTR_TYPE.get(this.toString(), String.valueOf(this.nameRelativeToCRLIssuer), names[i]));
                }
                try {
                    rdnElements.add(new ASN1Sequence(new ASN1Element[] { new ASN1ObjectIdentifier(at.getOID()), new ASN1UTF8String(values[i]) }));
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                    throw new CertException(CertMessages.ERR_CRL_DP_ENCODE_ERROR.get(this.toString(), String.valueOf(this.nameRelativeToCRLIssuer), StaticUtils.getExceptionMessage(e2)), e2);
                }
            }
            distributionPointElement = new ASN1Set((byte)(-95), rdnElements);
        }
        if (distributionPointElement != null) {
            elements.add(new ASN1Element((byte)(-96), distributionPointElement.encode()));
        }
        if (!this.revocationReasons.equals(EnumSet.allOf(CRLDistributionPointRevocationReason.class))) {
            elements.add(CRLDistributionPointRevocationReason.toBitString((byte)(-127), this.revocationReasons));
        }
        if (this.crlIssuer != null) {
            elements.add(new ASN1Element((byte)(-94), this.crlIssuer.encode().getValue()));
        }
        return new ASN1Sequence(elements);
    }
    
    public GeneralNames getFullName() {
        return this.fullName;
    }
    
    public RDN getNameRelativeToCRLIssuer() {
        return this.nameRelativeToCRLIssuer;
    }
    
    public Set<CRLDistributionPointRevocationReason> getPotentialRevocationReasons() {
        return this.revocationReasons;
    }
    
    public GeneralNames getCRLIssuer() {
        return this.crlIssuer;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("CRLDistributionPoint(");
        if (this.fullName != null) {
            buffer.append("fullName=");
            this.fullName.toString(buffer);
            buffer.append(", ");
        }
        else if (this.nameRelativeToCRLIssuer != null) {
            buffer.append("nameRelativeToCRLIssuer='");
            this.nameRelativeToCRLIssuer.toString(buffer);
            buffer.append("', ");
        }
        buffer.append("potentialRevocationReasons={");
        final Iterator<CRLDistributionPointRevocationReason> reasonIterator = this.revocationReasons.iterator();
        while (reasonIterator.hasNext()) {
            buffer.append('\'');
            buffer.append(reasonIterator.next().getName());
            buffer.append('\'');
            if (reasonIterator.hasNext()) {
                buffer.append(',');
            }
        }
        if (this.crlIssuer != null) {
            buffer.append(", crlIssuer=");
            this.crlIssuer.toString(buffer);
        }
        buffer.append('}');
    }
}
