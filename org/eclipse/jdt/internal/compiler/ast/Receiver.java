package org.eclipse.jdt.internal.compiler.ast;

public class Receiver extends Argument
{
    public NameReference qualifyingName;
    
    public Receiver(final char[] name, final long posNom, final TypeReference typeReference, final NameReference qualifyingName, final int modifiers) {
        super(name, posNom, typeReference, modifiers);
        this.qualifyingName = qualifyingName;
    }
    
    @Override
    public boolean isReceiver() {
        return true;
    }
    
    @Override
    public StringBuffer print(final int indent, final StringBuffer output) {
        ASTNode.printIndent(indent, output);
        ASTNode.printModifiers(this.modifiers, output);
        if (this.type == null) {
            output.append("<no type> ");
        }
        else {
            this.type.print(0, output).append(' ');
        }
        if (this.qualifyingName != null) {
            this.qualifyingName.print(indent, output);
            output.append('.');
        }
        return output.append(this.name);
    }
}
