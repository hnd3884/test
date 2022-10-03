package org.tanukisoftware.wrapper;

public class WrapperWin32Service
{
    public static final int SERVICE_STATE_STOPPED = 1;
    public static final int SERVICE_STATE_START_PENDING = 2;
    public static final int SERVICE_STATE_STOP_PENDING = 3;
    public static final int SERVICE_STATE_RUNNING = 4;
    public static final int SERVICE_STATE_CONTINUE_PENDING = 5;
    public static final int SERVICE_STATE_PAUSE_PENDING = 6;
    public static final int SERVICE_STATE_PAUSED = 7;
    private String m_name;
    private String m_displayName;
    private int m_serviceState;
    private int m_exitCode;
    
    WrapperWin32Service(final String name, final String displayName, final int serviceState, final int exitCode) {
        this.m_name = name;
        this.m_displayName = displayName;
        this.m_serviceState = serviceState;
        this.m_exitCode = exitCode;
    }
    
    public String getName() {
        return this.m_name;
    }
    
    public String getDisplayName() {
        return this.m_displayName;
    }
    
    public String getServiceStateName() {
        final int serviceState = this.getServiceState();
        switch (serviceState) {
            case 1: {
                return "STOPPED";
            }
            case 2: {
                return "START_PENDING";
            }
            case 3: {
                return "STOP_PENDING";
            }
            case 4: {
                return "RUNNING";
            }
            case 5: {
                return "CONTINUE_PENDING";
            }
            case 6: {
                return "PAUSE_PENDING";
            }
            case 7: {
                return "PAUSED";
            }
            default: {
                return "UNKNOWN(" + serviceState + ")";
            }
        }
    }
    
    public int getServiceState() {
        return this.m_serviceState;
    }
    
    public int getExitCode() {
        return this.m_exitCode;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("WrapperWin32Service[name=\"");
        sb.append(this.getName());
        sb.append("\", displayName=\"");
        sb.append(this.getDisplayName());
        sb.append("\", state=");
        sb.append(this.getServiceStateName());
        sb.append(", exitCode=");
        sb.append(this.getExitCode());
        sb.append("]");
        return sb.toString();
    }
}
