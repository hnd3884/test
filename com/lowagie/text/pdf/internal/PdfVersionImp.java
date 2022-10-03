package com.lowagie.text.pdf.internal;

import com.lowagie.text.pdf.PdfDeveloperExtension;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.DocWriter;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import com.lowagie.text.pdf.OutputStreamCounter;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.interfaces.PdfVersion;

public class PdfVersionImp implements PdfVersion
{
    public static final byte[][] HEADER;
    protected boolean headerWasWritten;
    protected boolean appendmode;
    protected char header_version;
    protected PdfName catalog_version;
    protected PdfDictionary extensions;
    
    public PdfVersionImp() {
        this.headerWasWritten = false;
        this.appendmode = false;
        this.header_version = '4';
        this.catalog_version = null;
        this.extensions = null;
    }
    
    @Override
    public void setPdfVersion(final char version) {
        if (this.headerWasWritten || this.appendmode) {
            this.setPdfVersion(this.getVersionAsName(version));
        }
        else {
            this.header_version = version;
        }
    }
    
    @Override
    public void setAtLeastPdfVersion(final char version) {
        if (version > this.header_version) {
            this.setPdfVersion(version);
        }
    }
    
    @Override
    public void setPdfVersion(final PdfName version) {
        if (this.catalog_version == null || this.catalog_version.compareTo(version) < 0) {
            this.catalog_version = version;
        }
    }
    
    public void setAppendmode(final boolean appendmode) {
        this.appendmode = appendmode;
    }
    
    public void writeHeader(final OutputStreamCounter os) throws IOException {
        if (this.appendmode) {
            os.write(PdfVersionImp.HEADER[0]);
        }
        else {
            os.write(PdfVersionImp.HEADER[1]);
            os.write(this.getVersionAsByteArray(this.header_version));
            os.write(PdfVersionImp.HEADER[2]);
            this.headerWasWritten = true;
        }
    }
    
    public PdfName getVersionAsName(final char version) {
        switch (version) {
            case '2': {
                return PdfWriter.PDF_VERSION_1_2;
            }
            case '3': {
                return PdfWriter.PDF_VERSION_1_3;
            }
            case '4': {
                return PdfWriter.PDF_VERSION_1_4;
            }
            case '5': {
                return PdfWriter.PDF_VERSION_1_5;
            }
            case '6': {
                return PdfWriter.PDF_VERSION_1_6;
            }
            case '7': {
                return PdfWriter.PDF_VERSION_1_7;
            }
            default: {
                return PdfWriter.PDF_VERSION_1_4;
            }
        }
    }
    
    public byte[] getVersionAsByteArray(final char version) {
        return DocWriter.getISOBytes(this.getVersionAsName(version).toString().substring(1));
    }
    
    public void addToCatalog(final PdfDictionary catalog) {
        if (this.catalog_version != null) {
            catalog.put(PdfName.VERSION, this.catalog_version);
        }
        if (this.extensions != null) {
            catalog.put(PdfName.EXTENSIONS, this.extensions);
        }
    }
    
    @Override
    public void addDeveloperExtension(final PdfDeveloperExtension de) {
        if (this.extensions == null) {
            this.extensions = new PdfDictionary();
        }
        else {
            final PdfDictionary extension = this.extensions.getAsDict(de.getPrefix());
            if (extension != null) {
                int diff = de.getBaseversion().compareTo(extension.getAsName(PdfName.BASEVERSION));
                if (diff < 0) {
                    return;
                }
                diff = de.getExtensionLevel() - extension.getAsNumber(PdfName.EXTENSIONLEVEL).intValue();
                if (diff <= 0) {
                    return;
                }
            }
        }
        this.extensions.put(de.getPrefix(), de.getDeveloperExtensions());
    }
    
    static {
        HEADER = new byte[][] { DocWriter.getISOBytes("\n"), DocWriter.getISOBytes("%PDF-"), DocWriter.getISOBytes("\n%\u00e2\u00e3\u00cf\u00d3\n") };
    }
}
