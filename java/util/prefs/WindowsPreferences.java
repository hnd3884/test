package java.util.prefs;

import java.util.StringTokenizer;
import java.io.ByteArrayOutputStream;
import sun.util.logging.PlatformLogger;

class WindowsPreferences extends AbstractPreferences
{
    private static PlatformLogger logger;
    private static final byte[] WINDOWS_ROOT_PATH;
    private static final int HKEY_CURRENT_USER = -2147483647;
    private static final int HKEY_LOCAL_MACHINE = -2147483646;
    private static final int USER_ROOT_NATIVE_HANDLE = -2147483647;
    private static final int SYSTEM_ROOT_NATIVE_HANDLE = -2147483646;
    private static final int MAX_WINDOWS_PATH_LENGTH = 256;
    private static volatile Preferences userRoot;
    private static volatile Preferences systemRoot;
    private static final int ERROR_SUCCESS = 0;
    private static final int ERROR_FILE_NOT_FOUND = 2;
    private static final int ERROR_ACCESS_DENIED = 5;
    private static final int NATIVE_HANDLE = 0;
    private static final int ERROR_CODE = 1;
    private static final int SUBKEYS_NUMBER = 0;
    private static final int VALUES_NUMBER = 2;
    private static final int MAX_KEY_LENGTH = 3;
    private static final int MAX_VALUE_NAME_LENGTH = 4;
    private static final int DISPOSITION = 2;
    private static final int REG_CREATED_NEW_KEY = 1;
    private static final int REG_OPENED_EXISTING_KEY = 2;
    private static final int NULL_NATIVE_HANDLE = 0;
    private static final int DELETE = 65536;
    private static final int KEY_QUERY_VALUE = 1;
    private static final int KEY_SET_VALUE = 2;
    private static final int KEY_CREATE_SUB_KEY = 4;
    private static final int KEY_ENUMERATE_SUB_KEYS = 8;
    private static final int KEY_READ = 131097;
    private static final int KEY_WRITE = 131078;
    private static final int KEY_ALL_ACCESS = 983103;
    private static int INIT_SLEEP_TIME;
    private static int MAX_ATTEMPTS;
    private boolean isBackingStoreAvailable;
    
    static Preferences getUserRoot() {
        Preferences preferences = WindowsPreferences.userRoot;
        if (preferences == null) {
            synchronized (WindowsPreferences.class) {
                preferences = WindowsPreferences.userRoot;
                if (preferences == null) {
                    preferences = (WindowsPreferences.userRoot = new WindowsPreferences(-2147483647, WindowsPreferences.WINDOWS_ROOT_PATH));
                }
            }
        }
        return preferences;
    }
    
    static Preferences getSystemRoot() {
        Preferences preferences = WindowsPreferences.systemRoot;
        if (preferences == null) {
            synchronized (WindowsPreferences.class) {
                preferences = WindowsPreferences.systemRoot;
                if (preferences == null) {
                    preferences = (WindowsPreferences.systemRoot = new WindowsPreferences(-2147483646, WindowsPreferences.WINDOWS_ROOT_PATH));
                }
            }
        }
        return preferences;
    }
    
    private static native int[] WindowsRegOpenKey(final int p0, final byte[] p1, final int p2);
    
    private static int[] WindowsRegOpenKey1(final int n, final byte[] array, final int n2) {
        int[] array2 = WindowsRegOpenKey(n, array, n2);
        if (array2[1] == 0) {
            return array2;
        }
        if (array2[1] == 2) {
            logger().warning("Trying to recreate Windows registry node " + byteArrayToString(array) + " at root 0x" + Integer.toHexString(n) + ".");
            WindowsRegCloseKey(WindowsRegCreateKeyEx(n, array)[0]);
            return WindowsRegOpenKey(n, array, n2);
        }
        if (array2[1] != 5) {
            long n3 = WindowsPreferences.INIT_SLEEP_TIME;
            for (int i = 0; i < WindowsPreferences.MAX_ATTEMPTS; ++i) {
                try {
                    Thread.sleep(n3);
                }
                catch (final InterruptedException ex) {
                    return array2;
                }
                n3 *= 2L;
                array2 = WindowsRegOpenKey(n, array, n2);
                if (array2[1] == 0) {
                    return array2;
                }
            }
        }
        return array2;
    }
    
    private static native int WindowsRegCloseKey(final int p0);
    
    private static native int[] WindowsRegCreateKeyEx(final int p0, final byte[] p1);
    
