package com.adventnet.sym.server.mdm.certificates.scepserver.digicert;

import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServer;

public class DigicertScepServer extends ScepServer
{
    public final long raCertificateId;
    public final String certificateOID;
    public final long csrId;
    
    public DigicertScepServer(final long raCertificateId, final long csrId, final String certificateOID) {
        this.raCertificateId = raCertificateId;
        this.csrId = csrId;
        this.certificateOID = certificateOID;
    }
}
