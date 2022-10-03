package org.owasp.esapi;

import org.owasp.esapi.util.CollectionsUtil;
import java.util.Set;

public class EncoderConstants
{
    public static final char[] CHAR_PASSWORD_SPECIALS;
    public static final Set<Character> PASSWORD_SPECIALS;
    public static final char[] CHAR_LOWERS;
    public static final Set<Character> LOWERS;
    public static final char[] CHAR_UPPERS;
    public static final Set<Character> UPPERS;
    public static final char[] CHAR_DIGITS;
    public static final Set<Character> DIGITS;
    public static final char[] CHAR_SPECIALS;
    public static final Set<Character> SPECIALS;
    public static final char[] CHAR_LETTERS;
    public static final Set<Character> LETTERS;
    public static final char[] CHAR_ALPHANUMERICS;
    public static final Set<Character> ALPHANUMERICS;
    public static final char[] CHAR_PASSWORD_LOWERS;
    public static final Set<Character> PASSWORD_LOWERS;
    public static final char[] CHAR_PASSWORD_UPPERS;
    public static final Set<Character> PASSWORD_UPPERS;
    public static final char[] CHAR_PASSWORD_DIGITS;
    public static final Set<Character> PASSWORD_DIGITS;
    public static final char[] CHAR_PASSWORD_LETTERS;
    public static final Set<Character> PASSWORD_LETTERS;
    
    private EncoderConstants() {
    }
    
    static {
        CHAR_PASSWORD_SPECIALS = new char[] { '!', '$', '*', '-', '.', '=', '?', '@', '_' };
        PASSWORD_SPECIALS = CollectionsUtil.arrayToSet(EncoderConstants.CHAR_PASSWORD_SPECIALS);
        CHAR_LOWERS = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
        LOWERS = CollectionsUtil.arrayToSet(EncoderConstants.CHAR_PASSWORD_SPECIALS);
        CHAR_UPPERS = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
        UPPERS = CollectionsUtil.arrayToSet(EncoderConstants.CHAR_UPPERS);
        CHAR_DIGITS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
        DIGITS = CollectionsUtil.arrayToSet(EncoderConstants.CHAR_DIGITS);
        CHAR_SPECIALS = new char[] { '!', '$', '*', '+', '-', '.', '=', '?', '@', '^', '_', '|', '~' };
        SPECIALS = CollectionsUtil.arrayToSet(EncoderConstants.CHAR_SPECIALS);
        CHAR_LETTERS = StringUtilities.union(new char[][] { EncoderConstants.CHAR_LOWERS, EncoderConstants.CHAR_UPPERS });
        LETTERS = CollectionsUtil.arrayToSet(EncoderConstants.CHAR_LETTERS);
        CHAR_ALPHANUMERICS = StringUtilities.union(new char[][] { EncoderConstants.CHAR_LETTERS, EncoderConstants.CHAR_DIGITS });
        ALPHANUMERICS = CollectionsUtil.arrayToSet(EncoderConstants.CHAR_ALPHANUMERICS);
        CHAR_PASSWORD_LOWERS = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
        PASSWORD_LOWERS = CollectionsUtil.arrayToSet(EncoderConstants.CHAR_ALPHANUMERICS);
        CHAR_PASSWORD_UPPERS = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
        PASSWORD_UPPERS = CollectionsUtil.arrayToSet(EncoderConstants.CHAR_PASSWORD_UPPERS);
        CHAR_PASSWORD_DIGITS = new char[] { '2', '3', '4', '5', '6', '7', '8', '9' };
        PASSWORD_DIGITS = CollectionsUtil.arrayToSet(EncoderConstants.CHAR_PASSWORD_DIGITS);
        CHAR_PASSWORD_LETTERS = StringUtilities.union(new char[][] { EncoderConstants.CHAR_PASSWORD_LOWERS, EncoderConstants.CHAR_PASSWORD_UPPERS });
        PASSWORD_LETTERS = CollectionsUtil.arrayToSet(EncoderConstants.CHAR_PASSWORD_LETTERS);
    }
}
