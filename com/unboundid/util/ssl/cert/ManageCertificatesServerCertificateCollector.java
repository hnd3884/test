package com.unboundid.util.ssl.cert;

import java.security.cert.CertificateException;
import java.util.Iterator;
import com.unboundid.ldap.protocol.ExtendedResponseProtocolOp;
import java.net.InetAddress;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.unboundidds.tools.ResultUtils;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1StreamReader;
import com.unboundid.ldap.protocol.ProtocolOp;
import com.unboundid.ldap.protocol.LDAPMessage;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.protocol.ExtendedRequestProtocolOp;
import com.unboundid.util.Debug;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.security.cert.X509Certificate;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import javax.net.ssl.X509TrustManager;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
final class ManageCertificatesServerCertificateCollector extends Thread implements X509TrustManager
{
    private static final int WRAP_COLUMN;
    private static final X509Certificate[] NO_CERTIFICATES;
    private volatile boolean gotCertificateChain;
    private final boolean useLDAPStartTLS;
    private final boolean verbose;
    private final int port;
    private final LinkedBlockingQueue<Object> queue;
    private final ManageCertificates manageCertificates;
    private final String hostname;
    
    ManageCertificatesServerCertificateCollector(final ManageCertificates manageCertificates, final String hostname, final int port, final boolean useLDAPStartTLS, final boolean verbose, final LinkedBlockingQueue<Object> queue) {
        this.setName("ManageCertificatesServerCertificateCollector background thread for " + hostname + ':' + port);
        this.setDaemon(true);
        this.manageCertificates = manageCertificates;
        this.hostname = hostname;
        this.port = port;
        this.useLDAPStartTLS = useLDAPStartTLS;
        this.verbose = verbose;
        this.queue = queue;
        this.gotCertificateChain = false;
    }
    
