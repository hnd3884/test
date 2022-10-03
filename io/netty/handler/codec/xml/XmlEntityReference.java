package io.netty.handler.codec.xml;

public class XmlEntityReference
{
    private final String name;
    private final String text;
    
    public XmlEntityReference(final String name, final String text) {
        this.name = name;
        this.text = text;
    }
    
    public String name() {
        return this.name;
    }
    
    public String text() {
        return this.text;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final XmlEntityReference that = (XmlEntityReference)o;
        if (this.name != null) {
            if (this.name.equals(that.name)) {
                return (this.text != null) ? this.text.equals(that.text) : (that.text == null);
            }
        }
        else if (that.name == null) {
            return (this.text != null) ? this.text.equals(that.text) : (that.text == null);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int result = (this.name != null) ? this.name.hashCode() : 0;
        result = 31 * result + ((this.text != null) ? this.text.hashCode() : 0);
        return result;
    }
    
    @Override
    public String toString() {
        return "XmlEntityReference{name='" + this.name + '\'' + ", text='" + this.text + '\'' + '}';
    }
}
