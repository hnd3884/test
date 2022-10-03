package org.apache.poi.xddf.usermodel;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineEndProperties;

public class XDDFLineEndProperties
{
    private CTLineEndProperties props;
    
    protected XDDFLineEndProperties(final CTLineEndProperties properties) {
        this.props = properties;
    }
    
    @Internal
    protected CTLineEndProperties getXmlObject() {
        return this.props;
    }
    
    public LineEndLength getLength() {
        return LineEndLength.valueOf(this.props.getLen());
    }
    
    public void setLength(final LineEndLength length) {
        this.props.setLen(length.underlying);
    }
    
    public LineEndType getType() {
        return LineEndType.valueOf(this.props.getType());
    }
    
    public void setType(final LineEndType type) {
        this.props.setType(type.underlying);
    }
    
    public LineEndWidth getWidth() {
        return LineEndWidth.valueOf(this.props.getW());
    }
    
    public void setWidth(final LineEndWidth width) {
        this.props.setW(width.underlying);
    }
}
