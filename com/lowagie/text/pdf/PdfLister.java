package com.lowagie.text.pdf;

import java.io.IOException;
import java.util.Iterator;
import java.io.PrintStream;

public class PdfLister
{
    PrintStream out;
    
    public PdfLister(final PrintStream out) {
        this.out = out;
    }
    
    public void listAnyObject(final PdfObject object) {
        switch (object.type()) {
            case 5: {
                this.listArray((PdfArray)object);
                break;
            }
            case 6: {
                this.listDict((PdfDictionary)object);
                break;
            }
            case 3: {
                this.out.println("(" + object.toString() + ")");
                break;
            }
            default: {
                this.out.println(object.toString());
                break;
            }
        }
    }
    
    public void listDict(final PdfDictionary dictionary) {
        this.out.println("<<");
        for (final PdfName key : dictionary.getKeys()) {
            final PdfObject value = dictionary.get(key);
            this.out.print(key.toString());
            this.out.print(' ');
            this.listAnyObject(value);
        }
        this.out.println(">>");
    }
    
    public void listArray(final PdfArray array) {
        this.out.println('[');
        final Iterator i = array.listIterator();
        while (i.hasNext()) {
            final PdfObject item = i.next();
            this.listAnyObject(item);
        }
        this.out.println(']');
    }
    
    public void listStream(final PRStream stream, final PdfReaderInstance reader) {
        try {
            this.listDict(stream);
            this.out.println("startstream");
            final byte[] b = PdfReader.getStreamBytes(stream);
            for (int len = b.length - 1, k = 0; k < len; ++k) {
                if (b[k] == 13 && b[k + 1] != 10) {
                    b[k] = 10;
                }
            }
            this.out.println(new String(b));
            this.out.println("endstream");
        }
        catch (final IOException e) {
            System.err.println("I/O exception: " + e);
        }
    }
    
    public void listPage(final PdfImportedPage iPage) {
        final int pageNum = iPage.getPageNumber();
        final PdfReaderInstance readerInst = iPage.getPdfReaderInstance();
        final PdfReader reader = readerInst.getReader();
        final PdfDictionary page = reader.getPageN(pageNum);
        this.listDict(page);
        final PdfObject obj = PdfReader.getPdfObject(page.get(PdfName.CONTENTS));
        if (obj == null) {
            return;
        }
        switch (obj.type) {
            case 7: {
                this.listStream((PRStream)obj, readerInst);
                break;
            }
            case 5: {
                final Iterator i = ((PdfArray)obj).listIterator();
                while (i.hasNext()) {
                    final PdfObject o = PdfReader.getPdfObject(i.next());
                    this.listStream((PRStream)o, readerInst);
                    this.out.println("-----------");
                }
                break;
            }
        }
    }
}
