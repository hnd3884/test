package io.grpc;

import java.util.logging.Level;
import java.util.logging.Logger;

final class ThreadLocalContextStorage extends Context.Storage
{
    private static final Logger log;
    static final ThreadLocal<Context> localContext;
    
    @Override
    public Context doAttach(final Context toAttach) {
        final Context current = this.current();
        ThreadLocalContextStorage.localContext.set(toAttach);
        return current;
    }
    
    @Override
    public void detach(final Context toDetach, final Context toRestore) {
        if (this.current() != toDetach) {
            ThreadLocalContextStorage.log.log(Level.SEVERE, "Context was not attached when detaching", new Throwable().fillInStackTrace());
        }
        if (toRestore != Context.ROOT) {
            ThreadLocalContextStorage.localContext.set(toRestore);
        }
        else {
            ThreadLocalContextStorage.localContext.set(null);
        }
    }
    
    @Override
    public Context current() {
        final Context current = ThreadLocalContextStorage.localContext.get();
        if (current == null) {
            return Context.ROOT;
        }
        return current;
    }
    
    static {
        log = Logger.getLogger(ThreadLocalContextStorage.class.getName());
        localContext = new ThreadLocal<Context>();
    }
}
