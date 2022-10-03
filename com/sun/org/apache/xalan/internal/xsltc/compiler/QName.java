package com.sun.org.apache.xalan.internal.xsltc.compiler;

final class QName
{
    private final String _localname;
    private String _prefix;
    private String _namespace;
    private String _stringRep;
    private int _hashCode;
    
    public QName(final String namespace, final String prefix, final String localname) {
        this._namespace = namespace;
        this._prefix = prefix;
        this._localname = localname;
        this._stringRep = ((namespace != null && !namespace.equals("")) ? (namespace + ':' + localname) : localname);
        this._hashCode = this._stringRep.hashCode() + 19;
    }
    
    public void clearNamespace() {
        this._namespace = "";
    }
    
    @Override
    public String toString() {
        return this._stringRep;
    }
    
    public String getStringRep() {
        return this._stringRep;
    }
    
    @Override
    public boolean equals(final Object other) {
        return this == other || (other instanceof QName && this._stringRep.equals(((QName)other).getStringRep()));
    }
    
    public String getLocalPart() {
        return this._localname;
    }
    
    public String getNamespace() {
        return this._namespace;
    }
    
    public String getPrefix() {
        return this._prefix;
    }
    
    @Override
    public int hashCode() {
        return this._hashCode;
    }
    
    public String dump() {
        return "QName: " + this._namespace + "(" + this._prefix + "):" + this._localname;
    }
}
