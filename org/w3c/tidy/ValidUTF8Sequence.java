package org.w3c.tidy;

public class ValidUTF8Sequence
{
    int lowChar;
    int highChar;
    int numBytes;
    char[] validBytes;
    
    public ValidUTF8Sequence(final int lowChar, final int highChar, final int numBytes, final char[] validBytes) {
        this.validBytes = new char[8];
        this.lowChar = lowChar;
        this.highChar = highChar;
        this.numBytes = numBytes;
        this.validBytes = validBytes;
    }
}
