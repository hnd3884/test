package java.rmi.server;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.io.DataInput;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.DataOutput;
import java.io.ObjectOutput;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.io.Serializable;

public final class ObjID implements Serializable
{
    public static final int REGISTRY_ID = 0;
    public static final int ACTIVATOR_ID = 1;
    public static final int DGC_ID = 2;
    private static final long serialVersionUID = -6386392263968365220L;
    private static final AtomicLong nextObjNum;
    private static final UID mySpace;
    private static final SecureRandom secureRandom;
    private final long objNum;
    private final UID space;
    
    public ObjID() {
        if (useRandomIDs()) {
            this.space = new UID();
            this.objNum = ObjID.secureRandom.nextLong();
        }
        else {
            this.space = ObjID.mySpace;
            this.objNum = ObjID.nextObjNum.getAndIncrement();
        }
    }
    
    public ObjID(final int n) {
        this.space = new UID((short)0);
        this.objNum = n;
    }
    
    private ObjID(final long objNum, final UID space) {
        this.objNum = objNum;
        this.space = space;
    }
    
    public void write(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeLong(this.objNum);
        this.space.write(objectOutput);
    }
    
    public static ObjID read(final ObjectInput objectInput) throws IOException {
        return new ObjID(objectInput.readLong(), UID.read(objectInput));
    }
    
    @Override
    public int hashCode() {
        return (int)this.objNum;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof ObjID) {
            final ObjID objID = (ObjID)o;
            return this.objNum == objID.objNum && this.space.equals(objID.space);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "[" + (this.space.equals(ObjID.mySpace) ? "" : (this.space + ", ")) + this.objNum + "]";
    }
    
    private static boolean useRandomIDs() {
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("java.rmi.server.randomIDs"));
        return s == null || Boolean.parseBoolean(s);
    }
    
    static {
        nextObjNum = new AtomicLong(0L);
        mySpace = new UID();
        secureRandom = new SecureRandom();
    }
}
