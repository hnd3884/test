package org.owasp.validator.css;

import org.w3c.css.sac.CSSException;
import java.util.logging.Level;
import org.w3c.css.sac.CSSParseException;
import java.util.logging.Logger;
import org.w3c.css.sac.ErrorHandler;

class CSSErrorHandler implements ErrorHandler
{
    private static final Logger LOGGER;
    
    public void error(final CSSParseException exception) throws CSSException {
        CSSErrorHandler.LOGGER.log(Level.SEVERE, "CSS Syntax Error at [ line - " + exception.getLineNumber() + " : column - " + exception.getColumnNumber() + " ]");
    }
    
    public void fatalError(final CSSParseException exception) throws CSSException {
        CSSErrorHandler.LOGGER.log(Level.SEVERE, "CSS Syntax Fatal Error at [ line - " + exception.getLineNumber() + " : column - " + exception.getColumnNumber() + " ]");
    }
    
    public void warning(final CSSParseException exception) throws CSSException {
        CSSErrorHandler.LOGGER.log(Level.INFO, "CSS Syntax Warning at [ line - " + exception.getLineNumber() + " : column - " + exception.getColumnNumber() + " ]");
    }
    
    static {
        LOGGER = Logger.getLogger(CSSErrorHandler.class.getName());
    }
}
