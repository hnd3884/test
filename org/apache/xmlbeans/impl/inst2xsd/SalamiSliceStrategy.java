package org.apache.xmlbeans.impl.inst2xsd;

import org.apache.xmlbeans.impl.inst2xsd.util.TypeSystemHolder;
import org.apache.xmlbeans.impl.inst2xsd.util.Element;

public class SalamiSliceStrategy extends RussianDollStrategy implements XsdGenStrategy
{
    @Override
    protected void checkIfElementReferenceIsNeeded(final Element child, final String parentNamespace, final TypeSystemHolder typeSystemHolder, final Inst2XsdOptions options) {
        Element referencedElem = new Element();
        referencedElem.setGlobal(true);
        referencedElem.setName(child.getName());
        referencedElem.setType(child.getType());
        if (child.isNillable()) {
            referencedElem.setNillable(true);
            child.setNillable(false);
        }
        referencedElem = this.addGlobalElement(referencedElem, typeSystemHolder, options);
        child.setRef(referencedElem);
    }
}
