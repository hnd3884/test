package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.SupportedValuesAttribute;
import javax.print.attribute.SetOfIntegerSyntax;

public final class JobImpressionsSupported extends SetOfIntegerSyntax implements SupportedValuesAttribute
{
    private static final long serialVersionUID = -4887354803843173692L;
    
    public JobImpressionsSupported(final int n, final int n2) {
        super(n, n2);
        if (n > n2) {
            throw new IllegalArgumentException("Null range specified");
        }
        if (n < 0) {
            throw new IllegalArgumentException("Job K octets value < 0 specified");
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof JobImpressionsSupported;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return JobImpressionsSupported.class;
    }
    
    @Override
    public final String getName() {
        return "job-impressions-supported";
    }
}
