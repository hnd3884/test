package com.lowagie.text.pdf;

import com.lowagie.text.error_messages.MessageLocalization;
import java.io.IOException;
import java.util.ArrayList;

public class PdfContentParser
{
    public static final int COMMAND_TYPE = 200;
    private PRTokeniser tokeniser;
    
    public PdfContentParser(final PRTokeniser tokeniser) {
        this.tokeniser = tokeniser;
    }
    
    public ArrayList parse(ArrayList ls) throws IOException {
        if (ls == null) {
            ls = new ArrayList();
        }
        else {
            ls.clear();
        }
        PdfObject ob = null;
        while ((ob = this.readPRObject()) != null) {
            ls.add(ob);
            if (ob.type() == 200) {
                break;
            }
        }
        return ls;
    }
    
    public PRTokeniser getTokeniser() {
        return this.tokeniser;
    }
    
    public void setTokeniser(final PRTokeniser tokeniser) {
        this.tokeniser = tokeniser;
    }
    
    public PdfDictionary readDictionary() throws IOException {
        final PdfDictionary dic = new PdfDictionary();
        while (this.nextValidToken()) {
            if (this.tokeniser.getTokenType() == 8) {
                return dic;
            }
            if (this.tokeniser.getTokenType() != 3) {
                throw new IOException(MessageLocalization.getComposedMessage("dictionary.key.is.not.a.name"));
            }
            final PdfName name = new PdfName(this.tokeniser.getStringValue(), false);
            final PdfObject obj = this.readPRObject();
            final int type = obj.type();
            if (-type == 8) {
                throw new IOException(MessageLocalization.getComposedMessage("unexpected.gt.gt"));
            }
            if (-type == 6) {
                throw new IOException(MessageLocalization.getComposedMessage("unexpected.close.bracket"));
            }
            dic.put(name, obj);
        }
        throw new IOException(MessageLocalization.getComposedMessage("unexpected.end.of.file"));
    }
    
    public PdfArray readArray() throws IOException {
        final PdfArray array = new PdfArray();
        while (true) {
            final PdfObject obj = this.readPRObject();
            final int type = obj.type();
            if (-type == 6) {
                return array;
            }
            if (-type == 8) {
                throw new IOException(MessageLocalization.getComposedMessage("unexpected.gt.gt"));
            }
            array.add(obj);
        }
    }
    
    public PdfObject readPRObject() throws IOException {
        if (!this.nextValidToken()) {
            return null;
        }
        final int type = this.tokeniser.getTokenType();
        switch (type) {
            case 7: {
                final PdfDictionary dic = this.readDictionary();
                return dic;
            }
            case 5: {
                return this.readArray();
            }
            case 2: {
                final PdfString str = new PdfString(this.tokeniser.getStringValue(), null).setHexWriting(this.tokeniser.isHexString());
                return str;
            }
            case 3: {
                return new PdfName(this.tokeniser.getStringValue(), false);
            }
            case 1: {
                return new PdfNumber(this.tokeniser.getStringValue());
            }
            case 10: {
                return new PdfLiteral(200, this.tokeniser.getStringValue());
            }
            default: {
                return new PdfLiteral(-type, this.tokeniser.getStringValue());
            }
        }
    }
    
    public boolean nextValidToken() throws IOException {
        while (this.tokeniser.nextToken()) {
            if (this.tokeniser.getTokenType() == 4) {
                continue;
            }
            return true;
        }
        return false;
    }
}
