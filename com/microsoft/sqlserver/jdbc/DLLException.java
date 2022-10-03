package com.microsoft.sqlserver.jdbc;

import java.text.MessageFormat;

class DLLException extends Exception
{
    private static final long serialVersionUID = -4498171382218222079L;
    private int category;
    private int status;
    private int state;
    private int errCode;
    private String param1;
    private String param2;
    private String param3;
    
    DLLException(final String message, final int category, final int status, final int state) {
        super(message);
        this.category = -9;
        this.status = -9;
        this.state = -9;
        this.errCode = -1;
        this.param1 = "";
        this.param2 = "";
        this.param3 = "";
        this.category = category;
        this.status = status;
        this.state = state;
    }
    
    DLLException(final String param1, final String param2, final String param3, final int errCode) {
        this.category = -9;
        this.status = -9;
        this.state = -9;
        this.errCode = -1;
        this.param1 = "";
        this.param2 = "";
        this.param3 = "";
        this.errCode = errCode;
        this.param1 = param1;
        this.param2 = param2;
        this.param3 = param3;
    }
    
    int GetCategory() {
        return this.category;
    }
    
    int GetStatus() {
        return this.status;
    }
    
    int GetState() {
        return this.state;
    }
    
    int GetErrCode() {
        return this.errCode;
    }
    
    String GetParam1() {
        return this.param1;
    }
    
    String GetParam2() {
        return this.param2;
    }
    
    String GetParam3() {
        return this.param3;
    }
    
    static void buildException(final int errCode, final String param1, final String param2, final String param3) throws SQLServerException {
        final String errMessage = getErrMessage(errCode);
        final MessageFormat form = new MessageFormat(SQLServerException.getErrString(errMessage));
        final String[] msgArgs = buildMsgParams(errMessage, param1, param2, param3);
        throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
    }
    
    private static String[] buildMsgParams(final String errMessage, final String parameter1, final String parameter2, final String parameter3) {
        final String[] msgArgs = new String[3];
        if ("R_AECertLocBad".equalsIgnoreCase(errMessage)) {
            msgArgs[0] = parameter1;
            msgArgs[1] = parameter1 + "/" + parameter2 + "/" + parameter3;
        }
        else if ("R_AECertStoreBad".equalsIgnoreCase(errMessage)) {
            msgArgs[0] = parameter2;
            msgArgs[1] = parameter1 + "/" + parameter2 + "/" + parameter3;
        }
        else if ("R_AECertHashEmpty".equalsIgnoreCase(errMessage)) {
            msgArgs[0] = parameter1 + "/" + parameter2 + "/" + parameter3;
        }
        else {
            msgArgs[0] = parameter1;
            msgArgs[1] = parameter2;
            msgArgs[2] = parameter3;
        }
        return msgArgs;
    }
    
    private static String getErrMessage(final int errCode) {
        String message = null;
        switch (errCode) {
            case 1: {
                message = "R_AEKeypathEmpty";
                break;
            }
            case 2: {
                message = "R_EncryptedCEKNull";
                break;
            }
            case 3: {
                message = "R_NullKeyEncryptionAlgorithm";
                break;
            }
            case 4: {
                message = "R_AEWinApiErr";
                break;
            }
            case 5: {
                message = "R_AECertpathBad";
                break;
            }
            case 6: {
                message = "R_AECertLocBad";
                break;
            }
            case 7: {
                message = "R_AECertStoreBad";
                break;
            }
            case 8: {
                message = "R_AECertHashEmpty";
                break;
            }
            case 9: {
                message = "R_AECertNotFound";
                break;
            }
            case 10: {
                message = "R_AEMaloc";
                break;
            }
            case 11: {
                message = "R_EmptyEncryptedCEK";
                break;
            }
            case 12: {
                message = "R_InvalidKeyEncryptionAlgorithm";
                break;
            }
            case 13: {
                message = "R_AEKeypathLong";
                break;
            }
            case 14: {
                message = "R_InvalidEcryptionAlgorithmVersion";
                break;
            }
            case 15: {
                message = "R_AEECEKLenBad";
                break;
            }
            case 16: {
                message = "R_AEECEKSigLenBad";
                break;
            }
            case 17: {
                message = "R_InvalidCertificateSignature";
                break;
            }
            default: {
                message = "R_AEWinApiErr";
                break;
            }
        }
        return message;
    }
}
