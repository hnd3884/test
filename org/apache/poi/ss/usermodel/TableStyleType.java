package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.CellRangeAddressBase;

public enum TableStyleType
{
    wholeTable {
        @Override
        public CellRangeAddressBase getRange(final Table table, final CellReference cell) {
            return new CellRangeAddress(table.getStartRowIndex(), table.getEndRowIndex(), table.getStartColIndex(), table.getEndColIndex());
        }
    }, 
    pageFieldLabels, 
    pageFieldValues, 
    firstColumnStripe {
        @Override
        public CellRangeAddressBase getRange(final Table table, final CellReference cell) {
            final TableStyleInfo info = table.getStyle();
            if (!info.isShowColumnStripes()) {
                return null;
            }
            final DifferentialStyleProvider c1Style = info.getStyle().getStyle(TableStyleType$2.firstColumnStripe);
            final DifferentialStyleProvider c2Style = info.getStyle().getStyle(TableStyleType$2.secondColumnStripe);
            for (int c1Stripe = (c1Style == null) ? 1 : Math.max(1, c1Style.getStripeSize()), c2Stripe = (c2Style == null) ? 1 : Math.max(1, c2Style.getStripeSize()), firstStart = table.getStartColIndex(), secondStart = firstStart + c1Stripe, c = cell.getCol(); firstStart <= c; firstStart = secondStart + c2Stripe, secondStart = firstStart + c1Stripe) {
                if (c <= secondStart - 1) {
                    return new CellRangeAddress(table.getStartRowIndex(), table.getEndRowIndex(), firstStart, secondStart - 1);
                }
            }
            return null;
        }
    }, 
    secondColumnStripe {
        @Override
        public CellRangeAddressBase getRange(final Table table, final CellReference cell) {
            final TableStyleInfo info = table.getStyle();
            if (!info.isShowColumnStripes()) {
                return null;
            }
            final DifferentialStyleProvider c1Style = info.getStyle().getStyle(TableStyleType$3.firstColumnStripe);
            final DifferentialStyleProvider c2Style = info.getStyle().getStyle(TableStyleType$3.secondColumnStripe);
            for (int c1Stripe = (c1Style == null) ? 1 : Math.max(1, c1Style.getStripeSize()), c2Stripe = (c2Style == null) ? 1 : Math.max(1, c2Style.getStripeSize()), firstStart = table.getStartColIndex(), secondStart = firstStart + c1Stripe, c = cell.getCol(); firstStart <= c; firstStart = secondStart + c2Stripe, secondStart = firstStart + c1Stripe) {
                if (c >= secondStart && c <= secondStart + c2Stripe - 1) {
                    return new CellRangeAddress(table.getStartRowIndex(), table.getEndRowIndex(), secondStart, secondStart + c2Stripe - 1);
                }
            }
            return null;
        }
    }, 
    firstRowStripe {
        @Override
        public CellRangeAddressBase getRange(final Table table, final CellReference cell) {
            final TableStyleInfo info = table.getStyle();
            if (!info.isShowRowStripes()) {
                return null;
            }
            final DifferentialStyleProvider c1Style = info.getStyle().getStyle(TableStyleType$4.firstRowStripe);
            final DifferentialStyleProvider c2Style = info.getStyle().getStyle(TableStyleType$4.secondRowStripe);
            for (int c1Stripe = (c1Style == null) ? 1 : Math.max(1, c1Style.getStripeSize()), c2Stripe = (c2Style == null) ? 1 : Math.max(1, c2Style.getStripeSize()), firstStart = table.getStartRowIndex() + table.getHeaderRowCount(), secondStart = firstStart + c1Stripe, c = cell.getRow(); firstStart <= c; firstStart = secondStart + c2Stripe, secondStart = firstStart + c1Stripe) {
                if (c <= secondStart - 1) {
                    return new CellRangeAddress(firstStart, secondStart - 1, table.getStartColIndex(), table.getEndColIndex());
                }
            }
            return null;
        }
    }, 
    secondRowStripe {
        @Override
        public CellRangeAddressBase getRange(final Table table, final CellReference cell) {
            final TableStyleInfo info = table.getStyle();
            if (!info.isShowRowStripes()) {
                return null;
            }
            final DifferentialStyleProvider c1Style = info.getStyle().getStyle(TableStyleType$5.firstRowStripe);
            final DifferentialStyleProvider c2Style = info.getStyle().getStyle(TableStyleType$5.secondRowStripe);
            for (int c1Stripe = (c1Style == null) ? 1 : Math.max(1, c1Style.getStripeSize()), c2Stripe = (c2Style == null) ? 1 : Math.max(1, c2Style.getStripeSize()), firstStart = table.getStartRowIndex() + table.getHeaderRowCount(), secondStart = firstStart + c1Stripe, c = cell.getRow(); firstStart <= c; firstStart = secondStart + c2Stripe, secondStart = firstStart + c1Stripe) {
                if (c >= secondStart && c <= secondStart + c2Stripe - 1) {
                    return new CellRangeAddress(secondStart, secondStart + c2Stripe - 1, table.getStartColIndex(), table.getEndColIndex());
                }
            }
            return null;
        }
    }, 
    lastColumn {
        @Override
        public CellRangeAddressBase getRange(final Table table, final CellReference cell) {
            if (!table.getStyle().isShowLastColumn()) {
                return null;
            }
            return new CellRangeAddress(table.getStartRowIndex(), table.getEndRowIndex(), table.getEndColIndex(), table.getEndColIndex());
        }
    }, 
    firstColumn {
        @Override
        public CellRangeAddressBase getRange(final Table table, final CellReference cell) {
            if (!table.getStyle().isShowFirstColumn()) {
                return null;
            }
            return new CellRangeAddress(table.getStartRowIndex(), table.getEndRowIndex(), table.getStartColIndex(), table.getStartColIndex());
        }
    }, 
    headerRow {
        @Override
        public CellRangeAddressBase getRange(final Table table, final CellReference cell) {
            if (table.getHeaderRowCount() < 1) {
                return null;
            }
            return new CellRangeAddress(table.getStartRowIndex(), table.getStartRowIndex() + table.getHeaderRowCount() - 1, table.getStartColIndex(), table.getEndColIndex());
        }
    }, 
    totalRow {
        @Override
        public CellRangeAddressBase getRange(final Table table, final CellReference cell) {
            if (table.getTotalsRowCount() < 1) {
                return null;
            }
            return new CellRangeAddress(table.getEndRowIndex() - table.getTotalsRowCount() + 1, table.getEndRowIndex(), table.getStartColIndex(), table.getEndColIndex());
        }
    }, 
    firstHeaderCell {
        @Override
        public CellRangeAddressBase getRange(final Table table, final CellReference cell) {
            if (table.getHeaderRowCount() < 1) {
                return null;
            }
            return new CellRangeAddress(table.getStartRowIndex(), table.getStartRowIndex(), table.getStartColIndex(), table.getStartColIndex());
        }
    }, 
    lastHeaderCell {
        @Override
        public CellRangeAddressBase getRange(final Table table, final CellReference cell) {
            if (table.getHeaderRowCount() < 1) {
                return null;
            }
            return new CellRangeAddress(table.getStartRowIndex(), table.getStartRowIndex(), table.getEndColIndex(), table.getEndColIndex());
        }
    }, 
    firstTotalCell {
        @Override
        public CellRangeAddressBase getRange(final Table table, final CellReference cell) {
            if (table.getTotalsRowCount() < 1) {
                return null;
            }
            return new CellRangeAddress(table.getEndRowIndex() - table.getTotalsRowCount() + 1, table.getEndRowIndex(), table.getStartColIndex(), table.getStartColIndex());
        }
    }, 
    lastTotalCell {
        @Override
        public CellRangeAddressBase getRange(final Table table, final CellReference cell) {
            if (table.getTotalsRowCount() < 1) {
                return null;
            }
            return new CellRangeAddress(table.getEndRowIndex() - table.getTotalsRowCount() + 1, table.getEndRowIndex(), table.getEndColIndex(), table.getEndColIndex());
        }
    }, 
    firstSubtotalColumn, 
    secondSubtotalColumn, 
    thirdSubtotalColumn, 
    blankRow, 
    firstSubtotalRow, 
    secondSubtotalRow, 
    thirdSubtotalRow, 
    firstColumnSubheading, 
    secondColumnSubheading, 
    thirdColumnSubheading, 
    firstRowSubheading, 
    secondRowSubheading, 
    thirdRowSubheading;
    
    public CellRangeAddressBase appliesTo(final Table table, final Cell cell) {
        if (cell == null) {
            return null;
        }
        return this.appliesTo(table, new CellReference(cell.getSheet().getSheetName(), cell.getRowIndex(), cell.getColumnIndex(), true, true));
    }
    
    public CellRangeAddressBase appliesTo(final Table table, final CellReference cell) {
        if (table == null || cell == null) {
            return null;
        }
        if (!cell.getSheetName().equals(table.getSheetName())) {
            return null;
        }
        if (!table.contains(cell)) {
            return null;
        }
        final CellRangeAddressBase range = this.getRange(table, cell);
        if (range != null && range.isInRange(cell.getRow(), cell.getCol())) {
            return range;
        }
        return null;
    }
    
    public final CellRangeAddressBase getRange(final Table table, final Cell cell) {
        if (cell == null) {
            return null;
        }
        return this.getRange(table, new CellReference(cell.getSheet().getSheetName(), cell.getRowIndex(), cell.getColumnIndex(), true, true));
    }
    
    public CellRangeAddressBase getRange(final Table table, final CellReference cell) {
        return null;
    }
}
