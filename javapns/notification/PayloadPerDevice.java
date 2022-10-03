package javapns.notification;

import javapns.devices.exceptions.InvalidDeviceTokenFormatException;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.devices.Device;

public class PayloadPerDevice
{
    private final Payload payload;
    private final Device device;
    
    public PayloadPerDevice(final Payload payload, final String token) throws InvalidDeviceTokenFormatException {
        this.payload = payload;
        this.device = new BasicDevice(token);
    }
    
    public PayloadPerDevice(final Payload payload, final Device device) {
        this.payload = payload;
        this.device = device;
    }
    
    public Payload getPayload() {
        return this.payload;
    }
    
    public Device getDevice() {
        return this.device;
    }
}
