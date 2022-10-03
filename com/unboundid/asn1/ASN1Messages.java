package com.unboundid.asn1;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ResourceBundle;

enum ASN1Messages
{
    ERR_BIG_INTEGER_DECODE_EMPTY_VALUE("Invalid length of zero bytes for an ASN.1 big integer element."), 
    ERR_BIT_STRING_DECODE_EMPTY_VALUE("Unable to decode an ASN.1 bit string element because the element value is empty."), 
    ERR_BIT_STRING_DECODE_INVALID_PADDING_BIT_COUNT("Unable to decode an ASN.1 bit string element because the first byte of the value, which is used to indicate the number of padding bits needed in the last byte, has an invalid value of {0,number,0}.  The value must be between 0 and 7, inclusive."), 
    ERR_BIT_STRING_DECODE_NONZERO_PADDING_BIT_COUNT_WITH_NO_MORE_BYTES("Unable to decode an ASN.1 bit string element because the first byte of the value, which is used to indicate the number of padding bits needed in the last byte, has a nonzero value, but the value does not have any more bytes."), 
    ERR_BIT_STRING_DECODE_STRING_INVALID_CHAR("Unable to parse the provided string as a bit string because the string is not comprised entirely of the characters '1' and '0'."), 
    ERR_BIT_STRING_GET_BYTES_NOT_MULTIPLE_OF_EIGHT_BITS("Unable to get the bit string value as a byte array because the bit string contains {0,number,0} bits, which is not a multiple of eight."), 
    ERR_BOOLEAN_INVALID_LENGTH("ASN.1 Boolean elements must have a value whose length is exactly one byte."), 
    ERR_ELEMENT_DECODE_EXCEPTION("Unable to decode the provided byte array as an ASN.1 BER element:  {0}"), 
    ERR_ELEMENT_LENGTH_MISMATCH("The decoded length of {0,number,0} does not match the number of bytes remaining in the provided array ({1,number,0})."), 
    ERR_ENUMERATED_INVALID_LENGTH("Invalid value length of {0,number,0} for an ASN.1 enumerated element.  Enumerated element values must have a length between 1 and 4 bytes."), 
    ERR_GENERALIZED_TIME_STRING_CHAR_NOT_DIGIT("Unable to parse the provided string as an ASN.1 generalized time value because the character at position {0,number,0} is not a digit."), 
    ERR_GENERALIZED_TIME_STRING_CHAR_NOT_PERIOD("Unable to parse the provided string as an ASN.1 generalized time value because the character at position {0,number,0} is neither a decimal point (to separate the seconds component from a sub-second component) or a 'Z' (to indicate that the timestamp is in the UTC time zone)."), 
    ERR_GENERALIZED_TIME_STRING_DOES_NOT_END_WITH_Z("Unable to parse the provided string as an ASN.1 generalized time value because the string does not end with 'Z' to indicate that the timestamp is in the UTC time zone."), 
    ERR_GENERALIZED_TIME_STRING_INVALID_DAY("Unable to parse the provided string as an ASN.1 generalized time value because the day-of-month component is not between 1 and 31, inclusive."), 
    ERR_GENERALIZED_TIME_STRING_INVALID_HOUR("Unable to parse the provided string as an ASN.1 generalized time value because the hour component is not between 0 and 23, inclusive."), 
    ERR_GENERALIZED_TIME_STRING_INVALID_MINUTE("Unable to parse the provided string as an ASN.1 generalized time value because the minute component is not between 0 and 59, inclusive."), 
    ERR_GENERALIZED_TIME_STRING_INVALID_MONTH("Unable to parse the provided string as an ASN.1 generalized time value because the month component is not between 1 and 12, inclusive."), 
    ERR_GENERALIZED_TIME_STRING_INVALID_SECOND("Unable to parse the provided string as an ASN.1 generalized time value because the second component is not between 0 and 60, inclusive."), 
    ERR_GENERALIZED_TIME_STRING_TOO_SHORT("Unable to parse the provided string as an ASN.1 generalized time value because the string is shorter than the minimum valid length of 15 characters."), 
    ERR_IA5_STRING_DECODE_VALUE_NOT_IA5("Unable to create an ASN.1 IA5 string with the provided value because the value contains one or more non-ASCII characters."), 
    ERR_INTEGER_INVALID_LENGTH("Invalid value length of {0,number,0} for an ASN.1 integer element.  Integer element values must have a length between 1 and 4 bytes."), 
    ERR_LONG_INVALID_LENGTH("Invalid value length of {0,number,0} for an ASN.1 long element.  Long element values must have a length between 1 and 8 bytes."), 
    ERR_NULL_HAS_VALUE("ASN.1 null elements must not have a value."), 
    ERR_NUMERIC_STRING_DECODE_VALUE_NOT_NUMERIC("Unable to create an ASN.1 numeric string with the provided value because the value contains one or more characters that are not allowed in numeric strings.  A numeric string must only contain ASCII numeric digits or the ASCII space character."), 
    ERR_OID_DECODE_EMPTY_VALUE("Unable to decode the provided ASN.1 element as an object identifier because the element value is empty."), 
    ERR_OID_DECODE_INCOMPLETE_VALUE("Unable to decode the provided ASN.1 element as an object identifier because the last byte of the encoded value has its most significant bit set to one, which indicates that there should be at least one more byte of data."), 
    ERR_OID_ENCODE_INVALID_FIRST_COMPONENT("Unable to parse string ''{0}'' as a valid OID because the first component has a value of {1,number,0} but the first component of an OID can only be 0, 1, or 2."), 
    ERR_OID_ENCODE_INVALID_SECOND_COMPONENT("Unable to parse string ''{0}'' as a valid OID because the first component has a value of {1,number,0} and the second component has a value of {2,number,0}.  If the value of the first component is 0 or 1, then the value of the second component must be between 0 and 39, inclusive."), 
    ERR_OID_ENCODE_NOT_ENOUGH_COMPONENTS("Unable to parse string ''{0}'' as a valid OID because it does not have at least two components"), 
    ERR_OID_ENCODE_NOT_NUMERIC("The provided object identifier is not a valid numeric OID"), 
    ERR_PRINTABLE_STRING_DECODE_VALUE_NOT_PRINTABLE("Unable to create an ASN.1 printable string with the provided value because the value contains one or more characters that are not in the set of printable characters.  A printable string may contain ASCII characters from the following set:  all uppercase and lowercase letters, all digits, space, apostrophe, open and close parentheses, plus sign, minus sign, comma, period, forward slash, colon, equal sign, and question mark."), 
    ERR_READ_END_BEFORE_FIRST_LENGTH("The end of the input stream was reached before the first length byte could be read."), 
    ERR_READ_END_BEFORE_LENGTH_END("The end of the input stream was reached before the full length could be read."), 
    ERR_READ_END_BEFORE_VALUE_END("The end of the input stream was reached before the full value could be read."), 
    ERR_READ_LENGTH_EXCEEDS_MAX("The element indicated that it required {0,number,0} bytes to hold the value, but this is larger than the maximum of {1,number,0} bytes that the client has been configured to accept."), 
    ERR_READ_LENGTH_TOO_LONG("The element indicated that it required {0,number,0} bytes to encode the multi-byte length, but multi-byte lengths must be encoded in 1 to 4 bytes."), 
    ERR_READ_SASL_LENGTH_EXCEEDS_MAX("The SASL client indicated that a wrapped message contained {0,number,0} bytes, but this is larger than the maximum of {1,number,0} bytes that the client has been configured to accept."), 
    ERR_SEQUENCE_BYTES_DECODE_EXCEPTION("Unable to decode the provided byte array as a sequence:  {0}"), 
    ERR_SEQUENCE_BYTES_DECODE_LENGTH_EXCEEDS_AVAILABLE("Unable to decode the provided byte array as a sequence because the decoded length of an embedded element exceeds the number of bytes remaining."), 
    ERR_SEQUENCE_DECODE_EXCEPTION("Unable to decode the provided ASN.1 element {0} as a sequence:  {1}"), 
    ERR_SEQUENCE_DECODE_LENGTH_EXCEEDS_AVAILABLE("Unable to decode the provided ASN.1 element {0} as a sequence because the decoded length of an embedded element exceeds the number of bytes remaining."), 
    ERR_SET_BYTES_DECODE_EXCEPTION("Unable to decode the provided byte array as a set:  {0}"), 
    ERR_SET_BYTES_DECODE_LENGTH_EXCEEDS_AVAILABLE("Unable to decode the provided byte array as a set because the decoded length of an embedded element exceeds the number of bytes remaining."), 
    ERR_SET_DECODE_EXCEPTION("Unable to decode the provided ASN.1 element {0} as a set:  {1}"), 
    ERR_SET_DECODE_LENGTH_EXCEEDS_AVAILABLE("Unable to decode the provided ASN.1 element {0} as a set because the decoded length of an embedded element exceeds the number of bytes remaining."), 
    ERR_STREAM_READER_EOS_READING_SASL_DATA("Unable to read SASL-encoded data because the end of the input stream was reached after reading only {0,number,0} bytes of the expected {1,number,0} bytes of wrapped data."), 
    ERR_STREAM_READER_EOS_READING_SASL_LENGTH("Unable to read SASL-encoded data because the end of the input stream was reached after reading only {0,number,0} bytes of the expected four-byte SASL length header."), 
    ERR_STREAM_READER_SEQUENCE_READ_PAST_END("The ASN.1 stream reader has already read beyond the end of this sequence (expected sequence of length {0} to end at {1} bytes into the stream, but {2} bytes have already been read from the stream)."), 
    ERR_STREAM_READER_SET_READ_PAST_END("The ASN.1 stream reader has already read beyond the end of this set (expected set of length {0} to end at {1} bytes into the stream, but {2} bytes have already been read from the stream)."), 
    ERR_UTC_TIME_STRING_CANNOT_PARSE("Unable to parse the provided string as an ASN.1 UTC time value:  {0}"), 
    ERR_UTC_TIME_STRING_CHAR_NOT_DIGIT("Unable to parse the provided string as an ASN.1 UTC time value because the character at position {0,number,0} is not a digit."), 
    ERR_UTC_TIME_STRING_DOES_NOT_END_WITH_Z("Unable to parse the provided string as an ASN.1 UTC time value because the string does not end with 'Z' to indicate that the timestamp is in the UTC time zone."), 
    ERR_UTC_TIME_STRING_INVALID_DAY("Unable to parse the provided string as an ASN.1 UTC time value because the day-of-month component is not between 1 and 31, inclusive."), 
    ERR_UTC_TIME_STRING_INVALID_HOUR("Unable to parse the provided string as an ASN.1 UTC time value because the hour component is not between 0 and 23, inclusive."), 
    ERR_UTC_TIME_STRING_INVALID_LENGTH("Unable to parse the provided string as an ASN.1 UTC time value because the string does not have a length of 13 characters."), 
    ERR_UTC_TIME_STRING_INVALID_MINUTE("Unable to parse the provided string as an ASN.1 UTC time value because the minute component is not between 0 and 59, inclusive."), 
    ERR_UTC_TIME_STRING_INVALID_MONTH("Unable to parse the provided string as an ASN.1 UTC time value because the month component is not between 1 and 12, inclusive."), 
    ERR_UTC_TIME_STRING_INVALID_SECOND("Unable to parse the provided string as an ASN.1 UTC time value because the second component is not between 0 and 60, inclusive."), 
    ERR_UTF_8_STRING_DECODE_VALUE_NOT_UTF_8("Unable to decode the provided ASN.1 element as a UTF-8 string element because the value is not valid UTF-8.");
    
