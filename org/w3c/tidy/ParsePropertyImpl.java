package org.w3c.tidy;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public final class ParsePropertyImpl
{
    static final ParseProperty INT;
    static final ParseProperty BOOL;
    static final ParseProperty INVBOOL;
    static final ParseProperty CHAR_ENCODING;
    static final ParseProperty NAME;
    static final ParseProperty TAGNAMES;
    static final ParseProperty DOCTYPE;
    static final ParseProperty REPEATED_ATTRIBUTES;
    static final ParseProperty STRING;
    static final ParseProperty INDENT;
    static final ParseProperty CSS1SELECTOR;
    static final ParseProperty NEWLINE;
    
    private ParsePropertyImpl() {
    }
    
    static {
        INT = new ParseInt();
        BOOL = new ParseBoolean();
        INVBOOL = new ParseInvBoolean();
        CHAR_ENCODING = new ParseCharEncoding();
        NAME = new ParseName();
        TAGNAMES = new ParseTagNames();
        DOCTYPE = new ParseDocType();
        REPEATED_ATTRIBUTES = new ParseRepeatedAttribute();
        STRING = new ParseString();
        INDENT = new ParseIndent();
        CSS1SELECTOR = new ParseCSS1Selector();
        NEWLINE = new ParseNewLine();
    }
    
    static class ParseBoolean implements ParseProperty
    {
        public Object parse(final String s, final String s2, final Configuration configuration) {
            Boolean b = Boolean.TRUE;
            if (s != null && s.length() > 0) {
                final char char1 = s.charAt(0);
                if (char1 == 't' || char1 == 'T' || char1 == 'Y' || char1 == 'y' || char1 == '1') {
                    b = Boolean.TRUE;
                }
                else if (char1 == 'f' || char1 == 'F' || char1 == 'N' || char1 == 'n' || char1 == '0') {
                    b = Boolean.FALSE;
                }
                else {
                    configuration.report.badArgument(s, s2);
                }
            }
            return b;
        }
        
        public String getType() {
            return "Boolean";
        }
        
        public String getOptionValues() {
            return "y/n, yes/no, t/f, true/false, 1/0";
        }
        
        public String getFriendlyName(final String s, final Object o, final Configuration configuration) {
            if (o == null) {
                return "";
            }
            return o ? "yes" : "no";
        }
    }
    
    static class ParseCSS1Selector implements ParseProperty
    {
        public Object parse(final String s, final String s2, final Configuration configuration) {
            final StringTokenizer stringTokenizer = new StringTokenizer(s);
            Object string = null;
            if (stringTokenizer.countTokens() >= 1) {
                string = stringTokenizer.nextToken() + "-";
            }
            else {
                configuration.report.badArgument(s, s2);
            }
            if (!Lexer.isCSS1Selector(s)) {
                configuration.report.badArgument(s, s2);
            }
            return string;
        }
        
        public String getType() {
            return "Name";
        }
        
        public String getOptionValues() {
            return "CSS1 selector";
        }
        
        public String getFriendlyName(final String s, final Object o, final Configuration configuration) {
            return (String)((o == null) ? "" : o);
        }
    }
    
    static class ParseCharEncoding implements ParseProperty
    {
        public Object parse(final String s, final String s2, final Configuration configuration) {
            if ("raw".equalsIgnoreCase(s)) {
                configuration.rawOut = true;
            }
            else if (!TidyUtils.isCharEncodingSupported(s)) {
                configuration.report.badArgument(s, s2);
            }
            else if ("input-encoding".equalsIgnoreCase(s2)) {
                configuration.setInCharEncodingName(s);
            }
            else if ("output-encoding".equalsIgnoreCase(s2)) {
                configuration.setOutCharEncodingName(s);
            }
            else if ("char-encoding".equalsIgnoreCase(s2)) {
                configuration.setInCharEncodingName(s);
                configuration.setOutCharEncodingName(s);
            }
            return null;
        }
        
        public String getType() {
            return "Encoding";
        }
        
        public String getOptionValues() {
            return "Any valid java char encoding name";
        }
        
        public String getFriendlyName(final String s, final Object o, final Configuration configuration) {
            if ("output-encoding".equalsIgnoreCase(s)) {
                return configuration.getOutCharEncodingName();
            }
            return configuration.getInCharEncodingName();
        }
    }
    
    static class ParseDocType implements ParseProperty
    {
        public Object parse(String trim, final String s, final Configuration configuration) {
            trim = trim.trim();
            if (trim.startsWith("\"")) {
                configuration.docTypeMode = 4;
                return trim;
            }
            String nextToken = "";
            final StringTokenizer stringTokenizer = new StringTokenizer(trim, " \t\n\r,");
            if (stringTokenizer.hasMoreTokens()) {
                nextToken = stringTokenizer.nextToken();
            }
            if ("auto".equalsIgnoreCase(nextToken)) {
                configuration.docTypeMode = 1;
            }
            else if ("omit".equalsIgnoreCase(nextToken)) {
                configuration.docTypeMode = 0;
            }
            else if ("strict".equalsIgnoreCase(nextToken)) {
                configuration.docTypeMode = 2;
            }
            else if ("loose".equalsIgnoreCase(nextToken) || "transitional".equalsIgnoreCase(nextToken)) {
                configuration.docTypeMode = 3;
            }
            else {
                configuration.report.badArgument(trim, s);
            }
            return null;
        }
        
        public String getType() {
            return "DocType";
        }
        
        public String getOptionValues() {
            return "omit | auto | strict | loose | [fpi]";
        }
        
        public String getFriendlyName(final String s, final Object o, final Configuration configuration) {
            String docTypeStr = null;
            switch (configuration.docTypeMode) {
                case 1: {
                    docTypeStr = "auto";
                    break;
                }
                case 0: {
                    docTypeStr = "omit";
                    break;
                }
                case 2: {
                    docTypeStr = "strict";
                    break;
                }
                case 3: {
                    docTypeStr = "transitional";
                    break;
                }
                case 4: {
                    docTypeStr = configuration.docTypeStr;
                    break;
                }
                default: {
                    docTypeStr = "unknown";
                    break;
                }
            }
            return docTypeStr;
        }
    }
    
    static class ParseIndent implements ParseProperty
    {
        public Object parse(final String s, final String s2, final Configuration configuration) {
            boolean indentContent = configuration.indentContent;
            if ("yes".equalsIgnoreCase(s)) {
                indentContent = true;
                configuration.smartIndent = false;
            }
            else if ("true".equalsIgnoreCase(s)) {
                indentContent = true;
                configuration.smartIndent = false;
            }
            else if ("no".equalsIgnoreCase(s)) {
                indentContent = false;
                configuration.smartIndent = false;
            }
            else if ("false".equalsIgnoreCase(s)) {
                indentContent = false;
                configuration.smartIndent = false;
            }
            else if ("auto".equalsIgnoreCase(s)) {
                indentContent = true;
                configuration.smartIndent = true;
            }
            else {
                configuration.report.badArgument(s, s2);
            }
            return indentContent ? Boolean.TRUE : Boolean.FALSE;
        }
        
        public String getType() {
            return "Indent";
        }
        
        public String getOptionValues() {
            return "auto, y/n, yes/no, t/f, true/false, 1/0";
        }
        
        public String getFriendlyName(final String s, final Object o, final Configuration configuration) {
            return (o == null) ? "" : o.toString();
        }
    }
    
    static class ParseInt implements ParseProperty
    {
        public Object parse(final String s, final String s2, final Configuration configuration) {
            int int1;
            try {
                int1 = Integer.parseInt(s);
            }
            catch (final NumberFormatException ex) {
                configuration.report.badArgument(s, s2);
                int1 = -1;
            }
            return new Integer(int1);
        }
        
        public String getType() {
            return "Integer";
        }
        
        public String getOptionValues() {
            return "0, 1, 2, ...";
        }
        
        public String getFriendlyName(final String s, final Object o, final Configuration configuration) {
            return (o == null) ? "" : o.toString();
        }
    }
    
    static class ParseInvBoolean implements ParseProperty
    {
        public Object parse(final String s, final String s2, final Configuration configuration) {
            return ParsePropertyImpl.BOOL.parse(s, s2, configuration) ? Boolean.FALSE : Boolean.TRUE;
        }
        
        public String getType() {
            return "Boolean";
        }
        
        public String getOptionValues() {
            return "yes, no, true, false";
        }
        
        public String getFriendlyName(final String s, final Object o, final Configuration configuration) {
            if (o == null) {
                return "";
            }
            return o ? "no" : "yes";
        }
    }
    
    static class ParseName implements ParseProperty
    {
        public Object parse(final String s, final String s2, final Configuration configuration) {
            final StringTokenizer stringTokenizer = new StringTokenizer(s);
            Object nextToken = null;
            if (stringTokenizer.countTokens() >= 1) {
                nextToken = stringTokenizer.nextToken();
            }
            else {
                configuration.report.badArgument(s, s2);
            }
            return nextToken;
        }
        
        public String getType() {
            return "Name";
        }
        
        public String getOptionValues() {
            return "-";
        }
        
        public String getFriendlyName(final String s, final Object o, final Configuration configuration) {
            return (o == null) ? "" : o.toString();
        }
    }
    
    static class ParseNewLine implements ParseProperty
    {
        public Object parse(final String s, final String s2, final Configuration configuration) {
            if ("lf".equalsIgnoreCase(s)) {
                configuration.newline = new char[] { '\n' };
            }
            else if ("cr".equalsIgnoreCase(s)) {
                configuration.newline = new char[] { '\r' };
            }
            else if ("crlf".equalsIgnoreCase(s)) {
                configuration.newline = new char[] { '\r', '\n' };
            }
            else {
                configuration.report.badArgument(s, s2);
            }
            return null;
        }
        
        public String getType() {
            return "Enum";
        }
        
        public String getOptionValues() {
            return "lf, crlf, cr";
        }
        
        public String getFriendlyName(final String s, final Object o, final Configuration configuration) {
            if (configuration.newline.length == 1) {
                return (configuration.newline[0] == '\n') ? "lf" : "cr";
            }
            return "crlf";
        }
    }
    
    static class ParseRepeatedAttribute implements ParseProperty
    {
        public Object parse(final String s, final String s2, final Configuration configuration) {
            int n;
            if ("keep-first".equalsIgnoreCase(s)) {
                n = 1;
            }
            else if ("keep-last".equalsIgnoreCase(s)) {
                n = 0;
            }
            else {
                configuration.report.badArgument(s, s2);
                n = -1;
            }
            return new Integer(n);
        }
        
        public String getType() {
            return "Enum";
        }
        
        public String getOptionValues() {
            return "keep-first, keep-last";
        }
        
        public String getFriendlyName(final String s, final Object o, final Configuration configuration) {
            if (o == null) {
                return "";
            }
            String s2 = null;
            switch ((int)o) {
                case 1: {
                    s2 = "keep-first";
                    break;
                }
                case 0: {
                    s2 = "keep-last";
                    break;
                }
                default: {
                    s2 = "unknown";
                    break;
                }
            }
            return s2;
        }
    }
    
    static class ParseString implements ParseProperty
    {
        public Object parse(final String s, final String s2, final Configuration configuration) {
            return s;
        }
        
        public String getType() {
            return "String";
        }
        
        public String getOptionValues() {
            return "-";
        }
        
        public String getFriendlyName(final String s, final Object o, final Configuration configuration) {
            return (String)((o == null) ? "" : o);
        }
    }
    
    static class ParseTagNames implements ParseProperty
    {
        public Object parse(final String s, final String s2, final Configuration configuration) {
            short n = 2;
            if ("new-inline-tags".equals(s2)) {
                n = 2;
            }
            else if ("new-blocklevel-tags".equals(s2)) {
                n = 4;
            }
            else if ("new-empty-tags".equals(s2)) {
                n = 1;
            }
            else if ("new-pre-tags".equals(s2)) {
                n = 8;
            }
            final StringTokenizer stringTokenizer = new StringTokenizer(s, " \t\n\r,");
            while (stringTokenizer.hasMoreTokens()) {
                configuration.definedTags |= n;
                configuration.tt.defineTag(n, stringTokenizer.nextToken());
            }
            return null;
        }
        
        public String getType() {
            return "Tag names";
        }
        
        public String getOptionValues() {
            return "tagX, tagY, ...";
        }
        
        public String getFriendlyName(final String s, final Object o, final Configuration configuration) {
            short n;
            if ("new-inline-tags".equals(s)) {
                n = 2;
            }
            else if ("new-blocklevel-tags".equals(s)) {
                n = 4;
            }
            else if ("new-empty-tags".equals(s)) {
                n = 1;
            }
            else {
                if (!"new-pre-tags".equals(s)) {
                    return "";
                }
                n = 8;
            }
            final List allDefinedTag = configuration.tt.findAllDefinedTag(n);
            if (allDefinedTag.isEmpty()) {
                return "";
            }
            final StringBuffer sb = new StringBuffer();
            final Iterator iterator = allDefinedTag.iterator();
            while (iterator.hasNext()) {
                sb.append(iterator.next());
                sb.append(" ");
            }
            return sb.toString();
        }
    }
}
