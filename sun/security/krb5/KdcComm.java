package sun.security.krb5;

import java.util.HashSet;
import java.util.Collection;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.List;
import java.util.Set;
import java.net.SocketTimeoutException;
import sun.security.krb5.internal.NetClient;
import sun.security.krb5.internal.Krb5;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import sun.security.krb5.internal.KRBError;
import java.util.Iterator;
import java.io.IOException;
import java.util.Locale;
import java.security.AccessController;
import java.security.Security;
import java.security.PrivilegedAction;

public final class KdcComm
{
    private static int defaultKdcRetryLimit;
    private static int defaultKdcTimeout;
    private static int defaultUdpPrefLimit;
    private static final boolean DEBUG;
    private static final String BAD_POLICY_KEY = "krb5.kdc.bad.policy";
    private static int tryLessMaxRetries;
    private static int tryLessTimeout;
    private static BpType badPolicy;
    private String realm;
    
    public static void initStatic() {
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                return Security.getProperty("krb5.kdc.bad.policy");
            }
        });
        if (s != null) {
            final String lowerCase = s.toLowerCase(Locale.ENGLISH);
            final String[] split = lowerCase.split(":");
            if ("tryless".equals(split[0])) {
                if (split.length > 1) {
                    final String[] split2 = split[1].split(",");
                    try {
                        final int int1 = Integer.parseInt(split2[0]);
                        if (split2.length > 1) {
                            KdcComm.tryLessTimeout = Integer.parseInt(split2[1]);
                        }
                        KdcComm.tryLessMaxRetries = int1;
                    }
                    catch (final NumberFormatException ex) {
                        if (KdcComm.DEBUG) {
                            System.out.println("Invalid krb5.kdc.bad.policy parameter for tryLess: " + lowerCase + ", use default");
                        }
                    }
                }
                KdcComm.badPolicy = BpType.TRY_LESS;
            }
            else if ("trylast".equals(split[0])) {
                KdcComm.badPolicy = BpType.TRY_LAST;
            }
            else {
                KdcComm.badPolicy = BpType.NONE;
            }
        }
        else {
            KdcComm.badPolicy = BpType.NONE;
        }
        int timeString = -1;
        int positiveIntString = -1;
        int positiveIntString2 = -1;
        try {
            final Config instance = Config.getInstance();
            timeString = parseTimeString(instance.get("libdefaults", "kdc_timeout"));
            positiveIntString = parsePositiveIntString(instance.get("libdefaults", "max_retries"));
            positiveIntString2 = parsePositiveIntString(instance.get("libdefaults", "udp_preference_limit"));
        }
        catch (final Exception ex2) {
            if (KdcComm.DEBUG) {
                System.out.println("Exception in getting KDC communication settings, using default value " + ex2.getMessage());
            }
        }
        KdcComm.defaultKdcTimeout = ((timeString > 0) ? timeString : 30000);
        KdcComm.defaultKdcRetryLimit = ((positiveIntString > 0) ? positiveIntString : 3);
        if (positiveIntString2 < 0) {
            KdcComm.defaultUdpPrefLimit = 1465;
        }
        else if (positiveIntString2 > 32700) {
            KdcComm.defaultUdpPrefLimit = 32700;
        }
        else {
            KdcComm.defaultUdpPrefLimit = positiveIntString2;
        }
        reset();
    }
    
    public KdcComm(String defaultRealm) throws KrbException {
        if (defaultRealm == null) {
            defaultRealm = Config.getInstance().getDefaultRealm();
            if (defaultRealm == null) {
                throw new KrbException(60, "Cannot find default realm");
            }
        }
        this.realm = defaultRealm;
    }
    
    public byte[] send(final byte[] array) throws IOException, KrbException {
        final int realmSpecificValue = this.getRealmSpecificValue(this.realm, "udp_preference_limit", KdcComm.defaultUdpPrefLimit);
        return this.send(array, realmSpecificValue > 0 && array != null && array.length > realmSpecificValue);
    }
    
    private byte[] send(final byte[] array, final boolean b) throws IOException, KrbException {
        if (array == null) {
            return null;
        }
        final Config instance = Config.getInstance();
        if (this.realm == null) {
            this.realm = instance.getDefaultRealm();
            if (this.realm == null) {
                throw new KrbException(60, "Cannot find default realm");
            }
        }
        final String kdcList = instance.getKDCList(this.realm);
        if (kdcList == null) {
            throw new KrbException("Cannot get kdc for realm " + this.realm);
        }
        final Iterator iterator = list(kdcList).iterator();
        if (!iterator.hasNext()) {
            throw new KrbException("Cannot get kdc for realm " + this.realm);
        }
        byte[] array2 = null;
        try {
            array2 = this.sendIfPossible(array, (String)iterator.next(), b);
        }
        catch (final Exception ex) {
            boolean b2 = false;
            while (iterator.hasNext()) {
                try {
                    array2 = this.sendIfPossible(array, (String)iterator.next(), b);
                    b2 = true;
                }
                catch (final Exception ex2) {
                    continue;
                }
                break;
            }
            if (!b2) {
                throw ex;
            }
        }
        if (array2 == null) {
            throw new IOException("Cannot get a KDC reply");
        }
        return array2;
    }
    
    private byte[] sendIfPossible(final byte[] array, final String s, final boolean b) throws IOException, KrbException {
        try {
            byte[] array2 = this.send(array, s, b);
            KRBError krbError = null;
            try {
                krbError = new KRBError(array2);
            }
            catch (final Exception ex) {}
            if (krbError != null && krbError.getErrorCode() == 52) {
                array2 = this.send(array, s, true);
            }
            removeBad(s);
            return array2;
        }
        catch (final Exception ex2) {
            if (KdcComm.DEBUG) {
                System.out.println(">>> KrbKdcReq send: error trying " + s);
                ex2.printStackTrace(System.out);
            }
            addBad(s);
            throw ex2;
        }
    }
    
    private byte[] send(final byte[] array, final String s, final boolean b) throws IOException, KrbException {
        if (array == null) {
            return null;
        }
        int n = 88;
        int n2 = this.getRealmSpecificValue(this.realm, "max_retries", KdcComm.defaultKdcRetryLimit);
        int n3 = this.getRealmSpecificValue(this.realm, "kdc_timeout", KdcComm.defaultKdcTimeout);
        if (KdcComm.badPolicy == BpType.TRY_LESS && isBad(s)) {
            if (n2 > KdcComm.tryLessMaxRetries) {
                n2 = KdcComm.tryLessMaxRetries;
            }
            if (n3 > KdcComm.tryLessTimeout) {
                n3 = KdcComm.tryLessTimeout;
            }
        }
        String s2 = null;
        String s3;
        if (s.charAt(0) == '[') {
            final int index = s.indexOf(93, 1);
            if (index == -1) {
                throw new IOException("Illegal KDC: " + s);
            }
            s3 = s.substring(1, index);
            if (index != s.length() - 1) {
                if (s.charAt(index + 1) != ':') {
                    throw new IOException("Illegal KDC: " + s);
                }
                s2 = s.substring(index + 2);
            }
        }
        else {
            final int index2 = s.indexOf(58);
            if (index2 == -1) {
                s3 = s;
            }
            else if (s.indexOf(58, index2 + 1) > 0) {
                s3 = s;
            }
            else {
                s3 = s.substring(0, index2);
                s2 = s.substring(index2 + 1);
            }
        }
        if (s2 != null) {
            final int positiveIntString = parsePositiveIntString(s2);
            if (positiveIntString > 0) {
                n = positiveIntString;
            }
        }
        if (KdcComm.DEBUG) {
            System.out.println(">>> KrbKdcReq send: kdc=" + s3 + (b ? " TCP:" : " UDP:") + n + ", timeout=" + n3 + ", number of retries =" + n2 + ", #bytes=" + array.length);
        }
        final KdcCommunication kdcCommunication = new KdcCommunication(s3, n, b, n3, n2, array);
        try {
            final byte[] array2 = AccessController.doPrivileged((PrivilegedExceptionAction<byte[]>)kdcCommunication);
            if (KdcComm.DEBUG) {
                System.out.println(">>> KrbKdcReq send: #bytes read=" + ((array2 != null) ? array2.length : 0));
            }
            return array2;
        }
        catch (final PrivilegedActionException ex) {
            final Exception exception = ex.getException();
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw (KrbException)exception;
        }
    }
    
    private static int parseTimeString(final String s) {
        if (s == null) {
            return -1;
        }
        if (s.endsWith("s")) {
            final int positiveIntString = parsePositiveIntString(s.substring(0, s.length() - 1));
            return (positiveIntString < 0) ? -1 : (positiveIntString * 1000);
        }
        return parsePositiveIntString(s);
    }
    
    private int getRealmSpecificValue(final String s, final String s2, final int n) {
        int n2 = n;
        if (s == null) {
            return n2;
        }
        int n3 = -1;
        try {
            final String value = Config.getInstance().get("realms", s, s2);
            if (s2.equals("kdc_timeout")) {
                n3 = parseTimeString(value);
            }
            else {
                n3 = parsePositiveIntString(value);
            }
        }
        catch (final Exception ex) {}
        if (n3 > 0) {
            n2 = n3;
        }
        return n2;
    }
    
    private static int parsePositiveIntString(final String s) {
        if (s == null) {
            return -1;
        }
        int int1;
        try {
            int1 = Integer.parseInt(s);
        }
        catch (final Exception ex) {
            return -1;
        }
        if (int1 >= 0) {
            return int1;
        }
        return -1;
    }
    
    static {
        DEBUG = Krb5.DEBUG;
        KdcComm.tryLessMaxRetries = 1;
        KdcComm.tryLessTimeout = 5000;
        initStatic();
    }
    
    private enum BpType
    {
        NONE, 
        TRY_LAST, 
        TRY_LESS;
    }
    
    private static class KdcCommunication implements PrivilegedExceptionAction<byte[]>
    {
        private String kdc;
        private int port;
        private boolean useTCP;
        private int timeout;
        private int retries;
        private byte[] obuf;
        
        public KdcCommunication(final String kdc, final int port, final boolean useTCP, final int timeout, final int retries, final byte[] obuf) {
            this.kdc = kdc;
            this.port = port;
            this.useTCP = useTCP;
            this.timeout = timeout;
            this.retries = retries;
            this.obuf = obuf;
        }
        
        @Override
        public byte[] run() throws IOException, KrbException {
            byte[] receive = null;
            int i = 1;
            while (i <= this.retries) {
                final String s = this.useTCP ? "TCP" : "UDP";
                if (KdcComm.DEBUG) {
                    System.out.println(">>> KDCCommunication: kdc=" + this.kdc + " " + s + ":" + this.port + ", timeout=" + this.timeout + ",Attempt =" + i + ", #bytes=" + this.obuf.length);
                }
                try (final NetClient instance = NetClient.getInstance(s, this.kdc, this.port, this.timeout)) {
                    instance.send(this.obuf);
                    receive = instance.receive();
                }
                catch (final SocketTimeoutException ex) {
                    if (KdcComm.DEBUG) {
                        System.out.println("SocketTimeOutException with attempt: " + i);
                    }
                    if (i == this.retries) {
                        throw ex;
                    }
                    ++i;
                    continue;
                }
                break;
            }
            return receive;
        }
    }
    
    static class KdcAccessibility
    {
        private static Set<String> bads;
        
        private static synchronized void addBad(final String s) {
            if (KdcComm.DEBUG) {
                System.out.println(">>> KdcAccessibility: add " + s);
            }
            KdcAccessibility.bads.add(s);
        }
        
        private static synchronized void removeBad(final String s) {
            if (KdcComm.DEBUG) {
                System.out.println(">>> KdcAccessibility: remove " + s);
            }
            KdcAccessibility.bads.remove(s);
        }
        
        private static synchronized boolean isBad(final String s) {
            return KdcAccessibility.bads.contains(s);
        }
        
        private static synchronized void reset() {
            if (KdcComm.DEBUG) {
                System.out.println(">>> KdcAccessibility: reset");
            }
            KdcAccessibility.bads.clear();
        }
        
        private static synchronized List<String> list(final String s) {
            final StringTokenizer stringTokenizer = new StringTokenizer(s);
            final ArrayList list = new ArrayList();
            if (KdcComm.badPolicy == BpType.TRY_LAST) {
                final ArrayList list2 = new ArrayList();
                while (stringTokenizer.hasMoreTokens()) {
                    final String nextToken = stringTokenizer.nextToken();
                    if (KdcAccessibility.bads.contains(nextToken)) {
                        list2.add(nextToken);
                    }
                    else {
                        list.add(nextToken);
                    }
                }
                list.addAll(list2);
            }
            else {
                while (stringTokenizer.hasMoreTokens()) {
                    list.add(stringTokenizer.nextToken());
                }
            }
            return list;
        }
        
        static {
            KdcAccessibility.bads = new HashSet<String>();
        }
    }
}
