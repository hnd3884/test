package org.apache.poi.xssf.usermodel;

import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTShape;

public final class XSSFTextBox extends XSSFSimpleShape
{
    protected XSSFTextBox(final XSSFDrawing drawing, final CTShape ctShape) {
        super(drawing, ctShape);
    }
}
