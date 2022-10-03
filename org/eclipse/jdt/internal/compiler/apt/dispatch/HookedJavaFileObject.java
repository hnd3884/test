package org.eclipse.jdt.internal.compiler.apt.dispatch;

import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import java.io.Writer;
import java.io.IOException;
import java.io.OutputStream;
import javax.tools.JavaFileObject;
import javax.tools.ForwardingJavaFileObject;

public class HookedJavaFileObject extends ForwardingJavaFileObject<JavaFileObject>
{
    protected final BatchFilerImpl _filer;
    protected final String _fileName;
    private boolean _closed;
    private String _typeName;
    
    public HookedJavaFileObject(final JavaFileObject fileObject, final String fileName, final String typeName, final BatchFilerImpl filer) {
        super(fileObject);
        this._closed = false;
        this._filer = filer;
        this._fileName = fileName;
        this._typeName = typeName;
    }
    
    @Override
    public OutputStream openOutputStream() throws IOException {
        return new ForwardingOutputStream(super.openOutputStream());
    }
    
    @Override
    public Writer openWriter() throws IOException {
        return new ForwardingWriter(super.openWriter());
    }
    
    protected void closed() {
        if (!this._closed) {
            this._closed = true;
            switch (this.getKind()) {
                case SOURCE: {
                    final CompilationUnit unit = new CompilationUnit(null, this._fileName, null);
                    this._filer.addNewUnit(unit);
                    break;
                }
                case CLASS: {
                    IBinaryType binaryType = null;
                    try {
                        binaryType = ClassFileReader.read(this._fileName);
                    }
                    catch (final ClassFormatException ex) {
                        final ReferenceBinding type = this._filer._env._compiler.lookupEnvironment.getType(CharOperation.splitOn('.', this._typeName.toCharArray()));
                        if (type != null) {
                            this._filer.addNewClassFile(type);
                        }
                    }
                    catch (final IOException ex2) {}
                    if (binaryType == null) {
                        break;
                    }
                    final char[] name = binaryType.getName();
                    final ReferenceBinding type2 = this._filer._env._compiler.lookupEnvironment.getType(CharOperation.splitOn('/', name));
                    if (type2 == null || !type2.isValidBinding()) {
                        break;
                    }
                    if (type2.isBinaryBinding()) {
                        this._filer.addNewClassFile(type2);
                        break;
                    }
                    final BinaryTypeBinding binaryBinding = new BinaryTypeBinding(type2.getPackage(), binaryType, this._filer._env._compiler.lookupEnvironment, true);
                    if (binaryBinding != null) {
                        this._filer.addNewClassFile(binaryBinding);
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    private class ForwardingWriter extends Writer
    {
        private final Writer _w;
        
        ForwardingWriter(final Writer w) {
            this._w = w;
        }
        
        @Override
        public Writer append(final char c) throws IOException {
            return this._w.append(c);
        }
        
        @Override
        public Writer append(final CharSequence csq, final int start, final int end) throws IOException {
            return this._w.append(csq, start, end);
        }
        
        @Override
        public Writer append(final CharSequence csq) throws IOException {
            return this._w.append(csq);
        }
        
        @Override
        public void close() throws IOException {
            this._w.close();
            HookedJavaFileObject.this.closed();
        }
        
        @Override
        public void flush() throws IOException {
            this._w.flush();
        }
        
        @Override
        public void write(final char[] cbuf) throws IOException {
            this._w.write(cbuf);
        }
        
        @Override
        public void write(final int c) throws IOException {
            this._w.write(c);
        }
        
        @Override
        public void write(final String str, final int off, final int len) throws IOException {
            this._w.write(str, off, len);
        }
        
        @Override
        public void write(final String str) throws IOException {
            this._w.write(str);
        }
        
        @Override
        public void write(final char[] cbuf, final int off, final int len) throws IOException {
            this._w.write(cbuf, off, len);
        }
        
        @Override
        protected Object clone() throws CloneNotSupportedException {
            return new ForwardingWriter(this._w);
        }
        
        @Override
        public int hashCode() {
            return this._w.hashCode();
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final ForwardingWriter other = (ForwardingWriter)obj;
            if (this._w == null) {
                if (other._w != null) {
                    return false;
                }
            }
            else if (!this._w.equals(other._w)) {
                return false;
            }
            return true;
        }
        
        @Override
        public String toString() {
            return "ForwardingWriter wrapping " + this._w.toString();
        }
    }
    
    private class ForwardingOutputStream extends OutputStream
    {
        private final OutputStream _os;
        
        ForwardingOutputStream(final OutputStream os) {
            this._os = os;
        }
        
        @Override
        public void close() throws IOException {
            this._os.close();
            HookedJavaFileObject.this.closed();
        }
        
        @Override
        public void flush() throws IOException {
            this._os.flush();
        }
        
        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            this._os.write(b, off, len);
        }
        
        @Override
        public void write(final byte[] b) throws IOException {
            this._os.write(b);
        }
        
        @Override
        public void write(final int b) throws IOException {
            this._os.write(b);
        }
        
        @Override
        protected Object clone() throws CloneNotSupportedException {
            return new ForwardingOutputStream(this._os);
        }
        
        @Override
        public int hashCode() {
            return this._os.hashCode();
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final ForwardingOutputStream other = (ForwardingOutputStream)obj;
            if (this._os == null) {
                if (other._os != null) {
                    return false;
                }
            }
            else if (!this._os.equals(other._os)) {
                return false;
            }
            return true;
        }
        
        @Override
        public String toString() {
            return "ForwardingOutputStream wrapping " + this._os.toString();
        }
    }
}
