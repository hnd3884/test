package com.sun.org.apache.xml.internal.security.signature.reference;

import org.w3c.dom.Node;
import java.util.Iterator;

public interface ReferenceNodeSetData extends ReferenceData
{
    Iterator<Node> iterator();
}
