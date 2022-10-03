package org.apache.poi.ss.util;

import java.util.List;
import java.util.ArrayList;
import org.apache.poi.util.POILogFactory;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import org.apache.poi.util.LocaleID;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.util.POILogger;

public final class DateFormatConverter
{
    private static POILogger logger;
    private static Map<String, String> tokenConversions;
    
    private DateFormatConverter() {
    }
    
    private static Map<String, String> prepareTokenConversions() {
        final Map<String, String> result = new HashMap<String, String>();
        result.put("EEEE", "dddd");
        result.put("EEE", "ddd");
        result.put("EE", "ddd");
        result.put("E", "d");
        result.put("Z", "");
        result.put("z", "");
        result.put("a", "am/pm");
        result.put("A", "AM/PM");
        result.put("K", "H");
        result.put("KK", "HH");
        result.put("k", "h");
        result.put("kk", "hh");
        result.put("S", "0");
        result.put("SS", "00");
        result.put("SSS", "000");
        return result;
    }
    
    public static String getPrefixForLocale(final Locale locale) {
        final String languageTag = locale.toLanguageTag();
        if ("".equals(languageTag)) {
            return "[$-0409]";
        }
        LocaleID loc = LocaleID.lookupByLanguageTag(languageTag);
        if (loc == null) {
            final String cmpTag = (languageTag.indexOf(95) > -1) ? languageTag.replace('_', '-') : languageTag;
            for (int idx = languageTag.length(); loc == null && (idx = cmpTag.lastIndexOf(45, idx - 1)) > 0; loc = LocaleID.lookupByLanguageTag(languageTag.substring(0, idx))) {}
            if (loc == null) {
                DateFormatConverter.logger.log(7, "Unable to find prefix for Locale '" + languageTag + "' or its parent locales.");
                return "";
            }
        }
        return String.format(Locale.ROOT, "[$-%04X]", loc.getLcid());
    }
    
    public static String convert(final Locale locale, final DateFormat df) {
        final String ptrn = ((SimpleDateFormat)df).toPattern();
        return convert(locale, ptrn);
    }
    
    public static String convert(final Locale locale, final String format) {
        final StringBuilder result = new StringBuilder();
        result.append(getPrefixForLocale(locale));
        final DateFormatTokenizer tokenizer = new DateFormatTokenizer(format);
        String token;
        while ((token = tokenizer.getNextToken()) != null) {
            if (token.startsWith("'")) {
                result.append(token.replaceAll("'", "\""));
            }
            else if (!Character.isLetter(token.charAt(0))) {
                result.append(token);
            }
            else {
                final String mappedToken = DateFormatConverter.tokenConversions.get(token);
                result.append((mappedToken == null) ? token : mappedToken);
            }
        }
        result.append(";@");
        return result.toString().trim();
    }
    
    public static String getJavaDatePattern(final int style, final Locale locale) {
        final DateFormat df = DateFormat.getDateInstance(style, locale);
        if (df instanceof SimpleDateFormat) {
            return ((SimpleDateFormat)df).toPattern();
        }
        switch (style) {
            case 3: {
                return "d/MM/yy";
            }
            case 1: {
                return "MMMM d, yyyy";
            }
            case 0: {
                return "dddd, MMMM d, yyyy";
            }
            default: {
                return "MMM d, yyyy";
            }
        }
    }
    
    public static String getJavaTimePattern(final int style, final Locale locale) {
        final DateFormat df = DateFormat.getTimeInstance(style, locale);
        if (df instanceof SimpleDateFormat) {
            return ((SimpleDateFormat)df).toPattern();
        }
        switch (style) {
            case 3: {
                return "h:mm a";
            }
            default: {
                return "h:mm:ss a";
            }
        }
    }
    
    public static String getJavaDateTimePattern(final int style, final Locale locale) {
        final DateFormat df = DateFormat.getDateTimeInstance(style, style, locale);
        if (df instanceof SimpleDateFormat) {
            return ((SimpleDateFormat)df).toPattern();
        }
        switch (style) {
            case 3: {
                return "M/d/yy h:mm a";
            }
            case 1: {
                return "MMMM d, yyyy h:mm:ss a";
            }
            case 0: {
                return "dddd, MMMM d, yyyy h:mm:ss a";
            }
            default: {
                return "MMM d, yyyy h:mm:ss a";
            }
        }
    }
    
    static {
        DateFormatConverter.logger = POILogFactory.getLogger(DateFormatConverter.class);
        DateFormatConverter.tokenConversions = prepareTokenConversions();
    }
    
    public static class DateFormatTokenizer
    {
        String format;
        int pos;
        
        public DateFormatTokenizer(final String format) {
            this.format = format;
        }
        
        public String getNextToken() {
            if (this.pos >= this.format.length()) {
                return null;
            }
            final int subStart = this.pos;
            final char curChar = this.format.charAt(this.pos);
            ++this.pos;
            if (curChar == '\'') {
                while (this.pos < this.format.length() && this.format.charAt(this.pos) != '\'') {
                    ++this.pos;
                }
                if (this.pos < this.format.length()) {
                    ++this.pos;
                }
            }
            else {
                while (this.pos < this.format.length() && this.format.charAt(this.pos) == curChar) {
                    ++this.pos;
                }
            }
            return this.format.substring(subStart, this.pos);
        }
        
        public static String[] tokenize(final String format) {
            final List<String> result = new ArrayList<String>();
            final DateFormatTokenizer tokenizer = new DateFormatTokenizer(format);
            String token;
            while ((token = tokenizer.getNextToken()) != null) {
                result.add(token);
            }
            return result.toArray(new String[0]);
        }
        
        @Override
        public String toString() {
            final StringBuilder result = new StringBuilder();
            final DateFormatTokenizer tokenizer = new DateFormatTokenizer(this.format);
            String token;
            while ((token = tokenizer.getNextToken()) != null) {
                if (result.length() > 0) {
                    result.append(", ");
                }
                result.append("[").append(token).append("]");
            }
            return result.toString();
        }
    }
}
