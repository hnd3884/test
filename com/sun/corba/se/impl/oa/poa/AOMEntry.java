package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.spi.orbutil.fsm.StateEngineFactory;
import com.sun.corba.se.spi.orbutil.fsm.ActionBase;
import com.sun.corba.se.spi.orbutil.fsm.InputImpl;
import com.sun.corba.se.spi.orbutil.fsm.FSM;
import com.sun.corba.se.spi.orbutil.fsm.StateImpl;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import com.sun.corba.se.spi.orbutil.fsm.StateEngine;
import com.sun.corba.se.spi.orbutil.fsm.GuardBase;
import com.sun.corba.se.spi.orbutil.fsm.Guard;
import com.sun.corba.se.spi.orbutil.fsm.Action;
import com.sun.corba.se.spi.orbutil.fsm.Input;
import com.sun.corba.se.spi.orbutil.fsm.State;
import com.sun.corba.se.impl.orbutil.concurrent.CondVar;
import com.sun.corba.se.spi.orbutil.fsm.FSMImpl;

public class AOMEntry extends FSMImpl
{
    private final Thread[] etherealizer;
    private final int[] counter;
    private final CondVar wait;
    final POAImpl poa;
    public static final State INVALID;
    public static final State INCARN;
    public static final State VALID;
    public static final State ETHP;
    public static final State ETH;
    public static final State DESTROYED;
    static final Input START_ETH;
    static final Input ETH_DONE;
    static final Input INC_DONE;
    static final Input INC_FAIL;
    static final Input ACTIVATE;
    static final Input ENTER;
    static final Input EXIT;
    private static Action incrementAction;
    private static Action decrementAction;
    private static Action throwIllegalStateExceptionAction;
    private static Action oaaAction;
    private static Guard waitGuard;
    private static GuardBase greaterZeroGuard;
    private static Guard zeroGuard;
    private static GuardBase greaterOneGuard;
    private static Guard oneGuard;
    private static StateEngine engine;
    
    public AOMEntry(final POAImpl poa) {
        super(AOMEntry.engine, AOMEntry.INVALID, poa.getORB().poaFSMDebugFlag);
        this.poa = poa;
        (this.etherealizer = new Thread[1])[0] = null;
        (this.counter = new int[1])[0] = 0;
        this.wait = new CondVar(poa.poaMutex, poa.getORB().poaConcurrencyDebugFlag);
    }
    
    public void startEtherealize(final Thread thread) {
        this.etherealizer[0] = thread;
        this.doIt(AOMEntry.START_ETH);
    }
    
    public void etherealizeComplete() {
        this.doIt(AOMEntry.ETH_DONE);
    }
    
    public void incarnateComplete() {
        this.doIt(AOMEntry.INC_DONE);
    }
    
    public void incarnateFailure() {
        this.doIt(AOMEntry.INC_FAIL);
    }
    
    public void activateObject() throws ObjectAlreadyActive {
        try {
            this.doIt(AOMEntry.ACTIVATE);
        }
        catch (final RuntimeException ex) {
            final Throwable cause = ex.getCause();
            if (cause instanceof ObjectAlreadyActive) {
                throw (ObjectAlreadyActive)cause;
            }
            throw ex;
        }
    }
    
    public void enter() {
        this.doIt(AOMEntry.ENTER);
    }
    
    public void exit() {
        this.doIt(AOMEntry.EXIT);
    }
    
