package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.SetOfIntegerSyntax;

public final class PageRanges extends SetOfIntegerSyntax implements DocAttribute, PrintRequestAttribute, PrintJobAttribute
{
    private static final long serialVersionUID = 8639895197656148392L;
    
    public PageRanges(final int[][] array) {
        super(array);
        if (array == null) {
            throw new NullPointerException("members is null");
        }
        this.myPageRanges();
    }
    
    public PageRanges(final String s) {
        super(s);
        if (s == null) {
            throw new NullPointerException("members is null");
        }
        this.myPageRanges();
    }
    
    private void myPageRanges() {
        final int[][] members = this.getMembers();
        final int length = members.length;
        if (length == 0) {
            throw new IllegalArgumentException("members is zero-length");
        }
        for (int i = 0; i < length; ++i) {
            if (members[i][0] < 1) {
                throw new IllegalArgumentException("Page value < 1 specified");
            }
        }
    }
    
    public PageRanges(final int n) {
        super(n);
        if (n < 1) {
            throw new IllegalArgumentException("Page value < 1 specified");
        }
    }
    
    public PageRanges(final int n, final int n2) {
        super(n, n2);
        if (n > n2) {
            throw new IllegalArgumentException("Null range specified");
        }
        if (n < 1) {
            throw new IllegalArgumentException("Page value < 1 specified");
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o instanceof PageRanges;
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return PageRanges.class;
    }
    
    @Override
    public final String getName() {
        return "page-ranges";
    }
}
