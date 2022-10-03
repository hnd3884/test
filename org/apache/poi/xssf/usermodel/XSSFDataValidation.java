package org.apache.poi.xssf.usermodel;

import java.util.Iterator;
import org.apache.commons.collections4.MapUtils;
import java.util.HashMap;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDataValidationErrorStyle;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDataValidationType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDataValidationOperator;
import java.util.Map;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataValidation;
import org.apache.poi.ss.usermodel.DataValidation;

public class XSSFDataValidation implements DataValidation
{
    private static final int MAX_TEXT_LENGTH = 255;
    private CTDataValidation ctDdataValidation;
    private XSSFDataValidationConstraint validationConstraint;
    private CellRangeAddressList regions;
    static Map<Integer, STDataValidationOperator.Enum> operatorTypeMappings;
    static Map<STDataValidationOperator.Enum, Integer> operatorTypeReverseMappings;
    static Map<Integer, STDataValidationType.Enum> validationTypeMappings;
    static Map<STDataValidationType.Enum, Integer> validationTypeReverseMappings;
    static Map<Integer, STDataValidationErrorStyle.Enum> errorStyleMappings;
    static Map<STDataValidationErrorStyle.Enum, Integer> reverseErrorStyleMappings;
    
    XSSFDataValidation(final CellRangeAddressList regions, final CTDataValidation ctDataValidation) {
        this(getConstraint(ctDataValidation), regions, ctDataValidation);
    }
    
    public XSSFDataValidation(final XSSFDataValidationConstraint constraint, final CellRangeAddressList regions, final CTDataValidation ctDataValidation) {
        this.validationConstraint = constraint;
        this.ctDdataValidation = ctDataValidation;
        this.regions = regions;
    }
    
    CTDataValidation getCtDdataValidation() {
        return this.ctDdataValidation;
    }
    
    public void createErrorBox(final String title, final String text) {
        if (title != null && title.length() > 255) {
            throw new IllegalStateException("Error-title cannot be longer than 32 characters, but had: " + title);
        }
        if (text != null && text.length() > 255) {
            throw new IllegalStateException("Error-text cannot be longer than 255 characters, but had: " + text);
        }
        this.ctDdataValidation.setErrorTitle(this.encodeUtf(title));
        this.ctDdataValidation.setError(this.encodeUtf(text));
    }
    
    public void createPromptBox(final String title, final String text) {
        if (title != null && title.length() > 255) {
            throw new IllegalStateException("Error-title cannot be longer than 32 characters, but had: " + title);
        }
        if (text != null && text.length() > 255) {
            throw new IllegalStateException("Error-text cannot be longer than 255 characters, but had: " + text);
        }
        this.ctDdataValidation.setPromptTitle(this.encodeUtf(title));
        this.ctDdataValidation.setPrompt(this.encodeUtf(text));
    }
    
    private String encodeUtf(final String text) {
        if (text == null) {
            return null;
        }
        final StringBuilder builder = new StringBuilder();
        for (final char c : text.toCharArray()) {
            if (c < ' ') {
                builder.append("_x").append((c < '\u0010') ? "000" : "00").append(Integer.toHexString(c)).append("_");
            }
            else {
                builder.append(c);
            }
        }
        return builder.toString();
    }
    
    public boolean getEmptyCellAllowed() {
        return this.ctDdataValidation.getAllowBlank();
    }
    
    public String getErrorBoxText() {
        return this.ctDdataValidation.getError();
    }
    
    public String getErrorBoxTitle() {
        return this.ctDdataValidation.getErrorTitle();
    }
    
    public int getErrorStyle() {
        return XSSFDataValidation.reverseErrorStyleMappings.get(this.ctDdataValidation.getErrorStyle());
    }
    
    public String getPromptBoxText() {
        return this.ctDdataValidation.getPrompt();
    }
    
    public String getPromptBoxTitle() {
        return this.ctDdataValidation.getPromptTitle();
    }
    
    public boolean getShowErrorBox() {
        return this.ctDdataValidation.getShowErrorMessage();
    }
    
    public boolean getShowPromptBox() {
        return this.ctDdataValidation.getShowInputMessage();
    }
    
    public boolean getSuppressDropDownArrow() {
        return !this.ctDdataValidation.getShowDropDown();
    }
    
    public DataValidationConstraint getValidationConstraint() {
        return (DataValidationConstraint)this.validationConstraint;
    }
    
    public void setEmptyCellAllowed(final boolean allowed) {
        this.ctDdataValidation.setAllowBlank(allowed);
    }
    
    public void setErrorStyle(final int errorStyle) {
        this.ctDdataValidation.setErrorStyle((STDataValidationErrorStyle.Enum)XSSFDataValidation.errorStyleMappings.get(errorStyle));
    }
    