    private static final boolean IS_WITHIN_UNIT_TESTS;
    private static final ResourceBundle RESOURCE_BUNDLE;
    private static final ConcurrentHashMap<ASN1Messages, String> MESSAGE_STRINGS;
    private static final ConcurrentHashMap<ASN1Messages, MessageFormat> MESSAGES;
    private final String defaultText;
    
    private ASN1Messages(final String defaultText) {
        this.defaultText = defaultText;
    }
    
    public String get() {
        String s = ASN1Messages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (ASN1Messages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = ASN1Messages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                ASN1Messages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        if (ASN1Messages.IS_WITHIN_UNIT_TESTS && (s.contains("{0}") || s.contains("{0,number,0}") || s.contains("{1}") || s.contains("{1,number,0}") || s.contains("{2}") || s.contains("{2,number,0}") || s.contains("{3}") || s.contains("{3,number,0}") || s.contains("{4}") || s.contains("{4,number,0}") || s.contains("{5}") || s.contains("{5,number,0}") || s.contains("{6}") || s.contains("{6,number,0}") || s.contains("{7}") || s.contains("{7,number,0}") || s.contains("{8}") || s.contains("{8,number,0}") || s.contains("{9}") || s.contains("{9,number,0}") || s.contains("{10}") || s.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + s);
        }
        return s;
    }
    
    public String get(final Object... args) {
        MessageFormat f = ASN1Messages.MESSAGES.get(this);
        if (f == null) {
            if (ASN1Messages.RESOURCE_BUNDLE == null) {
                f = new MessageFormat(this.defaultText);
            }
            else {
                try {
                    f = new MessageFormat(ASN1Messages.RESOURCE_BUNDLE.getString(this.name()));
                }
                catch (final Exception e) {
                    f = new MessageFormat(this.defaultText);
                }
            }
            ASN1Messages.MESSAGES.putIfAbsent(this, f);
        }
        final String formattedMessage;
        synchronized (f) {
            formattedMessage = f.format(args);
        }
        if (ASN1Messages.IS_WITHIN_UNIT_TESTS && (formattedMessage.contains("{0}") || formattedMessage.contains("{0,number,0}") || formattedMessage.contains("{1}") || formattedMessage.contains("{1,number,0}") || formattedMessage.contains("{2}") || formattedMessage.contains("{2,number,0}") || formattedMessage.contains("{3}") || formattedMessage.contains("{3,number,0}") || formattedMessage.contains("{4}") || formattedMessage.contains("{4,number,0}") || formattedMessage.contains("{5}") || formattedMessage.contains("{5,number,0}") || formattedMessage.contains("{6}") || formattedMessage.contains("{6,number,0}") || formattedMessage.contains("{7}") || formattedMessage.contains("{7,number,0}") || formattedMessage.contains("{8}") || formattedMessage.contains("{8,number,0}") || formattedMessage.contains("{9}") || formattedMessage.contains("{9,number,0}") || formattedMessage.contains("{10}") || formattedMessage.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + formattedMessage);
        }
        return f.format(args);
    }
    
    @Override
    public String toString() {
        String s = ASN1Messages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (ASN1Messages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = ASN1Messages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                ASN1Messages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        return s;
    }
    
    static {
        IS_WITHIN_UNIT_TESTS = (Boolean.getBoolean("com.unboundid.ldap.sdk.RunningUnitTests") || Boolean.getBoolean("com.unboundid.directory.server.RunningUnitTests"));
        ResourceBundle rb = null;
        try {
            rb = ResourceBundle.getBundle("unboundid-ldapsdk-asn1");
        }
        catch (final Exception ex) {}
        RESOURCE_BUNDLE = rb;
        MESSAGE_STRINGS = new ConcurrentHashMap<ASN1Messages, String>(100);
        MESSAGES = new ConcurrentHashMap<ASN1Messages, MessageFormat>(100);
    }
}
