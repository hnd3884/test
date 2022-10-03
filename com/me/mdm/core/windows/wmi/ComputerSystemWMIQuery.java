package com.me.mdm.core.windows.wmi;

import java.util.ArrayList;

public class ComputerSystemWMIQuery extends WMIQuery
{
    public ComputerSystemWMIQuery() {
        this.wmiNamespace = "./cimV2";
        this.wmiClassName = "Win32_ComputerSystem";
        this.wmiCommandName = "ComputerSystem";
        this.wmiClassProperties = new ArrayList<String>() {
            {
                this.add("Domain");
                this.add("PartOfDomain");
                this.add("PCSystemType");
                this.add("PCSystemTypeEx");
                this.add("Workgroup");
                this.add("Model");
                this.add("Manufacturer");
            }
        };
    }
}
