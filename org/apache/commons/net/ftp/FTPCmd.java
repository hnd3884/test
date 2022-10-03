package org.apache.commons.net.ftp;

public enum FTPCmd
{
    ABOR, 
    ACCT, 
    ALLO, 
    APPE, 
    CDUP, 
    CWD, 
    DELE, 
    EPRT, 
    EPSV, 
    FEAT, 
    HELP, 
    LIST, 
    MDTM, 
    MFMT, 
    MKD, 
    MLSD, 
    MLST, 
    MODE, 
    NLST, 
    NOOP, 
    PASS, 
    PASV, 
    PORT, 
    PWD, 
    QUIT, 
    REIN, 
    REST, 
    RETR, 
    RMD, 
    RNFR, 
    RNTO, 
    SITE, 
    SMNT, 
    STAT, 
    STOR, 
    STOU, 
    STRU, 
    SYST, 
    TYPE, 
    USER;
    
    public static final FTPCmd ABORT;
    public static final FTPCmd ACCOUNT;
    public static final FTPCmd ALLOCATE;
    public static final FTPCmd APPEND;
    public static final FTPCmd CHANGE_TO_PARENT_DIRECTORY;
    public static final FTPCmd CHANGE_WORKING_DIRECTORY;
    public static final FTPCmd DATA_PORT;
    public static final FTPCmd DELETE;
    public static final FTPCmd FEATURES;
    public static final FTPCmd FILE_STRUCTURE;
    public static final FTPCmd GET_MOD_TIME;
    public static final FTPCmd LOGOUT;
    public static final FTPCmd MAKE_DIRECTORY;
    public static final FTPCmd MOD_TIME;
    public static final FTPCmd NAME_LIST;
    public static final FTPCmd PASSIVE;
    public static final FTPCmd PASSWORD;
    public static final FTPCmd PRINT_WORKING_DIRECTORY;
    public static final FTPCmd REINITIALIZE;
    public static final FTPCmd REMOVE_DIRECTORY;
    public static final FTPCmd RENAME_FROM;
    public static final FTPCmd RENAME_TO;
    public static final FTPCmd REPRESENTATION_TYPE;
    public static final FTPCmd RESTART;
    public static final FTPCmd RETRIEVE;
    public static final FTPCmd SET_MOD_TIME;
    public static final FTPCmd SITE_PARAMETERS;
    public static final FTPCmd STATUS;
    public static final FTPCmd STORE;
    public static final FTPCmd STORE_UNIQUE;
    public static final FTPCmd STRUCTURE_MOUNT;
    public static final FTPCmd SYSTEM;
    public static final FTPCmd TRANSFER_MODE;
    public static final FTPCmd USERNAME;
    
    public final String getCommand() {
        return this.name();
    }
    
    static {
        ABORT = FTPCmd.ABOR;
        ACCOUNT = FTPCmd.ACCT;
        ALLOCATE = FTPCmd.ALLO;
        APPEND = FTPCmd.APPE;
        CHANGE_TO_PARENT_DIRECTORY = FTPCmd.CDUP;
        CHANGE_WORKING_DIRECTORY = FTPCmd.CWD;
        DATA_PORT = FTPCmd.PORT;
        DELETE = FTPCmd.DELE;
        FEATURES = FTPCmd.FEAT;
        FILE_STRUCTURE = FTPCmd.STRU;
        GET_MOD_TIME = FTPCmd.MDTM;
        LOGOUT = FTPCmd.QUIT;
        MAKE_DIRECTORY = FTPCmd.MKD;
        MOD_TIME = FTPCmd.MDTM;
        NAME_LIST = FTPCmd.NLST;
        PASSIVE = FTPCmd.PASV;
        PASSWORD = FTPCmd.PASS;
        PRINT_WORKING_DIRECTORY = FTPCmd.PWD;
        REINITIALIZE = FTPCmd.REIN;
        REMOVE_DIRECTORY = FTPCmd.RMD;
        RENAME_FROM = FTPCmd.RNFR;
        RENAME_TO = FTPCmd.RNTO;
        REPRESENTATION_TYPE = FTPCmd.TYPE;
        RESTART = FTPCmd.REST;
        RETRIEVE = FTPCmd.RETR;
        SET_MOD_TIME = FTPCmd.MFMT;
        SITE_PARAMETERS = FTPCmd.SITE;
        STATUS = FTPCmd.STAT;
        STORE = FTPCmd.STOR;
        STORE_UNIQUE = FTPCmd.STOU;
        STRUCTURE_MOUNT = FTPCmd.SMNT;
        SYSTEM = FTPCmd.SYST;
        TRANSFER_MODE = FTPCmd.MODE;
        USERNAME = FTPCmd.USER;
    }
}
