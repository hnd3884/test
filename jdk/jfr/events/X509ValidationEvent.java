package jdk.jfr.events;

import jdk.jfr.Description;
import jdk.jfr.Name;
import jdk.jfr.Label;
import jdk.jfr.Category;

@Category({ "Java Development Kit", "Security" })
@Label("X509 Validation")
@Name("jdk.X509Validation")
@Description("Serial numbers from X.509 Certificates forming chain of trust")
public final class X509ValidationEvent extends AbstractJDKEvent
{
    @CertificateId
    @Label("Certificate Id")
    public long certificateId;
    @Label("Certificate Position")
    @Description("Certificate position in chain of trust, 1 = trust anchor")
    public int certificatePosition;
    @Label("Validation Counter")
    public long validationCounter;
}
