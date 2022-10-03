package sun.security.x509;

import java.util.Comparator;

class AVAComparator implements Comparator<AVA>
{
    private static final Comparator<AVA> INSTANCE;
    
    private AVAComparator() {
    }
    
    static Comparator<AVA> getInstance() {
        return AVAComparator.INSTANCE;
    }
    
    @Override
    public int compare(final AVA ava, final AVA ava2) {
        final boolean hasRFC2253Keyword = ava.hasRFC2253Keyword();
        if (hasRFC2253Keyword == ava2.hasRFC2253Keyword()) {
            return ava.toRFC2253CanonicalString().compareTo(ava2.toRFC2253CanonicalString());
        }
        if (hasRFC2253Keyword) {
            return -1;
        }
        return 1;
    }
    
    static {
        INSTANCE = new AVAComparator();
    }
}
