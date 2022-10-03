package java.rmi.server;

import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import java.security.SecureRandom;
import java.io.Serializable;

public final class UID implements Serializable
{
    private static int hostUnique;
    private static boolean hostUniqueSet;
    private static final Object lock;
    private static long lastTime;
    private static short lastCount;
    private static final long serialVersionUID = 1086053664494604050L;
    private final int unique;
    private final long time;
    private final short count;
    
    public UID() {
        synchronized (UID.lock) {
            if (!UID.hostUniqueSet) {
                UID.hostUnique = new SecureRandom().nextInt();
                UID.hostUniqueSet = true;
            }
            this.unique = UID.hostUnique;
            if (UID.lastCount == 32767) {
                boolean interrupted = Thread.interrupted();
                int i = 0;
                while (i == 0) {
                    final long currentTimeMillis = System.currentTimeMillis();
                    if (currentTimeMillis == UID.lastTime) {
                        try {
                            Thread.sleep(1L);
                        }
                        catch (final InterruptedException ex) {
                            interrupted = true;
                        }
                    }
                    else {
                        UID.lastTime = ((currentTimeMillis < UID.lastTime) ? (UID.lastTime + 1L) : currentTimeMillis);
                        UID.lastCount = -32768;
                        i = 1;
                    }
                }
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
            }
            this.time = UID.lastTime;
            final short lastCount = UID.lastCount;
            UID.lastCount = (short)(lastCount + 1);
            this.count = lastCount;
        }
    }
    
    public UID(final short count) {
        this.unique = 0;
        this.time = 0L;
        this.count = count;
    }
    
    private UID(final int unique, final long time, final short count) {
        this.unique = unique;
        this.time = time;
        this.count = count;
    }
    
    @Override
    public int hashCode() {
        return (int)this.time + this.count;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof UID) {
            final UID uid = (UID)o;
            return this.unique == uid.unique && this.count == uid.count && this.time == uid.time;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return Integer.toString(this.unique, 16) + ":" + Long.toString(this.time, 16) + ":" + Integer.toString(this.count, 16);
    }
    
    public void write(final DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.unique);
        dataOutput.writeLong(this.time);
        dataOutput.writeShort(this.count);
    }
    
    public static UID read(final DataInput dataInput) throws IOException {
        return new UID(dataInput.readInt(), dataInput.readLong(), dataInput.readShort());
    }
    
    static {
        UID.hostUniqueSet = false;
        lock = new Object();
        UID.lastTime = System.currentTimeMillis();
        UID.lastCount = -32768;
    }
}
