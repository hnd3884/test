package javax.swing.text;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.UIManager;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CompoundEdit;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.swing.tree.TreeNode;
import java.io.IOException;
import java.io.ObjectInputValidation;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import sun.font.BidiUtils;
import java.text.Bidi;
import java.util.Vector;
import sun.swing.SwingUtilities2;
import javax.swing.undo.UndoableEdit;
import java.awt.font.TextAttribute;
import java.util.EventListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.util.Hashtable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.swing.event.EventListenerList;
import java.util.Dictionary;
import java.io.Serializable;

public abstract class AbstractDocument implements Document, Serializable
{
    private transient int numReaders;
    private transient Thread currWriter;
    private transient int numWriters;
    private transient boolean notifyingListeners;
    private static Boolean defaultI18NProperty;
    private Dictionary<Object, Object> documentProperties;
    protected EventListenerList listenerList;
    private Content data;
    private AttributeContext context;
    private transient BranchElement bidiRoot;
    private DocumentFilter documentFilter;
    private transient DocumentFilter.FilterBypass filterBypass;
    private static final String BAD_LOCK_STATE = "document lock failure";
    protected static final String BAD_LOCATION = "document location failure";
    public static final String ParagraphElementName = "paragraph";
    public static final String ContentElementName = "content";
    public static final String SectionElementName = "section";
    public static final String BidiElementName = "bidi level";
    public static final String ElementNameAttribute = "$ename";
    static final String I18NProperty = "i18n";
    static final Object MultiByteProperty;
    static final String AsyncLoadPriority = "load priority";
    
    protected AbstractDocument(final Content content) {
        this(content, StyleContext.getDefaultStyleContext());
    }
    
