package com.unboundid.ldap.sdk.schema;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ResourceBundle;

enum SchemaMessages
{
    ERR_ATTRSYNTAX_DECODE_CLOSE_NOT_AT_END("Unable to decode the provided string ''{0}'' as an attribute syntax because the closing parenthesis was not at the end of the string."), 
    ERR_ATTRSYNTAX_DECODE_DUP_EXT("Unable to decode the provided string ''{0}'' as an attribute syntax because it included multiple occurrences of the ''{1}'' extension."), 
    ERR_ATTRSYNTAX_DECODE_EMPTY("An empty string cannot be decoded as an attribute syntax."), 
    ERR_ATTRSYNTAX_DECODE_MULTIPLE_DESC("Unable to decode the provided string ''{0}'' as an attribute syntax because it included multiple DESC elements."), 
    ERR_ATTRSYNTAX_DECODE_NO_OPENING_PAREN("Unable to decode the provided string ''{0}'' as an attribute syntax definition because it does not start with an opening parenthesis."), 
    ERR_ATTRSYNTAX_DECODE_UNEXPECTED_TOKEN("Unable to decode the provided string ''{0}'' as an attribute syntax because it included an unexpected token ''{1}''."), 
    ERR_ATTRTYPE_DECODE_CLOSE_NOT_AT_END("Unable to decode the provided string ''{0}'' as an attribute type because the closing parenthesis was not at the end of the string."), 
    ERR_ATTRTYPE_DECODE_DUP_EXT("Unable to decode the provided string ''{0}'' as an attribute type because it included multiple occurrences of the ''{1}'' extension."), 
    ERR_ATTRTYPE_DECODE_EMPTY("An empty string cannot be decoded as an attribute type."), 
    ERR_ATTRTYPE_DECODE_INVALID_USAGE("Unable to decode the provided string ''{0}'' as an attribute type because it included an invalid attribute usage of ''{1}''."), 
    ERR_ATTRTYPE_DECODE_MULTIPLE_ELEMENTS("Unable to decode the provided string ''{0}'' as an attribute type because it included multiple occurrences of the {1} element."), 
    ERR_ATTRTYPE_DECODE_NO_OPENING_PAREN("Unable to decode the provided string ''{0}'' as an attribute type definition because it does not start with an opening parenthesis."), 
    ERR_ATTRTYPE_DECODE_UNEXPECTED_TOKEN("Unable to decode the provided string ''{0}'' as an attribute type because it included an unexpected token ''{1}''."), 
    ERR_DCR_DECODE_CLOSE_NOT_AT_END("Unable to decode the provided string ''{0}'' as a DIT content rule because the closing parenthesis was not at the end of the string."), 
    ERR_DCR_DECODE_DUP_EXT("Unable to decode the provided string ''{0}'' as a DIT content rule because it included multiple occurrences of the ''{1}'' extension."), 
    ERR_DCR_DECODE_EMPTY("An empty string cannot be decoded as a DIT content rule."), 
    ERR_DCR_DECODE_MULTIPLE_ELEMENTS("Unable to decode the provided string ''{0}'' as a DIT content rule because it included multiple occurrences of the {1} element."), 
    ERR_DCR_DECODE_NO_OPENING_PAREN("Unable to decode the provided string ''{0}'' as a DIT content rule definition because it does not start with an opening parenthesis."), 
    ERR_DSR_DECODE_CLOSE_NOT_AT_END("Unable to decode the provided string ''{0}'' as a DIT structure rule because the closing parenthesis was not at the end of the string."), 
    ERR_DSR_DECODE_DUP_EXT("Unable to decode the provided string ''{0}'' as a DIT structure rule because it included multiple occurrences of the ''{1}'' extension."), 
    ERR_DSR_DECODE_EMPTY("An empty string cannot be decoded as a DIT structure rule."), 
    ERR_DSR_DECODE_MULTIPLE_ELEMENTS("Unable to decode the provided string ''{0}'' as a DIT structure rule because it included multiple occurrences of the {1} element."), 
    ERR_DSR_DECODE_NO_FORM("Unable to decode the provided string ''{0}'' as a DIT structure rule because dit not include the required FORM element."), 
    ERR_DSR_DECODE_NO_OPENING_PAREN("Unable to decode the provided string ''{0}'' as a DIT structure rule definition because it does not start with an opening parenthesis."), 
    ERR_DSR_DECODE_RULE_ID_NOT_INT("Unable to decode the provided string ''{0}'' as a DIT structure rule because the rule ID could not be parsed as an integer."), 
    ERR_DSR_DECODE_SUP_ID_NOT_INT("Unable to decode the provided string ''{0}'' as a DIT structure rule because one of the superior rule IDs could not be parsed as an integer."), 
    ERR_DSR_DECODE_UNEXPECTED_TOKEN("Unable to decode the provided string ''{0}'' as a DIT structure rule because it included an unexpected token ''{1}''."), 
    ERR_ENTRY_ATTR_HAS_MULTIPLE_VALUES("The entry contains multiple values for attribute {0} which is defined as single-valued in the schema."), 
    ERR_ENTRY_ATTR_INVALID_SYNTAX("The entry contains value ''{0}'' for attribute {1} which violates the constraints of the associated attribute syntax:  {2}"), 
    ERR_ENTRY_ATTR_NOT_ALLOWED("The entry contains attribute {0} which is not allowed by its object classes and/or DIT content rule."), 
    ERR_ENTRY_ATTR_VALUE_INT_TOO_LARGE("The entry contains value {0,number,0} for attribute {1} that is larger than the maximum allowed value of {2,number,0} specified by the X-MAX-INT-VALUE extension."), 
    ERR_ENTRY_ATTR_VALUE_INT_TOO_SMALL("The entry contains value {0,number,0} for attribute {1} that is smaller than the minimum allowed value of {2,number,0} specified by the X-MIN-INT-VALUE extension."), 
    ERR_ENTRY_ATTR_VALUE_LONGER_THAN_MAX_LENGTH("The entry contains value ''{0}'' for attribute {1} that is longer than the maximum length of {2,number,0} characters specified by the X-MAX-VALUE-LENGTH attribute type extension."), 
    ERR_ENTRY_ATTR_VALUE_NOT_ALLOWED("The entry contains value ''{0}'' for attribute {1} that is not in the set of allowed values defined in the X-ALLOWED-VALUE attribute type extension."), 
    ERR_ENTRY_ATTR_VALUE_NOT_ALLOWED_BY_REGEX("The entry contains value ''{0}'' for attribute {1} that does not match a regular expression defined in the X-VALUE-REGEX attribute type extension."), 
    ERR_ENTRY_ATTR_VALUE_NOT_INT("The entry contains value ''{0}'' for attribute {1} that cannot be parsed as an integer, but that is expected to be an integer because the attribute type definition uses the {2} extension."), 
    ERR_ENTRY_ATTR_VALUE_SHORTER_THAN_MIN_LENGTH("The entry contains value ''{0}'' for attribute {1} that is shorter than the minimum length of {2,number,0} characters specified by the X-MIN-VALUE-LENGTH attribute type extension."), 
    ERR_ENTRY_AUX_CLASS_NOT_ALLOWED("The entry contains auxiliary object class {0} which is not allowed by the associated DIT content rule."), 
    ERR_ENTRY_INVALID_ABSTRACT_CLASS("The entry contains abstract object class {0} that is not subclassed by any of the structural or auxiliary object classes included in the entry."), 
    ERR_ENTRY_MALFORMED_DN("The entry contains a malformed DN:  {0}"), 
    ERR_ENTRY_MISSING_RDN_VALUE("The entry''s RDN contains value ''{0}'' for attribute ''{1}'' that is not present in the set of entry attributes."), 
    ERR_ENTRY_MISSING_REQUIRED_ATTR("The entry is missing required attribute {0}."), 
    ERR_ENTRY_MISSING_SUP_OC("The entry is missing object class {0} which is the superior class for the {1} object class.  Many servers will allow this when adding or importing data, but it may cause problems in other servers."), 
    ERR_ENTRY_MULTIPLE_STRUCTURAL_CLASSES("The entry has more than one structural object class ({0})."), 
    ERR_ENTRY_NO_OCS("The entry does not have any object classes."), 
    ERR_ENTRY_NO_STRUCTURAL_CLASS("The entry does not have a structural object class."), 
    ERR_ENTRY_RDN_ATTR_NOT_ALLOWED_BY_NF("The entry''s RDN contains attribute {0} which is not allowed by the associated name form."), 
    ERR_ENTRY_RDN_ATTR_NOT_ALLOWED_IN_ENTRY("The entry''s RDN contains attribute {0} which is not allowed to be included in the entry."), 
    ERR_ENTRY_RDN_ATTR_NOT_DEFINED("The entry''s RDN contains attribute {0} which is not defined in the schema."), 
    ERR_ENTRY_RDN_MISSING_REQUIRED_ATTR("The entry''s RDN does not contain attribute {0} which is required by the associated name form."), 
    ERR_ENTRY_TOO_FEW_VALUES("The entry contains {0,number,0} values for attribute {1}, which is smaller than the minimum of {2,number,0} specified by the X-MIN-VALUE-COUNT attribute type extension."), 
    ERR_ENTRY_TOO_MANY_VALUES("The entry contains {0,number,0} values for attribute {1}, which is larger than the maximum of {2,number,0} specified by the X-MAX-VALUE-COUNT attribute type extension."), 
    ERR_ENTRY_UNDEFINED_ATTR("The entry contains attribute {0} which is not defined in the schema."), 
    ERR_ENTRY_UNDEFINED_OC("The entry contains object class {0} which is not defined in the schema."), 
    ERR_ENTRY_UNDEFINED_SUP_OC("Object class {0} references superior class {1} which is not defined in the schema."), 
    ERR_MRU_DECODE_CLOSE_NOT_AT_END("Unable to decode the provided string ''{0}'' as a matching rule use because the closing parenthesis was not at the end of the string."), 
    ERR_MRU_DECODE_DUP_EXT("Unable to decode the provided string ''{0}'' as a matching rule use because it included multiple occurrences of the ''{1}'' extension."), 
    ERR_MRU_DECODE_EMPTY("An empty string cannot be decoded as a matching rule use."), 
    ERR_MRU_DECODE_MULTIPLE_ELEMENTS("Unable to decode the provided string ''{0}'' as a matching rule use because it included multiple occurrences of the {1} element."), 
    ERR_MRU_DECODE_NO_APPLIES("Unable to decode the provided string ''{0}'' as a matching rule use because it did not include any applicable attribute types."), 
    ERR_MRU_DECODE_NO_OPENING_PAREN("Unable to decode the provided string ''{0}'' as a matching rule use definition because it does not start with an opening parenthesis."), 
    ERR_MRU_DECODE_UNEXPECTED_TOKEN("Unable to decode the provided string ''{0}'' as a matching rule use because it included an unexpected token ''{1}''."), 
    ERR_MR_DECODE_CLOSE_NOT_AT_END("Unable to decode the provided string ''{0}'' as a matching rule because the closing parenthesis was not at the end of the string."), 
    ERR_MR_DECODE_DUP_EXT("Unable to decode the provided string ''{0}'' as a matching rule because it included multiple occurrences of the ''{1}'' extension."), 
    ERR_MR_DECODE_EMPTY("An empty string cannot be decoded as a matching rule."), 
    ERR_MR_DECODE_MULTIPLE_ELEMENTS("Unable to decode the provided string ''{0}'' as a matching rule because it included multiple occurrences of the {1} element."), 
    ERR_MR_DECODE_NO_OPENING_PAREN("Unable to decode the provided string ''{0}'' as a matching rule definition because it does not start with an opening parenthesis."), 
    ERR_MR_DECODE_NO_SYNTAX("Unable to decode the provided string ''{0}'' as a matching rule because it did not include a syntax OID."), 
    ERR_MR_DECODE_UNEXPECTED_TOKEN("Unable to decode the provided string ''{0}'' as a matching rule because it included an unexpected token ''{1}''."), 
    ERR_NF_DECODE_CLOSE_NOT_AT_END("Unable to decode the provided string ''{0}'' as a name form because the closing parenthesis was not at the end of the string."), 
    ERR_NF_DECODE_DUP_EXT("Unable to decode the provided string ''{0}'' as a name form because it included multiple occurrences of the ''{1}'' extension."), 
    ERR_NF_DECODE_EMPTY("An empty string cannot be decoded as a name form."), 
    ERR_NF_DECODE_MULTIPLE_ELEMENTS("Unable to decode the provided string ''{0}'' as a name form because it included multiple occurrences of the {1} element."), 
    ERR_NF_DECODE_NO_MUST("Unable to decode the provided string ''{0}'' as a name form because it did not include any required attributes."), 
    ERR_NF_DECODE_NO_OC("Unable to decode the provided string ''{0}'' as a name form because it did not include a structural object class."), 
    ERR_NF_DECODE_NO_OPENING_PAREN("Unable to decode the provided string ''{0}'' as a name form definition because it does not start with an opening parenthesis."), 
    ERR_NF_DECODE_UNEXPECTED_TOKEN("Unable to decode the provided string ''{0}'' as a name form because it included an unexpected token ''{1}''."), 
    ERR_OC_DECODE_CLOSE_NOT_AT_END("Unable to decode the provided string ''{0}'' as an object class because the closing parenthesis was not at the end of the string."), 
    ERR_OC_DECODE_DUP_EXT("Unable to decode the provided string ''{0}'' as an object class because it included multiple occurrences of the ''{1}'' extension."), 
    ERR_OC_DECODE_EMPTY("An empty string cannot be decoded as an object class."), 
    ERR_OC_DECODE_MULTIPLE_ELEMENTS("Unable to decode the provided string ''{0}'' as an object class because it included multiple occurrences of the {1} element."), 
    ERR_OC_DECODE_MULTIPLE_OC_TYPES("Unable to decode the provided string ''{0}'' as an object class because it included multiple object class type elements."), 
    ERR_OC_DECODE_NO_OPENING_PAREN("Unable to decode the provided string ''{0}'' as an object class definition because it does not start with an opening parenthesis."), 
    ERR_OC_DECODE_UNEXPECTED_TOKEN("Unable to decode the provided string ''{0}'' as an object class because it included an unexpected token ''{1}''."), 
    ERR_SCHEMA_CANNOT_LOAD_DEFAULT_DEFINITIONS("An error occurred while attempting to load or parse a default set of standard schema elements:  {0}"), 
    ERR_SCHEMA_ELEM_EMPTY_OID("Unable to parse string ''{0}'' as a schema element because an empty string was founded where a schema element name or OID was expected."), 
    ERR_SCHEMA_ELEM_EMPTY_OID_LIST("Unable to parse string ''{0}'' as a schema element because it contained an empty OID list."), 
    ERR_SCHEMA_ELEM_EMPTY_QUOTES("Unable to parse string ''{0}'' as a schema element because it contained an empty quoted string."), 
    ERR_SCHEMA_ELEM_EMPTY_STRING_LIST("Unable to parse string ''{0}'' as a schema element because it contained an empty quoted-string list."), 
    ERR_SCHEMA_ELEM_ENDS_WITH_BACKSLASH("Unable to parse string ''{0}'' as a schema element because the string ended with a backslash."), 
    ERR_SCHEMA_ELEM_EXPECTED_QUOTE_OR_PAREN("Unable to parse string ''{0}'' as a schema element because either a single quote or a closing parenthesis was expected at position {1,number,0}."), 
    ERR_SCHEMA_ELEM_EXPECTED_SINGLE_QUOTE("Unable to parse string ''{0}''  as a schema element because a single quote was expected at position {1,number,0}."), 
    ERR_SCHEMA_ELEM_INVALID_HEX_CHAR("Unable to parse string ''{0}'' as a schema element because it contained an invalid hex character ''{1}'' at position {2,number,0}."), 
    ERR_SCHEMA_ELEM_MISSING_HEX_CHAR("Unable to parse string ''{0}'' as a schema element because two hex digits were expected after a backslash but only one was found."), 
    ERR_SCHEMA_ELEM_NO_CLOSING_PAREN("Unable to parse string ''{0}'' as a schema element because the end of the string was reached without finding an expected space after a closing single quote."), 
    ERR_SCHEMA_ELEM_NO_SPACE_AFTER_OID("Unable to parse string ''{0}'' as a schema element because the end of the string was reached before finding a space to mark the end of an element name or OID."), 
    ERR_SCHEMA_ELEM_NO_SPACE_AFTER_OID_LIST("Unable to parse string ''{0}'' as a schema element because the end of the string was reached without finding an expected space after an OID list."), 
    ERR_SCHEMA_ELEM_NO_SPACE_AFTER_QUOTE("Unable to parse string ''{0}'' as a schema element because the end of the string was reached without finding an expected space after a closing single quote."), 
    ERR_SCHEMA_ELEM_SKIP_SPACES_NO_CLOSE_PAREN("Unable to parse string ''{0}'' as a schema element because the end of the string was reached while skipping over spaces and not finding a closing parenthesis."), 
    ERR_SCHEMA_ELEM_UNEXPECTED_CHAR_IN_OID("Unable to parse string ''{0}'' as a schema element because an unexpected character was found at position {1,number,0} while attempting to read an element name or OID."), 
    ERR_SCHEMA_ELEM_UNEXPECTED_CHAR_IN_OID_LIST("Unable to parse string ''{0}'' as a schema element because it contained an unexpected character at position {1,number,0} in an OID list."), 
    ERR_SCHEMA_UNPARSABLE_AS("Unable to parse {0} value {1} as an attribute syntax definition:  {2}"), 
    ERR_SCHEMA_UNPARSABLE_AT("Unable to parse {0} value {1} as an attribute type definition:  {2}"), 
    ERR_SCHEMA_UNPARSABLE_DCR("Unable to parse {0} value {1} as a DIT content rule definition:  {2}"), 
    ERR_SCHEMA_UNPARSABLE_DSR("Unable to parse {0} value {1} as a DIT structure rule definition:  {2}"), 
    ERR_SCHEMA_UNPARSABLE_MR("Unable to parse {0} value {1} as a matching rule definition:  {2}"), 
    ERR_SCHEMA_UNPARSABLE_MRU("Unable to parse {0} value {1} as a matching rule use definition:  {2}"), 
    ERR_SCHEMA_UNPARSABLE_NF("Unable to parse {0} value {1} as a name form definition:  {2}"), 
    ERR_SCHEMA_UNPARSABLE_OC("Unable to parse {0} value {1} as an object class definition:  {2}"), 
    INFO_ENTRY_INVALID_ENTRY_COUNT("{0,number,0} of {1,number,0} entries ({2,number,0} percent) were found to be invalid."), 
    INFO_ENTRY_MALFORMED_DN_COUNT("{0,number,0} of {1,number,0} entries ({2,number,0} percent) had malformed DNs."), 
    INFO_ENTRY_MISSING_ATTR_COUNT("{0,number,0} required attributes were missing from entries."), 
    INFO_ENTRY_MISSING_ATTR_NAME_COUNT("Required attribute {0} was found to be missing {1,number,0} times."), 
    INFO_ENTRY_MISSING_RDN_VALUE_COUNT("{0,number,0} of {1,number,0} entries ({2,number,0} percent) had attribute values present in their RDNs that were not present in the set of entry attributes."), 
    INFO_ENTRY_MISSING_SUPERIOR_OC_COUNT("{0,number,0} missing superior object classes were found in entries."), 
    INFO_ENTRY_MULTIPLE_STRUCTURAL_OCS_COUNT("{0,number,0} of {1,number,0} entries ({2,number,0} percent) had multiple structural object classes."), 
    INFO_ENTRY_NF_VIOLATION_COUNT("{0,number,0} of {1,number,0} entries ({2,number,0} percent) had RDNs which violated the associated name form."), 
    INFO_ENTRY_NO_OC_COUNT("{0,number,0} of {1,number,0} entries ({2,number,0} percent) did not have any object classes."), 
    INFO_ENTRY_NO_STRUCTURAL_OC_COUNT("{0,number,0} of {1,number,0} entries ({2,number,0} percent) did not have a structural object class."), 
    INFO_ENTRY_PROHIBITED_ATTR_COUNT("{0,number,0} prohibited attributes were found in entries."), 
    INFO_ENTRY_PROHIBITED_ATTR_NAME_COUNT("Prohibited attribute {0} was encountered {1,number,0} times."), 
    INFO_ENTRY_PROHIBITED_OC_COUNT("{0,number,0} prohibited object classes were found in entries."), 
    INFO_ENTRY_PROHIBITED_OC_NAME_COUNT("Prohibited object class {0} was encountered {1,number,0} times."), 
    INFO_ENTRY_SINGLE_VALUE_VIOLATION_COUNT("{0,number,0} single-valued attributes were found with multiple values."), 
    INFO_ENTRY_SINGLE_VALUE_VIOLATION_NAME_COUNT("Single-valued attribute {0} was found to have multiple values in {1,number,0} entries."), 
    INFO_ENTRY_SYNTAX_VIOLATION_COUNT("{0,number,0} attribute values were found which violated the associated attribute syntax."), 
    INFO_ENTRY_SYNTAX_VIOLATION_NAME_COUNT("Attribute {0} was found to have {1,number,0} invalid values."), 
    INFO_ENTRY_UNDEFINED_ATTR_COUNT("{0,number,0} undefined attributes were encountered."), 
    INFO_ENTRY_UNDEFINED_ATTR_NAME_COUNT("Undefined attribute {0} was encountered {1,number,0} times."), 
    INFO_ENTRY_UNDEFINED_OC_COUNT("{0,number,0} undefined object classes were encountered."), 
    INFO_ENTRY_UNDEFINED_OC_NAME_COUNT("Undefined object class {0} was encountered {1,number,0} times.");
    
