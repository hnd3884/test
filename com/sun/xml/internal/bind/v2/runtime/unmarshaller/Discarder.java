package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

public final class Discarder extends Loader
{
    public static final Loader INSTANCE;
    
    private Discarder() {
        super(false);
    }
    
    @Override
    public void childElement(final UnmarshallingContext.State state, final TagName ea) {
        state.setTarget(null);
        state.setLoader(this);
    }
    
    static {
        INSTANCE = new Discarder();
    }
}
