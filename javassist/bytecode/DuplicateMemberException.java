package javassist.bytecode;

import javassist.CannotCompileException;

public class DuplicateMemberException extends CannotCompileException
{
    private static final long serialVersionUID = 1L;
    
    public DuplicateMemberException(final String msg) {
        super(msg);
    }
}
