package sun.print;

import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.PrintService;
import java.awt.Window;
import java.awt.print.PrinterJob;

public abstract class DocumentPropertiesUI
{
    public static final int DOCUMENTPROPERTIES_ROLE = 199;
    public static final String DOCPROPERTIESCLASSNAME;
    
    public abstract PrintRequestAttributeSet showDocumentProperties(final PrinterJob p0, final Window p1, final PrintService p2, final PrintRequestAttributeSet p3);
    
    static {
        DOCPROPERTIESCLASSNAME = DocumentPropertiesUI.class.getName();
    }
}
