package sun.print;

import java.awt.print.PrinterJob;
import javax.print.attribute.PrintRequestAttribute;

public class PrinterJobWrapper implements PrintRequestAttribute
{
    private static final long serialVersionUID = -8792124426995707237L;
    private PrinterJob job;
    
    public PrinterJobWrapper(final PrinterJob job) {
        this.job = job;
    }
    
    public PrinterJob getPrinterJob() {
        return this.job;
    }
    
    @Override
    public final Class getCategory() {
        return PrinterJobWrapper.class;
    }
    
    @Override
    public final String getName() {
        return "printerjob-wrapper";
    }
    
    @Override
    public String toString() {
        return "printerjob-wrapper: " + this.job.toString();
    }
    
    @Override
    public int hashCode() {
        return this.job.hashCode();
    }
}
