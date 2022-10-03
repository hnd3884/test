package com.microsoft.sqlserver.jdbc;

abstract class IntColumnFilter extends ColumnFilter
{
    abstract int oneValueToAnother(final int p0);
    
    @Override
    final Object apply(final Object value, final JDBCType asJDBCType) throws SQLServerException {
        if (null == value) {
            return value;
        }
        switch (asJDBCType) {
            case INTEGER: {
                return this.oneValueToAnother((int)value);
            }
            case SMALLINT:
            case TINYINT: {
                return (short)this.oneValueToAnother((int)value);
            }
            case BIGINT: {
                return this.oneValueToAnother(((Long)value).intValue());
            }
            case CHAR:
            case VARCHAR:
            case LONGVARCHAR: {
                return Integer.toString(this.oneValueToAnother(Integer.parseInt((String)value)));
            }
            default: {
                DataTypes.throwConversionError("int", asJDBCType.toString());
                return value;
            }
        }
    }
}
