package javax.swing.text;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;
import java.util.Vector;
import java.io.Serializable;

public final class StringContent implements AbstractDocument.Content, Serializable
{
    private static final char[] empty;
    private char[] data;
    private int count;
    transient Vector<PosRec> marks;
    
    public StringContent() {
        this(10);
    }
    
    public StringContent(int n) {
        if (n < 1) {
            n = 1;
        }
        (this.data = new char[n])[0] = '\n';
        this.count = 1;
    }
    
    @Override
    public int length() {
        return this.count;
    }
    
    @Override
    public UndoableEdit insertString(final int n, final String s) throws BadLocationException {
        if (n >= this.count || n < 0) {
            throw new BadLocationException("Invalid location", this.count);
        }
        final char[] charArray = s.toCharArray();
        this.replace(n, 0, charArray, 0, charArray.length);
        if (this.marks != null) {
            this.updateMarksForInsert(n, s.length());
        }
        return new InsertUndo(n, s.length());
    }
    
    @Override
    public UndoableEdit remove(final int n, final int n2) throws BadLocationException {
        if (n + n2 >= this.count) {
            throw new BadLocationException("Invalid range", this.count);
        }
        final RemoveUndo removeUndo = new RemoveUndo(n, this.getString(n, n2));
        this.replace(n, n2, StringContent.empty, 0, 0);
        if (this.marks != null) {
            this.updateMarksForRemove(n, n2);
        }
        return removeUndo;
    }
    
    @Override
    public String getString(final int n, final int n2) throws BadLocationException {
        if (n + n2 > this.count) {
            throw new BadLocationException("Invalid range", this.count);
        }
        return new String(this.data, n, n2);
    }
    
    @Override
    public void getChars(final int offset, final int count, final Segment segment) throws BadLocationException {
        if (offset + count > this.count) {
            throw new BadLocationException("Invalid location", this.count);
        }
        segment.array = this.data;
        segment.offset = offset;
        segment.count = count;
    }
    
    @Override
    public Position createPosition(final int n) throws BadLocationException {
        if (this.marks == null) {
            this.marks = new Vector<PosRec>();
        }
        return new StickyPosition(n);
    }
    
    void replace(final int n, final int n2, final char[] array, final int n3, final int n4) {
        final int n5 = n4 - n2;
        final int n6 = n + n2;
        final int n7 = this.count - n6;
        final int n8 = n6 + n5;
        if (this.count + n5 >= this.data.length) {
            final char[] data = new char[Math.max(2 * this.data.length, this.count + n5)];
            System.arraycopy(this.data, 0, data, 0, n);
            System.arraycopy(array, n3, data, n, n4);
            System.arraycopy(this.data, n6, data, n8, n7);
            this.data = data;
        }
        else {
            System.arraycopy(this.data, n6, this.data, n8, n7);
            System.arraycopy(array, n3, this.data, n, n4);
        }
        this.count += n5;
    }
    
    void resize(final int n) {
        final char[] data = new char[n];
        System.arraycopy(this.data, 0, data, 0, Math.min(n, this.count));
        this.data = data;
    }
    
    synchronized void updateMarksForInsert(int n, final int n2) {
        if (n == 0) {
            n = 1;
        }
        for (int size = this.marks.size(), i = 0; i < size; ++i) {
            final PosRec posRec = this.marks.elementAt(i);
            if (posRec.unused) {
                this.marks.removeElementAt(i);
                --i;
                --size;
            }
            else if (posRec.offset >= n) {
                final PosRec posRec2 = posRec;
                posRec2.offset += n2;
            }
        }
    }
    
    synchronized void updateMarksForRemove(final int offset, final int n) {
        for (int size = this.marks.size(), i = 0; i < size; ++i) {
            final PosRec posRec = this.marks.elementAt(i);
            if (posRec.unused) {
                this.marks.removeElementAt(i);
                --i;
                --size;
            }
            else if (posRec.offset >= offset + n) {
                final PosRec posRec2 = posRec;
                posRec2.offset -= n;
            }
            else if (posRec.offset >= offset) {
                posRec.offset = offset;
            }
        }
    }
    
