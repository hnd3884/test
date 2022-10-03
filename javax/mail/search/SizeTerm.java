package javax.mail.search;

import javax.mail.Message;

public final class SizeTerm extends IntegerComparisonTerm
{
    private static final long serialVersionUID = -2556219451005103709L;
    
    public SizeTerm(final int comparison, final int size) {
        super(comparison, size);
    }
    
    @Override
    public boolean match(final Message msg) {
        int size;
        try {
            size = msg.getSize();
        }
        catch (final Exception e) {
            return false;
        }
        return size != -1 && super.match(size);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof SizeTerm && super.equals(obj);
    }
}
