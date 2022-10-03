package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.util.HashMap;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.ECParameterSpec;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Set;
import java.util.EnumSet;
import java.security.CryptoPrimitive;
import java.security.AlgorithmConstraints;
import java.io.IOException;
import javax.net.ssl.SSLProtocolException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.AlgorithmParameters;
import java.util.Map;

final class EllipticCurvesExtension extends HelloExtension
{
    private static final Debug debug;
    private static final int ARBITRARY_PRIME = 65281;
    private static final int ARBITRARY_CHAR2 = 65282;
    private static final Map<String, Integer> oidToIdMap;
    private static final Map<Integer, String> idToOidMap;
    private static final Map<Integer, AlgorithmParameters> idToParams;
    private static final int[] supportedCurveIds;
    private final int[] curveIds;
    
    private static boolean isAvailableCurve(final int curveId) {
        final String oid = EllipticCurvesExtension.idToOidMap.get(curveId);
        if (oid != null) {
            AlgorithmParameters params = null;
            try {
                params = JsseJce.getAlgorithmParameters("EC");
                params.init(new ECGenParameterSpec(oid));
            }
            catch (final Exception e) {
                return false;
            }
            EllipticCurvesExtension.idToParams.put(curveId, params);
            return true;
        }
        return false;
    }
    
    private EllipticCurvesExtension(final int[] curveIds) {
        super(ExtensionType.EXT_ELLIPTIC_CURVES);
        this.curveIds = curveIds;
    }
    
    EllipticCurvesExtension(final HandshakeInStream s, final int len) throws IOException {
        super(ExtensionType.EXT_ELLIPTIC_CURVES);
        final int k = s.getInt16();
        if ((len & 0x1) != 0x0 || k + 2 != len) {
            throw new SSLProtocolException("Invalid " + this.type + " extension");
        }
        this.curveIds = new int[k >> 1];
        for (int i = 0; i < this.curveIds.length; ++i) {
            this.curveIds[i] = s.getInt16();
        }
    }
    
