package org.apache.xmlbeans.impl.inst2xsd;

import org.apache.xmlbeans.impl.inst2xsd.util.Type;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.inst2xsd.util.TypeSystemHolder;
import org.apache.xmlbeans.impl.inst2xsd.util.Element;

public class VenetianBlindStrategy extends RussianDollStrategy implements XsdGenStrategy
{
    @Override
    protected void checkIfReferenceToGlobalTypeIsNeeded(final Element elem, final TypeSystemHolder typeSystemHolder, final Inst2XsdOptions options) {
        final Type elemType = elem.getType();
        final QName elemName = elem.getName();
        if (elemType.isGlobal()) {
            return;
        }
        if (elemType.isComplexType()) {
            int i = 0;
            while (true) {
                elemType.setName(new QName(elemName.getNamespaceURI(), elemName.getLocalPart() + "Type" + ((i == 0) ? "" : ("" + i))));
                final Type candidate = typeSystemHolder.getGlobalType(elemType.getName());
                if (candidate == null) {
                    elemType.setGlobal(true);
                    typeSystemHolder.addGlobalType(elemType);
                    break;
                }
                if (this.compatibleTypes(candidate, elemType)) {
                    this.combineTypes(candidate, elemType, options);
                    elem.setType(candidate);
                    break;
                }
                ++i;
            }
        }
    }
    
    private boolean compatibleTypes(final Type elemType, final Type candidate) {
        return elemType != candidate || true;
    }
}
