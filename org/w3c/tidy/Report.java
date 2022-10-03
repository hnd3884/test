package org.w3c.tidy;

import java.io.PrintWriter;
import java.util.MissingResourceException;
import java.text.MessageFormat;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;

public final class Report
{
    public static final String ACCESS_URL = "http://www.w3.org/WAI/GL";
    public static final String RELEASE_DATE_STRING;
    public static final short MISSING_SEMICOLON = 1;
    public static final short MISSING_SEMICOLON_NCR = 2;
    public static final short UNKNOWN_ENTITY = 3;
    public static final short UNESCAPED_AMPERSAND = 4;
    public static final short APOS_UNDEFINED = 5;
    public static final short MISSING_ENDTAG_FOR = 6;
    public static final short MISSING_ENDTAG_BEFORE = 7;
    public static final short DISCARDING_UNEXPECTED = 8;
    public static final short NESTED_EMPHASIS = 9;
    public static final short NON_MATCHING_ENDTAG = 10;
    public static final short TAG_NOT_ALLOWED_IN = 11;
    public static final short MISSING_STARTTAG = 12;
    public static final short UNEXPECTED_ENDTAG = 13;
    public static final short USING_BR_INPLACE_OF = 14;
    public static final short INSERTING_TAG = 15;
    public static final short SUSPECTED_MISSING_QUOTE = 16;
    public static final short MISSING_TITLE_ELEMENT = 17;
    public static final short DUPLICATE_FRAMESET = 18;
    public static final short CANT_BE_NESTED = 19;
    public static final short OBSOLETE_ELEMENT = 20;
    public static final short PROPRIETARY_ELEMENT = 21;
    public static final short UNKNOWN_ELEMENT = 22;
    public static final short TRIM_EMPTY_ELEMENT = 23;
    public static final short COERCE_TO_ENDTAG = 24;
    public static final short ILLEGAL_NESTING = 25;
    public static final short NOFRAMES_CONTENT = 26;
    public static final short CONTENT_AFTER_BODY = 27;
    public static final short INCONSISTENT_VERSION = 28;
    public static final short MALFORMED_COMMENT = 29;
    public static final short BAD_COMMENT_CHARS = 30;
    public static final short BAD_XML_COMMENT = 31;
    public static final short BAD_CDATA_CONTENT = 32;
    public static final short INCONSISTENT_NAMESPACE = 33;
    public static final short DOCTYPE_AFTER_TAGS = 34;
    public static final short MALFORMED_DOCTYPE = 35;
    public static final short UNEXPECTED_END_OF_FILE = 36;
    public static final short DTYPE_NOT_UPPER_CASE = 37;
    public static final short TOO_MANY_ELEMENTS = 38;
    public static final short UNESCAPED_ELEMENT = 39;
    public static final short NESTED_QUOTATION = 40;
    public static final short ELEMENT_NOT_EMPTY = 41;
    public static final short ENCODING_IO_CONFLICT = 42;
    public static final short MIXED_CONTENT_IN_BLOCK = 43;
    public static final short MISSING_DOCTYPE = 44;
    public static final short SPACE_PRECEDING_XMLDECL = 45;
    public static final short TOO_MANY_ELEMENTS_IN = 46;
    public static final short UNEXPECTED_ENDTAG_IN = 47;
    public static final short REPLACING_ELEMENT = 83;
    public static final short REPLACING_UNEX_ELEMENT = 84;
    public static final short COERCE_TO_ENDTAG_WARN = 85;
    public static final short UNKNOWN_ATTRIBUTE = 48;
    public static final short MISSING_ATTRIBUTE = 49;
    public static final short MISSING_ATTR_VALUE = 50;
    public static final short BAD_ATTRIBUTE_VALUE = 51;
    public static final short UNEXPECTED_GT = 52;
    public static final short PROPRIETARY_ATTRIBUTE = 53;
    public static final short PROPRIETARY_ATTR_VALUE = 54;
    public static final short REPEATED_ATTRIBUTE = 55;
    public static final short MISSING_IMAGEMAP = 56;
    public static final short XML_ATTRIBUTE_VALUE = 57;
    public static final short MISSING_QUOTEMARK = 58;
    public static final short UNEXPECTED_QUOTEMARK = 59;
    public static final short ID_NAME_MISMATCH = 60;
    public static final short BACKSLASH_IN_URI = 61;
    public static final short FIXED_BACKSLASH = 62;
    public static final short ILLEGAL_URI_REFERENCE = 63;
    public static final short ESCAPED_ILLEGAL_URI = 64;
    public static final short NEWLINE_IN_URI = 65;
    public static final short ANCHOR_NOT_UNIQUE = 66;
    public static final short ENTITY_IN_ID = 67;
    public static final short JOINING_ATTRIBUTE = 68;
    public static final short UNEXPECTED_EQUALSIGN = 69;
    public static final short ATTR_VALUE_NOT_LCASE = 70;
    public static final short XML_ID_SYNTAX = 71;
    public static final short INVALID_ATTRIBUTE = 72;
    public static final short BAD_ATTRIBUTE_VALUE_REPLACED = 73;
    public static final short INVALID_XML_ID = 74;
    public static final short UNEXPECTED_END_OF_FILE_ATTR = 75;
    public static final short VENDOR_SPECIFIC_CHARS = 76;
    public static final short INVALID_SGML_CHARS = 77;
    public static final short INVALID_UTF8 = 78;
    public static final short INVALID_UTF16 = 79;
    public static final short ENCODING_MISMATCH = 80;
    public static final short INVALID_URI = 81;
    public static final short INVALID_NCR = 82;
    public static final short DOCTYPE_GIVEN_SUMMARY = 110;
    public static final short REPORT_VERSION_SUMMARY = 111;
    public static final short BADACCESS_SUMMARY = 112;
    public static final short BADFORM_SUMMARY = 113;
    public static final short MISSING_IMAGE_ALT = 1;
    public static final short MISSING_LINK_ALT = 2;
    public static final short MISSING_SUMMARY = 4;
    public static final short MISSING_IMAGE_MAP = 8;
    public static final short USING_FRAMES = 16;
    public static final short USING_NOFRAMES = 32;
    public static final short USING_SPACER = 1;
    public static final short USING_LAYER = 2;
    public static final short USING_NOBR = 4;
    public static final short USING_FONT = 8;
    public static final short USING_BODY = 16;
    public static final short WINDOWS_CHARS = 1;
    public static final short NON_ASCII = 2;
    public static final short FOUND_UTF16 = 4;
    public static final short REPLACED_CHAR = 0;
    public static final short DISCARDED_CHAR = 1;
    private static ResourceBundle res;
    private String currentFile;
    private TidyMessageListener listener;
    
