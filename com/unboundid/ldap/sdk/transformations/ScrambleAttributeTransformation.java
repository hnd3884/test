package com.unboundid.ldap.sdk.transformations;

import java.util.Arrays;
import com.unboundid.util.json.JSONString;
import com.unboundid.util.json.JSONNumber;
import com.unboundid.util.json.JSONBoolean;
import java.util.List;
import com.unboundid.util.json.JSONArray;
import com.unboundid.util.json.JSONValue;
import java.util.LinkedHashMap;
import com.unboundid.util.json.JSONObject;
import com.unboundid.ldap.matchingrules.OctetStringMatchingRule;
import com.unboundid.ldap.matchingrules.TelephoneNumberMatchingRule;
import com.unboundid.ldap.matchingrules.NumericStringMatchingRule;
import com.unboundid.ldap.matchingrules.IntegerMatchingRule;
import com.unboundid.ldap.matchingrules.GeneralizedTimeMatchingRule;
import com.unboundid.ldap.matchingrules.DistinguishedNameMatchingRule;
import com.unboundid.ldap.matchingrules.BooleanMatchingRule;
import com.unboundid.ldap.sdk.RDN;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldif.LDIFModifyDNChangeRecord;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldif.LDIFModifyChangeRecord;
import com.unboundid.ldif.LDIFDeleteChangeRecord;
import com.unboundid.ldif.LDIFAddChangeRecord;
import com.unboundid.ldif.LDIFChangeRecord;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.schema.AttributeTypeDefinition;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Collections;
import com.unboundid.ldap.matchingrules.CaseIgnoreStringMatchingRule;
import com.unboundid.ldap.sdk.Attribute;
import java.util.HashMap;
import com.unboundid.util.Debug;
import com.unboundid.util.ThreadLocalRandom;
import com.unboundid.util.StaticUtils;
import java.util.Collection;
import java.util.Random;
import java.util.Set;
import com.unboundid.ldap.matchingrules.MatchingRule;
import java.util.Map;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ScrambleAttributeTransformation implements EntryTransformation, LDIFChangeRecordTransformation
{
    private static final char[] ASCII_DIGITS;
    private static final char[] ASCII_SYMBOLS;
    private static final char[] LOWERCASE_ASCII_LETTERS;
    private static final char[] UPPERCASE_ASCII_LETTERS;
    private static final long MILLIS_PER_DAY = 86400000L;
    private final boolean scrambleEntryDNs;
    private final long randomSeed;
    private final long createTime;
    private final Schema schema;
    private final Map<String, MatchingRule> attributes;
    private final Set<String> jsonFields;
    private final ThreadLocal<Random> randoms;
    
    public ScrambleAttributeTransformation(final String... attributes) {
        this(null, (Long)null, attributes);
    }
    
    public ScrambleAttributeTransformation(final Collection<String> attributes) {
        this(null, null, false, attributes, null);
    }
    
    public ScrambleAttributeTransformation(final Schema schema, final Long randomSeed, final String... attributes) {
        this(schema, randomSeed, false, StaticUtils.toList(attributes), null);
    }
    
    public ScrambleAttributeTransformation(final Schema schema, final Long randomSeed, final boolean scrambleEntryDNs, final Collection<String> attributes, final Collection<String> jsonFields) {
        this.createTime = System.currentTimeMillis();
        this.randoms = new ThreadLocal<Random>();
        this.scrambleEntryDNs = scrambleEntryDNs;
        if (randomSeed == null) {
            this.randomSeed = ThreadLocalRandom.get().nextLong();
        }
        else {
            this.randomSeed = randomSeed;
        }
        Schema s = schema;
        if (s == null) {
            try {
                s = Schema.getDefaultStandardSchema();
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
        this.schema = s;
        final HashMap<String, MatchingRule> m = new HashMap<String, MatchingRule>(StaticUtils.computeMapCapacity(10));
        for (final String a : attributes) {
            final String baseName = StaticUtils.toLowerCase(Attribute.getBaseName(a));
            AttributeTypeDefinition at = null;
            if (schema != null) {
                at = schema.getAttributeType(baseName);
            }
            if (at == null) {
                m.put(baseName, CaseIgnoreStringMatchingRule.getInstance());
            }
            else {
                final MatchingRule mr = MatchingRule.selectEqualityMatchingRule(baseName, schema);
                m.put(StaticUtils.toLowerCase(at.getOID()), mr);
                for (final String attrName : at.getNames()) {
                    m.put(StaticUtils.toLowerCase(attrName), mr);
                }
            }
        }
        this.attributes = Collections.unmodifiableMap((Map<? extends String, ? extends MatchingRule>)m);
        if (jsonFields == null) {
            this.jsonFields = Collections.emptySet();
        }
        else {
            final HashSet<String> fieldNames = new HashSet<String>(StaticUtils.computeMapCapacity(jsonFields.size()));
            for (final String fieldName : jsonFields) {
                fieldNames.add(StaticUtils.toLowerCase(fieldName));
            }
            this.jsonFields = Collections.unmodifiableSet((Set<? extends String>)fieldNames);
        }
    }
    
    @Override
    public Entry transformEntry(final Entry e) {
        if (e == null) {
            return null;
        }
        String dn;
        if (this.scrambleEntryDNs) {
            dn = this.scrambleDN(e.getDN());
        }
        else {
            dn = e.getDN();
        }
        final Collection<Attribute> originalAttributes = e.getAttributes();
        final ArrayList<Attribute> scrambledAttributes = new ArrayList<Attribute>(originalAttributes.size());
        for (final Attribute a : originalAttributes) {
            scrambledAttributes.add(this.scrambleAttribute(a));
        }
        return new Entry(dn, this.schema, scrambledAttributes);
    }
    
    @Override
    public LDIFChangeRecord transformChangeRecord(final LDIFChangeRecord r) {
        if (r == null) {
            return null;
        }
        if (r instanceof LDIFAddChangeRecord) {
            final LDIFAddChangeRecord addRecord = (LDIFAddChangeRecord)r;
            return new LDIFAddChangeRecord(this.transformEntry(addRecord.getEntryToAdd()), addRecord.getControls());
        }
        if (r instanceof LDIFDeleteChangeRecord) {
            if (this.scrambleEntryDNs) {
                return new LDIFDeleteChangeRecord(this.scrambleDN(r.getDN()), r.getControls());
            }
            return r;
        }
        else if (r instanceof LDIFModifyChangeRecord) {
            final LDIFModifyChangeRecord modifyRecord = (LDIFModifyChangeRecord)r;
            final Modification[] originalMods = modifyRecord.getModifications();
            final Modification[] newMods = new Modification[originalMods.length];
            for (int i = 0; i < originalMods.length; ++i) {
                final Modification m = originalMods[i];
                if (!m.hasValue()) {
                    newMods[i] = m;
                }
                else {
                    final String attrName = StaticUtils.toLowerCase(Attribute.getBaseName(m.getAttributeName()));
                    if (!this.attributes.containsKey(attrName)) {
                        newMods[i] = m;
                    }
                    else {
                        final Attribute scrambledAttribute = this.scrambleAttribute(m.getAttribute());
                        newMods[i] = new Modification(m.getModificationType(), m.getAttributeName(), scrambledAttribute.getRawValues());
                    }
                }
            }
            if (this.scrambleEntryDNs) {
                return new LDIFModifyChangeRecord(this.scrambleDN(modifyRecord.getDN()), newMods, modifyRecord.getControls());
            }
            return new LDIFModifyChangeRecord(modifyRecord.getDN(), newMods, modifyRecord.getControls());
        }
        else {
            if (!(r instanceof LDIFModifyDNChangeRecord)) {
                return r;
            }
            if (this.scrambleEntryDNs) {
                final LDIFModifyDNChangeRecord modDNRecord = (LDIFModifyDNChangeRecord)r;
                return new LDIFModifyDNChangeRecord(this.scrambleDN(modDNRecord.getDN()), this.scrambleDN(modDNRecord.getNewRDN()), modDNRecord.deleteOldRDN(), this.scrambleDN(modDNRecord.getNewSuperiorDN()), modDNRecord.getControls());
            }
            return r;
        }
    }
    
    public String scrambleDN(final String dn) {
        if (dn == null) {
            return null;
        }
        try {
            return this.scrambleDN(new DN(dn)).toString();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return dn;
        }
    }
    
    public DN scrambleDN(final DN dn) {
        if (dn == null || dn.isNullDN()) {
            return dn;
        }
        boolean changeApplied = false;
        final RDN[] originalRDNs = dn.getRDNs();
        final RDN[] scrambledRDNs = new RDN[originalRDNs.length];
        for (int i = 0; i < originalRDNs.length; ++i) {
            scrambledRDNs[i] = this.scrambleRDN(originalRDNs[i]);
            if (scrambledRDNs[i] != originalRDNs[i]) {
                changeApplied = true;
            }
        }
        if (changeApplied) {
            return new DN(scrambledRDNs);
        }
        return dn;
    }
    
    public RDN scrambleRDN(final RDN rdn) {
        boolean changeRequired = false;
        final String[] arr$;
        final String[] names = arr$ = rdn.getAttributeNames();
        for (final String s : arr$) {
            final String lowerBaseName = StaticUtils.toLowerCase(Attribute.getBaseName(s));
            if (this.attributes.containsKey(lowerBaseName)) {
                changeRequired = true;
                break;
            }
        }
        if (!changeRequired) {
            return rdn;
        }
        final Attribute[] originalAttrs = rdn.getAttributes();
        final byte[][] scrambledValues = new byte[originalAttrs.length][];
        for (int i = 0; i < originalAttrs.length; ++i) {
            scrambledValues[i] = this.scrambleAttribute(originalAttrs[i]).getValueByteArray();
        }
        return new RDN(names, scrambledValues, this.schema);
    }
    
    public Attribute scrambleAttribute(final Attribute a) {
        if (a == null || a.size() == 0) {
            return a;
        }
        final String baseName = StaticUtils.toLowerCase(a.getBaseName());
        final MatchingRule matchingRule = this.attributes.get(baseName);
        if (matchingRule == null) {
            return a;
        }
        if (matchingRule instanceof BooleanMatchingRule) {
            if (a.size() == 1) {
                return new Attribute(a.getName(), this.schema, new String[] { ThreadLocalRandom.get().nextBoolean() ? "TRUE" : "FALSE" });
            }
            return new Attribute(a.getName(), this.schema, new String[] { "TRUE", "FALSE" });
        }
        else {
            if (matchingRule instanceof DistinguishedNameMatchingRule) {
                final String[] originalValues = a.getValues();
                final String[] scrambledValues = new String[originalValues.length];
                for (int i = 0; i < originalValues.length; ++i) {
                    try {
                        scrambledValues[i] = this.scrambleDN(new DN(originalValues[i])).toString();
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        scrambledValues[i] = this.scrambleString(originalValues[i]);
                    }
                }
                return new Attribute(a.getName(), this.schema, scrambledValues);
            }
            if (matchingRule instanceof GeneralizedTimeMatchingRule) {
                final String[] originalValues = a.getValues();
                final String[] scrambledValues = new String[originalValues.length];
                for (int i = 0; i < originalValues.length; ++i) {
                    scrambledValues[i] = this.scrambleGeneralizedTime(originalValues[i]);
                }
                return new Attribute(a.getName(), this.schema, scrambledValues);
            }
            if (matchingRule instanceof IntegerMatchingRule || matchingRule instanceof NumericStringMatchingRule || matchingRule instanceof TelephoneNumberMatchingRule) {
                final String[] originalValues = a.getValues();
                final String[] scrambledValues = new String[originalValues.length];
                for (int i = 0; i < originalValues.length; ++i) {
                    scrambledValues[i] = this.scrambleNumericValue(originalValues[i]);
                }
                return new Attribute(a.getName(), this.schema, scrambledValues);
            }
            if (matchingRule instanceof OctetStringMatchingRule) {
                final byte[][] originalValues2 = a.getValueByteArrays();
                final byte[][] scrambledValues2 = new byte[originalValues2.length][];
                for (int i = 0; i < originalValues2.length; ++i) {
                    if (baseName.equals("userpassword") || baseName.equals("2.5.4.35")) {
                        scrambledValues2[i] = StaticUtils.getBytes(this.scrambleEncodedPassword(StaticUtils.toUTF8String(originalValues2[i])));
                    }
                    else {
                        scrambledValues2[i] = this.scrambleBinaryValue(originalValues2[i]);
                    }
                }
                return new Attribute(a.getName(), this.schema, scrambledValues2);
            }
            final String[] originalValues = a.getValues();
            final String[] scrambledValues = new String[originalValues.length];
            for (int i = 0; i < originalValues.length; ++i) {
                if (baseName.equals("userpassword") || baseName.equals("2.5.4.35") || baseName.equals("authpassword") || baseName.equals("1.3.6.1.4.1.4203.1.3.4")) {
                    scrambledValues[i] = this.scrambleEncodedPassword(originalValues[i]);
                }
                else if (originalValues[i].startsWith("{") && originalValues[i].endsWith("}")) {
                    scrambledValues[i] = this.scrambleJSONObject(originalValues[i]);
                }
                else {
                    scrambledValues[i] = this.scrambleString(originalValues[i]);
                }
            }
            return new Attribute(a.getName(), this.schema, scrambledValues);
        }
    }
    
    public String scrambleGeneralizedTime(final String s) {
        if (s == null) {
            return null;
        }
        final Random random = this.getRandom(s);
        long decodedTime;
        try {
            decodedTime = StaticUtils.decodeGeneralizedTime(s).getTime();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return this.scrambleString(s);
        }
        long timeSpan = Math.abs(this.createTime - decodedTime);
        if (timeSpan < 86400000L) {
            timeSpan += 86400000L;
        }
        timeSpan *= 2L;
        final long randomLong = random.nextLong() & Long.MAX_VALUE;
        final long randomOffset = randomLong % timeSpan;
        long randomTime;
        if (decodedTime > this.createTime) {
            randomTime = this.createTime + randomOffset;
        }
        else {
            randomTime = this.createTime - randomOffset;
        }
        final String generalizedTime = StaticUtils.encodeGeneralizedTime(randomTime);
        boolean stillInGeneralizedTime = true;
        final StringBuilder scrambledValue = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); ++i) {
            final char originalCharacter = s.charAt(i);
            if (stillInGeneralizedTime) {
                if (i < generalizedTime.length() && originalCharacter >= '0' && originalCharacter <= '9') {
                    final char generalizedTimeCharacter = generalizedTime.charAt(i);
                    if (generalizedTimeCharacter >= '0' && generalizedTimeCharacter <= '9') {
                        scrambledValue.append(generalizedTimeCharacter);
                    }
                    else {
                        scrambledValue.append(originalCharacter);
                        if (generalizedTimeCharacter != '.') {
                            stillInGeneralizedTime = false;
                        }
                    }
                }
                else {
                    scrambledValue.append(originalCharacter);
                    if (originalCharacter != '.') {
                        stillInGeneralizedTime = false;
                    }
                }
            }
            else {
                scrambledValue.append(originalCharacter);
            }
        }
        return scrambledValue.toString();
    }
    
    public String scrambleNumericValue(final String s) {
        if (s == null) {
            return null;
        }
        int firstDigitPos = -1;
        boolean multipleDigits = false;
        final char[] chars = s.toCharArray();
        final Random random = this.getRandom(s);
        final StringBuilder scrambledValue = new StringBuilder(s.length());
        for (int i = 0; i < chars.length; ++i) {
            final char c = chars[i];
            if (c >= '0' && c <= '9') {
                scrambledValue.append(random.nextInt(10));
                if (firstDigitPos < 0) {
                    firstDigitPos = i;
                }
                else {
                    multipleDigits = true;
                }
            }
            else {
                scrambledValue.append(c);
            }
        }
        if (firstDigitPos < 0) {
            return this.scrambleString(s);
        }
        if (multipleDigits && scrambledValue.charAt(firstDigitPos) == '0') {
            scrambledValue.setCharAt(firstDigitPos, (char)(random.nextInt(9) + 49));
        }
        return scrambledValue.toString();
    }
    
    public byte[] scrambleBinaryValue(final byte[] value) {
        if (value == null) {
            return null;
        }
        final Random random = this.getRandom(value);
        final byte[] scrambledValue = new byte[value.length];
        for (int i = 0; i < value.length; ++i) {
            final byte b = value[i];
            if (b >= 97 && b <= 122) {
                scrambledValue[i] = (byte)randomCharacter(ScrambleAttributeTransformation.LOWERCASE_ASCII_LETTERS, random);
            }
            else if (b >= 65 && b <= 90) {
                scrambledValue[i] = (byte)randomCharacter(ScrambleAttributeTransformation.UPPERCASE_ASCII_LETTERS, random);
            }
            else if (b >= 48 && b <= 57) {
                scrambledValue[i] = (byte)randomCharacter(ScrambleAttributeTransformation.ASCII_DIGITS, random);
            }
            else if (b >= 32 && b <= 126) {
                scrambledValue[i] = (byte)randomCharacter(ScrambleAttributeTransformation.ASCII_SYMBOLS, random);
            }
            else if ((b & 0x80) == 0x0) {
                scrambledValue[i] = (byte)(random.nextInt(95) + 32);
            }
            else {
                scrambledValue[i] = (byte)((random.nextInt() & 0xFF) | 0x80);
            }
        }
        return scrambledValue;
    }
    
    public String scrambleEncodedPassword(final String s) {
        if (s == null) {
            return null;
        }
        final int closeBracePos = s.indexOf(125);
        if (s.startsWith("{") && closeBracePos > 0 && closeBracePos < s.length() - 1) {
            return s.substring(0, closeBracePos + 1) + this.scrambleString(s.substring(closeBracePos + 1));
        }
        final int firstDollarPos = s.indexOf(36);
        if (firstDollarPos > 0) {
            final int secondDollarPos = s.indexOf(36, firstDollarPos + 1);
            if (secondDollarPos > 0) {
                return s.substring(0, firstDollarPos + 1) + this.scrambleString(s.substring(firstDollarPos + 1));
            }
        }
        return this.scrambleString(s);
    }
    
    public String scrambleJSONObject(final String s) {
        if (s == null) {
            return null;
        }
        JSONObject o;
        try {
            o = new JSONObject(s);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return this.scrambleString(s);
        }
        final boolean scrambleAllFields = this.jsonFields.isEmpty();
        final Map<String, JSONValue> originalFields = o.getFields();
        final LinkedHashMap<String, JSONValue> scrambledFields = new LinkedHashMap<String, JSONValue>(StaticUtils.computeMapCapacity(originalFields.size()));
        for (final Map.Entry<String, JSONValue> e2 : originalFields.entrySet()) {
            final String fieldName = e2.getKey();
            final JSONValue originalValue = e2.getValue();
            JSONValue scrambledValue;
            if (scrambleAllFields || this.jsonFields.contains(StaticUtils.toLowerCase(fieldName))) {
                scrambledValue = this.scrambleJSONValue(originalValue, true);
            }
            else if (originalValue instanceof JSONArray) {
                scrambledValue = this.scrambleObjectsInArray((JSONArray)originalValue);
            }
            else if (originalValue instanceof JSONObject) {
                scrambledValue = this.scrambleJSONValue(originalValue, false);
            }
            else {
                scrambledValue = originalValue;
            }
            scrambledFields.put(fieldName, scrambledValue);
        }
        return new JSONObject(scrambledFields).toString();
    }
    
    private JSONValue scrambleJSONValue(final JSONValue v, final boolean scrambleAllFields) {
        if (v instanceof JSONArray) {
            final JSONArray a = (JSONArray)v;
            final List<JSONValue> originalValues = a.getValues();
            final ArrayList<JSONValue> scrambledValues = new ArrayList<JSONValue>(originalValues.size());
            for (final JSONValue arrayValue : originalValues) {
                scrambledValues.add(this.scrambleJSONValue(arrayValue, true));
            }
            return new JSONArray(scrambledValues);
        }
        if (v instanceof JSONBoolean) {
            return new JSONBoolean(ThreadLocalRandom.get().nextBoolean());
        }
        if (v instanceof JSONNumber) {
            try {
                return new JSONNumber(this.scrambleNumericValue(v.toString()));
            }
            catch (final Exception e) {
                Debug.debugException(e);
                return v;
            }
        }
        if (v instanceof JSONObject) {
            final JSONObject o = (JSONObject)v;
            final Map<String, JSONValue> originalFields = o.getFields();
            final LinkedHashMap<String, JSONValue> scrambledFields = new LinkedHashMap<String, JSONValue>(StaticUtils.computeMapCapacity(originalFields.size()));
            for (final Map.Entry<String, JSONValue> e2 : originalFields.entrySet()) {
                final String fieldName = e2.getKey();
                final JSONValue originalValue = e2.getValue();
                JSONValue scrambledValue;
                if (scrambleAllFields || this.jsonFields.contains(StaticUtils.toLowerCase(fieldName))) {
                    scrambledValue = this.scrambleJSONValue(originalValue, scrambleAllFields);
                }
                else if (originalValue instanceof JSONArray) {
                    scrambledValue = this.scrambleObjectsInArray((JSONArray)originalValue);
                }
                else if (originalValue instanceof JSONObject) {
                    scrambledValue = this.scrambleJSONValue(originalValue, false);
                }
                else {
                    scrambledValue = originalValue;
                }
                scrambledFields.put(fieldName, scrambledValue);
            }
            return new JSONObject(scrambledFields);
        }
        if (v instanceof JSONString) {
            final JSONString s = (JSONString)v;
            return new JSONString(this.scrambleString(s.stringValue()));
        }
        return v;
    }
    
    private JSONArray scrambleObjectsInArray(final JSONArray a) {
        final List<JSONValue> originalValues = a.getValues();
        final ArrayList<JSONValue> scrambledValues = new ArrayList<JSONValue>(originalValues.size());
        for (final JSONValue arrayValue : originalValues) {
            if (arrayValue instanceof JSONArray) {
                scrambledValues.add(this.scrambleObjectsInArray((JSONArray)arrayValue));
            }
            else if (arrayValue instanceof JSONObject) {
                scrambledValues.add(this.scrambleJSONValue(arrayValue, false));
            }
            else {
                scrambledValues.add(arrayValue);
            }
        }
        return new JSONArray(scrambledValues);
    }
    
    public String scrambleString(final String s) {
        if (s == null) {
            return null;
        }
        final Random random = this.getRandom(s);
        final StringBuilder scrambledString = new StringBuilder(s.length());
        for (final char c : s.toCharArray()) {
            if (c >= 'a' && c <= 'z') {
                scrambledString.append(randomCharacter(ScrambleAttributeTransformation.LOWERCASE_ASCII_LETTERS, random));
            }
            else if (c >= 'A' && c <= 'Z') {
                scrambledString.append(randomCharacter(ScrambleAttributeTransformation.UPPERCASE_ASCII_LETTERS, random));
            }
            else if (c >= '0' && c <= '9') {
                scrambledString.append(randomCharacter(ScrambleAttributeTransformation.ASCII_DIGITS, random));
            }
            else {
                scrambledString.append(c);
            }
        }
        return scrambledString.toString();
    }
    
    private static char randomCharacter(final char[] set, final Random r) {
        return set[r.nextInt(set.length)];
    }
    
    private Random getRandom(final String value) {
        Random r = this.randoms.get();
        if (r == null) {
            r = new Random(this.randomSeed + value.hashCode());
            this.randoms.set(r);
        }
        else {
            r.setSeed(this.randomSeed + value.hashCode());
        }
        return r;
    }
    
    private Random getRandom(final byte[] value) {
        Random r = this.randoms.get();
        if (r == null) {
            r = new Random(this.randomSeed + Arrays.hashCode(value));
            this.randoms.set(r);
        }
        else {
            r.setSeed(this.randomSeed + Arrays.hashCode(value));
        }
        return r;
    }
    
    @Override
    public Entry translate(final Entry original, final long firstLineNumber) {
        return this.transformEntry(original);
    }
    
    @Override
    public LDIFChangeRecord translate(final LDIFChangeRecord original, final long firstLineNumber) {
        return this.transformChangeRecord(original);
    }
    
    @Override
    public Entry translateEntryToWrite(final Entry original) {
        return this.transformEntry(original);
    }
    
    @Override
    public LDIFChangeRecord translateChangeRecordToWrite(final LDIFChangeRecord original) {
        return this.transformChangeRecord(original);
    }
    
    static {
        ASCII_DIGITS = "0123456789".toCharArray();
        ASCII_SYMBOLS = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~".toCharArray();
        LOWERCASE_ASCII_LETTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        UPPERCASE_ASCII_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    }
}
