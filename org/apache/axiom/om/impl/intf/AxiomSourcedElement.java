package org.apache.axiom.om.impl.intf;

import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.ClonePolicy;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMXMLStreamReaderConfiguration;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMException;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMSourcedElement;

public interface AxiomSourcedElement extends OMSourcedElement, AxiomElement
{
    Class<? extends CoreNode> coreGetNodeClass();
    
    void forceExpand();
    
    OMDataSource getDataSource();
    
    OMNamespace getNamespace() throws OMException;
    
    Object getObject(final Class p0);
    
    QName getQName();
    
    XMLStreamReader getXMLStreamReader(final boolean p0, final OMXMLStreamReaderConfiguration p1);
    
    void init(final String p0, final OMNamespace p1, final OMDataSource p2);
    
    void init(final QName p0, final OMDataSource p1);
    
    void init(final OMDataSource p0);
    
     <T> void initSource(final ClonePolicy<T> p0, final T p1, final CoreElement p2);
    
    void internalSerialize(final Serializer p0, final OMOutputFormat p1, final boolean p2) throws OutputException;
    
    boolean isExpanded();
    
    void setComplete(final boolean p0);
    
    OMDataSource setDataSource(final OMDataSource p0);
    
    void updateLocalName();
}
