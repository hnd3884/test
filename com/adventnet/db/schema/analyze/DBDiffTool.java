package com.adventnet.db.schema.analyze;

import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.File;
import com.adventnet.mfw.ConsoleOut;

public class DBDiffTool implements DBDiffHandler
{
    public void compareSchema(final String dbtype, final String destinationDBParamsPath) throws Exception {
        try {
            String dbSpecificFilePath = null;
            if (dbtype == null || destinationDBParamsPath == null) {
                ConsoleOut.println("Unknown options specified");
            }
            else {
                final File dbPropsFile = new File(destinationDBParamsPath);
                if (!dbPropsFile.exists()) {
                    ConsoleOut.println("DB property file [" + System.getProperty("server.home") + destinationDBParamsPath + "] not found...");
                }
                else {
                    dbSpecificFilePath = dbPropsFile.getAbsolutePath();
                }
            }
            ConsoleOut.println("Reading DB Properties from file path ::: " + dbSpecificFilePath);
            final Properties props = new Properties();
            props.load(new FileInputStream(new File(dbSpecificFilePath)));
            final long start = System.currentTimeMillis();
            ConsoleOut.println("Started comparing the databases..");
            SchemaAnalyzerUtil.generateDiffForDBVsDB(dbtype, props);
            final long end = System.currentTimeMillis();
            ConsoleOut.println("DB compare finished in [" + (end - start) / 60L + "] seconds.");
            System.exit(0);
        }
        catch (final Throwable t) {
            ConsoleOut.println("\nError in generating diff. Refer logs for more details!!");
            t.printStackTrace();
            System.exit(2016);
        }
    }
    
    public void compareSchemaVsTableDef() throws Exception {
        try {
            final long start = System.currentTimeMillis();
            ConsoleOut.println("Started comparing the metadata and the database..");
            SchemaAnalyzerUtil.generateDiffForMetaDataVsDB();
            final long end = System.currentTimeMillis();
            ConsoleOut.println("DB compare finished in [" + (end - start) / 60L + "] seconds.");
            System.exit(0);
        }
        catch (final Throwable t) {
            ConsoleOut.println("\nError in generating diff. Refer logs for more details!!");
            t.printStackTrace();
            System.exit(2016);
        }
    }
}
