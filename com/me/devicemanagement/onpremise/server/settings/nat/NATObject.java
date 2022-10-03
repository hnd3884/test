package com.me.devicemanagement.onpremise.server.settings.nat;

import java.util.HashMap;

public class NATObject
{
    public String statusOnSavingNATdetails;
    public String givenNATAddress;
    public boolean isRegenerateRequired;
    public boolean isSecondaryMismatch;
    public HashMap natPorts;
}
