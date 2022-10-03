package sun.security.x509;

import sun.security.util.DerOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.math.BigInteger;

public class DeltaCRLIndicatorExtension extends CRLNumberExtension
{
    public static final String NAME = "DeltaCRLIndicator";
    private static final String LABEL = "Base CRL Number";
    
    public DeltaCRLIndicatorExtension(final int n) throws IOException {
        super(PKIXExtensions.DeltaCRLIndicator_Id, true, BigInteger.valueOf(n), "DeltaCRLIndicator", "Base CRL Number");
    }
    
    public DeltaCRLIndicatorExtension(final BigInteger bigInteger) throws IOException {
        super(PKIXExtensions.DeltaCRLIndicator_Id, true, bigInteger, "DeltaCRLIndicator", "Base CRL Number");
    }
    
    public DeltaCRLIndicatorExtension(final Boolean b, final Object o) throws IOException {
        super(PKIXExtensions.DeltaCRLIndicator_Id, (boolean)b, o, "DeltaCRLIndicator", "Base CRL Number");
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        super.encode(outputStream, PKIXExtensions.DeltaCRLIndicator_Id, true);
    }
}
