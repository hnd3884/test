package com.sun.jndi.ldap;

import javax.naming.ConfigurationException;
import java.util.Vector;
import javax.naming.directory.InvalidAttributeIdentifierException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.InvalidAttributeValueException;
import javax.naming.NamingEnumeration;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

final class LdapSchemaParser
{
    private static final boolean debug = false;
    static final String OBJECTCLASSDESC_ATTR_ID = "objectClasses";
    static final String ATTRIBUTEDESC_ATTR_ID = "attributeTypes";
    static final String SYNTAXDESC_ATTR_ID = "ldapSyntaxes";
    static final String MATCHRULEDESC_ATTR_ID = "matchingRules";
    static final String OBJECTCLASS_DEFINITION_NAME = "ClassDefinition";
    private static final String[] CLASS_DEF_ATTRS;
    static final String ATTRIBUTE_DEFINITION_NAME = "AttributeDefinition";
    private static final String[] ATTR_DEF_ATTRS;
    static final String SYNTAX_DEFINITION_NAME = "SyntaxDefinition";
    private static final String[] SYNTAX_DEF_ATTRS;
    static final String MATCHRULE_DEFINITION_NAME = "MatchingRule";
    private static final String[] MATCHRULE_DEF_ATTRS;
    private static final char SINGLE_QUOTE = '\'';
    private static final char WHSP = ' ';
    private static final char OID_LIST_BEGIN = '(';
    private static final char OID_LIST_END = ')';
    private static final char OID_SEPARATOR = '$';
    private static final String NUMERICOID_ID = "NUMERICOID";
    private static final String NAME_ID = "NAME";
    private static final String DESC_ID = "DESC";
    private static final String OBSOLETE_ID = "OBSOLETE";
    private static final String SUP_ID = "SUP";
    private static final String PRIVATE_ID = "X-";
    private static final String ABSTRACT_ID = "ABSTRACT";
    private static final String STRUCTURAL_ID = "STRUCTURAL";
    private static final String AUXILARY_ID = "AUXILIARY";
    private static final String MUST_ID = "MUST";
    private static final String MAY_ID = "MAY";
    private static final String EQUALITY_ID = "EQUALITY";
    private static final String ORDERING_ID = "ORDERING";
    private static final String SUBSTR_ID = "SUBSTR";
    private static final String SYNTAX_ID = "SYNTAX";
    private static final String SINGLE_VAL_ID = "SINGLE-VALUE";
    private static final String COLLECTIVE_ID = "COLLECTIVE";
    private static final String NO_USER_MOD_ID = "NO-USER-MODIFICATION";
    private static final String USAGE_ID = "USAGE";
    private static final String SCHEMA_TRUE_VALUE = "true";
    private boolean netscapeBug;
    
    LdapSchemaParser(final boolean netscapeBug) {
        this.netscapeBug = netscapeBug;
    }
    
    static final void LDAP2JNDISchema(final Attributes attributes, final LdapSchemaCtx ldapSchemaCtx) throws NamingException {
        final Attribute value = attributes.get("objectClasses");
        if (value != null) {
            objectDescs2ClassDefs(value, ldapSchemaCtx);
        }
        final Attribute value2 = attributes.get("attributeTypes");
        if (value2 != null) {
            attrDescs2AttrDefs(value2, ldapSchemaCtx);
        }
        final Attribute value3 = attributes.get("ldapSyntaxes");
        if (value3 != null) {
            syntaxDescs2SyntaxDefs(value3, ldapSchemaCtx);
        }
        final Attribute value4 = attributes.get("matchingRules");
        if (value4 != null) {
            matchRuleDescs2MatchRuleDefs(value4, ldapSchemaCtx);
        }
    }
    
    private static final DirContext objectDescs2ClassDefs(final Attribute attribute, final LdapSchemaCtx ldapSchemaCtx) throws NamingException {
        final BasicAttributes basicAttributes = new BasicAttributes(true);
        basicAttributes.put(LdapSchemaParser.CLASS_DEF_ATTRS[0], LdapSchemaParser.CLASS_DEF_ATTRS[1]);
        final LdapSchemaCtx setup = ldapSchemaCtx.setup(2, "ClassDefinition", basicAttributes);
        final NamingEnumeration<?> all = attribute.getAll();
        while (all.hasMore()) {
            final String s = (String)all.next();
            try {
                final Object[] desc2Def = desc2Def(s);
                setup.setup(6, (String)desc2Def[0], (Attributes)desc2Def[1]);
            }
            catch (final NamingException ex) {}
        }
        return setup;
    }
    
