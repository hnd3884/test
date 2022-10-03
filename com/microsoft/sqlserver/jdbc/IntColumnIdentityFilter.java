package com.microsoft.sqlserver.jdbc;

class IntColumnIdentityFilter extends ColumnFilter
{
    private static String zeroOneToYesNo(final int i) {
        return (0 == i) ? "NO" : "YES";
    }
    
    @Override
    final Object apply(final Object value, final JDBCType asJDBCType) throws SQLServerException {
        if (null == value) {
            return value;
        }
        switch (asJDBCType) {
            case INTEGER:
            case SMALLINT: {
                assert value instanceof Number;
                return zeroOneToYesNo(((Number)value).intValue());
            }
            case CHAR:
            case VARCHAR:
            case LONGVARCHAR: {
                assert value instanceof String;
                return zeroOneToYesNo(Integer.parseInt((String)value));
            }
            default: {
                DataTypes.throwConversionError("char", asJDBCType.toString());
                return value;
            }
        }
    }
}
