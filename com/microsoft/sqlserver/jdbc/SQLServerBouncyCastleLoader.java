package com.microsoft.sqlserver.jdbc;

import java.security.Provider;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

class SQLServerBouncyCastleLoader
{
    static void loadBouncyCastle() {
        Security.addProvider((Provider)new BouncyCastleProvider());
    }
}
