package javax.print.event;

import javax.print.attribute.AttributeSetUtilities;
import javax.print.PrintService;
import javax.print.attribute.PrintServiceAttributeSet;

public class PrintServiceAttributeEvent extends PrintEvent
{
    private static final long serialVersionUID = -7565987018140326600L;
    private PrintServiceAttributeSet attributes;
    
    public PrintServiceAttributeEvent(final PrintService printService, final PrintServiceAttributeSet set) {
        super(printService);
        this.attributes = AttributeSetUtilities.unmodifiableView(set);
    }
    
    public PrintService getPrintService() {
        return (PrintService)this.getSource();
    }
    
    public PrintServiceAttributeSet getAttributes() {
        return this.attributes;
    }
}
