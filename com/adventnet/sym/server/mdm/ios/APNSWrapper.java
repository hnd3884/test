package com.adventnet.sym.server.mdm.ios;

import javapns.notification.PushedNotification;
import java.util.List;
import javapns.notification.Payload;

public interface APNSWrapper
{
    void WakeUpIOSDevice(final String p0, final String p1, final boolean p2, final String p3, final String p4);
    
    void PushMDMPaylod(final Payload p0, final String p1, final String p2, final boolean p3, final String p4);
    
    Payload createMDMPayload(final String p0);
    
    void printPushedNotifications(final List<PushedNotification> p0);
    
    void printPushedNotifications(final String p0, final List<PushedNotification> p1);
}
