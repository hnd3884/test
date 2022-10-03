package com.me.mdm.server.windows.profile.payload;

import java.util.Iterator;
import java.util.List;
import com.me.mdm.framework.syncml.core.data.Item;
import com.me.mdm.server.windows.profile.admx.ADMXBackedPolicy;

public class WindowsADMXBackedPolicyPayload extends WindowsPayload
{
    protected void addADMXBackedPolicy(final ADMXBackedPolicy admxBackedPolicy) {
        final Item admxItem = this.createCommandItemTagElement(admxBackedPolicy.getLocURI(), admxBackedPolicy.toString(), "chr");
        this.getReplacePayloadCommand().addRequestItem(admxItem);
    }
    
    protected void addADMXBackedPolicy(final List<ADMXBackedPolicy> admxBackedPolicies) {
        for (final ADMXBackedPolicy policy : admxBackedPolicies) {
            this.addADMXBackedPolicy(policy);
        }
    }
    
    protected void removeADMXBackedPolicy(final ADMXBackedPolicy admxBackedPolicy) {
        final Item admxItem = this.createTargetItemTagElement(admxBackedPolicy.getLocURI());
        this.getDeletePayloadCommand().addRequestItem(admxItem);
    }
    
    protected void removeADMXBackedPolicy(final List<ADMXBackedPolicy> admxBackedPolicies) {
        for (final ADMXBackedPolicy policy : admxBackedPolicies) {
            this.removeADMXBackedPolicy(policy);
        }
    }
}
