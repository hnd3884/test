package javapns.devices.implementations.basic;

import javapns.devices.exceptions.InvalidDeviceTokenFormatException;
import java.sql.Timestamp;
import javapns.devices.Device;

public class BasicDevice implements Device
{
    private String deviceId;
    private String token;
    private Timestamp lastRegister;
    
    public BasicDevice() {
    }
    
    public BasicDevice(final String token) throws InvalidDeviceTokenFormatException {
        this(token, true);
    }
    
    private BasicDevice(final String token, final boolean validate) throws InvalidDeviceTokenFormatException {
        this.deviceId = token;
        this.token = token;
        try {
            this.lastRegister = new Timestamp(System.currentTimeMillis());
        }
        catch (final Exception ex) {}
        if (validate) {
            validateTokenFormat(token);
        }
    }
    
    BasicDevice(final String id, final String token, final Timestamp register) throws InvalidDeviceTokenFormatException {
        this.deviceId = id;
        this.token = token;
        this.lastRegister = register;
        validateTokenFormat(token);
    }
    
    public static void validateTokenFormat(final String token) throws InvalidDeviceTokenFormatException {
        if (token == null) {
            throw new InvalidDeviceTokenFormatException("Device Token is null, and not the required 64 bytes...");
        }
        if (token.getBytes().length != 64) {
            throw new InvalidDeviceTokenFormatException("Device Token has a length of [" + token.getBytes().length + "] and not the required 64 bytes!");
        }
    }
    
    public void validateTokenFormat() throws InvalidDeviceTokenFormatException {
        validateTokenFormat(this.token);
    }
    
    @Override
    public String getDeviceId() {
        return this.deviceId;
    }
    
    @Override
    public void setDeviceId(final String id) {
        this.deviceId = id;
    }
    
    @Override
    public String getToken() {
        return this.token;
    }
    
    @Override
    public void setToken(final String token) {
        this.token = token;
    }
    
    @Override
    public Timestamp getLastRegister() {
        return this.lastRegister;
    }
    
    @Override
    public void setLastRegister(final Timestamp lastRegister) {
        this.lastRegister = lastRegister;
    }
}
