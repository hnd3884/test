package com.sun.xml.internal.fastinfoset;

public class Notation
{
    public final String name;
    public final String systemIdentifier;
    public final String publicIdentifier;
    
    public Notation(final String _name, final String _systemIdentifier, final String _publicIdentifier) {
        this.name = _name;
        this.systemIdentifier = _systemIdentifier;
        this.publicIdentifier = _publicIdentifier;
    }
}
