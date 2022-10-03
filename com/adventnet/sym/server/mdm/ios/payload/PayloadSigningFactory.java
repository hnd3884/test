package com.adventnet.sym.server.mdm.ios.payload;

public class PayloadSigningFactory
{
    private static PayloadSigning payloadSigningObj;
    private static Boolean iOSProfileSigningStatus;
    
    public static PayloadSigning getInstance() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (PayloadSigningFactory.payloadSigningObj == null) {
            PayloadSigningFactory.payloadSigningObj = (PayloadSigning)Class.forName("com.adventnet.sym.server.mdm.ios.payload.CMSPayloadSigning").newInstance();
        }
        return PayloadSigningFactory.payloadSigningObj;
    }
    
    static {
        PayloadSigningFactory.payloadSigningObj = null;
        PayloadSigningFactory.iOSProfileSigningStatus = null;
    }
}