    private static int[] WindowsRegCreateKeyEx1(final int n, final byte[] array) {
        int[] array2 = WindowsRegCreateKeyEx(n, array);
        if (array2[1] == 0) {
            return array2;
        }
        long n2 = WindowsPreferences.INIT_SLEEP_TIME;
        for (int i = 0; i < WindowsPreferences.MAX_ATTEMPTS; ++i) {
            try {
                Thread.sleep(n2);
            }
            catch (final InterruptedException ex) {
                return array2;
            }
            n2 *= 2L;
            array2 = WindowsRegCreateKeyEx(n, array);
            if (array2[1] == 0) {
                return array2;
            }
        }
        return array2;
    }
    
    private static native int WindowsRegDeleteKey(final int p0, final byte[] p1);
    
    private static native int WindowsRegFlushKey(final int p0);
    
    private static int WindowsRegFlushKey1(final int n) {
        int n2 = WindowsRegFlushKey(n);
        if (n2 == 0) {
            return n2;
        }
        long n3 = WindowsPreferences.INIT_SLEEP_TIME;
        for (int i = 0; i < WindowsPreferences.MAX_ATTEMPTS; ++i) {
            try {
                Thread.sleep(n3);
            }
            catch (final InterruptedException ex) {
                return n2;
            }
            n3 *= 2L;
            n2 = WindowsRegFlushKey(n);
            if (n2 == 0) {
                return n2;
            }
        }
        return n2;
    }
    
    private static native byte[] WindowsRegQueryValueEx(final int p0, final byte[] p1);
    
    private static native int WindowsRegSetValueEx(final int p0, final byte[] p1, final byte[] p2);
    
    private static int WindowsRegSetValueEx1(final int n, final byte[] array, final byte[] array2) {
        int n2 = WindowsRegSetValueEx(n, array, array2);
        if (n2 == 0) {
            return n2;
        }
        long n3 = WindowsPreferences.INIT_SLEEP_TIME;
        for (int i = 0; i < WindowsPreferences.MAX_ATTEMPTS; ++i) {
            try {
                Thread.sleep(n3);
            }
            catch (final InterruptedException ex) {
                return n2;
            }
            n3 *= 2L;
            n2 = WindowsRegSetValueEx(n, array, array2);
            if (n2 == 0) {
                return n2;
            }
        }
        return n2;
    }
    
    private static native int WindowsRegDeleteValue(final int p0, final byte[] p1);
    
    private static native int[] WindowsRegQueryInfoKey(final int p0);
    
    private static int[] WindowsRegQueryInfoKey1(final int n) {
        int[] array = WindowsRegQueryInfoKey(n);
        if (array[1] == 0) {
            return array;
        }
        long n2 = WindowsPreferences.INIT_SLEEP_TIME;
        for (int i = 0; i < WindowsPreferences.MAX_ATTEMPTS; ++i) {
            try {
                Thread.sleep(n2);
            }
            catch (final InterruptedException ex) {
                return array;
            }
            n2 *= 2L;
            array = WindowsRegQueryInfoKey(n);
            if (array[1] == 0) {
                return array;
            }
        }
        return array;
    }
    
    private static native byte[] WindowsRegEnumKeyEx(final int p0, final int p1, final int p2);
    
    private static byte[] WindowsRegEnumKeyEx1(final int n, final int n2, final int n3) {
        byte[] array = WindowsRegEnumKeyEx(n, n2, n3);
        if (array != null) {
            return array;
        }
        long n4 = WindowsPreferences.INIT_SLEEP_TIME;
        for (int i = 0; i < WindowsPreferences.MAX_ATTEMPTS; ++i) {
            try {
                Thread.sleep(n4);
            }
            catch (final InterruptedException ex) {
                return array;
            }
            n4 *= 2L;
            array = WindowsRegEnumKeyEx(n, n2, n3);
            if (array != null) {
                return array;
            }
        }
        return array;
    }
    
    private static native byte[] WindowsRegEnumValue(final int p0, final int p1, final int p2);
    
    private static byte[] WindowsRegEnumValue1(final int n, final int n2, final int n3) {
        byte[] array = WindowsRegEnumValue(n, n2, n3);
        if (array != null) {
            return array;
        }
        long n4 = WindowsPreferences.INIT_SLEEP_TIME;
        for (int i = 0; i < WindowsPreferences.MAX_ATTEMPTS; ++i) {
            try {
                Thread.sleep(n4);
            }
            catch (final InterruptedException ex) {
                return array;
            }
            n4 *= 2L;
            array = WindowsRegEnumValue(n, n2, n3);
            if (array != null) {
                return array;
            }
        }
        return array;
    }
    
