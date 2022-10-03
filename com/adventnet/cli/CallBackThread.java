package com.adventnet.cli;

import java.util.Enumeration;
import com.adventnet.cli.util.CLILogMgr;
import java.util.Vector;

class CallBackThread extends Thread
{
    CLISession cliSession;
    private Vector asyncMessages;
    private Vector clients;
    boolean closeFlag;
    
    CallBackThread(final CLISession cliSession) {
        this.cliSession = null;
        this.asyncMessages = null;
        this.clients = null;
        this.closeFlag = false;
        this.cliSession = cliSession;
        this.clients = cliSession.clients;
        this.asyncMessages = new Vector();
        this.start();
    }
    
    synchronized void wait_for_asyncMess() {
        try {
            CLILogMgr.setDebugMessage("CLIUSER", "CallBackThread: waiting for async mess", 4, null);
            if (this.closeFlag) {
                return;
            }
            this.wait();
        }
        catch (final Exception ex) {
            CLILogMgr.setDebugMessage("CLIERR", "CallBackThread: exception in wait thread ", 4, ex);
            ex.printStackTrace();
        }
    }
    
    synchronized boolean checkForMessages() {
        return this.asyncMessages.size() > 0;
    }
    
    synchronized void notifyAsyncMessage(final CLIMessage cliMessage) {
        try {
            if (cliMessage != null) {
                this.asyncMessages.addElement(cliMessage.getData());
            }
            this.notifyAll();
        }
        catch (final Exception ex) {
            CLILogMgr.setDebugMessage("CLIERR", "CallBackThread: exception in notification of async message ", 4, ex);
            ex.printStackTrace();
        }
    }
    
    synchronized void close() {
        this.cliSession = null;
        this.closeFlag = true;
        this.notifyAll();
    }
    
    public void run() {
        while (true) {
            if (this.checkForMessages()) {
                final String s = this.asyncMessages.remove(0);
                final CLIMessage cliMessage = new CLIMessage(s);
                CLILogMgr.setDebugMessage("CLIUSER", "CallBackThread: asynchronous message received " + s, 4, null);
                if (this.clients == null) {
                    continue;
                }
                final Enumeration elements = this.clients.elements();
                while (elements.hasMoreElements()) {
                    ((CLIClient)elements.nextElement()).callback(this.cliSession, cliMessage, 0);
                }
                if (this.clients.size() > 0) {
                    continue;
                }
                CLILogMgr.setDebugMessage("CLIUSER", "CallBackThread: No clients to notify async message", 4, null);
            }
            else {
                this.wait_for_asyncMess();
                if (this.closeFlag) {
                    break;
                }
                continue;
            }
        }
    }
}
