package com.microsoft.sqlserver.jdbc;

import java.util.logging.Level;

abstract class UninterruptableTDSCommand extends TDSCommand
{
    private static final long serialVersionUID = -6457195977162963793L;
    
    UninterruptableTDSCommand(final String logContext) {
        super(logContext, 0, 0);
    }
    
    @Override
    final void interrupt(final String reason) throws SQLServerException {
        if (UninterruptableTDSCommand.logger.isLoggable(Level.FINEST)) {
            UninterruptableTDSCommand.logger.finest(this.toString() + " Ignoring interrupt of uninterruptable TDS command; Reason:" + reason);
        }
    }
}
