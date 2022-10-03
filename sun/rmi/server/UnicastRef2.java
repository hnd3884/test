package sun.rmi.server;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import sun.rmi.transport.LiveRef;

public class UnicastRef2 extends UnicastRef
{
    private static final long serialVersionUID = 1829537514995881838L;
    
    public UnicastRef2() {
    }
    
    public UnicastRef2(final LiveRef liveRef) {
        super(liveRef);
    }
    
    @Override
    public String getRefClass(final ObjectOutput objectOutput) {
        return "UnicastRef2";
    }
    
    @Override
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        this.ref.write(objectOutput, true);
    }
    
    @Override
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.ref = LiveRef.read(objectInput, true);
    }
}