    private static final DirContext attrDescs2AttrDefs(final Attribute attribute, final LdapSchemaCtx ldapSchemaCtx) throws NamingException {
        final BasicAttributes basicAttributes = new BasicAttributes(true);
        basicAttributes.put(LdapSchemaParser.ATTR_DEF_ATTRS[0], LdapSchemaParser.ATTR_DEF_ATTRS[1]);
        final LdapSchemaCtx setup = ldapSchemaCtx.setup(3, "AttributeDefinition", basicAttributes);
        final NamingEnumeration<?> all = attribute.getAll();
        while (all.hasMore()) {
            final String s = (String)all.next();
            try {
                final Object[] desc2Def = desc2Def(s);
                setup.setup(7, (String)desc2Def[0], (Attributes)desc2Def[1]);
            }
            catch (final NamingException ex) {}
        }
        return setup;
    }
    
    private static final DirContext syntaxDescs2SyntaxDefs(final Attribute attribute, final LdapSchemaCtx ldapSchemaCtx) throws NamingException {
        final BasicAttributes basicAttributes = new BasicAttributes(true);
        basicAttributes.put(LdapSchemaParser.SYNTAX_DEF_ATTRS[0], LdapSchemaParser.SYNTAX_DEF_ATTRS[1]);
        final LdapSchemaCtx setup = ldapSchemaCtx.setup(4, "SyntaxDefinition", basicAttributes);
        final NamingEnumeration<?> all = attribute.getAll();
        while (all.hasMore()) {
            final String s = (String)all.next();
            try {
                final Object[] desc2Def = desc2Def(s);
                setup.setup(8, (String)desc2Def[0], (Attributes)desc2Def[1]);
            }
            catch (final NamingException ex) {}
        }
        return setup;
    }
    
    private static final DirContext matchRuleDescs2MatchRuleDefs(final Attribute attribute, final LdapSchemaCtx ldapSchemaCtx) throws NamingException {
        final BasicAttributes basicAttributes = new BasicAttributes(true);
        basicAttributes.put(LdapSchemaParser.MATCHRULE_DEF_ATTRS[0], LdapSchemaParser.MATCHRULE_DEF_ATTRS[1]);
        final LdapSchemaCtx setup = ldapSchemaCtx.setup(5, "MatchingRule", basicAttributes);
        final NamingEnumeration<?> all = attribute.getAll();
        while (all.hasMore()) {
            final String s = (String)all.next();
            try {
                final Object[] desc2Def = desc2Def(s);
                setup.setup(9, (String)desc2Def[0], (Attributes)desc2Def[1]);
            }
            catch (final NamingException ex) {}
        }
        return setup;
    }
    
    private static final Object[] desc2Def(final String s) throws NamingException {
        final BasicAttributes basicAttributes = new BasicAttributes(true);
        final int[] array = { 1 };
        int i = 1;
        final Attribute numericOID = readNumericOID(s, array);
        String s2 = (String)numericOID.get(0);
        basicAttributes.put(numericOID);
        skipWhitespace(s, array);
        while (i != 0) {
            final Attribute nextTag = readNextTag(s, array);
            basicAttributes.put(nextTag);
            if (nextTag.getID().equals("NAME")) {
                s2 = (String)nextTag.get(0);
            }
            skipWhitespace(s, array);
            if (array[0] >= s.length() - 1) {
                i = 0;
            }
        }
        return new Object[] { s2, basicAttributes };
    }
    
    private static final int findTrailingWhitespace(final String s, final int n) {
        for (int i = n; i > 0; --i) {
            if (s.charAt(i) != ' ') {
                return i + 1;
            }
        }
        return 0;
    }
    
    private static final void skipWhitespace(final String s, final int[] array) {
        for (int i = array[0]; i < s.length(); ++i) {
            if (s.charAt(i) != ' ') {
                array[0] = i;
                return;
            }
        }
    }
    
    private static final Attribute readNumericOID(final String s, final int[] array) throws NamingException {
        skipWhitespace(s, array);
        final int n = array[0];
        final int index = s.indexOf(32, n);
        if (index == -1 || index - n < 1) {
            throw new InvalidAttributeValueException("no numericoid found: " + s);
        }
        final String substring = s.substring(n, index);
        final int n2 = 0;
        array[n2] += substring.length();
        return new BasicAttribute("NUMERICOID", substring);
    }
    
