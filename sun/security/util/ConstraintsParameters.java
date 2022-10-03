package sun.security.util;

import java.security.interfaces.ECKey;
import java.security.Timestamp;
import java.util.Date;
import java.security.cert.X509Certificate;
import java.security.Key;
import java.security.AlgorithmParameters;

public class ConstraintsParameters
{
    private final String algorithm;
    private final AlgorithmParameters algParams;
    private final Key key;
    private final X509Certificate cert;
    private final boolean trustedMatch;
    private final Date pkixDate;
    private final Timestamp jarTimestamp;
    private final String variant;
    private final String[] curveStr;
    private static final String[] EMPTYLIST;
    
    public ConstraintsParameters(final X509Certificate cert, final boolean trustedMatch, final Date pkixDate, final Timestamp jarTimestamp, final String s) {
        this.cert = cert;
        this.trustedMatch = trustedMatch;
        this.pkixDate = pkixDate;
        this.jarTimestamp = jarTimestamp;
        this.variant = ((s == null) ? "generic" : s);
        this.algorithm = null;
        this.algParams = null;
        this.key = null;
        if (cert != null) {
            this.curveStr = getNamedCurveFromKey(cert.getPublicKey());
        }
        else {
            this.curveStr = ConstraintsParameters.EMPTYLIST;
        }
    }
    
    public ConstraintsParameters(final String algorithm, final AlgorithmParameters algParams, final Key key, final String s) {
        this.algorithm = algorithm;
        this.algParams = algParams;
        this.key = key;
        this.curveStr = getNamedCurveFromKey(key);
        this.cert = null;
        this.trustedMatch = false;
        this.pkixDate = null;
        this.jarTimestamp = null;
        this.variant = ((s == null) ? "generic" : s);
    }
    
    public ConstraintsParameters(final X509Certificate x509Certificate) {
        this(x509Certificate, false, null, null, "generic");
    }
    
    public ConstraintsParameters(final Timestamp timestamp) {
        this(null, false, null, timestamp, "generic");
    }
    
    public String getAlgorithm() {
        return this.algorithm;
    }
    
    public AlgorithmParameters getAlgParams() {
        return this.algParams;
    }
    
    public Key getKey() {
        return this.key;
    }
    
    public boolean isTrustedMatch() {
        return this.trustedMatch;
    }
    
    public X509Certificate getCertificate() {
        return this.cert;
    }
    
    public Date getPKIXParamDate() {
        return this.pkixDate;
    }
    
    public Timestamp getJARTimestamp() {
        return this.jarTimestamp;
    }
    
    public String getVariant() {
        return this.variant;
    }
    
    public String[] getNamedCurve() {
        return this.curveStr;
    }
    
    public static String[] getNamedCurveFromKey(final Key key) {
        if (key instanceof ECKey) {
            final NamedCurve lookup = CurveDB.lookup(((ECKey)key).getParams());
            return (lookup == null) ? ConstraintsParameters.EMPTYLIST : CurveDB.getNamesByOID(lookup.getObjectId());
        }
        return ConstraintsParameters.EMPTYLIST;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Cert:       ");
        if (this.cert != null) {
            sb.append(this.cert.toString());
            sb.append("\nSigAlgo:    ");
            sb.append(this.cert.getSigAlgName());
        }
        else {
            sb.append("None");
        }
        sb.append("\nAlgParams:  ");
        if (this.getAlgParams() != null) {
            this.getAlgParams().toString();
        }
        else {
            sb.append("None");
        }
        sb.append("\nNamedCurves: ");
        final String[] namedCurve = this.getNamedCurve();
        for (int length = namedCurve.length, i = 0; i < length; ++i) {
            sb.append(namedCurve[i] + " ");
        }
        sb.append("\nVariant:    " + this.getVariant());
        return sb.toString();
    }
    
    static {
        EMPTYLIST = new String[0];
    }
}
