package com.sun.xml.internal.fastinfoset;

public class UnparsedEntity extends Notation
{
    public final String notationName;
    
    public UnparsedEntity(final String _name, final String _systemIdentifier, final String _publicIdentifier, final String _notationName) {
        super(_name, _systemIdentifier, _publicIdentifier);
        this.notationName = _notationName;
    }
}
