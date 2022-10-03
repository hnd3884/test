package com.zoho.cp;

import java.sql.SQLException;

public interface ExceptionSorter
{
    boolean isExceptionFatal(final SQLException p0);
}
