package javax.swing.text;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import javax.swing.event.ChangeEvent;
import java.lang.ref.ReferenceQueue;
import java.util.Map;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.AbstractUndoableEdit;
import java.util.ArrayList;
import java.util.Stack;
import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Iterator;
import javax.swing.event.DocumentListener;
import javax.swing.SwingUtilities;
import java.awt.Font;
import java.awt.Color;
import java.awt.font.TextAttribute;
import java.util.Enumeration;
import javax.swing.undo.UndoableEdit;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ChangeListener;
import java.util.Vector;

public class DefaultStyledDocument extends AbstractDocument implements StyledDocument
{
    public static final int BUFFER_SIZE_DEFAULT = 4096;
    protected ElementBuffer buffer;
    private transient Vector<Style> listeningStyles;
    private transient ChangeListener styleChangeListener;
    private transient ChangeListener styleContextChangeListener;
    private transient ChangeUpdateRunnable updateRunnable;
    
    public DefaultStyledDocument(final Content content, final StyleContext styleContext) {
        super(content, styleContext);
        this.listeningStyles = new Vector<Style>();
        this.buffer = new ElementBuffer(this.createDefaultRoot());
        this.setLogicalStyle(0, styleContext.getStyle("default"));
    }
    
    public DefaultStyledDocument(final StyleContext styleContext) {
        this(new GapContent(4096), styleContext);
    }
    
    public DefaultStyledDocument() {
        this(new GapContent(4096), new StyleContext());
    }
    
    @Override
    public Element getDefaultRootElement() {
        return this.buffer.getRootElement();
    }
    
    protected void create(final ElementSpec[] array) {
        try {
            if (this.getLength() != 0) {
                this.remove(0, this.getLength());
            }
            this.writeLock();
            final Content content = this.getContent();
            final int length = array.length;
            final StringBuilder sb = new StringBuilder();
            for (final ElementSpec elementSpec : array) {
                if (elementSpec.getLength() > 0) {
                    sb.append(elementSpec.getArray(), elementSpec.getOffset(), elementSpec.getLength());
                }
            }
            final UndoableEdit insertString = content.insertString(0, sb.toString());
            final int length2 = sb.length();
            final DefaultDocumentEvent defaultDocumentEvent = new DefaultDocumentEvent(0, length2, DocumentEvent.EventType.INSERT);
            defaultDocumentEvent.addEdit(insertString);
            this.buffer.create(length2, array, defaultDocumentEvent);
            super.insertUpdate(defaultDocumentEvent, null);
            defaultDocumentEvent.end();
            this.fireInsertUpdate(defaultDocumentEvent);
            this.fireUndoableEditUpdate(new UndoableEditEvent(this, defaultDocumentEvent));
        }
        catch (final BadLocationException ex) {
            throw new StateInvariantError("problem initializing");
        }
        finally {
            this.writeUnlock();
        }
    }
    
    protected void insert(final int n, final ElementSpec[] array) throws BadLocationException {
        if (array == null || array.length == 0) {
            return;
        }
        try {
            this.writeLock();
            final Content content = this.getContent();
            final int length = array.length;
            final StringBuilder sb = new StringBuilder();
            for (final ElementSpec elementSpec : array) {
                if (elementSpec.getLength() > 0) {
                    sb.append(elementSpec.getArray(), elementSpec.getOffset(), elementSpec.getLength());
                }
            }
            if (sb.length() == 0) {
                return;
            }
            final UndoableEdit insertString = content.insertString(n, sb.toString());
            final int length2 = sb.length();
            final DefaultDocumentEvent defaultDocumentEvent = new DefaultDocumentEvent(n, length2, DocumentEvent.EventType.INSERT);
            defaultDocumentEvent.addEdit(insertString);
            this.buffer.insert(n, length2, array, defaultDocumentEvent);
            super.insertUpdate(defaultDocumentEvent, null);
            defaultDocumentEvent.end();
            this.fireInsertUpdate(defaultDocumentEvent);
            this.fireUndoableEditUpdate(new UndoableEditEvent(this, defaultDocumentEvent));
        }
        finally {
            this.writeUnlock();
        }
    }
    
    public void removeElement(final Element element) {
        try {
            this.writeLock();
            this.removeElementImpl(element);
        }
        finally {
            this.writeUnlock();
        }
    }
    
    private void removeElementImpl(Element element) {
        if (element.getDocument() != this) {
            throw new IllegalArgumentException("element doesn't belong to document");
        }
        BranchElement branchElement = (BranchElement)element.getParentElement();
        if (branchElement == null) {
            throw new IllegalArgumentException("can't remove the root element");
        }
        int startOffset;
        final int n = startOffset = element.getStartOffset();
        int endOffset = element.getEndOffset();
        int n2 = this.getLength() + 1;
        final Content content = this.getContent();
        boolean b = false;
        final boolean composedTextElement = Utilities.isComposedTextElement(element);
        if (endOffset >= n2) {
            if (n <= 0) {
                throw new IllegalArgumentException("can't remove the whole content");
            }
            endOffset = n2 - 1;
            try {
                if (content.getString(n - 1, 1).charAt(0) == '\n') {
                    --startOffset;
                }
            }
            catch (final BadLocationException ex) {
                throw new IllegalStateException(ex);
            }
            b = true;
        }
        final int n3 = endOffset - startOffset;
        final DefaultDocumentEvent defaultDocumentEvent = new DefaultDocumentEvent(startOffset, n3, DocumentEvent.EventType.REMOVE);
        UndoableEdit remove = null;
        while (branchElement.getElementCount() == 1) {
            element = branchElement;
            branchElement = (BranchElement)branchElement.getParentElement();
            if (branchElement == null) {
                throw new IllegalStateException("invalid element structure");
            }
        }
        final Element[] array = { element };
        final Element[] array2 = new Element[0];
        final int elementIndex = branchElement.getElementIndex(n);
        branchElement.replace(elementIndex, 1, array2);
        defaultDocumentEvent.addEdit(new ElementEdit(branchElement, elementIndex, array, array2));
        if (n3 > 0) {
            try {
                remove = content.remove(startOffset, n3);
                if (remove != null) {
                    defaultDocumentEvent.addEdit(remove);
                }
            }
            catch (final BadLocationException ex2) {
                throw new IllegalStateException(ex2);
            }
            n2 -= n3;
        }
        if (b) {
            Element element2;
            for (element2 = branchElement.getElement(branchElement.getElementCount() - 1); element2 != null && !element2.isLeaf(); element2 = element2.getElement(element2.getElementCount() - 1)) {}
            if (element2 == null) {
                throw new IllegalStateException("invalid element structure");
            }
            final int startOffset2 = element2.getStartOffset();
            final BranchElement branchElement2 = (BranchElement)element2.getParentElement();
            final int elementIndex2 = branchElement2.getElementIndex(startOffset2);
            final Element leafElement = this.createLeafElement(branchElement2, element2.getAttributes(), startOffset2, n2);
            final Element[] array3 = { element2 };
            final Element[] array4 = { leafElement };
            branchElement2.replace(elementIndex2, 1, array4);
            defaultDocumentEvent.addEdit(new ElementEdit(branchElement2, elementIndex2, array3, array4));
        }
        this.postRemoveUpdate(defaultDocumentEvent);
        defaultDocumentEvent.end();
        this.fireRemoveUpdate(defaultDocumentEvent);
        if (!composedTextElement || remove == null) {
            this.fireUndoableEditUpdate(new UndoableEditEvent(this, defaultDocumentEvent));
        }
    }
    
