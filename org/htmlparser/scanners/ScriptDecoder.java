package org.htmlparser.scanners;

import org.htmlparser.util.ParserException;
import org.htmlparser.lexer.Cursor;
import org.htmlparser.lexer.Page;

public class ScriptDecoder
{
    public static final int STATE_DONE = 0;
    public static final int STATE_INITIAL = 1;
    protected static final int STATE_LENGTH = 2;
    protected static final int STATE_PREFIX = 3;
    protected static final int STATE_DECODE = 4;
    protected static final int STATE_ESCAPE = 5;
    protected static final int STATE_CHECKSUM = 6;
    protected static final int STATE_FINAL = 7;
    public static int LAST_STATE;
    protected static byte[] mEncodingIndex;
    protected static char[][] mLookupTable;
    protected static int[] mDigits;
    protected static char[] mLeader;
    protected static char[] mPrefix;
    protected static char[] mTrailer;
    protected static char[] mEscapes;
    protected static char[] mEscaped;
    
    protected static long decodeBase64(final char[] p) {
        long ret = 0L;
        ret += ScriptDecoder.mDigits[p[0]] << 2;
        ret += ScriptDecoder.mDigits[p[1]] >> 4;
        ret += (ScriptDecoder.mDigits[p[1]] & 0xF) << 12;
        ret += ScriptDecoder.mDigits[p[2]] >> 2 << 8;
        ret += (ScriptDecoder.mDigits[p[2]] & 0x3) << 22;
        ret += ScriptDecoder.mDigits[p[3]] << 16;
        ret += ScriptDecoder.mDigits[p[4]] << 2 << 24;
        ret += ScriptDecoder.mDigits[p[5]] >> 4 << 24;
        return ret;
    }
    
    public static String Decode(final Page page, final Cursor cursor) throws ParserException {
        final char[] buffer = new char[6];
        final StringBuffer ret = new StringBuffer(1024);
        int state = 1;
        int substate_initial = 0;
        int substate_length = 0;
        int substate_prefix = 0;
        int substate_checksum = 0;
        int substate_final = 0;
        long length = 0L;
        long checksum = 0L;
        int index = 0;
        while (0 != state) {
            int input_character = page.getCharacter(cursor);
            char character = (char)input_character;
            if (65535 == input_character) {
                if (1 != state || 0 != substate_initial || 0 != substate_length || 0 != substate_prefix || 0 != substate_checksum || 0 != substate_final) {
                    throw new ParserException("illegal state for exit");
                }
                state = 0;
            }
            else {
                switch (state) {
                    case 1: {
                        if (character != ScriptDecoder.mLeader[substate_initial]) {
                            for (int k = 0; 0 < substate_initial; --substate_initial, ++k) {
                                ret.append(ScriptDecoder.mLeader[k++]);
                            }
                            ret.append(character);
                            continue;
                        }
                        if (++substate_initial == ScriptDecoder.mLeader.length) {
                            substate_initial = 0;
                            state = 2;
                            continue;
                        }
                        continue;
                    }
                    case 2: {
                        buffer[substate_length] = character;
                        if (++substate_length < buffer.length) {
                            continue;
                        }
                        length = decodeBase64(buffer);
                        if (0L > length) {
                            throw new ParserException("illegal length: " + length);
                        }
                        substate_length = 0;
                        state = 3;
                        continue;
                    }
                    case 3: {
                        if (character != ScriptDecoder.mPrefix[substate_prefix]) {
                            throw new ParserException("illegal character encountered: " + (int)character + " ('" + character + "')");
                        }
                        ++substate_prefix;
                        if (substate_prefix >= ScriptDecoder.mPrefix.length) {
                            substate_prefix = 0;
                            state = 4;
                            continue;
                        }
                        continue;
                    }
                    case 4: {
                        if ('@' == character) {
                            state = 5;
                        }
                        else if (input_character < 128) {
                            if (input_character == 9) {
                                input_character = 0;
                            }
                            else {
                                if (input_character < 32) {
                                    throw new ParserException("illegal encoded character: " + input_character + " ('" + character + "')");
                                }
                                input_character -= 31;
                            }
                            final char ch = ScriptDecoder.mLookupTable[ScriptDecoder.mEncodingIndex[index % 64]][input_character];
                            ret.append(ch);
                            checksum += ch;
                            ++index;
                        }
                        else {
                            ret.append(character);
                        }
                        --length;
                        if (0L == length) {
                            index = 0;
                            state = 6;
                            continue;
                        }
                        continue;
                    }
                    case 5: {
                        boolean found = false;
                        for (int i = 0; i < ScriptDecoder.mEscapes.length; ++i) {
                            if (character == ScriptDecoder.mEscapes[i]) {
                                found = true;
                                character = ScriptDecoder.mEscaped[i];
                            }
                        }
                        if (!found) {
                            throw new ParserException("unexpected escape character: " + (int)character + " ('" + character + "')");
                        }
                        ret.append(character);
                        checksum += character;
                        ++index;
                        state = 4;
                        --length;
                        if (0L == length) {
                            index = 0;
                            state = 6;
                            continue;
                        }
                        continue;
                    }
                    case 6: {
                        buffer[substate_checksum] = character;
                        if (++substate_checksum < buffer.length) {
                            continue;
                        }
                        final long check = decodeBase64(buffer);
                        if (check != checksum) {
                            throw new ParserException("incorrect checksum, expected " + check + ", calculated " + checksum);
                        }
                        checksum = 0L;
                        substate_checksum = 0;
                        state = 7;
                        continue;
                    }
                    case 7: {
                        if (character != ScriptDecoder.mTrailer[substate_final]) {
                            throw new ParserException("illegal character encountered: " + (int)character + " ('" + character + "')");
                        }
                        ++substate_final;
                        if (substate_final >= ScriptDecoder.mTrailer.length) {
                            substate_final = 0;
                            state = ScriptDecoder.LAST_STATE;
                            continue;
                        }
                        continue;
                    }
                    default: {
                        throw new ParserException("invalid state: " + state);
                    }
                }
            }
        }
        return ret.toString();
    }
    
