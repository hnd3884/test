package sun.text;

import sun.text.normalizer.NormalizerImpl;
import sun.text.normalizer.NormalizerBase;

public final class Normalizer
{
    public static final int UNICODE_3_2 = 262432;
    
    private Normalizer() {
    }
    
    public static String normalize(final CharSequence charSequence, final java.text.Normalizer.Form form, final int n) {
        return NormalizerBase.normalize(charSequence.toString(), form, n);
    }
    
    public static boolean isNormalized(final CharSequence charSequence, final java.text.Normalizer.Form form, final int n) {
        return NormalizerBase.isNormalized(charSequence.toString(), form, n);
    }
    
    public static final int getCombiningClass(final int n) {
        return NormalizerImpl.getCombiningClass(n);
    }
}
