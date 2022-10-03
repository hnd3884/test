package javapns.devices;

import javapns.devices.exceptions.NullIdException;
import javapns.devices.exceptions.UnknownDeviceException;

@Deprecated
public interface DeviceFactory
{
    Device addDevice(final String p0, final String p1) throws Exception;
    
    Device getDevice(final String p0) throws UnknownDeviceException, NullIdException;
    
    void removeDevice(final String p0) throws UnknownDeviceException, NullIdException;
}
