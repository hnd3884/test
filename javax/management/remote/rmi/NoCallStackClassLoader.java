package javax.management.remote.rmi;

import java.security.ProtectionDomain;

class NoCallStackClassLoader extends ClassLoader
{
    private final String[] classNames;
    private final byte[][] byteCodes;
    private final String[] referencedClassNames;
    private final ClassLoader referencedClassLoader;
    private final ProtectionDomain protectionDomain;
    
    public NoCallStackClassLoader(final String s, final byte[] array, final String[] array2, final ClassLoader classLoader, final ProtectionDomain protectionDomain) {
        this(new String[] { s }, new byte[][] { array }, array2, classLoader, protectionDomain);
    }
    
    public NoCallStackClassLoader(final String[] classNames, final byte[][] byteCodes, final String[] referencedClassNames, final ClassLoader referencedClassLoader, final ProtectionDomain protectionDomain) {
        super(null);
        if (classNames == null || classNames.length == 0 || byteCodes == null || classNames.length != byteCodes.length || referencedClassNames == null || protectionDomain == null) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < classNames.length; ++i) {
            if (classNames[i] == null || byteCodes[i] == null) {
                throw new IllegalArgumentException();
            }
        }
        for (int j = 0; j < referencedClassNames.length; ++j) {
            if (referencedClassNames[j] == null) {
                throw new IllegalArgumentException();
            }
        }
        this.classNames = classNames;
        this.byteCodes = byteCodes;
        this.referencedClassNames = referencedClassNames;
        this.referencedClassLoader = referencedClassLoader;
        this.protectionDomain = protectionDomain;
    }
    
    @Override
    protected Class<?> findClass(final String s) throws ClassNotFoundException {
        for (int i = 0; i < this.classNames.length; ++i) {
            if (s.equals(this.classNames[i])) {
                return this.defineClass(this.classNames[i], this.byteCodes[i], 0, this.byteCodes[i].length, this.protectionDomain);
            }
        }
        if (this.referencedClassLoader != null) {
            for (int j = 0; j < this.referencedClassNames.length; ++j) {
                if (s.equals(this.referencedClassNames[j])) {
                    return this.referencedClassLoader.loadClass(s);
                }
            }
        }
        throw new ClassNotFoundException(s);
    }
    
    public static byte[] stringToBytes(final String s) {
        final int length = s.length();
        final byte[] array = new byte[length];
        for (int i = 0; i < length; ++i) {
            array[i] = (byte)s.charAt(i);
        }
        return array;
    }
}