    static {
        ScriptDecoder.LAST_STATE = 0;
        ScriptDecoder.mEncodingIndex = new byte[] { 1, 2, 0, 1, 2, 0, 2, 0, 0, 2, 0, 2, 1, 0, 2, 0, 1, 0, 2, 0, 1, 1, 2, 0, 0, 2, 1, 0, 2, 0, 0, 2, 1, 1, 0, 2, 0, 2, 0, 1, 0, 1, 1, 2, 0, 1, 0, 2, 1, 0, 2, 0, 1, 1, 2, 0, 0, 1, 1, 2, 0, 1, 0, 2 };
        ScriptDecoder.mLookupTable = new char[][] { { '{', '2', '0', '!', ')', '[', '8', '3', '=', 'X', ':', '5', 'e', '9', '\\', 'V', 's', 'f', 'N', 'E', 'k', 'b', 'Y', 'x', '^', '}', 'J', 'm', 'q', '\0', '`', '\0', 'S', '\0', 'B', '\'', 'H', 'r', 'u', '1', '7', 'M', 'R', '\"', 'T', 'j', 'G', 'd', '-', ' ', '\u007f', '.', 'L', ']', '~', 'l', 'o', 'y', 't', 'C', '&', 'v', '%', '$', '+', '(', '#', 'A', '4', '\t', '*', 'D', '?', 'w', ';', 'U', 'i', 'a', 'c', 'P', 'g', 'Q', 'I', 'O', 'F', 'h', '|', '6', 'p', 'n', 'z', '/', '_', 'K', 'Z', ',', 'W' }, { 'W', '.', 'G', 'z', 'V', 'B', 'j', '/', '&', 'I', 'A', '4', '2', '[', 'v', 'r', 'C', '8', '9', 'p', 'E', 'h', 'q', 'O', '\t', 'b', 'D', '#', 'u', '\0', '~', '\0', '^', '\0', 'w', 'J', 'a', ']', '\"', 'K', 'o', 'N', ';', 'L', 'P', 'g', '*', '}', 't', 'T', '+', '-', ',', '0', 'n', 'k', 'f', '5', '%', '!', 'd', 'M', 'R', 'c', '?', '{', 'x', ')', '(', 's', 'Y', '3', '\u007f', 'm', 'U', 'S', '|', ':', '_', 'e', 'F', 'X', '1', 'i', 'l', 'Z', 'H', '\'', '\\', '=', '$', 'y', '7', '`', 'Q', ' ', '6' }, { 'n', '-', 'u', 'R', '`', 'q', '^', 'I', '\\', 'b', '}', ')', '6', ' ', '|', 'z', '\u007f', 'k', 'c', '3', '+', 'h', 'Q', 'f', 'v', '1', 'd', 'T', 'C', '\0', ':', '\0', '~', '\0', 'E', ',', '*', 't', '\'', '7', 'D', 'y', 'Y', '/', 'o', '&', 'r', 'j', '9', '{', '?', '8', 'w', 'g', 'S', 'G', '4', 'x', ']', '0', '#', 'Z', '[', 'l', 'H', 'U', 'p', 'i', '.', 'L', '!', '$', 'N', 'P', '\t', 'V', 's', '5', 'a', 'K', 'X', ';', 'W', '\"', 'm', 'M', '%', '(', 'F', 'J', '2', 'A', '=', '_', 'O', 'B', 'e' } };
        ScriptDecoder.mDigits = new int[123];
        for (int i = 0; i < 26; ++i) {
            ScriptDecoder.mDigits[65 + i] = i;
            ScriptDecoder.mDigits[97 + i] = i + 26;
        }
        for (int i = 0; i < 10; ++i) {
            ScriptDecoder.mDigits[48 + i] = i + 52;
        }
        ScriptDecoder.mDigits[43] = 62;
        ScriptDecoder.mDigits[47] = 63;
        ScriptDecoder.mLeader = new char[] { '#', '@', '~', '^' };
        ScriptDecoder.mPrefix = new char[] { '=', '=' };
        ScriptDecoder.mTrailer = new char[] { '=', '=', '^', '#', '~', '@' };
        ScriptDecoder.mEscapes = new char[] { '#', '&', '!', '*', '$' };
        ScriptDecoder.mEscaped = new char[] { '\r', '\n', '<', '>', '@' };
    }
}
