package sun.security.x509;

import sun.security.pkcs.PKCS9Attribute;
import java.util.HashMap;
import java.util.Collections;
import java.io.IOException;
import java.util.Locale;
import sun.security.util.ObjectIdentifier;
import java.util.Map;

class AVAKeyword
{
    private static final Map<ObjectIdentifier, AVAKeyword> oidMap;
    private static final Map<String, AVAKeyword> keywordMap;
    private String keyword;
    private ObjectIdentifier oid;
    private boolean rfc1779Compliant;
    private boolean rfc2253Compliant;
    
    private AVAKeyword(final String keyword, final ObjectIdentifier oid, final boolean rfc1779Compliant, final boolean rfc2253Compliant) {
        this.keyword = keyword;
        this.oid = oid;
        this.rfc1779Compliant = rfc1779Compliant;
        this.rfc2253Compliant = rfc2253Compliant;
        AVAKeyword.oidMap.put(oid, this);
        AVAKeyword.keywordMap.put(keyword, this);
    }
    
    private boolean isCompliant(final int n) {
        switch (n) {
            case 2: {
                return this.rfc1779Compliant;
            }
            case 3: {
                return this.rfc2253Compliant;
            }
            case 1: {
                return true;
            }
            default: {
                throw new IllegalArgumentException("Invalid standard " + n);
            }
        }
    }
    
    static ObjectIdentifier getOID(String s, final int n, final Map<String, String> map) throws IOException {
        s = s.toUpperCase(Locale.ENGLISH);
        if (n == 3) {
            if (s.startsWith(" ") || s.endsWith(" ")) {
                throw new IOException("Invalid leading or trailing space in keyword \"" + s + "\"");
            }
        }
        else {
            s = s.trim();
        }
        final String s2 = map.get(s);
        if (s2 != null) {
            return new ObjectIdentifier(s2);
        }
        final AVAKeyword avaKeyword = AVAKeyword.keywordMap.get(s);
        if (avaKeyword != null && avaKeyword.isCompliant(n)) {
            return avaKeyword.oid;
        }
        if (n == 1 && s.startsWith("OID.")) {
            s = s.substring(4);
        }
        boolean b = false;
        if (s.length() != 0) {
            final char char1 = s.charAt(0);
            if (char1 >= '0' && char1 <= '9') {
                b = true;
            }
        }
        if (!b) {
            throw new IOException("Invalid keyword \"" + s + "\"");
        }
        return new ObjectIdentifier(s);
    }
    
    static String getKeyword(final ObjectIdentifier objectIdentifier, final int n) {
        return getKeyword(objectIdentifier, n, Collections.emptyMap());
    }
    
    static String getKeyword(final ObjectIdentifier objectIdentifier, final int n, final Map<String, String> map) {
        final String string = objectIdentifier.toString();
        final String s = map.get(string);
        if (s == null) {
            final AVAKeyword avaKeyword = AVAKeyword.oidMap.get(objectIdentifier);
            if (avaKeyword != null && avaKeyword.isCompliant(n)) {
                return avaKeyword.keyword;
            }
            if (n == 3) {
                return string;
            }
            return "OID." + string;
        }
        else {
            if (s.length() == 0) {
                throw new IllegalArgumentException("keyword cannot be empty");
            }
            final String trim = s.trim();
            final char char1 = trim.charAt(0);
            if (char1 < 'A' || char1 > 'z' || (char1 > 'Z' && char1 < 'a')) {
                throw new IllegalArgumentException("keyword does not start with letter");
            }
            for (int i = 1; i < trim.length(); ++i) {
                final char char2 = trim.charAt(i);
                if ((char2 < 'A' || char2 > 'z' || (char2 > 'Z' && char2 < 'a')) && (char2 < '0' || char2 > '9') && char2 != '_') {
                    throw new IllegalArgumentException("keyword character is not a letter, digit, or underscore");
                }
            }
            return trim;
        }
    }
    
    static boolean hasKeyword(final ObjectIdentifier objectIdentifier, final int n) {
        final AVAKeyword avaKeyword = AVAKeyword.oidMap.get(objectIdentifier);
        return avaKeyword != null && avaKeyword.isCompliant(n);
    }
    
    static {
        oidMap = new HashMap<ObjectIdentifier, AVAKeyword>();
        keywordMap = new HashMap<String, AVAKeyword>();
        new AVAKeyword("CN", X500Name.commonName_oid, true, true);
        new AVAKeyword("C", X500Name.countryName_oid, true, true);
        new AVAKeyword("L", X500Name.localityName_oid, true, true);
        new AVAKeyword("S", X500Name.stateName_oid, false, false);
        new AVAKeyword("ST", X500Name.stateName_oid, true, true);
        new AVAKeyword("O", X500Name.orgName_oid, true, true);
        new AVAKeyword("OU", X500Name.orgUnitName_oid, true, true);
        new AVAKeyword("T", X500Name.title_oid, false, false);
        new AVAKeyword("IP", X500Name.ipAddress_oid, false, false);
        new AVAKeyword("STREET", X500Name.streetAddress_oid, true, true);
        new AVAKeyword("DC", X500Name.DOMAIN_COMPONENT_OID, false, true);
        new AVAKeyword("DNQUALIFIER", X500Name.DNQUALIFIER_OID, false, false);
        new AVAKeyword("DNQ", X500Name.DNQUALIFIER_OID, false, false);
        new AVAKeyword("SURNAME", X500Name.SURNAME_OID, false, false);
        new AVAKeyword("GIVENNAME", X500Name.GIVENNAME_OID, false, false);
        new AVAKeyword("INITIALS", X500Name.INITIALS_OID, false, false);
        new AVAKeyword("GENERATION", X500Name.GENERATIONQUALIFIER_OID, false, false);
        new AVAKeyword("EMAIL", PKCS9Attribute.EMAIL_ADDRESS_OID, false, false);
        new AVAKeyword("EMAILADDRESS", PKCS9Attribute.EMAIL_ADDRESS_OID, false, false);
        new AVAKeyword("UID", X500Name.userid_oid, false, true);
        new AVAKeyword("SERIALNUMBER", X500Name.SERIALNUMBER_OID, false, false);
    }
}
