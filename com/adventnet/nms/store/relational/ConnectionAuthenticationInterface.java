package com.adventnet.nms.store.relational;

import java.sql.SQLException;
import java.sql.Connection;

public interface ConnectionAuthenticationInterface
{
    void authenticateConnection(final Connection p0) throws SQLException;
}
