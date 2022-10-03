package com.unboundid.util;

import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.LDAPConnection;
import java.util.Arrays;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPInterface;
import java.util.Iterator;
import com.unboundid.ldap.sdk.RDN;
import com.unboundid.ldap.matchingrules.MatchingRule;
import com.unboundid.ldap.matchingrules.DistinguishedNameMatchingRule;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.Attribute;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LDAPTestUtils
{
    private LDAPTestUtils() {
    }
    
    public static Entry generateDomainEntry(final String name, final String parentDN, final Attribute... additionalAttributes) {
        return generateDomainEntry(name, parentDN, StaticUtils.toList(additionalAttributes));
    }
    
    public static Entry generateDomainEntry(final String name, final String parentDN, final Collection<Attribute> additionalAttributes) {
        return generateEntry("dc", name, parentDN, new String[] { "top", "domain" }, additionalAttributes);
    }
    
    public static Entry generateOrgEntry(final String name, final String parentDN, final Attribute... additionalAttributes) {
        return generateOrgEntry(name, parentDN, StaticUtils.toList(additionalAttributes));
    }
    
    public static Entry generateOrgEntry(final String name, final String parentDN, final Collection<Attribute> additionalAttributes) {
        return generateEntry("o", name, parentDN, new String[] { "top", "organization" }, additionalAttributes);
    }
    
    public static Entry generateOrgUnitEntry(final String name, final String parentDN, final Attribute... additionalAttributes) {
        return generateOrgUnitEntry(name, parentDN, StaticUtils.toList(additionalAttributes));
    }
    
    public static Entry generateOrgUnitEntry(final String name, final String parentDN, final Collection<Attribute> additionalAttributes) {
        return generateEntry("ou", name, parentDN, new String[] { "top", "organizationalUnit" }, additionalAttributes);
    }
    
    public static Entry generateCountryEntry(final String name, final String parentDN, final Attribute... additionalAttributes) {
        return generateCountryEntry(name, parentDN, StaticUtils.toList(additionalAttributes));
    }
    
    public static Entry generateCountryEntry(final String name, final String parentDN, final Collection<Attribute> additionalAttributes) {
        return generateEntry("c", name, parentDN, new String[] { "top", "country" }, additionalAttributes);
    }
    
    public static Entry generateUserEntry(final String uid, final String parentDN, final String firstName, final String lastName, final String password, final Attribute... additionalAttributes) {
        return generateUserEntry(uid, parentDN, firstName, lastName, password, StaticUtils.toList(additionalAttributes));
    }
    
    public static Entry generateUserEntry(final String uid, final String parentDN, final String firstName, final String lastName, final String password, final Collection<Attribute> additionalAttributes) {
        final List<Attribute> attrList = new ArrayList<Attribute>(4);
        attrList.add(new Attribute("givenName", firstName));
        attrList.add(new Attribute("sn", lastName));
        attrList.add(new Attribute("cn", firstName + ' ' + lastName));
        if (password != null) {
            attrList.add(new Attribute("userPassword", password));
        }
        if (additionalAttributes != null) {
            attrList.addAll(additionalAttributes);
        }
        final String[] objectClasses = { "top", "person", "organizationalPerson", "inetOrgPerson" };
        return generateEntry("uid", uid, parentDN, objectClasses, attrList);
    }
    
    public static Entry generateGroupOfNamesEntry(final String name, final String parentDN, final String... memberDNs) {
        return generateGroupOfNamesEntry(name, parentDN, StaticUtils.toList(memberDNs));
    }
    
    public static Entry generateGroupOfNamesEntry(final String name, final String parentDN, final Collection<String> memberDNs) {
        final ArrayList<Attribute> attrList = new ArrayList<Attribute>(1);
        attrList.add(new Attribute("member", DistinguishedNameMatchingRule.getInstance(), memberDNs));
        return generateEntry("cn", name, parentDN, new String[] { "top", "groupOfNames" }, attrList);
    }
    
    public static Entry generateGroupOfUniqueNamesEntry(final String name, final String parentDN, final String... memberDNs) {
        return generateGroupOfUniqueNamesEntry(name, parentDN, StaticUtils.toList(memberDNs));
    }
    
    public static Entry generateGroupOfUniqueNamesEntry(final String name, final String parentDN, final Collection<String> memberDNs) {
        final ArrayList<Attribute> attrList = new ArrayList<Attribute>(1);
        attrList.add(new Attribute("uniqueMember", DistinguishedNameMatchingRule.getInstance(), memberDNs));
        return generateEntry("cn", name, parentDN, new String[] { "top", "groupOfUniqueNames" }, attrList);
    }
    
    private static Entry generateEntry(final String rdnAttr, final String rdnValue, final String parentDN, final String[] objectClasses, final Collection<Attribute> additionalAttributes) {
        final RDN rdn = new RDN(rdnAttr, rdnValue);
        String dn;
        if (parentDN == null || parentDN.trim().isEmpty()) {
            dn = rdn.toString();
        }
        else {
            dn = rdn.toString() + ',' + parentDN;
        }
        final Entry entry = new Entry(dn, new Attribute[] { new Attribute("objectClass", objectClasses), new Attribute(rdnAttr, rdnValue) });
        if (additionalAttributes != null) {
            for (final Attribute a : additionalAttributes) {
                entry.addAttribute(a);
            }
        }
        return entry;
    }
    
    public static boolean entryExists(final LDAPInterface conn, final String dn) throws LDAPException {
        return conn.getEntry(dn, "1.1") != null;
    }
    
    public static boolean entryExists(final LDAPInterface conn, final String dn, final String filter) throws LDAPException {
        try {
            final SearchResult searchResult = conn.search(dn, SearchScope.BASE, filter, "1.1");
            return searchResult.getEntryCount() == 1;
        }
        catch (final LDAPException le) {
            if (le.getResultCode() == ResultCode.NO_SUCH_OBJECT) {
                return false;
            }
            throw le;
        }
    }
    
    public static boolean entryExists(final LDAPInterface conn, final Entry entry) throws LDAPException {
        final Collection<Attribute> attrs = entry.getAttributes();
        final List<Filter> comps = new ArrayList<Filter>(attrs.size());
        for (final Attribute a : attrs) {
            for (final byte[] value : a.getValueByteArrays()) {
                comps.add(Filter.createEqualityFilter(a.getName(), value));
            }
        }
        try {
            final SearchResult searchResult = conn.search(entry.getDN(), SearchScope.BASE, Filter.createANDFilter(comps), "1.1");
            return searchResult.getEntryCount() == 1;
        }
        catch (final LDAPException le) {
            if (le.getResultCode() == ResultCode.NO_SUCH_OBJECT) {
                return false;
            }
            throw le;
        }
    }
    
    public static void assertEntryExists(final LDAPInterface conn, final String dn) throws LDAPException, AssertionError {
        if (conn.getEntry(dn, "1.1") == null) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_ENTRY_MISSING.get(dn));
        }
    }
    
    public static void assertEntryExists(final LDAPInterface conn, final String dn, final String filter) throws LDAPException, AssertionError {
        try {
            final SearchResult searchResult = conn.search(dn, SearchScope.BASE, filter, "1.1");
            if (searchResult.getEntryCount() == 0) {
                throw new AssertionError((Object)UtilityMessages.ERR_TEST_ENTRY_DOES_NOT_MATCH_FILTER.get(dn, filter));
            }
        }
        catch (final LDAPException le) {
            if (le.getResultCode() == ResultCode.NO_SUCH_OBJECT) {
                throw new AssertionError((Object)UtilityMessages.ERR_TEST_ENTRY_MISSING.get(dn));
            }
            throw le;
        }
    }
    
    public static void assertEntryExists(final LDAPInterface conn, final Entry entry) throws LDAPException, AssertionError {
        if (entryExists(conn, entry)) {
            return;
        }
        final Collection<Attribute> attributes = entry.getAttributes();
        final List<String> messages = new ArrayList<String>(attributes.size());
        for (final Attribute a : attributes) {
            try {
                final SearchResult searchResult = conn.search(entry.getDN(), SearchScope.BASE, Filter.createPresenceFilter(a.getName()), "1.1");
                if (searchResult.getEntryCount() == 0) {
                    messages.add(UtilityMessages.ERR_TEST_ATTR_MISSING.get(entry.getDN(), a.getName()));
                    continue;
                }
            }
            catch (final LDAPException le) {
                if (le.getResultCode() == ResultCode.NO_SUCH_OBJECT) {
                    throw new AssertionError((Object)UtilityMessages.ERR_TEST_ENTRY_MISSING.get(entry.getDN()));
                }
                throw le;
            }
            for (final byte[] value : a.getValueByteArrays()) {
                final SearchResult searchResult2 = conn.search(entry.getDN(), SearchScope.BASE, Filter.createEqualityFilter(a.getName(), value), "1.1");
                if (searchResult2.getEntryCount() == 0) {
                    messages.add(UtilityMessages.ERR_TEST_VALUE_MISSING.get(entry.getDN(), a.getName(), StaticUtils.toUTF8String(value)));
                }
            }
        }
        if (!messages.isEmpty()) {
            throw new AssertionError((Object)StaticUtils.concatenateStrings(messages));
        }
    }
    
    public static List<String> getMissingEntryDNs(final LDAPInterface conn, final String... dns) throws LDAPException {
        return getMissingEntryDNs(conn, StaticUtils.toList(dns));
    }
    
    public static List<String> getMissingEntryDNs(final LDAPInterface conn, final Collection<String> dns) throws LDAPException {
        final List<String> missingDNs = new ArrayList<String>(dns.size());
        for (final String dn : dns) {
            if (conn.getEntry(dn, "1.1") == null) {
                missingDNs.add(dn);
            }
        }
        return missingDNs;
    }
    
    public static void assertEntriesExist(final LDAPInterface conn, final String... dns) throws LDAPException, AssertionError {
        assertEntriesExist(conn, StaticUtils.toList(dns));
    }
    
    public static void assertEntriesExist(final LDAPInterface conn, final Collection<String> dns) throws LDAPException, AssertionError {
        final List<String> missingDNs = getMissingEntryDNs(conn, dns);
        if (missingDNs.isEmpty()) {
            return;
        }
        final ArrayList<String> msgList = new ArrayList<String>(missingDNs.size());
        for (final String dn : missingDNs) {
            msgList.add(UtilityMessages.ERR_TEST_ENTRY_MISSING.get(dn));
        }
        throw new AssertionError((Object)StaticUtils.concatenateStrings(msgList));
    }
    
    public static List<String> getMissingAttributeNames(final LDAPInterface conn, final String dn, final String... attributeNames) throws LDAPException {
        return getMissingAttributeNames(conn, dn, StaticUtils.toList(attributeNames));
    }
    
    public static List<String> getMissingAttributeNames(final LDAPInterface conn, final String dn, final Collection<String> attributeNames) throws LDAPException {
        final List<String> missingAttrs = new ArrayList<String>(attributeNames.size());
        for (final String attrName : attributeNames) {
            try {
                final SearchResult result = conn.search(dn, SearchScope.BASE, Filter.createPresenceFilter(attrName), new String[0]);
                if (result.getEntryCount() != 0) {
                    continue;
                }
                missingAttrs.add(attrName);
            }
            catch (final LDAPException le) {
                if (le.getResultCode() == ResultCode.NO_SUCH_OBJECT) {
                    return null;
                }
                throw le;
            }
        }
        return missingAttrs;
    }
    
    public static void assertAttributeExists(final LDAPInterface conn, final String dn, final String... attributeNames) throws LDAPException, AssertionError {
        assertAttributeExists(conn, dn, StaticUtils.toList(attributeNames));
    }
    
    public static void assertAttributeExists(final LDAPInterface conn, final String dn, final Collection<String> attributeNames) throws LDAPException, AssertionError {
        final List<String> missingAttrs = getMissingAttributeNames(conn, dn, attributeNames);
        if (missingAttrs == null) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_ENTRY_MISSING.get(dn));
        }
        if (missingAttrs.isEmpty()) {
            return;
        }
        final List<String> msgList = new ArrayList<String>(missingAttrs.size());
        for (final String attrName : missingAttrs) {
            msgList.add(UtilityMessages.ERR_TEST_ATTR_MISSING.get(dn, attrName));
        }
        throw new AssertionError((Object)StaticUtils.concatenateStrings(msgList));
    }
    
    public static List<String> getMissingAttributeValues(final LDAPInterface conn, final String dn, final String attributeName, final String... attributeValues) throws LDAPException {
        return getMissingAttributeValues(conn, dn, attributeName, StaticUtils.toList(attributeValues));
    }
    
    public static List<String> getMissingAttributeValues(final LDAPInterface conn, final String dn, final String attributeName, final Collection<String> attributeValues) throws LDAPException {
        final List<String> missingValues = new ArrayList<String>(attributeValues.size());
        for (final String value : attributeValues) {
            try {
                final SearchResult searchResult = conn.search(dn, SearchScope.BASE, Filter.createEqualityFilter(attributeName, value), "1.1");
                if (searchResult.getEntryCount() != 0) {
                    continue;
                }
                missingValues.add(value);
            }
            catch (final LDAPException le) {
                if (le.getResultCode() == ResultCode.NO_SUCH_OBJECT) {
                    return null;
                }
                throw le;
            }
        }
        return missingValues;
    }
    
    public static void assertValueExists(final LDAPInterface conn, final String dn, final String attributeName, final String... attributeValues) throws LDAPException, AssertionError {
        assertValueExists(conn, dn, attributeName, StaticUtils.toList(attributeValues));
    }
    
    public static void assertValueExists(final LDAPInterface conn, final String dn, final String attributeName, final Collection<String> attributeValues) throws LDAPException, AssertionError {
        final List<String> missingValues = getMissingAttributeValues(conn, dn, attributeName, attributeValues);
        if (missingValues == null) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_ENTRY_MISSING.get(dn));
        }
        if (missingValues.isEmpty()) {
            return;
        }
        final Entry entry = conn.getEntry(dn, attributeName);
        if (entry != null && entry.hasAttribute(attributeName)) {
            final Attribute a = entry.getAttribute(attributeName);
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_ATTR_MISSING_VALUE.get(dn, attributeName, StaticUtils.concatenateStrings("{", " '", ",", "'", " }", a.getValues()), StaticUtils.concatenateStrings("{", " '", ",", "'", " }", missingValues)));
        }
        throw new AssertionError((Object)UtilityMessages.ERR_TEST_ATTR_MISSING.get(dn, attributeName));
    }
    
    public static void assertEntryMissing(final LDAPInterface conn, final String dn) throws LDAPException, AssertionError {
        if (conn.getEntry(dn, "1.1") != null) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_ENTRY_EXISTS.get(dn));
        }
    }
    
    public static void assertAttributeMissing(final LDAPInterface conn, final String dn, final String... attributeNames) throws LDAPException, AssertionError {
        assertAttributeMissing(conn, dn, StaticUtils.toList(attributeNames));
    }
    
    public static void assertAttributeMissing(final LDAPInterface conn, final String dn, final Collection<String> attributeNames) throws LDAPException, AssertionError {
        final List<String> messages = new ArrayList<String>(attributeNames.size());
        for (final String attrName : attributeNames) {
            try {
                final SearchResult searchResult = conn.search(dn, SearchScope.BASE, Filter.createPresenceFilter(attrName), attrName);
                if (searchResult.getEntryCount() != 1) {
                    continue;
                }
                final Attribute a = searchResult.getSearchEntries().get(0).getAttribute(attrName);
                if (a == null) {
                    messages.add(UtilityMessages.ERR_TEST_ATTR_EXISTS.get(dn, attrName));
                }
                else {
                    messages.add(UtilityMessages.ERR_TEST_ATTR_EXISTS_WITH_VALUES.get(dn, attrName, StaticUtils.concatenateStrings("{", " '", ",", "'", " }", a.getValues())));
                }
            }
            catch (final LDAPException le) {
                if (le.getResultCode() == ResultCode.NO_SUCH_OBJECT) {
                    throw new AssertionError((Object)UtilityMessages.ERR_TEST_ENTRY_MISSING.get(dn));
                }
                throw le;
            }
        }
        if (!messages.isEmpty()) {
            throw new AssertionError((Object)StaticUtils.concatenateStrings(messages));
        }
    }
    
    public static void assertValueMissing(final LDAPInterface conn, final String dn, final String attributeName, final String... attributeValues) throws LDAPException, AssertionError {
        assertValueMissing(conn, dn, attributeName, StaticUtils.toList(attributeValues));
    }
    
    public static void assertValueMissing(final LDAPInterface conn, final String dn, final String attributeName, final Collection<String> attributeValues) throws LDAPException, AssertionError {
        final List<String> messages = new ArrayList<String>(attributeValues.size());
        for (final String value : attributeValues) {
            try {
                final SearchResult searchResult = conn.search(dn, SearchScope.BASE, Filter.createEqualityFilter(attributeName, value), "1.1");
                if (searchResult.getEntryCount() != 1) {
                    continue;
                }
                messages.add(UtilityMessages.ERR_TEST_VALUE_EXISTS.get(dn, attributeName, value));
            }
            catch (final LDAPException le) {
                if (le.getResultCode() == ResultCode.NO_SUCH_OBJECT) {
                    throw new AssertionError((Object)UtilityMessages.ERR_TEST_ENTRY_MISSING.get(dn));
                }
                throw le;
            }
        }
        if (!messages.isEmpty()) {
            throw new AssertionError((Object)StaticUtils.concatenateStrings(messages));
        }
    }
    
    public static void assertResultCodeEquals(final LDAPResult result, final ResultCode... acceptableResultCodes) throws AssertionError {
        for (final ResultCode rc : acceptableResultCodes) {
            if (rc.equals(result.getResultCode())) {
                return;
            }
        }
        if (acceptableResultCodes.length == 1) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_SINGLE_RESULT_CODE_MISSING.get(String.valueOf(result), String.valueOf(acceptableResultCodes[0])));
        }
        throw new AssertionError((Object)UtilityMessages.ERR_TEST_MULTI_RESULT_CODE_MISSING.get(String.valueOf(result), Arrays.toString(acceptableResultCodes)));
    }
    
    public static void assertResultCodeEquals(final LDAPException exception, final ResultCode... acceptableResultCodes) throws AssertionError {
        for (final ResultCode rc : acceptableResultCodes) {
            if (rc.equals(exception.getResultCode())) {
                return;
            }
        }
        if (acceptableResultCodes.length == 1) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_SINGLE_RESULT_CODE_MISSING.get(StaticUtils.getExceptionMessage(exception), String.valueOf(acceptableResultCodes[0])));
        }
        throw new AssertionError((Object)UtilityMessages.ERR_TEST_MULTI_RESULT_CODE_MISSING.get(StaticUtils.getExceptionMessage(exception), Arrays.toString(acceptableResultCodes)));
    }
    
    public static LDAPResult assertResultCodeEquals(final LDAPConnection conn, final LDAPRequest request, final ResultCode... acceptableResultCodes) throws AssertionError {
        LDAPResult result;
        try {
            result = conn.processOperation(request);
        }
        catch (final LDAPException le) {
            result = le.toLDAPResult();
        }
        for (final ResultCode rc : acceptableResultCodes) {
            if (rc.equals(result.getResultCode())) {
                return result;
            }
        }
        if (acceptableResultCodes.length == 1) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_SINGLE_RESULT_CODE_MISSING.get(String.valueOf(result), String.valueOf(acceptableResultCodes[0])));
        }
        throw new AssertionError((Object)UtilityMessages.ERR_TEST_MULTI_RESULT_CODE_MISSING.get(String.valueOf(result), Arrays.toString(acceptableResultCodes)));
    }
    
    public static void assertResultCodeNot(final LDAPResult result, final ResultCode... unacceptableResultCodes) throws AssertionError {
        final ResultCode[] arr$ = unacceptableResultCodes;
        final int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            final ResultCode rc = arr$[i$];
            if (rc.equals(result.getResultCode())) {
                if (unacceptableResultCodes.length == 1) {
                    throw new AssertionError((Object)UtilityMessages.ERR_TEST_SINGLE_RESULT_CODE_FOUND.get(String.valueOf(result), String.valueOf(unacceptableResultCodes[0])));
                }
                throw new AssertionError((Object)UtilityMessages.ERR_TEST_MULTI_RESULT_CODE_FOUND.get(String.valueOf(result), Arrays.toString(unacceptableResultCodes)));
            }
            else {
                ++i$;
            }
        }
    }
    
    public static void assertResultCodeNot(final LDAPException exception, final ResultCode... unacceptableResultCodes) throws AssertionError {
        final ResultCode[] arr$ = unacceptableResultCodes;
        final int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            final ResultCode rc = arr$[i$];
            if (rc.equals(exception.getResultCode())) {
                if (unacceptableResultCodes.length == 1) {
                    throw new AssertionError((Object)UtilityMessages.ERR_TEST_SINGLE_RESULT_CODE_FOUND.get(StaticUtils.getExceptionMessage(exception), String.valueOf(unacceptableResultCodes[0])));
                }
                throw new AssertionError((Object)UtilityMessages.ERR_TEST_MULTI_RESULT_CODE_FOUND.get(StaticUtils.getExceptionMessage(exception), Arrays.toString(unacceptableResultCodes)));
            }
            else {
                ++i$;
            }
        }
    }
    
    public static LDAPResult assertResultCodeNot(final LDAPConnection conn, final LDAPRequest request, final ResultCode... unacceptableResultCodes) throws AssertionError {
        LDAPResult result;
        try {
            result = conn.processOperation(request);
        }
        catch (final LDAPException le) {
            result = le.toLDAPResult();
        }
        final ResultCode[] arr$ = unacceptableResultCodes;
        final int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            final ResultCode rc = arr$[i$];
            if (rc.equals(result.getResultCode())) {
                if (unacceptableResultCodes.length == 1) {
                    throw new AssertionError((Object)UtilityMessages.ERR_TEST_SINGLE_RESULT_CODE_FOUND.get(String.valueOf(result), String.valueOf(unacceptableResultCodes[0])));
                }
                throw new AssertionError((Object)UtilityMessages.ERR_TEST_MULTI_RESULT_CODE_FOUND.get(String.valueOf(result), Arrays.toString(unacceptableResultCodes)));
            }
            else {
                ++i$;
            }
        }
        return result;
    }
    
    public static void assertContainsMatchedDN(final LDAPResult result) throws AssertionError {
        if (result.getMatchedDN() == null) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_RESULT_MISSING_MATCHED_DN.get(String.valueOf(result)));
        }
    }
    
    public static void assertContainsMatchedDN(final LDAPException exception) throws AssertionError {
        if (exception.getMatchedDN() == null) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_RESULT_MISSING_MATCHED_DN.get(StaticUtils.getExceptionMessage(exception)));
        }
    }
    
    public static void assertMissingMatchedDN(final LDAPResult result) throws AssertionError {
        if (result.getMatchedDN() != null) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_RESULT_CONTAINS_MATCHED_DN.get(String.valueOf(result), result.getMatchedDN()));
        }
    }
    
    public static void assertMissingMatchedDN(final LDAPException exception) throws AssertionError {
        if (exception.getMatchedDN() != null) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_RESULT_CONTAINS_MATCHED_DN.get(StaticUtils.getExceptionMessage(exception), exception.getMatchedDN()));
        }
    }
    
    public static void assertMatchedDNEquals(final LDAPResult result, final String matchedDN) throws LDAPException, AssertionError {
        if (result.getMatchedDN() == null) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_RESULT_MISSING_EXPECTED_MATCHED_DN.get(String.valueOf(result), matchedDN));
        }
        final DN foundDN = new DN(result.getMatchedDN());
        final DN expectedDN = new DN(matchedDN);
        if (!foundDN.equals(expectedDN)) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_MATCHED_DN_MISMATCH.get(String.valueOf(result), matchedDN, result.getMatchedDN()));
        }
    }
    
    public static void assertMatchedDNEquals(final LDAPException exception, final String matchedDN) throws LDAPException, AssertionError {
        if (exception.getMatchedDN() == null) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_RESULT_MISSING_EXPECTED_MATCHED_DN.get(StaticUtils.getExceptionMessage(exception), matchedDN));
        }
        final DN foundDN = new DN(exception.getMatchedDN());
        final DN expectedDN = new DN(matchedDN);
        if (!foundDN.equals(expectedDN)) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_MATCHED_DN_MISMATCH.get(StaticUtils.getExceptionMessage(exception), matchedDN, exception.getMatchedDN()));
        }
    }
    
    public static void assertContainsDiagnosticMessage(final LDAPResult result) throws AssertionError {
        if (result.getDiagnosticMessage() == null) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_RESULT_MISSING_DIAGNOSTIC_MESSAGE.get(String.valueOf(result)));
        }
    }
    
    public static void assertContainsDiagnosticMessage(final LDAPException exception) throws AssertionError {
        if (exception.getDiagnosticMessage() == null) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_RESULT_MISSING_DIAGNOSTIC_MESSAGE.get(StaticUtils.getExceptionMessage(exception)));
        }
    }
    
    public static void assertMissingDiagnosticMessage(final LDAPResult result) throws AssertionError {
        if (result.getDiagnosticMessage() != null) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_RESULT_CONTAINS_DIAGNOSTIC_MESSAGE.get(String.valueOf(result), result.getDiagnosticMessage()));
        }
    }
    
    public static void assertMissingDiagnosticMessage(final LDAPException exception) throws AssertionError {
        if (exception.getDiagnosticMessage() != null) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_RESULT_CONTAINS_DIAGNOSTIC_MESSAGE.get(StaticUtils.getExceptionMessage(exception), exception.getDiagnosticMessage()));
        }
    }
    
    public static void assertDiagnosticMessageEquals(final LDAPResult result, final String diagnosticMessage) throws AssertionError {
        if (result.getDiagnosticMessage() == null) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_RESULT_MISSING_EXPECTED_DIAGNOSTIC_MESSAGE.get(String.valueOf(result), diagnosticMessage));
        }
        if (!result.getDiagnosticMessage().equals(diagnosticMessage)) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_DIAGNOSTIC_MESSAGE_MISMATCH.get(String.valueOf(result), diagnosticMessage, result.getDiagnosticMessage()));
        }
    }
    
    public static void assertDiagnosticMessageEquals(final LDAPException exception, final String diagnosticMessage) throws AssertionError {
        if (exception.getDiagnosticMessage() == null) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_RESULT_MISSING_EXPECTED_DIAGNOSTIC_MESSAGE.get(StaticUtils.getExceptionMessage(exception), diagnosticMessage));
        }
        if (!exception.getDiagnosticMessage().equals(diagnosticMessage)) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_DIAGNOSTIC_MESSAGE_MISMATCH.get(StaticUtils.getExceptionMessage(exception), diagnosticMessage, exception.getDiagnosticMessage()));
        }
    }
    
    public static void assertHasReferral(final LDAPResult result) throws AssertionError {
        final String[] referralURLs = result.getReferralURLs();
        if (referralURLs == null || referralURLs.length == 0) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_RESULT_MISSING_REFERRAL.get(String.valueOf(result)));
        }
    }
    
    public static void assertHasReferral(final LDAPException exception) throws AssertionError {
        final String[] referralURLs = exception.getReferralURLs();
        if (referralURLs == null || referralURLs.length == 0) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_RESULT_MISSING_REFERRAL.get(StaticUtils.getExceptionMessage(exception)));
        }
    }
    
    public static void assertMissingReferral(final LDAPResult result) throws AssertionError {
        final String[] referralURLs = result.getReferralURLs();
        if (referralURLs != null && referralURLs.length > 0) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_RESULT_HAS_REFERRAL.get(String.valueOf(result)));
        }
    }
    
    public static void assertMissingReferral(final LDAPException exception) throws AssertionError {
        final String[] referralURLs = exception.getReferralURLs();
        if (referralURLs != null && referralURLs.length > 0) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_RESULT_HAS_REFERRAL.get(StaticUtils.getExceptionMessage(exception)));
        }
    }
    
    public static Control assertHasControl(final LDAPResult result, final String oid) throws AssertionError {
        for (final Control c : result.getResponseControls()) {
            if (c.getOID().equals(oid)) {
                return c;
            }
        }
        throw new AssertionError((Object)UtilityMessages.ERR_TEST_RESULT_MISSING_CONTROL.get(String.valueOf(result), oid));
    }
    
    public static Control assertHasControl(final LDAPException exception, final String oid) throws AssertionError {
        for (final Control c : exception.getResponseControls()) {
            if (c.getOID().equals(oid)) {
                return c;
            }
        }
        throw new AssertionError((Object)UtilityMessages.ERR_TEST_RESULT_MISSING_CONTROL.get(StaticUtils.getExceptionMessage(exception), oid));
    }
    
    public static Control assertHasControl(final SearchResultEntry entry, final String oid) throws AssertionError {
        for (final Control c : entry.getControls()) {
            if (c.getOID().equals(oid)) {
                return c;
            }
        }
        throw new AssertionError((Object)UtilityMessages.ERR_TEST_ENTRY_MISSING_CONTROL.get(String.valueOf(entry), oid));
    }
    
    public static Control assertHasControl(final SearchResultReference reference, final String oid) throws AssertionError {
        for (final Control c : reference.getControls()) {
            if (c.getOID().equals(oid)) {
                return c;
            }
        }
        throw new AssertionError((Object)UtilityMessages.ERR_TEST_REF_MISSING_CONTROL.get(String.valueOf(reference), oid));
    }
    
    public static void assertMissingControl(final LDAPResult result, final String oid) throws AssertionError {
        for (final Control c : result.getResponseControls()) {
            if (c.getOID().equals(oid)) {
                throw new AssertionError((Object)UtilityMessages.ERR_TEST_RESULT_HAS_CONTROL.get(String.valueOf(result), oid));
            }
        }
    }
    
    public static void assertMissingControl(final LDAPException exception, final String oid) throws AssertionError {
        for (final Control c : exception.getResponseControls()) {
            if (c.getOID().equals(oid)) {
                throw new AssertionError((Object)UtilityMessages.ERR_TEST_RESULT_HAS_CONTROL.get(StaticUtils.getExceptionMessage(exception), oid));
            }
        }
    }
    
    public static void assertMissingControl(final SearchResultEntry entry, final String oid) throws AssertionError {
        for (final Control c : entry.getControls()) {
            if (c.getOID().equals(oid)) {
                throw new AssertionError((Object)UtilityMessages.ERR_TEST_ENTRY_HAS_CONTROL.get(String.valueOf(entry), oid));
            }
        }
    }
    
    public static void assertMissingControl(final SearchResultReference reference, final String oid) throws AssertionError {
        for (final Control c : reference.getControls()) {
            if (c.getOID().equals(oid)) {
                throw new AssertionError((Object)UtilityMessages.ERR_TEST_REF_HAS_CONTROL.get(String.valueOf(reference), oid));
            }
        }
    }
    
    public static int assertEntryReturned(final SearchResult result) throws AssertionError {
        if (result.getEntryCount() == 0) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_SEARCH_NO_ENTRIES_RETURNED.get(String.valueOf(result)));
        }
        return result.getEntryCount();
    }
    
    public static int assertEntryReturned(final LDAPSearchException exception) throws AssertionError {
        if (exception.getEntryCount() == 0) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_SEARCH_NO_ENTRIES_RETURNED.get(StaticUtils.getExceptionMessage(exception)));
        }
        return exception.getEntryCount();
    }
    
    public static SearchResultEntry assertEntryReturned(final SearchResult result, final String dn) throws LDAPException, AssertionError {
        final DN parsedDN = new DN(dn);
        final List<SearchResultEntry> entryList = result.getSearchEntries();
        if (entryList != null) {
            for (final SearchResultEntry e : entryList) {
                if (e.getParsedDN().equals(parsedDN)) {
                    return e;
                }
            }
        }
        throw new AssertionError((Object)UtilityMessages.ERR_TEST_SEARCH_ENTRY_NOT_RETURNED.get(String.valueOf(result), dn));
    }
    
    public static SearchResultEntry assertEntryReturned(final LDAPSearchException exception, final String dn) throws LDAPException, AssertionError {
        final DN parsedDN = new DN(dn);
        final List<SearchResultEntry> entryList = exception.getSearchEntries();
        if (entryList != null) {
            for (final SearchResultEntry e : entryList) {
                if (e.getParsedDN().equals(parsedDN)) {
                    return e;
                }
            }
        }
        throw new AssertionError((Object)UtilityMessages.ERR_TEST_SEARCH_ENTRY_NOT_RETURNED.get(StaticUtils.getExceptionMessage(exception), dn));
    }
    
    public static void assertNoEntriesReturned(final SearchResult result) throws AssertionError {
        if (result.getEntryCount() > 0) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_SEARCH_ENTRIES_RETURNED.get(String.valueOf(result), result.getEntryCount()));
        }
    }
    
    public static void assertNoEntriesReturned(final LDAPSearchException exception) throws AssertionError {
        if (exception.getEntryCount() > 0) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_SEARCH_ENTRIES_RETURNED.get(StaticUtils.getExceptionMessage(exception), exception.getEntryCount()));
        }
    }
    
    public static void assertEntriesReturnedEquals(final SearchResult result, final int expectedEntryCount) throws AssertionError {
        if (result.getEntryCount() == expectedEntryCount) {
            return;
        }
        if (expectedEntryCount == 1) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_SEARCH_ENTRY_COUNT_MISMATCH_ONE_EXPECTED.get(String.valueOf(result), result.getEntryCount()));
        }
        throw new AssertionError((Object)UtilityMessages.ERR_TEST_SEARCH_ENTRY_COUNT_MISMATCH_MULTI_EXPECTED.get(expectedEntryCount, String.valueOf(result), result.getEntryCount()));
    }
    
    public static void assertEntriesReturnedEquals(final LDAPSearchException exception, final int expectedEntryCount) throws AssertionError {
        if (exception.getEntryCount() == expectedEntryCount) {
            return;
        }
        if (expectedEntryCount == 1) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_SEARCH_ENTRY_COUNT_MISMATCH_ONE_EXPECTED.get(StaticUtils.getExceptionMessage(exception), exception.getEntryCount()));
        }
        throw new AssertionError((Object)UtilityMessages.ERR_TEST_SEARCH_ENTRY_COUNT_MISMATCH_MULTI_EXPECTED.get(expectedEntryCount, StaticUtils.getExceptionMessage(exception), exception.getEntryCount()));
    }
    
    public static int assertReferenceReturned(final SearchResult result) throws AssertionError {
        if (result.getReferenceCount() == 0) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_SEARCH_NO_REFS_RETURNED.get(String.valueOf(result)));
        }
        return result.getReferenceCount();
    }
    
    public static int assertReferenceReturned(final LDAPSearchException exception) throws AssertionError {
        if (exception.getReferenceCount() == 0) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_SEARCH_NO_REFS_RETURNED.get(StaticUtils.getExceptionMessage(exception)));
        }
        return exception.getReferenceCount();
    }
    
    public static void assertNoReferencesReturned(final SearchResult result) throws AssertionError {
        if (result.getReferenceCount() > 0) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_SEARCH_REFS_RETURNED.get(String.valueOf(result), result.getReferenceCount()));
        }
    }
    
    public static void assertNoReferencesReturned(final LDAPSearchException exception) throws AssertionError {
        if (exception.getReferenceCount() > 0) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_SEARCH_REFS_RETURNED.get(StaticUtils.getExceptionMessage(exception), exception.getReferenceCount()));
        }
    }
    
    public static void assertReferencesReturnedEquals(final SearchResult result, final int expectedReferenceCount) throws AssertionError {
        if (result.getReferenceCount() == expectedReferenceCount) {
            return;
        }
        if (expectedReferenceCount == 1) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_SEARCH_REF_COUNT_MISMATCH_ONE_EXPECTED.get(String.valueOf(result), result.getReferenceCount()));
        }
        throw new AssertionError((Object)UtilityMessages.ERR_TEST_SEARCH_REF_COUNT_MISMATCH_MULTI_EXPECTED.get(expectedReferenceCount, String.valueOf(result), result.getReferenceCount()));
    }
    
    public static void assertReferencesReturnedEquals(final LDAPSearchException exception, final int expectedReferenceCount) throws AssertionError {
        if (exception.getReferenceCount() == expectedReferenceCount) {
            return;
        }
        if (expectedReferenceCount == 1) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_SEARCH_REF_COUNT_MISMATCH_ONE_EXPECTED.get(StaticUtils.getExceptionMessage(exception), exception.getReferenceCount()));
        }
        throw new AssertionError((Object)UtilityMessages.ERR_TEST_SEARCH_REF_COUNT_MISMATCH_MULTI_EXPECTED.get(expectedReferenceCount, StaticUtils.getExceptionMessage(exception), exception.getReferenceCount()));
    }
    
    public static void assertDNsEqual(final String s1, final String s2) throws AssertionError {
        DN dn1;
        try {
            dn1 = new DN(s1);
        }
        catch (final Exception e) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_VALUE_NOT_VALID_DN.get(s1, StaticUtils.getExceptionMessage(e)));
        }
        DN dn2;
        try {
            dn2 = new DN(s2);
        }
        catch (final Exception e2) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_VALUE_NOT_VALID_DN.get(s2, StaticUtils.getExceptionMessage(e2)));
        }
        if (!dn1.equals(dn2)) {
            throw new AssertionError((Object)UtilityMessages.ERR_TEST_DNS_NOT_EQUAL.get(s1, s2));
        }
    }
}
