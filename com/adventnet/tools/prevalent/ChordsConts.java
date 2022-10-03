package com.adventnet.tools.prevalent;

public final class ChordsConts
{
    public static char[] CONTSS;
    public static char[] CONTS;
    private static ChordsConts chord;
    
    private ChordsConts() {
    }
    
    public static ChordsConts getInstance() {
        if (ChordsConts.chord == null) {
            ChordsConts.chord = new ChordsConts();
        }
        return ChordsConts.chord;
    }
    
    public static int getPos(final char c) {
        return String.copyValueOf(ChordsConts.CONTS).indexOf(c);
    }
    
    static {
        ChordsConts.CONTSS = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
        ChordsConts.CONTS = new char[] { 'a', 'B', 'c', 'D', 'e', 'F', 'g', 'H', 'i', 'J', 'k', 'L', 'm', 'N', 'o', 'P', 'q', 'R', 's', 'T', 'u', 'V', 'w', 'X', 'y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'b', 'C', 'd', 'E', 'f', 'G', 'h', 'I', 'j', 'K', 'l', 'M', 'n', 'O', 'p', 'Q', 'r', 'S', 't', 'U', 'v', 'W', 'x', 'Y', 'z' };
        ChordsConts.chord = null;
    }
}
