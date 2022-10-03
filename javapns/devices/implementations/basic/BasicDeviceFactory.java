package javapns.devices.implementations.basic;

import javapns.devices.exceptions.UnknownDeviceException;
import javapns.devices.exceptions.DuplicateDeviceException;
import java.sql.Timestamp;
import java.util.Calendar;
import javapns.devices.exceptions.NullDeviceTokenException;
import javapns.devices.exceptions.NullIdException;
import javapns.devices.Device;
import java.util.HashMap;
import java.util.Map;
import javapns.devices.DeviceFactory;

@Deprecated
public class BasicDeviceFactory implements DeviceFactory
{
    private static final Object synclock;
    private final Map<String, BasicDevice> devices;
    
    public BasicDeviceFactory() {
        this.devices = new HashMap<String, BasicDevice>();
    }
    
    @Override
    public Device addDevice(final String id, String token) throws Exception {
        if (id == null || id.trim().equals("")) {
            throw new NullIdException();
        }
        if (token == null || token.trim().equals("")) {
            throw new NullDeviceTokenException();
        }
        if (!this.devices.containsKey(id)) {
            token = token.trim().replace(" ", "");
            final BasicDevice device = new BasicDevice(id, token, new Timestamp(Calendar.getInstance().getTime().getTime()));
            this.devices.put(id, device);
            return device;
        }
        throw new DuplicateDeviceException();
    }
    
    @Override
    public Device getDevice(final String id) throws UnknownDeviceException, NullIdException {
        if (id == null || id.trim().equals("")) {
            throw new NullIdException();
        }
        if (this.devices.containsKey(id)) {
            return this.devices.get(id);
        }
        throw new UnknownDeviceException();
    }
    
    @Override
    public void removeDevice(final String id) throws UnknownDeviceException, NullIdException {
        if (id == null || id.trim().equals("")) {
            throw new NullIdException();
        }
        if (this.devices.containsKey(id)) {
            this.devices.remove(id);
            return;
        }
        throw new UnknownDeviceException();
    }
    
    static {
        synclock = new Object();
    }
}