    @Override
    public Style addStyle(final String s, final Style style) {
        return ((StyleContext)this.getAttributeContext()).addStyle(s, style);
    }
    
    @Override
    public void removeStyle(final String s) {
        ((StyleContext)this.getAttributeContext()).removeStyle(s);
    }
    
    @Override
    public Style getStyle(final String s) {
        return ((StyleContext)this.getAttributeContext()).getStyle(s);
    }
    
    public Enumeration<?> getStyleNames() {
        return ((StyleContext)this.getAttributeContext()).getStyleNames();
    }
    
    @Override
    public void setLogicalStyle(final int n, final Style resolveParent) {
        final Element paragraphElement = this.getParagraphElement(n);
        if (paragraphElement != null && paragraphElement instanceof AbstractElement) {
            try {
                this.writeLock();
                final StyleChangeUndoableEdit styleChangeUndoableEdit = new StyleChangeUndoableEdit((AbstractElement)paragraphElement, resolveParent);
                ((AbstractElement)paragraphElement).setResolveParent(resolveParent);
                final int startOffset = paragraphElement.getStartOffset();
                final DefaultDocumentEvent defaultDocumentEvent = new DefaultDocumentEvent(startOffset, paragraphElement.getEndOffset() - startOffset, DocumentEvent.EventType.CHANGE);
                defaultDocumentEvent.addEdit(styleChangeUndoableEdit);
                defaultDocumentEvent.end();
                this.fireChangedUpdate(defaultDocumentEvent);
                this.fireUndoableEditUpdate(new UndoableEditEvent(this, defaultDocumentEvent));
            }
            finally {
                this.writeUnlock();
            }
        }
    }
    
    @Override
    public Style getLogicalStyle(final int n) {
        Style style = null;
        final Element paragraphElement = this.getParagraphElement(n);
        if (paragraphElement != null) {
            final AttributeSet resolveParent = paragraphElement.getAttributes().getResolveParent();
            if (resolveParent instanceof Style) {
                style = (Style)resolveParent;
            }
        }
        return style;
    }
    
    @Override
    public void setCharacterAttributes(final int n, final int n2, final AttributeSet set, final boolean b) {
        if (n2 == 0) {
            return;
        }
        try {
            this.writeLock();
            final DefaultDocumentEvent defaultDocumentEvent = new DefaultDocumentEvent(n, n2, DocumentEvent.EventType.CHANGE);
            this.buffer.change(n, n2, defaultDocumentEvent);
            final AttributeSet copyAttributes = set.copyAttributes();
            int endOffset;
            for (int i = n; i < n + n2; i = endOffset) {
                final Element characterElement = this.getCharacterElement(i);
                endOffset = characterElement.getEndOffset();
                if (i == endOffset) {
                    break;
                }
                final MutableAttributeSet set2 = (MutableAttributeSet)characterElement.getAttributes();
                defaultDocumentEvent.addEdit(new AttributeUndoableEdit(characterElement, copyAttributes, b));
                if (b) {
                    set2.removeAttributes(set2);
                }
                set2.addAttributes(set);
            }
            defaultDocumentEvent.end();
            this.fireChangedUpdate(defaultDocumentEvent);
            this.fireUndoableEditUpdate(new UndoableEditEvent(this, defaultDocumentEvent));
        }
        finally {
            this.writeUnlock();
        }
    }
    
    @Override
    public void setParagraphAttributes(final int n, final int n2, final AttributeSet set, final boolean b) {
        try {
            this.writeLock();
            final DefaultDocumentEvent defaultDocumentEvent = new DefaultDocumentEvent(n, n2, DocumentEvent.EventType.CHANGE);
            final AttributeSet copyAttributes = set.copyAttributes();
            final Element defaultRootElement = this.getDefaultRootElement();
            final int elementIndex = defaultRootElement.getElementIndex(n);
            final int elementIndex2 = defaultRootElement.getElementIndex(n + ((n2 > 0) ? (n2 - 1) : 0));
            final boolean equals = Boolean.TRUE.equals(this.getProperty("i18n"));
            boolean b2 = false;
            for (int i = elementIndex; i <= elementIndex2; ++i) {
                final Element element = defaultRootElement.getElement(i);
                final MutableAttributeSet set2 = (MutableAttributeSet)element.getAttributes();
                defaultDocumentEvent.addEdit(new AttributeUndoableEdit(element, copyAttributes, b));
                if (b) {
                    set2.removeAttributes(set2);
                }
                set2.addAttributes(set);
                if (equals && !b2) {
                    b2 = (set2.getAttribute(TextAttribute.RUN_DIRECTION) != null);
                }
            }
            if (b2) {
                this.updateBidi(defaultDocumentEvent);
            }
            defaultDocumentEvent.end();
            this.fireChangedUpdate(defaultDocumentEvent);
            this.fireUndoableEditUpdate(new UndoableEditEvent(this, defaultDocumentEvent));
        }
        finally {
            this.writeUnlock();
        }
    }
    
    @Override
    public Element getParagraphElement(final int n) {
        Element element;
        for (element = this.getDefaultRootElement(); !element.isLeaf(); element = element.getElement(element.getElementIndex(n))) {}
        if (element != null) {
            return element.getParentElement();
        }
        return element;
    }
    
    @Override
    public Element getCharacterElement(final int n) {
        Element element;
        for (element = this.getDefaultRootElement(); !element.isLeaf(); element = element.getElement(element.getElementIndex(n))) {}
        return element;
    }
    
