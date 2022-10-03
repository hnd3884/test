package org.bouncycastle.asn1.x509;

import java.util.Vector;

public class GeneralNamesBuilder
{
    private Vector names;
    
    public GeneralNamesBuilder() {
        this.names = new Vector();
    }
    
    public GeneralNamesBuilder addNames(final GeneralNames generalNames) {
        final GeneralName[] names = generalNames.getNames();
        for (int i = 0; i != names.length; ++i) {
            this.names.addElement(names[i]);
        }
        return this;
    }
    
    public GeneralNamesBuilder addName(final GeneralName generalName) {
        this.names.addElement(generalName);
        return this;
    }
    
    public GeneralNames build() {
        final GeneralName[] array = new GeneralName[this.names.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = (GeneralName)this.names.elementAt(i);
        }
        return new GeneralNames(array);
    }
}