    private static String readReleaseDate() {
        final Properties properties = new Properties();
        try {
            final InputStream resourceAsStream = Report.class.getResourceAsStream("/jtidy.properties");
            properties.load(resourceAsStream);
            resourceAsStream.close();
        }
        catch (final Exception ex) {
            throw new RuntimeException("Failed to load jtidy.properties", ex);
        }
        return properties.getProperty("date");
    }
    
    protected Report() {
    }
    
    protected String getMessage(final int n, final Lexer lexer, final String s, final Object[] array, final TidyMessage.Level level) throws MissingResourceException {
        final String string = Report.res.getString(s);
        String position;
        if (lexer != null && level != TidyMessage.Level.SUMMARY) {
            position = this.getPosition(lexer);
        }
        else {
            position = "";
        }
        String s2;
        if (level == TidyMessage.Level.ERROR) {
            s2 = Report.res.getString("error");
        }
        else if (level == TidyMessage.Level.WARNING) {
            s2 = Report.res.getString("warning");
        }
        else {
            s2 = "";
        }
        String format;
        if (array != null) {
            format = MessageFormat.format(string, array);
        }
        else {
            format = string;
        }
        if (this.listener != null) {
            this.listener.messageReceived(new TidyMessage(n, (lexer != null) ? lexer.lines : 0, (lexer != null) ? lexer.columns : 0, level, format));
        }
        return position + s2 + format;
    }
    
    private void printMessage(final int n, final Lexer lexer, final String s, final Object[] array, final TidyMessage.Level level) {
        String message;
        try {
            message = this.getMessage(n, lexer, s, array, level);
        }
        catch (final MissingResourceException ex) {
            lexer.errout.println(ex.toString());
            return;
        }
        lexer.errout.println(message);
    }
    
    private void printMessage(final PrintWriter printWriter, final String s, final Object[] array, final TidyMessage.Level level) {
        String message;
        try {
            message = this.getMessage(-1, null, s, array, level);
        }
        catch (final MissingResourceException ex) {
            printWriter.println(ex.toString());
            return;
        }
        printWriter.println(message);
    }
    
