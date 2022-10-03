package org.apache.commons.math3.exception;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.util.ExceptionContextProvider;

public class MathParseException extends MathIllegalStateException implements ExceptionContextProvider
{
    private static final long serialVersionUID = -6024911025449780478L;
    
    public MathParseException(final String wrong, final int position, final Class<?> type) {
        this.getContext().addMessage(LocalizedFormats.CANNOT_PARSE_AS_TYPE, wrong, position, type.getName());
    }
    
    public MathParseException(final String wrong, final int position) {
        this.getContext().addMessage(LocalizedFormats.CANNOT_PARSE, wrong, position);
    }
}
