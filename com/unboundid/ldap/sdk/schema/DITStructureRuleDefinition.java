package com.unboundid.ldap.sdk.schema;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Collections;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.ArrayList;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Validator;
import java.util.Map;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DITStructureRuleDefinition extends SchemaElement
{
    private static final int[] NO_INTS;
    private static final long serialVersionUID = -3233223742542121140L;
    private final boolean isObsolete;
    private final int ruleID;
    private final int[] superiorRuleIDs;
    private final Map<String, String[]> extensions;
    private final String description;
    private final String ditStructureRuleString;
    private final String nameFormID;
    private final String[] names;
    
    public DITStructureRuleDefinition(final String s) throws LDAPException {
        Validator.ensureNotNull(s);
        this.ditStructureRuleString = s.trim();
        final int length = this.ditStructureRuleString.length();
        if (length == 0) {
            throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_DSR_DECODE_EMPTY.get());
        }
        if (this.ditStructureRuleString.charAt(0) != '(') {
            throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_DSR_DECODE_NO_OPENING_PAREN.get(this.ditStructureRuleString));
        }
        int pos = SchemaElement.skipSpaces(this.ditStructureRuleString, 1, length);
        StringBuilder buffer = new StringBuilder();
        pos = SchemaElement.readOID(this.ditStructureRuleString, pos, length, buffer);
        final String ruleIDStr = buffer.toString();
        try {
            this.ruleID = Integer.parseInt(ruleIDStr);
        }
        catch (final NumberFormatException nfe) {
            Debug.debugException(nfe);
            throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_DSR_DECODE_RULE_ID_NOT_INT.get(this.ditStructureRuleString), nfe);
        }
        final ArrayList<Integer> supList = new ArrayList<Integer>(1);
        final ArrayList<String> nameList = new ArrayList<String>(1);
        final Map<String, String[]> exts = new LinkedHashMap<String, String[]>(StaticUtils.computeMapCapacity(5));
        Boolean obsolete = null;
        String descr = null;
        String nfID = null;
        while (true) {
            int tokenStartPos;
            for (pos = (tokenStartPos = SchemaElement.skipSpaces(this.ditStructureRuleString, pos, length)); pos < length && this.ditStructureRuleString.charAt(pos) != ' '; ++pos) {}
            String token = this.ditStructureRuleString.substring(tokenStartPos, pos);
            if (token.length() > 1 && token.endsWith(")")) {
                token = token.substring(0, token.length() - 1);
                --pos;
            }
            final String lowerToken = StaticUtils.toLowerCase(token);
            if (lowerToken.equals(")")) {
                if (pos < length) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_DSR_DECODE_CLOSE_NOT_AT_END.get(this.ditStructureRuleString));
                }
                this.description = descr;
                this.nameFormID = nfID;
                if (this.nameFormID == null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_DSR_DECODE_NO_FORM.get(this.ditStructureRuleString));
                }
                nameList.toArray(this.names = new String[nameList.size()]);
                this.superiorRuleIDs = new int[supList.size()];
                for (int i = 0; i < this.superiorRuleIDs.length; ++i) {
                    this.superiorRuleIDs[i] = supList.get(i);
                }
                this.isObsolete = (obsolete != null);
                this.extensions = Collections.unmodifiableMap((Map<? extends String, ? extends String[]>)exts);
            }
            else if (lowerToken.equals("name")) {
                if (!nameList.isEmpty()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_DSR_DECODE_MULTIPLE_ELEMENTS.get(this.ditStructureRuleString, "NAME"));
                }
                pos = SchemaElement.skipSpaces(this.ditStructureRuleString, pos, length);
                pos = SchemaElement.readQDStrings(this.ditStructureRuleString, pos, length, nameList);
            }
            else if (lowerToken.equals("desc")) {
                if (descr != null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_DSR_DECODE_MULTIPLE_ELEMENTS.get(this.ditStructureRuleString, "DESC"));
                }
                pos = SchemaElement.skipSpaces(this.ditStructureRuleString, pos, length);
                buffer = new StringBuilder();
                pos = SchemaElement.readQDString(this.ditStructureRuleString, pos, length, buffer);
                descr = buffer.toString();
            }
            else if (lowerToken.equals("obsolete")) {
                if (obsolete != null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_DSR_DECODE_MULTIPLE_ELEMENTS.get(this.ditStructureRuleString, "OBSOLETE"));
                }
                obsolete = true;
            }
            else if (lowerToken.equals("form")) {
                if (nfID != null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_DSR_DECODE_MULTIPLE_ELEMENTS.get(this.ditStructureRuleString, "FORM"));
                }
                pos = SchemaElement.skipSpaces(this.ditStructureRuleString, pos, length);
                buffer = new StringBuilder();
                pos = SchemaElement.readOID(this.ditStructureRuleString, pos, length, buffer);
                nfID = buffer.toString();
            }
            else if (lowerToken.equals("sup")) {
                if (!supList.isEmpty()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_DSR_DECODE_MULTIPLE_ELEMENTS.get(this.ditStructureRuleString, "SUP"));
                }
                final ArrayList<String> supStrs = new ArrayList<String>(1);
                pos = SchemaElement.skipSpaces(this.ditStructureRuleString, pos, length);
                pos = SchemaElement.readOIDs(this.ditStructureRuleString, pos, length, supStrs);
                supList.ensureCapacity(supStrs.size());
                for (final String supStr : supStrs) {
                    try {
                        supList.add(Integer.parseInt(supStr));
                    }
                    catch (final NumberFormatException nfe2) {
                        Debug.debugException(nfe2);
                        throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_DSR_DECODE_SUP_ID_NOT_INT.get(this.ditStructureRuleString), nfe2);
                    }
                }
            }
            else {
                if (!lowerToken.startsWith("x-")) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_DSR_DECODE_UNEXPECTED_TOKEN.get(this.ditStructureRuleString, token));
                }
                pos = SchemaElement.skipSpaces(this.ditStructureRuleString, pos, length);
                final ArrayList<String> valueList = new ArrayList<String>(5);
                pos = SchemaElement.readQDStrings(this.ditStructureRuleString, pos, length, valueList);
                final String[] values = new String[valueList.size()];
                valueList.toArray(values);
                if (exts.containsKey(token)) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_DSR_DECODE_DUP_EXT.get(this.ditStructureRuleString, token));
                }
                exts.put(token, values);
            }
        }
    }
    
    public DITStructureRuleDefinition(final int ruleID, final String name, final String description, final String nameFormID, final Integer superiorRuleID, final Map<String, String[]> extensions) {
        this(ruleID, (String[])((name == null) ? null : new String[] { name }), description, false, nameFormID, (int[])((superiorRuleID == null) ? null : new int[] { superiorRuleID }), extensions);
    }
    
    public DITStructureRuleDefinition(final int ruleID, final String[] names, final String description, final boolean isObsolete, final String nameFormID, final int[] superiorRuleIDs, final Map<String, String[]> extensions) {
        Validator.ensureNotNull(nameFormID);
        this.ruleID = ruleID;
        this.description = description;
        this.isObsolete = isObsolete;
        this.nameFormID = nameFormID;
        if (names == null) {
            this.names = StaticUtils.NO_STRINGS;
        }
        else {
            this.names = names;
        }
        if (superiorRuleIDs == null) {
            this.superiorRuleIDs = DITStructureRuleDefinition.NO_INTS;
        }
        else {
            this.superiorRuleIDs = superiorRuleIDs;
        }
        if (extensions == null) {
            this.extensions = Collections.emptyMap();
        }
        else {
            this.extensions = Collections.unmodifiableMap((Map<? extends String, ? extends String[]>)extensions);
        }
        final StringBuilder buffer = new StringBuilder();
        this.createDefinitionString(buffer);
        this.ditStructureRuleString = buffer.toString();
    }
    
    private void createDefinitionString(final StringBuilder buffer) {
        buffer.append("( ");
        buffer.append(this.ruleID);
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
        buffer.append(" FORM ");
        buffer.append(this.nameFormID);
        if (this.superiorRuleIDs.length == 1) {
            buffer.append(" SUP ");
            buffer.append(this.superiorRuleIDs[0]);
        }
        else if (this.superiorRuleIDs.length > 1) {
            buffer.append(" SUP (");
            for (final int supID : this.superiorRuleIDs) {
                buffer.append(" $ ");
                buffer.append(supID);
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
    
    public int getRuleID() {
        return this.ruleID;
    }
    
    public String[] getNames() {
        return this.names;
    }
    
    public String getNameOrRuleID() {
        if (this.names.length == 0) {
            return String.valueOf(this.ruleID);
        }
        return this.names[0];
    }
    
    public boolean hasNameOrRuleID(final String s) {
        for (final String name : this.names) {
            if (s.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return s.equalsIgnoreCase(String.valueOf(this.ruleID));
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public boolean isObsolete() {
        return this.isObsolete;
    }
    
    public String getNameFormID() {
        return this.nameFormID;
    }
    
    public int[] getSuperiorRuleIDs() {
        return this.superiorRuleIDs;
    }
    
    public Map<String, String[]> getExtensions() {
        return this.extensions;
    }
    
    @Override
    public int hashCode() {
        return this.ruleID;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof DITStructureRuleDefinition)) {
            return false;
        }
        final DITStructureRuleDefinition d = (DITStructureRuleDefinition)o;
        if (this.ruleID != d.ruleID || !this.nameFormID.equalsIgnoreCase(d.nameFormID) || !StaticUtils.stringsEqualIgnoreCaseOrderIndependent(this.names, d.names) || this.isObsolete != d.isObsolete || !SchemaElement.extensionsEqual(this.extensions, d.extensions)) {
            return false;
        }
        if (this.superiorRuleIDs.length != d.superiorRuleIDs.length) {
            return false;
        }
        final HashSet<Integer> s1 = new HashSet<Integer>(StaticUtils.computeMapCapacity(this.superiorRuleIDs.length));
        final HashSet<Integer> s2 = new HashSet<Integer>(StaticUtils.computeMapCapacity(this.superiorRuleIDs.length));
        for (final int i : this.superiorRuleIDs) {
            s1.add(i);
        }
        for (final int i : d.superiorRuleIDs) {
            s2.add(i);
        }
        return s1.equals(s2);
    }
    
    @Override
    public String toString() {
        return this.ditStructureRuleString;
    }
    
    static {
        NO_INTS = new int[0];
    }
}