    @Override
    protected void insertUpdate(final DefaultDocumentEvent defaultDocumentEvent, AttributeSet empty) {
        final int offset = defaultDocumentEvent.getOffset();
        final int length = defaultDocumentEvent.getLength();
        if (empty == null) {
            empty = SimpleAttributeSet.EMPTY;
        }
        final Element paragraphElement = this.getParagraphElement(offset + length);
        AttributeSet set = paragraphElement.getAttributes();
        final Element paragraphElement2 = this.getParagraphElement(offset);
        final Element element = paragraphElement2.getElement(paragraphElement2.getElementIndex(offset));
        final int n = offset + length;
        final boolean b = element.getEndOffset() == n;
        final AttributeSet attributes = element.getAttributes();
        try {
            final Segment segment = new Segment();
            final Vector vector = new Vector<ElementSpec>();
            ElementSpec elementSpec = null;
            boolean b2 = false;
            short specsForInsertAfterNewline = 6;
            if (offset > 0) {
                this.getText(offset - 1, 1, segment);
                if (segment.array[segment.offset] == '\n') {
                    b2 = true;
                    specsForInsertAfterNewline = this.createSpecsForInsertAfterNewline(paragraphElement, paragraphElement2, set, vector, offset, n);
                    for (int i = vector.size() - 1; i >= 0; --i) {
                        final ElementSpec elementSpec2 = vector.elementAt(i);
                        if (elementSpec2.getType() == 1) {
                            elementSpec = elementSpec2;
                            break;
                        }
                    }
                }
            }
            if (!b2) {
                set = paragraphElement2.getAttributes();
            }
            this.getText(offset, length, segment);
            final char[] array = segment.array;
            final int n2 = segment.offset + segment.count;
            int offset2 = segment.offset;
            for (int j = segment.offset; j < n2; ++j) {
                if (array[j] == '\n') {
                    final int n3 = j + 1;
                    vector.addElement(new ElementSpec(empty, (short)3, n3 - offset2));
                    vector.addElement(new ElementSpec(null, (short)2));
                    elementSpec = new ElementSpec(set, (short)1);
                    vector.addElement(elementSpec);
                    offset2 = n3;
                }
            }
            if (offset2 < n2) {
                vector.addElement(new ElementSpec(empty, (short)3, n2 - offset2));
            }
            final ElementSpec elementSpec3 = vector.firstElement();
            final int length2 = this.getLength();
            if (elementSpec3.getType() == 3 && attributes.isEqual(empty)) {
                elementSpec3.setDirection((short)4);
            }
            if (elementSpec != null) {
                if (b2) {
                    elementSpec.setDirection(specsForInsertAfterNewline);
                }
                else if (paragraphElement2.getEndOffset() != n) {
                    elementSpec.setDirection((short)7);
                }
                else {
                    final Element parentElement = paragraphElement2.getParentElement();
                    final int elementIndex = parentElement.getElementIndex(offset);
                    if (elementIndex + 1 < parentElement.getElementCount() && !parentElement.getElement(elementIndex + 1).isLeaf()) {
                        elementSpec.setDirection((short)5);
                    }
                }
            }
            if (b && n < length2) {
                final ElementSpec elementSpec4 = vector.lastElement();
                if (elementSpec4.getType() == 3 && elementSpec4.getDirection() != 4 && ((elementSpec == null && (paragraphElement == paragraphElement2 || b2)) || (elementSpec != null && elementSpec.getDirection() != 6))) {
                    final Element element2 = paragraphElement.getElement(paragraphElement.getElementIndex(n));
                    if (element2.isLeaf() && empty.isEqual(element2.getAttributes())) {
                        elementSpec4.setDirection((short)5);
                    }
                }
            }
            else if (!b && elementSpec != null && elementSpec.getDirection() == 7) {
                final ElementSpec elementSpec5 = vector.lastElement();
                if (elementSpec5.getType() == 3 && elementSpec5.getDirection() != 4 && empty.isEqual(attributes)) {
                    elementSpec5.setDirection((short)5);
                }
            }
            if (Utilities.isComposedTextAttributeDefined(empty)) {
                final MutableAttributeSet set2 = (MutableAttributeSet)empty;
                set2.addAttributes(attributes);
                set2.addAttribute("$ename", "content");
                set2.addAttribute(StyleConstants.NameAttribute, "content");
                if (set2.isDefined("CR")) {
                    set2.removeAttribute("CR");
                }
            }
            final ElementSpec[] array2 = new ElementSpec[vector.size()];
            vector.copyInto(array2);
            this.buffer.insert(offset, length, array2, defaultDocumentEvent);
        }
        catch (final BadLocationException ex) {}
        super.insertUpdate(defaultDocumentEvent, empty);
    }
    
    short createSpecsForInsertAfterNewline(final Element element, final Element element2, final AttributeSet set, final Vector<ElementSpec> vector, final int n, final int n2) {
        if (element.getParentElement() == element2.getParentElement()) {
            vector.addElement(new ElementSpec(set, (short)2));
            vector.addElement(new ElementSpec(set, (short)1));
            if (element2.getEndOffset() != n2) {
                return 7;
            }
            final Element parentElement = element2.getParentElement();
            if (parentElement.getElementIndex(n) + 1 < parentElement.getElementCount()) {
                return 5;
            }
        }
        else {
            final Vector vector2 = new Vector();
            final Vector vector3 = new Vector();
            for (Element parentElement2 = element2; parentElement2 != null; parentElement2 = parentElement2.getParentElement()) {
                vector2.addElement(parentElement2);
            }
            Element parentElement3 = element;
            int index = -1;
            while (parentElement3 != null && (index = vector2.indexOf(parentElement3)) == -1) {
                vector3.addElement(parentElement3);
                parentElement3 = parentElement3.getParentElement();
            }
            if (parentElement3 != null) {
                for (int i = 0; i < index; ++i) {
                    vector.addElement(new ElementSpec(null, (short)2));
                }
                for (int j = vector3.size() - 1; j >= 0; --j) {
                    final ElementSpec elementSpec = new ElementSpec(vector3.elementAt(j).getAttributes(), (short)1);
                    if (j > 0) {
                        elementSpec.setDirection((short)5);
                    }
                    vector.addElement(elementSpec);
                }
                if (vector3.size() > 0) {
                    return 5;
                }
                return 7;
            }
        }
        return 6;
    }
    
    @Override
    protected void removeUpdate(final DefaultDocumentEvent defaultDocumentEvent) {
        super.removeUpdate(defaultDocumentEvent);
        this.buffer.remove(defaultDocumentEvent.getOffset(), defaultDocumentEvent.getLength(), defaultDocumentEvent);
    }
    
    protected AbstractElement createDefaultRoot() {
        this.writeLock();
        final SectionElement sectionElement = new SectionElement();
        final BranchElement branchElement = new BranchElement(sectionElement, null);
        final Element[] array = { new LeafElement(branchElement, null, 0, 1) };
        branchElement.replace(0, 0, array);
        array[0] = branchElement;
        sectionElement.replace(0, 0, array);
        this.writeUnlock();
        return sectionElement;
    }
    
    @Override
    public Color getForeground(final AttributeSet set) {
        return ((StyleContext)this.getAttributeContext()).getForeground(set);
    }
    
    @Override
    public Color getBackground(final AttributeSet set) {
        return ((StyleContext)this.getAttributeContext()).getBackground(set);
    }
    
    @Override
    public Font getFont(final AttributeSet set) {
        return ((StyleContext)this.getAttributeContext()).getFont(set);
    }
    
    protected void styleChanged(final Style style) {
        if (this.getLength() != 0) {
            if (this.updateRunnable == null) {
                this.updateRunnable = new ChangeUpdateRunnable();
            }
            synchronized (this.updateRunnable) {
                if (!this.updateRunnable.isPending) {
                    SwingUtilities.invokeLater(this.updateRunnable);
                    this.updateRunnable.isPending = true;
                }
            }
        }
    }
    
    @Override
    public void addDocumentListener(final DocumentListener documentListener) {
        synchronized (this.listeningStyles) {
            final int listenerCount = this.listenerList.getListenerCount(DocumentListener.class);
            super.addDocumentListener(documentListener);
            if (listenerCount == 0) {
                if (this.styleContextChangeListener == null) {
                    this.styleContextChangeListener = this.createStyleContextChangeListener();
                }
                if (this.styleContextChangeListener != null) {
                    final StyleContext styleContext = (StyleContext)this.getAttributeContext();
                    final Iterator<ChangeListener> iterator = AbstractChangeHandler.getStaleListeners(this.styleContextChangeListener).iterator();
                    while (iterator.hasNext()) {
                        styleContext.removeChangeListener(iterator.next());
                    }
                    styleContext.addChangeListener(this.styleContextChangeListener);
                }
                this.updateStylesListeningTo();
            }
        }
    }
    
