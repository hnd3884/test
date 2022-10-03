package org.eclipse.jdt.internal.compiler.codegen;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class ExceptionLabel extends Label
{
    public int[] ranges;
    private int count;
    public TypeBinding exceptionType;
    public TypeReference exceptionTypeReference;
    public Annotation[] se7Annotations;
    
    public ExceptionLabel(final CodeStream codeStream, final TypeBinding exceptionType, final TypeReference exceptionTypeReference, final Annotation[] se7Annotations) {
        super(codeStream);
        this.ranges = new int[] { -1, -1 };
        this.count = 0;
        this.exceptionType = exceptionType;
        this.exceptionTypeReference = exceptionTypeReference;
        this.se7Annotations = se7Annotations;
    }
    
    public ExceptionLabel(final CodeStream codeStream, final TypeBinding exceptionType) {
        super(codeStream);
        this.ranges = new int[] { -1, -1 };
        this.count = 0;
        this.exceptionType = exceptionType;
    }
    
    public int getCount() {
        return this.count;
    }
    
    @Override
    public void place() {
        this.codeStream.registerExceptionHandler(this);
        this.position = this.codeStream.getPosition();
    }
    
    public void placeEnd() {
        final int endPosition = this.codeStream.position;
        if (this.ranges[this.count - 1] == endPosition) {
            --this.count;
        }
        else {
            this.ranges[this.count++] = endPosition;
        }
    }
    
    public void placeStart() {
        final int startPosition = this.codeStream.position;
        if (this.count > 0 && this.ranges[this.count - 1] == startPosition) {
            --this.count;
            return;
        }
        final int length;
        if (this.count == (length = this.ranges.length)) {
            System.arraycopy(this.ranges, 0, this.ranges = new int[length * 2], 0, length);
        }
        this.ranges[this.count++] = startPosition;
    }
    
    @Override
    public String toString() {
        String basic = this.getClass().getName();
        basic = basic.substring(basic.lastIndexOf(46) + 1);
        final StringBuffer buffer = new StringBuffer(basic);
        buffer.append('@').append(Integer.toHexString(this.hashCode()));
        buffer.append("(type=").append((this.exceptionType == null) ? CharOperation.NO_CHAR : this.exceptionType.readableName());
        buffer.append(", position=").append(this.position);
        buffer.append(", ranges = ");
        if (this.count == 0) {
            buffer.append("[]");
        }
        else {
            for (int i = 0; i < this.count; ++i) {
                if ((i & 0x1) == 0x0) {
                    buffer.append("[").append(this.ranges[i]);
                }
                else {
                    buffer.append(",").append(this.ranges[i]).append("]");
                }
            }
            if ((this.count & 0x1) == 0x1) {
                buffer.append(",?]");
            }
        }
        buffer.append(')');
        return buffer.toString();
    }
}