    private static final Attribute readNextTag(final String s, final int[] array) throws NamingException {
        skipWhitespace(s, array);
        final int index = s.indexOf(32, array[0]);
        String s2;
        if (index < 0) {
            s2 = s.substring(array[0], s.length() - 1);
        }
        else {
            s2 = s.substring(array[0], index);
        }
        final String[] tag = readTag(s2, s, array);
        if (tag.length < 0) {
            throw new InvalidAttributeValueException("no values for attribute \"" + s2 + "\"");
        }
        final BasicAttribute basicAttribute = new BasicAttribute(s2, tag[0]);
        for (int i = 1; i < tag.length; ++i) {
            basicAttribute.add(tag[i]);
        }
        return basicAttribute;
    }
    
    private static final String[] readTag(final String s, final String s2, final int[] array) throws NamingException {
        final int n = 0;
        array[n] += s.length();
        skipWhitespace(s2, array);
        if (s.equals("NAME")) {
            return readQDescrs(s2, array);
        }
        if (s.equals("DESC")) {
            return readQDString(s2, array);
        }
        if (s.equals("EQUALITY") || s.equals("ORDERING") || s.equals("SUBSTR") || s.equals("SYNTAX")) {
            return readWOID(s2, array);
        }
        if (s.equals("OBSOLETE") || s.equals("ABSTRACT") || s.equals("STRUCTURAL") || s.equals("AUXILIARY") || s.equals("SINGLE-VALUE") || s.equals("COLLECTIVE") || s.equals("NO-USER-MODIFICATION")) {
            return new String[] { "true" };
        }
        if (s.equals("SUP") || s.equals("MUST") || s.equals("MAY") || s.equals("USAGE")) {
            return readOIDs(s2, array);
        }
        return readQDStrings(s2, array);
    }
    
    private static final String[] readQDString(final String s, final int[] array) throws NamingException {
        final int n = s.indexOf(39, array[0]) + 1;
        final int index = s.indexOf(39, n);
        if (n == -1 || index == -1 || n == index) {
            throw new InvalidAttributeIdentifierException("malformed QDString: " + s);
        }
        if (s.charAt(n - 1) != '\'') {
            throw new InvalidAttributeIdentifierException("qdstring has no end mark: " + s);
        }
        array[0] = index + 1;
        return new String[] { s.substring(n, index) };
    }
    
    private static final String[] readQDStrings(final String s, final int[] array) throws NamingException {
        return readQDescrs(s, array);
    }
    
    private static final String[] readQDescrs(final String s, final int[] array) throws NamingException {
        skipWhitespace(s, array);
        switch (s.charAt(array[0])) {
            case '(': {
                return readQDescrList(s, array);
            }
            case '\'': {
                return readQDString(s, array);
            }
            default: {
                throw new InvalidAttributeValueException("unexpected oids string: " + s);
            }
        }
    }
    
    private static final String[] readQDescrList(final String s, final int[] array) throws NamingException {
        final Vector vector = new Vector(5);
        final int n = 0;
        ++array[n];
        skipWhitespace(s, array);
        int i = array[0];
        final int index = s.indexOf(41, i);
        if (index == -1) {
            throw new InvalidAttributeValueException("oidlist has no end mark: " + s);
        }
        while (i < index) {
            vector.addElement(readQDString(s, array)[0]);
            skipWhitespace(s, array);
            i = array[0];
        }
        array[0] = index + 1;
        final String[] array2 = new String[vector.size()];
        for (int j = 0; j < array2.length; ++j) {
            array2[j] = (String)vector.elementAt(j);
        }
        return array2;
    }
    
    private static final String[] readWOID(final String s, final int[] array) throws NamingException {
        skipWhitespace(s, array);
        if (s.charAt(array[0]) == '\'') {
            return readQDString(s, array);
        }
        final int n = array[0];
        final int index = s.indexOf(32, n);
        if (index == -1 || n == index) {
            throw new InvalidAttributeIdentifierException("malformed OID: " + s);
        }
        array[0] = index + 1;
        return new String[] { s.substring(n, index) };
    }
    
