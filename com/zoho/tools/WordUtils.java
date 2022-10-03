package com.zoho.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordUtils
{
    public static String wrap(final String str, final int wrapLength) {
        return wrap(str, wrapLength, null, false);
    }
    
    public static String wrap(final String str, final int wrapLength, final String newLineStr, final boolean wrapLongWords) {
        return wrap(str, wrapLength, newLineStr, wrapLongWords, " ");
    }
    
    public static String wrap(final String str, int wrapLength, String newLineStr, final boolean wrapLongWords, String wrapOn) {
        if (str == null) {
            return null;
        }
        if (newLineStr == null) {
            newLineStr = System.lineSeparator();
        }
        if (wrapLength < 1) {
            wrapLength = 1;
        }
        if (isBlank(wrapOn)) {
            wrapOn = " ";
        }
        final Pattern patternToWrapOn = Pattern.compile(wrapOn);
        final int inputLineLength = str.length();
        int offset = 0;
        final StringBuilder wrappedLine = new StringBuilder(inputLineLength + 32);
        int matcherSize = -1;
        while (offset < inputLineLength) {
            int spaceToWrapAt = -1;
            Matcher matcher = patternToWrapOn.matcher(str.substring(offset, Math.min((int)Math.min(2147483647L, offset + wrapLength + 1L), inputLineLength)));
            if (matcher.find()) {
                if (matcher.start() == 0) {
                    matcherSize = matcher.end() - matcher.start();
                    if (matcherSize != 0) {
                        offset += matcher.end();
                        continue;
                    }
                    ++offset;
                }
                spaceToWrapAt = matcher.start() + offset;
            }
            if (inputLineLength - offset <= wrapLength) {
                break;
            }
            while (matcher.find()) {
                spaceToWrapAt = matcher.start() + offset;
            }
            if (spaceToWrapAt >= offset) {
                wrappedLine.append(str, offset, spaceToWrapAt);
                wrappedLine.append(newLineStr);
                offset = spaceToWrapAt + 1;
            }
            else if (wrapLongWords) {
                if (matcherSize == 0) {
                    --offset;
                }
                wrappedLine.append(str, offset, wrapLength + offset);
                wrappedLine.append(newLineStr);
                offset += wrapLength;
                matcherSize = -1;
            }
            else {
                matcher = patternToWrapOn.matcher(str.substring(offset + wrapLength));
                if (matcher.find()) {
                    matcherSize = matcher.end() - matcher.start();
                    spaceToWrapAt = matcher.start() + offset + wrapLength;
                }
                if (spaceToWrapAt >= 0) {
                    if (matcherSize == 0 && offset != 0) {
                        --offset;
                    }
                    wrappedLine.append(str, offset, spaceToWrapAt);
                    wrappedLine.append(newLineStr);
                    offset = spaceToWrapAt + 1;
                }
                else {
                    if (matcherSize == 0 && offset != 0) {
                        --offset;
                    }
                    wrappedLine.append(str, offset, str.length());
                    offset = inputLineLength;
                    matcherSize = -1;
                }
            }
        }
        if (matcherSize == 0 && offset < inputLineLength) {
            --offset;
        }
        wrappedLine.append(str, offset, str.length());
        return wrappedLine.toString();
    }
    
    public static boolean isBlank(final CharSequence cs) {
        final int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }
}
