package com.maverick.ssh;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.io.File;
import java.math.BigInteger;

class b
{
    String k;
    String d;
    String e;
    String j;
    long b;
    String i;
    int c;
    int f;
    BigInteger h;
    BigInteger g;
    
    b() {
        this.c = 8;
        this.h = new BigInteger(new byte[] { 0, -114, 50, 88, -53, 37, -110, -74, -67, 34, 105, 25, 91, 43, -35, 116, -13, 86, 51, -124, 59, 6, -117, 107, 89, 115, 77, -127, -117, 8, -54, -35, -70, -32, -99, -84, -51, 63, 56, 77, -18, -7, 82, -66, -62, 68, -49, 29, 15, 106, 39, 107, 68, 66, 22, -101, 99, 44, -20, -77, 75, -103, -12, 101, -100, -127, 36, 43, 99, -34, -33, -60, -104, -64, 58, 120, -72, 46, 94, -34, 23, -76, 73, 41, -121, 3, 105, -49, -4, -104, 56, 53, 48, 87, -7, -78, -102, -85, -93, -87, 15, 112, 19, -75, 61, -31, -51, -71, -10, -127, 106, -20, 50, -103, 95, -126, -2, -12, 43, 85, 109, -7, -70, -75, -25, 122, 108, -22, -111 });
        this.g = new BigInteger(new byte[] { 1, 0, 1 });
        this.e();
    }
    
    void e() {
        this.b(this.getClass().getClassLoader().getResource("maverick-license.txt"));
        this.b(this.getClass().getClassLoader().getResource("META-INF/maverick-license.txt"));
        try {
            this.b(new File(System.getProperty("maverick.license.directory", System.getProperty("user.dir")) + File.separator + System.getProperty("maverick.license.filename", ".maverick-license.txt")));
        }
        catch (final Exception ex) {}
    }
    
    private void b(final URL url) {
        if (url != null) {
            try {
                final InputStream openStream = url.openStream();
                try {
                    this.b(openStream);
                }
                finally {
                    openStream.close();
                }
            }
            catch (final Exception ex) {
                System.err.println("WARNING: Failed to read Maverick license resource " + url + ". " + ex.getMessage());
            }
        }
    }
    
    private void b(final File file) {
        if (file.exists()) {
            try {
                final FileInputStream fileInputStream = new FileInputStream(file);
                try {
                    this.b(fileInputStream);
                }
                finally {
                    fileInputStream.close();
                }
            }
            catch (final Exception ex) {
                System.err.println("WARNING: Failed to read Maverick license file " + file + ". " + ex.getMessage());
            }
        }
    }
    
    void b(final InputStream inputStream) throws IOException {
        String s;
        for (s = this.c(inputStream); !s.startsWith("\"----BEGIN 3SP LICENSE"); s = s.substring(1)) {}
        while (!s.endsWith("----END 3SP LICENSE----\\r\\n")) {
            s = s.substring(0, s.length() - 1);
        }
        final StringBuffer sb = new StringBuffer();
        int n = 0;
        int n2 = 0;
        for (int i = 0; i < s.length(); ++i) {
            char char1 = s.charAt(i);
            if (char1 == '\"' && n == 0 && n2 == 0) {
                n = 1;
            }
            else if (char1 == '\"' && n != 0 && n2 == 0) {
                n = 0;
            }
            else if (n != 0) {
                if (n2 == 0 && char1 == '\\') {
                    n2 = 1;
                }
                else if (n != 0) {
                    if (n2 != 0) {
                        if (char1 == 'r') {
                            char1 = '\r';
                        }
                        else if (char1 == 'n') {
                            char1 = '\n';
                        }
                        n2 = 0;
                    }
                    sb.append(char1);
                }
            }
        }
        this.d = sb.toString();
    }
    
    final void b(final String d) {
        this.d = d;
    }
    
