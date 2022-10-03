package org.apache.axiom.om.impl.intf;

import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import javax.activation.DataHandler;
import org.apache.axiom.core.CoreCharacterDataContainer;
import org.apache.axiom.om.OMText;

public interface AxiomText extends OMText, AxiomLeafNode, CoreCharacterDataContainer
{
    void buildWithAttachments();
    
    String getContentID();
    
    DataHandler getDataHandler();
    
    OMNamespace getNamespace();
    
    String getText() throws OMException;
    
    QName getTextAsQName() throws OMException;
    
    char[] getTextCharacters();
    
    void internalSerialize(final Serializer p0, final OMOutputFormat p1, final boolean p2) throws OutputException;
    
    boolean isBinary();
    
    boolean isCharacters();
    
    boolean isOptimized();
    
    void setBinary(final boolean p0);
    
    void setContentID(final String p0);
    
    void setOptimize(final boolean p0);
}
