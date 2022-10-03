package com.sun.xml.internal.bind.v2.schemagen.episode;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.TypedXmlWriter;

public interface Klass extends TypedXmlWriter
{
    @XmlAttribute
    void ref(final String p0);
}
