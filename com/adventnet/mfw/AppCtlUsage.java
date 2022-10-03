package com.adventnet.mfw;

public class AppCtlUsage
{
    public static void main(final String[] args) {
        if (args.length >= 1 && args[0].equals("helpUsage")) {
            displayUsageInConsole();
        }
    }
    
    private static void displayUsageInConsole() {
        final String format = "%-50s %-70s";
        ConsoleOut.println(String.format(format, "app_ctl.bat / app_ctl.sh [options]", ""));
        ConsoleOut.println(String.format(format, "Options::", ""));
        ConsoleOut.println(String.format(format, "run", "To start the product server in normal mode"));
        ConsoleOut.println(String.format(format, "run -s", "To start the product server in safe mode"));
        ConsoleOut.println(String.format(format, "shutdown", "To stop the product server"));
        ConsoleOut.println(String.format(format, "startDB [port_number]", "To start the database server (options are required for mysql database only)"));
        ConsoleOut.println(String.format(format, "stopDB [port_number] [user_name] [password]", "To stop the database server (options are required for mysql database only)"));
        ConsoleOut.println(String.format(format, "reinitDB", "To drop all the product tables in database"));
        ConsoleOut.println(String.format(format, "reinitDB -f", "To drop all the tables in database"));
        ConsoleOut.println(String.format(format, "backupDB -t [backup_type] -d [backup_dir] -f [backup_file] -p [password]", "To backup the tables in database"));
        ConsoleOut.println(String.format(format, "restoreDB backup_zip -p [password]", "To restore the tables to database"));
        ConsoleOut.println(String.format(format, "standalone", "To start the product server in standalone mode"));
        ConsoleOut.println(String.format(format, "serverstatus", "To get the status of product server"));
        ConsoleOut.println(String.format(format, "migrateDB destinationDB destinationDBPropertyPath", "To migrate data from one database to another "));
        ConsoleOut.println(String.format(format, "compareSchema", "To compare the table schema between Mickey MetaData and DataBaseMetaData"));
        ConsoleOut.println(String.format(format, "infodump [options... <runningqueries/connection/thread>]", "To notify server to log requested informations"));
    }
}
