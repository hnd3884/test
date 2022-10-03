package io.netty.handler.timeout;

import io.netty.util.internal.StringUtil;
import io.netty.util.internal.ObjectUtil;

public class IdleStateEvent
{
    public static final IdleStateEvent FIRST_READER_IDLE_STATE_EVENT;
    public static final IdleStateEvent READER_IDLE_STATE_EVENT;
    public static final IdleStateEvent FIRST_WRITER_IDLE_STATE_EVENT;
    public static final IdleStateEvent WRITER_IDLE_STATE_EVENT;
    public static final IdleStateEvent FIRST_ALL_IDLE_STATE_EVENT;
    public static final IdleStateEvent ALL_IDLE_STATE_EVENT;
    private final IdleState state;
    private final boolean first;
    
    protected IdleStateEvent(final IdleState state, final boolean first) {
        this.state = ObjectUtil.checkNotNull(state, "state");
        this.first = first;
    }
    
    public IdleState state() {
        return this.state;
    }
    
    public boolean isFirst() {
        return this.first;
    }
    
    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + '(' + this.state + (this.first ? ", first" : "") + ')';
    }
    
    static {
        FIRST_READER_IDLE_STATE_EVENT = new DefaultIdleStateEvent(IdleState.READER_IDLE, true);
        READER_IDLE_STATE_EVENT = new DefaultIdleStateEvent(IdleState.READER_IDLE, false);
        FIRST_WRITER_IDLE_STATE_EVENT = new DefaultIdleStateEvent(IdleState.WRITER_IDLE, true);
        WRITER_IDLE_STATE_EVENT = new DefaultIdleStateEvent(IdleState.WRITER_IDLE, false);
        FIRST_ALL_IDLE_STATE_EVENT = new DefaultIdleStateEvent(IdleState.ALL_IDLE, true);
        ALL_IDLE_STATE_EVENT = new DefaultIdleStateEvent(IdleState.ALL_IDLE, false);
    }
    
    private static final class DefaultIdleStateEvent extends IdleStateEvent
    {
        private final String representation;
        
        DefaultIdleStateEvent(final IdleState state, final boolean first) {
            super(state, first);
            this.representation = "IdleStateEvent(" + state + (first ? ", first" : "") + ')';
        }
        
        @Override
        public String toString() {
            return this.representation;
        }
    }
}
