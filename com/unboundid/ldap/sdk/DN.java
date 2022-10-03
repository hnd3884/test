package com.unboundid.ldap.sdk;

import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.StaticUtils;
import java.util.ArrayList;
import java.util.List;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;
import java.util.Comparator;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DN implements Comparable<DN>, Comparator<DN>, Serializable
{
    private static final RDN[] NO_RDNS;
    public static final DN NULL_DN;
    private static final long serialVersionUID = -5272968942085729346L;
    private final RDN[] rdns;
    private final Schema schema;
    private final String dnString;
    private volatile String normalizedString;
    
    public DN(final RDN... rdns) {
        Validator.ensureNotNull(rdns);
        this.rdns = rdns;
        if (rdns.length == 0) {
            this.dnString = "";
            this.normalizedString = "";
            this.schema = null;
        }
        else {
            Schema s = null;
            final StringBuilder buffer = new StringBuilder();
            for (final RDN rdn : rdns) {
                if (buffer.length() > 0) {
                    buffer.append(',');
                }
                rdn.toString(buffer, false);
                if (s == null) {
                    s = rdn.getSchema();
                }
            }
            this.dnString = buffer.toString();
            this.schema = s;
        }
    }
    
    public DN(final List<RDN> rdns) {
        Validator.ensureNotNull(rdns);
        if (rdns.isEmpty()) {
            this.rdns = DN.NO_RDNS;
            this.dnString = "";
            this.normalizedString = "";
            this.schema = null;
        }
        else {
            this.rdns = rdns.toArray(new RDN[rdns.size()]);
            Schema s = null;
            final StringBuilder buffer = new StringBuilder();
            for (final RDN rdn : this.rdns) {
                if (buffer.length() > 0) {
                    buffer.append(',');
                }
                rdn.toString(buffer, false);
                if (s == null) {
                    s = rdn.getSchema();
                }
            }
            this.dnString = buffer.toString();
            this.schema = s;
        }
    }
    
    public DN(final RDN rdn, final DN parentDN) {
        Validator.ensureNotNull(rdn, parentDN);
        (this.rdns = new RDN[parentDN.rdns.length + 1])[0] = rdn;
        System.arraycopy(parentDN.rdns, 0, this.rdns, 1, parentDN.rdns.length);
        Schema s = null;
        final StringBuilder buffer = new StringBuilder();
        for (final RDN r : this.rdns) {
            if (buffer.length() > 0) {
                buffer.append(',');
            }
            r.toString(buffer, false);
            if (s == null) {
                s = r.getSchema();
            }
        }
        this.dnString = buffer.toString();
        this.schema = s;
    }
    
    public DN(final String dnString) throws LDAPException {
        this(dnString, null, false);
    }
    
    public DN(final String dnString, final Schema schema) throws LDAPException {
        this(dnString, schema, false);
    }
    
    public DN(final String dnString, final Schema schema, final boolean strictNameChecking) throws LDAPException {
        Validator.ensureNotNull(dnString);
        this.dnString = dnString;
        this.schema = schema;
        final ArrayList<RDN> rdnList = new ArrayList<RDN>(5);
        final int length = dnString.length();
        if (length == 0) {
            this.rdns = DN.NO_RDNS;
            this.normalizedString = "";
            return;
        }
        int pos = 0;
        boolean expectMore = false;
    Label_0059:
        while (true) {
            while (pos < length) {
                while (pos < length && dnString.charAt(pos) == ' ') {
                    ++pos;
                }
                if (pos >= length) {
                    if (rdnList.isEmpty()) {
                        break;
                    }
                    throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_DN_ENDS_WITH_COMMA.get(dnString));
                }
                else {
                    int attrStartPos = pos;
                    final int rdnStartPos = pos;
                    while (pos < length) {
                        final char c = dnString.charAt(pos);
                        if (c == ' ') {
                            break;
                        }
                        if (c == '=') {
                            break;
                        }
                        if (c == ',' || c == ';') {
                            throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_DN_UNEXPECTED_COMMA.get(dnString, pos));
                        }
                        ++pos;
                    }
                    String attrName = dnString.substring(attrStartPos, pos);
                    if (attrName.isEmpty()) {
                        throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_DN_NO_ATTR_IN_RDN.get(dnString));
                    }
                    if (strictNameChecking && !Attribute.nameIsValid(attrName) && !StaticUtils.isNumericOID(attrName)) {
                        throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_DN_INVALID_ATTR_NAME.get(dnString, attrName));
                    }
                    while (pos < length && dnString.charAt(pos) == ' ') {
                        ++pos;
                    }
                    if (pos >= length || dnString.charAt(pos) != '=') {
                        throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_DN_NO_EQUAL_SIGN.get(dnString, attrName));
                    }
                    ++pos;
                    while (pos < length && dnString.charAt(pos) == ' ') {
                        ++pos;
                    }
                    ASN1OctetString value;
                    int rdnEndPos;
                    if (pos >= length) {
                        value = new ASN1OctetString();
                        rdnEndPos = pos;
                    }
                    else if (dnString.charAt(pos) == '#') {
                        final byte[] valueArray = RDN.readHexString(dnString, ++pos);
                        try {
                            value = ASN1OctetString.decodeAsOctetString(valueArray);
                        }
                        catch (final Exception e) {
                            Debug.debugException(e);
                            throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_RDN_HEX_STRING_NOT_BER_ENCODED.get(dnString, attrName), e);
                        }
                        pos = (rdnEndPos = pos + valueArray.length * 2);
                    }
                    else {
                        final StringBuilder buffer = new StringBuilder();
                        pos = RDN.readValueString(dnString, pos, buffer);
                        value = new ASN1OctetString(buffer.toString());
                        rdnEndPos = pos;
                    }
                    while (pos < length && dnString.charAt(pos) == ' ') {
                        ++pos;
                    }
                    if (pos >= length) {
                        rdnList.add(new RDN(attrName, value, schema, getTrimmedRDN(dnString, rdnStartPos, rdnEndPos)));
                        expectMore = false;
                        break;
                    }
                    switch (dnString.charAt(pos)) {
                        case '+': {
                            ++pos;
                            if (pos >= length) {
                                throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_DN_ENDS_WITH_PLUS.get(dnString));
                            }
                            final ArrayList<String> nameList = new ArrayList<String>(5);
                            final ArrayList<ASN1OctetString> valueList = new ArrayList<ASN1OctetString>(5);
                            nameList.add(attrName);
                            valueList.add(value);
                            while (pos < length) {
                                while (pos < length && dnString.charAt(pos) == ' ') {
                                    ++pos;
                                }
                                if (pos >= length) {
                                    throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_DN_ENDS_WITH_PLUS.get(dnString));
                                }
                                attrStartPos = pos;
                                while (pos < length) {
                                    final char c2 = dnString.charAt(pos);
                                    if (c2 == ' ') {
                                        break;
                                    }
                                    if (c2 == '=') {
                                        break;
                                    }
                                    if (c2 == ',' || c2 == ';') {
                                        throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_DN_UNEXPECTED_COMMA.get(dnString, pos));
                                    }
                                    ++pos;
                                }
                                attrName = dnString.substring(attrStartPos, pos);
                                if (attrName.isEmpty()) {
                                    throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_DN_NO_ATTR_IN_RDN.get(dnString));
                                }
                                if (strictNameChecking && !Attribute.nameIsValid(attrName) && !StaticUtils.isNumericOID(attrName)) {
                                    throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_DN_INVALID_ATTR_NAME.get(dnString, attrName));
                                }
                                while (pos < length && dnString.charAt(pos) == ' ') {
                                    ++pos;
                                }
                                if (pos >= length || dnString.charAt(pos) != '=') {
                                    throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_DN_NO_EQUAL_SIGN.get(dnString, attrName));
                                }
                                ++pos;
                                while (pos < length && dnString.charAt(pos) == ' ') {
                                    ++pos;
                                }
                                if (pos >= length) {
                                    value = new ASN1OctetString();
                                    rdnEndPos = pos;
                                }
                                else if (dnString.charAt(pos) == '#') {
                                    final byte[] valueArray2 = RDN.readHexString(dnString, ++pos);
                                    try {
                                        value = ASN1OctetString.decodeAsOctetString(valueArray2);
                                    }
                                    catch (final Exception e2) {
                                        Debug.debugException(e2);
                                        throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_RDN_HEX_STRING_NOT_BER_ENCODED.get(dnString, attrName), e2);
                                    }
                                    pos = (rdnEndPos = pos + valueArray2.length * 2);
                                }
                                else {
                                    final StringBuilder buffer2 = new StringBuilder();
                                    pos = RDN.readValueString(dnString, pos, buffer2);
                                    value = new ASN1OctetString(buffer2.toString());
                                    rdnEndPos = pos;
                                }
                                while (pos < length && dnString.charAt(pos) == ' ') {
                                    ++pos;
                                }
                                nameList.add(attrName);
                                valueList.add(value);
                                if (pos >= length) {
                                    final String[] names = nameList.toArray(new String[nameList.size()]);
                                    final ASN1OctetString[] values = valueList.toArray(new ASN1OctetString[valueList.size()]);
                                    rdnList.add(new RDN(names, values, schema, getTrimmedRDN(dnString, rdnStartPos, rdnEndPos)));
                                    expectMore = false;
                                    break Label_0059;
                                }
                                switch (dnString.charAt(pos)) {
                                    case '+': {
                                        if (++pos >= length) {
                                            throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_DN_ENDS_WITH_PLUS.get(dnString));
                                        }
                                        continue;
                                    }
                                    case ',':
                                    case ';': {
                                        final String[] names = nameList.toArray(new String[nameList.size()]);
                                        final ASN1OctetString[] values = valueList.toArray(new ASN1OctetString[valueList.size()]);
                                        rdnList.add(new RDN(names, values, schema, getTrimmedRDN(dnString, rdnStartPos, rdnEndPos)));
                                        ++pos;
                                        expectMore = true;
                                        continue Label_0059;
                                    }
                                    default: {
                                        throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_DN_UNEXPECTED_CHAR.get(dnString, dnString.charAt(pos), pos));
                                    }
                                }
                            }
                            continue;
                        }
                        case ',':
                        case ';': {
                            rdnList.add(new RDN(attrName, value, schema, getTrimmedRDN(dnString, rdnStartPos, rdnEndPos)));
                            ++pos;
                            expectMore = true;
                            continue;
                        }
                        default: {
                            throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_DN_UNEXPECTED_CHAR.get(dnString, dnString.charAt(pos), pos));
                        }
                    }
                }
            }
            break;
        }
        if (expectMore) {
            throw new LDAPException(ResultCode.INVALID_DN_SYNTAX, LDAPMessages.ERR_DN_ENDS_WITH_COMMA.get(dnString));
        }
        rdnList.toArray(this.rdns = new RDN[rdnList.size()]);
    }
    
    private static String getTrimmedRDN(final String dnString, final int start, final int end) {
        final String rdnString = dnString.substring(start, end);
        if (!rdnString.endsWith(" ")) {
            return rdnString;
        }
        final StringBuilder buffer = new StringBuilder(rdnString);
        while (buffer.charAt(buffer.length() - 1) == ' ' && buffer.charAt(buffer.length() - 2) != '\\') {
            buffer.setLength(buffer.length() - 1);
        }
        return buffer.toString();
    }
    
    public static boolean isValidDN(final String s) {
        return isValidDN(s, false);
    }
    
    public static boolean isValidDN(final String s, final boolean strictNameChecking) {
        try {
            new DN(s, null, strictNameChecking);
            return true;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            return false;
        }
    }
    
    public RDN getRDN() {
        if (this.rdns.length == 0) {
            return null;
        }
        return this.rdns[0];
    }
    
    public String getRDNString() {
        if (this.rdns.length == 0) {
            return null;
        }
        return this.rdns[0].toString();
    }
    
    public static String getRDNString(final String s) throws LDAPException {
        return new DN(s).getRDNString();
    }
    
    public RDN[] getRDNs() {
        return this.rdns;
    }
    
    public static RDN[] getRDNs(final String s) throws LDAPException {
        return new DN(s).getRDNs();
    }
    
    public String[] getRDNStrings() {
        final String[] rdnStrings = new String[this.rdns.length];
        for (int i = 0; i < this.rdns.length; ++i) {
            rdnStrings[i] = this.rdns[i].toString();
        }
        return rdnStrings;
    }
    
    public static String[] getRDNStrings(final String s) throws LDAPException {
        return new DN(s).getRDNStrings();
    }
    
    public boolean isNullDN() {
        return this.rdns.length == 0;
    }
    
    public DN getParent() {
        switch (this.rdns.length) {
            case 0:
            case 1: {
                return null;
            }
            case 2: {
                return new DN(new RDN[] { this.rdns[1] });
            }
            case 3: {
                return new DN(new RDN[] { this.rdns[1], this.rdns[2] });
            }
            case 4: {
                return new DN(new RDN[] { this.rdns[1], this.rdns[2], this.rdns[3] });
            }
            case 5: {
                return new DN(new RDN[] { this.rdns[1], this.rdns[2], this.rdns[3], this.rdns[4] });
            }
            default: {
                final RDN[] parentRDNs = new RDN[this.rdns.length - 1];
                System.arraycopy(this.rdns, 1, parentRDNs, 0, parentRDNs.length);
                return new DN(parentRDNs);
            }
        }
    }
    
    public static DN getParent(final String s) throws LDAPException {
        return new DN(s).getParent();
    }
    
    public String getParentString() {
        final DN parentDN = this.getParent();
        if (parentDN == null) {
            return null;
        }
        return parentDN.toString();
    }
    
    public static String getParentString(final String s) throws LDAPException {
        return new DN(s).getParentString();
    }
    
    public boolean isAncestorOf(final DN dn, final boolean allowEquals) {
        int thisPos = this.rdns.length - 1;
        int thatPos = dn.rdns.length - 1;
        if (thisPos < 0) {
            return allowEquals || thatPos >= 0;
        }
        if (thisPos > thatPos || (thisPos == thatPos && !allowEquals)) {
            return false;
        }
        while (thisPos >= 0) {
            if (!this.rdns[thisPos--].equals(dn.rdns[thatPos--])) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isAncestorOf(final String s, final boolean allowEquals) throws LDAPException {
        return this.isAncestorOf(new DN(s), allowEquals);
    }
    
    public static boolean isAncestorOf(final String s1, final String s2, final boolean allowEquals) throws LDAPException {
        return new DN(s1).isAncestorOf(new DN(s2), allowEquals);
    }
    
    public boolean isDescendantOf(final DN dn, final boolean allowEquals) {
        int thisPos = this.rdns.length - 1;
        int thatPos = dn.rdns.length - 1;
        if (thatPos < 0) {
            return allowEquals || thisPos >= 0;
        }
        if (thisPos < thatPos || (thisPos == thatPos && !allowEquals)) {
            return false;
        }
        while (thatPos >= 0) {
            if (!this.rdns[thisPos--].equals(dn.rdns[thatPos--])) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isDescendantOf(final String s, final boolean allowEquals) throws LDAPException {
        return this.isDescendantOf(new DN(s), allowEquals);
    }
    
    public static boolean isDescendantOf(final String s1, final String s2, final boolean allowEquals) throws LDAPException {
        return new DN(s1).isDescendantOf(new DN(s2), allowEquals);
    }
    
    public boolean matchesBaseAndScope(final String baseDN, final SearchScope scope) throws LDAPException {
        return this.matchesBaseAndScope(new DN(baseDN), scope);
    }
    
    public boolean matchesBaseAndScope(final DN baseDN, final SearchScope scope) throws LDAPException {
        Validator.ensureNotNull(baseDN, scope);
        switch (scope.intValue()) {
            case 0: {
                return this.equals(baseDN);
            }
            case 1: {
                return baseDN.equals(this.getParent());
            }
            case 2: {
                return this.isDescendantOf(baseDN, true);
            }
            case 3: {
                return this.isDescendantOf(baseDN, false);
            }
            default: {
                throw new LDAPException(ResultCode.PARAM_ERROR, LDAPMessages.ERR_DN_MATCHES_UNSUPPORTED_SCOPE.get(this.dnString, String.valueOf(scope)));
            }
        }
    }
    
    @Override
    public int hashCode() {
        return this.toNormalizedString().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!(o instanceof DN)) {
            return false;
        }
        final DN dn = (DN)o;
        return this.toNormalizedString().equals(dn.toNormalizedString());
    }
    
    public boolean equals(final String s) throws LDAPException {
        return s != null && this.equals(new DN(s));
    }
    
    public static boolean equals(final String s1, final String s2) throws LDAPException {
        return new DN(s1).equals(new DN(s2));
    }
    
    public static boolean equals(final String s1, final String s2, final Schema schema) throws LDAPException {
        return new DN(s1, schema).equals(new DN(s2, schema));
    }
    
    @Override
    public String toString() {
        return this.dnString;
    }
    
    public String toMinimallyEncodedString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer, true);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        this.toString(buffer, false);
    }
    
    public void toString(final StringBuilder buffer, final boolean minimizeEncoding) {
        for (int i = 0; i < this.rdns.length; ++i) {
            if (i > 0) {
                buffer.append(',');
            }
            this.rdns[i].toString(buffer, minimizeEncoding);
        }
    }
    
    public String toNormalizedString() {
        if (this.normalizedString == null) {
            final StringBuilder buffer = new StringBuilder();
            this.toNormalizedString(buffer);
            this.normalizedString = buffer.toString();
        }
        return this.normalizedString;
    }
    
    public void toNormalizedString(final StringBuilder buffer) {
        for (int i = 0; i < this.rdns.length; ++i) {
            if (i > 0) {
                buffer.append(',');
            }
            buffer.append(this.rdns[i].toNormalizedString());
        }
    }
    
    public static String normalize(final String s) throws LDAPException {
        return normalize(s, null);
    }
    
    public static String normalize(final String s, final Schema schema) throws LDAPException {
        return new DN(s, schema).toNormalizedString();
    }
    
    @Override
    public int compareTo(final DN dn) {
        return this.compare(this, dn);
    }
    
    @Override
    public int compare(final DN dn1, final DN dn2) {
        Validator.ensureNotNull(dn1, dn2);
        int pos1 = dn1.rdns.length - 1;
        int pos2 = dn2.rdns.length - 1;
        if (pos1 < 0) {
            if (pos2 < 0) {
                return 0;
            }
            return -1;
        }
        else {
            if (pos2 < 0) {
                return 1;
            }
            while (pos1 >= 0 && pos2 >= 0) {
                final int compValue = dn1.rdns[pos1].compareTo(dn2.rdns[pos2]);
                if (compValue != 0) {
                    return compValue;
                }
                --pos1;
                --pos2;
            }
            if (pos1 >= 0) {
                return 1;
            }
            if (pos2 < 0) {
                return 0;
            }
            return -1;
        }
    }
    
    public static int compare(final String s1, final String s2) throws LDAPException {
        return compare(s1, s2, null);
    }
    
    public static int compare(final String s1, final String s2, final Schema schema) throws LDAPException {
        return new DN(s1, schema).compareTo(new DN(s2, schema));
    }
    
    static {
        NO_RDNS = new RDN[0];
        NULL_DN = new DN(new RDN[0]);
    }
}
