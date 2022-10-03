package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.util.CellRangeAddress;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDataValidationErrorStyle;
import java.util.List;
import java.util.ArrayList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDataValidationOperator;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDataValidationType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataValidation;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;

public class XSSFDataValidationHelper implements DataValidationHelper
{
    public XSSFDataValidationHelper(final XSSFSheet xssfSheet) {
    }
    
    public DataValidationConstraint createDateConstraint(final int operatorType, final String formula1, final String formula2, final String dateFormat) {
        return (DataValidationConstraint)new XSSFDataValidationConstraint(4, operatorType, formula1, formula2);
    }
    
    public DataValidationConstraint createDecimalConstraint(final int operatorType, final String formula1, final String formula2) {
        return (DataValidationConstraint)new XSSFDataValidationConstraint(2, operatorType, formula1, formula2);
    }
    
    public DataValidationConstraint createExplicitListConstraint(final String[] listOfValues) {
        return (DataValidationConstraint)new XSSFDataValidationConstraint(listOfValues);
    }
    
    public DataValidationConstraint createFormulaListConstraint(final String listFormula) {
        return (DataValidationConstraint)new XSSFDataValidationConstraint(3, listFormula);
    }
    
    public DataValidationConstraint createNumericConstraint(final int validationType, final int operatorType, final String formula1, final String formula2) {
        if (validationType == 1) {
            return this.createIntegerConstraint(operatorType, formula1, formula2);
        }
        if (validationType == 2) {
            return this.createDecimalConstraint(operatorType, formula1, formula2);
        }
        if (validationType == 6) {
            return this.createTextLengthConstraint(operatorType, formula1, formula2);
        }
        return null;
    }
    
    public DataValidationConstraint createIntegerConstraint(final int operatorType, final String formula1, final String formula2) {
        return (DataValidationConstraint)new XSSFDataValidationConstraint(1, operatorType, formula1, formula2);
    }
    
    public DataValidationConstraint createTextLengthConstraint(final int operatorType, final String formula1, final String formula2) {
        return (DataValidationConstraint)new XSSFDataValidationConstraint(6, operatorType, formula1, formula2);
    }
    
    public DataValidationConstraint createTimeConstraint(final int operatorType, final String formula1, final String formula2) {
        return (DataValidationConstraint)new XSSFDataValidationConstraint(5, operatorType, formula1, formula2);
    }
    
    public DataValidationConstraint createCustomConstraint(final String formula) {
        return (DataValidationConstraint)new XSSFDataValidationConstraint(7, formula);
    }
    
    public DataValidation createValidation(final DataValidationConstraint constraint, final CellRangeAddressList cellRangeAddressList) {
        final XSSFDataValidationConstraint dataValidationConstraint = (XSSFDataValidationConstraint)constraint;
        final CTDataValidation newDataValidation = CTDataValidation.Factory.newInstance();
        final int validationType = constraint.getValidationType();
        switch (validationType) {
            case 3: {
                newDataValidation.setType(STDataValidationType.LIST);
                newDataValidation.setFormula1(constraint.getFormula1());
                break;
            }
            case 0: {
                newDataValidation.setType(STDataValidationType.NONE);
                break;
            }
            case 6: {
                newDataValidation.setType(STDataValidationType.TEXT_LENGTH);
                break;
            }
            case 4: {
                newDataValidation.setType(STDataValidationType.DATE);
                break;
            }
            case 1: {
                newDataValidation.setType(STDataValidationType.WHOLE);
                break;
            }
            case 2: {
                newDataValidation.setType(STDataValidationType.DECIMAL);
                break;
            }
            case 5: {
                newDataValidation.setType(STDataValidationType.TIME);
                break;
            }
            case 7: {
                newDataValidation.setType(STDataValidationType.CUSTOM);
                break;
            }
            default: {
                newDataValidation.setType(STDataValidationType.NONE);
                break;
            }
        }
        if (validationType != 0 && validationType != 3) {
            final STDataValidationOperator.Enum op = XSSFDataValidation.operatorTypeMappings.get(constraint.getOperator());
            if (op != null) {
                newDataValidation.setOperator(op);
            }
            if (constraint.getFormula1() != null) {
                newDataValidation.setFormula1(constraint.getFormula1());
            }
            if (constraint.getFormula2() != null) {
                newDataValidation.setFormula2(constraint.getFormula2());
            }
        }
        final CellRangeAddress[] cellRangeAddresses = cellRangeAddressList.getCellRangeAddresses();
        final List<String> sqref = new ArrayList<String>();
        for (int i = 0; i < cellRangeAddresses.length; ++i) {
            final CellRangeAddress cellRangeAddress = cellRangeAddresses[i];
            sqref.add(cellRangeAddress.formatAsString());
        }
        newDataValidation.setSqref((List)sqref);
        newDataValidation.setAllowBlank(true);
        newDataValidation.setErrorStyle(STDataValidationErrorStyle.STOP);
        return (DataValidation)new XSSFDataValidation(dataValidationConstraint, cellRangeAddressList, newDataValidation);
    }
}
