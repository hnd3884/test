package com.lowagie.text.pdf.parser;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.PdfLiteral;
import java.util.ArrayList;
import com.lowagie.text.pdf.PdfContentParser;
import com.lowagie.text.pdf.PRTokeniser;
import java.util.ListIterator;
import com.lowagie.text.pdf.PdfArray;
import java.io.ByteArrayOutputStream;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PRIndirectReference;
import java.io.IOException;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfReader;

public class PdfTextExtractor
{
    private final PdfReader reader;
    private final TextAssembler renderListener;
    
    public PdfTextExtractor(final PdfReader reader) {
        this(reader, new MarkedUpTextAssembler(reader));
    }
    
    public PdfTextExtractor(final PdfReader reader, final boolean usePdfMarkupElements) {
        this(reader, new MarkedUpTextAssembler(reader, usePdfMarkupElements));
    }
    
    public PdfTextExtractor(final PdfReader reader, final TextAssembler renderListener) {
        this.reader = reader;
        this.renderListener = renderListener;
    }
    
    private byte[] getContentBytesForPage(final int pageNum) throws IOException {
        final RandomAccessFileOrArray f = this.reader.getSafeFile();
        try {
            final PdfDictionary pageDictionary = this.reader.getPageN(pageNum);
            final PdfObject contentObject = pageDictionary.get(PdfName.CONTENTS);
            final byte[] contentBytes = this.getContentBytesFromContentObject(contentObject);
            return contentBytes;
        }
        finally {
            f.close();
        }
    }
    
    private byte[] getContentBytesFromContentObject(final PdfObject contentObject) throws IOException {
        byte[] result = null;
        switch (contentObject.type()) {
            case 10: {
                final PRIndirectReference ref = (PRIndirectReference)contentObject;
                final PdfObject directObject = PdfReader.getPdfObject(ref);
                result = this.getContentBytesFromContentObject(directObject);
                break;
            }
            case 7: {
                final PRStream stream = (PRStream)PdfReader.getPdfObject(contentObject);
                result = PdfReader.getStreamBytes(stream);
                break;
            }
            case 5: {
                final ByteArrayOutputStream allBytes = new ByteArrayOutputStream();
                final PdfArray contentArray = (PdfArray)contentObject;
                final ListIterator<PdfObject> iter = contentArray.listIterator();
                while (iter.hasNext()) {
                    final PdfObject element = iter.next();
                    allBytes.write(this.getContentBytesFromContentObject(element));
                }
                result = allBytes.toByteArray();
                break;
            }
            default: {
                final String msg = "Unable to handle Content of type " + contentObject.getClass();
                throw new IllegalStateException(msg);
            }
        }
        return result;
    }
    
    public String getTextFromPage(final int page) throws IOException {
        return this.getTextFromPage(page, false);
    }
    
    public String getTextFromPage(final int page, final boolean useContainerMarkup) throws IOException {
        final PdfDictionary pageDict = this.reader.getPageN(page);
        if (pageDict == null) {
            return "";
        }
        final PdfDictionary resources = pageDict.getAsDict(PdfName.RESOURCES);
        this.renderListener.reset();
        this.renderListener.setPage(page);
        final PdfContentStreamHandler handler = new PdfContentStreamHandler(this.renderListener);
        this.processContent(this.getContentBytesForPage(page), resources, handler);
        return handler.getResultantText();
    }
    
    public void processContent(final byte[] contentBytes, final PdfDictionary resources, final PdfContentStreamHandler handler) {
        handler.pushContext("div class='t-extracted-page'");
        try {
            final PdfContentParser ps = new PdfContentParser(new PRTokeniser(contentBytes));
            final ArrayList<PdfObject> operands = new ArrayList<PdfObject>();
            while (ps.parse(operands).size() > 0) {
                final PdfLiteral operator = operands.get(operands.size() - 1);
                handler.invokeOperator(operator, operands, resources);
            }
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
        handler.popContext();
    }
}
