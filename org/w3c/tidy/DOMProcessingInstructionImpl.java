package org.w3c.tidy;

import org.w3c.dom.DOMException;
import org.w3c.dom.ProcessingInstruction;

public class DOMProcessingInstructionImpl extends DOMNodeImpl implements ProcessingInstruction
{
    protected DOMProcessingInstructionImpl(final org.w3c.tidy.Node node) {
        super(node);
    }
    
    public short getNodeType() {
        return 7;
    }
    
    public String getTarget() {
        return null;
    }
    
    public String getData() {
        return this.getNodeValue();
    }
    
    public void setData(final String s) throws DOMException {
        throw new DOMException((short)7, "Node is read only");
    }
}