    @Override
    public void run() {
        final String hostPort = this.hostname + ':' + this.port;
        if (this.verbose) {
            this.manageCertificates.wrapOut(0, ManageCertificatesServerCertificateCollector.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_CERT_COLLECTOR_CONNECTING.get(hostPort));
        }
        Socket nonSecureSocket;
        try {
            nonSecureSocket = new Socket();
            final InetAddress address = LDAPConnectionOptions.DEFAULT_NAME_RESOLVER.getByName(this.hostname);
            nonSecureSocket.connect(new InetSocketAddress(address, this.port), 60);
            if (this.verbose) {
                this.manageCertificates.wrapOut(0, ManageCertificatesServerCertificateCollector.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_CERT_COLLECTOR_CONNECTED.get());
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            final String message = CertMessages.ERR_MANAGE_CERTS_CERT_COLLECTOR_CONNECT_FAILED.get(hostPort);
            this.manageCertificates.err(new Object[0]);
            this.manageCertificates.wrapErr(0, ManageCertificatesServerCertificateCollector.WRAP_COLUMN, message);
            e.printStackTrace(this.manageCertificates.getErr());
            this.queue.offer(new CertException(message, e));
            return;
        }
        try {
            if (this.useLDAPStartTLS) {
                if (this.verbose) {
                    this.manageCertificates.out(new Object[0]);
                    this.manageCertificates.wrapOut(0, ManageCertificatesServerCertificateCollector.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_CERT_COLLECTOR_SENDING_START_TLS.get());
                }
                final LDAPMessage startTLSRequestMessage = new LDAPMessage(1, new ExtendedRequestProtocolOp("1.3.6.1.4.1.1466.20037", null), new Control[0]);
                try {
                    nonSecureSocket.getOutputStream().write(startTLSRequestMessage.encode().encode());
                    nonSecureSocket.getOutputStream().flush();
                    final ASN1StreamReader asn1Reader = new ASN1StreamReader(nonSecureSocket.getInputStream());
                    final LDAPMessage startTLSResponseMessage = LDAPMessage.readFrom(asn1Reader, true);
                    if (startTLSResponseMessage == null) {
                        final String message2 = CertMessages.ERR_MANAGE_CERTS_CERT_COLLECTOR_START_TLS_FAILED.get();
                        this.manageCertificates.wrapErr(0, ManageCertificatesServerCertificateCollector.WRAP_COLUMN, message2);
                        this.queue.offer(new CertException(message2));
                        return;
                    }
                    final ExtendedResponseProtocolOp startTLSResponse = startTLSResponseMessage.getExtendedResponseProtocolOp();
                    if (startTLSResponse.getResultCode() != 0) {
                        final String message3 = CertMessages.ERR_MANAGE_CERTS_CERT_COLLECTOR_START_TLS_FAILED.get();
                        this.manageCertificates.wrapErr(0, ManageCertificatesServerCertificateCollector.WRAP_COLUMN, message3);
                        final String[] referralURLArray = startTLSResponse.getReferralURLs().toArray(StaticUtils.NO_STRINGS);
                        final Control[] responseControlArray = startTLSResponseMessage.getControls().toArray(StaticUtils.NO_CONTROLS);
                        final ExtendedResult extendedResult = new ExtendedResult(startTLSRequestMessage.getMessageID(), ResultCode.valueOf(startTLSResponse.getResultCode()), startTLSResponse.getDiagnosticMessage(), startTLSResponse.getMatchedDN(), referralURLArray, startTLSResponse.getResponseOID(), startTLSResponse.getResponseValue(), responseControlArray);
                        for (final String line : ResultUtils.formatResult(extendedResult, false, 0, ManageCertificatesServerCertificateCollector.WRAP_COLUMN)) {
                            this.manageCertificates.err(line);
                        }
                        this.queue.offer(new CertException(message3));
                        return;
                    }
                    if (this.verbose) {
                        this.manageCertificates.wrapOut(0, ManageCertificatesServerCertificateCollector.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_CERT_COLLECTOR_START_TLS_SUCCESSFUL.get());
                    }
                }
                catch (final Exception e2) {
                    final String message4 = CertMessages.ERR_MANAGE_CERTS_CERT_COLLECTOR_START_TLS_FAILED.get();
                    this.manageCertificates.wrapErr(0, ManageCertificatesServerCertificateCollector.WRAP_COLUMN, message4);
                    e2.printStackTrace(this.manageCertificates.getErr());
                    this.queue.offer(new CertException(message4));
                    return;
                }
            }
            SSLSocket sslSocket;
            try {
                if (this.verbose) {
                    this.manageCertificates.out(new Object[0]);
                    this.manageCertificates.wrapOut(0, ManageCertificatesServerCertificateCollector.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_CERT_COLLECTOR_BEGINNING_TLS_NEGOTIATION.get());
                }
                final SSLUtil sslUtil = new SSLUtil(this);
                sslSocket = (SSLSocket)sslUtil.createSSLSocketFactory().createSocket(nonSecureSocket, this.hostname, this.port, true);
                sslSocket.startHandshake();
                sslSocket.setSoTimeout(1000);
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
                final String message4 = CertMessages.ERR_MANAGE_CERTS_CERT_COLLECTOR_ERROR_STARTING_TLS_NEGOTIATION.get();
                this.manageCertificates.wrapErr(0, ManageCertificatesServerCertificateCollector.WRAP_COLUMN, message4);
                e2.printStackTrace(this.manageCertificates.getErr());
                this.queue.offer(new CertException(message4, e2));
                return;
            }
            try {
                final long stopWaitingTime = System.currentTimeMillis() + 60000L;
                while (System.currentTimeMillis() < stopWaitingTime && !this.gotCertificateChain) {
                    try {
                        final int bytesRead = sslSocket.getInputStream().read();
                        if (bytesRead < 0 && this.gotCertificateChain) {
                            return;
                        }
                        continue;
                    }
                    catch (final Exception e3) {
                        Debug.debugException(e3);
                    }
                }
                if (!this.gotCertificateChain) {
                    final String message2 = CertMessages.ERR_MANAGE_CERTS_CERT_COLLECTOR_NO_CERT_CHAIN_RECEIVED.get(hostPort);
                    this.manageCertificates.wrapErr(0, ManageCertificatesServerCertificateCollector.WRAP_COLUMN, message2);
                    this.queue.offer(new CertException(message2));
                    return;
                }
            }
            finally {
                try {
                    sslSocket.close();
                }
                catch (final Exception e4) {
                    Debug.debugException(e4);
                }
            }
        }
        finally {
            try {
                nonSecureSocket.close();
            }
            catch (final Exception e5) {
                Debug.debugException(e5);
            }
        }
    }
    
    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        try {
            final com.unboundid.util.ssl.cert.X509Certificate[] c = new com.unboundid.util.ssl.cert.X509Certificate[chain.length];
            for (int i = 0; i < chain.length; ++i) {
                c[i] = new com.unboundid.util.ssl.cert.X509Certificate(chain[i].getEncoded());
            }
            if (this.verbose) {
                this.manageCertificates.wrapOut(0, ManageCertificatesServerCertificateCollector.WRAP_COLUMN, CertMessages.INFO_MANAGE_CERTS_CERT_COLLECTOR_GOT_CERT_CHAIN.get());
            }
            this.queue.offer(c);
            this.gotCertificateChain = true;
        }
        catch (final CertException ce) {
            Debug.debugException(ce);
            final String message = CertMessages.ERR_MANAGE_CERTS_CERT_COLLECTOR_ERROR_PARSING_CERT_CHAIN.get(this.hostname + ':' + this.port) + ":   " + ce.getMessage();
            this.manageCertificates.wrapErr(0, ManageCertificatesServerCertificateCollector.WRAP_COLUMN, message);
            for (final X509Certificate c2 : chain) {
                this.manageCertificates.err(c2);
            }
            this.queue.offer(new CertException(message, ce.getCause()));
            this.gotCertificateChain = true;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            final String message = CertMessages.ERR_MANAGE_CERTS_CERT_COLLECTOR_ERROR_PARSING_CERT_CHAIN.get(this.hostname + ':' + this.port);
            this.manageCertificates.wrapErr(0, ManageCertificatesServerCertificateCollector.WRAP_COLUMN, message);
            e.printStackTrace(this.manageCertificates.getErr());
            this.queue.offer(new CertException(message, e));
            this.gotCertificateChain = true;
        }
    }
    
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return ManageCertificatesServerCertificateCollector.NO_CERTIFICATES;
    }
    
    static {
        WRAP_COLUMN = StaticUtils.TERMINAL_WIDTH_COLUMNS - 1;
        NO_CERTIFICATES = new X509Certificate[0];
    }
}
