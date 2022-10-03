package org.apache.xerces.dom3.as;

public interface CharacterDataEditAS extends NodeEditAS
{
    boolean getIsWhitespaceOnly();
    
    boolean canSetData(final int p0, final int p1);
    
    boolean canAppendData(final String p0);
    
    boolean canReplaceData(final int p0, final int p1, final String p2);
    
    boolean canInsertData(final int p0, final String p1);
    
    boolean canDeleteData(final int p0, final int p1);
}
