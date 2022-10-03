package com.adventnet.client.components.table.pdf;

import com.lowagie.text.Phrase;
import java.util.Map;

public class TablePDFRendererWithImageCheck extends TablePDFRenderer
{
    @Override
    protected void printValue(final Map props, final Phrase ph, final String tcClass) throws Exception {
        this.printAfterImageCheck(props, ph, tcClass);
    }
}
