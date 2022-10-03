package jdk.jfr.events;

import jdk.jfr.Description;
import jdk.jfr.Name;
import jdk.jfr.Label;
import jdk.jfr.Category;

@Category({ "Java Development Kit", "Security" })
@Label("TLS Handshake")
@Name("jdk.TLSHandshake")
@Description("Parameters used in TLS Handshake")
public final class TLSHandshakeEvent extends AbstractJDKEvent
{
    @Label("Peer Host")
    public String peerHost;
    @Label("Peer Port")
    public int peerPort;
    @Label("Protocol Version")
    public String protocolVersion;
    @Label("Cipher Suite")
    public String cipherSuite;
    @Label("Certificate Id")
    @Description("Peer Certificate Id")
    @CertificateId
    public long certificateId;
}
