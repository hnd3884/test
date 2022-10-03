package org.apache.poi.xddf.usermodel.chart;

public interface XDDFCategoryDataSource extends XDDFDataSource<String>
{
    default int getColIndex() {
        return 0;
    }
    
    default boolean isLiteral() {
        return false;
    }
    
    default boolean isNumeric() {
        return false;
    }
    
    default boolean isReference() {
        return true;
    }
    
    default String getDataRangeReference() {
        return this.getFormula();
    }
}
