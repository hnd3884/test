package javapns.devices;

import java.sql.Timestamp;

public interface Device
{
    String getDeviceId();
    
    void setDeviceId(final String p0);
    
    String getToken();
    
    void setToken(final String p0);
    
    Timestamp getLastRegister();
    
    void setLastRegister(final Timestamp p0);
}
