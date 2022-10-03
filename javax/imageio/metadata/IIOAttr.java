package javax.imageio.metadata;

import org.w3c.dom.Element;
import org.w3c.dom.Attr;

class IIOAttr extends IIOMetadataNode implements Attr
{
    Element owner;
    String name;
    String value;
    
    public IIOAttr(final Element owner, final String name, final String value) {
        this.owner = owner;
        this.name = name;
        this.value = value;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String getNodeName() {
        return this.name;
    }
    
    @Override
    public short getNodeType() {
        return 2;
    }
    
    @Override
    public boolean getSpecified() {
        return true;
    }
    
    @Override
    public String getValue() {
        return this.value;
    }
    
    @Override
    public String getNodeValue() {
        return this.value;
    }
    
    @Override
    public void setValue(final String value) {
        this.value = value;
    }
    
    @Override
    public void setNodeValue(final String value) {
        this.value = value;
    }
    
    @Override
    public Element getOwnerElement() {
        return this.owner;
    }
    
    public void setOwnerElement(final Element owner) {
        this.owner = owner;
    }
    
    @Override
    public boolean isId() {
        return false;
    }
}
