package com.adventnet.iam.xss;

import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLString;
import org.cyberneko.html.filters.Writer;

public class UnEncodedWriter extends Writer
{
    public UnEncodedWriter(final java.io.Writer writer, final String encoding) {
        super(writer, encoding);
    }
    
    protected void printCharacters(final XMLString text, final boolean normalize) {
        if (normalize) {
            for (int i = 0; i < text.length; ++i) {
                final char c = text.ch[text.offset + i];
                if (c != '\n') {
                    this.fPrinter.print(c);
                }
                else {
                    this.fPrinter.println();
                }
            }
        }
        else {
            for (int i = 0; i < text.length; ++i) {
                final char c = text.ch[text.offset + i];
                this.fPrinter.print(c);
            }
        }
        this.fPrinter.flush();
    }
    
    public void doctypeDecl(final String root, final String publicId, final String systemId, final Augmentations augs) throws XNIException {
        this.fPrinter.print("<!DOCTYPE ");
        this.fPrinter.print(root);
        if (publicId == null && systemId != null) {
            this.fPrinter.print(" SYSTEM \"");
            this.fPrinter.print(systemId);
            this.fPrinter.print("\"");
        }
        else if (publicId != null) {
            this.fPrinter.print(" PUBLIC \"");
            this.fPrinter.print(publicId);
            this.fPrinter.print("\"");
            if (systemId != null) {
                this.fPrinter.print(" \"");
                this.fPrinter.print(systemId);
                this.fPrinter.print("\"");
            }
        }
        this.fPrinter.print(">");
        this.fPrinter.flush();
        super.doctypeDecl(root, publicId, systemId, augs);
    }
}
