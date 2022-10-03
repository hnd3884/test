package sun.security.x509;

import java.io.OutputStream;
import java.io.IOException;
import java.util.List;

public class FreshestCRLExtension extends CRLDistributionPointsExtension
{
    public static final String NAME = "FreshestCRL";
    
    public FreshestCRLExtension(final List<DistributionPoint> list) throws IOException {
        super(PKIXExtensions.FreshestCRL_Id, false, list, "FreshestCRL");
    }
    
    public FreshestCRLExtension(final Boolean b, final Object o) throws IOException {
        super(PKIXExtensions.FreshestCRL_Id, (boolean)b, o, "FreshestCRL");
    }
    
    @Override
    public void encode(final OutputStream outputStream) throws IOException {
        super.encode(outputStream, PKIXExtensions.FreshestCRL_Id, false);
    }
}