    public void showVersion(final PrintWriter printWriter) {
        this.printMessage(printWriter, "version_summary", new Object[] { Report.RELEASE_DATE_STRING }, TidyMessage.Level.SUMMARY);
    }
    
    private String getTagName(final Node node) {
        if (node == null) {
            return "";
        }
        if (node.type == 5) {
            return "<" + node.element + ">";
        }
        if (node.type == 6) {
            return "</" + node.element + ">";
        }
        if (node.type == 1) {
            return "<!DOCTYPE>";
        }
        if (node.type == 4) {
            return "plain text";
        }
        return node.element;
    }
    
    public void unknownOption(final String s) {
        try {
            System.err.println(MessageFormat.format(Report.res.getString("unknown_option"), s));
        }
        catch (final MissingResourceException ex) {
            System.err.println(ex.toString());
        }
    }
    
    public void badArgument(final String s, final String s2) {
        try {
            System.err.println(MessageFormat.format(Report.res.getString("bad_argument"), s2, s));
        }
        catch (final MissingResourceException ex) {
            System.err.println(ex.toString());
        }
    }
    
    private String getPosition(final Lexer lexer) {
        try {
            if (lexer.configuration.emacs) {
                return MessageFormat.format(Report.res.getString("emacs_format"), this.currentFile, new Integer(lexer.lines), new Integer(lexer.columns)) + " ";
            }
            return MessageFormat.format(Report.res.getString("line_column"), new Integer(lexer.lines), new Integer(lexer.columns));
        }
        catch (final MissingResourceException ex) {
            lexer.errout.println(ex.toString());
            return "";
        }
    }
    
    public void encodingError(final Lexer lexer, final int n, final int n2) {
        ++lexer.warnings;
        if (lexer.errors > lexer.configuration.showErrors) {
            return;
        }
        if (lexer.configuration.showWarnings) {
            final String hexString = Integer.toHexString(n2);
            if ((n & 0xFFFFFFFE) == 0x50) {
                lexer.badChars |= 0x50;
                this.printMessage(n, lexer, "encoding_mismatch", new Object[] { lexer.configuration.getInCharEncodingName(), ParsePropertyImpl.CHAR_ENCODING.getFriendlyName(null, new Integer(n2), lexer.configuration) }, TidyMessage.Level.WARNING);
            }
            else if ((n & 0xFFFFFFFE) == 0x4C) {
                lexer.badChars |= 0x4C;
                this.printMessage(n, lexer, "invalid_char", new Object[] { new Integer(n & 0x1), hexString }, TidyMessage.Level.WARNING);
            }
            else if ((n & 0xFFFFFFFE) == 0x4D) {
                lexer.badChars |= 0x4D;
                this.printMessage(n, lexer, "invalid_char", new Object[] { new Integer(n & 0x1), hexString }, TidyMessage.Level.WARNING);
            }
            else if ((n & 0xFFFFFFFE) == 0x4E) {
                lexer.badChars |= 0x4E;
                this.printMessage(n, lexer, "invalid_utf8", new Object[] { new Integer(n & 0x1), hexString }, TidyMessage.Level.WARNING);
            }
            else if ((n & 0xFFFFFFFE) == 0x4F) {
                lexer.badChars |= 0x4F;
                this.printMessage(n, lexer, "invalid_utf16", new Object[] { new Integer(n & 0x1), hexString }, TidyMessage.Level.WARNING);
            }
            else if ((n & 0xFFFFFFFE) == 0x52) {
                lexer.badChars |= 0x52;
                this.printMessage(n, lexer, "invalid_ncr", new Object[] { new Integer(n & 0x1), hexString }, TidyMessage.Level.WARNING);
            }
        }
    }
    
    public void entityError(final Lexer lexer, final short n, final String s, final int n2) {
        ++lexer.warnings;
        if (lexer.errors > lexer.configuration.showErrors) {
            return;
        }
        if (lexer.configuration.showWarnings) {
            switch (n) {
                case 1: {
                    this.printMessage(n, lexer, "missing_semicolon", new Object[] { s }, TidyMessage.Level.WARNING);
                    break;
                }
                case 2: {
                    this.printMessage(n, lexer, "missing_semicolon_ncr", new Object[] { s }, TidyMessage.Level.WARNING);
                    break;
                }
                case 3: {
                    this.printMessage(n, lexer, "unknown_entity", new Object[] { s }, TidyMessage.Level.WARNING);
                    break;
                }
                case 4: {
                    this.printMessage(n, lexer, "unescaped_ampersand", null, TidyMessage.Level.WARNING);
                    break;
                }
                case 5: {
                    this.printMessage(n, lexer, "apos_undefined", null, TidyMessage.Level.WARNING);
                    break;
                }
            }
        }
    }
    
