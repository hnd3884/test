package com.adventnet.cli.messageset;

public class InvalidCommandException extends Exception
{
    public static final String CMD_NAME_NOT_FOUND = "Command Name Not Found";
    public static final String CMD_NOT_FOUND = "Command Not Found";
    public static final String CMD_PATH_NOT_FOUND = "Command Path Not Found";
    public static final String CMD_DATA_NOT_FOUND = "Command Data Not Found";
    public static final String CMD_INSUFFICIENT_PARAMS = "Insufficient No Of Parameters";
    public static final String CMD_INVALID_DATA = "Invalid data in dataSet";
    public static final String CMD_PARAMNAME_MISMATCH = "Parameter name mismatch between DataSet and CommandSet";
    public static final String CMD_SIMPLEOPTIONARGSNAME_MISMATCH = "Simple option args name mismatch between DataSet and CommandSet";
    public static final String CMD_LONGOPTIONARGSNAME_MISMATCH = "Long option args name mismatch between DataSet and CommandSet";
    
    public InvalidCommandException() {
    }
    
    public InvalidCommandException(final String s) {
        super(s);
    }
}