    private static final boolean IS_WITHIN_UNIT_TESTS;
    private static final ResourceBundle RESOURCE_BUNDLE;
    private static final ConcurrentHashMap<SchemaMessages, String> MESSAGE_STRINGS;
    private static final ConcurrentHashMap<SchemaMessages, MessageFormat> MESSAGES;
    private final String defaultText;
    
    private SchemaMessages(final String defaultText) {
        this.defaultText = defaultText;
    }
    
    public String get() {
        String s = SchemaMessages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (SchemaMessages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = SchemaMessages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                SchemaMessages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        if (SchemaMessages.IS_WITHIN_UNIT_TESTS && (s.contains("{0}") || s.contains("{0,number,0}") || s.contains("{1}") || s.contains("{1,number,0}") || s.contains("{2}") || s.contains("{2,number,0}") || s.contains("{3}") || s.contains("{3,number,0}") || s.contains("{4}") || s.contains("{4,number,0}") || s.contains("{5}") || s.contains("{5,number,0}") || s.contains("{6}") || s.contains("{6,number,0}") || s.contains("{7}") || s.contains("{7,number,0}") || s.contains("{8}") || s.contains("{8,number,0}") || s.contains("{9}") || s.contains("{9,number,0}") || s.contains("{10}") || s.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + s);
        }
        return s;
    }
    
    public String get(final Object... args) {
        MessageFormat f = SchemaMessages.MESSAGES.get(this);
        if (f == null) {
            if (SchemaMessages.RESOURCE_BUNDLE == null) {
                f = new MessageFormat(this.defaultText);
            }
            else {
                try {
                    f = new MessageFormat(SchemaMessages.RESOURCE_BUNDLE.getString(this.name()));
                }
                catch (final Exception e) {
                    f = new MessageFormat(this.defaultText);
                }
            }
            SchemaMessages.MESSAGES.putIfAbsent(this, f);
        }
        final String formattedMessage;
        synchronized (f) {
            formattedMessage = f.format(args);
        }
        if (SchemaMessages.IS_WITHIN_UNIT_TESTS && (formattedMessage.contains("{0}") || formattedMessage.contains("{0,number,0}") || formattedMessage.contains("{1}") || formattedMessage.contains("{1,number,0}") || formattedMessage.contains("{2}") || formattedMessage.contains("{2,number,0}") || formattedMessage.contains("{3}") || formattedMessage.contains("{3,number,0}") || formattedMessage.contains("{4}") || formattedMessage.contains("{4,number,0}") || formattedMessage.contains("{5}") || formattedMessage.contains("{5,number,0}") || formattedMessage.contains("{6}") || formattedMessage.contains("{6,number,0}") || formattedMessage.contains("{7}") || formattedMessage.contains("{7,number,0}") || formattedMessage.contains("{8}") || formattedMessage.contains("{8,number,0}") || formattedMessage.contains("{9}") || formattedMessage.contains("{9,number,0}") || formattedMessage.contains("{10}") || formattedMessage.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + formattedMessage);
        }
        return f.format(args);
    }
    
    @Override
    public String toString() {
        String s = SchemaMessages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (SchemaMessages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = SchemaMessages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                SchemaMessages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        return s;
    }
    
    static {
        IS_WITHIN_UNIT_TESTS = (Boolean.getBoolean("com.unboundid.ldap.sdk.RunningUnitTests") || Boolean.getBoolean("com.unboundid.directory.server.RunningUnitTests"));
        ResourceBundle rb = null;
        try {
            rb = ResourceBundle.getBundle("unboundid-ldapsdk-schema");
        }
        catch (final Exception ex) {}
        RESOURCE_BUNDLE = rb;
        MESSAGE_STRINGS = new ConcurrentHashMap<SchemaMessages, String>(100);
        MESSAGES = new ConcurrentHashMap<SchemaMessages, MessageFormat>(100);
    }
}
