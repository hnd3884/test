package com.unboundid.ldap.sdk.schema;

import com.unboundid.util.Debug;
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
public final class AttributeTypeDefinition extends SchemaElement
{
    private static final long serialVersionUID = -6688185196734362719L;
    private final AttributeUsage usage;
    private final boolean isCollective;
    private final boolean isNoUserModification;
    private final boolean isObsolete;
    private final boolean isSingleValued;
    private final Map<String, String[]> extensions;
    private final String attributeTypeString;
    private final String description;
    private final String equalityMatchingRule;
    private final String oid;
    private final String orderingMatchingRule;
    private final String substringMatchingRule;
    private final String superiorType;
    private final String syntaxOID;
    private final String[] names;
    
    public AttributeTypeDefinition(final String s) throws LDAPException {
        Validator.ensureNotNull(s);
        this.attributeTypeString = s.trim();
        final int length = this.attributeTypeString.length();
        if (length == 0) {
            throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_ATTRTYPE_DECODE_EMPTY.get());
        }
        if (this.attributeTypeString.charAt(0) != '(') {
            throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_ATTRTYPE_DECODE_NO_OPENING_PAREN.get(this.attributeTypeString));
        }
        int pos = SchemaElement.skipSpaces(this.attributeTypeString, 1, length);
        StringBuilder buffer = new StringBuilder();
        pos = SchemaElement.readOID(this.attributeTypeString, pos, length, buffer);
        this.oid = buffer.toString();
        final ArrayList<String> nameList = new ArrayList<String>(1);
        AttributeUsage attrUsage = null;
        Boolean collective = null;
        Boolean noUserMod = null;
        Boolean obsolete = null;
        Boolean singleValue = null;
        final Map<String, String[]> exts = new LinkedHashMap<String, String[]>(StaticUtils.computeMapCapacity(5));
        String descr = null;
        String eqRule = null;
        String ordRule = null;
        String subRule = null;
        String supType = null;
        String synOID = null;
        while (true) {
            int tokenStartPos;
            for (pos = (tokenStartPos = SchemaElement.skipSpaces(this.attributeTypeString, pos, length)); pos < length && this.attributeTypeString.charAt(pos) != ' '; ++pos) {}
            String token = this.attributeTypeString.substring(tokenStartPos, pos);
            if (token.length() > 1 && token.endsWith(")")) {
                token = token.substring(0, token.length() - 1);
                --pos;
            }
            final String lowerToken = StaticUtils.toLowerCase(token);
            if (lowerToken.equals(")")) {
                if (pos < length) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_ATTRTYPE_DECODE_CLOSE_NOT_AT_END.get(this.attributeTypeString));
                }
                this.description = descr;
                this.equalityMatchingRule = eqRule;
                this.orderingMatchingRule = ordRule;
                this.substringMatchingRule = subRule;
                this.superiorType = supType;
                this.syntaxOID = synOID;
                nameList.toArray(this.names = new String[nameList.size()]);
                this.isObsolete = (obsolete != null);
                this.isSingleValued = (singleValue != null);
                this.isCollective = (collective != null);
                this.isNoUserModification = (noUserMod != null);
                if (attrUsage == null) {
                    this.usage = AttributeUsage.USER_APPLICATIONS;
                }
                else {
                    this.usage = attrUsage;
                }
                this.extensions = Collections.unmodifiableMap((Map<? extends String, ? extends String[]>)exts);
            }
            else if (lowerToken.equals("name")) {
                if (!nameList.isEmpty()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_ATTRTYPE_DECODE_MULTIPLE_ELEMENTS.get(this.attributeTypeString, "NAME"));
                }
                pos = SchemaElement.skipSpaces(this.attributeTypeString, pos, length);
                pos = SchemaElement.readQDStrings(this.attributeTypeString, pos, length, nameList);
            }
            else if (lowerToken.equals("desc")) {
                if (descr != null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_ATTRTYPE_DECODE_MULTIPLE_ELEMENTS.get(this.attributeTypeString, "DESC"));
                }
                pos = SchemaElement.skipSpaces(this.attributeTypeString, pos, length);
                buffer = new StringBuilder();
                pos = SchemaElement.readQDString(this.attributeTypeString, pos, length, buffer);
                descr = buffer.toString();
            }
            else if (lowerToken.equals("obsolete")) {
                if (obsolete != null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_ATTRTYPE_DECODE_MULTIPLE_ELEMENTS.get(this.attributeTypeString, "OBSOLETE"));
                }
                obsolete = true;
            }
            else if (lowerToken.equals("sup")) {
                if (supType != null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_ATTRTYPE_DECODE_MULTIPLE_ELEMENTS.get(this.attributeTypeString, "SUP"));
                }
                pos = SchemaElement.skipSpaces(this.attributeTypeString, pos, length);
                buffer = new StringBuilder();
                pos = SchemaElement.readOID(this.attributeTypeString, pos, length, buffer);
                supType = buffer.toString();
            }
            else if (lowerToken.equals("equality")) {
                if (eqRule != null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_ATTRTYPE_DECODE_MULTIPLE_ELEMENTS.get(this.attributeTypeString, "EQUALITY"));
                }
                pos = SchemaElement.skipSpaces(this.attributeTypeString, pos, length);
                buffer = new StringBuilder();
                pos = SchemaElement.readOID(this.attributeTypeString, pos, length, buffer);
                eqRule = buffer.toString();
            }
            else if (lowerToken.equals("ordering")) {
                if (ordRule != null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_ATTRTYPE_DECODE_MULTIPLE_ELEMENTS.get(this.attributeTypeString, "ORDERING"));
                }
                pos = SchemaElement.skipSpaces(this.attributeTypeString, pos, length);
                buffer = new StringBuilder();
                pos = SchemaElement.readOID(this.attributeTypeString, pos, length, buffer);
                ordRule = buffer.toString();
            }
            else if (lowerToken.equals("substr")) {
                if (subRule != null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_ATTRTYPE_DECODE_MULTIPLE_ELEMENTS.get(this.attributeTypeString, "SUBSTR"));
                }
                pos = SchemaElement.skipSpaces(this.attributeTypeString, pos, length);
                buffer = new StringBuilder();
                pos = SchemaElement.readOID(this.attributeTypeString, pos, length, buffer);
                subRule = buffer.toString();
            }
            else if (lowerToken.equals("syntax")) {
                if (synOID != null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_ATTRTYPE_DECODE_MULTIPLE_ELEMENTS.get(this.attributeTypeString, "SYNTAX"));
                }
                pos = SchemaElement.skipSpaces(this.attributeTypeString, pos, length);
                buffer = new StringBuilder();
                pos = SchemaElement.readOID(this.attributeTypeString, pos, length, buffer);
                synOID = buffer.toString();
            }
            else if (lowerToken.equals("single-value")) {
                if (singleValue != null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_ATTRTYPE_DECODE_MULTIPLE_ELEMENTS.get(this.attributeTypeString, "SINGLE-VALUE"));
                }
                singleValue = true;
            }
            else if (lowerToken.equals("collective")) {
                if (collective != null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_ATTRTYPE_DECODE_MULTIPLE_ELEMENTS.get(this.attributeTypeString, "COLLECTIVE"));
                }
                collective = true;
            }
            else if (lowerToken.equals("no-user-modification")) {
                if (noUserMod != null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_ATTRTYPE_DECODE_MULTIPLE_ELEMENTS.get(this.attributeTypeString, "NO-USER-MODIFICATION"));
                }
                noUserMod = true;
            }
            else if (lowerToken.equals("usage")) {
                if (attrUsage != null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_ATTRTYPE_DECODE_MULTIPLE_ELEMENTS.get(this.attributeTypeString, "USAGE"));
                }
                pos = SchemaElement.skipSpaces(this.attributeTypeString, pos, length);
                buffer = new StringBuilder();
                pos = SchemaElement.readOID(this.attributeTypeString, pos, length, buffer);
                final String usageStr = StaticUtils.toLowerCase(buffer.toString());
                if (usageStr.equals("userapplications")) {
                    attrUsage = AttributeUsage.USER_APPLICATIONS;
                }
                else if (usageStr.equals("directoryoperation")) {
                    attrUsage = AttributeUsage.DIRECTORY_OPERATION;
                }
                else if (usageStr.equals("distributedoperation")) {
                    attrUsage = AttributeUsage.DISTRIBUTED_OPERATION;
                }
                else {
                    if (!usageStr.equals("dsaoperation")) {
                        throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_ATTRTYPE_DECODE_INVALID_USAGE.get(this.attributeTypeString, usageStr));
                    }
                    attrUsage = AttributeUsage.DSA_OPERATION;
                }
            }
            else {
                if (!lowerToken.startsWith("x-")) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_ATTRTYPE_DECODE_UNEXPECTED_TOKEN.get(this.attributeTypeString, token));
                }
                pos = SchemaElement.skipSpaces(this.attributeTypeString, pos, length);
                final ArrayList<String> valueList = new ArrayList<String>(5);
                pos = SchemaElement.readQDStrings(this.attributeTypeString, pos, length, valueList);
                final String[] values = new String[valueList.size()];
                valueList.toArray(values);
                if (exts.containsKey(token)) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_ATTRTYPE_DECODE_DUP_EXT.get(this.attributeTypeString, token));
                }
                exts.put(token, values);
            }
        }
    }
    
    public AttributeTypeDefinition(final String oid, final String name, final String description, final String equalityMatchingRule, final String orderingMatchingRule, final String substringMatchingRule, final String syntaxOID, final boolean isSingleValued, final Map<String, String[]> extensions) {
        this(oid, (String[])((name == null) ? null : new String[] { name }), description, false, null, equalityMatchingRule, orderingMatchingRule, substringMatchingRule, syntaxOID, isSingleValued, false, false, AttributeUsage.USER_APPLICATIONS, extensions);
    }
    
    public AttributeTypeDefinition(final String oid, final String[] names, final String description, final boolean isObsolete, final String superiorType, final String equalityMatchingRule, final String orderingMatchingRule, final String substringMatchingRule, final String syntaxOID, final boolean isSingleValued, final boolean isCollective, final boolean isNoUserModification, final AttributeUsage usage, final Map<String, String[]> extensions) {
        Validator.ensureNotNull(oid);
        this.oid = oid;
        this.description = description;
        this.isObsolete = isObsolete;
        this.superiorType = superiorType;
        this.equalityMatchingRule = equalityMatchingRule;
        this.orderingMatchingRule = orderingMatchingRule;
        this.substringMatchingRule = substringMatchingRule;
        this.syntaxOID = syntaxOID;
        this.isSingleValued = isSingleValued;
        this.isCollective = isCollective;
        this.isNoUserModification = isNoUserModification;
        if (names == null) {
            this.names = StaticUtils.NO_STRINGS;
        }
        else {
            this.names = names;
        }
        if (usage == null) {
            this.usage = AttributeUsage.USER_APPLICATIONS;
        }
        else {
            this.usage = usage;
        }
        if (extensions == null) {
            this.extensions = Collections.emptyMap();
        }
        else {
            this.extensions = Collections.unmodifiableMap((Map<? extends String, ? extends String[]>)extensions);
        }
        final StringBuilder buffer = new StringBuilder();
        this.createDefinitionString(buffer);
        this.attributeTypeString = buffer.toString();
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
        if (this.superiorType != null) {
            buffer.append(" SUP ");
            buffer.append(this.superiorType);
        }
        if (this.equalityMatchingRule != null) {
            buffer.append(" EQUALITY ");
            buffer.append(this.equalityMatchingRule);
        }
        if (this.orderingMatchingRule != null) {
            buffer.append(" ORDERING ");
            buffer.append(this.orderingMatchingRule);
        }
        if (this.substringMatchingRule != null) {
            buffer.append(" SUBSTR ");
            buffer.append(this.substringMatchingRule);
        }
        if (this.syntaxOID != null) {
            buffer.append(" SYNTAX ");
            buffer.append(this.syntaxOID);
        }
        if (this.isSingleValued) {
            buffer.append(" SINGLE-VALUE");
        }
        if (this.isCollective) {
            buffer.append(" COLLECTIVE");
        }
        if (this.isNoUserModification) {
            buffer.append(" NO-USER-MODIFICATION");
        }
        buffer.append(" USAGE ");
        buffer.append(this.usage.getName());
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
    
    public String getSuperiorType() {
        return this.superiorType;
    }
    
    public AttributeTypeDefinition getSuperiorType(final Schema schema) {
        if (this.superiorType != null) {
            return schema.getAttributeType(this.superiorType);
        }
        return null;
    }
    
    public String getEqualityMatchingRule() {
        return this.equalityMatchingRule;
    }
    
    public String getEqualityMatchingRule(final Schema schema) {
        if (this.equalityMatchingRule == null) {
            final AttributeTypeDefinition sup = this.getSuperiorType(schema);
            if (sup != null) {
                return sup.getEqualityMatchingRule(schema);
            }
        }
        return this.equalityMatchingRule;
    }
    
    public String getOrderingMatchingRule() {
        return this.orderingMatchingRule;
    }
    
    public String getOrderingMatchingRule(final Schema schema) {
        if (this.orderingMatchingRule == null) {
            final AttributeTypeDefinition sup = this.getSuperiorType(schema);
            if (sup != null) {
                return sup.getOrderingMatchingRule(schema);
            }
        }
        return this.orderingMatchingRule;
    }
    
    public String getSubstringMatchingRule() {
        return this.substringMatchingRule;
    }
    
    public String getSubstringMatchingRule(final Schema schema) {
        if (this.substringMatchingRule == null) {
            final AttributeTypeDefinition sup = this.getSuperiorType(schema);
            if (sup != null) {
                return sup.getSubstringMatchingRule(schema);
            }
        }
        return this.substringMatchingRule;
    }
    
    public String getSyntaxOID() {
        return this.syntaxOID;
    }
    
    public String getSyntaxOID(final Schema schema) {
        if (this.syntaxOID == null) {
            final AttributeTypeDefinition sup = this.getSuperiorType(schema);
            if (sup != null) {
                return sup.getSyntaxOID(schema);
            }
        }
        return this.syntaxOID;
    }
    
    public String getBaseSyntaxOID() {
        return getBaseSyntaxOID(this.syntaxOID);
    }
    
    public String getBaseSyntaxOID(final Schema schema) {
        return getBaseSyntaxOID(this.getSyntaxOID(schema));
    }
    
    public static String getBaseSyntaxOID(final String syntaxOID) {
        if (syntaxOID == null) {
            return null;
        }
        final int curlyPos = syntaxOID.indexOf(123);
        if (curlyPos > 0) {
            return syntaxOID.substring(0, curlyPos);
        }
        return syntaxOID;
    }
    
    public int getSyntaxMinimumUpperBound() {
        return getSyntaxMinimumUpperBound(this.syntaxOID);
    }
    
    public int getSyntaxMinimumUpperBound(final Schema schema) {
        return getSyntaxMinimumUpperBound(this.getSyntaxOID(schema));
    }
    
    public static int getSyntaxMinimumUpperBound(final String syntaxOID) {
        if (syntaxOID == null) {
            return -1;
        }
        final int curlyPos = syntaxOID.indexOf(123);
        if (curlyPos > 0 && syntaxOID.endsWith("}")) {
            try {
                return Integer.parseInt(syntaxOID.substring(curlyPos + 1, syntaxOID.length() - 1));
            }
            catch (final Exception e) {
                Debug.debugException(e);
                return -1;
            }
        }
        return -1;
    }
    
    public boolean isSingleValued() {
        return this.isSingleValued;
    }
    
    public boolean isCollective() {
        return this.isCollective;
    }
    
    public boolean isNoUserModification() {
        return this.isNoUserModification;
    }
    
    public AttributeUsage getUsage() {
        return this.usage;
    }
    
    public boolean isOperational() {
        return this.usage.isOperational();
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
        if (!(o instanceof AttributeTypeDefinition)) {
            return false;
        }
        final AttributeTypeDefinition d = (AttributeTypeDefinition)o;
        return this.oid.equals(d.oid) && StaticUtils.stringsEqualIgnoreCaseOrderIndependent(this.names, d.names) && StaticUtils.bothNullOrEqual(this.usage, d.usage) && StaticUtils.bothNullOrEqualIgnoreCase(this.description, d.description) && StaticUtils.bothNullOrEqualIgnoreCase(this.equalityMatchingRule, d.equalityMatchingRule) && StaticUtils.bothNullOrEqualIgnoreCase(this.orderingMatchingRule, d.orderingMatchingRule) && StaticUtils.bothNullOrEqualIgnoreCase(this.substringMatchingRule, d.substringMatchingRule) && StaticUtils.bothNullOrEqualIgnoreCase(this.superiorType, d.superiorType) && StaticUtils.bothNullOrEqualIgnoreCase(this.syntaxOID, d.syntaxOID) && this.isCollective == d.isCollective && this.isNoUserModification == d.isNoUserModification && this.isObsolete == d.isObsolete && this.isSingleValued == d.isSingleValued && SchemaElement.extensionsEqual(this.extensions, d.extensions);
    }
    
    @Override
    public String toString() {
        return this.attributeTypeString;
    }
}
