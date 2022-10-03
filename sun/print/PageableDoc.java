package sun.print;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.io.Reader;
import java.io.IOException;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.DocAttributeSet;
import javax.print.DocFlavor;
import java.awt.print.Pageable;
import javax.print.Doc;

public class PageableDoc implements Doc
{
    private Pageable pageable;
    
    public PageableDoc(final Pageable pageable) {
        this.pageable = pageable;
    }
    
    @Override
    public DocFlavor getDocFlavor() {
        return DocFlavor.SERVICE_FORMATTED.PAGEABLE;
    }
    
    @Override
    public DocAttributeSet getAttributes() {
        return new HashDocAttributeSet();
    }
    
    @Override
    public Object getPrintData() throws IOException {
        return this.pageable;
    }
    
    @Override
    public Reader getReaderForText() throws UnsupportedEncodingException, IOException {
        return null;
    }
    
    @Override
    public InputStream getStreamForBytes() throws IOException {
        return null;
    }
}
