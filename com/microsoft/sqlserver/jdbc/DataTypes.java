package com.microsoft.sqlserver.jdbc;

import java.text.MessageFormat;

final class DataTypes
{
    static final int SHORT_VARTYPE_MAX_CHARS = 4000;
    static final int SHORT_VARTYPE_MAX_BYTES = 8000;
    static final int SQL_USHORTVARMAXLEN = 65535;
    static final int NTEXT_MAX_CHARS = 1073741823;
    static final int IMAGE_TEXT_MAX_BYTES = Integer.MAX_VALUE;
    static final int MAX_VARTYPE_MAX_CHARS = 1073741823;
    static final int MAX_VARTYPE_MAX_BYTES = Integer.MAX_VALUE;
    static final int MAXTYPE_LENGTH = 65535;
    static final int UNKNOWN_STREAM_LENGTH = -1;
    
    static final void throwConversionError(final String fromType, final String toType) throws SQLServerException {
        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_unsupportedConversionFromTo"));
        final Object[] msgArgs = { fromType, toType };
        SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, true);
    }
    
    static final long getCheckedLength(final SQLServerConnection con, final JDBCType jdbcType, final long length, final boolean allowUnknown) throws SQLServerException {
        long maxLength = 0L;
        switch (jdbcType) {
            case NCHAR:
            case NVARCHAR:
            case LONGNVARCHAR:
            case NCLOB: {
                maxLength = 1073741823L;
                break;
            }
            default: {
                maxLength = 2147483647L;
                break;
            }
        }
        if (length < (allowUnknown ? -1 : 0) || length > maxLength) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidLength"));
            final Object[] msgArgs = { length };
            SQLServerException.makeFromDriverError(con, null, form.format(msgArgs), null, false);
        }
        return length;
    }
}
