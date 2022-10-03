package com.theorem.radius3.dmcoaclient;

import com.theorem.radius3.AttributeList;

public final class DMCOAResponse
{
    protected AttributeList a;
    protected int b;
    
    public final void setPacketType(final int b) {
        this.b = b;
    }
    
    public final void setResponseAttributes(final AttributeList a) {
        this.a = a;
    }
    
    public final int getPacketType() {
        return this.b;
    }
    
    public final AttributeList getResponseAttributes() {
        return this.a;
    }
}
