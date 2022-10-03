package org.eclipse.jdt.internal.compiler.codegen;

public abstract class Label
{
    public CodeStream codeStream;
    public int position;
    public static final int POS_NOT_SET = -1;
    
    public Label() {
        this.position = -1;
    }
    
    public Label(final CodeStream codeStream) {
        this.position = -1;
        this.codeStream = codeStream;
    }
    
    public abstract void place();
}