    public void attrError(final Lexer lexer, final Node node, final AttVal attVal, final short n) {
        if (n == 52) {
            ++lexer.errors;
        }
        else {
            ++lexer.warnings;
        }
        if (lexer.errors > lexer.configuration.showErrors) {
            return;
        }
        if (n == 52) {
            this.printMessage(n, lexer, "unexpected_gt", new Object[] { this.getTagName(node) }, TidyMessage.Level.ERROR);
        }
        if (!lexer.configuration.showWarnings) {
            return;
        }
        switch (n) {
            case 48: {
                this.printMessage(n, lexer, "unknown_attribute", new Object[] { attVal.attribute }, TidyMessage.Level.WARNING);
                break;
            }
            case 49: {
                this.printMessage(n, lexer, "missing_attribute", new Object[] { this.getTagName(node), attVal.attribute }, TidyMessage.Level.WARNING);
                break;
            }
            case 50: {
                this.printMessage(n, lexer, "missing_attr_value", new Object[] { this.getTagName(node), attVal.attribute }, TidyMessage.Level.WARNING);
                break;
            }
            case 56: {
                this.printMessage(n, lexer, "missing_imagemap", new Object[] { this.getTagName(node) }, TidyMessage.Level.WARNING);
                lexer.badAccess |= 0x8;
                break;
            }
            case 51: {
                this.printMessage(n, lexer, "bad_attribute_value", new Object[] { this.getTagName(node), attVal.attribute, attVal.value }, TidyMessage.Level.WARNING);
                break;
            }
            case 71: {
                this.printMessage(n, lexer, "xml_id_sintax", new Object[] { this.getTagName(node), attVal.attribute }, TidyMessage.Level.WARNING);
                break;
            }
            case 57: {
                this.printMessage(n, lexer, "xml_attribute_value", new Object[] { this.getTagName(node), attVal.attribute }, TidyMessage.Level.WARNING);
                break;
            }
            case 59: {
                this.printMessage(n, lexer, "unexpected_quotemark", new Object[] { this.getTagName(node) }, TidyMessage.Level.WARNING);
                break;
            }
            case 58: {
                this.printMessage(n, lexer, "missing_quotemark", new Object[] { this.getTagName(node) }, TidyMessage.Level.WARNING);
                break;
            }
            case 55: {
                this.printMessage(n, lexer, "repeated_attribute", new Object[] { this.getTagName(node), attVal.value, attVal.attribute }, TidyMessage.Level.WARNING);
                break;
            }
            case 54: {
                this.printMessage(n, lexer, "proprietary_attr_value", new Object[] { this.getTagName(node), attVal.value }, TidyMessage.Level.WARNING);
                break;
            }
            case 53: {
                this.printMessage(n, lexer, "proprietary_attribute", new Object[] { this.getTagName(node), attVal.attribute }, TidyMessage.Level.WARNING);
                break;
            }
            case 36: {
                lexer.lines = lexer.in.getCurline();
                lexer.columns = lexer.in.getCurcol();
                this.printMessage(n, lexer, "unexpected_end_of_file", new Object[] { this.getTagName(node) }, TidyMessage.Level.WARNING);
                break;
            }
            case 60: {
                this.printMessage(n, lexer, "id_name_mismatch", new Object[] { this.getTagName(node) }, TidyMessage.Level.WARNING);
                break;
            }
            case 61: {
                this.printMessage(n, lexer, "backslash_in_uri", new Object[] { this.getTagName(node) }, TidyMessage.Level.WARNING);
                break;
            }
            case 62: {
                this.printMessage(n, lexer, "fixed_backslash", new Object[] { this.getTagName(node) }, TidyMessage.Level.WARNING);
                break;
            }
            case 63: {
                this.printMessage(n, lexer, "illegal_uri_reference", new Object[] { this.getTagName(node) }, TidyMessage.Level.WARNING);
                break;
            }
            case 64: {
                this.printMessage(n, lexer, "escaped_illegal_uri", new Object[] { this.getTagName(node) }, TidyMessage.Level.WARNING);
                break;
            }
            case 65: {
                this.printMessage(n, lexer, "newline_in_uri", new Object[] { this.getTagName(node) }, TidyMessage.Level.WARNING);
                break;
            }
            case 66: {
                this.printMessage(n, lexer, "anchor_not_unique", new Object[] { this.getTagName(node), attVal.value }, TidyMessage.Level.WARNING);
                break;
            }
            case 67: {
                this.printMessage(n, lexer, "entity_in_id", null, TidyMessage.Level.WARNING);
                break;
            }
            case 68: {
                this.printMessage(n, lexer, "joining_attribute", new Object[] { this.getTagName(node), attVal.attribute }, TidyMessage.Level.WARNING);
                break;
            }
            case 69: {
                this.printMessage(n, lexer, "expected_equalsign", new Object[] { this.getTagName(node) }, TidyMessage.Level.WARNING);
                break;
            }
            case 70: {
                this.printMessage(n, lexer, "attr_value_not_lcase", new Object[] { this.getTagName(node), attVal.value, attVal.attribute }, TidyMessage.Level.WARNING);
                break;
            }
        }
    }
    
