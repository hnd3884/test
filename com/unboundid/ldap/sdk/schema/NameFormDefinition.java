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
public final class NameFormDefinition extends SchemaElement
{
    private static final long serialVersionUID = -816231530223449984L;
    private final boolean isObsolete;
    private final Map<String, String[]> extensions;
    private final String description;
    private final String nameFormString;
    private final String oid;
    private final String[] names;
    private final String structuralClass;
    private final String[] optionalAttributes;
    private final String[] requiredAttributes;
    
    public NameFormDefinition(final String s) throws LDAPException {
        Validator.ensureNotNull(s);
        this.nameFormString = s.trim();
        final int length = this.nameFormString.length();
        if (length == 0) {
            throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_NF_DECODE_EMPTY.get());
        }
        if (this.nameFormString.charAt(0) != '(') {
            throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_NF_DECODE_NO_OPENING_PAREN.get(this.nameFormString));
        }
        int pos = SchemaElement.skipSpaces(this.nameFormString, 1, length);
        StringBuilder buffer = new StringBuilder();
        pos = SchemaElement.readOID(this.nameFormString, pos, length, buffer);
        this.oid = buffer.toString();
        final ArrayList<String> nameList = new ArrayList<String>(1);
        final ArrayList<String> reqAttrs = new ArrayList<String>(10);
        final ArrayList<String> optAttrs = new ArrayList<String>(10);
        final Map<String, String[]> exts = new LinkedHashMap<String, String[]>(StaticUtils.computeMapCapacity(5));
        Boolean obsolete = null;
        String descr = null;
        String oc = null;
        while (true) {
            int tokenStartPos;
            for (pos = (tokenStartPos = SchemaElement.skipSpaces(this.nameFormString, pos, length)); pos < length && this.nameFormString.charAt(pos) != ' '; ++pos) {}
            String token = this.nameFormString.substring(tokenStartPos, pos);
            if (token.length() > 1 && token.endsWith(")")) {
                token = token.substring(0, token.length() - 1);
                --pos;
            }
            final String lowerToken = StaticUtils.toLowerCase(token);
            if (lowerToken.equals(")")) {
                if (pos < length) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_NF_DECODE_CLOSE_NOT_AT_END.get(this.nameFormString));
                }
                this.description = descr;
                this.structuralClass = oc;
                if (this.structuralClass == null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_NF_DECODE_NO_OC.get(this.nameFormString));
                }
                nameList.toArray(this.names = new String[nameList.size()]);
                reqAttrs.toArray(this.requiredAttributes = new String[reqAttrs.size()]);
                if (reqAttrs.isEmpty()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_NF_DECODE_NO_MUST.get(this.nameFormString));
                }
                optAttrs.toArray(this.optionalAttributes = new String[optAttrs.size()]);
                this.isObsolete = (obsolete != null);
                this.extensions = Collections.unmodifiableMap((Map<? extends String, ? extends String[]>)exts);
            }
            else if (lowerToken.equals("name")) {
                if (!nameList.isEmpty()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_NF_DECODE_MULTIPLE_ELEMENTS.get(this.nameFormString, "NAME"));
                }
                pos = SchemaElement.skipSpaces(this.nameFormString, pos, length);
                pos = SchemaElement.readQDStrings(this.nameFormString, pos, length, nameList);
            }
            else if (lowerToken.equals("desc")) {
                if (descr != null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_NF_DECODE_MULTIPLE_ELEMENTS.get(this.nameFormString, "DESC"));
                }
                pos = SchemaElement.skipSpaces(this.nameFormString, pos, length);
                buffer = new StringBuilder();
                pos = SchemaElement.readQDString(this.nameFormString, pos, length, buffer);
                descr = buffer.toString();
            }
            else if (lowerToken.equals("obsolete")) {
                if (obsolete != null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_NF_DECODE_MULTIPLE_ELEMENTS.get(this.nameFormString, "OBSOLETE"));
                }
                obsolete = true;
            }
            else if (lowerToken.equals("oc")) {
                if (oc != null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_NF_DECODE_MULTIPLE_ELEMENTS.get(this.nameFormString, "OC"));
                }
                pos = SchemaElement.skipSpaces(this.nameFormString, pos, length);
                buffer = new StringBuilder();
                pos = SchemaElement.readOID(this.nameFormString, pos, length, buffer);
                oc = buffer.toString();
            }
            else if (lowerToken.equals("must")) {
                if (!reqAttrs.isEmpty()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_NF_DECODE_MULTIPLE_ELEMENTS.get(this.nameFormString, "MUST"));
                }
                pos = SchemaElement.skipSpaces(this.nameFormString, pos, length);
                pos = SchemaElement.readOIDs(this.nameFormString, pos, length, reqAttrs);
            }
            else if (lowerToken.equals("may")) {
                if (!optAttrs.isEmpty()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_NF_DECODE_MULTIPLE_ELEMENTS.get(this.nameFormString, "MAY"));
                }
                pos = SchemaElement.skipSpaces(this.nameFormString, pos, length);
                pos = SchemaElement.readOIDs(this.nameFormString, pos, length, optAttrs);
            }
            else {
                if (!lowerToken.startsWith("x-")) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_NF_DECODE_UNEXPECTED_TOKEN.get(this.nameFormString, token));
                }
                pos = SchemaElement.skipSpaces(this.nameFormString, pos, length);
                final ArrayList<String> valueList = new ArrayList<String>(5);
                pos = SchemaElement.readQDStrings(this.nameFormString, pos, length, valueList);
                final String[] values = new String[valueList.size()];
                valueList.toArray(values);
                if (exts.containsKey(token)) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_NF_DECODE_DUP_EXT.get(this.nameFormString, token));
                }
                exts.put(token, values);
            }
        }
    }
    
    public NameFormDefinition(final String oid, final String name, final String description, final String structuralClass, final String requiredAttribute, final Map<String, String[]> extensions) {
        this(oid, (String[])((name == null) ? null : new String[] { name }), description, false, structuralClass, new String[] { requiredAttribute }, null, extensions);
    }
    
    public NameFormDefinition(final String oid, final String[] names, final String description, final boolean isObsolete, final String structuralClass, final String[] requiredAttributes, final String[] optionalAttributes, final Map<String, String[]> extensions) {
        Validator.ensureNotNull(oid, structuralClass, requiredAttributes);
        Validator.ensureFalse(requiredAttributes.length == 0);
        this.oid = oid;
        this.isObsolete = isObsolete;
        this.description = description;
        this.structuralClass = structuralClass;
        this.requiredAttributes = requiredAttributes;
        if (names == null) {
            this.names = StaticUtils.NO_STRINGS;
        }
        else {
            this.names = names;
        }
        if (optionalAttributes == null) {
            this.optionalAttributes = StaticUtils.NO_STRINGS;
        }
        else {
            this.optionalAttributes = optionalAttributes;
        }
        if (extensions == null) {
            this.extensions = Collections.emptyMap();
        }
        else {
            this.extensions = Collections.unmodifiableMap((Map<? extends String, ? extends String[]>)extensions);
        }
        final StringBuilder buffer = new StringBuilder();
        this.createDefinitionString(buffer);
        this.nameFormString = buffer.toString();
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
        buffer.append(" OC ");
        buffer.append(this.structuralClass);
        if (this.requiredAttributes.length == 1) {
            buffer.append(" MUST ");
            buffer.append(this.requiredAttributes[0]);
        }
        else if (this.requiredAttributes.length > 1) {
            buffer.append(" MUST (");
            for (int i = 0; i < this.requiredAttributes.length; ++i) {
                if (i > 0) {
                    buffer.append(" $ ");
                }
                else {
                    buffer.append(' ');
                }
                buffer.append(this.requiredAttributes[i]);
            }
            buffer.append(" )");
        }
        if (this.optionalAttributes.length == 1) {
            buffer.append(" MAY ");
            buffer.append(this.optionalAttributes[0]);
        }
        else if (this.optionalAttributes.length > 1) {
            buffer.append(" MAY (");
            for (int i = 0; i < this.optionalAttributes.length; ++i) {
                if (i > 0) {
                    buffer.append(" $ ");
                }
                else {
                    buffer.append(' ');
                }
                buffer.append(this.optionalAttributes[i]);
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
    
    public String getStructuralClass() {
        return this.structuralClass;
    }
    
    public String[] getRequiredAttributes() {
        return this.requiredAttributes;
    }
    
    public String[] getOptionalAttributes() {
        return this.optionalAttributes;
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
        if (!(o instanceof NameFormDefinition)) {
            return false;
        }
        final NameFormDefinition d = (NameFormDefinition)o;
        return this.oid.equals(d.oid) && this.structuralClass.equalsIgnoreCase(d.structuralClass) && StaticUtils.stringsEqualIgnoreCaseOrderIndependent(this.names, d.names) && StaticUtils.stringsEqualIgnoreCaseOrderIndependent(this.requiredAttributes, d.requiredAttributes) && StaticUtils.stringsEqualIgnoreCaseOrderIndependent(this.optionalAttributes, d.optionalAttributes) && StaticUtils.bothNullOrEqualIgnoreCase(this.description, d.description) && this.isObsolete == d.isObsolete && SchemaElement.extensionsEqual(this.extensions, d.extensions);
    }
    
    @Override
    public String toString() {
        return this.nameFormString;
    }
}
