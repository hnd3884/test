package org.apache.poi.sl.usermodel;

public interface Background<S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>> extends Shape<S, P>
{
    FillStyle getFillStyle();
}
