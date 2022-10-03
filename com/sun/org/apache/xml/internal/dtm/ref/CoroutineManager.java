package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.res.XMLMessages;
import java.util.BitSet;

public class CoroutineManager
{
    BitSet m_activeIDs;
    static final int m_unreasonableId = 1024;
    Object m_yield;
    static final int NOBODY = -1;
    static final int ANYBODY = -1;
    int m_nextCoroutine;
    
    public CoroutineManager() {
        this.m_activeIDs = new BitSet();
        this.m_yield = null;
        this.m_nextCoroutine = -1;
    }
    
    public synchronized int co_joinCoroutineSet(int coroutineID) {
        if (coroutineID >= 0) {
            if (coroutineID >= 1024 || this.m_activeIDs.get(coroutineID)) {
                return -1;
            }
        }
        else {
            for (coroutineID = 0; coroutineID < 1024 && this.m_activeIDs.get(coroutineID); ++coroutineID) {}
            if (coroutineID >= 1024) {
                return -1;
            }
        }
        this.m_activeIDs.set(coroutineID);
        return coroutineID;
    }
    
    public synchronized Object co_entry_pause(final int thisCoroutine) throws NoSuchMethodException {
        if (!this.m_activeIDs.get(thisCoroutine)) {
            throw new NoSuchMethodException();
        }
        while (this.m_nextCoroutine != thisCoroutine) {
            try {
                this.wait();
            }
            catch (final InterruptedException ex) {}
        }
        return this.m_yield;
    }
    
    public synchronized Object co_resume(final Object arg_object, final int thisCoroutine, final int toCoroutine) throws NoSuchMethodException {
        if (!this.m_activeIDs.get(toCoroutine)) {
            throw new NoSuchMethodException(XMLMessages.createXMLMessage("ER_COROUTINE_NOT_AVAIL", new Object[] { Integer.toString(toCoroutine) }));
        }
        this.m_yield = arg_object;
        this.m_nextCoroutine = toCoroutine;
        this.notify();
        while (true) {
            if (this.m_nextCoroutine == thisCoroutine && this.m_nextCoroutine != -1) {
                if (this.m_nextCoroutine != -1) {
                    break;
                }
            }
            try {
                this.wait();
            }
            catch (final InterruptedException ex) {}
        }
        if (this.m_nextCoroutine == -1) {
            this.co_exit(thisCoroutine);
            throw new NoSuchMethodException(XMLMessages.createXMLMessage("ER_COROUTINE_CO_EXIT", null));
        }
        return this.m_yield;
    }
    
    public synchronized void co_exit(final int thisCoroutine) {
        this.m_activeIDs.clear(thisCoroutine);
        this.m_nextCoroutine = -1;
        this.notify();
    }
    
    public synchronized void co_exit_to(final Object arg_object, final int thisCoroutine, final int toCoroutine) throws NoSuchMethodException {
        if (!this.m_activeIDs.get(toCoroutine)) {
            throw new NoSuchMethodException(XMLMessages.createXMLMessage("ER_COROUTINE_NOT_AVAIL", new Object[] { Integer.toString(toCoroutine) }));
        }
        this.m_yield = arg_object;
        this.m_nextCoroutine = toCoroutine;
        this.m_activeIDs.clear(thisCoroutine);
        this.notify();
    }
}
