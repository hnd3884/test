package com.microsoft.sqlserver.jdbc;

class ZeroFixupFilter extends IntColumnFilter
{
    @Override
    int oneValueToAnother(final int precl) {
        if (0 == precl) {
            return Integer.MAX_VALUE;
        }
        return precl;
    }
}