    protected Vector getPositionsInRange(final Vector vector, final int n, final int n2) {
        int size = this.marks.size();
        final int n3 = n + n2;
        final Vector vector2 = (vector == null) ? new Vector() : vector;
        for (int i = 0; i < size; ++i) {
            final PosRec posRec = this.marks.elementAt(i);
            if (posRec.unused) {
                this.marks.removeElementAt(i);
                --i;
                --size;
            }
            else if (posRec.offset >= n && posRec.offset <= n3) {
                vector2.addElement(new UndoPosRef(posRec));
            }
        }
        return vector2;
    }
    
    protected void updateUndoPositions(final Vector vector) {
        for (int i = vector.size() - 1; i >= 0; --i) {
            final UndoPosRef undoPosRef = vector.elementAt(i);
            if (undoPosRef.rec.unused) {
                vector.removeElementAt(i);
            }
            else {
                undoPosRef.resetLocation();
            }
        }
    }
    
    static {
        empty = new char[0];
    }
    
    final class PosRec
    {
        int offset;
        boolean unused;
        
        PosRec(final int offset) {
            this.offset = offset;
        }
    }
    
    final class StickyPosition implements Position
    {
        PosRec rec;
        
        StickyPosition(final int n) {
            this.rec = new PosRec(n);
            StringContent.this.marks.addElement(this.rec);
        }
        
        @Override
        public int getOffset() {
            return this.rec.offset;
        }
        
        @Override
        protected void finalize() throws Throwable {
            this.rec.unused = true;
        }
        
        @Override
        public String toString() {
            return Integer.toString(this.getOffset());
        }
    }
    
    final class UndoPosRef
    {
        protected int undoLocation;
        protected PosRec rec;
        
        UndoPosRef(final PosRec rec) {
            this.rec = rec;
            this.undoLocation = rec.offset;
        }
        
        protected void resetLocation() {
            this.rec.offset = this.undoLocation;
        }
    }
    
    class InsertUndo extends AbstractUndoableEdit
    {
        protected int offset;
        protected int length;
        protected String string;
        protected Vector posRefs;
        
        protected InsertUndo(final int offset, final int length) {
            this.offset = offset;
            this.length = length;
        }
        
        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            try {
                synchronized (StringContent.this) {
                    if (StringContent.this.marks != null) {
                        this.posRefs = StringContent.this.getPositionsInRange(null, this.offset, this.length);
                    }
                    this.string = StringContent.this.getString(this.offset, this.length);
                    StringContent.this.remove(this.offset, this.length);
                }
            }
            catch (final BadLocationException ex) {
                throw new CannotUndoException();
            }
        }
        
        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            try {
                synchronized (StringContent.this) {
                    StringContent.this.insertString(this.offset, this.string);
                    this.string = null;
                    if (this.posRefs != null) {
                        StringContent.this.updateUndoPositions(this.posRefs);
                        this.posRefs = null;
                    }
                }
            }
            catch (final BadLocationException ex) {
                throw new CannotRedoException();
            }
        }
    }
    
    class RemoveUndo extends AbstractUndoableEdit
    {
        protected int offset;
        protected int length;
        protected String string;
        protected Vector posRefs;
        
        protected RemoveUndo(final int offset, final String string) {
            this.offset = offset;
            this.string = string;
            this.length = string.length();
            if (StringContent.this.marks != null) {
                this.posRefs = StringContent.this.getPositionsInRange(null, offset, this.length);
            }
        }
        
        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            try {
                synchronized (StringContent.this) {
                    StringContent.this.insertString(this.offset, this.string);
                    if (this.posRefs != null) {
                        StringContent.this.updateUndoPositions(this.posRefs);
                        this.posRefs = null;
                    }
                    this.string = null;
                }
            }
            catch (final BadLocationException ex) {
                throw new CannotUndoException();
            }
        }
        
        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            try {
                synchronized (StringContent.this) {
                    this.string = StringContent.this.getString(this.offset, this.length);
                    if (StringContent.this.marks != null) {
                        this.posRefs = StringContent.this.getPositionsInRange(null, this.offset, this.length);
                    }
                    StringContent.this.remove(this.offset, this.length);
                }
            }
            catch (final BadLocationException ex) {
                throw new CannotRedoException();
            }
        }
    }
}
