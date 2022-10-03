package sun.nio.fs;

import java.nio.file.Watchable;
import java.util.Objects;
import java.nio.file.StandardWatchEventKinds;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.nio.file.WatchEvent;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.WatchKey;

abstract class AbstractWatchKey implements WatchKey
{
    static final int MAX_EVENT_LIST_SIZE = 512;
    static final Event<Object> OVERFLOW_EVENT;
    private final AbstractWatchService watcher;
    private final Path dir;
    private State state;
    private List<WatchEvent<?>> events;
    private Map<Object, WatchEvent<?>> lastModifyEvents;
    
    protected AbstractWatchKey(final Path dir, final AbstractWatchService watcher) {
        this.watcher = watcher;
        this.dir = dir;
        this.state = State.READY;
        this.events = new ArrayList<WatchEvent<?>>();
        this.lastModifyEvents = new HashMap<Object, WatchEvent<?>>();
    }
    
    final AbstractWatchService watcher() {
        return this.watcher;
    }
    
    @Override
    public Path watchable() {
        return this.dir;
    }
    
    final void signal() {
        synchronized (this) {
            if (this.state == State.READY) {
                this.state = State.SIGNALLED;
                this.watcher.enqueueKey(this);
            }
        }
    }
    
    final void signalEvent(WatchEvent.Kind<?> overflow, Object o) {
        int n = (overflow == StandardWatchEventKinds.ENTRY_MODIFY) ? 1 : 0;
        synchronized (this) {
            final int size = this.events.size();
            if (size > 0) {
                final WatchEvent watchEvent = this.events.get(size - 1);
                if (watchEvent.kind() == StandardWatchEventKinds.OVERFLOW || (overflow == watchEvent.kind() && Objects.equals(o, watchEvent.context()))) {
                    ((Event)watchEvent).increment();
                    return;
                }
                if (!this.lastModifyEvents.isEmpty()) {
                    if (n != 0) {
                        final WatchEvent watchEvent2 = this.lastModifyEvents.get(o);
                        if (watchEvent2 != null) {
                            assert watchEvent2.kind() == StandardWatchEventKinds.ENTRY_MODIFY;
                            ((Event)watchEvent2).increment();
                            return;
                        }
                    }
                    else {
                        this.lastModifyEvents.remove(o);
                    }
                }
                if (size >= 512) {
                    overflow = StandardWatchEventKinds.OVERFLOW;
                    n = 0;
                    o = null;
                }
            }
            final Event event = new Event<Object>(overflow, o);
            if (n != 0) {
                this.lastModifyEvents.put(o, event);
            }
            else if (overflow == StandardWatchEventKinds.OVERFLOW) {
                this.events.clear();
                this.lastModifyEvents.clear();
            }
            this.events.add(event);
            this.signal();
        }
    }
    
    @Override
    public final List<WatchEvent<?>> pollEvents() {
        synchronized (this) {
            final List<WatchEvent<?>> events = this.events;
            this.events = new ArrayList<WatchEvent<?>>();
            this.lastModifyEvents.clear();
            return events;
        }
    }
    
    @Override
    public final boolean reset() {
        synchronized (this) {
            if (this.state == State.SIGNALLED && this.isValid()) {
                if (this.events.isEmpty()) {
                    this.state = State.READY;
                }
                else {
                    this.watcher.enqueueKey(this);
                }
            }
            return this.isValid();
        }
    }
    
    static {
        OVERFLOW_EVENT = new Event<Object>(StandardWatchEventKinds.OVERFLOW, null);
    }
    
    private enum State
    {
        READY, 
        SIGNALLED;
    }
    
    private static class Event<T> implements WatchEvent<T>
    {
        private final Kind<T> kind;
        private final T context;
        private int count;
        
        Event(final Kind<T> kind, final T context) {
            this.kind = kind;
            this.context = context;
            this.count = 1;
        }
        
        @Override
        public Kind<T> kind() {
            return this.kind;
        }
        
        @Override
        public T context() {
            return this.context;
        }
        
        @Override
        public int count() {
            return this.count;
        }
        
        void increment() {
            ++this.count;
        }
    }
}
