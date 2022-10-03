package java.awt;

abstract class MediaEntry
{
    MediaTracker tracker;
    int ID;
    MediaEntry next;
    int status;
    boolean cancelled;
    static final int LOADING = 1;
    static final int ABORTED = 2;
    static final int ERRORED = 4;
    static final int COMPLETE = 8;
    static final int LOADSTARTED = 13;
    static final int DONE = 14;
    
    MediaEntry(final MediaTracker tracker, final int id) {
        this.tracker = tracker;
        this.ID = id;
    }
    
    abstract Object getMedia();
    
    static MediaEntry insert(MediaEntry mediaEntry, final MediaEntry next) {
        MediaEntry next2 = mediaEntry;
        MediaEntry mediaEntry2 = null;
        while (next2 != null && next2.ID <= next.ID) {
            mediaEntry2 = next2;
            next2 = next2.next;
        }
        next.next = next2;
        if (mediaEntry2 == null) {
            mediaEntry = next;
        }
        else {
            mediaEntry2.next = next;
        }
        return mediaEntry;
    }
    
    int getID() {
        return this.ID;
    }
    
    abstract void startLoad();
    
    void cancel() {
        this.cancelled = true;
    }
    
    synchronized int getStatus(final boolean b, final boolean b2) {
        if (b && (this.status & 0xD) == 0x0) {
            this.status = ((this.status & 0xFFFFFFFD) | 0x1);
            this.startLoad();
        }
        return this.status;
    }
    
    void setStatus(final int status) {
        synchronized (this) {
            this.status = status;
        }
        this.tracker.setDone();
    }
}