    private static boolean isPermitted(final int curveId, final AlgorithmConstraints constraints) {
        return constraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), getCurveName(curveId), null) && constraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), "EC", EllipticCurvesExtension.idToParams.get(curveId));
    }
    
    static boolean isPermitted(final String name, final AlgorithmConstraints constraints) {
        final NamedEllipticCurve nec = NamedEllipticCurve.getCurve(name, false);
        return nec != null && isPermitted(nec.id, constraints);
    }
    
    static int getActiveCurves(final AlgorithmConstraints constraints) {
        return getPreferredCurve(EllipticCurvesExtension.supportedCurveIds, constraints);
    }
    
    static boolean hasActiveCurves(final AlgorithmConstraints constraints) {
        return getActiveCurves(constraints) >= 0;
    }
    
    static EllipticCurvesExtension createExtension(final AlgorithmConstraints constraints) {
        final ArrayList<Integer> idList = new ArrayList<Integer>(EllipticCurvesExtension.supportedCurveIds.length);
        for (final int curveId : EllipticCurvesExtension.supportedCurveIds) {
            if (isPermitted(curveId, constraints)) {
                idList.add(curveId);
            }
        }
        if (!idList.isEmpty()) {
            final int[] ids = new int[idList.size()];
            int i = 0;
            for (final Integer id : idList) {
                ids[i++] = id;
            }
            return new EllipticCurvesExtension(ids);
        }
        return null;
    }
    
    int getPreferredCurve(final AlgorithmConstraints constraints) {
        return getPreferredCurve(this.curveIds, constraints);
    }
    
    private static int getPreferredCurve(final int[] curves, final AlgorithmConstraints constraints) {
        for (final int curveId : curves) {
            if (isSupported(curveId) && isPermitted(curveId, constraints)) {
                return curveId;
            }
        }
        return -1;
    }
    
    boolean contains(final int index) {
        for (final int curveId : this.curveIds) {
            if (index == curveId) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    int length() {
        return 6 + (this.curveIds.length << 1);
    }
    
    @Override
    void send(final HandshakeOutStream s) throws IOException {
        s.putInt16(this.type.id);
        final int k = this.curveIds.length << 1;
        s.putInt16(k + 2);
        s.putInt16(k);
        for (final int curveId : this.curveIds) {
            s.putInt16(curveId);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Extension " + this.type + ", curve names: {");
        boolean first = true;
        for (final int curveId : this.curveIds) {
            if (first) {
                first = false;
            }
            else {
                sb.append(", ");
            }
            final String curveName = getCurveName(curveId);
            if (curveName != null) {
                sb.append(curveName);
            }
            else if (curveId == 65281) {
                sb.append("arbitrary_explicit_prime_curves");
            }
            else if (curveId == 65282) {
                sb.append("arbitrary_explicit_char2_curves");
            }
            else {
                sb.append("unknown curve " + curveId);
            }
        }
        sb.append("}");
        return sb.toString();
    }
    
    static boolean isSupported(final int index) {
        for (final int curveId : EllipticCurvesExtension.supportedCurveIds) {
            if (index == curveId) {
                return true;
            }
        }
        return false;
    }
    
    static int getCurveIndex(final ECParameterSpec params) {
        final String oid = JsseJce.getNamedCurveOid(params);
        if (oid == null) {
            return -1;
        }
        final Integer n = EllipticCurvesExtension.oidToIdMap.get(oid);
        return (n == null) ? -1 : n;
    }
    
    static String getCurveOid(final int index) {
        return EllipticCurvesExtension.idToOidMap.get(index);
    }
    
    static ECGenParameterSpec getECGenParamSpec(final int index) {
        final AlgorithmParameters params = EllipticCurvesExtension.idToParams.get(index);
        try {
            return params.getParameterSpec(ECGenParameterSpec.class);
        }
        catch (final InvalidParameterSpecException ipse) {
            final String curveOid = getCurveOid(index);
            return new ECGenParameterSpec(curveOid);
        }
    }
    
    private static String getCurveName(final int index) {
        for (final NamedEllipticCurve namedCurve : NamedEllipticCurve.values()) {
            if (namedCurve.id == index) {
                return namedCurve.name;
            }
        }
        return null;
    }
    
    static {
        debug = Debug.getInstance("ssl");
        oidToIdMap = new HashMap<String, Integer>();
        idToOidMap = new HashMap<Integer, String>();
        idToParams = new HashMap<Integer, AlgorithmParameters>();
        final boolean requireFips = Legacy8uJSSE.isFIPS();
        final NamedEllipticCurve nec = NamedEllipticCurve.getCurve("secp256r1", false);
        String property = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jdk.tls.namedGroups"));
        if (property != null && property.length() != 0 && property.length() > 1 && property.charAt(0) == '\"' && property.charAt(property.length() - 1) == '\"') {
            property = property.substring(1, property.length() - 1);
        }
        ArrayList<Integer> idList;
        if (property != null && property.length() != 0) {
            final String[] curves = property.split(",");
            idList = new ArrayList<Integer>(curves.length);
            for (String curve : curves) {
                curve = curve.trim();
                if (!curve.isEmpty()) {
                    final NamedEllipticCurve namedCurve = NamedEllipticCurve.getCurve(curve, requireFips);
                    if (namedCurve != null && isAvailableCurve(namedCurve.id)) {
                        idList.add(namedCurve.id);
                    }
                }
            }
            if (idList.isEmpty() && JsseJce.isEcAvailable()) {
                throw new IllegalArgumentException("System property jdk.tls.namedGroups(" + property + ") contains no supported elliptic curves");
            }
        }
        else {
            final int[] ids = { 23, 24, 25 };
            idList = new ArrayList<Integer>(ids.length);
            for (final int curveId : ids) {
                if (isAvailableCurve(curveId)) {
                    idList.add(curveId);
                }
            }
        }
        if (EllipticCurvesExtension.debug != null && idList.isEmpty()) {
            EllipticCurvesExtension.debug.println("Initialized [jdk.tls.namedGroups|default] list contains no available elliptic curves. " + ((property != null) ? ("(" + property + ")") : "[Default]"));
        }
        supportedCurveIds = new int[idList.size()];
        int i = 0;
        for (final Integer id : idList) {
            EllipticCurvesExtension.supportedCurveIds[i++] = id;
        }
    }
    
    private enum NamedEllipticCurve
    {
        T163_K1(1, "sect163k1", "1.3.132.0.1", true), 
        T163_R1(2, "sect163r1", "1.3.132.0.2", false), 
        T163_R2(3, "sect163r2", "1.3.132.0.15", true), 
        T193_R1(4, "sect193r1", "1.3.132.0.24", false), 
        T193_R2(5, "sect193r2", "1.3.132.0.25", false), 
        T233_K1(6, "sect233k1", "1.3.132.0.26", true), 
        T233_R1(7, "sect233r1", "1.3.132.0.27", true), 
        T239_K1(8, "sect239k1", "1.3.132.0.3", false), 
        T283_K1(9, "sect283k1", "1.3.132.0.16", true), 
        T283_R1(10, "sect283r1", "1.3.132.0.17", true), 
        T409_K1(11, "sect409k1", "1.3.132.0.36", true), 
        T409_R1(12, "sect409r1", "1.3.132.0.37", true), 
        T571_K1(13, "sect571k1", "1.3.132.0.38", true), 
        T571_R1(14, "sect571r1", "1.3.132.0.39", true), 
        P160_K1(15, "secp160k1", "1.3.132.0.9", false), 
        P160_R1(16, "secp160r1", "1.3.132.0.8", false), 
        P160_R2(17, "secp160r2", "1.3.132.0.30", false), 
        P192_K1(18, "secp192k1", "1.3.132.0.31", false), 
        P192_R1(19, "secp192r1", "1.2.840.10045.3.1.1", true), 
        P224_K1(20, "secp224k1", "1.3.132.0.32", false), 
        P224_R1(21, "secp224r1", "1.3.132.0.33", true), 
        P256_K1(22, "secp256k1", "1.3.132.0.10", false), 
        P256_R1(23, "secp256r1", "1.2.840.10045.3.1.7", true), 
        P384_R1(24, "secp384r1", "1.3.132.0.34", true), 
        P521_R1(25, "secp521r1", "1.3.132.0.35", true);
        
        int id;
        String name;
        String oid;
        boolean isFips;
        
        private NamedEllipticCurve(final int id, final String name, final String oid, final boolean isFips) {
            this.id = id;
            this.name = name;
            this.oid = oid;
            this.isFips = isFips;
            if (EllipticCurvesExtension.oidToIdMap.put(oid, id) != null || EllipticCurvesExtension.idToOidMap.put(id, oid) != null) {
                throw new RuntimeException("Duplicate named elliptic curve definition: " + name);
            }
        }
        
        static NamedEllipticCurve getCurve(final String name, final boolean requireFips) {
            for (final NamedEllipticCurve curve : values()) {
                if (curve.name.equals(name) && (!requireFips || curve.isFips)) {
                    return curve;
                }
            }
            return null;
        }
    }
}
