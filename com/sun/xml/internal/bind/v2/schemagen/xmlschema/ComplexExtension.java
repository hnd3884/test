package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.TypedXmlWriter;

@XmlElement("extension")
public interface ComplexExtension extends AttrDecls, ExtensionType, TypeDefParticle, TypedXmlWriter
{
}
