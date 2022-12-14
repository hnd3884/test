package sun.security.smartcardio;

final class PCSC extends PlatformPCSC
{
    static final int SCARD_S_SUCCESS = 0;
    static final int SCARD_E_CANCELLED = -2146435070;
    static final int SCARD_E_CANT_DISPOSE = -2146435058;
    static final int SCARD_E_INSUFFICIENT_BUFFER = -2146435064;
    static final int SCARD_E_INVALID_ATR = -2146435051;
    static final int SCARD_E_INVALID_HANDLE = -2146435069;
    static final int SCARD_E_INVALID_PARAMETER = -2146435068;
    static final int SCARD_E_INVALID_TARGET = -2146435067;
    static final int SCARD_E_INVALID_VALUE = -2146435055;
    static final int SCARD_E_NO_MEMORY = -2146435066;
    static final int SCARD_F_COMM_ERROR = -2146435053;
    static final int SCARD_F_INTERNAL_ERROR = -2146435071;
    static final int SCARD_F_UNKNOWN_ERROR = -2146435052;
    static final int SCARD_F_WAITED_TOO_LONG = -2146435065;
    static final int SCARD_E_UNKNOWN_READER = -2146435063;
    static final int SCARD_E_TIMEOUT = -2146435062;
    static final int SCARD_E_SHARING_VIOLATION = -2146435061;
    static final int SCARD_E_NO_SMARTCARD = -2146435060;
    static final int SCARD_E_UNKNOWN_CARD = -2146435059;
    static final int SCARD_E_PROTO_MISMATCH = -2146435057;
    static final int SCARD_E_NOT_READY = -2146435056;
    static final int SCARD_E_SYSTEM_CANCELLED = -2146435054;
    static final int SCARD_E_NOT_TRANSACTED = -2146435050;
    static final int SCARD_E_READER_UNAVAILABLE = -2146435049;
    static final int SCARD_W_UNSUPPORTED_CARD = -2146434971;
    static final int SCARD_W_UNRESPONSIVE_CARD = -2146434970;
    static final int SCARD_W_UNPOWERED_CARD = -2146434969;
    static final int SCARD_W_RESET_CARD = -2146434968;
    static final int SCARD_W_REMOVED_CARD = -2146434967;
    static final int SCARD_W_INSERTED_CARD = -2146434966;
    static final int SCARD_E_UNSUPPORTED_FEATURE = -2146435041;
    static final int SCARD_E_PCI_TOO_SMALL = -2146435047;
    static final int SCARD_E_READER_UNSUPPORTED = -2146435046;
    static final int SCARD_E_DUPLICATE_READER = -2146435045;
    static final int SCARD_E_CARD_UNSUPPORTED = -2146435044;
    static final int SCARD_E_NO_SERVICE = -2146435043;
    static final int SCARD_E_SERVICE_STOPPED = -2146435042;
    static final int SCARD_E_NO_READERS_AVAILABLE = -2146435026;
    static final int WINDOWS_ERROR_INVALID_HANDLE = 6;
    static final int WINDOWS_ERROR_INVALID_PARAMETER = 87;
    static final int SCARD_SCOPE_USER = 0;
    static final int SCARD_SCOPE_TERMINAL = 1;
    static final int SCARD_SCOPE_SYSTEM = 2;
    static final int SCARD_SCOPE_GLOBAL = 3;
    static final int SCARD_SHARE_EXCLUSIVE = 1;
    static final int SCARD_SHARE_SHARED = 2;
    static final int SCARD_SHARE_DIRECT = 3;
    static final int SCARD_LEAVE_CARD = 0;
    static final int SCARD_RESET_CARD = 1;
    static final int SCARD_UNPOWER_CARD = 2;
    static final int SCARD_EJECT_CARD = 3;
    static final int SCARD_STATE_UNAWARE = 0;
    static final int SCARD_STATE_IGNORE = 1;
    static final int SCARD_STATE_CHANGED = 2;
    static final int SCARD_STATE_UNKNOWN = 4;
    static final int SCARD_STATE_UNAVAILABLE = 8;
    static final int SCARD_STATE_EMPTY = 16;
    static final int SCARD_STATE_PRESENT = 32;
    static final int SCARD_STATE_ATRMATCH = 64;
    static final int SCARD_STATE_EXCLUSIVE = 128;
    static final int SCARD_STATE_INUSE = 256;
    static final int SCARD_STATE_MUTE = 512;
    static final int SCARD_STATE_UNPOWERED = 1024;
    static final int TIMEOUT_INFINITE = -1;
    private static final char[] hexDigits;
    
    private PCSC() {
    }
    
    static void checkAvailable() throws RuntimeException {
        if (PCSC.initException != null) {
            throw new UnsupportedOperationException("PC/SC not available on this platform", PCSC.initException);
        }
    }
    
    static native long SCardEstablishContext(final int p0) throws PCSCException;
    
    static native String[] SCardListReaders(final long p0) throws PCSCException;
    
    static native long SCardConnect(final long p0, final String p1, final int p2, final int p3) throws PCSCException;
    
    static native byte[] SCardTransmit(final long p0, final int p1, final byte[] p2, final int p3, final int p4) throws PCSCException;
    
    static native byte[] SCardStatus(final long p0, final byte[] p1) throws PCSCException;
    
    static native void SCardDisconnect(final long p0, final int p1) throws PCSCException;
    
    static native int[] SCardGetStatusChange(final long p0, final long p1, final int[] p2, final String[] p3) throws PCSCException;
    
    static native void SCardBeginTransaction(final long p0) throws PCSCException;
    
    static native void SCardEndTransaction(final long p0, final int p1) throws PCSCException;
    
    static native byte[] SCardControl(final long p0, final int p1, final byte[] p2) throws PCSCException;
    
    public static String toString(final byte[] array) {
        final StringBuffer sb = new StringBuffer(array.length * 3);
        for (int i = 0; i < array.length; ++i) {
            final int n = array[i] & 0xFF;
            if (i != 0) {
                sb.append(':');
            }
            sb.append(PCSC.hexDigits[n >>> 4]);
            sb.append(PCSC.hexDigits[n & 0xF]);
        }
        return sb.toString();
    }
    
    static {
        hexDigits = "0123456789abcdef".toCharArray();
    }
}
