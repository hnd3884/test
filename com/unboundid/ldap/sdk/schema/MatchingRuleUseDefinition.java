package com.unboundid.ldap.sdk.schema;

import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Validator;
import java.util.Map;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class MatchingRuleUseDefinition extends SchemaElement
{
    private static final long serialVersionUID = 2366143311976256897L;
    private final boolean isObsolete;
    private final Map<String, String[]> extensions;
    private final String description;
    private final String matchingRuleUseString;
    private final String oid;
    private final String[] applicableTypes;
    private final String[] names;
    
    public MatchingRuleUseDefinition(final String s) throws LDAPException {
        Validator.ensureNotNull(s);
        this.matchingRuleUseString = s.trim();
        final int length = this.matchingRuleUseString.length();
        if (length == 0) {
            throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_MRU_DECODE_EMPTY.get());
        }
        if (this.matchingRuleUseString.charAt(0) != '(') {
            throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_MRU_DECODE_NO_OPENING_PAREN.get(this.matchingRuleUseString));
        }
        int pos = SchemaElement.skipSpaces(this.matchingRuleUseString, 1, length);
        StringBuilder buffer = new StringBuilder();
        pos = SchemaElement.readOID(this.matchingRuleUseString, pos, length, buffer);
        this.oid = buffer.toString();
        final ArrayList<String> nameList = new ArrayList<String>(1);
        final ArrayList<String> typeList = new ArrayList<String>(1);
        String descr = null;
        Boolean obsolete = null;
        final Map<String, String[]> exts = new LinkedHashMap<String, String[]>(StaticUtils.computeMapCapacity(5));
        while (true) {
            int tokenStartPos;
            for (pos = (tokenStartPos = SchemaElement.skipSpaces(this.matchingRuleUseString, pos, length)); pos < length && this.matchingRuleUseString.charAt(pos) != ' '; ++pos) {}
            String token = this.matchingRuleUseString.substring(tokenStartPos, pos);
            if (token.length() > 1 && token.endsWith(")")) {
                token = token.substring(0, token.length() - 1);
                --pos;
            }
            final String lowerToken = StaticUtils.toLowerCase(token);
            if (lowerToken.equals(")")) {
                if (pos < length) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_MRU_DECODE_CLOSE_NOT_AT_END.get(this.matchingRuleUseString));
                }
                this.description = descr;
                nameList.toArray(this.names = new String[nameList.size()]);
                if (typeList.isEmpty()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_MRU_DECODE_NO_APPLIES.get(this.matchingRuleUseString));
                }
                typeList.toArray(this.applicableTypes = new String[typeList.size()]);
                this.isObsolete = (obsolete != null);
                this.extensions = Collections.unmodifiableMap((Map<? extends String, ? extends String[]>)exts);
            }
            else if (lowerToken.equals("name")) {
                if (!nameList.isEmpty()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_MRU_DECODE_MULTIPLE_ELEMENTS.get(this.matchingRuleUseString, "NAME"));
                }
                pos = SchemaElement.skipSpaces(this.matchingRuleUseString, pos, length);
                pos = SchemaElement.readQDStrings(this.matchingRuleUseString, pos, length, nameList);
            }
            else if (lowerToken.equals("desc")) {
                if (descr != null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_MRU_DECODE_MULTIPLE_ELEMENTS.get(this.matchingRuleUseString, "DESC"));
                }
                pos = SchemaElement.skipSpaces(this.matchingRuleUseString, pos, length);
                buffer = new StringBuilder();
                pos = SchemaElement.readQDString(this.matchingRuleUseString, pos, length, buffer);
                descr = buffer.toString();
            }
            else if (lowerToken.equals("obsolete")) {
                if (obsolete != null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_MRU_DECODE_MULTIPLE_ELEMENTS.get(this.matchingRuleUseString, "OBSOLETE"));
                }
                obsolete = true;
            }
            else if (lowerToken.equals("applies")) {
                if (!typeList.isEmpty()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_MRU_DECODE_MULTIPLE_ELEMENTS.get(this.matchingRuleUseString, "APPLIES"));
                }
                pos = SchemaElement.skipSpaces(this.matchingRuleUseString, pos, length);
                pos = SchemaElement.readOIDs(this.matchingRuleUseString, pos, length, typeList);
            }
            else {
                if (!lowerToken.startsWith("x-")) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_MRU_DECODE_UNEXPECTED_TOKEN.get(this.matchingRuleUseString, token));
                }
                pos = SchemaElement.skipSpaces(this.matchingRuleUseString, pos, length);
                final ArrayList<String> valueList = new ArrayList<String>(5);
                pos = SchemaElement.readQDStrings(this.matchingRuleUseString, pos, length, valueList);
                final String[] values = new String[valueList.size()];
                valueList.toArray(values);
                if (exts.containsKey(token)) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_MRU_DECODE_DUP_EXT.get(this.matchingRuleUseString, token));
                }
                exts.put(token, values);
            }
        }
    }
    
    public MatchingRuleUseDefinition(final String oid, final String name, final String description, final String[] applicableTypes, final Map<String, String[]> extensions) {
        this(oid, (String[])((name == null) ? null : new String[] { name }), description, false, applicableTypes, extensions);
    }
    
    public MatchingRuleUseDefinition(final String oid, final String name, final String description, final Collection<String> applicableTypes, final Map<String, String[]> extensions) {
        this(oid, (String[])((name == null) ? null : new String[] { name }), description, false, SchemaElement.toArray(applicableTypes), extensions);
    }
    
    public MatchingRuleUseDefinition(final String oid, final String[] names, final String description, final boolean isObsolete, final String[] applicableTypes, final Map<String, String[]> extensions) {
        Validator.ensureNotNull(oid, applicableTypes);
        Validator.ensureFalse(applicableTypes.length == 0);
        this.oid = oid;
        this.description = description;
        this.isObsolete = isObsolete;
        this.applicableTypes = applicableTypes;
        if (names == null) {
            this.names = StaticUtils.NO_STRINGS;
        }
        else {
            this.names = names;
        }
        if (extensions == null) {
            this.extensions = Collections.emptyMap();
        }
        else {
            this.extensions = Collections.unmodifiableMap((Map<? extends String, ? extends String[]>)extensions);
        }
        final StringBuilder buffer = new StringBuilder();
        this.createDefinitionString(buffer);
        this.matchingRuleUseString = buffer.toString();
    }
    
    private void createDefinitionString(final StringBuilder buffer) {
        buffer.append("( ");
        buffer.append(this.oid);
        if (this.names.length == 1) {
            buffer.append(" NAME '");
            buffer.append(this.names[0]);
            buffer.append('\'');
        }
        else if (this.names.length > 1) {
            buffer.append(" NAME (");
            for (final String name : this.names) {
                buffer.append(" '");
                buffer.append(name);
                buffer.append('\'');
            }
            buffer.append(" )");
        }
        if (this.description != null) {
            buffer.append(" DESC '");
            SchemaElement.encodeValue(this.description, buffer);
            buffer.append('\'');
        }
        if (this.isObsolete) {
            buffer.append(" OBSOLETE");
        }
        if (this.applicableTypes.length == 1) {
            buffer.append(" APPLIES ");
            buffer.append(this.applicableTypes[0]);
        }
        else if (this.applicableTypes.length > 1) {
            buffer.append(" APPLIES (");
            for (int i = 0; i < this.applicableTypes.length; ++i) {
                if (i > 0) {
                    buffer.append(" $");
                }
                buffer.append(' ');
                buffer.append(this.applicableTypes[i]);
            }
            buffer.append(" )");
        }
        for (final Map.Entry<String, String[]> e : this.extensions.entrySet()) {
            final String name2 = e.getKey();
            final String[] values = e.getValue();
            if (values.length == 1) {
                buffer.append(' ');
                buffer.append(name2);
                buffer.append(" '");
                SchemaElement.encodeValue(values[0], buffer);
                buffer.append('\'');
            }
            else {
                buffer.append(' ');
                buffer.append(name2);
                buffer.append(" (");
                for (final String value : values) {
                    buffer.append(" '");
                    SchemaElement.encodeValue(value, buffer);
                    buffer.append('\'');
                }
                buffer.append(" )");
            }
        }
        buffer.append(" )");
    }
    
    public String getOID() {
        return this.oid;
    }
    
    public String[] getNames() {
        return this.names;
    }
    
    public String getNameOrOID() {
        if (this.names.length == 0) {
            return this.oid;
        }
        return this.names[0];
    }
    
    public boolean hasNameOrOID(final String s) {
        for (final String name : this.names) {
            if (s.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return s.equalsIgnoreCase(this.oid);
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public boolean isObsolete() {
        return this.isObsolete;
    }
    
    public String[] getApplicableAttributeTypes() {
        return this.applicableTypes;
    }
    
    public Map<String, String[]> getExtensions() {
        return this.extensions;
    }
    
    @Override
    public int hashCode() {
        return this.oid.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof MatchingRuleUseDefinition)) {
            return false;
        }
        final MatchingRuleUseDefinition d = (MatchingRuleUseDefinition)o;
        return this.oid.equals(d.oid) && StaticUtils.stringsEqualIgnoreCaseOrderIndependent(this.names, d.names) && StaticUtils.stringsEqualIgnoreCaseOrderIndependent(this.applicableTypes, d.applicableTypes) && StaticUtils.bothNullOrEqualIgnoreCase(this.description, d.description) && this.isObsolete == d.isObsolete && SchemaElement.extensionsEqual(this.extensions, d.extensions);
    }
    
    @Override
    public String toString() {
        return this.matchingRuleUseString;
    }
}
