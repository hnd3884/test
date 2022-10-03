package com.adventnet.db.migration;

import com.adventnet.db.migration.util.DBMigrationUtil;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.File;
import com.adventnet.mfw.ConsoleOut;

public class DBMigration implements DBMigrationInterface
{
    public void migrateDB(final String dsName, final String dbPropsPath) throws Exception {
        try {
            String dbSpecificFilePath = null;
            if (dsName == null || dbPropsPath == null) {
                ConsoleOut.println("Unknown options specified");
            }
            else {
                final File dbPropsFile = new File(dbPropsPath);
                if (!dbPropsFile.exists()) {
                    ConsoleOut.println("DB property file [" + System.getProperty("server.home") + dbPropsPath + "] not found...");
                }
                else {
                    dbSpecificFilePath = dbPropsFile.getAbsolutePath();
                }
            }
            ConsoleOut.println("Reading DB Properties from file path ::: " + dbSpecificFilePath);
            final Properties props = new Properties();
            props.load(new FileInputStream(new File(dbSpecificFilePath)));
            DBMigrationUtil.migrateTables(dsName, props);
            ConsoleOut.println("\nDatabase migration process completed...");
            System.exit(0);
        }
        catch (final Throwable e) {
            ConsoleOut.println("\nDatabase migration failed !!!!!!!!");
            e.printStackTrace();
            System.exit(2013);
        }
    }
}
