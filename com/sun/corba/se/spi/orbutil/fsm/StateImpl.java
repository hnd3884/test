package com.sun.corba.se.spi.orbutil.fsm;

import java.util.HashSet;
import java.util.Set;
import com.sun.corba.se.impl.orbutil.fsm.GuardedAction;
import java.util.HashMap;
import java.util.Map;
import com.sun.corba.se.impl.orbutil.fsm.NameBase;

public class StateImpl extends NameBase implements State
{
    private Action defaultAction;
    private State defaultNextState;
    private Map inputToGuardedActions;
    
    public StateImpl(final String s) {
        super(s);
        this.defaultAction = null;
        this.inputToGuardedActions = new HashMap();
    }
    
    @Override
    public void preAction(final FSM fsm) {
    }
    
    @Override
    public void postAction(final FSM fsm) {
    }
    
    public State getDefaultNextState() {
        return this.defaultNextState;
    }
    
    public void setDefaultNextState(final State defaultNextState) {
        this.defaultNextState = defaultNextState;
    }
    
    public Action getDefaultAction() {
        return this.defaultAction;
    }
    
    public void setDefaultAction(final Action defaultAction) {
        this.defaultAction = defaultAction;
    }
    
    public void addGuardedAction(final Input input, final GuardedAction guardedAction) {
        Set set = this.inputToGuardedActions.get(input);
        if (set == null) {
            set = new HashSet();
            this.inputToGuardedActions.put(input, set);
        }
        set.add(guardedAction);
    }
    
    public Set getGuardedActions(final Input input) {
        return this.inputToGuardedActions.get(input);
    }
}
