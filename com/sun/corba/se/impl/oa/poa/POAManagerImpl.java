package com.sun.corba.se.impl.oa.poa;

import java.util.Iterator;
import java.util.Collection;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.CORBA.CompletionStatus;
import org.omg.PortableServer.POA;
import java.util.HashSet;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import java.util.Set;
import org.omg.PortableServer.POAManagerPackage.State;
import com.sun.corba.se.spi.protocol.PIHandler;
import org.omg.PortableServer.POAManager;
import org.omg.CORBA.LocalObject;

public class POAManagerImpl extends LocalObject implements POAManager
{
    private final POAFactory factory;
    private PIHandler pihandler;
    private State state;
    private Set poas;
    private int nInvocations;
    private int nWaiters;
    private int myId;
    private boolean debug;
    private boolean explicitStateChange;
    
    private String stateToString(final State state) {
        switch (state.value()) {
            case 0: {
                return "State[HOLDING]";
            }
            case 1: {
                return "State[ACTIVE]";
            }
            case 2: {
                return "State[DISCARDING]";
            }
            case 3: {
                return "State[INACTIVE]";
            }
            default: {
                return "State[UNKNOWN]";
            }
        }
    }
    
    @Override
    public String toString() {
        return "POAManagerImpl[myId=" + this.myId + " state=" + this.stateToString(this.state) + " nInvocations=" + this.nInvocations + " nWaiters=" + this.nWaiters + "]";
    }
    
    POAFactory getFactory() {
        return this.factory;
    }
    
    PIHandler getPIHandler() {
        return this.pihandler;
    }
    
    private void countedWait() {
        try {
            if (this.debug) {
                ORBUtility.dprint(this, "Calling countedWait on POAManager " + this + " nWaiters=" + this.nWaiters);
            }
            ++this.nWaiters;
            this.wait();
        }
        catch (final InterruptedException ex) {}
        finally {
            --this.nWaiters;
            if (this.debug) {
                ORBUtility.dprint(this, "Exiting countedWait on POAManager " + this + " nWaiters=" + this.nWaiters);
            }
        }
    }
    
    private void notifyWaiters() {
        if (this.debug) {
            ORBUtility.dprint(this, "Calling notifyWaiters on POAManager " + this + " nWaiters=" + this.nWaiters);
        }
        if (this.nWaiters > 0) {
            this.notifyAll();
        }
    }
    
    public int getManagerId() {
        return this.myId;
    }
    
    POAManagerImpl(final POAFactory factory, final PIHandler pihandler) {
        this.poas = new HashSet(4);
        this.nInvocations = 0;
        this.nWaiters = 0;
        this.myId = 0;
        (this.factory = factory).addPoaManager(this);
        this.pihandler = pihandler;
        this.myId = factory.newPOAManagerId();
        this.state = State.HOLDING;
        this.debug = factory.getORB().poaDebugFlag;
        this.explicitStateChange = false;
        if (this.debug) {
            ORBUtility.dprint(this, "Creating POAManagerImpl " + this);
        }
    }
    
    synchronized void addPOA(final POA poa) {
        if (this.state.value() == 3) {
            throw this.factory.getWrapper().addPoaInactive(CompletionStatus.COMPLETED_NO);
        }
        this.poas.add(poa);
    }
    
    synchronized void removePOA(final POA poa) {
        this.poas.remove(poa);
        if (this.poas.isEmpty()) {
            this.factory.removePoaManager(this);
        }
    }
    
    public short getORTState() {
        switch (this.state.value()) {
            case 0: {
                return 0;
            }
            case 1: {
                return 1;
            }
            case 3: {
                return 3;
            }
            case 2: {
                return 2;
            }
            default: {
                return 4;
            }
        }
    }
    
    @Override
    public synchronized void activate() throws AdapterInactive {
        this.explicitStateChange = true;
        if (this.debug) {
            ORBUtility.dprint(this, "Calling activate on POAManager " + this);
        }
        try {
            if (this.state.value() == 3) {
                throw new AdapterInactive();
            }
            this.state = State.ACTIVE;
            this.pihandler.adapterManagerStateChanged(this.myId, this.getORTState());
            this.notifyWaiters();
        }
        finally {
            if (this.debug) {
                ORBUtility.dprint(this, "Exiting activate on POAManager " + this);
            }
        }
    }
    
    @Override
    public synchronized void hold_requests(final boolean b) throws AdapterInactive {
        this.explicitStateChange = true;
        if (this.debug) {
            ORBUtility.dprint(this, "Calling hold_requests on POAManager " + this);
        }
        try {
            if (this.state.value() == 3) {
                throw new AdapterInactive();
            }
            this.state = State.HOLDING;
            this.pihandler.adapterManagerStateChanged(this.myId, this.getORTState());
            this.notifyWaiters();
            if (b) {
                while (this.state.value() == 0 && this.nInvocations > 0) {
                    this.countedWait();
                }
            }
        }
        finally {
            if (this.debug) {
                ORBUtility.dprint(this, "Exiting hold_requests on POAManager " + this);
            }
        }
    }
    
