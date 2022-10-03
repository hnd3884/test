package org.apache.poi.sl.usermodel;

public interface MasterSheet<S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>> extends Sheet<S, P>
{
    SimpleShape<S, P> getPlaceholder(final Placeholder p0);
}
