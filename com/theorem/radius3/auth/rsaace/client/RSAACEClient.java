package com.theorem.radius3.auth.rsaace.client;

import com.theorem.radius3.ClientReceiveException;
import com.theorem.radius3.ClientSendException;
import com.theorem.radius3.RADIUSEncrypt;
import com.theorem.radius3.radutil.Util;
import com.theorem.radius3.AttributeList;
import com.theorem.radius3.Attribute;
import com.theorem.radius3.RADIUSClient;

public class RSAACEClient
{
    private RADIUSClient a;
    private Attribute[] b;
    private AttributeList c;
    private RSAACEInfo d;
    protected RSAACEHandler e;
    
    public RSAACEClient(final RADIUSClient a, final String s, final char[] array, final AttributeList list, final RSAACEHandler rsaaceHandler) {
        this.b = null;
        this.d = new RSAACEInfo();
        this.a = a;
        (this.c = new AttributeList(list)).addAttribute(1, s);
        this.e = rsaaceHandler;
        this.d.g = rsaaceHandler;
        this.d.setResponse(Util.toUTF8(array));
        this.d.c = 1;
    }
    
    public final RSAACEInfo process() throws ClientSendException, ClientReceiveException {
        if (this.d.c == 4) {
            this.a();
            return this.d;
        }
        if (this.d.c == 3) {
            this.d.c = 4;
            this.d.a = -1;
            this.d.b = "Already returned a completed state, this method must not be called again.";
            this.a();
            return this.d;
        }
        this.d.c = 2;
        final AttributeList list = new AttributeList(this.c);
        list.addAttribute(2, RADIUSEncrypt.encrypt(this.d.a(), this.a.getSecret(), this.a.getRequestAuthenticator()));
        if (this.b != null) {
            list.mergeAttributes(this.b);
        }
        this.b = null;
        int authenticate;
        try {
            this.a.reset();
            authenticate = this.a.authenticate(list);
        }
        catch (final Exception ex) {
            this.d.c = 4;
            this.d.a = -3;
            this.d.b = ex.getMessage();
            this.a();
            return this.d;
        }
        if (this.a.getError() != 0) {
            this.d.c = 4;
            this.d.a = this.a.getError();
            this.d.b = this.a.getErrorString();
            this.a();
            return this.d;
        }
        this.d.d = this.a.getAttributes();
        switch (authenticate) {
            case 2: {
                this.d.c = 3;
                break;
            }
            case 3: {
                this.d.a = -2;
                this.d.b = "Access-Reject recieved.";
                this.d.c = 4;
                break;
            }
            case 11: {
                this.b = this.a.getAttributes().getAttributeArray(24);
                this.d.a(this.d.d.getStringAttribute(18));
                this.d.c = 2;
                break;
            }
            case 0: {
                this.d.c = 4;
                break;
            }
            default: {
                this.d.c = 4;
                break;
            }
        }
        this.a();
        return this.d;
    }
    
    protected final void a() {
        if (this.e != null) {
            this.e.rsaaceHandler(this.d);
        }
    }
}
