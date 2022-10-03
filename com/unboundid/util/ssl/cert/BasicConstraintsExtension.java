package com.unboundid.util.ssl.cert;

import java.util.Collection;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.asn1.ASN1Boolean;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.util.OID;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class BasicConstraintsExtension extends X509CertificateExtension
{
    public static final OID BASIC_CONSTRAINTS_OID;
    private static final long serialVersionUID = 7597324354728536247L;
    private final boolean isCA;
    private final Integer pathLengthConstraint;
    
    BasicConstraintsExtension(final boolean isCritical, final boolean isCA, final Integer pathLengthConstraint) {
        super(BasicConstraintsExtension.BASIC_CONSTRAINTS_OID, isCritical, encodeValue(isCA, pathLengthConstraint));
        this.isCA = isCA;
        this.pathLengthConstraint = pathLengthConstraint;
    }
    
    BasicConstraintsExtension(final X509CertificateExtension extension) throws CertException {
        super(extension);
        try {
            boolean ca = false;
            Integer lengthConstraint = null;
            for (final ASN1Element e : ASN1Sequence.decodeAsSequence(extension.getValue()).elements()) {
                switch (e.getType()) {
                    case 1: {
                        ca = e.decodeAsBoolean().booleanValue();
                        break;
                    }
                    case 2: {
                        lengthConstraint = e.decodeAsInteger().intValue();
                        break;
                    }
                }
            }
            this.isCA = ca;
            this.pathLengthConstraint = lengthConstraint;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new CertException(CertMessages.ERR_BASIC_CONSTRAINTS_EXTENSION_CANNOT_PARSE.get(String.valueOf(extension), StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    private static byte[] encodeValue(final boolean isCA, final Integer pathLengthConstraint) {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(2);
        if (isCA) {
            elements.add(new ASN1Boolean(isCA));
        }
        if (pathLengthConstraint != null) {
            elements.add(new ASN1Integer(pathLengthConstraint));
        }
        return new ASN1Sequence(elements).encode();
    }
    
    public boolean isCA() {
        return this.isCA;
    }
    
    public Integer getPathLengthConstraint() {
        return this.pathLengthConstraint;
    }
    
    @Override
    public String getExtensionName() {
        return CertMessages.INFO_BASIC_CONSTRAINTS_EXTENSION_NAME.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("BasicConstraintsExtension(oid='");
        buffer.append(this.getOID());
        buffer.append("', isCritical=");
        buffer.append(this.isCritical());
        buffer.append(", isCA=");
        buffer.append(this.isCA);
        if (this.pathLengthConstraint != null) {
            buffer.append(", pathLengthConstraint=");
            buffer.append(this.pathLengthConstraint);
        }
        buffer.append(')');
    }
    
    static {
        BASIC_CONSTRAINTS_OID = new OID("2.5.29.19");
    }
}
