package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLInputFactory;

class StAXDialectUtils
{
    public static XMLInputFactory disallowDoctypeDecl(final XMLInputFactory factory) {
        factory.setProperty("javax.xml.stream.supportDTD", Boolean.FALSE);
        factory.setProperty("javax.xml.stream.isReplacingEntityReferences", Boolean.FALSE);
        factory.setProperty("javax.xml.stream.isSupportingExternalEntities", Boolean.FALSE);
        return new DisallowDoctypeDeclInputFactoryWrapper(factory);
    }
}
