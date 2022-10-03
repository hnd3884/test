package org.postgresql;

public interface PGNotification
{
    String getName();
    
    int getPID();
    
    String getParameter();
}