    protected AbstractDocument(final Content data, final AttributeContext context) {
        this.documentProperties = null;
        this.listenerList = new EventListenerList();
        this.data = data;
        this.context = context;
        this.bidiRoot = new BidiRootElement();
        if (AbstractDocument.defaultI18NProperty == null) {
            final String s = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
                @Override
                public String run() {
                    return System.getProperty("i18n");
                }
            });
            if (s != null) {
                AbstractDocument.defaultI18NProperty = Boolean.valueOf(s);
            }
            else {
                AbstractDocument.defaultI18NProperty = Boolean.FALSE;
            }
        }
        this.putProperty("i18n", AbstractDocument.defaultI18NProperty);
        this.writeLock();
        try {
            this.bidiRoot.replace(0, 0, new Element[] { new BidiElement(this.bidiRoot, 0, 1, 0) });
        }
        finally {
            this.writeUnlock();
        }
    }
    
    public Dictionary<Object, Object> getDocumentProperties() {
        if (this.documentProperties == null) {
            this.documentProperties = new Hashtable<Object, Object>(2);
        }
        return this.documentProperties;
    }
    
    public void setDocumentProperties(final Dictionary<Object, Object> documentProperties) {
        this.documentProperties = documentProperties;
    }
    
    protected void fireInsertUpdate(final DocumentEvent documentEvent) {
        this.notifyingListeners = true;
        try {
            final Object[] listenerList = this.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == DocumentListener.class) {
                    ((DocumentListener)listenerList[i + 1]).insertUpdate(documentEvent);
                }
            }
        }
        finally {
            this.notifyingListeners = false;
        }
    }
    
    protected void fireChangedUpdate(final DocumentEvent documentEvent) {
        this.notifyingListeners = true;
        try {
            final Object[] listenerList = this.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == DocumentListener.class) {
                    ((DocumentListener)listenerList[i + 1]).changedUpdate(documentEvent);
                }
            }
        }
        finally {
            this.notifyingListeners = false;
        }
    }
    
    protected void fireRemoveUpdate(final DocumentEvent documentEvent) {
        this.notifyingListeners = true;
        try {
            final Object[] listenerList = this.listenerList.getListenerList();
            for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                if (listenerList[i] == DocumentListener.class) {
                    ((DocumentListener)listenerList[i + 1]).removeUpdate(documentEvent);
                }
            }
        }
        finally {
            this.notifyingListeners = false;
        }
    }
    
    protected void fireUndoableEditUpdate(final UndoableEditEvent undoableEditEvent) {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == UndoableEditListener.class) {
                ((UndoableEditListener)listenerList[i + 1]).undoableEditHappened(undoableEditEvent);
            }
        }
    }
    
    public <T extends EventListener> T[] getListeners(final Class<T> clazz) {
        return this.listenerList.getListeners(clazz);
    }
    
    public int getAsynchronousLoadPriority() {
        final Integer n = (Integer)this.getProperty("load priority");
        if (n != null) {
            return n;
        }
        return -1;
    }
    
    public void setAsynchronousLoadPriority(final int n) {
        this.putProperty("load priority", (n >= 0) ? Integer.valueOf(n) : null);
    }
    
    public void setDocumentFilter(final DocumentFilter documentFilter) {
        this.documentFilter = documentFilter;
    }
    
    public DocumentFilter getDocumentFilter() {
        return this.documentFilter;
    }
    
    @Override
    public void render(final Runnable runnable) {
        this.readLock();
        try {
            runnable.run();
        }
        finally {
            this.readUnlock();
        }
    }
    
    @Override
    public int getLength() {
        return this.data.length() - 1;
    }
    
    @Override
    public void addDocumentListener(final DocumentListener documentListener) {
        this.listenerList.add(DocumentListener.class, documentListener);
    }
    
    @Override
    public void removeDocumentListener(final DocumentListener documentListener) {
        this.listenerList.remove(DocumentListener.class, documentListener);
    }
    
    public DocumentListener[] getDocumentListeners() {
        return this.listenerList.getListeners(DocumentListener.class);
    }
    
    @Override
    public void addUndoableEditListener(final UndoableEditListener undoableEditListener) {
        this.listenerList.add(UndoableEditListener.class, undoableEditListener);
    }
    
    @Override
    public void removeUndoableEditListener(final UndoableEditListener undoableEditListener) {
        this.listenerList.remove(UndoableEditListener.class, undoableEditListener);
    }
    
    public UndoableEditListener[] getUndoableEditListeners() {
        return this.listenerList.getListeners(UndoableEditListener.class);
    }
    
    @Override
    public final Object getProperty(final Object o) {
        return this.getDocumentProperties().get(o);
    }
    
    @Override
    public final void putProperty(final Object o, final Object o2) {
        if (o2 != null) {
            this.getDocumentProperties().put(o, o2);
        }
        else {
            this.getDocumentProperties().remove(o);
        }
        if (o == TextAttribute.RUN_DIRECTION && Boolean.TRUE.equals(this.getProperty("i18n"))) {
            this.writeLock();
            try {
                this.updateBidi(new DefaultDocumentEvent(0, this.getLength(), DocumentEvent.EventType.INSERT));
            }
            finally {
                this.writeUnlock();
            }
        }
    }
    
    @Override
    public void remove(final int n, final int n2) throws BadLocationException {
        final DocumentFilter documentFilter = this.getDocumentFilter();
        this.writeLock();
        try {
            if (documentFilter != null) {
                documentFilter.remove(this.getFilterBypass(), n, n2);
            }
            else {
                this.handleRemove(n, n2);
            }
        }
        finally {
            this.writeUnlock();
        }
    }
    
    void handleRemove(final int n, final int n2) throws BadLocationException {
        if (n2 > 0) {
            if (n < 0 || n + n2 > this.getLength()) {
                throw new BadLocationException("Invalid remove", this.getLength() + 1);
            }
            final DefaultDocumentEvent defaultDocumentEvent = new DefaultDocumentEvent(n, n2, DocumentEvent.EventType.REMOVE);
            final boolean composedTextElement = Utilities.isComposedTextElement(this, n);
            this.removeUpdate(defaultDocumentEvent);
            final UndoableEdit remove = this.data.remove(n, n2);
            if (remove != null) {
                defaultDocumentEvent.addEdit(remove);
            }
            this.postRemoveUpdate(defaultDocumentEvent);
            defaultDocumentEvent.end();
            this.fireRemoveUpdate(defaultDocumentEvent);
            if (remove != null && !composedTextElement) {
                this.fireUndoableEditUpdate(new UndoableEditEvent(this, defaultDocumentEvent));
            }
        }
    }
    
    public void replace(final int n, final int n2, final String s, final AttributeSet set) throws BadLocationException {
        if (n2 == 0 && (s == null || s.length() == 0)) {
            return;
        }
        final DocumentFilter documentFilter = this.getDocumentFilter();
        this.writeLock();
        try {
            if (documentFilter != null) {
                documentFilter.replace(this.getFilterBypass(), n, n2, s, set);
            }
            else {
                if (n2 > 0) {
                    this.remove(n, n2);
                }
                if (s != null && s.length() > 0) {
                    this.insertString(n, s, set);
                }
            }
        }
        finally {
            this.writeUnlock();
        }
    }
    
    @Override
    public void insertString(final int n, final String s, final AttributeSet set) throws BadLocationException {
        if (s == null || s.length() == 0) {
            return;
        }
        final DocumentFilter documentFilter = this.getDocumentFilter();
        this.writeLock();
        try {
            if (documentFilter != null) {
                documentFilter.insertString(this.getFilterBypass(), n, s, set);
            }
            else {
                this.handleInsertString(n, s, set);
            }
        }
        finally {
            this.writeUnlock();
        }
    }
    
    private void handleInsertString(final int n, final String s, final AttributeSet set) throws BadLocationException {
        if (s == null || s.length() == 0) {
            return;
        }
        final UndoableEdit insertString = this.data.insertString(n, s);
        final DefaultDocumentEvent defaultDocumentEvent = new DefaultDocumentEvent(n, s.length(), DocumentEvent.EventType.INSERT);
        if (insertString != null) {
            defaultDocumentEvent.addEdit(insertString);
        }
        if (this.getProperty("i18n").equals(Boolean.FALSE)) {
            final Object property = this.getProperty(TextAttribute.RUN_DIRECTION);
            if (property != null && property.equals(TextAttribute.RUN_DIRECTION_RTL)) {
                this.putProperty("i18n", Boolean.TRUE);
            }
            else {
                final char[] charArray = s.toCharArray();
                if (SwingUtilities2.isComplexLayout(charArray, 0, charArray.length)) {
                    this.putProperty("i18n", Boolean.TRUE);
                }
            }
        }
        this.insertUpdate(defaultDocumentEvent, set);
        defaultDocumentEvent.end();
        this.fireInsertUpdate(defaultDocumentEvent);
        if (insertString != null && (set == null || !set.isDefined(StyleConstants.ComposedTextAttribute))) {
            this.fireUndoableEditUpdate(new UndoableEditEvent(this, defaultDocumentEvent));
        }
    }
    
    @Override
    public String getText(final int n, final int n2) throws BadLocationException {
        if (n2 < 0) {
            throw new BadLocationException("Length must be positive", n2);
        }
        return this.data.getString(n, n2);
    }
    
    @Override
    public void getText(final int n, final int n2, final Segment segment) throws BadLocationException {
        if (n2 < 0) {
            throw new BadLocationException("Length must be positive", n2);
        }
        this.data.getChars(n, n2, segment);
    }
    
    @Override
    public synchronized Position createPosition(final int n) throws BadLocationException {
        return this.data.createPosition(n);
    }
    
    @Override
    public final Position getStartPosition() {
        Position position;
        try {
            position = this.createPosition(0);
        }
        catch (final BadLocationException ex) {
            position = null;
        }
        return position;
    }
    
    @Override
    public final Position getEndPosition() {
        Position position;
        try {
            position = this.createPosition(this.data.length());
        }
        catch (final BadLocationException ex) {
            position = null;
        }
        return position;
    }
    
    @Override
    public Element[] getRootElements() {
        return new Element[] { this.getDefaultRootElement(), this.getBidiRootElement() };
    }
    
    @Override
    public abstract Element getDefaultRootElement();
    
    private DocumentFilter.FilterBypass getFilterBypass() {
        if (this.filterBypass == null) {
            this.filterBypass = new DefaultFilterBypass();
        }
        return this.filterBypass;
    }
    
    public Element getBidiRootElement() {
        return this.bidiRoot;
    }
    
    static boolean isLeftToRight(final Document document, final int n, final int n2) {
        if (Boolean.TRUE.equals(document.getProperty("i18n")) && document instanceof AbstractDocument) {
            final Element bidiRootElement = ((AbstractDocument)document).getBidiRootElement();
            final Element element = bidiRootElement.getElement(bidiRootElement.getElementIndex(n));
            if (element.getEndOffset() >= n2) {
                return StyleConstants.getBidiLevel(element.getAttributes()) % 2 == 0;
            }
        }
        return true;
    }
    
    public abstract Element getParagraphElement(final int p0);
    
    protected final AttributeContext getAttributeContext() {
        return this.context;
    }
    
    protected void insertUpdate(final DefaultDocumentEvent defaultDocumentEvent, final AttributeSet set) {
        if (this.getProperty("i18n").equals(Boolean.TRUE)) {
            this.updateBidi(defaultDocumentEvent);
        }
        if (defaultDocumentEvent.type == DocumentEvent.EventType.INSERT && defaultDocumentEvent.getLength() > 0 && !Boolean.TRUE.equals(this.getProperty(AbstractDocument.MultiByteProperty))) {
            final Segment sharedSegment = SegmentCache.getSharedSegment();
            Label_0112: {
                try {
                    this.getText(defaultDocumentEvent.getOffset(), defaultDocumentEvent.getLength(), sharedSegment);
                    sharedSegment.first();
                    while (sharedSegment.current() <= '\u00ff') {
                        if (sharedSegment.next() == '\uffff') {
                            break Label_0112;
                        }
                    }
                    this.putProperty(AbstractDocument.MultiByteProperty, Boolean.TRUE);
                }
                catch (final BadLocationException ex) {}
            }
            SegmentCache.releaseSharedSegment(sharedSegment);
        }
    }
    
    protected void removeUpdate(final DefaultDocumentEvent defaultDocumentEvent) {
    }
    
    protected void postRemoveUpdate(final DefaultDocumentEvent defaultDocumentEvent) {
        if (this.getProperty("i18n").equals(Boolean.TRUE)) {
            this.updateBidi(defaultDocumentEvent);
        }
    }
    
    void updateBidi(final DefaultDocumentEvent defaultDocumentEvent) {
        int n2;
        int n3;
        if (defaultDocumentEvent.type == DocumentEvent.EventType.INSERT || defaultDocumentEvent.type == DocumentEvent.EventType.CHANGE) {
            final int offset = defaultDocumentEvent.getOffset();
            final int n = offset + defaultDocumentEvent.getLength();
            n2 = this.getParagraphElement(offset).getStartOffset();
            n3 = this.getParagraphElement(n).getEndOffset();
        }
        else {
            if (defaultDocumentEvent.type != DocumentEvent.EventType.REMOVE) {
                throw new Error("Internal error: unknown event type.");
            }
            final Element paragraphElement = this.getParagraphElement(defaultDocumentEvent.getOffset());
            n2 = paragraphElement.getStartOffset();
            n3 = paragraphElement.getEndOffset();
        }
        final byte[] calculateBidiLevels = this.calculateBidiLevels(n2, n3);
        final Vector<BidiElement> vector = new Vector<BidiElement>();
        int startOffset = n2;
        int elementIndex = 0;
        if (startOffset > 0) {
            final Element element = this.bidiRoot.getElement(elementIndex = this.bidiRoot.getElementIndex(n2 - 1));
            final int bidiLevel = StyleConstants.getBidiLevel(element.getAttributes());
            if (bidiLevel == calculateBidiLevels[0]) {
                startOffset = element.getStartOffset();
            }
            else if (element.getEndOffset() > n2) {
                vector.addElement(new BidiElement(this.bidiRoot, element.getStartOffset(), n2, bidiLevel));
            }
            else {
                ++elementIndex;
            }
        }
        int n4;
        for (n4 = 0; n4 < calculateBidiLevels.length && calculateBidiLevels[n4] == calculateBidiLevels[0]; ++n4) {}
        int endOffset = n3;
        BidiElement bidiElement = null;
        int elementIndex2 = this.bidiRoot.getElementCount() - 1;
        if (endOffset <= this.getLength()) {
            final Element element2 = this.bidiRoot.getElement(elementIndex2 = this.bidiRoot.getElementIndex(n3));
            final int bidiLevel2 = StyleConstants.getBidiLevel(element2.getAttributes());
            if (bidiLevel2 == calculateBidiLevels[calculateBidiLevels.length - 1]) {
                endOffset = element2.getEndOffset();
            }
            else if (element2.getStartOffset() < n3) {
                bidiElement = new BidiElement(this.bidiRoot, n3, element2.getEndOffset(), bidiLevel2);
            }
            else {
                --elementIndex2;
            }
        }
        int length;
        for (length = calculateBidiLevels.length; length > n4 && calculateBidiLevels[length - 1] == calculateBidiLevels[calculateBidiLevels.length - 1]; --length) {}
        if (n4 == length && calculateBidiLevels[0] == calculateBidiLevels[calculateBidiLevels.length - 1]) {
            vector.addElement(new BidiElement(this.bidiRoot, startOffset, endOffset, calculateBidiLevels[0]));
        }
        else {
            vector.addElement(new BidiElement(this.bidiRoot, startOffset, n4 + n2, calculateBidiLevels[0]));
            int n5;
            for (int i = n4; i < length; i = n5) {
                for (n5 = i; n5 < calculateBidiLevels.length && calculateBidiLevels[n5] == calculateBidiLevels[i]; ++n5) {}
                vector.addElement(new BidiElement(this.bidiRoot, n2 + i, n2 + n5, calculateBidiLevels[i]));
            }
            vector.addElement(new BidiElement(this.bidiRoot, length + n2, endOffset, calculateBidiLevels[calculateBidiLevels.length - 1]));
        }
        if (bidiElement != null) {
            vector.addElement(bidiElement);
        }
        int n6 = 0;
        if (this.bidiRoot.getElementCount() > 0) {
            n6 = elementIndex2 - elementIndex + 1;
        }
        final Element[] array = new Element[n6];
        for (int j = 0; j < n6; ++j) {
            array[j] = this.bidiRoot.getElement(elementIndex + j);
        }
        final Element[] array2 = new Element[vector.size()];
        vector.copyInto(array2);
        defaultDocumentEvent.addEdit(new ElementEdit(this.bidiRoot, elementIndex, array, array2));
        this.bidiRoot.replace(elementIndex, array.length, array2);
    }
    
    private byte[] calculateBidiLevels(final int n, final int n2) {
        final byte[] array = new byte[n2 - n];
        int n3 = 0;
        Boolean b = null;
        final Object property = this.getProperty(TextAttribute.RUN_DIRECTION);
        if (property instanceof Boolean) {
            b = (Boolean)property;
        }
        int i = n;
        while (i < n2) {
            final Element paragraphElement = this.getParagraphElement(i);
            final int startOffset = paragraphElement.getStartOffset();
            final int endOffset = paragraphElement.getEndOffset();
            Boolean b2 = b;
            final Object attribute = paragraphElement.getAttributes().getAttribute(TextAttribute.RUN_DIRECTION);
            if (attribute instanceof Boolean) {
                b2 = (Boolean)attribute;
            }
            final Segment sharedSegment = SegmentCache.getSharedSegment();
            try {
                this.getText(startOffset, endOffset - startOffset, sharedSegment);
            }
            catch (final BadLocationException ex) {
                throw new Error("Internal error: " + ex.toString());
            }
            int n4 = -2;
            if (b2 != null) {
                if (TextAttribute.RUN_DIRECTION_LTR.equals(b2)) {
                    n4 = 0;
                }
                else {
                    n4 = 1;
                }
            }
            final Bidi bidi = new Bidi(sharedSegment.array, sharedSegment.offset, null, 0, sharedSegment.count, n4);
            BidiUtils.getLevels(bidi, array, n3);
            n3 += bidi.getLength();
            i = paragraphElement.getEndOffset();
            SegmentCache.releaseSharedSegment(sharedSegment);
        }
        if (n3 != array.length) {
            throw new Error("levelsEnd assertion failed.");
        }
        return array;
    }
    
    public void dump(final PrintStream printStream) {
        final Element defaultRootElement = this.getDefaultRootElement();
        if (defaultRootElement instanceof AbstractElement) {
            ((AbstractElement)defaultRootElement).dump(printStream, 0);
        }
        this.bidiRoot.dump(printStream, 0);
    }
    
    protected final Content getContent() {
        return this.data;
    }
    
    protected Element createLeafElement(final Element element, final AttributeSet set, final int n, final int n2) {
        return new LeafElement(element, set, n, n2);
    }
    
    protected Element createBranchElement(final Element element, final AttributeSet set) {
        return new BranchElement(element, set);
    }
    
    protected final synchronized Thread getCurrentWriter() {
        return this.currWriter;
    }
    
    protected final synchronized void writeLock() {
        try {
            while (this.numReaders > 0 || this.currWriter != null) {
                if (Thread.currentThread() == this.currWriter) {
                    if (this.notifyingListeners) {
                        throw new IllegalStateException("Attempt to mutate in notification");
                    }
                    ++this.numWriters;
                    return;
                }
                else {
                    this.wait();
                }
            }
            this.currWriter = Thread.currentThread();
            this.numWriters = 1;
        }
        catch (final InterruptedException ex) {
            throw new Error("Interrupted attempt to acquire write lock");
        }
    }
    
    protected final synchronized void writeUnlock() {
        final int numWriters = this.numWriters - 1;
        this.numWriters = numWriters;
        if (numWriters <= 0) {
            this.numWriters = 0;
            this.currWriter = null;
            this.notifyAll();
        }
    }
    
    public final synchronized void readLock() {
        try {
            while (this.currWriter != null) {
                if (this.currWriter == Thread.currentThread()) {
                    return;
                }
                this.wait();
            }
            ++this.numReaders;
        }
        catch (final InterruptedException ex) {
            throw new Error("Interrupted attempt to acquire read lock");
        }
    }
    
    public final synchronized void readUnlock() {
        if (this.currWriter == Thread.currentThread()) {
            return;
        }
        if (this.numReaders <= 0) {
            throw new StateInvariantError("document lock failure");
        }
        --this.numReaders;
        this.notify();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        this.listenerList = new EventListenerList();
        this.bidiRoot = new BidiRootElement();
        try {
            this.writeLock();
            this.bidiRoot.replace(0, 0, new Element[] { new BidiElement(this.bidiRoot, 0, 1, 0) });
        }
        finally {
            this.writeUnlock();
        }
        objectInputStream.registerValidation(new ObjectInputValidation() {
            @Override
            public void validateObject() {
                try {
                    AbstractDocument.this.writeLock();
                    AbstractDocument.this.updateBidi(new DefaultDocumentEvent(0, AbstractDocument.this.getLength(), DocumentEvent.EventType.INSERT));
                }
                finally {
                    AbstractDocument.this.writeUnlock();
                }
            }
        }, 0);
    }
    
    static {
        MultiByteProperty = "multiByte";
    }
    
    public abstract class AbstractElement implements Element, MutableAttributeSet, Serializable, TreeNode
    {
        private Element parent;
        private transient AttributeSet attributes;
        
        public AbstractElement(final Element parent, final AttributeSet set) {
            this.parent = parent;
            this.attributes = AbstractDocument.this.getAttributeContext().getEmptySet();
            if (set != null) {
                this.addAttributes(set);
            }
        }
        
        private final void indent(final PrintWriter printWriter, final int n) {
            for (int i = 0; i < n; ++i) {
                printWriter.print("  ");
            }
        }
        
        public void dump(final PrintStream printStream, final int n) {
            PrintWriter printWriter;
            try {
                printWriter = new PrintWriter(new OutputStreamWriter(printStream, "JavaEsc"), true);
            }
            catch (final UnsupportedEncodingException ex) {
                printWriter = new PrintWriter(printStream, true);
            }
            this.indent(printWriter, n);
            if (this.getName() == null) {
                printWriter.print("<??");
            }
            else {
                printWriter.print("<" + this.getName());
            }
            if (this.getAttributeCount() > 0) {
                printWriter.println("");
                final Enumeration<?> attributeNames = this.attributes.getAttributeNames();
                while (attributeNames.hasMoreElements()) {
                    final Object nextElement = attributeNames.nextElement();
                    this.indent(printWriter, n + 1);
                    printWriter.println(nextElement + "=" + this.getAttribute(nextElement));
                }
                this.indent(printWriter, n);
            }
            printWriter.println(">");
            if (this.isLeaf()) {
                this.indent(printWriter, n + 1);
                printWriter.print("[" + this.getStartOffset() + "," + this.getEndOffset() + "]");
                final Content content = AbstractDocument.this.getContent();
                try {
                    String s = content.getString(this.getStartOffset(), this.getEndOffset() - this.getStartOffset());
                    if (s.length() > 40) {
                        s = s.substring(0, 40) + "...";
                    }
                    printWriter.println("[" + s + "]");
                }
                catch (final BadLocationException ex2) {}
            }
            else {
                for (int elementCount = this.getElementCount(), i = 0; i < elementCount; ++i) {
                    ((AbstractElement)this.getElement(i)).dump(printStream, n + 1);
                }
            }
        }
        
        @Override
        public int getAttributeCount() {
            return this.attributes.getAttributeCount();
        }
        
        @Override
        public boolean isDefined(final Object o) {
            return this.attributes.isDefined(o);
        }
        
        @Override
        public boolean isEqual(final AttributeSet set) {
            return this.attributes.isEqual(set);
        }
        
        @Override
        public AttributeSet copyAttributes() {
            return this.attributes.copyAttributes();
        }
        
        @Override
        public Object getAttribute(final Object o) {
            Object o2 = this.attributes.getAttribute(o);
            if (o2 == null) {
                final AttributeSet set = (this.parent != null) ? this.parent.getAttributes() : null;
                if (set != null) {
                    o2 = set.getAttribute(o);
                }
            }
            return o2;
        }
        
        @Override
        public Enumeration<?> getAttributeNames() {
            return this.attributes.getAttributeNames();
        }
        
        @Override
        public boolean containsAttribute(final Object o, final Object o2) {
            return this.attributes.containsAttribute(o, o2);
        }
        
        @Override
        public boolean containsAttributes(final AttributeSet set) {
            return this.attributes.containsAttributes(set);
        }
        
        @Override
        public AttributeSet getResolveParent() {
            AttributeSet set = this.attributes.getResolveParent();
            if (set == null && this.parent != null) {
                set = this.parent.getAttributes();
            }
            return set;
        }
        
        @Override
        public void addAttribute(final Object o, final Object o2) {
            this.checkForIllegalCast();
            this.attributes = AbstractDocument.this.getAttributeContext().addAttribute(this.attributes, o, o2);
        }
        
        @Override
        public void addAttributes(final AttributeSet set) {
            this.checkForIllegalCast();
            this.attributes = AbstractDocument.this.getAttributeContext().addAttributes(this.attributes, set);
        }
        
        @Override
        public void removeAttribute(final Object o) {
            this.checkForIllegalCast();
            this.attributes = AbstractDocument.this.getAttributeContext().removeAttribute(this.attributes, o);
        }
        
        @Override
        public void removeAttributes(final Enumeration<?> enumeration) {
            this.checkForIllegalCast();
            this.attributes = AbstractDocument.this.getAttributeContext().removeAttributes(this.attributes, enumeration);
        }
        
        @Override
        public void removeAttributes(final AttributeSet set) {
            this.checkForIllegalCast();
            final AttributeContext attributeContext = AbstractDocument.this.getAttributeContext();
            if (set == this) {
                this.attributes = attributeContext.getEmptySet();
            }
            else {
                this.attributes = attributeContext.removeAttributes(this.attributes, set);
            }
        }
        
        @Override
        public void setResolveParent(final AttributeSet set) {
            this.checkForIllegalCast();
            final AttributeContext attributeContext = AbstractDocument.this.getAttributeContext();
            if (set != null) {
                this.attributes = attributeContext.addAttribute(this.attributes, StyleConstants.ResolveAttribute, set);
            }
            else {
                this.attributes = attributeContext.removeAttribute(this.attributes, StyleConstants.ResolveAttribute);
            }
        }
        
        private final void checkForIllegalCast() {
            final Thread currentWriter = AbstractDocument.this.getCurrentWriter();
            if (currentWriter == null || currentWriter != Thread.currentThread()) {
                throw new StateInvariantError("Illegal cast to MutableAttributeSet");
            }
        }
        
        @Override
        public Document getDocument() {
            return AbstractDocument.this;
        }
        
        @Override
        public Element getParentElement() {
            return this.parent;
        }
        
        @Override
        public AttributeSet getAttributes() {
            return this;
        }
        
        @Override
        public String getName() {
            if (this.attributes.isDefined("$ename")) {
                return (String)this.attributes.getAttribute("$ename");
            }
            return null;
        }
        
        @Override
        public abstract int getStartOffset();
        
        @Override
        public abstract int getEndOffset();
        
        @Override
        public abstract Element getElement(final int p0);
        
        @Override
        public abstract int getElementCount();
        
        @Override
        public abstract int getElementIndex(final int p0);
        
        @Override
        public abstract boolean isLeaf();
        
        @Override
        public TreeNode getChildAt(final int n) {
            return (TreeNode)this.getElement(n);
        }
        
        @Override
        public int getChildCount() {
            return this.getElementCount();
        }
        
        @Override
        public TreeNode getParent() {
            return (TreeNode)this.getParentElement();
        }
        
        @Override
        public int getIndex(final TreeNode treeNode) {
            for (int i = this.getChildCount() - 1; i >= 0; --i) {
                if (this.getChildAt(i) == treeNode) {
                    return i;
                }
            }
            return -1;
        }
        
        @Override
        public abstract boolean getAllowsChildren();
        
        @Override
        public abstract Enumeration children();
        
        private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.defaultWriteObject();
            StyleContext.writeAttributeSet(objectOutputStream, this.attributes);
        }
        
        private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
            objectInputStream.defaultReadObject();
            final SimpleAttributeSet set = new SimpleAttributeSet();
            StyleContext.readAttributeSet(objectInputStream, set);
            this.attributes = AbstractDocument.this.getAttributeContext().addAttributes(SimpleAttributeSet.EMPTY, set);
        }
    }
    
    public class BranchElement extends AbstractElement
    {
        private AbstractElement[] children;
        private int nchildren;
        private int lastIndex;
        
        public BranchElement(final Element element, final AttributeSet set) {
            super(element, set);
            this.children = new AbstractElement[1];
            this.nchildren = 0;
            this.lastIndex = -1;
        }
        
        public Element positionToElement(final int n) {
            final AbstractElement abstractElement = this.children[this.getElementIndex(n)];
            final int startOffset = abstractElement.getStartOffset();
            final int endOffset = abstractElement.getEndOffset();
            if (n >= startOffset && n < endOffset) {
                return abstractElement;
            }
            return null;
        }
        
        public void replace(final int n, final int n2, final Element[] array) {
            final int n3 = array.length - n2;
            final int n4 = n + n2;
            final int n5 = this.nchildren - n4;
            final int n6 = n4 + n3;
            if (this.nchildren + n3 >= this.children.length) {
                final AbstractElement[] children = new AbstractElement[Math.max(2 * this.children.length, this.nchildren + n3)];
                System.arraycopy(this.children, 0, children, 0, n);
                System.arraycopy(array, 0, children, n, array.length);
                System.arraycopy(this.children, n4, children, n6, n5);
                this.children = children;
            }
            else {
                System.arraycopy(this.children, n4, this.children, n6, n5);
                System.arraycopy(array, 0, this.children, n, array.length);
            }
            this.nchildren += n3;
        }
        
        @Override
        public String toString() {
            return "BranchElement(" + this.getName() + ") " + this.getStartOffset() + "," + this.getEndOffset() + "\n";
        }
        
        @Override
        public String getName() {
            String name = super.getName();
            if (name == null) {
                name = "paragraph";
            }
            return name;
        }
        
        @Override
        public int getStartOffset() {
            return this.children[0].getStartOffset();
        }
        
        @Override
        public int getEndOffset() {
            return ((this.nchildren > 0) ? this.children[this.nchildren - 1] : this.children[0]).getEndOffset();
        }
        
        @Override
        public Element getElement(final int n) {
            if (n < this.nchildren) {
                return this.children[n];
            }
            return null;
        }
        
        @Override
        public int getElementCount() {
            return this.nchildren;
        }
        
        @Override
        public int getElementIndex(final int n) {
            int i = 0;
            int lastIndex = this.nchildren - 1;
            int lastIndex2 = 0;
            int n2 = this.getStartOffset();
            if (this.nchildren == 0) {
                return 0;
            }
            if (n >= this.getEndOffset()) {
                return this.nchildren - 1;
            }
            if (this.lastIndex >= i && this.lastIndex <= lastIndex) {
                final AbstractElement abstractElement = this.children[this.lastIndex];
                n2 = abstractElement.getStartOffset();
                final int endOffset = abstractElement.getEndOffset();
                if (n >= n2 && n < endOffset) {
                    return this.lastIndex;
                }
                if (n < n2) {
                    lastIndex = this.lastIndex;
                }
                else {
                    i = this.lastIndex;
                }
            }
            while (i <= lastIndex) {
                lastIndex2 = i + (lastIndex - i) / 2;
                final AbstractElement abstractElement2 = this.children[lastIndex2];
                n2 = abstractElement2.getStartOffset();
                final int endOffset2 = abstractElement2.getEndOffset();
                if (n >= n2 && n < endOffset2) {
                    return this.lastIndex = lastIndex2;
                }
                if (n < n2) {
                    lastIndex = lastIndex2 - 1;
                }
                else {
                    i = lastIndex2 + 1;
                }
            }
            int lastIndex3;
            if (n < n2) {
                lastIndex3 = lastIndex2;
            }
            else {
                lastIndex3 = lastIndex2 + 1;
            }
            return this.lastIndex = lastIndex3;
        }
        
        @Override
        public boolean isLeaf() {
            return false;
        }
        
        @Override
        public boolean getAllowsChildren() {
            return true;
        }
        
        @Override
        public Enumeration children() {
            if (this.nchildren == 0) {
                return null;
            }
            final Vector vector = new Vector(this.nchildren);
            for (int i = 0; i < this.nchildren; ++i) {
                vector.addElement(this.children[i]);
            }
            return vector.elements();
        }
    }
    
    public class LeafElement extends AbstractElement
    {
        private transient Position p0;
        private transient Position p1;
        
        public LeafElement(final Element element, final AttributeSet set, final int n, final int n2) {
            super(element, set);
            try {
                this.p0 = AbstractDocument.this.createPosition(n);
                this.p1 = AbstractDocument.this.createPosition(n2);
            }
            catch (final BadLocationException ex) {
                this.p0 = null;
                this.p1 = null;
                throw new StateInvariantError("Can't create Position references");
            }
        }
        
        @Override
        public String toString() {
            return "LeafElement(" + this.getName() + ") " + this.p0 + "," + this.p1 + "\n";
        }
        
        @Override
        public int getStartOffset() {
            return this.p0.getOffset();
        }
        
        @Override
        public int getEndOffset() {
            return this.p1.getOffset();
        }
        
        @Override
        public String getName() {
            String name = super.getName();
            if (name == null) {
                name = "content";
            }
            return name;
        }
        
        @Override
        public int getElementIndex(final int n) {
            return -1;
        }
        
        @Override
        public Element getElement(final int n) {
            return null;
        }
        
        @Override
        public int getElementCount() {
            return 0;
        }
        
        @Override
        public boolean isLeaf() {
            return true;
        }
        
        @Override
        public boolean getAllowsChildren() {
            return false;
        }
        
        @Override
        public Enumeration children() {
            return null;
        }
        
        private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.defaultWriteObject();
            objectOutputStream.writeInt(this.p0.getOffset());
            objectOutputStream.writeInt(this.p1.getOffset());
        }
        
        private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
            objectInputStream.defaultReadObject();
            final int int1 = objectInputStream.readInt();
            final int int2 = objectInputStream.readInt();
            try {
                this.p0 = AbstractDocument.this.createPosition(int1);
                this.p1 = AbstractDocument.this.createPosition(int2);
            }
            catch (final BadLocationException ex) {
                this.p0 = null;
                this.p1 = null;
                throw new IOException("Can't restore Position references");
            }
        }
    }
    
    class BidiRootElement extends BranchElement
    {
        BidiRootElement() {
            super(null, null);
        }
        
        @Override
        public String getName() {
            return "bidi root";
        }
    }
    
    class BidiElement extends LeafElement
    {
        BidiElement(final Element element, final int n, final int n2, final int n3) {
            super(element, new SimpleAttributeSet(), n, n2);
            this.addAttribute(StyleConstants.BidiLevel, n3);
        }
        
        @Override
        public String getName() {
            return "bidi level";
        }
        
        int getLevel() {
            final Integer n = (Integer)this.getAttribute(StyleConstants.BidiLevel);
            if (n != null) {
                return n;
            }
            return 0;
        }
        
        boolean isLeftToRight() {
            return this.getLevel() % 2 == 0;
        }
    }
    
    public class DefaultDocumentEvent extends CompoundEdit implements DocumentEvent
    {
        private int offset;
        private int length;
        private Hashtable<Element, ElementChange> changeLookup;
        private EventType type;
        
        public DefaultDocumentEvent(final int offset, final int length, final EventType type) {
            this.offset = offset;
            this.length = length;
            this.type = type;
        }
        
        @Override
        public String toString() {
            return this.edits.toString();
        }
        
        @Override
        public boolean addEdit(final UndoableEdit undoableEdit) {
            if (this.changeLookup == null && this.edits.size() > 10) {
                this.changeLookup = new Hashtable<Element, ElementChange>();
                for (int size = this.edits.size(), i = 0; i < size; ++i) {
                    final UndoableEdit element = this.edits.elementAt(i);
                    if (element instanceof ElementChange) {
                        final ElementChange elementChange = (ElementChange)element;
                        this.changeLookup.put(elementChange.getElement(), elementChange);
                    }
                }
            }
            if (this.changeLookup != null && undoableEdit instanceof ElementChange) {
                final ElementChange elementChange2 = (ElementChange)undoableEdit;
                this.changeLookup.put(elementChange2.getElement(), elementChange2);
            }
            return super.addEdit(undoableEdit);
        }
        
        @Override
        public void redo() throws CannotRedoException {
            AbstractDocument.this.writeLock();
            try {
                super.redo();
                final UndoRedoDocumentEvent undoRedoDocumentEvent = new UndoRedoDocumentEvent(this, false);
                if (this.type == EventType.INSERT) {
                    AbstractDocument.this.fireInsertUpdate(undoRedoDocumentEvent);
                }
                else if (this.type == EventType.REMOVE) {
                    AbstractDocument.this.fireRemoveUpdate(undoRedoDocumentEvent);
                }
                else {
                    AbstractDocument.this.fireChangedUpdate(undoRedoDocumentEvent);
                }
            }
            finally {
                AbstractDocument.this.writeUnlock();
            }
        }
        
        @Override
        public void undo() throws CannotUndoException {
            AbstractDocument.this.writeLock();
            try {
                super.undo();
                final UndoRedoDocumentEvent undoRedoDocumentEvent = new UndoRedoDocumentEvent(this, true);
                if (this.type == EventType.REMOVE) {
                    AbstractDocument.this.fireInsertUpdate(undoRedoDocumentEvent);
                }
                else if (this.type == EventType.INSERT) {
                    AbstractDocument.this.fireRemoveUpdate(undoRedoDocumentEvent);
                }
                else {
                    AbstractDocument.this.fireChangedUpdate(undoRedoDocumentEvent);
                }
            }
            finally {
                AbstractDocument.this.writeUnlock();
            }
        }
        
        @Override
        public boolean isSignificant() {
            return true;
        }
        
        @Override
        public String getPresentationName() {
            final EventType type = this.getType();
            if (type == EventType.INSERT) {
                return UIManager.getString("AbstractDocument.additionText");
            }
            if (type == EventType.REMOVE) {
                return UIManager.getString("AbstractDocument.deletionText");
            }
            return UIManager.getString("AbstractDocument.styleChangeText");
        }
        
        @Override
        public String getUndoPresentationName() {
            return UIManager.getString("AbstractDocument.undoText") + " " + this.getPresentationName();
        }
        
        @Override
        public String getRedoPresentationName() {
            return UIManager.getString("AbstractDocument.redoText") + " " + this.getPresentationName();
        }
        
        @Override
        public EventType getType() {
            return this.type;
        }
        
        @Override
        public int getOffset() {
            return this.offset;
        }
        
        @Override
        public int getLength() {
            return this.length;
        }
        
        @Override
        public Document getDocument() {
            return AbstractDocument.this;
        }
        
        @Override
        public ElementChange getChange(final Element element) {
            if (this.changeLookup != null) {
                return this.changeLookup.get(element);
            }
            for (int size = this.edits.size(), i = 0; i < size; ++i) {
                final UndoableEdit element2 = this.edits.elementAt(i);
                if (element2 instanceof ElementChange) {
                    final ElementChange elementChange = (ElementChange)element2;
                    if (element.equals(elementChange.getElement())) {
                        return elementChange;
                    }
                }
            }
            return null;
        }
    }
    
    class UndoRedoDocumentEvent implements DocumentEvent
    {
        private DefaultDocumentEvent src;
        private EventType type;
        
        public UndoRedoDocumentEvent(final DefaultDocumentEvent src, final boolean b) {
            this.src = null;
            this.type = null;
            this.src = src;
            if (b) {
                if (src.getType().equals(EventType.INSERT)) {
                    this.type = EventType.REMOVE;
                }
                else if (src.getType().equals(EventType.REMOVE)) {
                    this.type = EventType.INSERT;
                }
                else {
                    this.type = src.getType();
                }
            }
            else {
                this.type = src.getType();
            }
        }
        
        public DefaultDocumentEvent getSource() {
            return this.src;
        }
        
        @Override
        public int getOffset() {
            return this.src.getOffset();
        }
        
        @Override
        public int getLength() {
            return this.src.getLength();
        }
        
        @Override
        public Document getDocument() {
            return this.src.getDocument();
        }
        
        @Override
        public EventType getType() {
            return this.type;
        }
        
        @Override
        public ElementChange getChange(final Element element) {
            return this.src.getChange(element);
        }
    }
    
    public static class ElementEdit extends AbstractUndoableEdit implements DocumentEvent.ElementChange
    {
        private Element e;
        private int index;
        private Element[] removed;
        private Element[] added;
        
        public ElementEdit(final Element e, final int index, final Element[] removed, final Element[] added) {
            this.e = e;
            this.index = index;
            this.removed = removed;
            this.added = added;
        }
        
        @Override
        public Element getElement() {
            return this.e;
        }
        
        @Override
        public int getIndex() {
            return this.index;
        }
        
        @Override
        public Element[] getChildrenRemoved() {
            return this.removed;
        }
        
        @Override
        public Element[] getChildrenAdded() {
            return this.added;
        }
        
        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            final Element[] removed = this.removed;
            this.removed = this.added;
            this.added = removed;
            ((BranchElement)this.e).replace(this.index, this.removed.length, this.added);
        }
        
        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            ((BranchElement)this.e).replace(this.index, this.added.length, this.removed);
            final Element[] removed = this.removed;
            this.removed = this.added;
            this.added = removed;
        }
    }
    
    private class DefaultFilterBypass extends DocumentFilter.FilterBypass
    {
        @Override
        public Document getDocument() {
            return AbstractDocument.this;
        }
        
        @Override
        public void remove(final int n, final int n2) throws BadLocationException {
            AbstractDocument.this.handleRemove(n, n2);
        }
        
        @Override
        public void insertString(final int n, final String s, final AttributeSet set) throws BadLocationException {
            AbstractDocument.this.handleInsertString(n, s, set);
        }
        
        @Override
        public void replace(final int n, final int n2, final String s, final AttributeSet set) throws BadLocationException {
            AbstractDocument.this.handleRemove(n, n2);
            AbstractDocument.this.handleInsertString(n, s, set);
        }
    }
    
    public interface Content
    {
        Position createPosition(final int p0) throws BadLocationException;
        
        int length();
        
        UndoableEdit insertString(final int p0, final String p1) throws BadLocationException;
        
        UndoableEdit remove(final int p0, final int p1) throws BadLocationException;
        
        String getString(final int p0, final int p1) throws BadLocationException;
        
        void getChars(final int p0, final int p1, final Segment p2) throws BadLocationException;
    }
    
    public interface AttributeContext
    {
        AttributeSet addAttribute(final AttributeSet p0, final Object p1, final Object p2);
        
        AttributeSet addAttributes(final AttributeSet p0, final AttributeSet p1);
        
        AttributeSet removeAttribute(final AttributeSet p0, final Object p1);
        
        AttributeSet removeAttributes(final AttributeSet p0, final Enumeration<?> p1);
        
        AttributeSet removeAttributes(final AttributeSet p0, final AttributeSet p1);
        
        AttributeSet getEmptySet();
        
        void reclaim(final AttributeSet p0);
    }
}
