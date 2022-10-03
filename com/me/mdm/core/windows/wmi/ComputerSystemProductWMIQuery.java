package com.me.mdm.core.windows.wmi;

import java.util.ArrayList;

public class ComputerSystemProductWMIQuery extends WMIQuery
{
    public ComputerSystemProductWMIQuery() {
        this.wmiNamespace = "./cimV2";
        this.wmiClassName = "Win32_ComputerSystemProduct";
        this.wmiCommandName = "ComputerSystemProduct";
        this.wmiClassProperties = new ArrayList<String>() {
            {
                this.add("UUID");
                this.add("IdentifyingNumber");
            }
        };
    }
}