    public void warning(final Lexer lexer, final Node node, final Node node2, final short n) {
        final TagTable tt = lexer.configuration.tt;
        if (n != 8 || lexer.badForm == 0) {
            ++lexer.warnings;
        }
        if (lexer.errors > lexer.configuration.showErrors) {
            return;
        }
        if (lexer.configuration.showWarnings) {
            switch (n) {
                case 6: {
                    this.printMessage(n, lexer, "missing_endtag_for", new Object[] { node.element }, TidyMessage.Level.WARNING);
                    break;
                }
                case 7: {
                    this.printMessage(n, lexer, "missing_endtag_before", new Object[] { node.element, this.getTagName(node2) }, TidyMessage.Level.WARNING);
                    break;
                }
                case 8: {
                    if (lexer.badForm == 0) {
                        this.printMessage(n, lexer, "discarding_unexpected", new Object[] { this.getTagName(node2) }, TidyMessage.Level.WARNING);
                        break;
                    }
                    break;
                }
                case 9: {
                    this.printMessage(n, lexer, "nested_emphasis", new Object[] { this.getTagName(node2) }, TidyMessage.Level.INFO);
                    break;
                }
                case 24: {
                    this.printMessage(n, lexer, "coerce_to_endtag", new Object[] { node.element }, TidyMessage.Level.INFO);
                    break;
                }
                case 10: {
                    this.printMessage(n, lexer, "non_matching_endtag", new Object[] { this.getTagName(node2), node.element }, TidyMessage.Level.WARNING);
                    break;
                }
                case 11: {
                    this.printMessage(n, lexer, "tag_not_allowed_in", new Object[] { this.getTagName(node2), node.element }, TidyMessage.Level.WARNING);
                    break;
                }
                case 34: {
                    this.printMessage(n, lexer, "doctype_after_tags", null, TidyMessage.Level.WARNING);
                    break;
                }
                case 12: {
                    this.printMessage(n, lexer, "missing_starttag", new Object[] { node2.element }, TidyMessage.Level.WARNING);
                    break;
                }
                case 13: {
                    if (node != null) {
                        this.printMessage(n, lexer, "unexpected_endtag_in", new Object[] { node2.element, node.element }, TidyMessage.Level.WARNING);
                        break;
                    }
                    this.printMessage(n, lexer, "unexpected_endtag", new Object[] { node2.element }, TidyMessage.Level.WARNING);
                    break;
                }
                case 38: {
                    if (node != null) {
                        this.printMessage(n, lexer, "too_many_elements_in", new Object[] { node2.element, node.element }, TidyMessage.Level.WARNING);
                        break;
                    }
                    this.printMessage(n, lexer, "too_many_elements", new Object[] { node2.element }, TidyMessage.Level.WARNING);
                    break;
                }
                case 14: {
                    this.printMessage(n, lexer, "using_br_inplace_of", new Object[] { this.getTagName(node2) }, TidyMessage.Level.WARNING);
                    break;
                }
                case 15: {
                    this.printMessage(n, lexer, "inserting_tag", new Object[] { node2.element }, TidyMessage.Level.WARNING);
                    break;
                }
                case 19: {
                    this.printMessage(n, lexer, "cant_be_nested", new Object[] { this.getTagName(node2) }, TidyMessage.Level.WARNING);
                    break;
                }
                case 21: {
                    this.printMessage(n, lexer, "proprietary_element", new Object[] { this.getTagName(node2) }, TidyMessage.Level.WARNING);
                    if (node2.tag == tt.tagLayer) {
                        lexer.badLayout |= 0x2;
                        break;
                    }
                    if (node2.tag == tt.tagSpacer) {
                        lexer.badLayout |= 0x1;
                        break;
                    }
                    if (node2.tag == tt.tagNobr) {
                        lexer.badLayout |= 0x4;
                        break;
                    }
                    break;
                }
                case 20: {
                    if (node.tag != null && (node.tag.model & 0x80000) != 0x0) {
                        this.printMessage(n, lexer, "obsolete_element", new Object[] { this.getTagName(node), this.getTagName(node2) }, TidyMessage.Level.WARNING);
                        break;
                    }
                    this.printMessage(n, lexer, "replacing_element", new Object[] { this.getTagName(node), this.getTagName(node2) }, TidyMessage.Level.WARNING);
                    break;
                }
                case 39: {
                    this.printMessage(n, lexer, "unescaped_element", new Object[] { this.getTagName(node) }, TidyMessage.Level.WARNING);
                    break;
                }
                case 23: {
                    this.printMessage(n, lexer, "trim_empty_element", new Object[] { this.getTagName(node) }, TidyMessage.Level.WARNING);
                    break;
                }
                case 17: {
                    this.printMessage(n, lexer, "missing_title_element", null, TidyMessage.Level.WARNING);
                    break;
                }
                case 25: {
                    this.printMessage(n, lexer, "illegal_nesting", new Object[] { this.getTagName(node) }, TidyMessage.Level.WARNING);
                    break;
                }
                case 26: {
                    this.printMessage(n, lexer, "noframes_content", new Object[] { this.getTagName(node2) }, TidyMessage.Level.WARNING);
                    break;
                }
                case 28: {
                    this.printMessage(n, lexer, "inconsistent_version", null, TidyMessage.Level.WARNING);
                    break;
                }
                case 35: {
                    this.printMessage(n, lexer, "malformed_doctype", null, TidyMessage.Level.WARNING);
                    break;
                }
                case 27: {
                    this.printMessage(n, lexer, "content_after_body", null, TidyMessage.Level.WARNING);
                    break;
                }
                case 29: {
                    this.printMessage(n, lexer, "malformed_comment", null, TidyMessage.Level.WARNING);
                    break;
                }
                case 30: {
                    this.printMessage(n, lexer, "bad_comment_chars", null, TidyMessage.Level.WARNING);
                    break;
                }
                case 31: {
                    this.printMessage(n, lexer, "bad_xml_comment", null, TidyMessage.Level.WARNING);
                    break;
                }
                case 32: {
                    this.printMessage(n, lexer, "bad_cdata_content", null, TidyMessage.Level.WARNING);
                    break;
                }
                case 33: {
                    this.printMessage(n, lexer, "inconsistent_namespace", null, TidyMessage.Level.WARNING);
                    break;
                }
                case 37: {
                    this.printMessage(n, lexer, "dtype_not_upper_case", null, TidyMessage.Level.WARNING);
                    break;
                }
                case 36: {
                    lexer.lines = lexer.in.getCurline();
                    lexer.columns = lexer.in.getCurcol();
                    this.printMessage(n, lexer, "unexpected_end_of_file", new Object[] { this.getTagName(node) }, TidyMessage.Level.WARNING);
                    break;
                }
                case 40: {
                    this.printMessage(n, lexer, "nested_quotation", null, TidyMessage.Level.WARNING);
                    break;
                }
                case 41: {
                    this.printMessage(n, lexer, "element_not_empty", new Object[] { this.getTagName(node) }, TidyMessage.Level.WARNING);
                    break;
                }
                case 44: {
                    this.printMessage(n, lexer, "missing_doctype", null, TidyMessage.Level.WARNING);
                    break;
                }
            }
        }
        if (n == 8 && lexer.badForm != 0) {
            this.printMessage(n, lexer, "discarding_unexpected", new Object[] { this.getTagName(node2) }, TidyMessage.Level.ERROR);
        }
    }
    
