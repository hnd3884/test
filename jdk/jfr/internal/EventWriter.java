package jdk.jfr.internal;

import sun.misc.Unsafe;

public final class EventWriter
{
    private static final Unsafe unsafe;
    private static final JVM jvm;
    private long startPosition;
    private long startPositionAddress;
    private long currentPosition;
    private long maxPosition;
    private final long threadID;
    private PlatformEventType eventType;
    private int maxEventSize;
    private boolean started;
    private boolean valid;
    private boolean flushOnEnd;
    boolean notified;
    
    public static EventWriter getEventWriter() {
        final EventWriter eventWriter = (EventWriter)JVM.getEventWriter();
        return (eventWriter != null) ? eventWriter : JVM.newEventWriter();
    }
    
    public void putBoolean(final boolean b) {
        if (this.isValidForSize(1)) {
            this.currentPosition += Bits.putBoolean(this.currentPosition, b);
        }
    }
    
    public void putByte(final byte b) {
        if (this.isValidForSize(1)) {
            EventWriter.unsafe.putByte(this.currentPosition, b);
            ++this.currentPosition;
        }
    }
    
    public void putChar(final char c) {
        if (this.isValidForSize(3)) {
            this.putUncheckedLong(c);
        }
    }
    
    private void putUncheckedChar(final char c) {
        this.putUncheckedLong(c);
    }
    
    public void putShort(final short n) {
        if (this.isValidForSize(3)) {
            this.putUncheckedLong(n & 0xFFFF);
        }
    }
    
    public void putInt(final int n) {
        if (this.isValidForSize(5)) {
            this.putUncheckedLong((long)n & 0xFFFFFFFFL);
        }
    }
    
    private void putUncheckedInt(final int n) {
        this.putUncheckedLong((long)n & 0xFFFFFFFFL);
    }
    
    public void putFloat(final float n) {
        if (this.isValidForSize(4)) {
            this.currentPosition += Bits.putFloat(this.currentPosition, n);
        }
    }
    
    public void putLong(final long n) {
        if (this.isValidForSize(9)) {
            this.putUncheckedLong(n);
        }
    }
    
    public void putDouble(final double n) {
        if (this.isValidForSize(8)) {
            this.currentPosition += Bits.putDouble(this.currentPosition, n);
        }
    }
    
    public void putString(final String s, final StringPool stringPool) {
        if (s == null) {
            this.putByte((byte)0);
            return;
        }
        final int length = s.length();
        if (length == 0) {
            this.putByte((byte)1);
            return;
        }
        if (length > 16 && length < 128) {
            final long addString = StringPool.addString(s);
            if (addString > 0L) {
                this.putByte((byte)2);
                this.putLong(addString);
                return;
            }
        }
        this.putStringValue(s);
    }
    
    private void putStringValue(final String s) {
        final int length = s.length();
        if (this.isValidForSize(6 + 3 * length)) {
            this.putUncheckedByte((byte)4);
            this.putUncheckedInt(length);
            for (int i = 0; i < length; ++i) {
                this.putUncheckedChar(s.charAt(i));
            }
        }
    }
    
    public void putEventThread() {
        this.putLong(this.threadID);
    }
    
    public void putThread(final Thread thread) {
        if (thread == null) {
            this.putLong(0L);
        }
        else {
            this.putLong(EventWriter.jvm.getThreadId(thread));
        }
    }
    
    public void putClass(final Class<?> clazz) {
        if (clazz == null) {
            this.putLong(0L);
        }
        else {
            this.putLong(JVM.getClassIdNonIntrinsic(clazz));
        }
    }
    
    public void putStackTrace() {
        if (this.eventType.getStackTraceEnabled()) {
            this.putLong(EventWriter.jvm.getStackTraceId(this.eventType.getStackTraceOffset()));
        }
        else {
            this.putLong(0L);
        }
    }
    
    private void reserveEventSizeField() {
        if (this.isValidForSize(4)) {
            this.currentPosition += 4L;
        }
    }
    
    private void reset() {
        this.currentPosition = this.startPosition;
        if (this.flushOnEnd) {
            this.flushOnEnd = this.flush();
        }
        this.valid = true;
        this.started = false;
    }
    
    private boolean isValidForSize(final int n) {
        if (!this.valid) {
            return false;
        }
        if (this.currentPosition + n > this.maxPosition) {
            this.flushOnEnd = this.flush(this.usedSize(), n);
            if (this.currentPosition + n > this.maxPosition) {
                Logger.log(LogTag.JFR_SYSTEM, LogLevel.WARN, () -> "Unable to commit. Requested size " + n2 + " too large");
                return this.valid = false;
            }
        }
        return true;
    }
    
