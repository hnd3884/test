package com.me.mdm.core.windows.wmi;

import java.util.ArrayList;

public class BiosWMIQuery extends WMIQuery
{
    public BiosWMIQuery() {
        this.wmiNamespace = "./cimV2";
        this.wmiClassName = "Win32_Bios";
        this.wmiCommandName = "Bios";
        this.wmiClassProperties = new ArrayList<String>() {
            {
                this.add("SerialNumber");
            }
        };
    }
}
