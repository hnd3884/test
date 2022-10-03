package jcifs.smb;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import jcifs.util.Hexdump;
import java.io.IOException;

public class SmbException extends IOException implements NtStatus, DosError, WinError
{
    private int status;
    private Throwable rootCause;
    
    static String getMessageByCode(final int errcode) {
        if ((errcode & 0xC0000000) == 0xC0000000) {
            int min = 0;
            int max = NtStatus.NT_STATUS_CODES.length;
            while (max >= min) {
                final int mid = (min + max) / 2;
                if (errcode > NtStatus.NT_STATUS_CODES[mid]) {
                    min = mid + 1;
                }
                else {
                    if (errcode >= NtStatus.NT_STATUS_CODES[mid]) {
                        return NtStatus.NT_STATUS_MESSAGES[mid];
                    }
                    max = mid - 1;
                }
            }
        }
        else {
            int min = 0;
            int max = DosError.DOS_ERROR_CODES.length;
            while (max >= min) {
                final int mid = (min + max) / 2;
                if (errcode > DosError.DOS_ERROR_CODES[mid][0]) {
                    min = mid + 1;
                }
                else {
                    if (errcode >= DosError.DOS_ERROR_CODES[mid][0]) {
                        return DosError.DOS_ERROR_MESSAGES[mid];
                    }
                    max = mid - 1;
                }
            }
        }
        return "0x" + Hexdump.toHexString(errcode, 8);
    }
    
    static int getStatusByCode(final int errcode) {
        if ((errcode & 0xC0000000) != 0x0) {
            return errcode;
        }
        int min = 0;
        int max = DosError.DOS_ERROR_CODES.length;
        while (max >= min) {
            final int mid = (min + max) / 2;
            if (errcode > DosError.DOS_ERROR_CODES[mid][0]) {
                min = mid + 1;
            }
            else {
                if (errcode >= DosError.DOS_ERROR_CODES[mid][0]) {
                    return DosError.DOS_ERROR_CODES[mid][1];
                }
                max = mid - 1;
            }
        }
        return -1073741823;
    }
    
    static String getMessageByWinerrCode(final int errcode) {
        int min = 0;
        int max = WinError.WINERR_CODES.length;
        while (max >= min) {
            final int mid = (min + max) / 2;
            if (errcode > WinError.WINERR_CODES[mid]) {
                min = mid + 1;
            }
            else {
                if (errcode >= WinError.WINERR_CODES[mid]) {
                    return WinError.WINERR_MESSAGES[mid];
                }
                max = mid - 1;
            }
        }
        return errcode + "";
    }
    
    SmbException() {
    }
    
    SmbException(final int errcode, final Throwable rootCause) {
        super(getMessageByCode(errcode));
        this.status = getStatusByCode(errcode);
        this.rootCause = rootCause;
    }
    
    SmbException(final String msg) {
        super(msg);
        this.status = -1073741823;
    }
    
    SmbException(final String msg, final Throwable rootCause) {
        super(msg);
        this.rootCause = rootCause;
        this.status = -1073741823;
    }
    
    SmbException(final int errcode, final boolean winerr) {
        super(winerr ? getMessageByWinerrCode(errcode) : getMessageByCode(errcode));
        this.status = (winerr ? errcode : getStatusByCode(errcode));
    }
    
    public int getNtStatus() {
        return this.status;
    }
    
    public Throwable getRootCause() {
        return this.rootCause;
    }
    
    public String toString() {
        if (this.rootCause != null) {
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw);
            this.rootCause.printStackTrace(pw);
            return super.toString() + "\n" + sw;
        }
        return super.toString();
    }
}
