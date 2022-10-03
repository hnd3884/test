package com.microsoft.sqlserver.jdbc;

final class ParameterUtils
{
    static byte[] HexToBin(final String hexV) throws SQLServerException {
        final int len = hexV.length();
        final char[] orig = hexV.toCharArray();
        if (len % 2 != 0) {
            SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_stringNotInHex"), null, false);
        }
        final byte[] bin = new byte[len / 2];
        for (int i = 0; i < len / 2; ++i) {
            bin[i] = (byte)((CharToHex(orig[2 * i]) << 4) + (CharToHex(orig[2 * i + 1]) & 0xFF));
        }
        return bin;
    }
    
    static byte CharToHex(final char CTX) throws SQLServerException {
        byte ret = 0;
        if (CTX >= 'A' && CTX <= 'F') {
            ret = (byte)(CTX - 'A' + 10);
        }
        else if (CTX >= 'a' && CTX <= 'f') {
            ret = (byte)(CTX - 'a' + 10);
        }
        else if (CTX >= '0' && CTX <= '9') {
            ret = (byte)(CTX - '0');
        }
        else {
            SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_stringNotInHex"), null, false);
        }
        return ret;
    }
    
    static int scanSQLForChar(final char ch, final String sql, int offset) {
        final int len = sql.length();
        while (offset < len) {
            char chTmp;
            switch (chTmp = sql.charAt(offset++)) {
                case '/': {
                    if (offset == len) {
                        continue;
                    }
                    if (sql.charAt(offset) == '*') {
                        while (++offset < len) {
                            if (sql.charAt(offset) == '*' && offset + 1 < len && sql.charAt(offset + 1) == '/') {
                                offset += 2;
                                break;
                            }
                        }
                        continue;
                    }
                    if (sql.charAt(offset) == '-') {
                        continue;
                    }
                }
                case '-': {
                    if (offset >= 0 && offset < sql.length() && sql.charAt(offset) == '-') {
                        while (++offset < len) {
                            if (sql.charAt(offset) == '\n' || sql.charAt(offset) == '\r') {
                                ++offset;
                                break;
                            }
                        }
                        continue;
                    }
                    break;
                }
                case '[': {
                    chTmp = ']';
                }
                case '\"':
                case '\'': {
                    final char chQuote = chTmp;
                    while (offset < len) {
                        if (sql.charAt(offset++) == chQuote) {
                            if (len == offset) {
                                break;
                            }
                            if (sql.charAt(offset) != chQuote) {
                                break;
                            }
                            ++offset;
                        }
                    }
                    continue;
                }
            }
            if (ch == chTmp) {
                return offset - 1;
            }
        }
        return len;
    }
}
