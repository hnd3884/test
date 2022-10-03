package sun.security.util;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.util.TimeZone;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
import java.security.Key;
import java.security.AlgorithmParameters;
import java.security.CryptoPrimitive;
import java.util.Set;
import java.util.Iterator;
import java.util.Collection;
import java.util.List;

public class DisabledAlgorithmConstraints extends AbstractAlgorithmConstraints
{
    private static final Debug debug;
    public static final String PROPERTY_CERTPATH_DISABLED_ALGS = "jdk.certpath.disabledAlgorithms";
    public static final String PROPERTY_TLS_DISABLED_ALGS = "jdk.tls.disabledAlgorithms";
    public static final String PROPERTY_JAR_DISABLED_ALGS = "jdk.jar.disabledAlgorithms";
    private static final String PROPERTY_DISABLED_EC_CURVES = "jdk.disabled.namedCurves";
    private final List<String> disabledAlgorithms;
    private final Constraints algorithmConstraints;
    
    public DisabledAlgorithmConstraints(final String s) {
        this(s, new AlgorithmDecomposer());
    }
    
    public DisabledAlgorithmConstraints(final String s, final AlgorithmDecomposer algorithmDecomposer) {
        super(algorithmDecomposer);
        this.disabledAlgorithms = AbstractAlgorithmConstraints.getAlgorithms(s);
        int n = -1;
        int n2 = 0;
        for (final String s2 : this.disabledAlgorithms) {
            if (s2.regionMatches(true, 0, "include ", 0, 8) && s2.regionMatches(true, 8, "jdk.disabled.namedCurves", 0, "jdk.disabled.namedCurves".length())) {
                n = n2;
                break;
            }
            ++n2;
        }
        if (n > -1) {
            this.disabledAlgorithms.remove(n);
            this.disabledAlgorithms.addAll(n, AbstractAlgorithmConstraints.getAlgorithms("jdk.disabled.namedCurves"));
        }
        this.algorithmConstraints = new Constraints(this.disabledAlgorithms);
    }
    
    @Override
    public final boolean permits(final Set<CryptoPrimitive> set, final String s, final AlgorithmParameters algorithmParameters) {
        return AbstractAlgorithmConstraints.checkAlgorithm(this.disabledAlgorithms, s, this.decomposer) && (algorithmParameters == null || this.algorithmConstraints.permits(s, algorithmParameters));
    }
    
    @Override
    public final boolean permits(final Set<CryptoPrimitive> set, final Key key) {
        return this.checkConstraints(set, "", key, null);
    }
    
