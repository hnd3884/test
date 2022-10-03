package org.apache.catalina.storeconfig;

import org.apache.tomcat.util.net.openssl.OpenSSLConf;
import java.util.ArrayList;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.net.SSLHostConfig;
import java.io.PrintWriter;

public class SSLHostConfigSF extends StoreFactoryBase
{
    @Override
    public void storeChildren(final PrintWriter aWriter, final int indent, final Object aSSLHostConfig, final StoreDescription parentDesc) throws Exception {
        if (aSSLHostConfig instanceof SSLHostConfig) {
            final SSLHostConfig sslHostConfig = (SSLHostConfig)aSSLHostConfig;
            SSLHostConfigCertificate[] hostConfigsCertificates = sslHostConfig.getCertificates().toArray(new SSLHostConfigCertificate[0]);
            if (hostConfigsCertificates.length > 1) {
                final ArrayList<SSLHostConfigCertificate> certificates = new ArrayList<SSLHostConfigCertificate>();
                for (final SSLHostConfigCertificate certificate : hostConfigsCertificates) {
                    if (SSLHostConfigCertificate.Type.UNDEFINED != certificate.getType()) {
                        certificates.add(certificate);
                    }
                }
                hostConfigsCertificates = certificates.toArray(new SSLHostConfigCertificate[0]);
            }
            this.storeElementArray(aWriter, indent, hostConfigsCertificates);
            final OpenSSLConf openSslConf = sslHostConfig.getOpenSslConf();
            this.storeElement(aWriter, indent, openSslConf);
        }
    }
}
