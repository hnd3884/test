package com.adventnet.sym.server.mdm.certificates.scepserver;

public enum ScepServerType
{
    GENERIC(1), 
    DIGICERT(2), 
    ADCS(3), 
    EJBCA(4);
    
    public final int type;
    
    private ScepServerType(final int type) {
        this.type = type;
    }
}
