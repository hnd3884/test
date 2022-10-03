package com.unboundid.util.ssl.cert;

import java.util.Iterator;
import java.util.Collection;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import java.util.Collections;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Sequence;
import java.util.List;
import com.unboundid.util.OID;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class CRLDistributionPointsExtension extends X509CertificateExtension
{
    public static final OID CRL_DISTRIBUTION_POINTS_OID;
    private static final long serialVersionUID = -4710958813506834961L;
    private final List<CRLDistributionPoint> crlDistributionPoints;
    
    CRLDistributionPointsExtension(final boolean isCritical, final List<CRLDistributionPoint> crlDistributionPoints) throws CertException {
        super(CRLDistributionPointsExtension.CRL_DISTRIBUTION_POINTS_OID, isCritical, encodeValue(crlDistributionPoints));
        this.crlDistributionPoints = crlDistributionPoints;
    }
    
    CRLDistributionPointsExtension(final X509CertificateExtension extension) throws CertException {
        super(extension);
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(extension.getValue()).elements();
            final ArrayList<CRLDistributionPoint> dps = new ArrayList<CRLDistributionPoint>(elements.length);
            for (final ASN1Element e : elements) {
                dps.add(new CRLDistributionPoint(e));
            }
            this.crlDistributionPoints = Collections.unmodifiableList((List<? extends CRLDistributionPoint>)dps);
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new CertException(CertMessages.ERR_CRL_DP_EXTENSION_CANNOT_PARSE.get(String.valueOf(extension), StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    private static byte[] encodeValue(final List<CRLDistributionPoint> crlDistributionPoints) throws CertException {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(crlDistributionPoints.size());
        for (final CRLDistributionPoint p : crlDistributionPoints) {
            elements.add(p.encode());
        }
        return new ASN1Sequence(elements).encode();
    }
    
    public List<CRLDistributionPoint> getCRLDistributionPoints() {
        return this.crlDistributionPoints;
    }
    
    @Override
    public String getExtensionName() {
        return CertMessages.INFO_CRL_DP_EXTENSION_NAME.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("CRLDistributionPointsExtension(oid='");
        buffer.append(this.getOID());
        buffer.append("', isCritical=");
        buffer.append(this.isCritical());
        buffer.append(", distributionPoints={");
        final Iterator<CRLDistributionPoint> iterator = this.crlDistributionPoints.iterator();
        while (iterator.hasNext()) {
            iterator.next().toString(buffer);
            if (iterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("})");
    }
    
    static {
        CRL_DISTRIBUTION_POINTS_OID = new OID("2.5.29.31");
    }
}
