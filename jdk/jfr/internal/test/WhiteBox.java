package jdk.jfr.internal.test;

public final class WhiteBox
{
    private static boolean writeAllObjectSamples;
    
    public static void setWriteAllObjectSamples(final boolean writeAllObjectSamples) {
        WhiteBox.writeAllObjectSamples = writeAllObjectSamples;
    }
    
    public static boolean getWriteAllObjectSamples() {
        return WhiteBox.writeAllObjectSamples;
    }
}
