package com.adventnet.sym.server.mdm.certificates.scep.passwordgetter;

import com.adventnet.sym.server.mdm.certificates.scep.PasswordResponse;
import java.util.Map;
import java.util.List;
import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServer;

public interface ScepPasswordGetter
{
    Map<Long, PasswordResponse> getPasswordsFromScepServer(final ScepServer p0, final List<Long> p1);
}
