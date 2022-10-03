package com.unboundid.ldap.sdk.schema;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Validator;
import java.util.Map;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AttributeSyntaxDefinition extends SchemaElement
{
    private static final long serialVersionUID = 8593718232711987488L;
    private final Map<String, String[]> extensions;
    private final String description;
    private final String attributeSyntaxString;
    private final String oid;
    
    public AttributeSyntaxDefinition(final String s) throws LDAPException {
        Validator.ensureNotNull(s);
        this.attributeSyntaxString = s.trim();
        final int length = this.attributeSyntaxString.length();
        if (length == 0) {
            throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_ATTRSYNTAX_DECODE_EMPTY.get());
        }
        if (this.attributeSyntaxString.charAt(0) != '(') {
            throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_ATTRSYNTAX_DECODE_NO_OPENING_PAREN.get(this.attributeSyntaxString));
        }
        int pos = SchemaElement.skipSpaces(this.attributeSyntaxString, 1, length);
        StringBuilder buffer = new StringBuilder();
        pos = SchemaElement.readOID(this.attributeSyntaxString, pos, length, buffer);
        this.oid = buffer.toString();
        String descr = null;
        final Map<String, String[]> exts = new LinkedHashMap<String, String[]>(StaticUtils.computeMapCapacity(5));
        while (true) {
            int tokenStartPos;
            for (pos = (tokenStartPos = SchemaElement.skipSpaces(this.attributeSyntaxString, pos, length)); pos < length && this.attributeSyntaxString.charAt(pos) != ' '; ++pos) {}
            final String token = this.attributeSyntaxString.substring(tokenStartPos, pos);
            final String lowerToken = StaticUtils.toLowerCase(token);
            if (lowerToken.equals(")")) {
                if (pos < length) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_ATTRSYNTAX_DECODE_CLOSE_NOT_AT_END.get(this.attributeSyntaxString));
                }
                this.description = descr;
                this.extensions = Collections.unmodifiableMap((Map<? extends String, ? extends String[]>)exts);
            }
            else if (lowerToken.equals("desc")) {
                if (descr != null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_ATTRSYNTAX_DECODE_MULTIPLE_DESC.get(this.attributeSyntaxString));
                }
                pos = SchemaElement.skipSpaces(this.attributeSyntaxString, pos, length);
                buffer = new StringBuilder();
                pos = SchemaElement.readQDString(this.attributeSyntaxString, pos, length, buffer);
                descr = buffer.toString();
            }
            else {
                if (!lowerToken.startsWith("x-")) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_ATTRSYNTAX_DECODE_UNEXPECTED_TOKEN.get(this.attributeSyntaxString, token));
                }
                pos = SchemaElement.skipSpaces(this.attributeSyntaxString, pos, length);
                final ArrayList<String> valueList = new ArrayList<String>(5);
                pos = SchemaElement.readQDStrings(this.attributeSyntaxString, pos, length, valueList);
                final String[] values = new String[valueList.size()];
                valueList.toArray(values);
                if (exts.containsKey(token)) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, SchemaMessages.ERR_ATTRSYNTAX_DECODE_DUP_EXT.get(this.attributeSyntaxString, token));
                }
                exts.put(token, values);
            }
        }
    }
    
    public AttributeSyntaxDefinition(final String oid, final String description, final Map<String, String[]> extensions) {
        Validator.ensureNotNull(oid);
        this.oid = oid;
        this.description = description;
        if (extensions == null) {
            this.extensions = Collections.emptyMap();
        }
        else {
            this.extensions = Collections.unmodifiableMap((Map<? extends String, ? extends String[]>)extensions);
        }
        final StringBuilder buffer = new StringBuilder();
        this.createDefinitionString(buffer);
        this.attributeSyntaxString = buffer.toString();
    }
    
    private void createDefinitionString(final StringBuilder buffer) {
        buffer.append("( ");
        buffer.append(this.oid);
        if (this.description != null) {
            buffer.append(" DESC '");
            SchemaElement.encodeValue(this.description, buffer);
            buffer.append('\'');
        }
        for (final Map.Entry<String, String[]> e : this.extensions.entrySet()) {
            final String name = e.getKey();
            final String[] values = e.getValue();
            if (values.length == 1) {
                buffer.append(' ');
                buffer.append(name);
                buffer.append(" '");
                SchemaElement.encodeValue(values[0], buffer);
                buffer.append('\'');
            }
            else {
                buffer.append(' ');
                buffer.append(name);
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
    
    public String getDescription() {
        return this.description;
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
        if (!(o instanceof AttributeSyntaxDefinition)) {
            return false;
        }
        final AttributeSyntaxDefinition d = (AttributeSyntaxDefinition)o;
        return this.oid.equals(d.oid) && StaticUtils.bothNullOrEqualIgnoreCase(this.description, d.description) && SchemaElement.extensionsEqual(this.extensions, d.extensions);
    }
    
    @Override
    public String toString() {
        return this.attributeSyntaxString;
    }
}