    public void setShowErrorBox(final boolean show) {
        this.ctDdataValidation.setShowErrorMessage(show);
    }
    
    public void setShowPromptBox(final boolean show) {
        this.ctDdataValidation.setShowInputMessage(show);
    }
    
    public void setSuppressDropDownArrow(final boolean suppress) {
        if (this.validationConstraint.getValidationType() == 3) {
            this.ctDdataValidation.setShowDropDown(!suppress);
        }
    }
    
    public CellRangeAddressList getRegions() {
        return this.regions;
    }
    
    public String prettyPrint() {
        final StringBuilder builder = new StringBuilder();
        for (final CellRangeAddress address : this.regions.getCellRangeAddresses()) {
            builder.append(address.formatAsString());
        }
        builder.append(" => ");
        builder.append(this.validationConstraint.prettyPrint());
        return builder.toString();
    }
    
    private static XSSFDataValidationConstraint getConstraint(final CTDataValidation ctDataValidation) {
        final String formula1 = ctDataValidation.getFormula1();
        final String formula2 = ctDataValidation.getFormula2();
        final STDataValidationOperator.Enum operator = ctDataValidation.getOperator();
        final STDataValidationType.Enum type = ctDataValidation.getType();
        final Integer validationType = XSSFDataValidation.validationTypeReverseMappings.get(type);
        final Integer operatorType = XSSFDataValidation.operatorTypeReverseMappings.get(operator);
        return new XSSFDataValidationConstraint(validationType, operatorType, formula1, formula2);
    }
    
    static {
        XSSFDataValidation.operatorTypeMappings = new HashMap<Integer, STDataValidationOperator.Enum>();
        XSSFDataValidation.operatorTypeReverseMappings = new HashMap<STDataValidationOperator.Enum, Integer>();
        XSSFDataValidation.validationTypeMappings = new HashMap<Integer, STDataValidationType.Enum>();
        XSSFDataValidation.validationTypeReverseMappings = new HashMap<STDataValidationType.Enum, Integer>();
        (XSSFDataValidation.errorStyleMappings = new HashMap<Integer, STDataValidationErrorStyle.Enum>()).put(2, STDataValidationErrorStyle.INFORMATION);
        XSSFDataValidation.errorStyleMappings.put(0, STDataValidationErrorStyle.STOP);
        XSSFDataValidation.errorStyleMappings.put(1, STDataValidationErrorStyle.WARNING);
        XSSFDataValidation.reverseErrorStyleMappings = MapUtils.invertMap((Map)XSSFDataValidation.errorStyleMappings);
        XSSFDataValidation.operatorTypeMappings.put(0, STDataValidationOperator.BETWEEN);
        XSSFDataValidation.operatorTypeMappings.put(1, STDataValidationOperator.NOT_BETWEEN);
        XSSFDataValidation.operatorTypeMappings.put(2, STDataValidationOperator.EQUAL);
        XSSFDataValidation.operatorTypeMappings.put(3, STDataValidationOperator.NOT_EQUAL);
        XSSFDataValidation.operatorTypeMappings.put(4, STDataValidationOperator.GREATER_THAN);
        XSSFDataValidation.operatorTypeMappings.put(6, STDataValidationOperator.GREATER_THAN_OR_EQUAL);
        XSSFDataValidation.operatorTypeMappings.put(5, STDataValidationOperator.LESS_THAN);
        XSSFDataValidation.operatorTypeMappings.put(7, STDataValidationOperator.LESS_THAN_OR_EQUAL);
        for (final Map.Entry<Integer, STDataValidationOperator.Enum> entry : XSSFDataValidation.operatorTypeMappings.entrySet()) {
            XSSFDataValidation.operatorTypeReverseMappings.put(entry.getValue(), entry.getKey());
        }
        XSSFDataValidation.validationTypeMappings.put(7, STDataValidationType.CUSTOM);
        XSSFDataValidation.validationTypeMappings.put(4, STDataValidationType.DATE);
        XSSFDataValidation.validationTypeMappings.put(2, STDataValidationType.DECIMAL);
        XSSFDataValidation.validationTypeMappings.put(3, STDataValidationType.LIST);
        XSSFDataValidation.validationTypeMappings.put(0, STDataValidationType.NONE);
        XSSFDataValidation.validationTypeMappings.put(6, STDataValidationType.TEXT_LENGTH);
        XSSFDataValidation.validationTypeMappings.put(5, STDataValidationType.TIME);
        XSSFDataValidation.validationTypeMappings.put(1, STDataValidationType.WHOLE);
        for (final Map.Entry<Integer, STDataValidationType.Enum> entry2 : XSSFDataValidation.validationTypeMappings.entrySet()) {
            XSSFDataValidation.validationTypeReverseMappings.put(entry2.getValue(), entry2.getKey());
        }
    }
}
