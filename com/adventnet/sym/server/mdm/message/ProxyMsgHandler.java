package com.adventnet.sym.server.mdm.message;

import com.adventnet.sym.server.mdm.util.MDMUtil;

class ProxyMsgHandler implements MessageListener
{
    @Override
    public Boolean getMessageStatus(final Long customerId) {
        final String proxyDefined = MDMUtil.getSyMParameter("proxy_defined");
        if (proxyDefined != null) {
            return Boolean.parseBoolean(proxyDefined);
        }
        return Boolean.FALSE;
    }
}