    @Override
    public synchronized void discard_requests(final boolean b) throws AdapterInactive {
        this.explicitStateChange = true;
        if (this.debug) {
            ORBUtility.dprint(this, "Calling hold_requests on POAManager " + this);
        }
        try {
            if (this.state.value() == 3) {
                throw new AdapterInactive();
            }
            this.state = State.DISCARDING;
            this.pihandler.adapterManagerStateChanged(this.myId, this.getORTState());
            this.notifyWaiters();
            if (b) {
                while (this.state.value() == 2 && this.nInvocations > 0) {
                    this.countedWait();
                }
            }
        }
        finally {
            if (this.debug) {
                ORBUtility.dprint(this, "Exiting hold_requests on POAManager " + this);
            }
        }
    }
    
    @Override
    public void deactivate(final boolean b, final boolean b2) throws AdapterInactive {
        this.explicitStateChange = true;
        try {
            synchronized (this) {
                if (this.debug) {
                    ORBUtility.dprint(this, "Calling deactivate on POAManager " + this);
                }
                if (this.state.value() == 3) {
                    throw new AdapterInactive();
                }
                this.state = State.INACTIVE;
                this.pihandler.adapterManagerStateChanged(this.myId, this.getORTState());
                this.notifyWaiters();
            }
            final POAManagerDeactivator poaManagerDeactivator = new POAManagerDeactivator(this, b, this.debug);
            if (b2) {
                poaManagerDeactivator.run();
            }
            else {
                new Thread(poaManagerDeactivator).start();
            }
        }
        finally {
            synchronized (this) {
                if (this.debug) {
                    ORBUtility.dprint(this, "Exiting deactivate on POAManager " + this);
                }
            }
        }
    }
    
    @Override
    public State get_state() {
        return this.state;
    }
    
    synchronized void checkIfActive() {
        try {
            if (this.debug) {
                ORBUtility.dprint(this, "Calling checkIfActive for POAManagerImpl " + this);
            }
            this.checkState();
        }
        finally {
            if (this.debug) {
                ORBUtility.dprint(this, "Exiting checkIfActive for POAManagerImpl " + this);
            }
        }
    }
    
    private void checkState() {
        while (this.state.value() != 1) {
            switch (this.state.value()) {
                case 0: {
                    while (this.state.value() == 0) {
                        this.countedWait();
                    }
                    continue;
                }
                case 2: {
                    throw this.factory.getWrapper().poaDiscarding();
                }
                case 3: {
                    throw this.factory.getWrapper().poaInactive();
                }
            }
        }
    }
    
    synchronized void enter() {
        try {
            if (this.debug) {
                ORBUtility.dprint(this, "Calling enter for POAManagerImpl " + this);
            }
            this.checkState();
            ++this.nInvocations;
        }
        finally {
            if (this.debug) {
                ORBUtility.dprint(this, "Exiting enter for POAManagerImpl " + this);
            }
        }
    }
    
    synchronized void exit() {
        try {
            if (this.debug) {
                ORBUtility.dprint(this, "Calling exit for POAManagerImpl " + this);
            }
            --this.nInvocations;
            if (this.nInvocations == 0) {
                this.notifyWaiters();
            }
        }
        finally {
            if (this.debug) {
                ORBUtility.dprint(this, "Exiting exit for POAManagerImpl " + this);
            }
        }
    }
    
    public synchronized void implicitActivation() {
        if (!this.explicitStateChange) {
            try {
                this.activate();
            }
            catch (final AdapterInactive adapterInactive) {}
        }
    }
    
    private class POAManagerDeactivator implements Runnable
    {
        private boolean etherealize_objects;
        private POAManagerImpl pmi;
        private boolean debug;
        
        POAManagerDeactivator(final POAManagerImpl pmi, final boolean etherealize_objects, final boolean debug) {
            this.etherealize_objects = etherealize_objects;
            this.pmi = pmi;
            this.debug = debug;
        }
        
        @Override
        public void run() {
            try {
                synchronized (this.pmi) {
                    if (this.debug) {
                        ORBUtility.dprint(this, "Calling run with etherealize_objects=" + this.etherealize_objects + " pmi=" + this.pmi);
                    }
                    while (this.pmi.nInvocations > 0) {
                        POAManagerImpl.this.countedWait();
                    }
                }
                if (this.etherealize_objects) {
                    Iterator iterator = null;
                    synchronized (this.pmi) {
                        if (this.debug) {
                            ORBUtility.dprint(this, "run: Preparing to etherealize with pmi=" + this.pmi);
                        }
                        iterator = new HashSet(this.pmi.poas).iterator();
                    }
                    while (iterator.hasNext()) {
                        ((POAImpl)iterator.next()).etherealizeAll();
                    }
                    synchronized (this.pmi) {
                        if (this.debug) {
                            ORBUtility.dprint(this, "run: removing POAManager and clearing poas with pmi=" + this.pmi);
                        }
                        POAManagerImpl.this.factory.removePoaManager(this.pmi);
                        POAManagerImpl.this.poas.clear();
                    }
                }
            }
            finally {
                if (this.debug) {
                    synchronized (this.pmi) {
                        ORBUtility.dprint(this, "Exiting run");
                    }
                }
            }
        }
    }
}
