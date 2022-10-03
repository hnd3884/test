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
public final class DITContentRuleDefinition extends SchemaElement
{
    private static final long serialVersionUID = 3224440505307817586L;
    private final boolean isObsolete;
    private final Map<String, String[]> extensions;
    private final String description;
    private final String ditContentRuleString;
    private final String oid;
    private final String[] auxiliaryClasses;
    private final String[] names;
    private final String[] optionalAttributes;
    private final String[] prohibitedAttributes;
    private final String[] requiredAttributes;
    
    public DITContentRuleDefinition(final String s) throws LDAPException {
        Validator.ensureNotNull(s);
        this.ditContentRuleString = s.trim();
        final int length = this.ditContentRuleString.length();
        if (length == 0) {
            throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_DCR_DECODE_EMPTY.get());
        }
        if (this.ditContentRuleString.charAt(0) != '(') {
            throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_DCR_DECODE_NO_OPENING_PAREN.get(this.ditContentRuleString));
        }
        int pos = SchemaElement.skipSpaces(this.ditContentRuleString, 1, length);
        StringBuilder buffer = new StringBuilder();
        pos = SchemaElement.readOID(this.ditContentRuleString, pos, length, buffer);
        this.oid = buffer.toString();
        final ArrayList<String> nameList = new ArrayList<String>(5);
        final ArrayList<String> reqAttrs = new ArrayList<String>(10);
        final ArrayList<String> optAttrs = new ArrayList<String>(10);
        final ArrayList<String> notAttrs = new ArrayList<String>(10);
        final ArrayList<String> auxOCs = new ArrayList<String>(10);
        final Map<String, String[]> exts = new LinkedHashMap<String, String[]>(StaticUtils.computeMapCapacity(5));
        Boolean obsolete = null;
        String descr = null;
        while (true) {
            int tokenStartPos;
            for (pos = (tokenStartPos = SchemaElement.skipSpaces(this.ditContentRuleString, pos, length)); pos < length && this.ditContentRuleString.charAt(pos) != ' '; ++pos) {}
            String token = this.ditContentRuleString.substring(tokenStartPos, pos);
            if (token.length() > 1 && token.endsWith(")")) {
                token = token.substring(0, token.length() - 1);
                --pos;
            }
            final String lowerToken = StaticUtils.toLowerCase(token);
            if (lowerToken.equals(")")) {
                if (pos < length) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_DCR_DECODE_CLOSE_NOT_AT_END.get(this.ditContentRuleString));
                }
                this.description = descr;
                nameList.toArray(this.names = new String[nameList.size()]);
                auxOCs.toArray(this.auxiliaryClasses = new String[auxOCs.size()]);
                reqAttrs.toArray(this.requiredAttributes = new String[reqAttrs.size()]);
                optAttrs.toArray(this.optionalAttributes = new String[optAttrs.size()]);
                notAttrs.toArray(this.prohibitedAttributes = new String[notAttrs.size()]);
                this.isObsolete = (obsolete != null);
                this.extensions = Collections.unmodifiableMap((Map<? extends String, ? extends String[]>)exts);
            }
            else if (lowerToken.equals("name")) {
                if (!nameList.isEmpty()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_DCR_DECODE_MULTIPLE_ELEMENTS.get(this.ditContentRuleString, "NAME"));
                }
                pos = SchemaElement.skipSpaces(this.ditContentRuleString, pos, length);
                pos = SchemaElement.readQDStrings(this.ditContentRuleString, pos, length, nameList);
            }
            else if (lowerToken.equals("desc")) {
                if (descr != null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_DCR_DECODE_MULTIPLE_ELEMENTS.get(this.ditContentRuleString, "DESC"));
                }
                pos = SchemaElement.skipSpaces(this.ditContentRuleString, pos, length);
                buffer = new StringBuilder();
                pos = SchemaElement.readQDString(this.ditContentRuleString, pos, length, buffer);
                descr = buffer.toString();
            }
            else if (lowerToken.equals("obsolete")) {
                if (obsolete != null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_DCR_DECODE_MULTIPLE_ELEMENTS.get(this.ditContentRuleString, "OBSOLETE"));
                }
                obsolete = true;
            }
            else if (lowerToken.equals("aux")) {
                if (!auxOCs.isEmpty()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_DCR_DECODE_MULTIPLE_ELEMENTS.get(this.ditContentRuleString, "AUX"));
                }
                pos = SchemaElement.skipSpaces(this.ditContentRuleString, pos, length);
                pos = SchemaElement.readOIDs(this.ditContentRuleString, pos, length, auxOCs);
            }
            else if (lowerToken.equals("must")) {
                if (!reqAttrs.isEmpty()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_DCR_DECODE_MULTIPLE_ELEMENTS.get(this.ditContentRuleString, "MUST"));
                }
                pos = SchemaElement.skipSpaces(this.ditContentRuleString, pos, length);
                pos = SchemaElement.readOIDs(this.ditContentRuleString, pos, length, reqAttrs);
            }
            else if (lowerToken.equals("may")) {
                if (!optAttrs.isEmpty()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_DCR_DECODE_MULTIPLE_ELEMENTS.get(this.ditContentRuleString, "MAY"));
                }
                pos = SchemaElement.skipSpaces(this.ditContentRuleString, pos, length);
                pos = SchemaElement.readOIDs(this.ditContentRuleString, pos, length, optAttrs);
            }
            else if (lowerToken.equals("not")) {
                if (!notAttrs.isEmpty()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_DCR_DECODE_MULTIPLE_ELEMENTS.get(this.ditContentRuleString, "NOT"));
                }
                pos = SchemaElement.skipSpaces(this.ditContentRuleString, pos, length);
                pos = SchemaElement.readOIDs(this.ditContentRuleString, pos, length, notAttrs);
            }
            else {
                if (!lowerToken.startsWith("x-")) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_DCR_DECODE_DUP_EXT.get(this.ditContentRuleString, token));
                }
                pos = SchemaElement.skipSpaces(this.ditContentRuleString, pos, length);
                final ArrayList<String> valueList = new ArrayList<String>(5);
                pos = SchemaElement.readQDStrings(this.ditContentRuleString, pos, length, valueList);
                final String[] values = new String[valueList.size()];
                valueList.toArray(values);
                if (exts.containsKey(token)) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_DCR_DECODE_DUP_EXT.get(this.ditContentRuleString, token));
                }
                exts.put(token, values);
            }
        }
    }
    
    public DITContentRuleDefinition(final String oid, final String name, final String description, final String[] auxiliaryClasses, final String[] requiredAttributes, final String[] optionalAttributes, final String[] prohibitedAttributes, final Map<String, String[]> extensions) {
        this(oid, (String[])((name == null) ? null : new String[] { name }), description, false, auxiliaryClasses, requiredAttributes, optionalAttributes, prohibitedAttributes, extensions);
    }
    
    public DITContentRuleDefinition(final String oid, final String name, final String description, final Collection<String> auxiliaryClasses, final Collection<String> requiredAttributes, final Collection<String> optionalAttributes, final Collection<String> prohibitedAttributes, final Map<String, String[]> extensions) {
        this(oid, (String[])((name == null) ? null : new String[] { name }), description, false, SchemaElement.toArray(auxiliaryClasses), SchemaElement.toArray(requiredAttributes), SchemaElement.toArray(optionalAttributes), SchemaElement.toArray(prohibitedAttributes), extensions);
    }
    
    public DITContentRuleDefinition(final String oid, final String[] names, final String description, final boolean isObsolete, final String[] auxiliaryClasses, final String[] requiredAttributes, final String[] optionalAttributes, final String[] prohibitedAttributes, final Map<String, String[]> extensions) {
        Validator.ensureNotNull(oid);
        this.oid = oid;
        this.isObsolete = isObsolete;
        this.description = description;
        if (names == null) {
            this.names = StaticUtils.NO_STRINGS;
        }
        else {
            this.names = names;
        }
        if (auxiliaryClasses == null) {
            this.auxiliaryClasses = StaticUtils.NO_STRINGS;
        }
        else {
            this.auxiliaryClasses = auxiliaryClasses;
        }
        if (requiredAttributes == null) {
            this.requiredAttributes = StaticUtils.NO_STRINGS;
        }
        else {
            this.requiredAttributes = requiredAttributes;
        }
        if (optionalAttributes == null) {
            this.optionalAttributes = StaticUtils.NO_STRINGS;
        }
        else {
            this.optionalAttributes = optionalAttributes;
        }
        if (prohibitedAttributes == null) {
            this.prohibitedAttributes = StaticUtils.NO_STRINGS;
        }
        else {
            this.prohibitedAttributes = prohibitedAttributes;
        }
        if (extensions == null) {
            this.extensions = Collections.emptyMap();
        }
        else {
            this.extensions = Collections.unmodifiableMap((Map<? extends String, ? extends String[]>)extensions);
        }
        final StringBuilder buffer = new StringBuilder();
        this.createDefinitionString(buffer);
        this.ditContentRuleString = buffer.toString();
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
        if (this.auxiliaryClasses.length == 1) {
            buffer.append(" AUX ");
            buffer.append(this.auxiliaryClasses[0]);
        }
        else if (this.auxiliaryClasses.length > 1) {
            buffer.append(" AUX (");
            for (int i = 0; i < this.auxiliaryClasses.length; ++i) {
                if (i > 0) {
                    buffer.append(" $ ");
                }
                else {
                    buffer.append(' ');
                }
                buffer.append(this.auxiliaryClasses[i]);
            }
            buffer.append(" )");
        }
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
        if (this.prohibitedAttributes.length == 1) {
            buffer.append(" NOT ");
            buffer.append(this.prohibitedAttributes[0]);
        }
        else if (this.prohibitedAttributes.length > 1) {
            buffer.append(" NOT (");
            for (int i = 0; i < this.prohibitedAttributes.length; ++i) {
                if (i > 0) {
                    buffer.append(" $ ");
                }
                else {
                    buffer.append(' ');
                }
                buffer.append(this.prohibitedAttributes[i]);
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
    
    public String[] getAuxiliaryClasses() {
        return this.auxiliaryClasses;
    }
    
    public String[] getRequiredAttributes() {
        return this.requiredAttributes;
    }
    
    public String[] getOptionalAttributes() {
        return this.optionalAttributes;
    }
    
    public String[] getProhibitedAttributes() {
        return this.prohibitedAttributes;
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
        if (!(o instanceof DITContentRuleDefinition)) {
            return false;
        }
        final DITContentRuleDefinition d = (DITContentRuleDefinition)o;
        return this.oid.equals(d.oid) && StaticUtils.stringsEqualIgnoreCaseOrderIndependent(this.names, d.names) && StaticUtils.stringsEqualIgnoreCaseOrderIndependent(this.auxiliaryClasses, d.auxiliaryClasses) && StaticUtils.stringsEqualIgnoreCaseOrderIndependent(this.requiredAttributes, d.requiredAttributes) && StaticUtils.stringsEqualIgnoreCaseOrderIndependent(this.optionalAttributes, d.optionalAttributes) && StaticUtils.stringsEqualIgnoreCaseOrderIndependent(this.prohibitedAttributes, d.prohibitedAttributes) && StaticUtils.bothNullOrEqualIgnoreCase(this.description, d.description) && this.isObsolete == d.isObsolete && SchemaElement.extensionsEqual(this.extensions, d.extensions);
    }
    
    @Override
    public String toString() {
        return this.ditContentRuleString;
    }
}
