package com.adventnet.cli;

import java.util.Enumeration;
import com.adventnet.cli.util.CLILogMgr;

class CLIAsynchSendThread extends Thread
{
    CLISession cliSession;
    boolean closeFlag;
    
    CLIAsynchSendThread(final CLISession cliSession) {
        this.cliSession = null;
        this.closeFlag = false;
        this.cliSession = cliSession;
        this.start();
    }
    
    @Override
    public void run() {
        while (!this.closeFlag) {
            this.cliSession.wait_for_message();
            final CLIMessage deQMessage = this.cliSession.deQMessage();
            if (deQMessage == null) {
                if (this.closeFlag) {
                    break;
                }
                continue;
            }
            else {
                CLILogMgr.setDebugMessage("CLIUSER", "CLIAsynchSendThread: deQueuing message " + deQMessage.getData(), 4, null);
                CLIMessage syncSend;
                try {
                    syncSend = this.cliSession.syncSend(deQMessage);
                }
                catch (final Exception ex) {
                    syncSend = null;
                    CLILogMgr.setDebugMessage("CLIERR", "CLIAsynchSendThread: No response received", 4, ex);
                }
                if (this.closeFlag) {
                    break;
                }
                if (syncSend != null) {
                    CLILogMgr.setDebugMessage("CLIUSER", "CLIAsynchSendThread: received message " + syncSend.getData(), 4, null);
                    syncSend.setMsgID(deQMessage.getMsgID());
                }
                if (this.cliSession.clients == null) {
                    continue;
                }
                final Enumeration elements = this.cliSession.clients.elements();
                while (elements.hasMoreElements() && !((CLIClient)elements.nextElement()).callback(this.cliSession, syncSend, deQMessage.getMsgID())) {}
                if (this.cliSession.getCLIClientsSize() > 0) {
                    continue;
                }
                CLILogMgr.setDebugMessage("CLIUSER", "CLIAsynchSendThread: No clients for the session", 4, null);
            }
        }
        this.cliSession = null;
    }
}
