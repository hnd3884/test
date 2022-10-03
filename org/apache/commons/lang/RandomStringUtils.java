package org.apache.commons.lang;

import java.util.Random;

public class RandomStringUtils
{
    private static final Random RANDOM;
    
    public static String random(final int count) {
        return random(count, false, false);
    }
    
    public static String randomAscii(final int count) {
        return random(count, 32, 127, false, false);
    }
    
    public static String randomAlphabetic(final int count) {
        return random(count, true, false);
    }
    
    public static String randomAlphanumeric(final int count) {
        return random(count, true, true);
    }
    
    public static String randomNumeric(final int count) {
        return random(count, false, true);
    }
    
    public static String random(final int count, final boolean letters, final boolean numbers) {
        return random(count, 0, 0, letters, numbers);
    }
    
    public static String random(final int count, final int start, final int end, final boolean letters, final boolean numbers) {
        return random(count, start, end, letters, numbers, null, RandomStringUtils.RANDOM);
    }
    
    public static String random(final int count, final int start, final int end, final boolean letters, final boolean numbers, final char[] chars) {
        return random(count, start, end, letters, numbers, chars, RandomStringUtils.RANDOM);
    }
    
    public static String random(int count, int start, int end, final boolean letters, final boolean numbers, final char[] chars, final Random random) {
        if (count == 0) {
            return "";
        }
        if (count < 0) {
            throw new IllegalArgumentException("Requested random string length " + count + " is less than 0.");
        }
        if (start == 0 && end == 0) {
            end = 123;
            start = 32;
            if (!letters && !numbers) {
                start = 0;
                end = Integer.MAX_VALUE;
            }
        }
        final StringBuffer buffer = new StringBuffer();
        final int gap = end - start;
        while (count-- != 0) {
            char ch;
            if (chars == null) {
                ch = (char)(random.nextInt(gap) + start);
            }
            else {
                ch = chars[random.nextInt(gap) + start];
            }
            if ((letters && numbers && Character.isLetterOrDigit(ch)) || (letters && Character.isLetter(ch)) || (numbers && Character.isDigit(ch)) || (!letters && !numbers)) {
                buffer.append(ch);
            }
            else {
                ++count;
            }
        }
        return buffer.toString();
    }
    
    public static String random(final int count, final String chars) {
        if (chars == null) {
            return random(count, 0, 0, false, false, null, RandomStringUtils.RANDOM);
        }
        return random(count, chars.toCharArray());
    }
    
    public static String random(final int count, final char[] chars) {
        if (chars == null) {
            return random(count, 0, 0, false, false, null, RandomStringUtils.RANDOM);
        }
        return random(count, 0, chars.length, false, false, chars, RandomStringUtils.RANDOM);
    }
    
    static {
        RANDOM = new Random();
    }
}
