package sun.text;

import sun.text.normalizer.NormalizerBase;

public class CollatorUtilities
{
    static NormalizerBase.Mode[] legacyModeMap;
    
    public static int toLegacyMode(final NormalizerBase.Mode mode) {
        int i = CollatorUtilities.legacyModeMap.length;
        while (i > 0) {
            --i;
            if (CollatorUtilities.legacyModeMap[i] == mode) {
                break;
            }
        }
        return i;
    }
    
    public static NormalizerBase.Mode toNormalizerMode(final int n) {
        NormalizerBase.Mode none;
        try {
            none = CollatorUtilities.legacyModeMap[n];
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            none = NormalizerBase.NONE;
        }
        return none;
    }
    
    static {
        CollatorUtilities.legacyModeMap = new NormalizerBase.Mode[] { NormalizerBase.NONE, NormalizerBase.NFD, NormalizerBase.NFKD };
    }
}