    private static final String[] readOIDs(final String s, final int[] array) throws NamingException {
        skipWhitespace(s, array);
        if (s.charAt(array[0]) != '(') {
            return readWOID(s, array);
        }
        final Vector vector = new Vector(5);
        final int n = 0;
        ++array[n];
        skipWhitespace(s, array);
        int n2 = array[0];
        final int index = s.indexOf(41, n2);
        int n3 = s.indexOf(36, n2);
        if (index == -1) {
            throw new InvalidAttributeValueException("oidlist has no end mark: " + s);
        }
        if (n3 == -1 || index < n3) {
            n3 = index;
        }
        while (n3 < index && n3 > 0) {
            vector.addElement(s.substring(n2, findTrailingWhitespace(s, n3 - 1)));
            array[0] = n3 + 1;
            skipWhitespace(s, array);
            n2 = array[0];
            n3 = s.indexOf(36, n2);
        }
        vector.addElement(s.substring(n2, findTrailingWhitespace(s, index - 1)));
        array[0] = index + 1;
        final String[] array2 = new String[vector.size()];
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = (String)vector.elementAt(i);
        }
        return array2;
    }
    
    private final String classDef2ObjectDesc(final Attributes attributes) throws NamingException {
        final StringBuffer sb = new StringBuffer("( ");
        int n = 0;
        final Attribute value = attributes.get("NUMERICOID");
        if (value != null) {
            sb.append(this.writeNumericOID(value));
            ++n;
            final Attribute value2 = attributes.get("NAME");
            if (value2 != null) {
                sb.append(this.writeQDescrs(value2));
                ++n;
            }
            final Attribute value3 = attributes.get("DESC");
            if (value3 != null) {
                sb.append(this.writeQDString(value3));
                ++n;
            }
            final Attribute value4 = attributes.get("OBSOLETE");
            if (value4 != null) {
                sb.append(this.writeBoolean(value4));
                ++n;
            }
            final Attribute value5 = attributes.get("SUP");
            if (value5 != null) {
                sb.append(this.writeOIDs(value5));
                ++n;
            }
            final Attribute value6 = attributes.get("ABSTRACT");
            if (value6 != null) {
                sb.append(this.writeBoolean(value6));
                ++n;
            }
            final Attribute value7 = attributes.get("STRUCTURAL");
            if (value7 != null) {
                sb.append(this.writeBoolean(value7));
                ++n;
            }
            final Attribute value8 = attributes.get("AUXILIARY");
            if (value8 != null) {
                sb.append(this.writeBoolean(value8));
                ++n;
            }
            final Attribute value9 = attributes.get("MUST");
            if (value9 != null) {
                sb.append(this.writeOIDs(value9));
                ++n;
            }
            final Attribute value10 = attributes.get("MAY");
            if (value10 != null) {
                sb.append(this.writeOIDs(value10));
                ++n;
            }
            if (n < attributes.size()) {
                final NamingEnumeration<? extends Attribute> all = attributes.getAll();
                while (all.hasMoreElements()) {
                    final Attribute attribute = (Attribute)all.next();
                    final String id = attribute.getID();
                    if (!id.equals("NUMERICOID") && !id.equals("NAME") && !id.equals("SUP") && !id.equals("MAY") && !id.equals("MUST") && !id.equals("STRUCTURAL") && !id.equals("DESC") && !id.equals("AUXILIARY") && !id.equals("ABSTRACT")) {
                        if (id.equals("OBSOLETE")) {
                            continue;
                        }
                        sb.append(this.writeQDStrings(attribute));
                    }
                }
            }
            sb.append(")");
            return sb.toString();
        }
        throw new ConfigurationException("Class definition doesn'thave a numeric OID");
    }
    
    private final String attrDef2AttrDesc(final Attributes attributes) throws NamingException {
        final StringBuffer sb = new StringBuffer("( ");
        int n = 0;
        final Attribute value = attributes.get("NUMERICOID");
        if (value != null) {
            sb.append(this.writeNumericOID(value));
            ++n;
            final Attribute value2 = attributes.get("NAME");
            if (value2 != null) {
                sb.append(this.writeQDescrs(value2));
                ++n;
            }
            final Attribute value3 = attributes.get("DESC");
            if (value3 != null) {
                sb.append(this.writeQDString(value3));
                ++n;
            }
            final Attribute value4 = attributes.get("OBSOLETE");
            if (value4 != null) {
                sb.append(this.writeBoolean(value4));
                ++n;
            }
            final Attribute value5 = attributes.get("SUP");
            if (value5 != null) {
                sb.append(this.writeWOID(value5));
                ++n;
            }
            final Attribute value6 = attributes.get("EQUALITY");
            if (value6 != null) {
                sb.append(this.writeWOID(value6));
                ++n;
            }
            final Attribute value7 = attributes.get("ORDERING");
            if (value7 != null) {
                sb.append(this.writeWOID(value7));
                ++n;
            }
            final Attribute value8 = attributes.get("SUBSTR");
            if (value8 != null) {
                sb.append(this.writeWOID(value8));
                ++n;
            }
            final Attribute value9 = attributes.get("SYNTAX");
            if (value9 != null) {
                sb.append(this.writeWOID(value9));
                ++n;
            }
            final Attribute value10 = attributes.get("SINGLE-VALUE");
            if (value10 != null) {
                sb.append(this.writeBoolean(value10));
                ++n;
            }
            final Attribute value11 = attributes.get("COLLECTIVE");
            if (value11 != null) {
                sb.append(this.writeBoolean(value11));
                ++n;
            }
            final Attribute value12 = attributes.get("NO-USER-MODIFICATION");
            if (value12 != null) {
                sb.append(this.writeBoolean(value12));
                ++n;
            }
            final Attribute value13 = attributes.get("USAGE");
            if (value13 != null) {
                sb.append(this.writeQDString(value13));
                ++n;
            }
            if (n < attributes.size()) {
                final NamingEnumeration<? extends Attribute> all = attributes.getAll();
                while (all.hasMoreElements()) {
                    final Attribute attribute = (Attribute)all.next();
                    final String id = attribute.getID();
                    if (!id.equals("NUMERICOID") && !id.equals("NAME") && !id.equals("SYNTAX") && !id.equals("DESC") && !id.equals("SINGLE-VALUE") && !id.equals("EQUALITY") && !id.equals("ORDERING") && !id.equals("SUBSTR") && !id.equals("NO-USER-MODIFICATION") && !id.equals("USAGE") && !id.equals("SUP") && !id.equals("COLLECTIVE")) {
                        if (id.equals("OBSOLETE")) {
                            continue;
                        }
                        sb.append(this.writeQDStrings(attribute));
                    }
                }
            }
            sb.append(")");
            return sb.toString();
        }
        throw new ConfigurationException("Attribute type doesn'thave a numeric OID");
    }
    
    private final String syntaxDef2SyntaxDesc(final Attributes attributes) throws NamingException {
        final StringBuffer sb = new StringBuffer("( ");
        int n = 0;
        final Attribute value = attributes.get("NUMERICOID");
        if (value != null) {
            sb.append(this.writeNumericOID(value));
            ++n;
            final Attribute value2 = attributes.get("DESC");
            if (value2 != null) {
                sb.append(this.writeQDString(value2));
                ++n;
            }
            if (n < attributes.size()) {
                final NamingEnumeration<? extends Attribute> all = attributes.getAll();
                while (all.hasMoreElements()) {
                    final Attribute attribute = (Attribute)all.next();
                    final String id = attribute.getID();
                    if (!id.equals("NUMERICOID")) {
                        if (id.equals("DESC")) {
                            continue;
                        }
                        sb.append(this.writeQDStrings(attribute));
                    }
                }
            }
            sb.append(")");
            return sb.toString();
        }
        throw new ConfigurationException("Attribute type doesn'thave a numeric OID");
    }
    
    private final String matchRuleDef2MatchRuleDesc(final Attributes attributes) throws NamingException {
        final StringBuffer sb = new StringBuffer("( ");
        int n = 0;
        final Attribute value = attributes.get("NUMERICOID");
        if (value == null) {
            throw new ConfigurationException("Attribute type doesn'thave a numeric OID");
        }
        sb.append(this.writeNumericOID(value));
        ++n;
        final Attribute value2 = attributes.get("NAME");
        if (value2 != null) {
            sb.append(this.writeQDescrs(value2));
            ++n;
        }
        final Attribute value3 = attributes.get("DESC");
        if (value3 != null) {
            sb.append(this.writeQDString(value3));
            ++n;
        }
        final Attribute value4 = attributes.get("OBSOLETE");
        if (value4 != null) {
            sb.append(this.writeBoolean(value4));
            ++n;
        }
        final Attribute value5 = attributes.get("SYNTAX");
        if (value5 != null) {
            sb.append(this.writeWOID(value5));
            ++n;
            if (n < attributes.size()) {
                final NamingEnumeration<? extends Attribute> all = attributes.getAll();
                while (all.hasMoreElements()) {
                    final Attribute attribute = (Attribute)all.next();
                    final String id = attribute.getID();
                    if (!id.equals("NUMERICOID") && !id.equals("NAME") && !id.equals("SYNTAX") && !id.equals("DESC")) {
                        if (id.equals("OBSOLETE")) {
                            continue;
                        }
                        sb.append(this.writeQDStrings(attribute));
                    }
                }
            }
            sb.append(")");
            return sb.toString();
        }
        throw new ConfigurationException("Attribute type doesn'thave a syntax OID");
    }
    
    private final String writeNumericOID(final Attribute attribute) throws NamingException {
        if (attribute.size() != 1) {
            throw new InvalidAttributeValueException("A class definition must have exactly one numeric OID");
        }
        return (String)attribute.get() + ' ';
    }
    
    private final String writeWOID(final Attribute attribute) throws NamingException {
        if (this.netscapeBug) {
            return this.writeQDString(attribute);
        }
        return attribute.getID() + ' ' + attribute.get() + ' ';
    }
    
    private final String writeQDString(final Attribute attribute) throws NamingException {
        if (attribute.size() != 1) {
            throw new InvalidAttributeValueException(attribute.getID() + " must have exactly one value");
        }
        return attribute.getID() + ' ' + '\'' + attribute.get() + '\'' + ' ';
    }
    
    private final String writeQDStrings(final Attribute attribute) throws NamingException {
        return this.writeQDescrs(attribute);
    }
    
    private final String writeQDescrs(final Attribute attribute) throws NamingException {
        switch (attribute.size()) {
            case 0: {
                throw new InvalidAttributeValueException(attribute.getID() + "has no values");
            }
            case 1: {
                return this.writeQDString(attribute);
            }
            default: {
                final StringBuffer sb = new StringBuffer(attribute.getID());
                sb.append(' ');
                sb.append('(');
                final NamingEnumeration<?> all = attribute.getAll();
                while (all.hasMore()) {
                    sb.append(' ');
                    sb.append('\'');
                    sb.append((String)all.next());
                    sb.append('\'');
                    sb.append(' ');
                }
                sb.append(')');
                sb.append(' ');
                return sb.toString();
            }
        }
    }
    
    private final String writeOIDs(final Attribute attribute) throws NamingException {
        switch (attribute.size()) {
            case 0: {
                throw new InvalidAttributeValueException(attribute.getID() + "has no values");
            }
            case 1: {
                if (this.netscapeBug) {
                    break;
                }
                return this.writeWOID(attribute);
            }
        }
        final StringBuffer sb = new StringBuffer(attribute.getID());
        sb.append(' ');
        sb.append('(');
        final NamingEnumeration<?> all = attribute.getAll();
        sb.append(' ');
        sb.append(all.next());
        while (all.hasMore()) {
            sb.append(' ');
            sb.append('$');
            sb.append(' ');
            sb.append((String)all.next());
        }
        sb.append(' ');
        sb.append(')');
        sb.append(' ');
        return sb.toString();
    }
    
    private final String writeBoolean(final Attribute attribute) throws NamingException {
        return attribute.getID() + ' ';
    }
    
    final Attribute stringifyObjDesc(final Attributes attributes) throws NamingException {
        final BasicAttribute basicAttribute = new BasicAttribute("objectClasses");
        basicAttribute.add(this.classDef2ObjectDesc(attributes));
        return basicAttribute;
    }
    
    final Attribute stringifyAttrDesc(final Attributes attributes) throws NamingException {
        final BasicAttribute basicAttribute = new BasicAttribute("attributeTypes");
        basicAttribute.add(this.attrDef2AttrDesc(attributes));
        return basicAttribute;
    }
    
    final Attribute stringifySyntaxDesc(final Attributes attributes) throws NamingException {
        final BasicAttribute basicAttribute = new BasicAttribute("ldapSyntaxes");
        basicAttribute.add(this.syntaxDef2SyntaxDesc(attributes));
        return basicAttribute;
    }
    
    final Attribute stringifyMatchRuleDesc(final Attributes attributes) throws NamingException {
        final BasicAttribute basicAttribute = new BasicAttribute("matchingRules");
        basicAttribute.add(this.matchRuleDef2MatchRuleDesc(attributes));
        return basicAttribute;
    }
    
    static {
        CLASS_DEF_ATTRS = new String[] { "objectclass", "ClassDefinition" };
        ATTR_DEF_ATTRS = new String[] { "objectclass", "AttributeDefinition" };
        SYNTAX_DEF_ATTRS = new String[] { "objectclass", "SyntaxDefinition" };
        MATCHRULE_DEF_ATTRS = new String[] { "objectclass", "MatchingRule" };
    }
}