    public void error(final Lexer lexer, final Node node, final Node node2, final short n) {
        ++lexer.errors;
        if (lexer.errors > lexer.configuration.showErrors) {
            return;
        }
        if (n == 16) {
            this.printMessage(n, lexer, "suspected_missing_quote", null, TidyMessage.Level.ERROR);
        }
        else if (n == 18) {
            this.printMessage(n, lexer, "duplicate_frameset", null, TidyMessage.Level.ERROR);
        }
        else if (n == 22) {
            this.printMessage(n, lexer, "unknown_element", new Object[] { this.getTagName(node2) }, TidyMessage.Level.ERROR);
        }
        else if (n == 13) {
            if (node != null) {
                this.printMessage(n, lexer, "unexpected_endtag_in", new Object[] { node2.element, node.element }, TidyMessage.Level.ERROR);
            }
            else {
                this.printMessage(n, lexer, "unexpected_endtag", new Object[] { node2.element }, TidyMessage.Level.ERROR);
            }
        }
    }
    
    public void errorSummary(final Lexer lexer) {
        if ((lexer.badAccess & 0x30) != 0x0 && ((lexer.badAccess & 0x10) == 0x0 || (lexer.badAccess & 0x20) != 0x0)) {
            lexer.badAccess &= 0xFFFFFFCF;
        }
        if (lexer.badChars != 0) {
            if ((lexer.badChars & 0x4C) != 0x0) {
                int n = 0;
                if ("Cp1252".equals(lexer.configuration.getInCharEncodingName())) {
                    n = 1;
                }
                else if ("MacRoman".equals(lexer.configuration.getInCharEncodingName())) {
                    n = 2;
                }
                this.printMessage(76, lexer, "vendor_specific_chars_summary", new Object[] { new Integer(n) }, TidyMessage.Level.SUMMARY);
            }
            if ((lexer.badChars & 0x4D) != 0x0 || (lexer.badChars & 0x52) != 0x0) {
                int n2 = 0;
                if ("Cp1252".equals(lexer.configuration.getInCharEncodingName())) {
                    n2 = 1;
                }
                else if ("MacRoman".equals(lexer.configuration.getInCharEncodingName())) {
                    n2 = 2;
                }
                this.printMessage(77, lexer, "invalid_sgml_chars_summary", new Object[] { new Integer(n2) }, TidyMessage.Level.SUMMARY);
            }
            if ((lexer.badChars & 0x4E) != 0x0) {
                this.printMessage(78, lexer, "invalid_utf8_summary", null, TidyMessage.Level.SUMMARY);
            }
            if ((lexer.badChars & 0x4F) != 0x0) {
                this.printMessage(79, lexer, "invalid_utf16_summary", null, TidyMessage.Level.SUMMARY);
            }
            if ((lexer.badChars & 0x51) != 0x0) {
                this.printMessage(81, lexer, "invaliduri_summary", null, TidyMessage.Level.SUMMARY);
            }
        }
        if (lexer.badForm != 0) {
            this.printMessage(113, lexer, "badform_summary", null, TidyMessage.Level.SUMMARY);
        }
        if (lexer.badAccess != 0) {
            if ((lexer.badAccess & 0x4) != 0x0) {
                this.printMessage(4, lexer, "badaccess_missing_summary", null, TidyMessage.Level.SUMMARY);
            }
            if ((lexer.badAccess & 0x1) != 0x0) {
                this.printMessage(1, lexer, "badaccess_missing_image_alt", null, TidyMessage.Level.SUMMARY);
            }
            if ((lexer.badAccess & 0x8) != 0x0) {
                this.printMessage(8, lexer, "badaccess_missing_image_map", null, TidyMessage.Level.SUMMARY);
            }
            if ((lexer.badAccess & 0x2) != 0x0) {
                this.printMessage(2, lexer, "badaccess_missing_link_alt", null, TidyMessage.Level.SUMMARY);
            }
            if ((lexer.badAccess & 0x10) != 0x0 && (lexer.badAccess & 0x20) == 0x0) {
                this.printMessage(16, lexer, "badaccess_frames", null, TidyMessage.Level.SUMMARY);
            }
            this.printMessage(112, lexer, "badaccess_summary", new Object[] { "http://www.w3.org/WAI/GL" }, TidyMessage.Level.SUMMARY);
        }
        if (lexer.badLayout != 0) {
            if ((lexer.badLayout & 0x2) != 0x0) {
                this.printMessage(2, lexer, "badlayout_using_layer", null, TidyMessage.Level.SUMMARY);
            }
            if ((lexer.badLayout & 0x1) != 0x0) {
                this.printMessage(1, lexer, "badlayout_using_spacer", null, TidyMessage.Level.SUMMARY);
            }
            if ((lexer.badLayout & 0x8) != 0x0) {
                this.printMessage(8, lexer, "badlayout_using_font", null, TidyMessage.Level.SUMMARY);
            }
            if ((lexer.badLayout & 0x4) != 0x0) {
                this.printMessage(4, lexer, "badlayout_using_nobr", null, TidyMessage.Level.SUMMARY);
            }
            if ((lexer.badLayout & 0x10) != 0x0) {
                this.printMessage(16, lexer, "badlayout_using_body", null, TidyMessage.Level.SUMMARY);
            }
        }
    }
    