    @Override
    public void removeDocumentListener(final DocumentListener documentListener) {
        synchronized (this.listeningStyles) {
            super.removeDocumentListener(documentListener);
            if (this.listenerList.getListenerCount(DocumentListener.class) == 0) {
                for (int i = this.listeningStyles.size() - 1; i >= 0; --i) {
                    this.listeningStyles.elementAt(i).removeChangeListener(this.styleChangeListener);
                }
                this.listeningStyles.removeAllElements();
                if (this.styleContextChangeListener != null) {
                    ((StyleContext)this.getAttributeContext()).removeChangeListener(this.styleContextChangeListener);
                }
            }
        }
    }
    
    ChangeListener createStyleChangeListener() {
        return new StyleChangeHandler(this);
    }
    
    ChangeListener createStyleContextChangeListener() {
        return new StyleContextChangeHandler(this);
    }
    
    void updateStylesListeningTo() {
        synchronized (this.listeningStyles) {
            final StyleContext styleContext = (StyleContext)this.getAttributeContext();
            if (this.styleChangeListener == null) {
                this.styleChangeListener = this.createStyleChangeListener();
            }
            if (this.styleChangeListener != null && styleContext != null) {
                final Enumeration<?> styleNames = styleContext.getStyleNames();
                final Vector vector = (Vector)this.listeningStyles.clone();
                this.listeningStyles.removeAllElements();
                final List<ChangeListener> staleListeners = AbstractChangeHandler.getStaleListeners(this.styleChangeListener);
                while (styleNames.hasMoreElements()) {
                    final Style style = styleContext.getStyle((String)styleNames.nextElement());
                    final int index = vector.indexOf(style);
                    this.listeningStyles.addElement(style);
                    if (index == -1) {
                        final Iterator<ChangeListener> iterator = staleListeners.iterator();
                        while (iterator.hasNext()) {
                            style.removeChangeListener(iterator.next());
                        }
                        style.addChangeListener(this.styleChangeListener);
                    }
                    else {
                        vector.removeElementAt(index);
                    }
                }
                for (int i = vector.size() - 1; i >= 0; --i) {
                    ((Style)vector.elementAt(i)).removeChangeListener(this.styleChangeListener);
                }
                if (this.listeningStyles.size() == 0) {
                    this.styleChangeListener = null;
                }
            }
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        this.listeningStyles = new Vector<Style>();
        objectInputStream.defaultReadObject();
        if (this.styleContextChangeListener == null && this.listenerList.getListenerCount(DocumentListener.class) > 0) {
            this.styleContextChangeListener = this.createStyleContextChangeListener();
            if (this.styleContextChangeListener != null) {
                ((StyleContext)this.getAttributeContext()).addChangeListener(this.styleContextChangeListener);
            }
            this.updateStylesListeningTo();
        }
    }
    
    protected class SectionElement extends BranchElement
    {
        public SectionElement() {
            super(null, null);
        }
        
        @Override
        public String getName() {
            return "section";
        }
    }
    
    public static class ElementSpec
    {
        public static final short StartTagType = 1;
        public static final short EndTagType = 2;
        public static final short ContentType = 3;
        public static final short JoinPreviousDirection = 4;
        public static final short JoinNextDirection = 5;
        public static final short OriginateDirection = 6;
        public static final short JoinFractureDirection = 7;
        private AttributeSet attr;
        private int len;
        private short type;
        private short direction;
        private int offs;
        private char[] data;
        
        public ElementSpec(final AttributeSet set, final short n) {
            this(set, n, null, 0, 0);
        }
        
        public ElementSpec(final AttributeSet set, final short n, final int n2) {
            this(set, n, null, 0, n2);
        }
        
        public ElementSpec(final AttributeSet attr, final short type, final char[] data, final int offs, final int len) {
            this.attr = attr;
            this.type = type;
            this.data = data;
            this.offs = offs;
            this.len = len;
            this.direction = 6;
        }
        
        public void setType(final short type) {
            this.type = type;
        }
        
        public short getType() {
            return this.type;
        }
        
        public void setDirection(final short direction) {
            this.direction = direction;
        }
        
        public short getDirection() {
            return this.direction;
        }
        
        public AttributeSet getAttributes() {
            return this.attr;
        }
        
        public char[] getArray() {
            return this.data;
        }
        
        public int getOffset() {
            return this.offs;
        }
        
        public int getLength() {
            return this.len;
        }
        
        @Override
        public String toString() {
            String s = "??";
            String s2 = "??";
            switch (this.type) {
                case 1: {
                    s = "StartTag";
                    break;
                }
                case 3: {
                    s = "Content";
                    break;
                }
                case 2: {
                    s = "EndTag";
                    break;
                }
            }
            switch (this.direction) {
                case 4: {
                    s2 = "JoinPrevious";
                    break;
                }
                case 5: {
                    s2 = "JoinNext";
                    break;
                }
                case 6: {
                    s2 = "Originate";
                    break;
                }
                case 7: {
                    s2 = "Fracture";
                    break;
                }
            }
            return s + ":" + s2 + ":" + this.getLength();
        }
    }
    
    public class ElementBuffer implements Serializable
    {
        Element root;
        transient int pos;
        transient int offset;
        transient int length;
        transient int endOffset;
        transient Vector<ElemChanges> changes;
        transient Stack<ElemChanges> path;
        transient boolean insertOp;
        transient boolean recreateLeafs;
        transient ElemChanges[] insertPath;
        transient boolean createdFracture;
        transient Element fracturedParent;
        transient Element fracturedChild;
        transient boolean offsetLastIndex;
        transient boolean offsetLastIndexOnReplace;
        
        public ElementBuffer(final Element root) {
            this.root = root;
            this.changes = new Vector<ElemChanges>();
            this.path = new Stack<ElemChanges>();
        }
        
        public Element getRootElement() {
            return this.root;
        }
        
        public void insert(final int n, final int n2, final ElementSpec[] array, final DefaultDocumentEvent defaultDocumentEvent) {
            if (n2 == 0) {
                return;
            }
            this.insertOp = true;
            this.beginEdits(n, n2);
            this.insertUpdate(array);
            this.endEdits(defaultDocumentEvent);
            this.insertOp = false;
        }
        
        void create(final int n, final ElementSpec[] array, final DefaultDocumentEvent defaultDocumentEvent) {
            this.insertOp = true;
            this.beginEdits(this.offset, n);
            Element root = this.root;
            int n2 = root.getElementIndex(0);
            while (!root.isLeaf()) {
                final Element element = root.getElement(n2);
                this.push(root, n2);
                root = element;
                n2 = root.getElementIndex(0);
            }
            final ElemChanges elemChanges = this.path.peek();
            final Element element2 = elemChanges.parent.getElement(elemChanges.index);
            elemChanges.added.addElement(DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element2.getAttributes(), DefaultStyledDocument.this.getLength(), element2.getEndOffset()));
            elemChanges.removed.addElement(element2);
            while (this.path.size() > 1) {
                this.pop();
            }
            final int length = array.length;
            AttributeSet set = null;
            if (length > 0 && array[0].getType() == 1) {
                set = array[0].getAttributes();
            }
            if (set == null) {
                set = SimpleAttributeSet.EMPTY;
            }
            final MutableAttributeSet set2 = (MutableAttributeSet)this.root.getAttributes();
            defaultDocumentEvent.addEdit(new AttributeUndoableEdit(this.root, set, true));
            set2.removeAttributes(set2);
            set2.addAttributes(set);
            for (int i = 1; i < length; ++i) {
                this.insertElement(array[i]);
            }
            while (this.path.size() != 0) {
                this.pop();
            }
            this.endEdits(defaultDocumentEvent);
            this.insertOp = false;
        }
        
        public void remove(final int n, final int n2, final DefaultDocumentEvent defaultDocumentEvent) {
            this.beginEdits(n, n2);
            this.removeUpdate();
            this.endEdits(defaultDocumentEvent);
        }
        
        public void change(final int n, final int n2, final DefaultDocumentEvent defaultDocumentEvent) {
            this.beginEdits(n, n2);
            this.changeUpdate();
            this.endEdits(defaultDocumentEvent);
        }
        
        protected void insertUpdate(final ElementSpec[] array) {
            Element root = this.root;
            int n = root.getElementIndex(this.offset);
            while (!root.isLeaf()) {
                final Element element = root.getElement(n);
                this.push(root, element.isLeaf() ? n : (n + 1));
                root = element;
                n = root.getElementIndex(this.offset);
            }
            this.insertPath = new ElemChanges[this.path.size()];
            this.path.copyInto(this.insertPath);
            this.createdFracture = false;
            this.recreateLeafs = false;
            int i;
            if (array[0].getType() == 3) {
                this.insertFirstContent(array);
                this.pos += array[0].getLength();
                i = 1;
            }
            else {
                this.fractureDeepestLeaf(array);
                i = 0;
            }
            while (i < array.length) {
                this.insertElement(array[i]);
                ++i;
            }
            if (!this.createdFracture) {
                this.fracture(-1);
            }
            while (this.path.size() != 0) {
                this.pop();
            }
            if (this.offsetLastIndex && this.offsetLastIndexOnReplace) {
                final ElemChanges elemChanges = this.insertPath[this.insertPath.length - 1];
                ++elemChanges.index;
            }
            for (int j = this.insertPath.length - 1; j >= 0; --j) {
                final ElemChanges elemChanges2 = this.insertPath[j];
                if (elemChanges2.parent == this.fracturedParent) {
                    elemChanges2.added.addElement(this.fracturedChild);
                }
                if ((elemChanges2.added.size() > 0 || elemChanges2.removed.size() > 0) && !this.changes.contains(elemChanges2)) {
                    this.changes.addElement(elemChanges2);
                }
            }
            if (this.offset == 0 && this.fracturedParent != null && array[0].getType() == 2) {
                int n2;
                for (n2 = 0; n2 < array.length && array[n2].getType() == 2; ++n2) {}
                final ElemChanges elemChanges3 = this.insertPath[this.insertPath.length - n2 - 1];
                final Vector<Element> removed = elemChanges3.removed;
                final Element parent = elemChanges3.parent;
                final ElemChanges elemChanges4 = elemChanges3;
                final int index = elemChanges4.index - 1;
                elemChanges4.index = index;
                removed.insertElementAt(parent.getElement(index), 0);
            }
        }
        
        protected void removeUpdate() {
            this.removeElements(this.root, this.offset, this.offset + this.length);
        }
        
        protected void changeUpdate() {
            if (!this.split(this.offset, this.length)) {
                while (this.path.size() != 0) {
                    this.pop();
                }
                this.split(this.offset + this.length, 0);
            }
            while (this.path.size() != 0) {
                this.pop();
            }
        }
        
        boolean split(final int pos, final int n) {
            boolean b = false;
            Element element = this.root;
            for (int n2 = element.getElementIndex(pos); !element.isLeaf(); element = element.getElement(n2), n2 = element.getElementIndex(pos)) {
                this.push(element, n2);
            }
            final ElemChanges elemChanges = this.path.peek();
            final Element element2 = elemChanges.parent.getElement(elemChanges.index);
            if (element2.getStartOffset() < pos && pos < element2.getEndOffset()) {
                int n4;
                final int n3 = n4 = elemChanges.index;
                if (pos + n < elemChanges.parent.getEndOffset() && n != 0) {
                    n4 = elemChanges.parent.getElementIndex(pos + n);
                    if (n4 == n3) {
                        elemChanges.removed.addElement(element2);
                        elemChanges.added.addElement(DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element2.getAttributes(), element2.getStartOffset(), pos));
                        elemChanges.added.addElement(DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element2.getAttributes(), pos, pos + n));
                        elemChanges.added.addElement(DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element2.getAttributes(), pos + n, element2.getEndOffset()));
                        return true;
                    }
                    if (pos + n == elemChanges.parent.getElement(n4).getStartOffset()) {
                        n4 = n3;
                    }
                    b = true;
                }
                this.pos = pos;
                final Element element3 = elemChanges.parent.getElement(n3);
                elemChanges.removed.addElement(element3);
                elemChanges.added.addElement(DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element3.getAttributes(), element3.getStartOffset(), this.pos));
                elemChanges.added.addElement(DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element3.getAttributes(), this.pos, element3.getEndOffset()));
                for (int i = n3 + 1; i < n4; ++i) {
                    final Element element4 = elemChanges.parent.getElement(i);
                    elemChanges.removed.addElement(element4);
                    elemChanges.added.addElement(element4);
                }
                if (n4 != n3) {
                    final Element element5 = elemChanges.parent.getElement(n4);
                    this.pos = pos + n;
                    elemChanges.removed.addElement(element5);
                    elemChanges.added.addElement(DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element5.getAttributes(), element5.getStartOffset(), this.pos));
                    elemChanges.added.addElement(DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element5.getAttributes(), this.pos, element5.getEndOffset()));
                }
            }
            return b;
        }
        
        void endEdits(final DefaultDocumentEvent defaultDocumentEvent) {
            for (int size = this.changes.size(), i = 0; i < size; ++i) {
                final ElemChanges elemChanges = this.changes.elementAt(i);
                final Element[] array = new Element[elemChanges.removed.size()];
                elemChanges.removed.copyInto(array);
                final Element[] array2 = new Element[elemChanges.added.size()];
                elemChanges.added.copyInto(array2);
                final int index = elemChanges.index;
                ((BranchElement)elemChanges.parent).replace(index, array.length, array2);
                defaultDocumentEvent.addEdit(new ElementEdit(elemChanges.parent, index, array, array2));
            }
            this.changes.removeAllElements();
            this.path.removeAllElements();
        }
        
        void beginEdits(final int n, final int length) {
            this.offset = n;
            this.length = length;
            this.endOffset = n + length;
            this.pos = n;
            if (this.changes == null) {
                this.changes = new Vector<ElemChanges>();
            }
            else {
                this.changes.removeAllElements();
            }
            if (this.path == null) {
                this.path = new Stack<ElemChanges>();
            }
            else {
                this.path.removeAllElements();
            }
            this.fracturedParent = null;
            this.fracturedChild = null;
            final boolean b = false;
            this.offsetLastIndexOnReplace = b;
            this.offsetLastIndex = b;
        }
        
        void push(final Element element, final int n, final boolean b) {
            this.path.push(new ElemChanges(element, n, b));
        }
        
        void push(final Element element, final int n) {
            this.push(element, n, false);
        }
        
        void pop() {
            final ElemChanges elemChanges = this.path.peek();
            this.path.pop();
            if (elemChanges.added.size() > 0 || elemChanges.removed.size() > 0) {
                this.changes.addElement(elemChanges);
            }
            else if (!this.path.isEmpty()) {
                final Element parent = elemChanges.parent;
                if (parent.getElementCount() == 0) {
                    this.path.peek().added.removeElement(parent);
                }
            }
        }
        
        void advance(final int n) {
            this.pos += n;
        }
        
        void insertElement(final ElementSpec elementSpec) {
            final ElemChanges elemChanges = this.path.peek();
            switch (elementSpec.getType()) {
                case 1: {
                    switch (elementSpec.getDirection()) {
                        case 5: {
                            Element element = elemChanges.parent.getElement(elemChanges.index);
                            if (element.isLeaf()) {
                                if (elemChanges.index + 1 >= elemChanges.parent.getElementCount()) {
                                    throw new StateInvariantError("Join next to leaf");
                                }
                                element = elemChanges.parent.getElement(elemChanges.index + 1);
                            }
                            this.push(element, 0, true);
                            break;
                        }
                        case 7: {
                            if (!this.createdFracture) {
                                this.fracture(this.path.size() - 1);
                            }
                            if (!elemChanges.isFracture) {
                                this.push(this.fracturedChild, 0, true);
                                break;
                            }
                            this.push(elemChanges.parent.getElement(0), 0, true);
                            break;
                        }
                        default: {
                            final Element branchElement = DefaultStyledDocument.this.createBranchElement(elemChanges.parent, elementSpec.getAttributes());
                            elemChanges.added.addElement(branchElement);
                            this.push(branchElement, 0);
                            break;
                        }
                    }
                    break;
                }
                case 2: {
                    this.pop();
                    break;
                }
                case 3: {
                    final int length = elementSpec.getLength();
                    if (elementSpec.getDirection() != 5) {
                        elemChanges.added.addElement(DefaultStyledDocument.this.createLeafElement(elemChanges.parent, elementSpec.getAttributes(), this.pos, this.pos + length));
                    }
                    else if (!elemChanges.isFracture) {
                        Element element2 = null;
                        if (this.insertPath != null) {
                            int i = this.insertPath.length - 1;
                            while (i >= 0) {
                                if (this.insertPath[i] == elemChanges) {
                                    if (i != this.insertPath.length - 1) {
                                        element2 = elemChanges.parent.getElement(elemChanges.index);
                                        break;
                                    }
                                    break;
                                }
                                else {
                                    --i;
                                }
                            }
                        }
                        if (element2 == null) {
                            element2 = elemChanges.parent.getElement(elemChanges.index + 1);
                        }
                        elemChanges.added.addElement(DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element2.getAttributes(), this.pos, element2.getEndOffset()));
                        elemChanges.removed.addElement(element2);
                    }
                    else {
                        final Element element3 = elemChanges.parent.getElement(0);
                        elemChanges.added.addElement(DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element3.getAttributes(), this.pos, element3.getEndOffset()));
                        elemChanges.removed.addElement(element3);
                    }
                    this.pos += length;
                    break;
                }
            }
        }
        
        boolean removeElements(final Element element, final int n, final int n2) {
            if (!element.isLeaf()) {
                final int elementIndex = element.getElementIndex(n);
                final int elementIndex2 = element.getElementIndex(n2);
                this.push(element, elementIndex);
                final ElemChanges elemChanges = this.path.peek();
                if (elementIndex == elementIndex2) {
                    final Element element2 = element.getElement(elementIndex);
                    if (n <= element2.getStartOffset() && n2 >= element2.getEndOffset()) {
                        elemChanges.removed.addElement(element2);
                    }
                    else if (this.removeElements(element2, n, n2)) {
                        elemChanges.removed.addElement(element2);
                    }
                }
                else {
                    Element element3 = element.getElement(elementIndex);
                    Element element4 = element.getElement(elementIndex2);
                    final boolean b = n2 < element.getEndOffset();
                    if (b && this.canJoin(element3, element4)) {
                        for (int i = elementIndex; i <= elementIndex2; ++i) {
                            elemChanges.removed.addElement(element.getElement(i));
                        }
                        elemChanges.added.addElement(this.join(element, element3, element4, n, n2));
                    }
                    else {
                        int index = elementIndex + 1;
                        int n3 = elementIndex2 - 1;
                        if (element3.getStartOffset() == n || (elementIndex == 0 && element3.getStartOffset() > n && element3.getEndOffset() <= n2)) {
                            element3 = null;
                            index = elementIndex;
                        }
                        if (!b) {
                            element4 = null;
                            ++n3;
                        }
                        else if (element4.getStartOffset() == n2) {
                            element4 = null;
                        }
                        if (index <= n3) {
                            elemChanges.index = index;
                        }
                        for (int j = index; j <= n3; ++j) {
                            elemChanges.removed.addElement(element.getElement(j));
                        }
                        if (element3 != null && this.removeElements(element3, n, n2)) {
                            elemChanges.removed.insertElementAt(element3, 0);
                            elemChanges.index = elementIndex;
                        }
                        if (element4 != null && this.removeElements(element4, n, n2)) {
                            elemChanges.removed.addElement(element4);
                        }
                    }
                }
                this.pop();
                if (element.getElementCount() == elemChanges.removed.size() - elemChanges.added.size()) {
                    return true;
                }
            }
            return false;
        }
        
        boolean canJoin(final Element element, final Element element2) {
            if (element == null || element2 == null) {
                return false;
            }
            final boolean leaf = element.isLeaf();
            if (leaf != element2.isLeaf()) {
                return false;
            }
            if (leaf) {
                return element.getAttributes().isEqual(element2.getAttributes());
            }
            final String name = element.getName();
            final String name2 = element2.getName();
            if (name != null) {
                return name.equals(name2);
            }
            return name2 == null || name2.equals(name);
        }
        
        Element join(final Element element, final Element element2, final Element element3, final int n, final int n2) {
            if (element2.isLeaf() && element3.isLeaf()) {
                return DefaultStyledDocument.this.createLeafElement(element, element2.getAttributes(), element2.getStartOffset(), element3.getEndOffset());
            }
            if (!element2.isLeaf() && !element3.isLeaf()) {
                final Element branchElement = DefaultStyledDocument.this.createBranchElement(element, element2.getAttributes());
                final int elementIndex = element2.getElementIndex(n);
                final int elementIndex2 = element3.getElementIndex(n2);
                Element element4 = element2.getElement(elementIndex);
                if (element4.getStartOffset() >= n) {
                    element4 = null;
                }
                Element element5 = element3.getElement(elementIndex2);
                if (element5.getStartOffset() == n2) {
                    element5 = null;
                }
                final Vector<Element> vector = new Vector<Element>();
                for (int i = 0; i < elementIndex; ++i) {
                    vector.addElement(this.clone(branchElement, element2.getElement(i)));
                }
                if (this.canJoin(element4, element5)) {
                    vector.addElement(this.join(branchElement, element4, element5, n, n2));
                }
                else {
                    if (element4 != null) {
                        vector.addElement(this.cloneAsNecessary(branchElement, element4, n, n2));
                    }
                    if (element5 != null) {
                        vector.addElement(this.cloneAsNecessary(branchElement, element5, n, n2));
                    }
                }
                for (int elementCount = element3.getElementCount(), j = (element5 == null) ? elementIndex2 : (elementIndex2 + 1); j < elementCount; ++j) {
                    vector.addElement(this.clone(branchElement, element3.getElement(j)));
                }
                final Element[] array = new Element[vector.size()];
                vector.copyInto(array);
                ((BranchElement)branchElement).replace(0, 0, array);
                return branchElement;
            }
            throw new StateInvariantError("No support to join leaf element with non-leaf element");
        }
        
        public Element clone(final Element element, final Element element2) {
            if (element2.isLeaf()) {
                return DefaultStyledDocument.this.createLeafElement(element, element2.getAttributes(), element2.getStartOffset(), element2.getEndOffset());
            }
            final Element branchElement = DefaultStyledDocument.this.createBranchElement(element, element2.getAttributes());
            final int elementCount = element2.getElementCount();
            final Element[] array = new Element[elementCount];
            for (int i = 0; i < elementCount; ++i) {
                array[i] = this.clone(branchElement, element2.getElement(i));
            }
            ((BranchElement)branchElement).replace(0, 0, array);
            return branchElement;
        }
        
        Element cloneAsNecessary(final Element element, final Element element2, final int n, final int n2) {
            if (element2.isLeaf()) {
                return DefaultStyledDocument.this.createLeafElement(element, element2.getAttributes(), element2.getStartOffset(), element2.getEndOffset());
            }
            final Element branchElement = DefaultStyledDocument.this.createBranchElement(element, element2.getAttributes());
            final int elementCount = element2.getElementCount();
            final ArrayList list = new ArrayList<Element>(elementCount);
            for (int i = 0; i < elementCount; ++i) {
                final Element element3 = element2.getElement(i);
                if (element3.getStartOffset() < n || element3.getEndOffset() > n2) {
                    list.add(this.cloneAsNecessary(branchElement, element3, n, n2));
                }
            }
            ((BranchElement)branchElement).replace(0, 0, list.toArray(new Element[list.size()]));
            return branchElement;
        }
        
        void fracture(final int n) {
            final int length = this.insertPath.length;
            int n2 = -1;
            int recreateLeafs = this.recreateLeafs ? 1 : 0;
            final ElemChanges elemChanges = this.insertPath[length - 1];
            int n3 = (elemChanges.index + 1 < elemChanges.parent.getElementCount()) ? 1 : 0;
            int n4 = (recreateLeafs != 0) ? length : -1;
            int n5 = length - 1;
            this.createdFracture = true;
            for (int i = length - 2; i >= 0; --i) {
                final ElemChanges elemChanges2 = this.insertPath[i];
                if (elemChanges2.added.size() > 0 || i == n) {
                    n2 = i;
                    if (recreateLeafs == 0 && n3 != 0) {
                        recreateLeafs = 1;
                        if (n4 == -1) {
                            n4 = n5 + 1;
                        }
                    }
                }
                if (n3 == 0 && elemChanges2.index < elemChanges2.parent.getElementCount()) {
                    n3 = 1;
                    n5 = i;
                }
            }
            if (recreateLeafs != 0) {
                if (n2 == -1) {
                    n2 = length - 1;
                }
                this.fractureFrom(this.insertPath, n2, n4);
            }
        }
        
        void fractureFrom(final ElemChanges[] array, int n, final int n2) {
            final ElemChanges elemChanges = array[n];
            final int length = array.length;
            Element element;
            if (n + 1 == length) {
                element = elemChanges.parent.getElement(elemChanges.index);
            }
            else {
                element = elemChanges.parent.getElement(elemChanges.index - 1);
            }
            Element fracturedChild;
            if (element.isLeaf()) {
                fracturedChild = DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element.getAttributes(), Math.max(this.endOffset, element.getStartOffset()), element.getEndOffset());
            }
            else {
                fracturedChild = DefaultStyledDocument.this.createBranchElement(elemChanges.parent, element.getAttributes());
            }
            this.fracturedParent = elemChanges.parent;
            this.fracturedChild = fracturedChild;
            Element element2 = fracturedChild;
            while (++n < n2) {
                final boolean b = n + 1 == n2;
                final boolean b2 = n + 1 == length;
                final ElemChanges elemChanges2 = array[n];
                Element element3;
                if (b) {
                    if (this.offsetLastIndex || !b2) {
                        element3 = null;
                    }
                    else {
                        element3 = elemChanges2.parent.getElement(elemChanges2.index);
                    }
                }
                else {
                    element3 = elemChanges2.parent.getElement(elemChanges2.index - 1);
                }
                Element element4;
                if (element3 != null) {
                    if (element3.isLeaf()) {
                        element4 = DefaultStyledDocument.this.createLeafElement(element2, element3.getAttributes(), Math.max(this.endOffset, element3.getStartOffset()), element3.getEndOffset());
                    }
                    else {
                        element4 = DefaultStyledDocument.this.createBranchElement(element2, element3.getAttributes());
                    }
                }
                else {
                    element4 = null;
                }
                int n3 = elemChanges2.parent.getElementCount() - elemChanges2.index;
                int n4 = 1;
                int n5;
                Element[] array2;
                if (element4 == null) {
                    if (b2) {
                        --n3;
                        n5 = elemChanges2.index + 1;
                    }
                    else {
                        n5 = elemChanges2.index;
                    }
                    n4 = 0;
                    array2 = new Element[n3];
                }
                else {
                    if (!b) {
                        ++n3;
                        n5 = elemChanges2.index;
                    }
                    else {
                        n5 = elemChanges2.index + 1;
                    }
                    array2 = new Element[n3];
                    array2[0] = element4;
                }
                for (int i = n4; i < n3; ++i) {
                    final Element element5 = elemChanges2.parent.getElement(n5++);
                    array2[i] = this.recreateFracturedElement(element2, element5);
                    elemChanges2.removed.addElement(element5);
                }
                ((BranchElement)element2).replace(0, 0, array2);
                element2 = element4;
            }
        }
        
        Element recreateFracturedElement(final Element element, final Element element2) {
            if (element2.isLeaf()) {
                return DefaultStyledDocument.this.createLeafElement(element, element2.getAttributes(), Math.max(element2.getStartOffset(), this.endOffset), element2.getEndOffset());
            }
            final Element branchElement = DefaultStyledDocument.this.createBranchElement(element, element2.getAttributes());
            final int elementCount = element2.getElementCount();
            final Element[] array = new Element[elementCount];
            for (int i = 0; i < elementCount; ++i) {
                array[i] = this.recreateFracturedElement(branchElement, element2.getElement(i));
            }
            ((BranchElement)branchElement).replace(0, 0, array);
            return branchElement;
        }
        
        void fractureDeepestLeaf(final ElementSpec[] array) {
            final ElemChanges elemChanges = this.path.peek();
            final Element element = elemChanges.parent.getElement(elemChanges.index);
            if (this.offset != 0) {
                elemChanges.added.addElement(DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element.getAttributes(), element.getStartOffset(), this.offset));
            }
            elemChanges.removed.addElement(element);
            if (element.getEndOffset() != this.endOffset) {
                this.recreateLeafs = true;
            }
            else {
                this.offsetLastIndex = true;
            }
        }
        
        void insertFirstContent(final ElementSpec[] array) {
            final ElementSpec elementSpec = array[0];
            final ElemChanges elemChanges = this.path.peek();
            final Element element = elemChanges.parent.getElement(elemChanges.index);
            final int n = this.offset + elementSpec.getLength();
            final boolean b = array.length == 1;
            switch (elementSpec.getDirection()) {
                case 4: {
                    if (element.getEndOffset() != n && !b) {
                        elemChanges.added.addElement(DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element.getAttributes(), element.getStartOffset(), n));
                        elemChanges.removed.addElement(element);
                        if (element.getEndOffset() != this.endOffset) {
                            this.recreateLeafs = true;
                        }
                        else {
                            this.offsetLastIndex = true;
                        }
                        break;
                    }
                    this.offsetLastIndex = true;
                    this.offsetLastIndexOnReplace = true;
                    break;
                }
                case 5: {
                    if (this.offset != 0) {
                        elemChanges.added.addElement(DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element.getAttributes(), element.getStartOffset(), this.offset));
                        final Element element2 = elemChanges.parent.getElement(elemChanges.index + 1);
                        Element element3;
                        if (b) {
                            element3 = DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element2.getAttributes(), this.offset, element2.getEndOffset());
                        }
                        else {
                            element3 = DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element2.getAttributes(), this.offset, n);
                        }
                        elemChanges.added.addElement(element3);
                        elemChanges.removed.addElement(element);
                        elemChanges.removed.addElement(element2);
                        break;
                    }
                    break;
                }
                default: {
                    if (element.getStartOffset() != this.offset) {
                        elemChanges.added.addElement(DefaultStyledDocument.this.createLeafElement(elemChanges.parent, element.getAttributes(), element.getStartOffset(), this.offset));
                    }
                    elemChanges.removed.addElement(element);
                    elemChanges.added.addElement(DefaultStyledDocument.this.createLeafElement(elemChanges.parent, elementSpec.getAttributes(), this.offset, n));
                    if (element.getEndOffset() != this.endOffset) {
                        this.recreateLeafs = true;
                        break;
                    }
                    this.offsetLastIndex = true;
                    break;
                }
            }
        }
        
        class ElemChanges
        {
            Element parent;
            int index;
            Vector<Element> added;
            Vector<Element> removed;
            boolean isFracture;
            
            ElemChanges(final Element parent, final int index, final boolean isFracture) {
                this.parent = parent;
                this.index = index;
                this.isFracture = isFracture;
                this.added = new Vector<Element>();
                this.removed = new Vector<Element>();
            }
            
            @Override
            public String toString() {
                return "added: " + this.added + "\nremoved: " + this.removed + "\n";
            }
        }
    }
    
    public static class AttributeUndoableEdit extends AbstractUndoableEdit
    {
        protected AttributeSet newAttributes;
        protected AttributeSet copy;
        protected boolean isReplacing;
        protected Element element;
        
        public AttributeUndoableEdit(final Element element, final AttributeSet newAttributes, final boolean isReplacing) {
            this.element = element;
            this.newAttributes = newAttributes;
            this.isReplacing = isReplacing;
            this.copy = element.getAttributes().copyAttributes();
        }
        
        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            final MutableAttributeSet set = (MutableAttributeSet)this.element.getAttributes();
            if (this.isReplacing) {
                set.removeAttributes(set);
            }
            set.addAttributes(this.newAttributes);
        }
        
        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            final MutableAttributeSet set = (MutableAttributeSet)this.element.getAttributes();
            set.removeAttributes(set);
            set.addAttributes(this.copy);
        }
    }
    
    static class StyleChangeUndoableEdit extends AbstractUndoableEdit
    {
        protected AbstractElement element;
        protected Style newStyle;
        protected AttributeSet oldStyle;
        
        public StyleChangeUndoableEdit(final AbstractElement element, final Style newStyle) {
            this.element = element;
            this.newStyle = newStyle;
            this.oldStyle = element.getResolveParent();
        }
        
        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            this.element.setResolveParent(this.newStyle);
        }
        
        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            this.element.setResolveParent(this.oldStyle);
        }
    }
    
    abstract static class AbstractChangeHandler implements ChangeListener
    {
        private static final Map<Class, ReferenceQueue<DefaultStyledDocument>> queueMap;
        private DocReference doc;
        
        AbstractChangeHandler(final DefaultStyledDocument defaultStyledDocument) {
            final Class<? extends AbstractChangeHandler> class1 = this.getClass();
            ReferenceQueue referenceQueue;
            synchronized (AbstractChangeHandler.queueMap) {
                referenceQueue = AbstractChangeHandler.queueMap.get(class1);
                if (referenceQueue == null) {
                    referenceQueue = new ReferenceQueue();
                    AbstractChangeHandler.queueMap.put(class1, referenceQueue);
                }
            }
            this.doc = defaultStyledDocument.new DocReference(referenceQueue);
        }
        
        static List<ChangeListener> getStaleListeners(final ChangeListener changeListener) {
            final ArrayList list = new ArrayList();
            final ReferenceQueue referenceQueue = AbstractChangeHandler.queueMap.get(changeListener.getClass());
            if (referenceQueue != null) {
                synchronized (referenceQueue) {
                    DocReference docReference;
                    while ((docReference = (DocReference)referenceQueue.poll()) != null) {
                        list.add(docReference.getListener());
                    }
                }
            }
            return list;
        }
        
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            final DefaultStyledDocument defaultStyledDocument = this.doc.get();
            if (defaultStyledDocument != null) {
                this.fireStateChanged(defaultStyledDocument, changeEvent);
            }
        }
        
        abstract void fireStateChanged(final DefaultStyledDocument p0, final ChangeEvent p1);
        
        static {
            queueMap = new HashMap<Class, ReferenceQueue<DefaultStyledDocument>>();
        }
        
        private class DocReference extends WeakReference<DefaultStyledDocument>
        {
            DocReference(final DefaultStyledDocument defaultStyledDocument, final ReferenceQueue<DefaultStyledDocument> referenceQueue) {
                super(defaultStyledDocument, referenceQueue);
            }
            
            ChangeListener getListener() {
                return AbstractChangeHandler.this;
            }
        }
    }
    
    static class StyleChangeHandler extends AbstractChangeHandler
    {
        StyleChangeHandler(final DefaultStyledDocument defaultStyledDocument) {
            super(defaultStyledDocument);
        }
        
        @Override
        void fireStateChanged(final DefaultStyledDocument defaultStyledDocument, final ChangeEvent changeEvent) {
            final Object source = changeEvent.getSource();
            if (source instanceof Style) {
                defaultStyledDocument.styleChanged((Style)source);
            }
            else {
                defaultStyledDocument.styleChanged(null);
            }
        }
    }
    
    static class StyleContextChangeHandler extends AbstractChangeHandler
    {
        StyleContextChangeHandler(final DefaultStyledDocument defaultStyledDocument) {
            super(defaultStyledDocument);
        }
        
        @Override
        void fireStateChanged(final DefaultStyledDocument defaultStyledDocument, final ChangeEvent changeEvent) {
            defaultStyledDocument.updateStylesListeningTo();
        }
    }
    
    class ChangeUpdateRunnable implements Runnable
    {
        boolean isPending;
        
        ChangeUpdateRunnable() {
            this.isPending = false;
        }
        
        @Override
        public void run() {
            synchronized (this) {
                this.isPending = false;
            }
            try {
                DefaultStyledDocument.this.writeLock();
                final DefaultDocumentEvent defaultDocumentEvent = new DefaultDocumentEvent(0, DefaultStyledDocument.this.getLength(), DocumentEvent.EventType.CHANGE);
                defaultDocumentEvent.end();
                DefaultStyledDocument.this.fireChangedUpdate(defaultDocumentEvent);
            }
            finally {
                DefaultStyledDocument.this.writeUnlock();
            }
        }
    }
}
