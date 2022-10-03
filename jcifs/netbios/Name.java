package jcifs.netbios;

import jcifs.Config;
import jcifs.util.Hexdump;
import java.io.UnsupportedEncodingException;

public class Name
{
    private static final int TYPE_OFFSET = 31;
    private static final int SCOPE_OFFSET = 33;
    private static final String DEFAULT_SCOPE;
    static final String OEM_ENCODING;
    public String name;
    public String scope;
    public int hexCode;
    int srcHashCode;
    
    Name() {
    }
    
    public Name(String name, final int hexCode, final String scope) {
        if (name.length() > 15) {
            name = name.substring(0, 15);
        }
        this.name = name.toUpperCase();
        this.hexCode = hexCode;
        this.scope = ((scope != null && scope.length() > 0) ? scope : Name.DEFAULT_SCOPE);
        this.srcHashCode = 0;
    }
    
    int writeWireFormat(final byte[] dst, final int dstIndex) {
        dst[dstIndex] = 32;
        try {
            byte[] tmp;
            int i;
            for (tmp = this.name.getBytes(Name.OEM_ENCODING), i = 0; i < tmp.length; ++i) {
                dst[dstIndex + (2 * i + 1)] = (byte)(((tmp[i] & 0xF0) >> 4) + 65);
                dst[dstIndex + (2 * i + 2)] = (byte)((tmp[i] & 0xF) + 65);
            }
            while (i < 15) {
                dst[dstIndex + (2 * i + 1)] = 67;
                dst[dstIndex + (2 * i + 2)] = 65;
                ++i;
            }
            dst[dstIndex + 31] = (byte)(((this.hexCode & 0xF0) >> 4) + 65);
            dst[dstIndex + 31 + 1] = (byte)((this.hexCode & 0xF) + 65);
        }
        catch (final UnsupportedEncodingException ex) {}
        return 33 + this.writeScopeWireFormat(dst, dstIndex + 33);
    }
    
    int readWireFormat(final byte[] src, final int srcIndex) {
        final byte[] tmp = new byte[33];
        int length = 15;
        for (int i = 0; i < 15; ++i) {
            tmp[i] = (byte)((src[srcIndex + (2 * i + 1)] & 0xFF) - 65 << 4);
            final byte[] array = tmp;
            final int n = i;
            array[n] |= (byte)((src[srcIndex + (2 * i + 2)] & 0xFF) - 65 & 0xF);
            if (tmp[i] != 32) {
                length = i + 1;
            }
        }
        try {
            this.name = new String(tmp, 0, length, Name.OEM_ENCODING);
        }
        catch (final UnsupportedEncodingException ex) {}
        this.hexCode = (src[srcIndex + 31] & 0xFF) - 65 << 4;
        this.hexCode |= ((src[srcIndex + 31 + 1] & 0xFF) - 65 & 0xF);
        return 33 + this.readScopeWireFormat(src, srcIndex + 33);
    }
    
    int writeScopeWireFormat(final byte[] dst, int dstIndex) {
        if (this.scope == null) {
            dst[dstIndex] = 0;
            return 1;
        }
        dst[dstIndex++] = 46;
        try {
            System.arraycopy(this.scope.getBytes(Name.OEM_ENCODING), 0, dst, dstIndex, this.scope.length());
        }
        catch (final UnsupportedEncodingException ex) {}
        dstIndex += this.scope.length();
        dst[dstIndex++] = 0;
        int i = dstIndex - 2;
        final int e = i - this.scope.length();
        int c = 0;
        do {
            if (dst[i] == 46) {
                dst[i] = (byte)c;
                c = 0;
            }
            else {
                ++c;
            }
        } while (i-- > e);
        return this.scope.length() + 2;
    }
    
    int readScopeWireFormat(final byte[] src, int srcIndex) {
        final int start = srcIndex;
        int n;
        if ((n = (src[srcIndex++] & 0xFF)) == 0) {
            this.scope = null;
            return 1;
        }
        try {
            final StringBuffer sb = new StringBuffer(new String(src, srcIndex, n, Name.OEM_ENCODING));
            for (srcIndex += n; (n = (src[srcIndex++] & 0xFF)) != 0; srcIndex += n) {
                sb.append('.').append(new String(src, srcIndex, n, Name.OEM_ENCODING));
            }
            this.scope = sb.toString();
        }
        catch (final UnsupportedEncodingException ex) {}
        return srcIndex - start;
    }
    
    public int hashCode() {
        int result = this.name.hashCode();
        result += 65599 * this.hexCode;
        result += 65599 * this.srcHashCode;
        if (this.scope != null && this.scope.length() != 0) {
            result += this.scope.hashCode();
        }
        return result;
    }
    
    public boolean equals(final Object obj) {
        if (!(obj instanceof Name)) {
            return false;
        }
        final Name n = (Name)obj;
        if (this.scope == null && n.scope == null) {
            return this.name.equals(n.name) && this.hexCode == n.hexCode;
        }
        return this.name.equals(n.name) && this.hexCode == n.hexCode && this.scope.equals(n.scope);
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        String n = this.name;
        if (n == null) {
            n = "null";
        }
        else if (n.charAt(0) == '\u0001') {
            final char[] c = n.toCharArray();
            c[0] = '.';
            c[14] = (c[1] = '.');
            n = new String(c);
        }
        sb.append(n).append("<").append(Hexdump.toHexString(this.hexCode, 2)).append(">");
        if (this.scope != null) {
            sb.append(".").append(this.scope);
        }
        return sb.toString();
    }
    
    static {
        DEFAULT_SCOPE = Config.getProperty("jcifs.netbios.scope");
        OEM_ENCODING = Config.getProperty("jcifs.encoding", System.getProperty("file.encoding"));
    }
}
