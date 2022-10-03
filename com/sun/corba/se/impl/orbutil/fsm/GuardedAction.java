package com.sun.corba.se.impl.orbutil.fsm;

import com.sun.corba.se.spi.orbutil.fsm.Input;
import com.sun.corba.se.spi.orbutil.fsm.FSM;
import com.sun.corba.se.spi.orbutil.fsm.GuardBase;
import com.sun.corba.se.spi.orbutil.fsm.State;
import com.sun.corba.se.spi.orbutil.fsm.Action;
import com.sun.corba.se.spi.orbutil.fsm.Guard;

public class GuardedAction
{
    private static Guard trueGuard;
    private Guard guard;
    private Action action;
    private State nextState;
    
    public GuardedAction(final Action action, final State nextState) {
        this.guard = GuardedAction.trueGuard;
        this.action = action;
        this.nextState = nextState;
    }
    
    public GuardedAction(final Guard guard, final Action action, final State nextState) {
        this.guard = guard;
        this.action = action;
        this.nextState = nextState;
    }
    
    @Override
    public String toString() {
        return "GuardedAction[action=" + this.action + " guard=" + this.guard + " nextState=" + this.nextState + "]";
    }
    
    public Action getAction() {
        return this.action;
    }
    
    public Guard getGuard() {
        return this.guard;
    }
    
    public State getNextState() {
        return this.nextState;
    }
    
    static {
        GuardedAction.trueGuard = new GuardBase("true") {
            @Override
            public Guard.Result evaluate(final FSM fsm, final Input input) {
                return Guard.Result.ENABLED;
            }
        };
    }
}
