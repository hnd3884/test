package org.apache.catalina.storeconfig;

import org.apache.tomcat.util.net.openssl.OpenSSLConfCmd;
import org.apache.tomcat.util.net.openssl.OpenSSLConf;
import java.io.PrintWriter;

public class OpenSSLConfSF extends StoreFactoryBase
{
    @Override
    public void storeChildren(final PrintWriter aWriter, final int indent, final Object aOpenSSLConf, final StoreDescription parentDesc) throws Exception {
        if (aOpenSSLConf instanceof OpenSSLConf) {
            final OpenSSLConf openSslConf = (OpenSSLConf)aOpenSSLConf;
            final OpenSSLConfCmd[] openSSLConfCmds = openSslConf.getCommands().toArray(new OpenSSLConfCmd[0]);
            this.storeElementArray(aWriter, indent + 2, openSSLConfCmds);
        }
    }
}
