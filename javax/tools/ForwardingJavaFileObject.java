package javax.tools;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;

public class ForwardingJavaFileObject<F extends JavaFileObject> extends ForwardingFileObject<F> implements JavaFileObject
{
    protected ForwardingJavaFileObject(final F n) {
        super(n);
    }
    
    @Override
    public Kind getKind() {
        return this.fileObject.getKind();
    }
    
    @Override
    public boolean isNameCompatible(final String s, final Kind kind) {
        return this.fileObject.isNameCompatible(s, kind);
    }
    
    @Override
    public NestingKind getNestingKind() {
        return this.fileObject.getNestingKind();
    }
    
    @Override
    public Modifier getAccessLevel() {
        return this.fileObject.getAccessLevel();
    }
}
