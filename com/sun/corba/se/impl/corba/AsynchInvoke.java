package com.sun.corba.se.impl.corba;

import com.sun.corba.se.spi.orb.ORB;

public class AsynchInvoke implements Runnable
{
    private RequestImpl _req;
    private ORB _orb;
    private boolean _notifyORB;
    
    public AsynchInvoke(final ORB orb, final RequestImpl req, final boolean notifyORB) {
        this._orb = orb;
        this._req = req;
        this._notifyORB = notifyORB;
    }
    
    @Override
    public void run() {
        this._req.doInvocation();
        synchronized (this._req) {
            this._req.gotResponse = true;
            this._req.notify();
        }
        if (this._notifyORB) {
            this._orb.notifyORB();
        }
    }
}
