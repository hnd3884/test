package org.apache.axiom.core;

public enum NodeType
{
    DOCUMENT((Class<? extends CoreNode>)CoreDocument.class), 
    DOCUMENT_TYPE_DECLARATION((Class<? extends CoreNode>)CoreDocumentTypeDeclaration.class), 
    NS_UNAWARE_ELEMENT((Class<? extends CoreNode>)CoreNSUnawareElement.class), 
    NS_AWARE_ELEMENT((Class<? extends CoreNode>)CoreNSAwareElement.class), 
    NS_UNAWARE_ATTRIBUTE((Class<? extends CoreNode>)CoreNSUnawareAttribute.class), 
    NS_AWARE_ATTRIBUTE((Class<? extends CoreNode>)CoreNSAwareAttribute.class), 
    NAMESPACE_DECLARATION((Class<? extends CoreNode>)CoreNamespaceDeclaration.class), 
    PROCESSING_INSTRUCTION((Class<? extends CoreNode>)CoreProcessingInstruction.class), 
    DOCUMENT_FRAGMENT((Class<? extends CoreNode>)CoreDocumentFragment.class), 
    CHARACTER_DATA((Class<? extends CoreNode>)CoreCharacterDataNode.class), 
    COMMENT((Class<? extends CoreNode>)CoreComment.class), 
    CDATA_SECTION((Class<? extends CoreNode>)CoreCDATASection.class), 
    ENTITY_REFERENCE((Class<? extends CoreNode>)CoreEntityReference.class);
    
    private final Class<? extends CoreNode> iface;
    
    private NodeType(final Class<? extends CoreNode> iface) {
        this.iface = iface;
    }
    
    public Class<? extends CoreNode> getInterface() {
        return this.iface;
    }
}
