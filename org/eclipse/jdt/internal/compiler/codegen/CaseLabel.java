package org.eclipse.jdt.internal.compiler.codegen;

public class CaseLabel extends BranchLabel
{
    public int instructionPosition;
    
    public CaseLabel(final CodeStream codeStream) {
        super(codeStream);
        this.instructionPosition = -1;
    }
    
    @Override
    void branch() {
        if (this.position == -1) {
            this.addForwardReference(this.codeStream.position);
            final CodeStream codeStream = this.codeStream;
            codeStream.position += 4;
            final CodeStream codeStream2 = this.codeStream;
            codeStream2.classFileOffset += 4;
        }
        else {
            this.codeStream.writeSignedWord(this.position - this.instructionPosition);
        }
    }
    
    @Override
    void branchWide() {
        this.branch();
    }
    
    @Override
    public boolean isCaseLabel() {
        return true;
    }
    
    @Override
    public boolean isStandardLabel() {
        return false;
    }
    
    @Override
    public void place() {
        if ((this.tagBits & 0x2) != 0x0) {
            this.position = this.codeStream.getPosition();
        }
        else {
            this.position = this.codeStream.position;
        }
        if (this.instructionPosition != -1) {
            final int offset = this.position - this.instructionPosition;
            final int[] forwardRefs = this.forwardReferences();
            for (int i = 0, length = this.forwardReferenceCount(); i < length; ++i) {
                this.codeStream.writeSignedWord(forwardRefs[i], offset);
            }
            this.codeStream.addLabel(this);
        }
    }
    
    void placeInstruction() {
        if (this.instructionPosition == -1) {
            this.instructionPosition = this.codeStream.position;
        }
    }
}
