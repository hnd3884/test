package com.adventnet.cli.config;

import com.adventnet.cli.serial.SerialCommOptionsImpl;
import com.adventnet.cli.transport.CLIProtocolOptions;
import com.adventnet.cli.CLIMessage;
import com.adventnet.util.script.ScriptHandler;
import com.adventnet.cli.CLISession;

public class ExecutionInterfaceImpl implements ExecutionInterface
{
    private CLISession cliSession;
    private ScriptHandler scriptHandler;
    private LoginLevel loginLevel;
    
    public ExecutionInterfaceImpl() {
        this.cliSession = null;
        this.scriptHandler = null;
        this.loginLevel = null;
    }
    
    public String executeCommand(final CLIMessage cliMessage) throws ExecutionException {
        try {
            if (cliMessage.getCLIPrompt() == null) {
                cliMessage.setCLIPrompt(this.loginLevel.getCommandPrompt());
            }
            return this.cliSession.syncSend(cliMessage).getData();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new ExecutionException(ex.getMessage() + ex);
        }
    }
    
    public void executeScript(final String s, final String[] array, final String s2) throws ExecutionException {
        try {
            if (this.scriptHandler == null) {
                this.scriptHandler = new ScriptHandler();
            }
            this.scriptHandler.executeScriptFromFile(s, array, s2);
        }
        catch (final Exception ex) {
            throw new ExecutionException(ex.getMessage() + ex);
        }
    }
    
    public void setLoginLevel(final LoginLevel loginLevel) throws ExecutionException {
        try {
            if (this.loginLevel == null) {
                this.loginLevel = loginLevel;
            }
            else if (this.loginLevel.getParentLevel() != null && this.loginLevel.getParentLevel().equals(loginLevel.getLoginLevel())) {
                final CLIMessage cliMessage = new CLIMessage(this.loginLevel.getLevelExitCmd());
                cliMessage.setCLIPrompt(loginLevel.getCommandPrompt());
                this.cliSession.syncSend(cliMessage);
                this.loginLevel = loginLevel;
            }
            else if (loginLevel.getParentLevel() != null && loginLevel.getParentLevel().equals(this.loginLevel.getLoginLevel())) {
                final CLIMessage cliMessage2 = new CLIMessage(loginLevel.getLoginCommand());
                if (loginLevel.getLoginPrompt() != null && loginLevel.getLoginName() != null) {
                    cliMessage2.setCLIPrompt(loginLevel.getLoginPrompt());
                    this.cliSession.syncSend(cliMessage2);
                    cliMessage2.setData(loginLevel.getLoginName());
                }
                if (loginLevel.getPasswordPrompt() != null && loginLevel.getLoginPassword() != null) {
                    cliMessage2.setCLIPrompt(loginLevel.getPasswordPrompt());
                    this.cliSession.syncSend(cliMessage2);
                    cliMessage2.setData(loginLevel.getLoginPassword());
                }
                cliMessage2.setCLIPrompt(loginLevel.getCommandPrompt());
                this.cliSession.syncSend(cliMessage2);
                this.loginLevel = loginLevel;
            }
        }
        catch (final Exception ex) {
            throw new ExecutionException(ex.getMessage() + ex);
        }
    }
    
    public void login(final CLIProtocolOptions cliProtocolOptions) throws ExecutionException {
        try {
            this.cliSession = new CLISession(cliProtocolOptions);
            if (cliProtocolOptions instanceof SerialCommOptionsImpl) {
                this.cliSession.setTransportProviderClassName("com.adventnet.cli.serial.SerialCommProviderImpl");
            }
            this.cliSession.open();
        }
        catch (final Exception ex) {
            throw new ExecutionException(ex.getMessage() + ex);
        }
    }
    
    public void close() throws ExecutionException {
        try {
            this.cliSession.close();
        }
        catch (final Exception ex) {
            throw new ExecutionException(ex.getMessage() + ex);
        }
    }
}
