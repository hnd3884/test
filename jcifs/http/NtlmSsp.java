package jcifs.http;

import jcifs.ntlmssp.Type3Message;
import jcifs.ntlmssp.Type2Message;
import jcifs.ntlmssp.Type1Message;
import jcifs.util.Base64;
import javax.servlet.ServletException;
import java.io.IOException;
import jcifs.smb.NtlmPasswordAuthentication;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import jcifs.ntlmssp.NtlmFlags;

public class NtlmSsp implements NtlmFlags
{
    public NtlmPasswordAuthentication doAuthentication(final HttpServletRequest req, final HttpServletResponse resp, final byte[] challenge) throws IOException, ServletException {
        return authenticate(req, resp, challenge);
    }
    
    public static NtlmPasswordAuthentication authenticate(final HttpServletRequest req, final HttpServletResponse resp, final byte[] challenge) throws IOException, ServletException {
        String msg = req.getHeader("Authorization");
        if (msg != null && msg.startsWith("NTLM ")) {
            final byte[] src = Base64.decode(msg.substring(5));
            if (src[8] == 1) {
                final Type1Message type1 = new Type1Message(src);
                final Type2Message type2 = new Type2Message(type1, challenge, null);
                msg = Base64.encode(type2.toByteArray());
                resp.setHeader("WWW-Authenticate", "NTLM " + msg);
            }
            else if (src[8] == 3) {
                final Type3Message type3 = new Type3Message(src);
                byte[] lmResponse = type3.getLMResponse();
                if (lmResponse == null) {
                    lmResponse = new byte[0];
                }
                byte[] ntResponse = type3.getNTResponse();
                if (ntResponse == null) {
                    ntResponse = new byte[0];
                }
                return new NtlmPasswordAuthentication(type3.getDomain(), type3.getUser(), challenge, lmResponse, ntResponse);
            }
        }
        else {
            resp.setHeader("WWW-Authenticate", "NTLM");
        }
        resp.setStatus(401);
        resp.setContentLength(0);
        resp.flushBuffer();
        return null;
    }
}
