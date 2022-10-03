package com.me.mdm.onpremise.server.sgs;

import com.me.ems.onpremise.security.securegatewayserver.proxy.SGSProxyFileData;
import com.me.mdm.server.sgs.SGSProxyFileDataApple;
import com.me.ems.onpremise.security.securegatewayserver.proxy.SGSProxyFilePrimaryData;
import com.me.ems.onpremise.security.securegatewayserver.proxy.SGSProxyFileDataDefaultImpl;

public class SGSProxyFileDataMDMPImpl extends SGSProxyFileDataDefaultImpl
{
    public String getProxyFileData() throws Exception {
        final SGSProxyFilePrimaryData sgsProxyFilePrimaryData = new SGSProxyFilePrimaryData();
        final SGSProxyFileDataApple sgsProxyFileDataApple = new SGSProxyFileDataApple((SGSProxyFileData)sgsProxyFilePrimaryData);
        return sgsProxyFileDataApple.getProxyFileData().toString();
    }
}
