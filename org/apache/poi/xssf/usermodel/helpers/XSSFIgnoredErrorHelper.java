package org.apache.poi.xssf.usermodel.helpers;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.List;
import java.util.Collections;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTIgnoredError;
import org.apache.poi.ss.usermodel.IgnoredErrorType;

public class XSSFIgnoredErrorHelper
{
    public static boolean isSet(final IgnoredErrorType errorType, final CTIgnoredError error) {
        switch (errorType) {
            case CALCULATED_COLUMN: {
                return error.isSetCalculatedColumn();
            }
            case EMPTY_CELL_REFERENCE: {
                return error.isSetEmptyCellReference();
            }
            case EVALUATION_ERROR: {
                return error.isSetEvalError();
            }
            case FORMULA: {
                return error.isSetFormula();
            }
            case FORMULA_RANGE: {
                return error.isSetFormulaRange();
            }
            case LIST_DATA_VALIDATION: {
                return error.isSetListDataValidation();
            }
            case NUMBER_STORED_AS_TEXT: {
                return error.isSetNumberStoredAsText();
            }
            case TWO_DIGIT_TEXT_YEAR: {
                return error.isSetTwoDigitTextYear();
            }
            case UNLOCKED_FORMULA: {
                return error.isSetUnlockedFormula();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    public static void set(final IgnoredErrorType errorType, final CTIgnoredError error) {
        switch (errorType) {
            case CALCULATED_COLUMN: {
                error.setCalculatedColumn(true);
                break;
            }
            case EMPTY_CELL_REFERENCE: {
                error.setEmptyCellReference(true);
                break;
            }
            case EVALUATION_ERROR: {
                error.setEvalError(true);
                break;
            }
            case FORMULA: {
                error.setFormula(true);
                break;
            }
            case FORMULA_RANGE: {
                error.setFormulaRange(true);
                break;
            }
            case LIST_DATA_VALIDATION: {
                error.setListDataValidation(true);
                break;
            }
            case NUMBER_STORED_AS_TEXT: {
                error.setNumberStoredAsText(true);
                break;
            }
            case TWO_DIGIT_TEXT_YEAR: {
                error.setTwoDigitTextYear(true);
                break;
            }
            case UNLOCKED_FORMULA: {
                error.setUnlockedFormula(true);
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    public static void addIgnoredErrors(final CTIgnoredError err, final String ref, final IgnoredErrorType... ignoredErrorTypes) {
        err.setSqref((List)Collections.singletonList(ref));
        for (final IgnoredErrorType errType : ignoredErrorTypes) {
            set(errType, err);
        }
    }
    
    public static Set<IgnoredErrorType> getErrorTypes(final CTIgnoredError err) {
        final Set<IgnoredErrorType> result = new LinkedHashSet<IgnoredErrorType>();
        for (final IgnoredErrorType errType : IgnoredErrorType.values()) {
            if (isSet(errType, err)) {
                result.add(errType);
            }
        }
        return result;
    }
}
