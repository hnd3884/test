package com.sun.corba.se.impl.orbutil.fsm;

import com.sun.corba.se.spi.orbutil.fsm.FSMImpl;
import java.util.Iterator;
import java.util.Set;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.orbutil.fsm.StateImpl;
import com.sun.corba.se.spi.orbutil.fsm.Guard;
import com.sun.corba.se.spi.orbutil.fsm.State;
import org.omg.CORBA.INTERNAL;
import com.sun.corba.se.spi.orbutil.fsm.Input;
import com.sun.corba.se.spi.orbutil.fsm.FSM;
import com.sun.corba.se.spi.orbutil.fsm.ActionBase;
import com.sun.corba.se.spi.orbutil.fsm.Action;
import com.sun.corba.se.spi.orbutil.fsm.StateEngine;

public class StateEngineImpl implements StateEngine
{
    private static Action emptyAction;
    private boolean initializing;
    private Action defaultAction;
    
    public StateEngineImpl() {
        this.initializing = true;
        this.defaultAction = new ActionBase("Invalid Transition") {
            @Override
            public void doIt(final FSM fsm, final Input input) {
                throw new INTERNAL("Invalid transition attempted from " + fsm.getState() + " under " + input);
            }
        };
    }
    
    @Override
    public StateEngine add(final State state, final Input input, final Guard guard, final Action action, final State state2) throws IllegalArgumentException, IllegalStateException {
        this.mustBeInitializing();
        ((StateImpl)state).addGuardedAction(input, new GuardedAction(guard, action, state2));
        return this;
    }
    
    @Override
    public StateEngine add(final State state, final Input input, final Action action, final State state2) throws IllegalArgumentException, IllegalStateException {
        this.mustBeInitializing();
        ((StateImpl)state).addGuardedAction(input, new GuardedAction(action, state2));
        return this;
    }
    
    @Override
    public StateEngine setDefault(final State state, final Action defaultAction, final State defaultNextState) throws IllegalArgumentException, IllegalStateException {
        this.mustBeInitializing();
        final StateImpl stateImpl = (StateImpl)state;
        stateImpl.setDefaultAction(defaultAction);
        stateImpl.setDefaultNextState(defaultNextState);
        return this;
    }
    
    @Override
    public StateEngine setDefault(final State state, final State state2) throws IllegalArgumentException, IllegalStateException {
        return this.setDefault(state, StateEngineImpl.emptyAction, state2);
    }
    
    @Override
    public StateEngine setDefault(final State state) throws IllegalArgumentException, IllegalStateException {
        return this.setDefault(state, state);
    }
    
    @Override
    public void done() throws IllegalStateException {
        this.mustBeInitializing();
        this.initializing = false;
    }
    
    @Override
    public void setDefaultAction(final Action defaultAction) throws IllegalStateException {
        this.mustBeInitializing();
        this.defaultAction = defaultAction;
    }
    
    public void doIt(final FSM fsm, final Input input, final boolean b) {
        if (b) {
            ORBUtility.dprint(this, "doIt enter: currentState = " + fsm.getState() + " in = " + input);
        }
        try {
            this.innerDoIt(fsm, input, b);
        }
        finally {
            if (b) {
                ORBUtility.dprint(this, "doIt exit");
            }
        }
    }
    
    private StateImpl getDefaultNextState(final StateImpl stateImpl) {
        StateImpl stateImpl2 = (StateImpl)stateImpl.getDefaultNextState();
        if (stateImpl2 == null) {
            stateImpl2 = stateImpl;
        }
        return stateImpl2;
    }
    
    private Action getDefaultAction(final StateImpl stateImpl) {
        Action action = stateImpl.getDefaultAction();
        if (action == null) {
            action = this.defaultAction;
        }
        return action;
    }
    
    private void innerDoIt(final FSM fsm, final Input input, final boolean b) {
        if (b) {
            ORBUtility.dprint(this, "Calling innerDoIt with input " + input);
        }
        boolean b2;
        StateImpl defaultNextState;
        Action action;
        do {
            b2 = false;
            final StateImpl stateImpl = (StateImpl)fsm.getState();
            defaultNextState = this.getDefaultNextState(stateImpl);
            action = this.getDefaultAction(stateImpl);
            if (b) {
                ORBUtility.dprint(this, "currentState      = " + stateImpl);
                ORBUtility.dprint(this, "in                = " + input);
                ORBUtility.dprint(this, "default nextState = " + defaultNextState);
                ORBUtility.dprint(this, "default action    = " + action);
            }
            final Set guardedActions = stateImpl.getGuardedActions(input);
            if (guardedActions != null) {
                for (final GuardedAction guardedAction : guardedActions) {
                    final Guard.Result evaluate = guardedAction.getGuard().evaluate(fsm, input);
                    if (b) {
                        ORBUtility.dprint(this, "doIt: evaluated " + guardedAction + " with result " + evaluate);
                    }
                    if (evaluate == Guard.Result.ENABLED) {
                        defaultNextState = (StateImpl)guardedAction.getNextState();
                        action = guardedAction.getAction();
                        if (b) {
                            ORBUtility.dprint(this, "nextState = " + defaultNextState);
                            ORBUtility.dprint(this, "action    = " + action);
                            break;
                        }
                        break;
                    }
                    else {
                        if (evaluate == Guard.Result.DEFERED) {
                            b2 = true;
                            break;
                        }
                        continue;
                    }
                }
            }
        } while (b2);
        this.performStateTransition(fsm, input, defaultNextState, action, b);
    }
    
    private void performStateTransition(final FSM fsm, final Input input, final StateImpl stateImpl, final Action action, final boolean b) {
        final StateImpl stateImpl2 = (StateImpl)fsm.getState();
        final boolean b2 = !stateImpl2.equals(stateImpl);
        if (b2) {
            if (b) {
                ORBUtility.dprint(this, "doIt: executing postAction for state " + stateImpl2);
            }
            try {
                stateImpl2.postAction(fsm);
            }
            catch (final Throwable t) {
                if (b) {
                    ORBUtility.dprint(this, "doIt: postAction threw " + t);
                }
                if (t instanceof ThreadDeath) {
                    throw (ThreadDeath)t;
                }
            }
        }
        try {
            if (action != null) {
                action.doIt(fsm, input);
            }
        }
        finally {
            if (b2) {
                if (b) {
                    ORBUtility.dprint(this, "doIt: executing preAction for state " + stateImpl);
                }
                try {
                    stateImpl.preAction(fsm);
                }
                catch (final Throwable t2) {
                    if (b) {
                        ORBUtility.dprint(this, "doIt: preAction threw " + t2);
                    }
                    if (t2 instanceof ThreadDeath) {
                        throw (ThreadDeath)t2;
                    }
                }
                ((FSMImpl)fsm).internalSetState(stateImpl);
            }
            if (b) {
                ORBUtility.dprint(this, "doIt: state is now " + stateImpl);
            }
        }
    }
    
    @Override
    public FSM makeFSM(final State state) throws IllegalStateException {
        this.mustNotBeInitializing();
        return new FSMImpl(this, state);
    }
    
    private void mustBeInitializing() throws IllegalStateException {
        if (!this.initializing) {
            throw new IllegalStateException("Invalid method call after initialization completed");
        }
    }
    
    private void mustNotBeInitializing() throws IllegalStateException {
        if (this.initializing) {
            throw new IllegalStateException("Invalid method call before initialization completed");
        }
    }
    
    static {
        StateEngineImpl.emptyAction = new ActionBase("Empty") {
            @Override
            public void doIt(final FSM fsm, final Input input) {
            }
        };
    }
}
