package sun.print;

import javax.print.StreamPrintService;
import java.io.OutputStream;
import javax.print.DocFlavor;
import javax.print.StreamPrintServiceFactory;

public class PSStreamPrinterFactory extends StreamPrintServiceFactory
{
    static final String psMimeType = "application/postscript";
    static final DocFlavor[] supportedDocFlavors;
    
    @Override
    public String getOutputFormat() {
        return "application/postscript";
    }
    
    @Override
    public DocFlavor[] getSupportedDocFlavors() {
        return getFlavors();
    }
    
    static DocFlavor[] getFlavors() {
        final DocFlavor[] array = new DocFlavor[PSStreamPrinterFactory.supportedDocFlavors.length];
        System.arraycopy(PSStreamPrinterFactory.supportedDocFlavors, 0, array, 0, array.length);
        return array;
    }
    
    @Override
    public StreamPrintService getPrintService(final OutputStream outputStream) {
        return new PSStreamPrintService(outputStream);
    }
    
    static {
        supportedDocFlavors = new DocFlavor[] { DocFlavor.SERVICE_FORMATTED.PAGEABLE, DocFlavor.SERVICE_FORMATTED.PRINTABLE, DocFlavor.BYTE_ARRAY.GIF, DocFlavor.INPUT_STREAM.GIF, DocFlavor.URL.GIF, DocFlavor.BYTE_ARRAY.JPEG, DocFlavor.INPUT_STREAM.JPEG, DocFlavor.URL.JPEG, DocFlavor.BYTE_ARRAY.PNG, DocFlavor.INPUT_STREAM.PNG, DocFlavor.URL.PNG };
    }
}