    public void unknownOption(final PrintWriter printWriter, final char c) {
        this.printMessage(printWriter, "unrecognized_option", new Object[] { new String(new char[] { c }) }, TidyMessage.Level.ERROR);
    }
    
    public void unknownFile(final PrintWriter printWriter, final String s) {
        this.printMessage(printWriter, "unknown_file", new Object[] { "Tidy", s }, TidyMessage.Level.ERROR);
    }
    
    public void needsAuthorIntervention(final PrintWriter printWriter) {
        this.printMessage(printWriter, "needs_author_intervention", null, TidyMessage.Level.SUMMARY);
    }
    
    public void missingBody(final PrintWriter printWriter) {
        this.printMessage(printWriter, "missing_body", null, TidyMessage.Level.ERROR);
    }
    
    public void reportNumberOfSlides(final PrintWriter printWriter, final int n) {
        this.printMessage(printWriter, "slides_found", new Object[] { new Integer(n) }, TidyMessage.Level.SUMMARY);
    }
    
    public void generalInfo(final PrintWriter printWriter) {
        this.printMessage(printWriter, "general_info", null, TidyMessage.Level.SUMMARY);
    }
    
    public void setFilename(final String currentFile) {
        this.currentFile = currentFile;
    }
    
    public void reportVersion(final PrintWriter printWriter, final Lexer lexer, final String s, final Node node) {
        int n = 0;
        final String htmlVersionName = lexer.htmlVersionName();
        final int[] array = { 0 };
        lexer.lines = 1;
        lexer.columns = 1;
        if (node != null) {
            final StringBuffer sb = new StringBuffer();
            for (int i = node.start; i < node.end; ++i) {
                int n2 = node.textarray[i];
                if (n2 < 0) {
                    i += PPrint.getUTF8(node.textarray, i, array);
                    n2 = array[0];
                }
                if (n2 == 34) {
                    ++n;
                }
                else if (n == 1) {
                    sb.append((char)n2);
                }
            }
            this.printMessage(110, lexer, "doctype_given", new Object[] { s, sb }, TidyMessage.Level.SUMMARY);
        }
        this.printMessage(111, lexer, "report_version", new Object[] { s, (htmlVersionName != null) ? htmlVersionName : "HTML proprietary" }, TidyMessage.Level.SUMMARY);
    }
    
    public void reportNumWarnings(final PrintWriter printWriter, final Lexer lexer) {
        if (lexer.warnings > 0 || lexer.errors > 0) {
            this.printMessage(printWriter, "num_warnings", new Object[] { new Integer(lexer.warnings), new Integer(lexer.errors) }, TidyMessage.Level.SUMMARY);
        }
        else {
            this.printMessage(printWriter, "no_warnings", null, TidyMessage.Level.SUMMARY);
        }
    }
    
    public void helpText(final PrintWriter printWriter) {
        this.printMessage(printWriter, "help_text", new Object[] { "Tidy", Report.RELEASE_DATE_STRING }, TidyMessage.Level.SUMMARY);
    }
    
    public void badTree(final PrintWriter printWriter) {
        this.printMessage(printWriter, "bad_tree", null, TidyMessage.Level.ERROR);
    }
    
    public void addMessageListener(final TidyMessageListener listener) {
        this.listener = listener;
    }
    
    static {
        RELEASE_DATE_STRING = readReleaseDate();
        try {
            Report.res = ResourceBundle.getBundle("org/w3c/tidy/TidyMessages");
        }
        catch (final MissingResourceException ex) {
            throw new Error(ex.toString());
        }
    }
}
