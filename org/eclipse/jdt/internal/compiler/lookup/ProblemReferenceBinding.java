package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;
import java.lang.reflect.Field;

public class ProblemReferenceBinding extends ReferenceBinding
{
    ReferenceBinding closestMatch;
    private int problemReason;
    
    public ProblemReferenceBinding(final char[][] compoundName, final ReferenceBinding closestMatch, final int problemReason) {
        this.compoundName = compoundName;
        this.closestMatch = closestMatch;
        this.problemReason = problemReason;
    }
    
    @Override
    public TypeBinding clone(final TypeBinding enclosingType) {
        throw new IllegalStateException();
    }
    
    @Override
    public TypeBinding closestMatch() {
        return this.closestMatch;
    }
    
    public ReferenceBinding closestReferenceMatch() {
        return this.closestMatch;
    }
    
    @Override
    public boolean hasTypeBit(final int bit) {
        return this.closestMatch != null && this.closestMatch.hasTypeBit(bit);
    }
    
    @Override
    public int problemId() {
        return this.problemReason;
    }
    
    public static String problemReasonString(final int problemReason) {
        try {
            final Class reasons = ProblemReasons.class;
            String simpleName = reasons.getName();
            final int lastDot = simpleName.lastIndexOf(46);
            if (lastDot >= 0) {
                simpleName = simpleName.substring(lastDot + 1);
            }
            final Field[] fields = reasons.getFields();
            for (int i = 0, length = fields.length; i < length; ++i) {
                final Field field = fields[i];
                if (field.getType().equals(Integer.TYPE)) {
                    if (field.getInt(reasons) == problemReason) {
                        return String.valueOf(simpleName) + '.' + field.getName();
                    }
                }
            }
        }
        catch (final IllegalAccessException ex) {}
        return "unknown";
    }
    
    @Override
    public void setTypeAnnotations(final AnnotationBinding[] annotations, final boolean evalNullAnnotations) {
    }
    
    @Override
    public char[] shortReadableName() {
        return this.readableName();
    }
    
    @Override
    public char[] sourceName() {
        return (char[])((this.compoundName.length == 0) ? null : this.compoundName[this.compoundName.length - 1]);
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer(10);
        buffer.append("ProblemType:[compoundName=");
        buffer.append((this.compoundName == null) ? "<null>" : new String(CharOperation.concatWith(this.compoundName, '.')));
        buffer.append("][problemID=").append(problemReasonString(this.problemReason));
        buffer.append("][closestMatch=");
        buffer.append((this.closestMatch == null) ? "<null>" : this.closestMatch.toString());
        buffer.append("]");
        return buffer.toString();
    }
}