    private WindowsPreferences(final WindowsPreferences windowsPreferences, final String s) {
        super(windowsPreferences, s);
        this.isBackingStoreAvailable = true;
        final int openKey = windowsPreferences.openKey(4, 131097);
        if (openKey == 0) {
            this.isBackingStoreAvailable = false;
            return;
        }
        final int[] windowsRegCreateKeyEx1 = WindowsRegCreateKeyEx1(openKey, toWindowsName(s));
        if (windowsRegCreateKeyEx1[1] != 0) {
            logger().warning("Could not create windows registry node " + byteArrayToString(this.windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(this.rootNativeHandle()) + ". Windows RegCreateKeyEx(...) returned error code " + windowsRegCreateKeyEx1[1] + ".");
            this.isBackingStoreAvailable = false;
            return;
        }
        this.newNode = (windowsRegCreateKeyEx1[2] == 1);
        this.closeKey(openKey);
        this.closeKey(windowsRegCreateKeyEx1[0]);
    }
    
    private WindowsPreferences(final int n, final byte[] array) {
        super(null, "");
        this.isBackingStoreAvailable = true;
        final int[] windowsRegCreateKeyEx1 = WindowsRegCreateKeyEx1(n, array);
        if (windowsRegCreateKeyEx1[1] != 0) {
            logger().warning("Could not open/create prefs root node " + byteArrayToString(this.windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(this.rootNativeHandle()) + ". Windows RegCreateKeyEx(...) returned error code " + windowsRegCreateKeyEx1[1] + ".");
            this.isBackingStoreAvailable = false;
            return;
        }
        this.newNode = (windowsRegCreateKeyEx1[2] == 1);
        this.closeKey(windowsRegCreateKeyEx1[0]);
    }
    
    private byte[] windowsAbsolutePath() {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(WindowsPreferences.WINDOWS_ROOT_PATH, 0, WindowsPreferences.WINDOWS_ROOT_PATH.length - 1);
        final StringTokenizer stringTokenizer = new StringTokenizer(this.absolutePath(), "/");
        while (stringTokenizer.hasMoreTokens()) {
            byteArrayOutputStream.write(92);
            final byte[] windowsName = toWindowsName(stringTokenizer.nextToken());
            byteArrayOutputStream.write(windowsName, 0, windowsName.length - 1);
        }
        byteArrayOutputStream.write(0);
        return byteArrayOutputStream.toByteArray();
    }
    
    private int openKey(final int n) {
        return this.openKey(n, n);
    }
    
    private int openKey(final int n, final int n2) {
        return this.openKey(this.windowsAbsolutePath(), n, n2);
    }
    
    private int openKey(final byte[] array, final int n, final int n2) {
        if (array.length <= 257) {
            int[] array2 = WindowsRegOpenKey1(this.rootNativeHandle(), array, n);
            if (array2[1] == 5 && n2 != n) {
                array2 = WindowsRegOpenKey1(this.rootNativeHandle(), array, n2);
            }
            if (array2[1] != 0) {
                logger().warning("Could not open windows registry node " + byteArrayToString(this.windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(this.rootNativeHandle()) + ". Windows RegOpenKey(...) returned error code " + array2[1] + ".");
                array2[0] = 0;
                if (array2[1] == 5) {
                    throw new SecurityException("Could not open windows registry node " + byteArrayToString(this.windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(this.rootNativeHandle()) + ": Access denied");
                }
            }
            return array2[0];
        }
        return this.openKey(this.rootNativeHandle(), array, n, n2);
    }
    
    private int openKey(final int n, final byte[] array, final int n2, final int n3) {
        if (array.length <= 257) {
            int[] array2 = WindowsRegOpenKey1(n, array, n2);
            if (array2[1] == 5 && n3 != n2) {
                array2 = WindowsRegOpenKey1(n, array, n3);
            }
            if (array2[1] != 0) {
                logger().warning("Could not open windows registry node " + byteArrayToString(this.windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(n) + ". Windows RegOpenKey(...) returned error code " + array2[1] + ".");
                array2[0] = 0;
            }
            return array2[0];
        }
        int n4 = -1;
        for (int i = 256; i > 0; --i) {
            if (array[i] == 92) {
                n4 = i;
                break;
            }
        }
        final byte[] array3 = new byte[n4 + 1];
        System.arraycopy(array, 0, array3, 0, n4);
        array3[n4] = 0;
        final byte[] array4 = new byte[array.length - n4 - 1];
        System.arraycopy(array, n4 + 1, array4, 0, array4.length);
        final int openKey = this.openKey(n, array3, n2, n3);
        if (openKey == 0) {
            return 0;
        }
        final int openKey2 = this.openKey(openKey, array4, n2, n3);
        this.closeKey(openKey);
        return openKey2;
    }
    
    private void closeKey(final int n) {
        final int windowsRegCloseKey = WindowsRegCloseKey(n);
        if (windowsRegCloseKey != 0) {
            logger().warning("Could not close windows registry node " + byteArrayToString(this.windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(this.rootNativeHandle()) + ". Windows RegCloseKey(...) returned error code " + windowsRegCloseKey + ".");
        }
    }
    
    @Override
    protected void putSpi(final String s, final String s2) {
        final int openKey = this.openKey(2);
        if (openKey == 0) {
            this.isBackingStoreAvailable = false;
            return;
        }
        final int windowsRegSetValueEx1 = WindowsRegSetValueEx1(openKey, toWindowsName(s), toWindowsValueString(s2));
        if (windowsRegSetValueEx1 != 0) {
            logger().warning("Could not assign value to key " + byteArrayToString(toWindowsName(s)) + " at Windows registry node " + byteArrayToString(this.windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(this.rootNativeHandle()) + ". Windows RegSetValueEx(...) returned error code " + windowsRegSetValueEx1 + ".");
            this.isBackingStoreAvailable = false;
        }
        this.closeKey(openKey);
    }
    
    @Override
    protected String getSpi(final String s) {
        final int openKey = this.openKey(1);
        if (openKey == 0) {
            return null;
        }
        final byte[] windowsRegQueryValueEx = WindowsRegQueryValueEx(openKey, toWindowsName(s));
        if (windowsRegQueryValueEx == null) {
            this.closeKey(openKey);
            return null;
        }
        this.closeKey(openKey);
        return toJavaValueString(windowsRegQueryValueEx);
    }
    
    @Override
    protected void removeSpi(final String s) {
        final int openKey = this.openKey(2);
        if (openKey == 0) {
            return;
        }
        final int windowsRegDeleteValue = WindowsRegDeleteValue(openKey, toWindowsName(s));
        if (windowsRegDeleteValue != 0 && windowsRegDeleteValue != 2) {
            logger().warning("Could not delete windows registry value " + byteArrayToString(this.windowsAbsolutePath()) + "\\" + toWindowsName(s) + " at root 0x" + Integer.toHexString(this.rootNativeHandle()) + ". Windows RegDeleteValue(...) returned error code " + windowsRegDeleteValue + ".");
            this.isBackingStoreAvailable = false;
        }
        this.closeKey(openKey);
    }
    
    @Override
    protected String[] keysSpi() throws BackingStoreException {
        final int openKey = this.openKey(1);
        if (openKey == 0) {
            throw new BackingStoreException("Could not open windows registry node " + byteArrayToString(this.windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(this.rootNativeHandle()) + ".");
        }
        final int[] windowsRegQueryInfoKey1 = WindowsRegQueryInfoKey1(openKey);
        if (windowsRegQueryInfoKey1[1] != 0) {
            final String string = "Could not query windows registry node " + byteArrayToString(this.windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(this.rootNativeHandle()) + ". Windows RegQueryInfoKeyEx(...) returned error code " + windowsRegQueryInfoKey1[1] + ".";
            logger().warning(string);
            throw new BackingStoreException(string);
        }
        final int n = windowsRegQueryInfoKey1[4];
        final int n2 = windowsRegQueryInfoKey1[2];
        if (n2 == 0) {
            this.closeKey(openKey);
            return new String[0];
        }
        final String[] array = new String[n2];
        for (int i = 0; i < n2; ++i) {
            final byte[] windowsRegEnumValue1 = WindowsRegEnumValue1(openKey, i, n + 1);
            if (windowsRegEnumValue1 == null) {
                final String string2 = "Could not enumerate value #" + i + "  of windows node " + byteArrayToString(this.windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(this.rootNativeHandle()) + ".";
                logger().warning(string2);
                throw new BackingStoreException(string2);
            }
            array[i] = toJavaName(windowsRegEnumValue1);
        }
        this.closeKey(openKey);
        return array;
    }
    
    @Override
    protected String[] childrenNamesSpi() throws BackingStoreException {
        final int openKey = this.openKey(9);
        if (openKey == 0) {
            throw new BackingStoreException("Could not open windows registry node " + byteArrayToString(this.windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(this.rootNativeHandle()) + ".");
        }
        final int[] windowsRegQueryInfoKey1 = WindowsRegQueryInfoKey1(openKey);
        if (windowsRegQueryInfoKey1[1] != 0) {
            final String string = "Could not query windows registry node " + byteArrayToString(this.windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(this.rootNativeHandle()) + ". Windows RegQueryInfoKeyEx(...) returned error code " + windowsRegQueryInfoKey1[1] + ".";
            logger().warning(string);
            throw new BackingStoreException(string);
        }
        final int n = windowsRegQueryInfoKey1[3];
        final int n2 = windowsRegQueryInfoKey1[0];
        if (n2 == 0) {
            this.closeKey(openKey);
            return new String[0];
        }
        final String[] array = new String[n2];
        final String[] array2 = new String[n2];
        for (int i = 0; i < n2; ++i) {
            final byte[] windowsRegEnumKeyEx1 = WindowsRegEnumKeyEx1(openKey, i, n + 1);
            if (windowsRegEnumKeyEx1 == null) {
                final String string2 = "Could not enumerate key #" + i + "  of windows node " + byteArrayToString(this.windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(this.rootNativeHandle()) + ". ";
                logger().warning(string2);
                throw new BackingStoreException(string2);
            }
            array2[i] = toJavaName(windowsRegEnumKeyEx1);
        }
        this.closeKey(openKey);
        return array2;
    }
    
    @Override
    public void flush() throws BackingStoreException {
        if (this.isRemoved()) {
            this.parent.flush();
            return;
        }
        if (!this.isBackingStoreAvailable) {
            throw new BackingStoreException("flush(): Backing store not available.");
        }
        final int openKey = this.openKey(131097);
        if (openKey == 0) {
            throw new BackingStoreException("Could not open windows registry node " + byteArrayToString(this.windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(this.rootNativeHandle()) + ".");
        }
        final int windowsRegFlushKey1 = WindowsRegFlushKey1(openKey);
        if (windowsRegFlushKey1 != 0) {
            final String string = "Could not flush windows registry node " + byteArrayToString(this.windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(this.rootNativeHandle()) + ". Windows RegFlushKey(...) returned error code " + windowsRegFlushKey1 + ".";
            logger().warning(string);
            throw new BackingStoreException(string);
        }
        this.closeKey(openKey);
    }
    
    @Override
    public void sync() throws BackingStoreException {
        if (this.isRemoved()) {
            throw new IllegalStateException("Node has been removed");
        }
        this.flush();
    }
    
    @Override
    protected AbstractPreferences childSpi(final String s) {
        return new WindowsPreferences(this, s);
    }
    
    public void removeNodeSpi() throws BackingStoreException {
        final int openKey = ((WindowsPreferences)this.parent()).openKey(65536);
        if (openKey == 0) {
            throw new BackingStoreException("Could not open parent windows registry node of " + byteArrayToString(this.windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(this.rootNativeHandle()) + ".");
        }
        final int windowsRegDeleteKey = WindowsRegDeleteKey(openKey, toWindowsName(this.name()));
        if (windowsRegDeleteKey != 0) {
            final String string = "Could not delete windows registry node " + byteArrayToString(this.windowsAbsolutePath()) + " at root 0x" + Integer.toHexString(this.rootNativeHandle()) + ". Windows RegDeleteKeyEx(...) returned error code " + windowsRegDeleteKey + ".";
            logger().warning(string);
            throw new BackingStoreException(string);
        }
        this.closeKey(openKey);
    }
    
    private static String toJavaName(final byte[] array) {
        final String byteArrayToString = byteArrayToString(array);
        if (byteArrayToString.length() > 1 && byteArrayToString.substring(0, 2).equals("/!")) {
            return toJavaAlt64Name(byteArrayToString);
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < byteArrayToString.length(); ++i) {
            char char1;
            if ((char1 = byteArrayToString.charAt(i)) == '/') {
                char char2 = ' ';
                if (byteArrayToString.length() > i + 1 && (char2 = byteArrayToString.charAt(i + 1)) >= 'A' && char2 <= 'Z') {
                    char1 = char2;
                    ++i;
                }
                else if (byteArrayToString.length() > i + 1 && char2 == '/') {
                    char1 = '\\';
                    ++i;
                }
            }
            else if (char1 == '\\') {
                char1 = '/';
            }
            sb.append(char1);
        }
        return sb.toString();
    }
    
    private static String toJavaAlt64Name(final String s) {
        final byte[] altBase64ToByteArray = Base64.altBase64ToByteArray(s.substring(2));
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < altBase64ToByteArray.length; ++i) {
            sb.append((char)(((altBase64ToByteArray[i++] & 0xFF) << 8) + (altBase64ToByteArray[i] & 0xFF)));
        }
        return sb.toString();
    }
    
    private static byte[] toWindowsName(final String s) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            if (char1 < ' ' || char1 > '\u007f') {
                return toWindowsAlt64Name(s);
            }
            if (char1 == '\\') {
                sb.append("//");
            }
            else if (char1 == '/') {
                sb.append('\\');
            }
            else if (char1 >= 'A' && char1 <= 'Z') {
                sb.append('/').append(char1);
            }
            else {
                sb.append(char1);
            }
        }
        return stringToByteArray(sb.toString());
    }
    
    private static byte[] toWindowsAlt64Name(final String s) {
        final byte[] array = new byte[2 * s.length()];
        int n = 0;
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            array[n++] = (byte)(char1 >>> 8);
            array[n++] = (byte)char1;
        }
        return stringToByteArray("/!" + Base64.byteArrayToAltBase64(array));
    }
    
    private static String toJavaValueString(final byte[] array) {
        final String byteArrayToString = byteArrayToString(array);
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < byteArrayToString.length(); ++i) {
            char char1;
            if ((char1 = byteArrayToString.charAt(i)) == '/') {
                char char2 = ' ';
                if (byteArrayToString.length() > i + 1 && (char2 = byteArrayToString.charAt(i + 1)) == 'u') {
                    if (byteArrayToString.length() < i + 6) {
                        break;
                    }
                    char1 = (char)Integer.parseInt(byteArrayToString.substring(i + 2, i + 6), 16);
                    i += 5;
                }
                else if (byteArrayToString.length() > i + 1 && byteArrayToString.charAt(i + 1) >= 'A' && char2 <= 'Z') {
                    char1 = char2;
                    ++i;
                }
                else if (byteArrayToString.length() > i + 1 && char2 == '/') {
                    char1 = '\\';
                    ++i;
                }
            }
            else if (char1 == '\\') {
                char1 = '/';
            }
            sb.append(char1);
        }
        return sb.toString();
    }
    
    private static byte[] toWindowsValueString(final String s) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            if (char1 < ' ' || char1 > '\u007f') {
                sb.append("/u");
                final StringBuilder sb2 = new StringBuilder(Integer.toHexString(s.charAt(i)));
                sb2.reverse();
                for (int n = 4 - sb2.length(), j = 0; j < n; ++j) {
                    sb2.append('0');
                }
                for (int k = 0; k < 4; ++k) {
                    sb.append(sb2.charAt(3 - k));
                }
            }
            else if (char1 == '\\') {
                sb.append("//");
            }
            else if (char1 == '/') {
                sb.append('\\');
            }
            else if (char1 >= 'A' && char1 <= 'Z') {
                sb.append('/').append(char1);
            }
            else {
                sb.append(char1);
            }
        }
        return stringToByteArray(sb.toString());
    }
    
    private int rootNativeHandle() {
        return this.isUserNode() ? -2147483647 : -2147483646;
    }
    
    private static byte[] stringToByteArray(final String s) {
        final byte[] array = new byte[s.length() + 1];
        for (int i = 0; i < s.length(); ++i) {
            array[i] = (byte)s.charAt(i);
        }
        array[s.length()] = 0;
        return array;
    }
    
    private static String byteArrayToString(final byte[] array) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length - 1; ++i) {
            sb.append((char)array[i]);
        }
        return sb.toString();
    }
    
    @Override
    protected void flushSpi() throws BackingStoreException {
    }
    
    @Override
    protected void syncSpi() throws BackingStoreException {
    }
    
    private static synchronized PlatformLogger logger() {
        if (WindowsPreferences.logger == null) {
            WindowsPreferences.logger = PlatformLogger.getLogger("java.util.prefs");
        }
        return WindowsPreferences.logger;
    }
    
    static {
        WINDOWS_ROOT_PATH = stringToByteArray("Software\\JavaSoft\\Prefs");
        WindowsPreferences.INIT_SLEEP_TIME = 50;
        WindowsPreferences.MAX_ATTEMPTS = 5;
    }
}