    @Override
    public final boolean permits(final Set<CryptoPrimitive> set, final String s, final Key key, final AlgorithmParameters algorithmParameters) {
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("No algorithm name specified");
        }
        return this.checkConstraints(set, s, key, algorithmParameters);
    }
    
    public final void permits(final ConstraintsParameters constraintsParameters) throws CertPathValidatorException {
        this.permits(constraintsParameters.getAlgorithm(), constraintsParameters);
    }
    
    public final void permits(final String s, final Key key, final AlgorithmParameters algorithmParameters, final String s2) throws CertPathValidatorException {
        this.permits(s, new ConstraintsParameters(s, algorithmParameters, key, (s2 == null) ? "generic" : s2));
    }
    
    public final void permits(final String s, final ConstraintsParameters constraintsParameters) throws CertPathValidatorException {
        if (constraintsParameters.getNamedCurve() != null) {
            for (final String s2 : constraintsParameters.getNamedCurve()) {
                if (!AbstractAlgorithmConstraints.checkAlgorithm(this.disabledAlgorithms, s2, this.decomposer)) {
                    throw new CertPathValidatorException("Algorithm constraints check failed on disabled algorithm: " + s2, null, null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED);
                }
            }
        }
        this.algorithmConstraints.permits(s, constraintsParameters);
    }
    
    public boolean checkProperty(String lowerCase) {
        lowerCase = lowerCase.toLowerCase(Locale.ENGLISH);
        final Iterator<String> iterator = this.disabledAlgorithms.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().toLowerCase(Locale.ENGLISH).indexOf(lowerCase) >= 0) {
                return true;
            }
        }
        return false;
    }
    
    private boolean checkConstraints(final Set<CryptoPrimitive> set, final String s, final Key key, final AlgorithmParameters algorithmParameters) {
        if (key == null) {
            throw new IllegalArgumentException("The key cannot be null");
        }
        if (s != null && s.length() != 0 && !this.permits(set, s, algorithmParameters)) {
            return false;
        }
        if (!this.permits(set, key.getAlgorithm(), null)) {
            return false;
        }
        final String[] namedCurveFromKey = ConstraintsParameters.getNamedCurveFromKey(key);
        for (int length = namedCurveFromKey.length, i = 0; i < length; ++i) {
            if (!this.permits(set, namedCurveFromKey[i], null)) {
                return false;
            }
        }
        return this.algorithmConstraints.permits(key);
    }
    
    static {
        debug = Debug.getInstance("certpath");
    }
    
    private static class Constraints
    {
        private Map<String, List<Constraint>> constraintsMap;
        
        public Constraints(final List<String> list) {
            this.constraintsMap = new HashMap<String, List<Constraint>>();
            for (final String s : list) {
                if (s != null) {
                    if (s.isEmpty()) {
                        continue;
                    }
                    final String trim = s.trim();
                    if (DisabledAlgorithmConstraints.debug != null) {
                        DisabledAlgorithmConstraints.debug.println("Constraints: " + trim);
                    }
                    final int index = trim.indexOf(32);
                    final String hashName = AlgorithmDecomposer.hashName((index > 0) ? trim.substring(0, index) : trim);
                    final List<Constraint> list2 = this.constraintsMap.getOrDefault(hashName.toUpperCase(Locale.ENGLISH), new ArrayList<Constraint>(1));
                    final Iterator<String> iterator2 = AlgorithmDecomposer.getAliases(hashName).iterator();
                    while (iterator2.hasNext()) {
                        this.constraintsMap.putIfAbsent(iterator2.next().toUpperCase(Locale.ENGLISH), list2);
                    }
                    if (index <= 0 || CurveDB.lookup(trim) != null) {
                        list2.add(new DisabledConstraint(hashName));
                    }
                    else {
                        final String substring = trim.substring(index + 1);
                        Constraint constraint = null;
                        int n = 0;
                        int n2 = 0;
                        final String[] split = substring.split("&");
                        for (int length = split.length, i = 0; i < length; ++i) {
                            final String trim2 = split[i].trim();
                            Constraint nextConstraint;
                            if (trim2.startsWith("keySize")) {
                                if (DisabledAlgorithmConstraints.debug != null) {
                                    DisabledAlgorithmConstraints.debug.println("Constraints set to keySize: " + trim2);
                                }
                                final StringTokenizer stringTokenizer = new StringTokenizer(trim2);
                                if (!"keySize".equals(stringTokenizer.nextToken())) {
                                    throw new IllegalArgumentException("Error in security property. Constraint unknown: " + trim2);
                                }
                                nextConstraint = new KeySizeConstraint(hashName, Constraint.Operator.of(stringTokenizer.nextToken()), Integer.parseInt(stringTokenizer.nextToken()));
                            }
                            else if (trim2.equalsIgnoreCase("jdkCA")) {
                                if (DisabledAlgorithmConstraints.debug != null) {
                                    DisabledAlgorithmConstraints.debug.println("Constraints set to jdkCA.");
                                }
                                if (n != 0) {
                                    throw new IllegalArgumentException("Only one jdkCA entry allowed in property. Constraint: " + trim);
                                }
                                nextConstraint = new jdkCAConstraint(hashName);
                                n = 1;
                            }
                            else {
                                final Matcher matcher;
                                if (trim2.startsWith("denyAfter") && (matcher = Holder.DENY_AFTER_PATTERN.matcher(trim2)).matches()) {
                                    if (DisabledAlgorithmConstraints.debug != null) {
                                        DisabledAlgorithmConstraints.debug.println("Constraints set to denyAfter");
                                    }
                                    if (n2 != 0) {
                                        throw new IllegalArgumentException("Only one denyAfter entry allowed in property. Constraint: " + trim);
                                    }
                                    nextConstraint = new DenyAfterConstraint(hashName, Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)));
                                    n2 = 1;
                                }
                                else {
                                    if (!trim2.startsWith("usage")) {
                                        throw new IllegalArgumentException("Error in security property. Constraint unknown: " + trim2);
                                    }
                                    final String[] split2 = trim2.substring(5).trim().split(" ");
                                    nextConstraint = new UsageConstraint(hashName, split2);
                                    if (DisabledAlgorithmConstraints.debug != null) {
                                        DisabledAlgorithmConstraints.debug.println("Constraints usage length is " + split2.length);
                                    }
                                }
                            }
                            if (constraint == null) {
                                list2.add(nextConstraint);
                            }
                            else {
                                constraint.nextConstraint = nextConstraint;
                            }
                            constraint = nextConstraint;
                        }
                    }
                }
            }
        }
        
        private List<Constraint> getConstraints(final String s) {
            return this.constraintsMap.get(s.toUpperCase(Locale.ENGLISH));
        }
        
        public boolean permits(final Key key) {
            final List<Constraint> constraints = this.getConstraints(key.getAlgorithm());
            if (constraints == null) {
                return true;
            }
            final Iterator<Constraint> iterator = constraints.iterator();
            while (iterator.hasNext()) {
                if (!iterator.next().permits(key)) {
                    if (DisabledAlgorithmConstraints.debug != null) {
                        DisabledAlgorithmConstraints.debug.println("Constraints: failed key sizeconstraint check " + KeyUtil.getKeySize(key));
                    }
                    return false;
                }
            }
            return true;
        }
        
        public boolean permits(final String s, final AlgorithmParameters algorithmParameters) {
            final List<Constraint> constraints = this.getConstraints(s);
            if (constraints == null) {
                return true;
            }
            final Iterator<Constraint> iterator = constraints.iterator();
            while (iterator.hasNext()) {
                if (!iterator.next().permits(algorithmParameters)) {
                    if (DisabledAlgorithmConstraints.debug != null) {
                        DisabledAlgorithmConstraints.debug.println("Constraints: failed algorithm parameters constraint check " + algorithmParameters);
                    }
                    return false;
                }
            }
            return true;
        }
        
        public void permits(final String s, final ConstraintsParameters constraintsParameters) throws CertPathValidatorException {
            final X509Certificate certificate = constraintsParameters.getCertificate();
            if (DisabledAlgorithmConstraints.debug != null) {
                DisabledAlgorithmConstraints.debug.println("Constraints.permits(): " + constraintsParameters.toString());
            }
            final HashSet set = new HashSet();
            if (s != null) {
                set.addAll(AlgorithmDecomposer.decomposeOneHash(s));
                set.add(s);
            }
            if (certificate != null) {
                set.add(certificate.getPublicKey().getAlgorithm());
            }
            if (constraintsParameters.getKey() != null) {
                set.add(constraintsParameters.getKey().getAlgorithm());
            }
            final Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                final List<Constraint> constraints = this.getConstraints((String)iterator.next());
                if (constraints == null) {
                    continue;
                }
                final Iterator<Constraint> iterator2 = constraints.iterator();
                while (iterator2.hasNext()) {
                    iterator2.next().permits(constraintsParameters);
                }
            }
        }
        
        private static class Holder
        {
            private static final Pattern DENY_AFTER_PATTERN;
            
            static {
                DENY_AFTER_PATTERN = Pattern.compile("denyAfter\\s+(\\d{4})-(\\d{2})-(\\d{2})");
            }
        }
    }
    
    private abstract static class Constraint
    {
        String algorithm;
        Constraint nextConstraint;
        
        private Constraint() {
            this.nextConstraint = null;
        }
        
        public boolean permits(final Key key) {
            return true;
        }
        
        public boolean permits(final AlgorithmParameters algorithmParameters) {
            return true;
        }
        
        public abstract void permits(final ConstraintsParameters p0) throws CertPathValidatorException;
        
        boolean next(final ConstraintsParameters constraintsParameters) throws CertPathValidatorException {
            if (this.nextConstraint != null) {
                this.nextConstraint.permits(constraintsParameters);
                return true;
            }
            return false;
        }
        
        boolean next(final Key key) {
            return this.nextConstraint != null && this.nextConstraint.permits(key);
        }
        
        String extendedMsg(final ConstraintsParameters constraintsParameters) {
            return (constraintsParameters.getCertificate() == null) ? "." : (" used with certificate: " + constraintsParameters.getCertificate().getSubjectX500Principal() + ((constraintsParameters.getVariant() != "generic") ? (".  Usage was " + constraintsParameters.getVariant()) : "."));
        }
        
        enum Operator
        {
            EQ, 
            NE, 
            LT, 
            LE, 
            GT, 
            GE;
            
            static Operator of(final String s) {
                switch (s) {
                    case "==": {
                        return Operator.EQ;
                    }
                    case "!=": {
                        return Operator.NE;
                    }
                    case "<": {
                        return Operator.LT;
                    }
                    case "<=": {
                        return Operator.LE;
                    }
                    case ">": {
                        return Operator.GT;
                    }
                    case ">=": {
                        return Operator.GE;
                    }
                    default: {
                        throw new IllegalArgumentException("Error in security property. " + s + " is not a legal Operator");
                    }
                }
            }
        }
    }
    
    private static class jdkCAConstraint extends Constraint
    {
        jdkCAConstraint(final String algorithm) {
            this.algorithm = algorithm;
        }
        
        @Override
        public void permits(final ConstraintsParameters constraintsParameters) throws CertPathValidatorException {
            if (DisabledAlgorithmConstraints.debug != null) {
                DisabledAlgorithmConstraints.debug.println("jdkCAConstraints.permits(): " + this.algorithm);
            }
            if (!constraintsParameters.isTrustedMatch()) {
                return;
            }
            if (this.next(constraintsParameters)) {
                return;
            }
            throw new CertPathValidatorException("Algorithm constraints check failed on certificate anchor limits. " + this.algorithm + this.extendedMsg(constraintsParameters), null, null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED);
        }
    }
    
    private static class DenyAfterConstraint extends Constraint
    {
        private Date denyAfterDate;
        private static final SimpleDateFormat dateFormat;
        
        DenyAfterConstraint(final String algorithm, final int n, final int n2, final int n3) {
            this.algorithm = algorithm;
            if (DisabledAlgorithmConstraints.debug != null) {
                DisabledAlgorithmConstraints.debug.println("DenyAfterConstraint read in as:  year " + n + ", month = " + n2 + ", day = " + n3);
            }
            final Calendar build = new Calendar.Builder().setTimeZone(TimeZone.getTimeZone("GMT")).setDate(n, n2 - 1, n3).build();
            if (n > build.getActualMaximum(1) || n < build.getActualMinimum(1)) {
                throw new IllegalArgumentException("Invalid year given in constraint: " + n);
            }
            if (n2 - 1 > build.getActualMaximum(2) || n2 - 1 < build.getActualMinimum(2)) {
                throw new IllegalArgumentException("Invalid month given in constraint: " + n2);
            }
            if (n3 > build.getActualMaximum(5) || n3 < build.getActualMinimum(5)) {
                throw new IllegalArgumentException("Invalid Day of Month given in constraint: " + n3);
            }
            this.denyAfterDate = build.getTime();
            if (DisabledAlgorithmConstraints.debug != null) {
                DisabledAlgorithmConstraints.debug.println("DenyAfterConstraint date set to: " + DenyAfterConstraint.dateFormat.format(this.denyAfterDate));
            }
        }
        
        @Override
        public void permits(final ConstraintsParameters constraintsParameters) throws CertPathValidatorException {
            Date date;
            String s;
            if (constraintsParameters.getJARTimestamp() != null) {
                date = constraintsParameters.getJARTimestamp().getTimestamp();
                s = "JAR Timestamp date: ";
            }
            else if (constraintsParameters.getPKIXParamDate() != null) {
                date = constraintsParameters.getPKIXParamDate();
                s = "PKIXParameter date: ";
            }
            else {
                date = new Date();
                s = "Current date: ";
            }
            if (this.denyAfterDate.after(date)) {
                return;
            }
            if (this.next(constraintsParameters)) {
                return;
            }
            throw new CertPathValidatorException("denyAfter constraint check failed: " + this.algorithm + " used with Constraint date: " + DenyAfterConstraint.dateFormat.format(this.denyAfterDate) + "; " + s + DenyAfterConstraint.dateFormat.format(date) + this.extendedMsg(constraintsParameters), null, null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED);
        }
        
        @Override
        public boolean permits(final Key key) {
            if (this.next(key)) {
                return true;
            }
            if (DisabledAlgorithmConstraints.debug != null) {
                DisabledAlgorithmConstraints.debug.println("DenyAfterConstraints.permits(): " + this.algorithm);
            }
            return this.denyAfterDate.after(new Date());
        }
        
        static {
            dateFormat = new SimpleDateFormat("EEE, MMM d HH:mm:ss z yyyy");
        }
    }
    
    private static class UsageConstraint extends Constraint
    {
        String[] usages;
        
        UsageConstraint(final String algorithm, final String[] usages) {
            this.algorithm = algorithm;
            this.usages = usages;
        }
        
        @Override
        public void permits(final ConstraintsParameters constraintsParameters) throws CertPathValidatorException {
            final String[] usages = this.usages;
            final int length = usages.length;
            int i = 0;
            while (i < length) {
                final String s = usages[i];
                String s2 = null;
                if (s.compareToIgnoreCase("TLSServer") == 0) {
                    s2 = "tls server";
                }
                else if (s.compareToIgnoreCase("TLSClient") == 0) {
                    s2 = "tls client";
                }
                else if (s.compareToIgnoreCase("SignedJAR") == 0) {
                    s2 = "plugin code signing";
                }
                if (DisabledAlgorithmConstraints.debug != null) {
                    DisabledAlgorithmConstraints.debug.println("Checking if usage constraint \"" + s2 + "\" matches \"" + constraintsParameters.getVariant() + "\"");
                    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    new Exception().printStackTrace(new PrintStream(byteArrayOutputStream));
                    DisabledAlgorithmConstraints.debug.println(byteArrayOutputStream.toString());
                }
                if (constraintsParameters.getVariant().compareTo(s2) == 0) {
                    if (this.next(constraintsParameters)) {
                        return;
                    }
                    throw new CertPathValidatorException("Usage constraint " + s + " check failed: " + this.algorithm + this.extendedMsg(constraintsParameters), null, null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED);
                }
                else {
                    ++i;
                }
            }
        }
    }
    
    private static class KeySizeConstraint extends Constraint
    {
        private int minSize;
        private int maxSize;
        private int prohibitedSize;
        private int size;
        
        public KeySizeConstraint(final String algorithm, final Operator operator, final int maxSize) {
            this.prohibitedSize = -1;
            this.algorithm = algorithm;
            switch (operator) {
                case EQ: {
                    this.minSize = 0;
                    this.maxSize = Integer.MAX_VALUE;
                    this.prohibitedSize = maxSize;
                    break;
                }
                case NE: {
                    this.minSize = maxSize;
                    this.maxSize = maxSize;
                    break;
                }
                case LT: {
                    this.minSize = maxSize;
                    this.maxSize = Integer.MAX_VALUE;
                    break;
                }
                case LE: {
                    this.minSize = maxSize + 1;
                    this.maxSize = Integer.MAX_VALUE;
                    break;
                }
                case GT: {
                    this.minSize = 0;
                    this.maxSize = maxSize;
                    break;
                }
                case GE: {
                    this.minSize = 0;
                    this.maxSize = ((maxSize > 1) ? (maxSize - 1) : 0);
                    break;
                }
                default: {
                    this.minSize = Integer.MAX_VALUE;
                    this.maxSize = -1;
                    break;
                }
            }
        }
        
        @Override
        public void permits(final ConstraintsParameters constraintsParameters) throws CertPathValidatorException {
            Key key = null;
            if (constraintsParameters.getKey() != null) {
                key = constraintsParameters.getKey();
            }
            else if (constraintsParameters.getCertificate() != null) {
                key = constraintsParameters.getCertificate().getPublicKey();
            }
            if (key == null || this.permitsImpl(key)) {
                return;
            }
            if (this.nextConstraint != null) {
                this.nextConstraint.permits(constraintsParameters);
                return;
            }
            throw new CertPathValidatorException("Algorithm constraints check failed on keysize limits. " + this.algorithm + " " + KeyUtil.getKeySize(key) + "bit key" + this.extendedMsg(constraintsParameters), null, null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED);
        }
        
        @Override
        public boolean permits(final Key key) {
            if (this.nextConstraint != null && this.nextConstraint.permits(key)) {
                return true;
            }
            if (DisabledAlgorithmConstraints.debug != null) {
                DisabledAlgorithmConstraints.debug.println("KeySizeConstraints.permits(): " + this.algorithm);
            }
            return this.permitsImpl(key);
        }
        
        @Override
        public boolean permits(final AlgorithmParameters algorithmParameters) {
            final String algorithm = algorithmParameters.getAlgorithm();
            if (!this.algorithm.equalsIgnoreCase(algorithmParameters.getAlgorithm()) && !AlgorithmDecomposer.getAliases(this.algorithm).contains(algorithm)) {
                return true;
            }
            final int keySize = KeyUtil.getKeySize(algorithmParameters);
            return keySize != 0 && (keySize <= 0 || (keySize >= this.minSize && keySize <= this.maxSize && this.prohibitedSize != keySize));
        }
        
        private boolean permitsImpl(final Key key) {
            if (this.algorithm.compareToIgnoreCase(key.getAlgorithm()) != 0) {
                return true;
            }
            this.size = KeyUtil.getKeySize(key);
            return this.size != 0 && (this.size <= 0 || (this.size >= this.minSize && this.size <= this.maxSize && this.prohibitedSize != this.size));
        }
    }
    
    private static class DisabledConstraint extends Constraint
    {
        DisabledConstraint(final String algorithm) {
            this.algorithm = algorithm;
        }
        
        @Override
        public void permits(final ConstraintsParameters constraintsParameters) throws CertPathValidatorException {
            throw new CertPathValidatorException("Algorithm constraints check failed on disabled algorithm: " + this.algorithm + this.extendedMsg(constraintsParameters), null, null, -1, CertPathValidatorException.BasicReason.ALGORITHM_CONSTRAINED);
        }
        
        @Override
        public boolean permits(final Key key) {
            return false;
        }
    }
}
