package org.postgresql.hostchooser;

public enum HostStatus
{
    ConnectFail, 
    ConnectOK, 
    Primary, 
    Secondary;
}
