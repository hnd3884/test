package com.adventnet.sym.server.mdm.certificates.scep.request;

import com.adventnet.sym.server.mdm.certificates.scep.response.ScepResponse;

public interface ScepRequestHandler
{
    ScepResponse handleRequest() throws Exception;
}
