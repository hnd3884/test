package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class PrintingDisabled extends GenericJson
{
    @Key
    private PolicyOptions policyOptions;
    @Key
    private Boolean printingDisabled;
    
    public PolicyOptions getPolicyOptions() {
        return this.policyOptions;
    }
    
    public PrintingDisabled setPolicyOptions(final PolicyOptions policyOptions) {
        this.policyOptions = policyOptions;
        return this;
    }
    
    public Boolean getPrintingDisabled() {
        return this.printingDisabled;
    }
    
    public PrintingDisabled setPrintingDisabled(final Boolean printingDisabled) {
        this.printingDisabled = printingDisabled;
        return this;
    }
    
    public PrintingDisabled set(final String s, final Object o) {
        return (PrintingDisabled)super.set(s, o);
    }
    
    public PrintingDisabled clone() {
        return (PrintingDisabled)super.clone();
    }
}
