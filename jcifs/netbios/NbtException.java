package jcifs.netbios;

import java.io.IOException;

public class NbtException extends IOException
{
    public static final int SUCCESS = 0;
    public static final int ERR_NAM_SRVC = 1;
    public static final int ERR_SSN_SRVC = 2;
    public static final int FMT_ERR = 1;
    public static final int SRV_ERR = 2;
    public static final int IMP_ERR = 4;
    public static final int RFS_ERR = 5;
    public static final int ACT_ERR = 6;
    public static final int CFT_ERR = 7;
    public static final int CONNECTION_REFUSED = -1;
    public static final int NOT_LISTENING_CALLED = 128;
    public static final int NOT_LISTENING_CALLING = 129;
    public static final int CALLED_NOT_PRESENT = 130;
    public static final int NO_RESOURCES = 131;
    public static final int UNSPECIFIED = 143;
    public int errorClass;
    public int errorCode;
    
    public static String getErrorString(final int errorClass, final int errorCode) {
        String result = "";
        Label_0413: {
            switch (errorClass) {
                case 0: {
                    result += "SUCCESS";
                    break;
                }
                case 1: {
                    result += "ERR_NAM_SRVC/";
                    switch (errorCode) {
                        case 1: {
                            result += "FMT_ERR: Format Error";
                            break;
                        }
                    }
                    result = result + "Unknown error code: " + errorCode;
                    break;
                }
                case 2: {
                    result += "ERR_SSN_SRVC/";
                    switch (errorCode) {
                        case -1: {
                            result += "Connection refused";
                            break Label_0413;
                        }
                        case 128: {
                            result += "Not listening on called name";
                            break Label_0413;
                        }
                        case 129: {
                            result += "Not listening for calling name";
                            break Label_0413;
                        }
                        case 130: {
                            result += "Called name not present";
                            break Label_0413;
                        }
                        case 131: {
                            result += "Called name present, but insufficient resources";
                            break Label_0413;
                        }
                        case 143: {
                            result += "Unspecified error";
                            break Label_0413;
                        }
                        default: {
                            result = result + "Unknown error code: " + errorCode;
                            break Label_0413;
                        }
                    }
                    break;
                }
                default: {
                    result = result + "unknown error class: " + errorClass;
                    break;
                }
            }
        }
        return result;
    }
    
    public NbtException(final int errorClass, final int errorCode) {
        super(getErrorString(errorClass, errorCode));
        this.errorClass = errorClass;
        this.errorCode = errorCode;
    }
    
    public String toString() {
        return new String("errorClass=" + this.errorClass + ",errorCode=" + this.errorCode + ",errorString=" + getErrorString(this.errorClass, this.errorCode));
    }
}
