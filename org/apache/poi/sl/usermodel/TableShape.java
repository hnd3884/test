package org.apache.poi.sl.usermodel;

public interface TableShape<S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>> extends Shape<S, P>, PlaceableShape<S, P>
{
    int getNumberOfColumns();
    
    int getNumberOfRows();
    
    TableCell<S, P> getCell(final int p0, final int p1);
    
    double getColumnWidth(final int p0);
    
    void setColumnWidth(final int p0, final double p1);
    
    double getRowHeight(final int p0);
    
    void setRowHeight(final int p0, final double p1);
}
