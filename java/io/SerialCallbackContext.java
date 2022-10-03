package java.io;

final class SerialCallbackContext
{
    private final Object obj;
    private final ObjectStreamClass desc;
    private Thread thread;
    
    public SerialCallbackContext(final Object obj, final ObjectStreamClass desc) {
        this.obj = obj;
        this.desc = desc;
        this.thread = Thread.currentThread();
    }
    
    public Object getObj() throws NotActiveException {
        this.checkAndSetUsed();
        return this.obj;
    }
    
    public ObjectStreamClass getDesc() {
        return this.desc;
    }
    
    public void check() throws NotActiveException {
        if (this.thread != null && this.thread != Thread.currentThread()) {
            throw new NotActiveException("expected thread: " + this.thread + ", but got: " + Thread.currentThread());
        }
    }
    
    private void checkAndSetUsed() throws NotActiveException {
        if (this.thread != Thread.currentThread()) {
            throw new NotActiveException("not in readObject invocation or fields already read");
        }
        this.thread = null;
    }
    
    public void setUsed() {
        this.thread = null;
    }
}
