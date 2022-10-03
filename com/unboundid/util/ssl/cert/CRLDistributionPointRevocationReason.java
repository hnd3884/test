package com.unboundid.util.ssl.cert;

import com.unboundid.util.StaticUtils;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import com.unboundid.asn1.ASN1BitString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum CRLDistributionPointRevocationReason
{
    UNSPECIFIED("unspecified", 0), 
    KEY_COMPROMISE("keyCompromise", 1), 
    CA_COMPROMISE("caCompromise", 2), 
    AFFILIATION_CHANGED("affiliationChanged", 3), 
    SUPERSEDED("superseded", 4), 
    CESSATION_OF_OPERATION("cessationOfOperation", 5), 
    CERTIFICATE_HOLD("certificateHold", 6), 
    PRIVILEGE_WITHDRAWN("privilegeWithdrawn", 7), 
    AA_COMPROMISE("aaCompromise", 8);
    
    private final int bitPosition;
    private final String name;
    
    private CRLDistributionPointRevocationReason(final String name, final int bitPosition) {
        this.name = name;
        this.bitPosition = bitPosition;
    }
    
    public String getName() {
        return this.name;
    }
    
    int getBitPosition() {
        return this.bitPosition;
    }
    
    static Set<CRLDistributionPointRevocationReason> getReasonSet(final ASN1BitString bitString) {
        final boolean[] bits = bitString.getBits();
        final EnumSet<CRLDistributionPointRevocationReason> s = EnumSet.noneOf(CRLDistributionPointRevocationReason.class);
        for (final CRLDistributionPointRevocationReason r : values()) {
            if (bits.length > r.bitPosition && bits[r.bitPosition]) {
                s.add(r);
            }
        }
        return Collections.unmodifiableSet((Set<? extends CRLDistributionPointRevocationReason>)s);
    }
    
    static ASN1BitString toBitString(final byte type, final Set<CRLDistributionPointRevocationReason> reasons) {
        final CRLDistributionPointRevocationReason[] values = values();
        final boolean[] bits = new boolean[values.length];
        for (final CRLDistributionPointRevocationReason r : values) {
            bits[r.bitPosition] = reasons.contains(r);
        }
        return new ASN1BitString(type, bits);
    }
    
    public static CRLDistributionPointRevocationReason forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "unspecified": {
                return CRLDistributionPointRevocationReason.UNSPECIFIED;
            }
            case "keycompromise":
            case "key-compromise":
            case "key_compromise": {
                return CRLDistributionPointRevocationReason.KEY_COMPROMISE;
            }
            case "cacompromise":
            case "ca-compromise":
            case "ca_compromise": {
                return CRLDistributionPointRevocationReason.CA_COMPROMISE;
            }
            case "affiliationchanged":
            case "affiliation-changed":
            case "affiliation_changed": {
                return CRLDistributionPointRevocationReason.AFFILIATION_CHANGED;
            }
            case "superseded": {
                return CRLDistributionPointRevocationReason.SUPERSEDED;
            }
            case "cessationofoperation":
            case "cessation-of-operation":
            case "cessation_of_operation": {
                return CRLDistributionPointRevocationReason.CESSATION_OF_OPERATION;
            }
            case "certificatehold":
            case "certificate-hold":
            case "certificate_hold": {
                return CRLDistributionPointRevocationReason.CERTIFICATE_HOLD;
            }
            case "privilegewithdrawn":
            case "privilege-withdrawn":
            case "privilege_withdrawn": {
                return CRLDistributionPointRevocationReason.PRIVILEGE_WITHDRAWN;
            }
            case "aacompromise":
            case "aa-compromise":
            case "aa_compromise": {
                return CRLDistributionPointRevocationReason.AA_COMPROMISE;
            }
            default: {
                return null;
            }
        }
    }
}
