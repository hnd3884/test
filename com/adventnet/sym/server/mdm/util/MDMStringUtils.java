package com.adventnet.sym.server.mdm.util;

import java.util.Collection;
import java.io.UnsupportedEncodingException;
import org.json.JSONObject;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.me.devicemanagement.framework.server.util.SyMUtil;

public class MDMStringUtils
{
    public static boolean isEmpty(final String str) {
        return SyMUtil.isStringEmpty(str);
    }
    
    public static boolean isValidDeviceIdentifier(final String str) {
        return !isEmpty(str) && str.trim().length() >= 5;
    }
    
    public static String matchFirstOccurenceOfPattern(final String str, final String regExpPattern) {
        String retVal = null;
        final Pattern pattern = Pattern.compile(regExpPattern);
        final Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            retVal = matcher.group();
        }
        return retVal;
    }
    
    public String replaceAndEscapeMetaCharacters(String inputString, final String replaceWhat, String replaceWith) {
        replaceWith = this.escapeMetaCharacters(replaceWith);
        inputString = inputString.replaceAll(replaceWhat, replaceWith);
        return inputString;
    }
    
    public String escapeMetaCharacters(String inputString) {
        final String[] metaCharacters = { "\\", "^", "$", "{", "}", "[", "]", "(", ")", ".", "*", "+", "?", "|", "<", ">", "-", "&" };
        for (int i = 0; i < metaCharacters.length; ++i) {
            if (inputString.contains(metaCharacters[i])) {
                inputString = inputString.replace(metaCharacters[i], "\\" + metaCharacters[i]);
            }
        }
        return inputString;
    }
    
    public static String trimStringLenght(String name, final int trimLenght) {
        if (name != null && name.length() > trimLenght) {
            name = name.substring(0, trimLenght - 1);
        }
        return name;
    }
    
    public static String trimStringLength(String name, final int trimLength, final CharSequence suffix) {
        if (name != null && name.length() > trimLength) {
            name = trimStringLenght(name, trimLength) + (Object)suffix;
        }
        return name;
    }
    
    public static String isValueMatchinList(final String strToMatch, final List listOfRegEx) {
        String match = null;
        for (final String regEx : listOfRegEx) {
            if (strToMatch.matches(regEx)) {
                match = regEx;
                break;
            }
        }
        return match;
    }
    
    public static Long[] convertStringToLongArray(final String[] arrays) {
        final Long[] longArray = new Long[arrays.length];
        for (int i = 0; i < arrays.length; ++i) {
            longArray[i] = Long.parseLong(arrays[i].trim());
        }
        return longArray;
    }
    
    public static String getDecodedString(final String encodedString) throws Exception {
        final String decoded = URLDecoder.decode(encodedString, "UTF-8");
        return decoded.replaceAll("\\\\", "");
    }
    
    public static boolean isURLValid(final JSONObject object, final String key) {
        return object.has(key) && !isEmpty(object.optString(key)) && !object.optString(key).equalsIgnoreCase("Not available");
    }
    
    public static String getValueFromParams(final String queryParameters, final String keyToFind) throws UnsupportedEncodingException {
        if (queryParameters != null) {
            final String[] split;
            final String[] pairs = split = queryParameters.split("&");
            for (final String pair : split) {
                final int idx = pair.indexOf("=");
                if (idx != -1) {
                    final String key = URLDecoder.decode(pair.substring(0, idx), "utf-8");
                    final String value = URLDecoder.decode(pair.substring(idx + 1), "utf-8");
                    if (key.equals(keyToFind)) {
                        return value;
                    }
                }
            }
        }
        return null;
    }
    
    public static String removeValueFromUrlParams(final String queryParameters, final String keyToFind) throws UnsupportedEncodingException {
        if (queryParameters != null) {
            String result = null;
            final String[] split;
            final String[] pairs = split = queryParameters.split("&");
            for (final String pair : split) {
                final int idx = pair.indexOf("=");
                if (idx != -1) {
                    final String key = pair.substring(0, idx);
                    final String value = pair.substring(idx + 1);
                    if (!key.equals(keyToFind)) {
                        result = ((result == null) ? "" : "&") + key + "=" + value;
                    }
                }
            }
            return result;
        }
        return null;
    }
    
    public static boolean equals(final String s1, final String s2, final boolean caseSensitive) {
        return s1 != null && s2 != null && (caseSensitive ? s1.equals(s2) : s1.equalsIgnoreCase(s2));
    }
    
    public static String getCommaSeparatedStringFromStrList(final Collection<String> list) {
        if (list.size() == 0) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        for (final String t : list) {
            builder.append(t);
            builder.append(", ");
        }
        return builder.toString().trim().substring(0, builder.lastIndexOf(","));
    }
    
    public static boolean isEmptyOrEquals(final String s1, final String s2) {
        return isEmpty(s1) || isEmpty(s2) || s1.equalsIgnoreCase(s2);
    }
    
    public static String escapeJson(String escapeString) {
        escapeString = escapeString.replace("\\", "\\\\").replace("\"", "\\\"").replace("/", "\\/");
        return escapeString;
    }
}
