package javax.print;

import javax.print.attribute.AttributeSet;
import javax.print.attribute.Attribute;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.event.PrintServiceAttributeListener;

public interface PrintService
{
    String getName();
    
    DocPrintJob createPrintJob();
    
    void addPrintServiceAttributeListener(final PrintServiceAttributeListener p0);
    
    void removePrintServiceAttributeListener(final PrintServiceAttributeListener p0);
    
    PrintServiceAttributeSet getAttributes();
    
     <T extends PrintServiceAttribute> T getAttribute(final Class<T> p0);
    
    DocFlavor[] getSupportedDocFlavors();
    
    boolean isDocFlavorSupported(final DocFlavor p0);
    
    Class<?>[] getSupportedAttributeCategories();
    
    boolean isAttributeCategorySupported(final Class<? extends Attribute> p0);
    
    Object getDefaultAttributeValue(final Class<? extends Attribute> p0);
    
    Object getSupportedAttributeValues(final Class<? extends Attribute> p0, final DocFlavor p1, final AttributeSet p2);
    
    boolean isAttributeValueSupported(final Attribute p0, final DocFlavor p1, final AttributeSet p2);
    
    AttributeSet getUnsupportedAttributes(final DocFlavor p0, final AttributeSet p1);
    
    ServiceUIFactory getServiceUIFactory();
    
    boolean equals(final Object p0);
    
    int hashCode();
}
