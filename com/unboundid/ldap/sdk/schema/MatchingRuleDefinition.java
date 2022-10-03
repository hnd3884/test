package com.unboundid.ldap.sdk.schema;

import java.util.Iterator;
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
public final class MatchingRuleDefinition extends SchemaElement
{
    private static final long serialVersionUID = 8214648655449007967L;
    private final boolean isObsolete;
    private final Map<String, String[]> extensions;
    private final String description;
    private final String matchingRuleString;
    private final String oid;
    private final String syntaxOID;
    private final String[] names;
    
    public MatchingRuleDefinition(final String s) throws LDAPException {
        Validator.ensureNotNull(s);
        this.matchingRuleString = s.trim();
        final int length = this.matchingRuleString.length();
        if (length == 0) {
            throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_MR_DECODE_EMPTY.get());
        }
        if (this.matchingRuleString.charAt(0) != '(') {
            throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_MR_DECODE_NO_OPENING_PAREN.get(this.matchingRuleString));
        }
        int pos = SchemaElement.skipSpaces(this.matchingRuleString, 1, length);
        StringBuilder buffer = new StringBuilder();
        pos = SchemaElement.readOID(this.matchingRuleString, pos, length, buffer);
        this.oid = buffer.toString();
        final ArrayList<String> nameList = new ArrayList<String>(1);
        String descr = null;
        Boolean obsolete = null;
        String synOID = null;
        final Map<String, String[]> exts = new LinkedHashMap<String, String[]>(StaticUtils.computeMapCapacity(5));
        while (true) {
            int tokenStartPos;
            for (pos = (tokenStartPos = SchemaElement.skipSpaces(this.matchingRuleString, pos, length)); pos < length && this.matchingRuleString.charAt(pos) != ' '; ++pos) {}
            String token = this.matchingRuleString.substring(tokenStartPos, pos);
            if (token.length() > 1 && token.endsWith(")")) {
                token = token.substring(0, token.length() - 1);
                --pos;
            }
            final String lowerToken = StaticUtils.toLowerCase(token);
            if (lowerToken.equals(")")) {
                if (pos < length) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_MR_DECODE_CLOSE_NOT_AT_END.get(this.matchingRuleString));
                }
                this.description = descr;
                this.syntaxOID = synOID;
                if (this.syntaxOID == null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_MR_DECODE_NO_SYNTAX.get(this.matchingRuleString));
                }
                nameList.toArray(this.names = new String[nameList.size()]);
                this.isObsolete = (obsolete != null);
                this.extensions = Collections.unmodifiableMap((Map<? extends String, ? extends String[]>)exts);
            }
            else if (lowerToken.equals("name")) {
                if (!nameList.isEmpty()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_MR_DECODE_MULTIPLE_ELEMENTS.get(this.matchingRuleString, "NAME"));
                }
                pos = SchemaElement.skipSpaces(this.matchingRuleString, pos, length);
                pos = SchemaElement.readQDStrings(this.matchingRuleString, pos, length, nameList);
            }
            else if (lowerToken.equals("desc")) {
                if (descr != null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_MR_DECODE_MULTIPLE_ELEMENTS.get(this.matchingRuleString, "DESC"));
                }
                pos = SchemaElement.skipSpaces(this.matchingRuleString, pos, length);
                buffer = new StringBuilder();
                pos = SchemaElement.readQDString(this.matchingRuleString, pos, length, buffer);
                descr = buffer.toString();
            }
            else if (lowerToken.equals("obsolete")) {
                if (obsolete != null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_MR_DECODE_MULTIPLE_ELEMENTS.get(this.matchingRuleString, "OBSOLETE"));
                }
                obsolete = true;
            }
            else if (lowerToken.equals("syntax")) {
                if (synOID != null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_MR_DECODE_MULTIPLE_ELEMENTS.get(this.matchingRuleString, "SYNTAX"));
                }
                pos = SchemaElement.skipSpaces(this.matchingRuleString, pos, length);
                buffer = new StringBuilder();
                pos = SchemaElement.readOID(this.matchingRuleString, pos, length, buffer);
                synOID = buffer.toString();
            }
            else {
                if (!lowerToken.startsWith("x-")) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_MR_DECODE_UNEXPECTED_TOKEN.get(this.matchingRuleString, token));
                }
                pos = SchemaElement.skipSpaces(this.matchingRuleString, pos, length);
                final ArrayList<String> valueList = new ArrayList<String>(5);
                pos = SchemaElement.readQDStrings(this.matchingRuleString, pos, length, valueList);
                final String[] values = new String[valueList.size()];
                valueList.toArray(values);
                if (exts.containsKey(token)) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_MR_DECODE_DUP_EXT.get(this.matchingRuleString, token));
                }
                exts.put(token, values);
            }
        }
    }
    
    public MatchingRuleDefinition(final String oid, final String name, final String description, final String syntaxOID, final Map<String, String[]> extensions) {
        this(oid, (String[])((name == null) ? null : new String[] { name }), description, false, syntaxOID, extensions);
    }
    
    public MatchingRuleDefinition(final String oid, final String[] names, final String description, final boolean isObsolete, final String syntaxOID, final Map<String, String[]> extensions) {
        Validator.ensureNotNull(oid, syntaxOID);
        this.oid = oid;
        this.description = description;
        this.isObsolete = isObsolete;
        this.syntaxOID = syntaxOID;
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
        this.matchingRuleString = buffer.toString();
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
        buffer.append(" SYNTAX ");
        buffer.append(this.syntaxOID);
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
    
    public String getSyntaxOID() {
        return this.syntaxOID;
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
        if (!(o instanceof MatchingRuleDefinition)) {
            return false;
        }
        final MatchingRuleDefinition d = (MatchingRuleDefinition)o;
        return this.oid.equals(d.oid) && this.syntaxOID.equals(d.syntaxOID) && StaticUtils.stringsEqualIgnoreCaseOrderIndependent(this.names, d.names) && StaticUtils.bothNullOrEqualIgnoreCase(this.description, d.description) && this.isObsolete == d.isObsolete && SchemaElement.extensionsEqual(this.extensions, d.extensions);
    }
    
    @Override
    public String toString() {
        return this.matchingRuleString;
    }
}
