package org.apache.poi.ddf;

public class EscherRGBProperty extends EscherSimpleProperty
{
    public EscherRGBProperty(final short propertyNumber, final int rgbColor) {
        super(propertyNumber, rgbColor);
    }
    
    public EscherRGBProperty(final EscherPropertyTypes propertyType, final int rgbColor) {
        super(propertyType.propNumber, rgbColor);
    }
    
    public int getRgbColor() {
        return this.getPropertyValue();
    }
    
    public byte getRed() {
        return (byte)(this.getRgbColor() & 0xFF);
    }
    
    public byte getGreen() {
        return (byte)(this.getRgbColor() >> 8 & 0xFF);
    }
    
    public byte getBlue() {
        return (byte)(this.getRgbColor() >> 16 & 0xFF);
    }
}
