package com.unboundid.ldif;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ResourceBundle;

enum LDIFMessages
{
    ERR_READ_CANNOT_BASE64_DECODE_ATTR("Unable to base64-decode a value for attribute {0} in the record starting at or near line number {1,number,0}:  {2}"), 
    ERR_READ_CANNOT_BASE64_DECODE_CONTROL("Unable to base64-decode the control value of the change record starting at or near line number {0,number,0}:  {1}"), 
    ERR_READ_CANNOT_BASE64_DECODE_CT("Unable to base64-decode the changetype value of the change record starting at or near line number {0,number,0}:  {1}"), 
    ERR_READ_CANNOT_BASE64_DECODE_DN("Unable to base64-decode the DN of the entry starting at or near line number {0,number,0}:  {1}"), 
    ERR_READ_CONTROL_LINE_CANNOT_BASE64_DECODE_VALUE("Unable decode a control contained in the LDIF change record starting at or near line number {0,number,0} because an error was encountered while attempting to base64-decode the control value:  {1}"), 
    ERR_READ_CONTROL_LINE_CANNOT_RETRIEVE_VALUE_FROM_URL("Unable to decode a control contained in the LDIF change record starting at or near line number {0,number,0} because an error was encountered while attempting to retrieve the value from a URL reference:  {1}"), 
    ERR_READ_CONTROL_LINE_INVALID_CRITICALITY("Unable to decode ''{0}'' as a valid control criticality in the LDIF change record starting at or near line number {1,number,0}.  The criticality must be either ''true'' or ''false''."), 
    ERR_READ_CONTROL_LINE_NO_CONTROL_VALUE("The control line of the change record starting at or near line number {0,number,0} does not have a value."), 
    ERR_READ_CR_CANNOT_BASE64_DECODE_DN("Unable to base64-decode the DN of the change record starting at or near line number {0,number,0}:  {1}"), 
    ERR_READ_CR_CT_LINE_DOESNT_START_WITH_CONTROL_OR_CT("The second line of the change record starting at or near line number {0,number,0} does not begin with either ''control'' or ''changetype''."), 
    ERR_READ_CR_DN_LINE_DOESNT_START_WITH_DN("The first line of the change record starting at or near line number {0,number,0} did not begin with ''dn:''."), 
    ERR_READ_CR_EXTRA_DELETE_DATA("Unexpected additional data after the changetype line for the delete change record starting at or near line number {0,number,0}."), 
    ERR_READ_CR_EXTRA_MODDN_DATA("Unexpected additional information at the end of the modify DN change record starting at or near line number {0,number,0}."), 
    ERR_READ_CR_INVALID_CT("Invalid changetype value ''{0}'' for the change record starting at or near line number {1,number,0}."), 
    ERR_READ_CR_NO_ATTRIBUTES("No attributes found for the change record starting at or near line number {0,number,0}."), 
    ERR_READ_CR_NO_MODS("No modifications were contained in the modify change record starting at or near line number {0,number,0}."), 
    ERR_READ_CR_NO_NEWRDN("No newRDN found for the modify DN change record starting at or near line number {0,number,0}."), 
    ERR_READ_CR_SECOND_LINE_MISSING_COLON("The second line of the change record starting at or near line number {0,number,0} does not include a colon to separate the expected ''control'' or ''changetype'' token from its corresponding value."), 
    ERR_READ_CR_TOO_SHORT("The change record starting at or near line {0,number,0} is too short to represent a valid change record."), 
    ERR_READ_CT_LINE_NO_CT_VALUE("The changetype line of the change record starting at or near line number {0,number,0} does not have a value."), 
    ERR_READ_DN_LINE_DOESNT_START_WITH_DN("The first line of the entry starting at or near line number {0,number,0} did not begin with ''dn:''."), 
    ERR_READ_DUPLICATE_VALUE("The record for entry ''{0}'' starting near line {1,number,0} contains a duplicate value for attribute ''{2}''."), 
    ERR_READ_ILLEGAL_TRAILING_SPACE_WITHOUT_DN("The LDIF record starting at or near line number {0,number,0} contains line ''{1}'' which ends with an illegal trailing space."), 
    ERR_READ_ILLEGAL_TRAILING_SPACE_WITH_DN("The LDIF record for entry ''{0}'' starting at or near line number {1,number,0} contains line ''{2}'' which ends with an illegal trailing space."), 
    ERR_READ_MODDN_CR_CANNOT_BASE64_DECODE_DELOLDRDN("Unable to base64-decode the deleteOldRDN value in the modify DN change record starting at or near line number {0,number,0}:  {1}"), 
    ERR_READ_MODDN_CR_CANNOT_BASE64_DECODE_NEWRDN("Unable to base64-decode the newRDN value in the modify DN change record starting at or near line number {0,number,0}:  {1}"), 
    ERR_READ_MODDN_CR_CANNOT_BASE64_DECODE_NEWSUPERIOR("Unable to base64-decode the newSuperior value in the modify DN change record starting at or near line number {0,number,0}:  {1}"), 
    ERR_READ_MODDN_CR_INVALID_DELOLDRDN("Invalid deleteOldRDN value ''{0}'' found in the modify DN change record starting at or near line {1,number,0}.  The value must be either 0 or 1."), 
    ERR_READ_MODDN_CR_NO_DELOLDRDN_COLON("The fourth line of the modify DN change record starting at or near line number {0,number,0} does not begin with ''deleteOldRDN:''."), 
    ERR_READ_MODDN_CR_NO_DELOLDRDN_VALUE("The deleteOldRDN line of the modify DN change record starting at or near line number {0,number,0} does not have a value."), 
    ERR_READ_MODDN_CR_NO_NEWRDN_COLON("The third line of the modify DN change record starting at or near line number {0,number,0} does not begin with ''newRDN:''."), 
    ERR_READ_MODDN_CR_NO_NEWRDN_VALUE("The newRDN line of the modify DN change record starting at or near line number {0,number,0} does not have a value."), 
    ERR_READ_MODDN_CR_NO_NEWSUPERIOR_COLON("The fifth line of the modify DN change record starting at or near line number {0,number,0} does not begin with ''newSuperior:''."), 
    ERR_READ_MOD_CR_ATTR_MISMATCH("The modify change record starting at or near line {0,number,0} has attribute name {1} where {2} was expected."), 
    ERR_READ_MOD_CR_INVALID_INCR_VALUE_COUNT("The modify change record starting at or near line {0,number,0} includes an increment for attribute {1} with an invalid number of values.  Increment changes must have exactly one value."), 
    ERR_READ_MOD_CR_INVALID_MODTYPE("Invalid modification type {0} found in the modify change record starting at or near line number {1,number,0}."), 
    ERR_READ_MOD_CR_MODTYPE_CANNOT_BASE64_DECODE_ATTR("Unable to base64-decode the attribute name in a modification type line for the modify change record starting at or near line number {0,number,0}:  {1}"), 
    ERR_READ_MOD_CR_MODTYPE_NO_ATTR("The modification type line of the modify change record starting at or near line number {0,number,0} does not include an attribute name."), 
    ERR_READ_MOD_CR_NO_ADD_VALUES("No values to add for attribute {0} in the modify change record starting at or near line number {1,number,0}."), 
    ERR_READ_MOD_CR_NO_MODTYPE("The modify change record starting at or near line {0,number,0} includes a line that does not contain a colon to separate the modification type from the attribute name."), 
    ERR_READ_NOT_CHANGE_RECORD("The LDIF record starting at or near line {0,number,0} is an entry, which cannot be converted to an LDIF change record when defaultAdd=false."), 
    ERR_READ_NO_ATTR_COLON("The record starting at or near line number {0,number,0} contains a line that does not begin with an attribute name followed by a colon."), 
    ERR_READ_NO_DATA("The provided LDIF entry did not contain any non-blank lines."), 
    ERR_READ_NO_LDIF_FILES("An LDIF reader cannot be created with an empty set of LDIF files."), 
    ERR_READ_ONLY_BLANKS("The provided LDIF content contained only blank lines."), 
    ERR_READ_UNEXPECTED_BLANK("Unexpected blank line found at or near line number {0,number,0}."), 
    ERR_READ_UNEXPECTED_FIRST_SPACE("Unexpected space found at the beginning of the first line for an LDIF entry on or near line number {0,number,0}."), 
    ERR_READ_UNEXPECTED_FIRST_SPACE_NO_NUMBER("Unexpected space found at the beginning of the first line of the LDIF entry."), 
    ERR_READ_URL_EXCEPTION("Unable to access the value for attribute {0} using URL {1} in the record starting at or near line number {2,number,0}:  {3}"), 
    ERR_READ_URL_FILE_SIZE_CHANGED("Unable retrieve the contents of URL ''{0}'' because the size of file ''{1}'' changed while it was being read."), 
    ERR_READ_URL_FILE_TOO_LARGE("Unable to retrieve the contents of URL ''{0}'' because file ''{1}'' is larger than the maximum supported size of {2,number,0} bytes."), 
    ERR_READ_URL_INVALID_SCHEME("Unable to retrieve the contents of URL ''{0}'' because this implementation only supports the file:// URL format."), 
    ERR_READ_URL_NO_SUCH_FILE("Unable to retrieve the contents of URL ''{0}'' because the file ''{1}'' does not exist."), 
    ERR_READ_VALUE_SYNTAX_VIOLATION("The record for entry ''{0}'' starting near line {1,number,0} contains a value for attribute ''{2}'' which violates the associated attribute syntax:  {3}");
    
