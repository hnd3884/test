package com.adventnet.tools.update;

public final class UpdateManagerConts
{
    public static final int SUCCESS = 1;
    public static final int FAILURE_REVERT_ALL = 2;
    public static final int FAILURE_REVERT_CONTINUE = 3;
    public static final int FAILURE_REVERT_ALL_CONTINUE = 4;
    public static final int FAILURE_REVERT_COMPLETE = 5;
    public static final int FAILURE_REVERT_COMPLETE_CONTINUE = 6;
    public static final int FAILURE_REVERT_PRE = 7;
    public static final int FAILURE_REVERT_PRE_CONTINUE = 8;
    public static final int FAILURE_REVERT_ABSOLUTE = 9;
    public static final int READMEINSWINGUI = 1;
    public static final int READMEINBROWSERWITHLOCALFILE = 2;
    public static final int READMEINBROWSERWITHWEBSITEURL = 3;
    public static final int PREINSTALL = 10;
    public static final int EEAREXTRACTION = 11;
    public static final int BACKUP = 12;
    public static final int FILESYSTEMUPDATE = 13;
    public static final int POSTINSTALL = 14;
    public static final int EEARCOMPRESSION = 15;
    public static final int PREINSTALLERROR = 100;
    public static final int POSTINSTALLERROR = 200;
    public static final int BACKUPERROR = 300;
    public static final int FILESYSTEMUPDATEERROR = 400;
    public static final int EEAREXTRACTIONERROR = 500;
    public static final int EEARCOMPRESSIONERROR = 600;
    public static final int FILEACCESSDENIED = 700;
    public static final int DISKSPACEERROR = 800;
    public static final int PPMALREADYINSTALLED = 900;
    public static final int INCOMPATIABLEPRODUCT = 1000;
    public static final int INCOMPATIABLEPPM = 1100;
    public static final int PPMINVALID = 1200;
    public static final int INSTALL = 16;
    public static final int REVERT = 17;
    public static final int PATCHVALIDATION = 18;
    
    public static String getType(final int code) {
        switch (code) {
            case 18: {
                return CommonUtil.getString("PatchValidation");
            }
            case 12: {
                return CommonUtil.getString("File Back");
            }
            case 10: {
                return CommonUtil.getString("Pre install");
            }
            case 11: {
                return CommonUtil.getString("Eear extraction");
            }
            case 13: {
                return CommonUtil.getString("File update");
            }
            case 14: {
                return CommonUtil.getString("Post install");
            }
            case 15: {
                return CommonUtil.getString("Eear compresion");
            }
            default: {
                return String.valueOf(code);
            }
        }
    }
}
