package sun.print;

import javax.print.DocFlavor;
import javax.print.FlavorException;
import javax.print.PrintException;

class PrintJobFlavorException extends PrintException implements FlavorException
{
    private DocFlavor flavor;
    
    PrintJobFlavorException(final String s, final DocFlavor flavor) {
        super(s);
        this.flavor = flavor;
    }
    
    @Override
    public DocFlavor[] getUnsupportedFlavors() {
        return new DocFlavor[] { this.flavor };
    }
}