    private static final boolean IS_WITHIN_UNIT_TESTS;
    private static final ResourceBundle RESOURCE_BUNDLE;
    private static final ConcurrentHashMap<LDIFMessages, String> MESSAGE_STRINGS;
    private static final ConcurrentHashMap<LDIFMessages, MessageFormat> MESSAGES;
    private final String defaultText;
    
    private LDIFMessages(final String defaultText) {
        this.defaultText = defaultText;
    }
    
    public String get() {
        String s = LDIFMessages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (LDIFMessages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = LDIFMessages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                LDIFMessages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        if (LDIFMessages.IS_WITHIN_UNIT_TESTS && (s.contains("{0}") || s.contains("{0,number,0}") || s.contains("{1}") || s.contains("{1,number,0}") || s.contains("{2}") || s.contains("{2,number,0}") || s.contains("{3}") || s.contains("{3,number,0}") || s.contains("{4}") || s.contains("{4,number,0}") || s.contains("{5}") || s.contains("{5,number,0}") || s.contains("{6}") || s.contains("{6,number,0}") || s.contains("{7}") || s.contains("{7,number,0}") || s.contains("{8}") || s.contains("{8,number,0}") || s.contains("{9}") || s.contains("{9,number,0}") || s.contains("{10}") || s.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + s);
        }
        return s;
    }
    
    public String get(final Object... args) {
        MessageFormat f = LDIFMessages.MESSAGES.get(this);
        if (f == null) {
            if (LDIFMessages.RESOURCE_BUNDLE == null) {
                f = new MessageFormat(this.defaultText);
            }
            else {
                try {
                    f = new MessageFormat(LDIFMessages.RESOURCE_BUNDLE.getString(this.name()));
                }
                catch (final Exception e) {
                    f = new MessageFormat(this.defaultText);
                }
            }
            LDIFMessages.MESSAGES.putIfAbsent(this, f);
        }
        final String formattedMessage;
        synchronized (f) {
            formattedMessage = f.format(args);
        }
        if (LDIFMessages.IS_WITHIN_UNIT_TESTS && (formattedMessage.contains("{0}") || formattedMessage.contains("{0,number,0}") || formattedMessage.contains("{1}") || formattedMessage.contains("{1,number,0}") || formattedMessage.contains("{2}") || formattedMessage.contains("{2,number,0}") || formattedMessage.contains("{3}") || formattedMessage.contains("{3,number,0}") || formattedMessage.contains("{4}") || formattedMessage.contains("{4,number,0}") || formattedMessage.contains("{5}") || formattedMessage.contains("{5,number,0}") || formattedMessage.contains("{6}") || formattedMessage.contains("{6,number,0}") || formattedMessage.contains("{7}") || formattedMessage.contains("{7,number,0}") || formattedMessage.contains("{8}") || formattedMessage.contains("{8,number,0}") || formattedMessage.contains("{9}") || formattedMessage.contains("{9,number,0}") || formattedMessage.contains("{10}") || formattedMessage.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + formattedMessage);
        }
        return f.format(args);
    }
    
    @Override
    public String toString() {
        String s = LDIFMessages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (LDIFMessages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = LDIFMessages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                LDIFMessages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        return s;
    }
    
    static {
        IS_WITHIN_UNIT_TESTS = (Boolean.getBoolean("com.unboundid.ldap.sdk.RunningUnitTests") || Boolean.getBoolean("com.unboundid.directory.server.RunningUnitTests"));
        ResourceBundle rb = null;
        try {
            rb = ResourceBundle.getBundle("unboundid-ldapsdk-ldif");
        }
        catch (final Exception ex) {}
        RESOURCE_BUNDLE = rb;
        MESSAGE_STRINGS = new ConcurrentHashMap<LDIFMessages, String>(100);
        MESSAGES = new ConcurrentHashMap<LDIFMessages, MessageFormat>(100);
    }
}
