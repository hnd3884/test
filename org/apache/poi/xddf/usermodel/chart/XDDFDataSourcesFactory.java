package org.apache.poi.xddf.usermodel.chart;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrData;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumData;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;

public class XDDFDataSourcesFactory
{
    private XDDFDataSourcesFactory() {
    }
    
    public static XDDFCategoryDataSource fromDataSource(final CTAxDataSource categoryDS) {
        if (categoryDS.getStrRef() == null) {
            return new XDDFCategoryDataSource() {
                private CTNumData category = (CTNumData)categoryDS.getNumRef().getNumCache().copy();
                
                @Override
                public boolean isCellRange() {
                    return true;
                }
                
                @Override
                public boolean isNumeric() {
                    return true;
                }
                
                @Override
                public String getFormula() {
                    return categoryDS.getNumRef().getF();
                }
                
                @Override
                public int getPointCount() {
                    return (int)this.category.getPtCount().getVal();
                }
                
                @Override
                public String getPointAt(final int index) {
                    return this.category.getPtArray(index).getV();
                }
            };
        }
        return new XDDFCategoryDataSource() {
            private CTStrData category = (CTStrData)categoryDS.getStrRef().getStrCache().copy();
            
            @Override
            public boolean isCellRange() {
                return true;
            }
            
            @Override
            public String getFormula() {
                return categoryDS.getStrRef().getF();
            }
            
            @Override
            public int getPointCount() {
                return (int)this.category.getPtCount().getVal();
            }
            
            @Override
            public String getPointAt(final int index) {
                return this.category.getPtArray(index).getV();
            }
        };
    }
    
    public static XDDFNumericalDataSource<Double> fromDataSource(final CTNumDataSource valuesDS) {
        return new XDDFNumericalDataSource<Double>() {
            private CTNumData values = (CTNumData)valuesDS.getNumRef().getNumCache().copy();
            private String formatCode = this.values.isSetFormatCode() ? this.values.getFormatCode() : null;
            
            @Override
            public String getFormula() {
                return valuesDS.getNumRef().getF();
            }
            
            @Override
            public String getFormatCode() {
                return this.formatCode;
            }
            
            @Override
            public void setFormatCode(final String formatCode) {
                this.formatCode = formatCode;
            }
            
            @Override
            public boolean isCellRange() {
                return true;
            }
            
            @Override
            public boolean isNumeric() {
                return true;
            }
            
            @Override
            public boolean isReference() {
                return true;
            }
            
            @Override
            public int getPointCount() {
                return (int)this.values.getPtCount().getVal();
            }
            
            @Override
            public Double getPointAt(final int index) {
                return Double.valueOf(this.values.getPtArray(index).getV());
            }
            
            @Override
            public String getDataRangeReference() {
                return valuesDS.getNumRef().getF();
            }
            
            @Override
            public int getColIndex() {
                return 0;
            }
        };
    }
    
    public static <T extends Number> XDDFNumericalDataSource<T> fromArray(final T[] elements) {
        return new LiteralNumericalArrayDataSource<T>(elements);
    }
    
    public static XDDFCategoryDataSource fromArray(final String[] elements) {
        return new LiteralStringArrayDataSource(elements);
    }
    
    public static <T extends Number> XDDFNumericalDataSource<T> fromArray(final T[] elements, final String dataRange) {
        return new NumericalArrayDataSource<T>(elements, dataRange);
    }
    
    public static XDDFCategoryDataSource fromArray(final String[] elements, final String dataRange) {
        return new StringArrayDataSource(elements, dataRange);
    }
    
    public static <T extends Number> XDDFNumericalDataSource<T> fromArray(final T[] elements, final String dataRange, final int col) {
        return new NumericalArrayDataSource<T>(elements, dataRange, col);
    }
    
    public static XDDFCategoryDataSource fromArray(final String[] elements, final String dataRange, final int col) {
        return new StringArrayDataSource(elements, dataRange, col);
    }
    
    public static XDDFNumericalDataSource<Double> fromNumericCellRange(final XSSFSheet sheet, final CellRangeAddress cellRangeAddress) {
        return new NumericalCellRangeDataSource(sheet, cellRangeAddress);
    }
    
    public static XDDFCategoryDataSource fromStringCellRange(final XSSFSheet sheet, final CellRangeAddress cellRangeAddress) {
        return new StringCellRangeDataSource(sheet, cellRangeAddress);
    }
    
    private abstract static class AbstractArrayDataSource<T> implements XDDFDataSource<T>
    {
        private final T[] elements;
        private final String dataRange;
        private int col;
        
        public AbstractArrayDataSource(final T[] elements, final String dataRange) {
            this.col = 0;
            this.elements = elements.clone();
            this.dataRange = dataRange;
        }
        
        public AbstractArrayDataSource(final T[] elements, final String dataRange, final int col) {
            this.col = 0;
            this.elements = elements.clone();
            this.dataRange = dataRange;
            this.col = col;
        }
        
        @Override
        public int getPointCount() {
            return this.elements.length;
        }
        
        @Override
        public T getPointAt(final int index) {
            return this.elements[index];
        }
        
        @Override
        public boolean isCellRange() {
            return false;
        }
        
        @Override
        public boolean isReference() {
            return this.dataRange != null;
        }
        
        @Override
        public boolean isNumeric() {
            final Class<?> arrayComponentType = this.elements.getClass().getComponentType();
            return Number.class.isAssignableFrom(arrayComponentType);
        }
        
