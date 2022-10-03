package com.lowagie.text.pdf;

import java.util.HashMap;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.DocumentException;
import java.io.OutputStream;

class PdfCopyFormsImp extends PdfCopyFieldsImp
{
    PdfCopyFormsImp(final OutputStream os) throws DocumentException {
        super(os);
    }
    
    public void copyDocumentFields(PdfReader reader) throws DocumentException {
        if (!reader.isOpenedWithFullPermissions()) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("pdfreader.not.opened.with.owner.password"));
        }
        if (this.readers2intrefs.containsKey(reader)) {
            reader = new PdfReader(reader);
        }
        else {
            if (reader.isTampered()) {
                throw new DocumentException(MessageLocalization.getComposedMessage("the.document.was.reused"));
            }
            reader.consolidateNamedDestinations();
            reader.setTampered(true);
        }
        reader.shuffleSubsetNames();
        this.readers2intrefs.put(reader, new IntHashtable());
        this.fields.add(reader.getAcroFields());
        this.updateCalculationOrder(reader);
    }
    
    @Override
    void mergeFields() {
        for (int k = 0; k < this.fields.size(); ++k) {
            final HashMap fd = this.fields.get(k).getFields();
            this.mergeWithMaster(fd);
        }
    }
}
