package sun.text.normalizer;

import java.util.HashMap;

public final class VersionInfo
{
    private int m_version_;
    private static final HashMap<Integer, Object> MAP_;
    private static final String INVALID_VERSION_NUMBER_ = "Invalid version number: Version number may be negative or greater than 255";
    
    public static VersionInfo getInstance(final String s) {
        final int length = s.length();
        final int[] array = { 0, 0, 0, 0 };
        int n;
        int n2;
        for (n = 0, n2 = 0; n < 4 && n2 < length; ++n2) {
            final char char1 = s.charAt(n2);
            if (char1 == '.') {
                ++n;
            }
            else {
                final char c = (char)(char1 - '0');
                if (c < '\0' || c > '\t') {
                    throw new IllegalArgumentException("Invalid version number: Version number may be negative or greater than 255");
                }
                final int[] array2 = array;
                final int n3 = n;
                array2[n3] *= 10;
                final int[] array3 = array;
                final int n4 = n;
                array3[n4] += c;
            }
        }
        if (n2 != length) {
            throw new IllegalArgumentException("Invalid version number: String '" + s + "' exceeds version format");
        }
        for (int i = 0; i < 4; ++i) {
            if (array[i] < 0 || array[i] > 255) {
                throw new IllegalArgumentException("Invalid version number: Version number may be negative or greater than 255");
            }
        }
        return getInstance(array[0], array[1], array[2], array[3]);
    }
    
    public static VersionInfo getInstance(final int n, final int n2, final int n3, final int n4) {
        if (n < 0 || n > 255 || n2 < 0 || n2 > 255 || n3 < 0 || n3 > 255 || n4 < 0 || n4 > 255) {
            throw new IllegalArgumentException("Invalid version number: Version number may be negative or greater than 255");
        }
        final int int1 = getInt(n, n2, n3, n4);
        final Integer value = int1;
        Object value2 = VersionInfo.MAP_.get(value);
        if (value2 == null) {
            value2 = new VersionInfo(int1);
            VersionInfo.MAP_.put(value, value2);
        }
        return (VersionInfo)value2;
    }
    
    public int compareTo(final VersionInfo versionInfo) {
        return this.m_version_ - versionInfo.m_version_;
    }
    
    private VersionInfo(final int version_) {
        this.m_version_ = version_;
    }
    
    private static int getInt(final int n, final int n2, final int n3, final int n4) {
        return n << 24 | n2 << 16 | n3 << 8 | n4;
    }
    
    static {
        MAP_ = new HashMap<Integer, Object>();
    }
}
