package org.apache.poi.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CommonsLogger implements POILogger
{
    private static final LogFactory _creator;
    private Log log;
    
    @Override
    public void initialize(final String cat) {
        this.log = CommonsLogger._creator.getInstance(cat);
    }
    
    @Override
    public void _log(final int level, final Object obj1) {
        switch (level) {
            case 9: {
                if (this.log.isFatalEnabled()) {
                    this.log.fatal(obj1);
                    break;
                }
                break;
            }
            case 7: {
                if (this.log.isErrorEnabled()) {
                    this.log.error(obj1);
                    break;
                }
                break;
            }
            case 5: {
                if (this.log.isWarnEnabled()) {
                    this.log.warn(obj1);
                    break;
                }
                break;
            }
            case 3: {
                if (this.log.isInfoEnabled()) {
                    this.log.info(obj1);
                    break;
                }
                break;
            }
            case 1: {
                if (this.log.isDebugEnabled()) {
                    this.log.debug(obj1);
                    break;
                }
                break;
            }
            default: {
                if (this.log.isTraceEnabled()) {
                    this.log.trace(obj1);
                    break;
                }
                break;
            }
        }
    }
    
    @Override
    public void _log(final int level, final Object obj1, final Throwable exception) {
        switch (level) {
            case 9: {
                if (!this.log.isFatalEnabled()) {
                    break;
                }
                if (obj1 != null) {
                    this.log.fatal(obj1, exception);
                    break;
                }
                this.log.fatal((Object)exception);
                break;
            }
            case 7: {
                if (!this.log.isErrorEnabled()) {
                    break;
                }
                if (obj1 != null) {
                    this.log.error(obj1, exception);
                    break;
                }
                this.log.error((Object)exception);
                break;
            }
            case 5: {
                if (!this.log.isWarnEnabled()) {
                    break;
                }
                if (obj1 != null) {
                    this.log.warn(obj1, exception);
                    break;
                }
                this.log.warn((Object)exception);
                break;
            }
            case 3: {
                if (!this.log.isInfoEnabled()) {
                    break;
                }
                if (obj1 != null) {
                    this.log.info(obj1, exception);
                    break;
                }
                this.log.info((Object)exception);
                break;
            }
            case 1: {
                if (!this.log.isDebugEnabled()) {
                    break;
                }
                if (obj1 != null) {
                    this.log.debug(obj1, exception);
                    break;
                }
                this.log.debug((Object)exception);
                break;
            }
            default: {
                if (!this.log.isTraceEnabled()) {
                    break;
                }
                if (obj1 != null) {
                    this.log.trace(obj1, exception);
                    break;
                }
                this.log.trace((Object)exception);
                break;
            }
        }
    }
    
    @Override
    public boolean check(final int level) {
        switch (level) {
            case 9: {
                return this.log.isFatalEnabled();
            }
            case 7: {
                return this.log.isErrorEnabled();
            }
            case 5: {
                return this.log.isWarnEnabled();
            }
            case 3: {
                return this.log.isInfoEnabled();
            }
            case 1: {
                return this.log.isDebugEnabled();
            }
            default: {
                return false;
            }
        }
    }
    
    static {
        _creator = LogFactory.getFactory();
    }
}
