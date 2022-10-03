package sun.text;

import sun.text.normalizer.NormalizerImpl;

public final class ComposedCharIter
{
    public static final int DONE = -1;
    private static int[] chars;
    private static String[] decomps;
    private static int decompNum;
    private int curChar;
    
    public ComposedCharIter() {
        this.curChar = -1;
    }
    
    public int next() {
        if (this.curChar == ComposedCharIter.decompNum - 1) {
            return -1;
        }
        return ComposedCharIter.chars[++this.curChar];
    }
    
    public String decomposition() {
        return ComposedCharIter.decomps[this.curChar];
    }
    
    static {
        final int n = 2000;
        ComposedCharIter.chars = new int[n];
        ComposedCharIter.decomps = new String[n];
        ComposedCharIter.decompNum = NormalizerImpl.getDecompose(ComposedCharIter.chars, ComposedCharIter.decomps);
    }
}
