package sun.reflect;

class UTF8
{
    static byte[] encode(final String s) {
        final int length = s.length();
        final byte[] array = new byte[utf8Length(s)];
        int n = 0;
        try {
            for (int i = 0; i < length; ++i) {
                final int n2 = s.charAt(i) & '\uffff';
                if (n2 >= 1 && n2 <= 127) {
                    array[n++] = (byte)n2;
                }
                else if (n2 == 0 || (n2 >= 128 && n2 <= 2047)) {
                    array[n++] = (byte)(192 + (n2 >> 6));
                    array[n++] = (byte)(128 + (n2 & 0x3F));
                }
                else {
                    array[n++] = (byte)(224 + (n2 >> 12));
                    array[n++] = (byte)(128 + (n2 >> 6 & 0x3F));
                    array[n++] = (byte)(128 + (n2 & 0x3F));
                }
            }
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            throw new InternalError("Bug in sun.reflect bootstrap UTF-8 encoder", ex);
        }
        return array;
    }
    
    private static int utf8Length(final String s) {
        final int length = s.length();
        int n = 0;
        for (int i = 0; i < length; ++i) {
            final int n2 = s.charAt(i) & '\uffff';
            if (n2 >= 1 && n2 <= 127) {
                ++n;
            }
            else if (n2 == 0 || (n2 >= 128 && n2 <= 2047)) {
                n += 2;
            }
            else {
                n += 3;
            }
        }
        return n;
    }
}