        @Override
        public String getDataRangeReference() {
            if (this.dataRange == null) {
                throw new UnsupportedOperationException("Literal data source can not be expressed by reference.");
            }
            return this.dataRange;
        }
        
        @Override
        public int getColIndex() {
            return this.col;
        }
    }
    
    private static class NumericalArrayDataSource<T extends Number> extends AbstractArrayDataSource<T> implements XDDFNumericalDataSource<T>
    {
        private String formatCode;
        
        public NumericalArrayDataSource(final T[] elements, final String dataRange) {
            super(elements, dataRange);
        }
        
        public NumericalArrayDataSource(final T[] elements, final String dataRange, final int col) {
            super(elements, dataRange, col);
        }
        
        @Override
        public String getFormula() {
            return this.getDataRangeReference();
        }
        
        @Override
        public String getFormatCode() {
            return this.formatCode;
        }
        
        @Override
        public void setFormatCode(final String formatCode) {
            this.formatCode = formatCode;
        }
    }
    
    private static class StringArrayDataSource extends AbstractArrayDataSource<String> implements XDDFCategoryDataSource
    {
        public StringArrayDataSource(final String[] elements, final String dataRange) {
            super(elements, dataRange);
        }
        
        public StringArrayDataSource(final String[] elements, final String dataRange, final int col) {
            super(elements, dataRange, col);
        }
        
        @Override
        public String getFormula() {
            return this.getDataRangeReference();
        }
    }
    
    private static class LiteralNumericalArrayDataSource<T extends Number> extends NumericalArrayDataSource<T>
    {
        public LiteralNumericalArrayDataSource(final T[] elements) {
            super(elements, null, 0);
        }
        
        @Override
        public boolean isLiteral() {
            return true;
        }
    }
    
    private static class LiteralStringArrayDataSource extends StringArrayDataSource
    {
        public LiteralStringArrayDataSource(final String[] elements) {
            super(elements, null, 0);
        }
        
        @Override
        public boolean isLiteral() {
            return true;
        }
    }
    
    private abstract static class AbstractCellRangeDataSource<T> implements XDDFDataSource<T>
    {
        private final XSSFSheet sheet;
        private final CellRangeAddress cellRangeAddress;
        private final int numOfCells;
        private XSSFFormulaEvaluator evaluator;
        
        protected AbstractCellRangeDataSource(final XSSFSheet sheet, final CellRangeAddress cellRangeAddress) {
            this.sheet = sheet;
            this.cellRangeAddress = cellRangeAddress.copy();
            this.numOfCells = this.cellRangeAddress.getNumberOfCells();
            this.evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
        }
        
        @Override
        public int getPointCount() {
            return this.numOfCells;
        }
        
        @Override
        public boolean isCellRange() {
            return true;
        }
        
        @Override
        public boolean isReference() {
            return true;
        }
        
        @Override
        public int getColIndex() {
            return this.cellRangeAddress.getFirstColumn();
        }
        
        @Override
        public String getDataRangeReference() {
            return this.cellRangeAddress.formatAsString(this.sheet.getSheetName(), true);
        }
        
        protected CellValue getCellValueAt(final int index) {
            if (index < 0 || index >= this.numOfCells) {
                throw new IndexOutOfBoundsException("Index must be between 0 and " + (this.numOfCells - 1) + " (inclusive), given: " + index);
            }
            final int firstRow = this.cellRangeAddress.getFirstRow();
            final int firstCol = this.cellRangeAddress.getFirstColumn();
            final int lastCol = this.cellRangeAddress.getLastColumn();
            final int width = lastCol - firstCol + 1;
            final int rowIndex = firstRow + index / width;
            final int cellIndex = firstCol + index % width;
            final XSSFRow row = this.sheet.getRow(rowIndex);
            return (row == null) ? null : this.evaluator.evaluate((Cell)row.getCell(cellIndex));
        }
    }
    
    private static class NumericalCellRangeDataSource extends AbstractCellRangeDataSource<Double> implements XDDFNumericalDataSource<Double>
    {
        private String formatCode;
        
        protected NumericalCellRangeDataSource(final XSSFSheet sheet, final CellRangeAddress cellRangeAddress) {
            super(sheet, cellRangeAddress);
        }
        
        @Override
        public String getFormula() {
            return this.getDataRangeReference();
        }
        
        @Override
        public String getFormatCode() {
            return this.formatCode;
        }
        
        @Override
        public void setFormatCode(final String formatCode) {
            this.formatCode = formatCode;
        }
        
        @Override
        public Double getPointAt(final int index) {
            final CellValue cellValue = this.getCellValueAt(index);
            if (cellValue != null && cellValue.getCellType() == CellType.NUMERIC) {
                return cellValue.getNumberValue();
            }
            return null;
        }
        
        @Override
        public boolean isNumeric() {
            return true;
        }
    }
    
    private static class StringCellRangeDataSource extends AbstractCellRangeDataSource<String> implements XDDFCategoryDataSource
    {
        protected StringCellRangeDataSource(final XSSFSheet sheet, final CellRangeAddress cellRangeAddress) {
            super(sheet, cellRangeAddress);
        }
        
        @Override
        public String getFormula() {
            return this.getDataRangeReference();
        }
        
        @Override
        public String getPointAt(final int index) {
            final CellValue cellValue = this.getCellValueAt(index);
            if (cellValue != null && cellValue.getCellType() == CellType.STRING) {
                return cellValue.getStringValue();
            }
            return null;
        }
        
        @Override
        public boolean isNumeric() {
            return false;
        }
    }
}
