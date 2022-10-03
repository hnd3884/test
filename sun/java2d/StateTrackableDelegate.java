package sun.java2d;

public final class StateTrackableDelegate implements StateTrackable
{
    public static final StateTrackableDelegate UNTRACKABLE_DELEGATE;
    public static final StateTrackableDelegate IMMUTABLE_DELEGATE;
    private State theState;
    StateTracker theTracker;
    private int numDynamicAgents;
    
    public static StateTrackableDelegate createInstance(final State state) {
        switch (state) {
            case UNTRACKABLE: {
                return StateTrackableDelegate.UNTRACKABLE_DELEGATE;
            }
            case STABLE: {
                return new StateTrackableDelegate(State.STABLE);
            }
            case DYNAMIC: {
                return new StateTrackableDelegate(State.DYNAMIC);
            }
            case IMMUTABLE: {
                return StateTrackableDelegate.IMMUTABLE_DELEGATE;
            }
            default: {
                throw new InternalError("unknown state");
            }
        }
    }
    
    private StateTrackableDelegate(final State theState) {
        this.theState = theState;
    }
    
    @Override
    public State getState() {
        return this.theState;
    }
    
    @Override
    public synchronized StateTracker getStateTracker() {
        StateTracker theTracker = this.theTracker;
        if (theTracker == null) {
            switch (this.theState) {
                case IMMUTABLE: {
                    theTracker = StateTracker.ALWAYS_CURRENT;
                    break;
                }
                case STABLE: {
                    theTracker = new StateTracker() {
                        @Override
                        public boolean isCurrent() {
                            return StateTrackableDelegate.this.theTracker == this;
                        }
                    };
                    break;
                }
                case UNTRACKABLE:
                case DYNAMIC: {
                    theTracker = StateTracker.NEVER_CURRENT;
                    break;
                }
            }
            this.theTracker = theTracker;
        }
        return theTracker;
    }
    
    public synchronized void setImmutable() {
        if (this.theState == State.UNTRACKABLE || this.theState == State.DYNAMIC) {
            throw new IllegalStateException("UNTRACKABLE or DYNAMIC objects cannot become IMMUTABLE");
        }
        this.theState = State.IMMUTABLE;
        this.theTracker = null;
    }
    
    public synchronized void setUntrackable() {
        if (this.theState == State.IMMUTABLE) {
            throw new IllegalStateException("IMMUTABLE objects cannot become UNTRACKABLE");
        }
        this.theState = State.UNTRACKABLE;
        this.theTracker = null;
    }
    
    public synchronized void addDynamicAgent() {
        if (this.theState == State.IMMUTABLE) {
            throw new IllegalStateException("Cannot change state from IMMUTABLE");
        }
        ++this.numDynamicAgents;
        if (this.theState == State.STABLE) {
            this.theState = State.DYNAMIC;
            this.theTracker = null;
        }
    }
    
    protected synchronized void removeDynamicAgent() {
        final int numDynamicAgents = this.numDynamicAgents - 1;
        this.numDynamicAgents = numDynamicAgents;
        if (numDynamicAgents == 0 && this.theState == State.DYNAMIC) {
            this.theState = State.STABLE;
            this.theTracker = null;
        }
    }
    
    public final void markDirty() {
        this.theTracker = null;
    }
    
    static {
        UNTRACKABLE_DELEGATE = new StateTrackableDelegate(State.UNTRACKABLE);
        IMMUTABLE_DELEGATE = new StateTrackableDelegate(State.IMMUTABLE);
    }
}
