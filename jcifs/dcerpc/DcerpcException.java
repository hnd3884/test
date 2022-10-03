package jcifs.dcerpc;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import jcifs.util.Hexdump;
import jcifs.smb.WinError;
import java.io.IOException;

public class DcerpcException extends IOException implements DcerpcError, WinError
{
    private int error;
    private Throwable rootCause;
    
    static String getMessageByDcerpcError(final int errcode) {
        int min = 0;
        int max = DcerpcError.DCERPC_FAULT_CODES.length;
        while (max >= min) {
            final int mid = (min + max) / 2;
            if (errcode > DcerpcError.DCERPC_FAULT_CODES[mid]) {
                min = mid + 1;
            }
            else {
                if (errcode >= DcerpcError.DCERPC_FAULT_CODES[mid]) {
                    return DcerpcError.DCERPC_FAULT_MESSAGES[mid];
                }
                max = mid - 1;
            }
        }
        return "0x" + Hexdump.toHexString(errcode, 8);
    }
    
    DcerpcException(final String msg) {
        super(msg);
    }
    
    DcerpcException(final int error) {
        super(getMessageByDcerpcError(error));
        this.error = error;
    }
    
    DcerpcException(final String msg, final Throwable rootCause) {
        super(msg);
        this.rootCause = rootCause;
    }
    
    public int getErrorCode() {
        return this.error;
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
