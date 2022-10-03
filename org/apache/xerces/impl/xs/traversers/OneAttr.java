package org.apache.xerces.impl.xs.traversers;

class OneAttr
{
    public String name;
    public int dvIndex;
    public int valueIndex;
    public Object dfltValue;
    
    public OneAttr(final String name, final int dvIndex, final int valueIndex, final Object dfltValue) {
        this.name = name;
        this.dvIndex = dvIndex;
        this.valueIndex = valueIndex;
        this.dfltValue = dfltValue;
    }
}
