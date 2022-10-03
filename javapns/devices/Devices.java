package javapns.devices;

import javapns.notification.PayloadPerDevice;
import java.util.Iterator;
import java.util.Arrays;
import javapns.devices.implementations.basic.BasicDevice;
import java.util.Vector;
import java.util.List;

public class Devices
{
    public static List<Device> asDevices(final Object rawList) {
        final List<Device> list = new Vector<Device>();
        if (rawList == null) {
            return list;
        }
        if (rawList instanceof List) {
            final List devices = (List)rawList;
            if (devices.size() == 0) {
                return list;
            }
            final Object firstDevice = devices.get(0);
            if (firstDevice instanceof Device) {
                return devices;
            }
            if (firstDevice instanceof String) {
                for (final Object token : devices) {
                    final BasicDevice device = new BasicDevice();
                    device.setToken((String)token);
                    list.add(device);
                }
            }
        }
        else if (rawList instanceof String[]) {
            final String[] array;
            final String[] tokens = array = (String[])rawList;
            for (final String token2 : array) {
                final BasicDevice device2 = new BasicDevice();
                device2.setToken(token2);
                list.add(device2);
            }
        }
        else {
            if (rawList instanceof Device[]) {
                final Device[] dvs = (Device[])rawList;
                return Arrays.asList(dvs);
            }
            if (rawList instanceof String) {
                final BasicDevice device3 = new BasicDevice();
                device3.setToken((String)rawList);
                list.add(device3);
            }
            else {
                if (!(rawList instanceof Device)) {
                    throw new IllegalArgumentException("Device list type not supported. Supported types are: String[], List<String>, Device[], List<Device>, String and Device");
                }
                list.add((Device)rawList);
            }
        }
        return list;
    }
    
    public static List<PayloadPerDevice> asPayloadsPerDevices(final Object rawList) {
        final List<PayloadPerDevice> list = new Vector<PayloadPerDevice>();
        if (rawList == null) {
            return list;
        }
        if (rawList instanceof List) {
            final List devices = (List)rawList;
            if (devices.size() == 0) {
                return list;
            }
            return devices;
        }
        else {
            if (rawList instanceof PayloadPerDevice[]) {
                final PayloadPerDevice[] dvs = (PayloadPerDevice[])rawList;
                return Arrays.asList(dvs);
            }
            if (rawList instanceof PayloadPerDevice) {
                list.add((PayloadPerDevice)rawList);
                return list;
            }
            throw new IllegalArgumentException("PayloadPerDevice list type not supported. Supported types are: PayloadPerDevice[], List<PayloadPerDevice> and PayloadPerDevice");
        }
    }
}