    private boolean isNotified() {
        return this.notified;
    }
    
    private void resetNotified() {
        this.notified = false;
    }
    
    private int usedSize() {
        return (int)(this.currentPosition - this.startPosition);
    }
    
    private boolean flush() {
        return this.flush(this.usedSize(), 0);
    }
    
    private boolean flush(final int n, final int n2) {
        return JVM.flush(this, n, n2);
    }
    
    public boolean beginEvent(final PlatformEventType eventType) {
        if (this.started) {
            return false;
        }
        this.started = true;
        this.eventType = eventType;
        this.reserveEventSizeField();
        this.putLong(eventType.getId());
        return true;
    }
    
    public boolean endEvent() {
        if (!this.valid) {
            this.reset();
            return true;
        }
        final int usedSize = this.usedSize();
        if (usedSize > this.maxEventSize) {
            this.reset();
            return true;
        }
        Bits.putInt(this.startPosition, makePaddedInt(usedSize));
        if (this.isNotified()) {
            this.resetNotified();
            this.reset();
            return false;
        }
        this.startPosition = this.currentPosition;
        EventWriter.unsafe.putAddress(this.startPositionAddress, this.startPosition);
        if (this.flushOnEnd) {
            this.flushOnEnd = this.flush();
        }
        this.started = false;
        return true;
    }
    
    private EventWriter(final long n, final long maxPosition, final long startPositionAddress, final long threadID, final boolean valid) {
        this.currentPosition = n;
        this.startPosition = n;
        this.maxPosition = maxPosition;
        this.startPositionAddress = startPositionAddress;
        this.threadID = threadID;
        this.started = false;
        this.flushOnEnd = false;
        this.valid = valid;
        this.notified = false;
        this.maxEventSize = 268435455;
    }
    
    private static int makePaddedInt(final int n) {
        return (int)((((n >>> 0 & 0x7F) | 0x80) << 24) + (long)(((n >>> 7 & 0x7F) | 0x80) << 16) + (((n >>> 14 & 0x7F) | 0x80) << 8) + ((n >>> 21 & 0x7F) << 0));
    }
    
    private void putUncheckedLong(long n) {
        if ((n & 0xFFFFFFFFFFFFFF80L) == 0x0L) {
            this.putUncheckedByte((byte)n);
            return;
        }
        this.putUncheckedByte((byte)(n | 0x80L));
        n >>>= 7;
        if ((n & 0xFFFFFFFFFFFFFF80L) == 0x0L) {
            this.putUncheckedByte((byte)n);
            return;
        }
        this.putUncheckedByte((byte)(n | 0x80L));
        n >>>= 7;
        if ((n & 0xFFFFFFFFFFFFFF80L) == 0x0L) {
            this.putUncheckedByte((byte)n);
            return;
        }
        this.putUncheckedByte((byte)(n | 0x80L));
        n >>>= 7;
        if ((n & 0xFFFFFFFFFFFFFF80L) == 0x0L) {
            this.putUncheckedByte((byte)n);
            return;
        }
        this.putUncheckedByte((byte)(n | 0x80L));
        n >>>= 7;
        if ((n & 0xFFFFFFFFFFFFFF80L) == 0x0L) {
            this.putUncheckedByte((byte)n);
            return;
        }
        this.putUncheckedByte((byte)(n | 0x80L));
        n >>>= 7;
        if ((n & 0xFFFFFFFFFFFFFF80L) == 0x0L) {
            this.putUncheckedByte((byte)n);
            return;
        }
        this.putUncheckedByte((byte)(n | 0x80L));
        n >>>= 7;
        if ((n & 0xFFFFFFFFFFFFFF80L) == 0x0L) {
            this.putUncheckedByte((byte)n);
            return;
        }
        this.putUncheckedByte((byte)(n | 0x80L));
        n >>>= 7;
        if ((n & 0xFFFFFFFFFFFFFF80L) == 0x0L) {
            this.putUncheckedByte((byte)n);
            return;
        }
        this.putUncheckedByte((byte)(n | 0x80L));
        this.putUncheckedByte((byte)(n >>> 7));
    }
    
    private void putUncheckedByte(final byte b) {
        EventWriter.unsafe.putByte(this.currentPosition, b);
        ++this.currentPosition;
    }
    
    static {
        unsafe = Unsafe.getUnsafe();
        jvm = JVM.getJVM();
    }
}