    final String c(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            int read;
            while ((read = inputStream.read()) > -1) {
                byteArrayOutputStream.write(read);
            }
            return new String(byteArrayOutputStream.toByteArray(), "UTF8");
        }
        finally {
            try {
                inputStream.close();
            }
            catch (final IOException ex) {}
            try {
                byteArrayOutputStream.close();
            }
            catch (final IOException ex2) {}
        }
    }
    
    final int d() {
        try {
            final long n = 1374169522052L;
            final long n2 = 31708800000L;
            if (this.d == null || this.d.equals("")) {
                return this.c = 8;
            }
            final _e e = new _e(this.h, this.g);
            final _c c = new _c(new ByteArrayInputStream(this.d.getBytes()));
            final String b = c.b();
            if (!b.equals("----BEGIN 3SP LICENSE----") && !b.equals("----BEGIN SSHTOOLS LICENSE----")) {
                return this.c = 2;
            }
            String k = "";
            String e2 = "";
            String i = "";
            final StringBuffer sb = new StringBuffer("");
            String b2;
            while ((b2 = c.b()) != null && !b2.equals("----END 3SP LICENSE----") && !b2.equals("----END SSHTOOLS LICENSE----")) {
                final int index = b2.indexOf(58);
                if (index > -1) {
                    final String trim = b2.substring(0, index).trim();
                    final String trim2 = b2.substring(index + 1).trim();
                    if (trim.equals("Licensee")) {
                        k = trim2;
                    }
                    else if (trim.equals("Comments")) {
                        e2 = trim2;
                    }
                    else {
                        if (trim.equals("Created")) {
                            continue;
                        }
                        if (trim.equals("Type")) {
                            this.j = trim2;
                        }
                        else if (trim.equals("Product")) {
                            i = trim2;
                        }
                        else {
                            if (trim.equals("Expires")) {
                                continue;
                            }
                            sb.append(trim);
                            for (int j = trim.length(); j < 8; ++j) {
                                sb.append(' ');
                            }
                            sb.append(": " + trim2);
                        }
                    }
                }
                else {
                    sb.append(b2);
                }
            }
            final String string = sb.toString();
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int l = 0;
            while (l < string.length()) {
                if (string.charAt(l) == '\r' || string.charAt(l) == '\n') {
                    ++l;
                }
                else {
                    byteArrayOutputStream.write(Integer.parseInt(string.substring(l, l + 2), 16));
                    l += 2;
                }
            }
            final DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
            final byte[] array = new byte[16];
            dataInputStream.readFully(array);
            final byte[] array2 = { 55, -121, 33, 9, 68, 73, 11, -37, -39, -1, 12, 48, 99, 49, 11, 55 };
            for (int n3 = 0; n3 < 16; ++n3) {
                final byte[] array3 = array;
                final int n4 = n3;
                array3[n4] ^= array2[n3];
            }
            final DataInputStream dataInputStream2 = new DataInputStream(new ByteArrayInputStream(array));
            final long long1 = dataInputStream2.readLong();
            final long long2 = dataInputStream2.readLong();
            final byte[] array4 = new byte[dataInputStream.available()];
            dataInputStream.readFully(array4);
            this.b = long2;
            int n5 = 0;
            while (n5 < 23) {
                final ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
                final _g g = new _g(byteArrayOutputStream2);
                g.b(i);
                g.b("3SP Ltd");
                g.b(e2);
                g.b(k);
                g.writeInt(this.f = 256 << n5);
                g.b(this.j);
                g.writeLong(long1);
                g.writeLong(long2);
                if (long2 <= System.currentTimeMillis()) {
                    return this.c = (0x1 | this.f);
                }
                this.e = e2;
                this.k = k;
                this.i = i;
                if (e.b(array4, byteArrayOutputStream2.toByteArray())) {
                    if (n > long1 + n2) {
                        return this.c = (0x10 | this.f);
                    }
                    return this.c = (0x4 | this.f);
                }
                else {
                    ++n5;
                }
            }
            return this.c = 2;
        }
        catch (final Throwable t) {
            return this.c = 2;
        }
    }
    
    String g() {
        return this.e;
    }
    
    String c() {
        return this.k;
    }
    
    int f() {
        return this.c;
    }
    
    int b() {
        return this.f;
    }
    
    abstract class _b
    {
        private byte[] b;
        private int c;
        private long d;
        
        protected _b() {
            this.b = new byte[4];
            this.c = 0;
        }
        
        public void b(final byte b) {
            this.b[this.c++] = b;
            if (this.c == this.b.length) {
                this.b(this.b, 0);
                this.c = 0;
            }
            ++this.d;
        }
        
        public void b(final byte[] array, int n, int i) {
            while (this.c != 0 && i > 0) {
                this.b(array[n]);
                ++n;
                --i;
            }
            while (i > this.b.length) {
                this.b(array, n);
                n += this.b.length;
                i -= this.b.length;
                this.d += this.b.length;
            }
            while (i > 0) {
                this.b(array[n]);
                ++n;
                --i;
            }
        }
        
        public void c() {
            final long n = this.d << 3;
            this.b((byte)(-128));
            while (this.c != 0) {
                this.b((byte)0);
            }
            this.b(n);
            this.d();
        }
        
        public void b() {
            this.d = 0L;
            this.c = 0;
            for (int i = 0; i < this.b.length; ++i) {
                this.b[i] = 0;
            }
        }
        
        protected abstract void b(final byte[] p0, final int p1);
        
        protected abstract void b(final long p0);
        
        protected abstract void d();
    }
    
    static class _c
    {
        InputStream b;
        
        _c(final InputStream b) {
            this.b = b;
        }
        
        String b() throws IOException {
            final StringBuffer sb = new StringBuffer();
            int read;
            while ((read = this.b.read()) > -1 && read != 10) {
                sb.append((char)read);
            }
            if (read == -1 && sb.length() == 0) {
                return null;
            }
            return new String(sb.toString().getBytes("UTF8"), "UTF8").trim();
        }
    }
    
    class _d
    {
        BigInteger d;
        BigInteger b;
        final byte[] c;
        
        public _d(final BigInteger b, final BigInteger d) {
            this.c = new byte[] { 48, 33, 48, 9, 6, 5, 43, 14, 3, 2, 26, 5, 0, 4, 20 };
            this.b = b;
            this.d = d;
        }
        
        public BigInteger b(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3) {
            return bigInteger.modPow(bigInteger3, bigInteger2);
        }
        
        public BigInteger b(final BigInteger bigInteger, final int n) throws IOException {
            final byte[] byteArray = bigInteger.toByteArray();
            if (byteArray[0] != n) {
                throw new IOException("PKCS1 padding type " + n + " is not valid");
            }
            int n2;
            for (n2 = 1; n2 < byteArray.length && byteArray[n2] != 0; ++n2) {
                if (n == 1 && byteArray[n2] != -1) {
                    throw new IOException("Corrupt data found in expected PKSC1 padding");
                }
            }
            if (n2 == byteArray.length) {
                throw new IOException("Corrupt data found in expected PKSC1 padding");
            }
            final byte[] array = new byte[byteArray.length - n2];
            System.arraycopy(byteArray, n2, array, 0, array.length);
            return new BigInteger(1, array);
        }
        
        public boolean b(byte[] byteArray, final byte[] array) {
            try {
                byteArray = this.b(this.b(new BigInteger(1, byteArray), this.b, this.d), 1).toByteArray();
                final _f f = new _f();
                f.b(array, 0, array.length);
                final byte[] array2 = new byte[f.e()];
                f.c(array2, 0);
                if (array2.length != byteArray.length - this.c.length) {
                    return false;
                }
                byte[] c = this.c;
                for (int i = 0, n = 0; i < byteArray.length; ++i, ++n) {
                    if (i == this.c.length) {
                        c = array2;
                        n = 0;
                    }
                    if (byteArray[i] != c[n]) {
                        return false;
                    }
                }
                return true;
            }
            catch (final IOException ex) {
                return false;
            }
        }
    }
    
    class _f extends _b
    {
        private final int i = 20;
        private int m;
        private int k;
        private int h;
        private int g;
        private int f;
        private int[] e;
        private int p;
        private final int o = 1518500249;
        private final int n = 1859775393;
        private final int l = -1894007588;
        private final int j = -899497514;
        
        public _f() {
            this.e = new int[80];
            this.b();
        }
        
        public int e() {
            return 20;
        }
        
        protected void b(final byte[] array, final int n) {
            this.e[this.p++] = ((array[n] & 0xFF) << 24 | (array[n + 1] & 0xFF) << 16 | (array[n + 2] & 0xFF) << 8 | (array[n + 3] & 0xFF));
            if (this.p == 16) {
                this.d();
            }
        }
        
        private void b(final int n, final byte[] array, final int n2) {
            array[n2] = (byte)(n >>> 24);
            array[n2 + 1] = (byte)(n >>> 16);
            array[n2 + 2] = (byte)(n >>> 8);
            array[n2 + 3] = (byte)n;
        }
        
        protected void b(final long n) {
            if (this.p > 14) {
                this.d();
            }
            this.e[14] = (int)(n >>> 32);
            this.e[15] = (int)(n & -1L);
        }
        
        public int c(final byte[] array, final int n) {
            this.c();
            this.b(this.m, array, n);
            this.b(this.k, array, n + 4);
            this.b(this.h, array, n + 8);
            this.b(this.g, array, n + 12);
            this.b(this.f, array, n + 16);
            this.b();
            return 20;
        }
        
        public void b() {
            super.b();
            this.m = 1732584193;
            this.k = -271733879;
            this.h = -1732584194;
            this.g = 271733878;
            this.f = -1009589776;
            this.p = 0;
            for (int i = 0; i != this.e.length; ++i) {
                this.e[i] = 0;
            }
        }
        
        private int c(final int n, final int n2, final int n3) {
            return (n & n2) | (~n & n3);
        }
        
        private int d(final int n, final int n2, final int n3) {
            return n ^ n2 ^ n3;
        }
        
        private int b(final int n, final int n2, final int n3) {
            return (n & n2) | (n & n3) | (n2 & n3);
        }
        
        private int b(final int n, final int n2) {
            return n << n2 | n >>> 32 - n2;
        }
        
        protected void d() {
            for (int i = 16; i <= 79; ++i) {
                this.e[i] = this.b(this.e[i - 3] ^ this.e[i - 8] ^ this.e[i - 14] ^ this.e[i - 16], 1);
            }
            int m = this.m;
            int k = this.k;
            int n = this.h;
            int g = this.g;
            int f = this.f;
            for (int j = 0; j <= 19; ++j) {
                final int n2 = this.b(m, 5) + this.c(k, n, g) + f + this.e[j] + 1518500249;
                f = g;
                g = n;
                n = this.b(k, 30);
                k = m;
                m = n2;
            }
            for (int l = 20; l <= 39; ++l) {
                final int n3 = this.b(m, 5) + this.d(k, n, g) + f + this.e[l] + 1859775393;
                f = g;
                g = n;
                n = this.b(k, 30);
                k = m;
                m = n3;
            }
            for (int n4 = 40; n4 <= 59; ++n4) {
                final int n5 = this.b(m, 5) + this.b(k, n, g) + f + this.e[n4] - 1894007588;
                f = g;
                g = n;
                n = this.b(k, 30);
                k = m;
                m = n5;
            }
            for (int n6 = 60; n6 <= 79; ++n6) {
                final int n7 = this.b(m, 5) + this.d(k, n, g) + f + this.e[n6] - 899497514;
                f = g;
                g = n;
                n = this.b(k, 30);
                k = m;
                m = n7;
            }
            this.m += m;
            this.k += k;
            this.h += n;
            this.g += g;
            this.f += f;
            this.p = 0;
            for (int n8 = 0; n8 != this.e.length; ++n8) {
                this.e[n8] = 0;
            }
        }
    }
    
    class _e extends _d
    {
        _e(final BigInteger bigInteger, final BigInteger bigInteger2) {
            super(bigInteger, bigInteger2);
        }
    }
    
    static class _g extends DataOutputStream
    {
        _g(final OutputStream outputStream) {
            super(outputStream);
        }
        
        void b(final String s) throws IOException {
            final byte[] bytes = s.getBytes("UTF8");
            this.writeInt(bytes.length);
            this.write(bytes);
        }
    }
}
