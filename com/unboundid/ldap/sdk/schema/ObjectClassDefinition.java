package com.unboundid.ldap.sdk.schema;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
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
public final class ObjectClassDefinition extends SchemaElement
{
    private static final long serialVersionUID = -3024333376249332728L;
    private final boolean isObsolete;
    private final Map<String, String[]> extensions;
    private final ObjectClassType objectClassType;
    private final String description;
    private final String objectClassString;
    private final String oid;
    private final String[] names;
    private final String[] optionalAttributes;
    private final String[] requiredAttributes;
    private final String[] superiorClasses;
    
    public ObjectClassDefinition(final String s) throws LDAPException {
        Validator.ensureNotNull(s);
        this.objectClassString = s.trim();
        final int length = this.objectClassString.length();
        if (length == 0) {
            throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_OC_DECODE_EMPTY.get());
        }
        if (this.objectClassString.charAt(0) != '(') {
            throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_OC_DECODE_NO_OPENING_PAREN.get(this.objectClassString));
        }
        int pos = SchemaElement.skipSpaces(this.objectClassString, 1, length);
        StringBuilder buffer = new StringBuilder();
        pos = SchemaElement.readOID(this.objectClassString, pos, length, buffer);
        this.oid = buffer.toString();
        final ArrayList<String> nameList = new ArrayList<String>(1);
        final ArrayList<String> supList = new ArrayList<String>(1);
        final ArrayList<String> reqAttrs = new ArrayList<String>(20);
        final ArrayList<String> optAttrs = new ArrayList<String>(20);
        final Map<String, String[]> exts = new LinkedHashMap<String, String[]>(StaticUtils.computeMapCapacity(5));
        Boolean obsolete = null;
        ObjectClassType ocType = null;
        String descr = null;
        while (true) {
            int tokenStartPos;
            for (pos = (tokenStartPos = SchemaElement.skipSpaces(this.objectClassString, pos, length)); pos < length && this.objectClassString.charAt(pos) != ' '; ++pos) {}
            String token = this.objectClassString.substring(tokenStartPos, pos);
            if (token.length() > 1 && token.endsWith(")")) {
                token = token.substring(0, token.length() - 1);
                --pos;
            }
            final String lowerToken = StaticUtils.toLowerCase(token);
            if (lowerToken.equals(")")) {
                if (pos < length) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_OC_DECODE_CLOSE_NOT_AT_END.get(this.objectClassString));
                }
                this.description = descr;
                nameList.toArray(this.names = new String[nameList.size()]);
                supList.toArray(this.superiorClasses = new String[supList.size()]);
                reqAttrs.toArray(this.requiredAttributes = new String[reqAttrs.size()]);
                optAttrs.toArray(this.optionalAttributes = new String[optAttrs.size()]);
                this.isObsolete = (obsolete != null);
                this.objectClassType = ocType;
                this.extensions = Collections.unmodifiableMap((Map<? extends String, ? extends String[]>)exts);
            }
            else if (lowerToken.equals("name")) {
                if (!nameList.isEmpty()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_OC_DECODE_MULTIPLE_ELEMENTS.get(this.objectClassString, "NAME"));
                }
                pos = SchemaElement.skipSpaces(this.objectClassString, pos, length);
                pos = SchemaElement.readQDStrings(this.objectClassString, pos, length, nameList);
            }
            else if (lowerToken.equals("desc")) {
                if (descr != null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_OC_DECODE_MULTIPLE_ELEMENTS.get(this.objectClassString, "DESC"));
                }
                pos = SchemaElement.skipSpaces(this.objectClassString, pos, length);
                buffer = new StringBuilder();
                pos = SchemaElement.readQDString(this.objectClassString, pos, length, buffer);
                descr = buffer.toString();
            }
            else if (lowerToken.equals("obsolete")) {
                if (obsolete != null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_OC_DECODE_MULTIPLE_ELEMENTS.get(this.objectClassString, "OBSOLETE"));
                }
                obsolete = true;
            }
            else if (lowerToken.equals("sup")) {
                if (!supList.isEmpty()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_OC_DECODE_MULTIPLE_ELEMENTS.get(this.objectClassString, "SUP"));
                }
                pos = SchemaElement.skipSpaces(this.objectClassString, pos, length);
                pos = SchemaElement.readOIDs(this.objectClassString, pos, length, supList);
            }
            else if (lowerToken.equals("abstract")) {
                if (ocType != null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_OC_DECODE_MULTIPLE_OC_TYPES.get(this.objectClassString));
                }
                ocType = ObjectClassType.ABSTRACT;
            }
            else if (lowerToken.equals("structural")) {
                if (ocType != null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_OC_DECODE_MULTIPLE_OC_TYPES.get(this.objectClassString));
                }
                ocType = ObjectClassType.STRUCTURAL;
            }
            else if (lowerToken.equals("auxiliary")) {
                if (ocType != null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_OC_DECODE_MULTIPLE_OC_TYPES.get(this.objectClassString));
                }
                ocType = ObjectClassType.AUXILIARY;
            }
            else if (lowerToken.equals("must")) {
                if (!reqAttrs.isEmpty()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_OC_DECODE_MULTIPLE_ELEMENTS.get(this.objectClassString, "MUST"));
                }
                pos = SchemaElement.skipSpaces(this.objectClassString, pos, length);
                pos = SchemaElement.readOIDs(this.objectClassString, pos, length, reqAttrs);
            }
            else if (lowerToken.equals("may")) {
                if (!optAttrs.isEmpty()) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_OC_DECODE_MULTIPLE_ELEMENTS.get(this.objectClassString, "MAY"));
                }
                pos = SchemaElement.skipSpaces(this.objectClassString, pos, length);
                pos = SchemaElement.readOIDs(this.objectClassString, pos, length, optAttrs);
            }
            else {
                if (!lowerToken.startsWith("x-")) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_OC_DECODE_UNEXPECTED_TOKEN.get(this.objectClassString, token));
                }
                pos = SchemaElement.skipSpaces(this.objectClassString, pos, length);
                final ArrayList<String> valueList = new ArrayList<String>(5);
                pos = SchemaElement.readQDStrings(this.objectClassString, pos, length, valueList);
                final String[] values = new String[valueList.size()];
                valueList.toArray(values);
                if (exts.containsKey(token)) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_OC_DECODE_DUP_EXT.get(this.objectClassString, token));
                }
                exts.put(token, values);
            }
        }
    }
    
    public ObjectClassDefinition(final String oid, final String name, final String description, final String superiorClass, final ObjectClassType objectClassType, final String[] requiredAttributes, final String[] optionalAttributes, final Map<String, String[]> extensions) {
        this(oid, (String[])((name == null) ? null : new String[] { name }), description, false, (String[])((superiorClass == null) ? null : new String[] { superiorClass }), objectClassType, requiredAttributes, optionalAttributes, extensions);
    }
    
    public ObjectClassDefinition(final String oid, final String name, final String description, final String superiorClass, final ObjectClassType objectClassType, final Collection<String> requiredAttributes, final Collection<String> optionalAttributes, final Map<String, String[]> extensions) {
        this(oid, (String[])((name == null) ? null : new String[] { name }), description, false, (String[])((superiorClass == null) ? null : new String[] { superiorClass }), objectClassType, SchemaElement.toArray(requiredAttributes), SchemaElement.toArray(optionalAttributes), extensions);
    }
    
    public ObjectClassDefinition(final String oid, final String[] names, final String description, final boolean isObsolete, final String[] superiorClasses, final ObjectClassType objectClassType, final String[] requiredAttributes, final String[] optionalAttributes, final Map<String, String[]> extensions) {
        Validator.ensureNotNull(oid);
        this.oid = oid;
        this.isObsolete = isObsolete;
        this.description = description;
        this.objectClassType = objectClassType;
        if (names == null) {
            this.names = StaticUtils.NO_STRINGS;
        }
        else {
            this.names = names;
        }
        if (superiorClasses == null) {
            this.superiorClasses = StaticUtils.NO_STRINGS;
        }
        else {
            this.superiorClasses = superiorClasses;
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
        if (extensions == null) {
            this.extensions = Collections.emptyMap();
        }
        else {
            this.extensions = Collections.unmodifiableMap((Map<? extends String, ? extends String[]>)extensions);
        }
        final StringBuilder buffer = new StringBuilder();
        this.createDefinitionString(buffer);
        this.objectClassString = buffer.toString();
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
        if (this.superiorClasses.length == 1) {
            buffer.append(" SUP ");
            buffer.append(this.superiorClasses[0]);
        }
        else if (this.superiorClasses.length > 1) {
            buffer.append(" SUP (");
            for (int i = 0; i < this.superiorClasses.length; ++i) {
                if (i > 0) {
                    buffer.append(" $ ");
                }
                else {
                    buffer.append(' ');
                }
                buffer.append(this.superiorClasses[i]);
            }
            buffer.append(" )");
        }
        if (this.objectClassType != null) {
            buffer.append(' ');
            buffer.append(this.objectClassType.getName());
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
    
    public String[] getSuperiorClasses() {
        return this.superiorClasses;
    }
    
    public Set<ObjectClassDefinition> getSuperiorClasses(final Schema schema, final boolean recursive) {
        final LinkedHashSet<ObjectClassDefinition> ocSet = new LinkedHashSet<ObjectClassDefinition>(StaticUtils.computeMapCapacity(10));
        for (final String s : this.superiorClasses) {
            final ObjectClassDefinition d = schema.getObjectClass(s);
            if (d != null) {
                ocSet.add(d);
                if (recursive) {
                    getSuperiorClasses(schema, d, ocSet);
                }
            }
        }
        return Collections.unmodifiableSet((Set<? extends ObjectClassDefinition>)ocSet);
    }
    
    private static void getSuperiorClasses(final Schema schema, final ObjectClassDefinition oc, final Set<ObjectClassDefinition> ocSet) {
        for (final String s : oc.superiorClasses) {
            final ObjectClassDefinition d = schema.getObjectClass(s);
            if (d != null) {
                ocSet.add(d);
                getSuperiorClasses(schema, d, ocSet);
            }
        }
    }
    
    public ObjectClassType getObjectClassType() {
        return this.objectClassType;
    }
    
    public ObjectClassType getObjectClassType(final Schema schema) {
        if (this.objectClassType == null) {
            return ObjectClassType.STRUCTURAL;
        }
        return this.objectClassType;
    }
    
    public String[] getRequiredAttributes() {
        return this.requiredAttributes;
    }
    
    public Set<AttributeTypeDefinition> getRequiredAttributes(final Schema schema, final boolean includeSuperiorClasses) {
        final HashSet<AttributeTypeDefinition> attrSet = new HashSet<AttributeTypeDefinition>(StaticUtils.computeMapCapacity(20));
        for (final String s : this.requiredAttributes) {
            final AttributeTypeDefinition d = schema.getAttributeType(s);
            if (d != null) {
                attrSet.add(d);
            }
        }
        if (includeSuperiorClasses) {
            for (final String s : this.superiorClasses) {
                final ObjectClassDefinition d2 = schema.getObjectClass(s);
                if (d2 != null) {
                    getSuperiorRequiredAttributes(schema, d2, attrSet);
                }
            }
        }
        return Collections.unmodifiableSet((Set<? extends AttributeTypeDefinition>)attrSet);
    }
    
    private static void getSuperiorRequiredAttributes(final Schema schema, final ObjectClassDefinition oc, final Set<AttributeTypeDefinition> attrSet) {
        for (final String s : oc.requiredAttributes) {
            final AttributeTypeDefinition d = schema.getAttributeType(s);
            if (d != null) {
                attrSet.add(d);
            }
        }
        for (final String s : oc.superiorClasses) {
            final ObjectClassDefinition d2 = schema.getObjectClass(s);
            if (d2 != null) {
                getSuperiorRequiredAttributes(schema, d2, attrSet);
            }
        }
    }
    
    public String[] getOptionalAttributes() {
        return this.optionalAttributes;
    }
    
    public Set<AttributeTypeDefinition> getOptionalAttributes(final Schema schema, final boolean includeSuperiorClasses) {
        final HashSet<AttributeTypeDefinition> attrSet = new HashSet<AttributeTypeDefinition>(StaticUtils.computeMapCapacity(20));
        for (final String s : this.optionalAttributes) {
            final AttributeTypeDefinition d = schema.getAttributeType(s);
            if (d != null) {
                attrSet.add(d);
            }
        }
        if (includeSuperiorClasses) {
            final Set<AttributeTypeDefinition> requiredAttrs = this.getRequiredAttributes(schema, true);
            for (final AttributeTypeDefinition d2 : requiredAttrs) {
                attrSet.remove(d2);
            }
            for (final String s2 : this.superiorClasses) {
                final ObjectClassDefinition d3 = schema.getObjectClass(s2);
                if (d3 != null) {
                    getSuperiorOptionalAttributes(schema, d3, attrSet, requiredAttrs);
                }
            }
        }
        return Collections.unmodifiableSet((Set<? extends AttributeTypeDefinition>)attrSet);
    }
    
    private static void getSuperiorOptionalAttributes(final Schema schema, final ObjectClassDefinition oc, final Set<AttributeTypeDefinition> attrSet, final Set<AttributeTypeDefinition> requiredSet) {
        for (final String s : oc.optionalAttributes) {
            final AttributeTypeDefinition d = schema.getAttributeType(s);
            if (d != null && !requiredSet.contains(d)) {
                attrSet.add(d);
            }
        }
        for (final String s : oc.superiorClasses) {
            final ObjectClassDefinition d2 = schema.getObjectClass(s);
            if (d2 != null) {
                getSuperiorOptionalAttributes(schema, d2, attrSet, requiredSet);
            }
        }
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
        if (!(o instanceof ObjectClassDefinition)) {
            return false;
        }
        final ObjectClassDefinition d = (ObjectClassDefinition)o;
        return this.oid.equals(d.oid) && StaticUtils.stringsEqualIgnoreCaseOrderIndependent(this.names, d.names) && StaticUtils.stringsEqualIgnoreCaseOrderIndependent(this.requiredAttributes, d.requiredAttributes) && StaticUtils.stringsEqualIgnoreCaseOrderIndependent(this.optionalAttributes, d.optionalAttributes) && StaticUtils.stringsEqualIgnoreCaseOrderIndependent(this.superiorClasses, d.superiorClasses) && StaticUtils.bothNullOrEqual(this.objectClassType, d.objectClassType) && StaticUtils.bothNullOrEqualIgnoreCase(this.description, d.description) && this.isObsolete == d.isObsolete && SchemaElement.extensionsEqual(this.extensions, d.extensions);
    }
    
    @Override
    public String toString() {
        return this.objectClassString;
    }
}
