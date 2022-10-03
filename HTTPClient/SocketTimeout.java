package HTTPClient;

class SocketTimeout extends Thread
{
    private boolean alive;
    TimeoutEntry[] time_list;
    int current;
    
    SocketTimeout(final int secs) {
        super("SocketTimeout");
        this.alive = true;
        try {
            this.setDaemon(true);
        }
        catch (final SecurityException ex) {}
        this.setPriority(10);
        this.time_list = new TimeoutEntry[secs];
        for (int idx = 0; idx < secs; ++idx) {
            this.time_list[idx] = new TimeoutEntry(null);
            final TimeoutEntry timeoutEntry = this.time_list[idx];
            final TimeoutEntry timeoutEntry2 = this.time_list[idx];
            final TimeoutEntry timeoutEntry3 = this.time_list[idx];
            timeoutEntry2.prev = timeoutEntry3;
            timeoutEntry.next = timeoutEntry3;
        }
        this.current = 0;
    }
    
    public TimeoutEntry setTimeout(final StreamDemultiplexor demux) {
        final TimeoutEntry entry = new TimeoutEntry(demux);
        synchronized (this.time_list) {
            entry.next = this.time_list[this.current];
            entry.prev = this.time_list[this.current].prev;
            entry.prev.next = entry;
            entry.next.prev = entry;
            monitorexit(this.time_list);
        }
        return entry;
    }
    
    public void run() {
        TimeoutEntry marked = null;
    Label_0180_Outer:
        while (this.alive) {
            try {
                Thread.sleep(1000L);
            }
            catch (final InterruptedException ex) {}
            while (true) {
                Label_0193: {
                    synchronized (this.time_list) {
                        for (TimeoutEntry entry = this.time_list[this.current].next; entry != this.time_list[this.current]; entry = entry.next) {
                            entry.restart = false;
                        }
                        ++this.current;
                        if (this.current >= this.time_list.length) {
                            this.current = 0;
                        }
                        for (TimeoutEntry entry2 = this.time_list[this.current].next; entry2 != this.time_list[this.current]; entry2 = entry2.next) {
                            if (entry2.alive && !entry2.hyber) {
                                final TimeoutEntry prev = entry2.prev;
                                entry2.kill();
                                entry2.next = marked;
                                marked = entry2;
                                entry2 = prev;
                            }
                        }
                        monitorexit(this.time_list);
                        break Label_0193;
                    }
                    marked.demux.markForClose(null);
                    marked = marked.next;
                }
                if (marked == null) {
                    continue Label_0180_Outer;
                }
                continue;
            }
        }
    }
    
    public void kill() {
        this.alive = false;
    }
    
    class TimeoutEntry
    {
        boolean restart;
        boolean hyber;
        boolean alive;
        StreamDemultiplexor demux;
        TimeoutEntry next;
        TimeoutEntry prev;
        
        TimeoutEntry(final StreamDemultiplexor demux) {
            this.restart = false;
            this.hyber = false;
            this.alive = true;
            this.demux = demux;
        }
        
        void reset() {
            this.hyber = false;
            if (this.restart) {
                return;
            }
            this.restart = true;
            synchronized (SocketTimeout.this.time_list) {
                if (!this.alive) {
                    monitorexit(SocketTimeout.this.time_list);
                    return;
                }
                this.next.prev = this.prev;
                this.prev.next = this.next;
                this.next = SocketTimeout.this.time_list[SocketTimeout.this.current];
                this.prev = SocketTimeout.this.time_list[SocketTimeout.this.current].prev;
                this.prev.next = this;
                this.next.prev = this;
                monitorexit(SocketTimeout.this.time_list);
            }
        }
        
        void hyber() {
            if (this.alive) {
                this.hyber = true;
            }
        }
        
        void kill() {
            this.alive = false;
            this.restart = false;
            this.hyber = false;
            synchronized (SocketTimeout.this.time_list) {
                if (this.prev == null) {
                    monitorexit(SocketTimeout.this.time_list);
                    return;
                }
                this.next.prev = this.prev;
                this.prev.next = this.next;
                this.prev = null;
                monitorexit(SocketTimeout.this.time_list);
            }
        }
    }
}
