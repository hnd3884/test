package sun.security.util;

import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.CodingErrorAction;
import sun.misc.SharedSecrets;
import java.io.Console;
import java.util.Arrays;
import java.io.PushbackInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.CharsetEncoder;

public class Password
{
    private static volatile CharsetEncoder enc;
    
    public static char[] readPassword(final InputStream inputStream) throws IOException {
        return readPassword(inputStream, false);
    }
    
    public static char[] readPassword(InputStream inputStream, final boolean b) throws IOException {
        char[] password = null;
        byte[] convertToBytes = null;
        try {
            final Console console;
            if (!b && inputStream == System.in && (console = System.console()) != null) {
                password = console.readPassword();
                if (password != null && password.length == 0) {
                    return null;
                }
                convertToBytes = convertToBytes(password);
                inputStream = new ByteArrayInputStream(convertToBytes);
            }
            char[] array2;
            char[] array = array2 = new char[128];
            int length = array2.length;
            int n = 0;
            int i = 0;
            while (i == 0) {
                final int read;
                switch (read = inputStream.read()) {
                    case -1:
                    case 10: {
                        i = 1;
                        continue;
                    }
                    case 13: {
                        final int read2 = inputStream.read();
                        if (read2 != 10 && read2 != -1) {
                            if (!(inputStream instanceof PushbackInputStream)) {
                                inputStream = new PushbackInputStream(inputStream);
                            }
                            ((PushbackInputStream)inputStream).unread(read2);
                            break;
                        }
                        i = 1;
                        continue;
                    }
                }
                if (--length < 0) {
                    array2 = new char[n + 128];
                    length = array2.length - n - 1;
                    System.arraycopy(array, 0, array2, 0, n);
                    Arrays.fill(array, ' ');
                    array = array2;
                }
                array2[n++] = (char)read;
            }
            if (n == 0) {
                return null;
            }
            final char[] array3 = new char[n];
            System.arraycopy(array2, 0, array3, 0, n);
            Arrays.fill(array2, ' ');
            return array3;
        }
        finally {
            if (password != null) {
                Arrays.fill(password, ' ');
            }
            if (convertToBytes != null) {
                Arrays.fill(convertToBytes, (byte)0);
            }
        }
    }
    
    private static byte[] convertToBytes(final char[] array) {
        if (Password.enc == null) {
            synchronized (Password.class) {
                Password.enc = SharedSecrets.getJavaIOAccess().charset().newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
            }
        }
        final byte[] array2 = new byte[(int)(Password.enc.maxBytesPerChar() * array.length)];
        final ByteBuffer wrap = ByteBuffer.wrap(array2);
        synchronized (Password.enc) {
            Password.enc.reset().encode(CharBuffer.wrap(array), wrap, true);
        }
        if (wrap.position() < array2.length) {
            array2[wrap.position()] = 10;
        }
        return array2;
    }
}
