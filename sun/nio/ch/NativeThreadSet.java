package sun.nio.ch;

class NativeThreadSet
{
    private long[] elts;
    private int used;
    private boolean waitingToEmpty;
    
    NativeThreadSet(final int n) {
        this.used = 0;
        this.elts = new long[n];
    }
    
    int add() {
        long current = NativeThread.current();
        if (current == 0L) {
            current = -1L;
        }
        synchronized (this) {
            int n = 0;
            if (this.used >= this.elts.length) {
                final int length = this.elts.length;
                final long[] elts = new long[length * 2];
                System.arraycopy(this.elts, 0, elts, 0, length);
                this.elts = elts;
                n = length;
            }
            for (int i = n; i < this.elts.length; ++i) {
                if (this.elts[i] == 0L) {
                    this.elts[i] = current;
                    ++this.used;
                    return i;
                }
            }
            assert false;
            return -1;
        }
    }
    
    void remove(final int n) {
        synchronized (this) {
            this.elts[n] = 0L;
            --this.used;
            if (this.used == 0 && this.waitingToEmpty) {
                this.notifyAll();
            }
        }
    }
    
    synchronized void signalAndWait() {
        boolean b = false;
        while (this.used > 0) {
            int used = this.used;
            for (int length = this.elts.length, i = 0; i < length; ++i) {
                final long n = this.elts[i];
                if (n != 0L) {
                    if (n != -1L) {
                        NativeThread.signal(n);
                    }
                    if (--used == 0) {
                        break;
                    }
                }
            }
            this.waitingToEmpty = true;
            try {
                this.wait(50L);
            }
            catch (final InterruptedException ex) {
                b = true;
            }
            finally {
                this.waitingToEmpty = false;
            }
        }
        if (b) {
            Thread.currentThread().interrupt();
        }
    }
}
