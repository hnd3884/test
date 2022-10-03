package org.tanukisoftware.wrapper.event;

import org.tanukisoftware.wrapper.WrapperManager;

public abstract class WrapperServiceActionEvent extends WrapperServiceEvent
{
    private static final long serialVersionUID = 7901768955067874864L;
    public static final int SOURCE_CODE_FILTER = 1;
    public static final int SOURCE_CODE_COMMANDFILE = 2;
    public static final int SOURCE_CODE_WINDOWS_SERVICE_MANAGER = 3;
    public static final int SOURCE_CODE_ON_EXIT = 4;
    public static final int SOURCE_CODE_SIGNAL = 5;
    public static final int SOURCE_CODE_DEADLOCK = 10;
    public static final int SOURCE_CODE_TIMER = 21;
    public static final int SOURCE_CODE_COMMAND_BLOCK_TIMEOUT = 22;
    private int m_actionSourceCode;
    
    public static String getSourceCodeName(final int actionSourceCode) {
        switch (actionSourceCode) {
            case 1: {
                return WrapperManager.getRes().getString("Filter Action");
            }
            case 2: {
                return WrapperManager.getRes().getString("Command File Action");
            }
            case 3: {
                return WrapperManager.getRes().getString("Windows Service Manager");
            }
            case 4: {
                return WrapperManager.getRes().getString("On Exit Action");
            }
            case 5: {
                return WrapperManager.getRes().getString("Signal Action");
            }
            case 10: {
                return WrapperManager.getRes().getString("Deadlock Action");
            }
            case 21: {
                return WrapperManager.getRes().getString("Timer Action");
            }
            case 22: {
                return WrapperManager.getRes().getString("Block Timeout Action");
            }
            default: {
                return WrapperManager.getRes().getString("Unknown Code {0}", new Integer(actionSourceCode));
            }
        }
    }
    
    public WrapperServiceActionEvent(final int actionSourceCode) {
        this.m_actionSourceCode = actionSourceCode;
    }
    
    public int getSourceCode() {
        return this.m_actionSourceCode;
    }
    
    public String getSourceCodeName() {
        return getSourceCodeName(this.m_actionSourceCode);
    }
    
    public String toString() {
        return "WrapperServiceActionEvent[actionSourceCode=" + this.getSourceCodeName() + "]";
    }
}