    static {
        INVALID = new StateImpl("Invalid");
        INCARN = new StateImpl("Incarnating") {
            @Override
            public void postAction(final FSM fsm) {
                ((AOMEntry)fsm).wait.broadcast();
            }
        };
        VALID = new StateImpl("Valid");
        ETHP = new StateImpl("EtherealizePending");
        ETH = new StateImpl("Etherealizing") {
            @Override
            public void preAction(final FSM fsm) {
                final Thread thread = ((AOMEntry)fsm).etherealizer[0];
                if (thread != null) {
                    thread.start();
                }
            }
            
            @Override
            public void postAction(final FSM fsm) {
                ((AOMEntry)fsm).wait.broadcast();
            }
        };
        DESTROYED = new StateImpl("Destroyed");
        START_ETH = new InputImpl("startEtherealize");
        ETH_DONE = new InputImpl("etherealizeDone");
        INC_DONE = new InputImpl("incarnateDone");
        INC_FAIL = new InputImpl("incarnateFailure");
        ACTIVATE = new InputImpl("activateObject");
        ENTER = new InputImpl("enter");
        EXIT = new InputImpl("exit");
        AOMEntry.incrementAction = new ActionBase("increment") {
            @Override
            public void doIt(final FSM fsm, final Input input) {
                final int[] access$200 = ((AOMEntry)fsm).counter;
                final int n = 0;
                ++access$200[n];
            }
        };
        AOMEntry.decrementAction = new ActionBase("decrement") {
            @Override
            public void doIt(final FSM fsm, final Input input) {
                final AOMEntry aomEntry = (AOMEntry)fsm;
                if (aomEntry.counter[0] > 0) {
                    final int[] access$200 = aomEntry.counter;
                    final int n = 0;
                    --access$200[n];
                    return;
                }
                throw aomEntry.poa.lifecycleWrapper().aomEntryDecZero();
            }
        };
        AOMEntry.throwIllegalStateExceptionAction = new ActionBase("throwIllegalStateException") {
            @Override
            public void doIt(final FSM fsm, final Input input) {
                throw new IllegalStateException("No transitions allowed from the DESTROYED state");
            }
        };
        AOMEntry.oaaAction = new ActionBase("throwObjectAlreadyActive") {
            @Override
            public void doIt(final FSM fsm, final Input input) {
                throw new RuntimeException(new ObjectAlreadyActive());
            }
        };
        AOMEntry.waitGuard = new GuardBase("wait") {
            @Override
            public Guard.Result evaluate(final FSM fsm, final Input input) {
                final AOMEntry aomEntry = (AOMEntry)fsm;
                try {
                    aomEntry.wait.await();
                }
                catch (final InterruptedException ex) {}
                return Guard.Result.DEFERED;
            }
        };
        AOMEntry.greaterZeroGuard = new CounterGuard(0);
        AOMEntry.zeroGuard = new Guard.Complement(AOMEntry.greaterZeroGuard);
        AOMEntry.greaterOneGuard = new CounterGuard(1);
        AOMEntry.oneGuard = new Guard.Complement(AOMEntry.greaterOneGuard);
        (AOMEntry.engine = StateEngineFactory.create()).add(AOMEntry.INVALID, AOMEntry.ENTER, AOMEntry.incrementAction, AOMEntry.INCARN);
        AOMEntry.engine.add(AOMEntry.INVALID, AOMEntry.ACTIVATE, null, AOMEntry.VALID);
        AOMEntry.engine.setDefault(AOMEntry.INVALID);
        AOMEntry.engine.add(AOMEntry.INCARN, AOMEntry.ENTER, AOMEntry.waitGuard, null, AOMEntry.INCARN);
        AOMEntry.engine.add(AOMEntry.INCARN, AOMEntry.EXIT, null, AOMEntry.INCARN);
        AOMEntry.engine.add(AOMEntry.INCARN, AOMEntry.START_ETH, AOMEntry.waitGuard, null, AOMEntry.INCARN);
        AOMEntry.engine.add(AOMEntry.INCARN, AOMEntry.INC_DONE, null, AOMEntry.VALID);
        AOMEntry.engine.add(AOMEntry.INCARN, AOMEntry.INC_FAIL, AOMEntry.decrementAction, AOMEntry.INVALID);
        AOMEntry.engine.add(AOMEntry.INCARN, AOMEntry.ACTIVATE, AOMEntry.oaaAction, AOMEntry.INCARN);
        AOMEntry.engine.add(AOMEntry.VALID, AOMEntry.ENTER, AOMEntry.incrementAction, AOMEntry.VALID);
        AOMEntry.engine.add(AOMEntry.VALID, AOMEntry.EXIT, AOMEntry.decrementAction, AOMEntry.VALID);
        AOMEntry.engine.add(AOMEntry.VALID, AOMEntry.START_ETH, AOMEntry.greaterZeroGuard, null, AOMEntry.ETHP);
        AOMEntry.engine.add(AOMEntry.VALID, AOMEntry.START_ETH, AOMEntry.zeroGuard, null, AOMEntry.ETH);
        AOMEntry.engine.add(AOMEntry.VALID, AOMEntry.ACTIVATE, AOMEntry.oaaAction, AOMEntry.VALID);
        AOMEntry.engine.add(AOMEntry.ETHP, AOMEntry.ENTER, AOMEntry.waitGuard, null, AOMEntry.ETHP);
        AOMEntry.engine.add(AOMEntry.ETHP, AOMEntry.START_ETH, null, AOMEntry.ETHP);
        AOMEntry.engine.add(AOMEntry.ETHP, AOMEntry.EXIT, AOMEntry.greaterOneGuard, AOMEntry.decrementAction, AOMEntry.ETHP);
        AOMEntry.engine.add(AOMEntry.ETHP, AOMEntry.EXIT, AOMEntry.oneGuard, AOMEntry.decrementAction, AOMEntry.ETH);
        AOMEntry.engine.add(AOMEntry.ETHP, AOMEntry.ACTIVATE, AOMEntry.oaaAction, AOMEntry.ETHP);
        AOMEntry.engine.add(AOMEntry.ETH, AOMEntry.START_ETH, null, AOMEntry.ETH);
        AOMEntry.engine.add(AOMEntry.ETH, AOMEntry.ETH_DONE, null, AOMEntry.DESTROYED);
        AOMEntry.engine.add(AOMEntry.ETH, AOMEntry.ACTIVATE, AOMEntry.oaaAction, AOMEntry.ETH);
        AOMEntry.engine.add(AOMEntry.ETH, AOMEntry.ENTER, AOMEntry.waitGuard, null, AOMEntry.ETH);
        AOMEntry.engine.setDefault(AOMEntry.DESTROYED, AOMEntry.throwIllegalStateExceptionAction, AOMEntry.DESTROYED);
        AOMEntry.engine.done();
    }
    
    private static class CounterGuard extends GuardBase
    {
        private int value;
        
        public CounterGuard(final int value) {
            super("counter>" + value);
            this.value = value;
        }
        
        @Override
        public Guard.Result evaluate(final FSM fsm, final Input input) {
            return Guard.Result.convert(((AOMEntry)fsm).counter[0] > this.value);
        }
    }
}
