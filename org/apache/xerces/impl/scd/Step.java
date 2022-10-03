package org.apache.xerces.impl.scd;

import org.apache.xerces.xni.QName;

class Step
{
    private final short axis;
    private final QName nametest;
    private final int predicate;
    
    public Step(final short axis, final QName nametest, final int predicate) {
        this.axis = axis;
        this.nametest = nametest;
        this.predicate = predicate;
    }
    
    public short getAxisType() {
        return this.axis;
    }
    
    public String getAxisName() {
        return Axis.axisToString(this.axis);
    }
    
    public QName getNametest() {
        return this.nametest;
    }
    
    public int getPredicate() {
        return this.predicate;
    }
    
    public String toString() {
        return "(axis=" + Axis.axisToString(this.axis) + ", nametest=" + ((this.nametest != null) ? ("{\"" + this.nametest.uri + "\"" + " \"" + this.nametest.rawname + "\"}") : null) + ", predicate= " + this.predicate + ")";
    }
}
