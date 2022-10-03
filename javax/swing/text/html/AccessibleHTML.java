package javax.swing.text.html;

import javax.swing.event.DocumentEvent;
import java.util.Iterator;
import javax.swing.text.Position;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import javax.accessibility.AccessibleTable;
import javax.accessibility.AccessibleIcon;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.text.CharacterIterator;
import java.text.BreakIterator;
import javax.swing.text.Segment;
import javax.swing.text.StyledDocument;
import javax.swing.text.PlainDocument;
import java.awt.Container;
import javax.swing.text.BadLocationException;
import java.awt.Shape;
import javax.accessibility.AccessibleText;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Cursor;
import java.awt.Color;
import java.awt.IllegalComponentStateException;
import java.util.Locale;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleContext;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.Element;
import javax.swing.text.AbstractDocument;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;
import java.beans.PropertyChangeListener;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.JEditorPane;
import javax.accessibility.Accessible;

class AccessibleHTML implements Accessible
{
    private JEditorPane editor;
    private Document model;
    private DocumentListener docListener;
    private PropertyChangeListener propChangeListener;
    private ElementInfo rootElementInfo;
    private RootHTMLAccessibleContext rootHTMLAccessibleContext;
    
    public AccessibleHTML(final JEditorPane editor) {
        this.editor = editor;
        this.propChangeListener = new PropertyChangeHandler();
        this.setDocument(this.editor.getDocument());
        this.docListener = new DocumentHandler();
    }
    
    private void setDocument(final Document model) {
        if (this.model != null) {
            this.model.removeDocumentListener(this.docListener);
        }
        if (this.editor != null) {
            this.editor.removePropertyChangeListener(this.propChangeListener);
        }
        this.model = model;
        if (this.model != null) {
            if (this.rootElementInfo != null) {
                this.rootElementInfo.invalidate(false);
            }
            this.buildInfo();
            this.model.addDocumentListener(this.docListener);
        }
        else {
            this.rootElementInfo = null;
        }
        if (this.editor != null) {
            this.editor.addPropertyChangeListener(this.propChangeListener);
        }
    }
    
    private Document getDocument() {
        return this.model;
    }
    
    private JEditorPane getTextComponent() {
        return this.editor;
    }
    
    private ElementInfo getRootInfo() {
        return this.rootElementInfo;
    }
    
    private View getRootView() {
        return this.getTextComponent().getUI().getRootView(this.getTextComponent());
    }
    
    private Rectangle getRootEditorRect() {
        final Rectangle bounds = this.getTextComponent().getBounds();
        if (bounds.width > 0 && bounds.height > 0) {
            final Rectangle rectangle = bounds;
            final Rectangle rectangle2 = bounds;
            final int n = 0;
            rectangle2.y = n;
            rectangle.x = n;
            final Insets insets = this.editor.getInsets();
            final Rectangle rectangle3 = bounds;
            rectangle3.x += insets.left;
            final Rectangle rectangle4 = bounds;
            rectangle4.y += insets.top;
            final Rectangle rectangle5 = bounds;
            rectangle5.width -= insets.left + insets.right;
            final Rectangle rectangle6 = bounds;
            rectangle6.height -= insets.top + insets.bottom;
            return bounds;
        }
        return null;
    }
    
    private Object lock() {
        final Document document = this.getDocument();
        if (document instanceof AbstractDocument) {
            ((AbstractDocument)document).readLock();
            return document;
        }
        return null;
    }
    
    private void unlock(final Object o) {
        if (o != null) {
            ((AbstractDocument)o).readUnlock();
        }
    }
    
    private void buildInfo() {
        final Object lock = this.lock();
        try {
            (this.rootElementInfo = new ElementInfo(this.getDocument().getDefaultRootElement())).validate();
        }
        finally {
            this.unlock(lock);
        }
    }
    
    ElementInfo createElementInfo(final Element element, final ElementInfo elementInfo) {
        final AttributeSet attributes = element.getAttributes();
        if (attributes != null) {
            final Object attribute = attributes.getAttribute(StyleConstants.NameAttribute);
            if (attribute == HTML.Tag.IMG) {
                return new IconElementInfo(element, elementInfo);
            }
            if (attribute == HTML.Tag.CONTENT || attribute == HTML.Tag.CAPTION) {
                return new TextElementInfo(element, elementInfo);
            }
            if (attribute == HTML.Tag.TABLE) {
                return new TableElementInfo(element, elementInfo);
            }
        }
        return null;
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.rootHTMLAccessibleContext == null) {
            this.rootHTMLAccessibleContext = new RootHTMLAccessibleContext(this.rootElementInfo);
        }
        return this.rootHTMLAccessibleContext;
    }
    
    private class RootHTMLAccessibleContext extends HTMLAccessibleContext
    {
        public RootHTMLAccessibleContext(final ElementInfo elementInfo) {
            super(elementInfo);
        }
        
        @Override
        public String getAccessibleName() {
            if (AccessibleHTML.this.model != null) {
                return (String)AccessibleHTML.this.model.getProperty("title");
            }
            return null;
        }
        
        @Override
        public String getAccessibleDescription() {
            return AccessibleHTML.this.editor.getContentType();
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.TEXT;
        }
    }
    
    protected abstract class HTMLAccessibleContext extends AccessibleContext implements Accessible, AccessibleComponent
    {
        protected ElementInfo elementInfo;
        
        public HTMLAccessibleContext(final ElementInfo elementInfo) {
            this.elementInfo = elementInfo;
        }
        
        @Override
        public AccessibleContext getAccessibleContext() {
            return this;
        }
        
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            final AccessibleStateSet set = new AccessibleStateSet();
            final JEditorPane access$400 = AccessibleHTML.this.getTextComponent();
            if (access$400.isEnabled()) {
                set.add(AccessibleState.ENABLED);
            }
            if (access$400 instanceof JTextComponent && access$400.isEditable()) {
                set.add(AccessibleState.EDITABLE);
                set.add(AccessibleState.FOCUSABLE);
            }
            if (access$400.isVisible()) {
                set.add(AccessibleState.VISIBLE);
            }
            if (access$400.isShowing()) {
                set.add(AccessibleState.SHOWING);
            }
            return set;
        }
        
        @Override
        public int getAccessibleIndexInParent() {
            return this.elementInfo.getIndexInParent();
        }
        
        @Override
        public int getAccessibleChildrenCount() {
            return this.elementInfo.getChildCount();
        }
        
        @Override
        public Accessible getAccessibleChild(final int n) {
            final ElementInfo child = this.elementInfo.getChild(n);
            if (child != null && child instanceof Accessible) {
                return (Accessible)child;
            }
            return null;
        }
        
        @Override
        public Locale getLocale() throws IllegalComponentStateException {
            return AccessibleHTML.this.editor.getLocale();
        }
        
        @Override
        public AccessibleComponent getAccessibleComponent() {
            return this;
        }
        
        @Override
        public Color getBackground() {
            return AccessibleHTML.this.getTextComponent().getBackground();
        }
        
        @Override
        public void setBackground(final Color background) {
            AccessibleHTML.this.getTextComponent().setBackground(background);
        }
        
        @Override
        public Color getForeground() {
            return AccessibleHTML.this.getTextComponent().getForeground();
        }
        
        @Override
        public void setForeground(final Color foreground) {
            AccessibleHTML.this.getTextComponent().setForeground(foreground);
        }
        
        @Override
        public Cursor getCursor() {
            return AccessibleHTML.this.getTextComponent().getCursor();
        }
        
        @Override
        public void setCursor(final Cursor cursor) {
            AccessibleHTML.this.getTextComponent().setCursor(cursor);
        }
        
        @Override
        public Font getFont() {
            return AccessibleHTML.this.getTextComponent().getFont();
        }
        
        @Override
        public void setFont(final Font font) {
            AccessibleHTML.this.getTextComponent().setFont(font);
        }
        
        @Override
        public FontMetrics getFontMetrics(final Font font) {
            return AccessibleHTML.this.getTextComponent().getFontMetrics(font);
        }
        
        @Override
        public boolean isEnabled() {
            return AccessibleHTML.this.getTextComponent().isEnabled();
        }
        
        @Override
        public void setEnabled(final boolean enabled) {
            AccessibleHTML.this.getTextComponent().setEnabled(enabled);
        }
        
        @Override
        public boolean isVisible() {
            return AccessibleHTML.this.getTextComponent().isVisible();
        }
        
        @Override
        public void setVisible(final boolean visible) {
            AccessibleHTML.this.getTextComponent().setVisible(visible);
        }
        
        @Override
        public boolean isShowing() {
            return AccessibleHTML.this.getTextComponent().isShowing();
        }
        
        @Override
        public boolean contains(final Point point) {
            final Rectangle bounds = this.getBounds();
            return bounds != null && bounds.contains(point.x, point.y);
        }
        
        @Override
        public Point getLocationOnScreen() {
            final Point locationOnScreen = AccessibleHTML.this.getTextComponent().getLocationOnScreen();
            final Rectangle bounds = this.getBounds();
            if (bounds != null) {
                return new Point(locationOnScreen.x + bounds.x, locationOnScreen.y + bounds.y);
            }
            return null;
        }
        
        @Override
        public Point getLocation() {
            final Rectangle bounds = this.getBounds();
            if (bounds != null) {
                return new Point(bounds.x, bounds.y);
            }
            return null;
        }
        
        @Override
        public void setLocation(final Point point) {
        }
        
        @Override
        public Rectangle getBounds() {
            return this.elementInfo.getBounds();
        }
        
        @Override
        public void setBounds(final Rectangle rectangle) {
        }
        
        @Override
        public Dimension getSize() {
            final Rectangle bounds = this.getBounds();
            if (bounds != null) {
                return new Dimension(bounds.width, bounds.height);
            }
            return null;
        }
        
        @Override
        public void setSize(final Dimension size) {
            AccessibleHTML.this.getTextComponent().setSize(size);
        }
        
        @Override
        public Accessible getAccessibleAt(final Point point) {
            final ElementInfo elementInfo = this.getElementInfoAt(AccessibleHTML.this.rootElementInfo, point);
            if (elementInfo instanceof Accessible) {
                return (Accessible)elementInfo;
            }
            return null;
        }
        
        private ElementInfo getElementInfoAt(final ElementInfo elementInfo, final Point point) {
            if (elementInfo.getBounds() == null) {
                return null;
            }
            if (elementInfo.getChildCount() == 0 && elementInfo.getBounds().contains(point)) {
                return elementInfo;
            }
            if (elementInfo instanceof TableElementInfo) {
                final ElementInfo captionInfo = ((TableElementInfo)elementInfo).getCaptionInfo();
                if (captionInfo != null) {
                    final Rectangle bounds = captionInfo.getBounds();
                    if (bounds != null && bounds.contains(point)) {
                        return captionInfo;
                    }
                }
            }
            for (int i = 0; i < elementInfo.getChildCount(); ++i) {
                final ElementInfo elementInfo2 = this.getElementInfoAt(elementInfo.getChild(i), point);
                if (elementInfo2 != null) {
                    return elementInfo2;
                }
            }
            return null;
        }
        
        @Override
        public boolean isFocusTraversable() {
            final JEditorPane access$400 = AccessibleHTML.this.getTextComponent();
            return access$400 instanceof JTextComponent && access$400.isEditable();
        }
        
        @Override
        public void requestFocus() {
            if (!this.isFocusTraversable()) {
                return;
            }
            final JEditorPane access$400 = AccessibleHTML.this.getTextComponent();
            if (access$400 instanceof JTextComponent) {
                access$400.requestFocusInWindow();
                try {
                    if (this.elementInfo.validateIfNecessary()) {
                        access$400.setCaretPosition(this.elementInfo.getElement().getStartOffset());
                        AccessibleHTML.this.editor.getAccessibleContext().firePropertyChange("AccessibleState", null, new PropertyChangeEvent(this, "AccessibleState", null, AccessibleState.FOCUSED));
                    }
                }
                catch (final IllegalArgumentException ex) {}
            }
        }
        
        @Override
        public void addFocusListener(final FocusListener focusListener) {
            AccessibleHTML.this.getTextComponent().addFocusListener(focusListener);
        }
        
        @Override
        public void removeFocusListener(final FocusListener focusListener) {
            AccessibleHTML.this.getTextComponent().removeFocusListener(focusListener);
        }
    }
    
    class TextElementInfo extends ElementInfo implements Accessible
    {
        private AccessibleContext accessibleContext;
        final /* synthetic */ AccessibleHTML this$0;
        
        TextElementInfo(final Element element, final ElementInfo elementInfo) {
            super(element, elementInfo);
        }
        
        @Override
        public AccessibleContext getAccessibleContext() {
            if (this.accessibleContext == null) {
                this.accessibleContext = new TextAccessibleContext(this);
            }
            return this.accessibleContext;
        }
        
        public class TextAccessibleContext extends HTMLAccessibleContext implements AccessibleText
        {
            public TextAccessibleContext(final ElementInfo elementInfo) {
                TextElementInfo.this.this$0.super(elementInfo);
            }
            
            @Override
            public AccessibleText getAccessibleText() {
                return this;
            }
            
            @Override
            public String getAccessibleName() {
                if (AccessibleHTML.this.model != null) {
                    return (String)AccessibleHTML.this.model.getProperty("title");
                }
                return null;
            }
            
            @Override
            public String getAccessibleDescription() {
                return AccessibleHTML.this.editor.getContentType();
            }
            
            @Override
            public AccessibleRole getAccessibleRole() {
                return AccessibleRole.TEXT;
            }
            
            @Override
            public int getIndexAtPoint(final Point point) {
                final View view = TextElementInfo.this.getView();
                if (view != null) {
                    return view.viewToModel((float)point.x, (float)point.y, this.getBounds());
                }
                return -1;
            }
            
            @Override
            public Rectangle getCharacterBounds(final int n) {
                try {
                    return AccessibleHTML.this.editor.getUI().modelToView(AccessibleHTML.this.editor, n);
                }
                catch (final BadLocationException ex) {
                    return null;
                }
            }
            
            @Override
            public int getCharCount() {
                if (TextElementInfo.this.validateIfNecessary()) {
                    final Element element = this.elementInfo.getElement();
                    return element.getEndOffset() - element.getStartOffset();
                }
                return 0;
            }
            
            @Override
            public int getCaretPosition() {
                final View view = TextElementInfo.this.getView();
                if (view == null) {
                    return -1;
                }
                final Container container = view.getContainer();
                if (container == null) {
                    return -1;
                }
                if (container instanceof JTextComponent) {
                    return ((JTextComponent)container).getCaretPosition();
                }
                return -1;
            }
            
            @Override
            public String getAtIndex(final int n, final int n2) {
                return this.getAtIndex(n, n2, 0);
            }
            
            @Override
            public String getAfterIndex(final int n, final int n2) {
                return this.getAtIndex(n, n2, 1);
            }
            
            @Override
            public String getBeforeIndex(final int n, final int n2) {
                return this.getAtIndex(n, n2, -1);
            }
            
            private String getAtIndex(final int n, final int n2, final int n3) {
                if (AccessibleHTML.this.model instanceof AbstractDocument) {
                    ((AbstractDocument)AccessibleHTML.this.model).readLock();
                }
                try {
                    if (n2 < 0 || n2 >= AccessibleHTML.this.model.getLength()) {
                        return null;
                    }
                    switch (n) {
                        case 1: {
                            if (n2 + n3 < AccessibleHTML.this.model.getLength() && n2 + n3 >= 0) {
                                return AccessibleHTML.this.model.getText(n2 + n3, 1);
                            }
                            break;
                        }
                        case 2:
                        case 3: {
                            IndexedSegment indexedSegment = this.getSegmentAt(n, n2);
                            if (indexedSegment == null) {
                                break;
                            }
                            if (n3 != 0) {
                                int n4;
                                if (n3 < 0) {
                                    n4 = indexedSegment.modelOffset - 1;
                                }
                                else {
                                    n4 = indexedSegment.modelOffset + n3 * indexedSegment.count;
                                }
                                if (n4 >= 0 && n4 <= AccessibleHTML.this.model.getLength()) {
                                    indexedSegment = this.getSegmentAt(n, n4);
                                }
                                else {
                                    indexedSegment = null;
                                }
                            }
                            if (indexedSegment != null) {
                                return new String(indexedSegment.array, indexedSegment.offset, indexedSegment.count);
                            }
                            break;
                        }
                    }
                }
                catch (final BadLocationException ex) {}
                finally {
                    if (AccessibleHTML.this.model instanceof AbstractDocument) {
                        ((AbstractDocument)AccessibleHTML.this.model).readUnlock();
                    }
                }
                return null;
            }
            
            private Element getParagraphElement(final int n) {
                if (AccessibleHTML.this.model instanceof PlainDocument) {
                    return ((PlainDocument)AccessibleHTML.this.model).getParagraphElement(n);
                }
                if (AccessibleHTML.this.model instanceof StyledDocument) {
                    return ((StyledDocument)AccessibleHTML.this.model).getParagraphElement(n);
                }
                Element element;
                for (element = AccessibleHTML.this.model.getDefaultRootElement(); !element.isLeaf(); element = element.getElement(element.getElementIndex(n))) {}
                if (element == null) {
                    return null;
                }
                return element.getParentElement();
            }
            
            private IndexedSegment getParagraphElementText(final int n) throws BadLocationException {
                final Element paragraphElement = this.getParagraphElement(n);
                if (paragraphElement != null) {
                    final IndexedSegment indexedSegment = new IndexedSegment();
                    try {
                        AccessibleHTML.this.model.getText(paragraphElement.getStartOffset(), paragraphElement.getEndOffset() - paragraphElement.getStartOffset(), indexedSegment);
                    }
                    catch (final BadLocationException ex) {
                        return null;
                    }
                    indexedSegment.modelOffset = paragraphElement.getStartOffset();
                    return indexedSegment;
                }
                return null;
            }
            
            private IndexedSegment getSegmentAt(final int n, final int n2) throws BadLocationException {
                final IndexedSegment paragraphElementText = this.getParagraphElementText(n2);
                if (paragraphElementText == null) {
                    return null;
                }
                BreakIterator breakIterator = null;
                switch (n) {
                    case 2: {
                        breakIterator = BreakIterator.getWordInstance(this.getLocale());
                        break;
                    }
                    case 3: {
                        breakIterator = BreakIterator.getSentenceInstance(this.getLocale());
                        break;
                    }
                    default: {
                        return null;
                    }
                }
                paragraphElementText.first();
                breakIterator.setText(paragraphElementText);
                final int following = breakIterator.following(n2 - paragraphElementText.modelOffset + paragraphElementText.offset);
                if (following == -1) {
                    return null;
                }
                if (following > paragraphElementText.offset + paragraphElementText.count) {
                    return null;
                }
                final int previous = breakIterator.previous();
                if (previous == -1 || previous >= paragraphElementText.offset + paragraphElementText.count) {
                    return null;
                }
                paragraphElementText.modelOffset = paragraphElementText.modelOffset + previous - paragraphElementText.offset;
                paragraphElementText.offset = previous;
                paragraphElementText.count = following - previous;
                return paragraphElementText;
            }
            
            @Override
            public AttributeSet getCharacterAttribute(final int n) {
                if (AccessibleHTML.this.model instanceof StyledDocument) {
                    final Element characterElement = ((StyledDocument)AccessibleHTML.this.model).getCharacterElement(n);
                    if (characterElement != null) {
                        return characterElement.getAttributes();
                    }
                }
                return null;
            }
            
            @Override
            public int getSelectionStart() {
                return AccessibleHTML.this.editor.getSelectionStart();
            }
            
            @Override
            public int getSelectionEnd() {
                return AccessibleHTML.this.editor.getSelectionEnd();
            }
            
            @Override
            public String getSelectedText() {
                return AccessibleHTML.this.editor.getSelectedText();
            }
            
            private String getText(final int n, final int n2) throws BadLocationException {
                if (AccessibleHTML.this.model != null && AccessibleHTML.this.model instanceof StyledDocument) {
                    final StyledDocument styledDocument = (StyledDocument)AccessibleHTML.this.model;
                    return AccessibleHTML.this.model.getText(n, n2);
                }
                return null;
            }
            
            private class IndexedSegment extends Segment
            {
                public int modelOffset;
            }
        }
    }
    
    private class IconElementInfo extends ElementInfo implements Accessible
    {
        private int width;
        private int height;
        private AccessibleContext accessibleContext;
        final /* synthetic */ AccessibleHTML this$0;
        
        IconElementInfo(final Element element, final ElementInfo elementInfo) {
            super(element, elementInfo);
            this.width = -1;
            this.height = -1;
        }
        
        @Override
        protected void invalidate(final boolean b) {
            super.invalidate(b);
            final int n = -1;
            this.height = n;
            this.width = n;
        }
        
        private int getImageSize(final Object o) {
            if (this.validateIfNecessary()) {
                int n = this.getIntAttr(this.getAttributes(), o, -1);
                if (n == -1) {
                    final View view = this.getView();
                    n = 0;
                    if (view instanceof ImageView) {
                        final Image image = ((ImageView)view).getImage();
                        if (image != null) {
                            if (o == HTML.Attribute.WIDTH) {
                                n = image.getWidth(null);
                            }
                            else {
                                n = image.getHeight(null);
                            }
                        }
                    }
                }
                return n;
            }
            return 0;
        }
        
        @Override
        public AccessibleContext getAccessibleContext() {
            if (this.accessibleContext == null) {
                this.accessibleContext = new IconAccessibleContext(this);
            }
            return this.accessibleContext;
        }
        
        protected class IconAccessibleContext extends HTMLAccessibleContext implements AccessibleIcon
        {
            public IconAccessibleContext(final ElementInfo elementInfo) {
                IconElementInfo.this.this$0.super(elementInfo);
            }
            
            @Override
            public String getAccessibleName() {
                return this.getAccessibleIconDescription();
            }
            
            @Override
            public String getAccessibleDescription() {
                return AccessibleHTML.this.editor.getContentType();
            }
            
            @Override
            public AccessibleRole getAccessibleRole() {
                return AccessibleRole.ICON;
            }
            
            @Override
            public AccessibleIcon[] getAccessibleIcon() {
                return new AccessibleIcon[] { this };
            }
            
            @Override
            public String getAccessibleIconDescription() {
                return ((ImageView)IconElementInfo.this.getView()).getAltText();
            }
            
            @Override
            public void setAccessibleIconDescription(final String s) {
            }
            
            @Override
            public int getAccessibleIconWidth() {
                if (IconElementInfo.this.width == -1) {
                    IconElementInfo.this.width = IconElementInfo.this.getImageSize(HTML.Attribute.WIDTH);
                }
                return IconElementInfo.this.width;
            }
            
            @Override
            public int getAccessibleIconHeight() {
                if (IconElementInfo.this.height == -1) {
                    IconElementInfo.this.height = IconElementInfo.this.getImageSize(HTML.Attribute.HEIGHT);
                }
                return IconElementInfo.this.height;
            }
        }
    }
    
    private class TableElementInfo extends ElementInfo implements Accessible
    {
        protected ElementInfo caption;
        private TableCellElementInfo[][] grid;
        private AccessibleContext accessibleContext;
        final /* synthetic */ AccessibleHTML this$0;
        
        TableElementInfo(final Element element, final ElementInfo elementInfo) {
            super(element, elementInfo);
        }
        
        public ElementInfo getCaptionInfo() {
            return this.caption;
        }
        
        @Override
        protected void validate() {
            super.validate();
            this.updateGrid();
        }
        
        @Override
        protected void loadChildren(final Element element) {
            for (int i = 0; i < element.getElementCount(); ++i) {
                final Element element2 = element.getElement(i);
                final AttributeSet attributes = element2.getAttributes();
                if (attributes.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.TR) {
                    this.addChild(new TableRowElementInfo(element2, this, i));
                }
                else if (attributes.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.CAPTION) {
                    this.caption = AccessibleHTML.this.createElementInfo(element2, this);
                }
            }
        }
        
        private void updateGrid() {
            int max = 0;
            int max2 = 0;
            for (int i = 0; i < this.getChildCount(); ++i) {
                final TableRowElementInfo row = this.getRow(i);
                int max3 = 0;
                for (int j = 0; j < max; ++j) {
                    max3 = Math.max(max3, this.getRow(i - j - 1).getColumnCount(j + 2));
                }
                max = Math.max(row.getRowCount(), max);
                --max;
                max2 = Math.max(max2, row.getColumnCount() + max3);
            }
            final int n = this.getChildCount() + max;
            this.grid = new TableCellElementInfo[n][];
            for (int k = 0; k < n; ++k) {
                this.grid[k] = new TableCellElementInfo[max2];
            }
            for (int l = 0; l < n; ++l) {
                this.getRow(l).updateGrid(l);
            }
        }
        
        public TableRowElementInfo getRow(final int n) {
            return (TableRowElementInfo)this.getChild(n);
        }
        
        public TableCellElementInfo getCell(final int n, final int n2) {
            if (this.validateIfNecessary() && n < this.grid.length && n2 < this.grid[0].length) {
                return this.grid[n][n2];
            }
            return null;
        }
        
        public int getRowExtentAt(final int n, final int n2) {
            final TableCellElementInfo cell = this.getCell(n, n2);
            if (cell != null) {
                final int rowCount = cell.getRowCount();
                int n3;
                for (n3 = 1; n - n3 >= 0 && this.grid[n - n3][n2] == cell; ++n3) {}
                return rowCount - n3 + 1;
            }
            return 0;
        }
        
        public int getColumnExtentAt(final int n, final int n2) {
            final TableCellElementInfo cell = this.getCell(n, n2);
            if (cell != null) {
                final int columnCount = cell.getColumnCount();
                int n3;
                for (n3 = 1; n2 - n3 >= 0 && this.grid[n][n2 - n3] == cell; ++n3) {}
                return columnCount - n3 + 1;
            }
            return 0;
        }
        
        public int getRowCount() {
            if (this.validateIfNecessary()) {
                return this.grid.length;
            }
            return 0;
        }
        
        public int getColumnCount() {
            if (this.validateIfNecessary() && this.grid.length > 0) {
                return this.grid[0].length;
            }
            return 0;
        }
        
        @Override
        public AccessibleContext getAccessibleContext() {
            if (this.accessibleContext == null) {
                this.accessibleContext = new TableAccessibleContext(this);
            }
            return this.accessibleContext;
        }
        
        public class TableAccessibleContext extends HTMLAccessibleContext implements AccessibleTable
        {
            private AccessibleHeadersTable rowHeadersTable;
            
            public TableAccessibleContext(final ElementInfo elementInfo) {
                TableElementInfo.this.this$0.super(elementInfo);
            }
            
            @Override
            public String getAccessibleName() {
                return this.getAccessibleRole().toString();
            }
            
            @Override
            public String getAccessibleDescription() {
                return AccessibleHTML.this.editor.getContentType();
            }
            
            @Override
            public AccessibleRole getAccessibleRole() {
                return AccessibleRole.TABLE;
            }
            
            @Override
            public int getAccessibleIndexInParent() {
                return this.elementInfo.getIndexInParent();
            }
            
            @Override
            public int getAccessibleChildrenCount() {
                return ((TableElementInfo)this.elementInfo).getRowCount() * ((TableElementInfo)this.elementInfo).getColumnCount();
            }
            
            @Override
            public Accessible getAccessibleChild(final int n) {
                final int rowCount = ((TableElementInfo)this.elementInfo).getRowCount();
                final int columnCount = ((TableElementInfo)this.elementInfo).getColumnCount();
                final int n2 = n / rowCount;
                final int n3 = n % columnCount;
                if (n2 < 0 || n2 >= rowCount || n3 < 0 || n3 >= columnCount) {
                    return null;
                }
                return this.getAccessibleAt(n2, n3);
            }
            
            @Override
            public AccessibleTable getAccessibleTable() {
                return this;
            }
            
            @Override
            public Accessible getAccessibleCaption() {
                if (TableElementInfo.this.getCaptionInfo() instanceof Accessible) {
                    return (Accessible)TableElementInfo.this.caption;
                }
                return null;
            }
            
            @Override
            public void setAccessibleCaption(final Accessible accessible) {
            }
            
            @Override
            public Accessible getAccessibleSummary() {
                return null;
            }
            
            @Override
            public void setAccessibleSummary(final Accessible accessible) {
            }
            
            @Override
            public int getAccessibleRowCount() {
                return ((TableElementInfo)this.elementInfo).getRowCount();
            }
            
            @Override
            public int getAccessibleColumnCount() {
                return ((TableElementInfo)this.elementInfo).getColumnCount();
            }
            
            @Override
            public Accessible getAccessibleAt(final int n, final int n2) {
                final TableCellElementInfo cell = TableElementInfo.this.getCell(n, n2);
                if (cell != null) {
                    return cell.getAccessible();
                }
                return null;
            }
            
            @Override
            public int getAccessibleRowExtentAt(final int n, final int n2) {
                return ((TableElementInfo)this.elementInfo).getRowExtentAt(n, n2);
            }
            
            @Override
            public int getAccessibleColumnExtentAt(final int n, final int n2) {
                return ((TableElementInfo)this.elementInfo).getColumnExtentAt(n, n2);
            }
            
            @Override
            public AccessibleTable getAccessibleRowHeader() {
                return this.rowHeadersTable;
            }
            
            @Override
            public void setAccessibleRowHeader(final AccessibleTable accessibleTable) {
            }
            
            @Override
            public AccessibleTable getAccessibleColumnHeader() {
                return null;
            }
            
            @Override
            public void setAccessibleColumnHeader(final AccessibleTable accessibleTable) {
            }
            
            @Override
            public Accessible getAccessibleRowDescription(final int n) {
                return null;
            }
            
            @Override
            public void setAccessibleRowDescription(final int n, final Accessible accessible) {
            }
            
            @Override
            public Accessible getAccessibleColumnDescription(final int n) {
                return null;
            }
            
            @Override
            public void setAccessibleColumnDescription(final int n, final Accessible accessible) {
            }
            
            @Override
            public boolean isAccessibleSelected(final int n, final int n2) {
                if (TableElementInfo.this.validateIfNecessary()) {
                    if (n < 0 || n >= this.getAccessibleRowCount() || n2 < 0 || n2 >= this.getAccessibleColumnCount()) {
                        return false;
                    }
                    final TableCellElementInfo cell = TableElementInfo.this.getCell(n, n2);
                    if (cell != null) {
                        final Element element = cell.getElement();
                        final int startOffset = element.getStartOffset();
                        final int endOffset = element.getEndOffset();
                        return startOffset >= AccessibleHTML.this.editor.getSelectionStart() && endOffset <= AccessibleHTML.this.editor.getSelectionEnd();
                    }
                }
                return false;
            }
            
            @Override
            public boolean isAccessibleRowSelected(final int n) {
                if (!TableElementInfo.this.validateIfNecessary()) {
                    return false;
                }
                if (n < 0 || n >= this.getAccessibleRowCount()) {
                    return false;
                }
                final int accessibleColumnCount = this.getAccessibleColumnCount();
                final TableCellElementInfo cell = TableElementInfo.this.getCell(n, 0);
                if (cell == null) {
                    return false;
                }
                final int startOffset = cell.getElement().getStartOffset();
                final TableCellElementInfo cell2 = TableElementInfo.this.getCell(n, accessibleColumnCount - 1);
                if (cell2 == null) {
                    return false;
                }
                final int endOffset = cell2.getElement().getEndOffset();
                return startOffset >= AccessibleHTML.this.editor.getSelectionStart() && endOffset <= AccessibleHTML.this.editor.getSelectionEnd();
            }
            
            @Override
            public boolean isAccessibleColumnSelected(final int n) {
                if (!TableElementInfo.this.validateIfNecessary()) {
                    return false;
                }
                if (n < 0 || n >= this.getAccessibleColumnCount()) {
                    return false;
                }
                final int accessibleRowCount = this.getAccessibleRowCount();
                final TableCellElementInfo cell = TableElementInfo.this.getCell(0, n);
                if (cell == null) {
                    return false;
                }
                final int startOffset = cell.getElement().getStartOffset();
                final TableCellElementInfo cell2 = TableElementInfo.this.getCell(accessibleRowCount - 1, n);
                if (cell2 == null) {
                    return false;
                }
                final int endOffset = cell2.getElement().getEndOffset();
                return startOffset >= AccessibleHTML.this.editor.getSelectionStart() && endOffset <= AccessibleHTML.this.editor.getSelectionEnd();
            }
            
            @Override
            public int[] getSelectedAccessibleRows() {
                if (TableElementInfo.this.validateIfNecessary()) {
                    final int accessibleRowCount = this.getAccessibleRowCount();
                    final Vector vector = new Vector();
                    for (int i = 0; i < accessibleRowCount; ++i) {
                        if (this.isAccessibleRowSelected(i)) {
                            vector.addElement(i);
                        }
                    }
                    final int[] array = new int[vector.size()];
                    for (int j = 0; j < array.length; ++j) {
                        array[j] = (int)vector.elementAt(j);
                    }
                    return array;
                }
                return new int[0];
            }
            
            @Override
            public int[] getSelectedAccessibleColumns() {
                if (TableElementInfo.this.validateIfNecessary()) {
                    final int accessibleRowCount = this.getAccessibleRowCount();
                    final Vector vector = new Vector();
                    for (int i = 0; i < accessibleRowCount; ++i) {
                        if (this.isAccessibleColumnSelected(i)) {
                            vector.addElement(i);
                        }
                    }
                    final int[] array = new int[vector.size()];
                    for (int j = 0; j < array.length; ++j) {
                        array[j] = (int)vector.elementAt(j);
                    }
                    return array;
                }
                return new int[0];
            }
            
            public int getAccessibleRow(final int n) {
                if (!TableElementInfo.this.validateIfNecessary()) {
                    return -1;
                }
                if (n >= this.getAccessibleColumnCount() * this.getAccessibleRowCount()) {
                    return -1;
                }
                return n / this.getAccessibleColumnCount();
            }
            
            public int getAccessibleColumn(final int n) {
                if (!TableElementInfo.this.validateIfNecessary()) {
                    return -1;
                }
                if (n >= this.getAccessibleColumnCount() * this.getAccessibleRowCount()) {
                    return -1;
                }
                return n % this.getAccessibleColumnCount();
            }
            
            public int getAccessibleIndex(final int n, final int n2) {
                if (!TableElementInfo.this.validateIfNecessary()) {
                    return -1;
                }
                if (n >= this.getAccessibleRowCount() || n2 >= this.getAccessibleColumnCount()) {
                    return -1;
                }
                return n * this.getAccessibleColumnCount() + n2;
            }
            
            public String getAccessibleRowHeader(final int n) {
                if (TableElementInfo.this.validateIfNecessary()) {
                    final TableCellElementInfo cell = TableElementInfo.this.getCell(n, 0);
                    if (cell.isHeaderCell()) {
                        final View view = cell.getView();
                        if (view != null && AccessibleHTML.this.model != null) {
                            try {
                                return AccessibleHTML.this.model.getText(view.getStartOffset(), view.getEndOffset() - view.getStartOffset());
                            }
                            catch (final BadLocationException ex) {
                                return null;
                            }
                        }
                    }
                }
                return null;
            }
            
            public String getAccessibleColumnHeader(final int n) {
                if (TableElementInfo.this.validateIfNecessary()) {
                    final TableCellElementInfo cell = TableElementInfo.this.getCell(0, n);
                    if (cell.isHeaderCell()) {
                        final View view = cell.getView();
                        if (view != null && AccessibleHTML.this.model != null) {
                            try {
                                return AccessibleHTML.this.model.getText(view.getStartOffset(), view.getEndOffset() - view.getStartOffset());
                            }
                            catch (final BadLocationException ex) {
                                return null;
                            }
                        }
                    }
                }
                return null;
            }
            
            public void addRowHeader(final TableCellElementInfo tableCellElementInfo, final int n) {
                if (this.rowHeadersTable == null) {
                    this.rowHeadersTable = new AccessibleHeadersTable();
                }
                this.rowHeadersTable.addHeader(tableCellElementInfo, n);
            }
            
            protected class AccessibleHeadersTable implements AccessibleTable
            {
                private Hashtable<Integer, ArrayList<TableCellElementInfo>> headers;
                private int rowCount;
                private int columnCount;
                
                protected AccessibleHeadersTable() {
                    this.headers = new Hashtable<Integer, ArrayList<TableCellElementInfo>>();
                    this.rowCount = 0;
                    this.columnCount = 0;
                }
                
                public void addHeader(final TableCellElementInfo tableCellElementInfo, final int n) {
                    final Integer value = n;
                    ArrayList list = this.headers.get(value);
                    if (list == null) {
                        list = new ArrayList();
                        this.headers.put(value, list);
                    }
                    list.add(tableCellElementInfo);
                }
                
                @Override
                public Accessible getAccessibleCaption() {
                    return null;
                }
                
                @Override
                public void setAccessibleCaption(final Accessible accessible) {
                }
                
                @Override
                public Accessible getAccessibleSummary() {
                    return null;
                }
                
                @Override
                public void setAccessibleSummary(final Accessible accessible) {
                }
                
                @Override
                public int getAccessibleRowCount() {
                    return this.rowCount;
                }
                
                @Override
                public int getAccessibleColumnCount() {
                    return this.columnCount;
                }
                
                private TableCellElementInfo getElementInfoAt(final int n, final int n2) {
                    final ArrayList list = this.headers.get(n);
                    if (list != null) {
                        return (TableCellElementInfo)list.get(n2);
                    }
                    return null;
                }
                
                @Override
                public Accessible getAccessibleAt(final int n, final int n2) {
                    final TableCellElementInfo elementInfo = this.getElementInfoAt(n, n2);
                    if (elementInfo instanceof Accessible) {
                        return (Accessible)elementInfo;
                    }
                    return null;
                }
                
                @Override
                public int getAccessibleRowExtentAt(final int n, final int n2) {
                    final TableCellElementInfo elementInfo = this.getElementInfoAt(n, n2);
                    if (elementInfo != null) {
                        return elementInfo.getRowCount();
                    }
                    return 0;
                }
                
                @Override
                public int getAccessibleColumnExtentAt(final int n, final int n2) {
                    final TableCellElementInfo elementInfo = this.getElementInfoAt(n, n2);
                    if (elementInfo != null) {
                        return elementInfo.getRowCount();
                    }
                    return 0;
                }
                
                @Override
                public AccessibleTable getAccessibleRowHeader() {
                    return null;
                }
                
                @Override
                public void setAccessibleRowHeader(final AccessibleTable accessibleTable) {
                }
                
                @Override
                public AccessibleTable getAccessibleColumnHeader() {
                    return null;
                }
                
                @Override
                public void setAccessibleColumnHeader(final AccessibleTable accessibleTable) {
                }
                
                @Override
                public Accessible getAccessibleRowDescription(final int n) {
                    return null;
                }
                
                @Override
                public void setAccessibleRowDescription(final int n, final Accessible accessible) {
                }
                
                @Override
                public Accessible getAccessibleColumnDescription(final int n) {
                    return null;
                }
                
                @Override
                public void setAccessibleColumnDescription(final int n, final Accessible accessible) {
                }
                
                @Override
                public boolean isAccessibleSelected(final int n, final int n2) {
                    return false;
                }
                
                @Override
                public boolean isAccessibleRowSelected(final int n) {
                    return false;
                }
                
                @Override
                public boolean isAccessibleColumnSelected(final int n) {
                    return false;
                }
                
                @Override
                public int[] getSelectedAccessibleRows() {
                    return new int[0];
                }
                
                @Override
                public int[] getSelectedAccessibleColumns() {
                    return new int[0];
                }
            }
        }
        
        private class TableRowElementInfo extends ElementInfo
        {
            private TableElementInfo parent;
            private int rowNumber;
            
            TableRowElementInfo(final Element element, final TableElementInfo parent, final int rowNumber) {
                TableElementInfo.this.this$0.super(element, parent);
                this.parent = parent;
                this.rowNumber = rowNumber;
            }
            
            @Override
            protected void loadChildren(final Element element) {
                for (int i = 0; i < element.getElementCount(); ++i) {
                    final AttributeSet attributes = element.getElement(i).getAttributes();
                    if (attributes.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.TH) {
                        final TableCellElementInfo tableCellElementInfo = new TableCellElementInfo(element.getElement(i), this, true);
                        this.addChild(tableCellElementInfo);
                        ((TableAccessibleContext)this.parent.getAccessibleContext().getAccessibleTable()).addRowHeader(tableCellElementInfo, this.rowNumber);
                    }
                    else if (attributes.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.TD) {
                        this.addChild(new TableCellElementInfo(element.getElement(i), this, false));
                    }
                }
            }
            
            public int getRowCount() {
                int max = 1;
                if (this.validateIfNecessary()) {
                    for (int i = 0; i < this.getChildCount(); ++i) {
                        final TableCellElementInfo tableCellElementInfo = (TableCellElementInfo)this.getChild(i);
                        if (tableCellElementInfo.validateIfNecessary()) {
                            max = Math.max(max, tableCellElementInfo.getRowCount());
                        }
                    }
                }
                return max;
            }
            
            public int getColumnCount() {
                int n = 0;
                if (this.validateIfNecessary()) {
                    for (int i = 0; i < this.getChildCount(); ++i) {
                        final TableCellElementInfo tableCellElementInfo = (TableCellElementInfo)this.getChild(i);
                        if (tableCellElementInfo.validateIfNecessary()) {
                            n += tableCellElementInfo.getColumnCount();
                        }
                    }
                }
                return n;
            }
            
            @Override
            protected void invalidate(final boolean b) {
                super.invalidate(b);
                this.getParent().invalidate(true);
            }
            
            private void updateGrid(int n) {
                if (this.validateIfNecessary()) {
                    int i = 0;
                    while (i == 0) {
                        for (int j = 0; j < TableElementInfo.this.grid[n].length; ++j) {
                            if (TableElementInfo.this.grid[n][j] == null) {
                                i = 1;
                                break;
                            }
                        }
                        if (i == 0) {
                            ++n;
                        }
                    }
                    int n2 = 0;
                    for (int k = 0; k < this.getChildCount(); ++k) {
                        final TableCellElementInfo tableCellElementInfo = (TableCellElementInfo)this.getChild(k);
                        while (TableElementInfo.this.grid[n][n2] != null) {
                            ++n2;
                        }
                        for (int l = tableCellElementInfo.getRowCount() - 1; l >= 0; --l) {
                            for (int n3 = tableCellElementInfo.getColumnCount() - 1; n3 >= 0; --n3) {
                                TableElementInfo.this.grid[n + l][n2 + n3] = tableCellElementInfo;
                            }
                        }
                        n2 += tableCellElementInfo.getColumnCount();
                    }
                }
            }
            
            private int getColumnCount(final int n) {
                if (this.validateIfNecessary()) {
                    int n2 = 0;
                    for (int i = 0; i < this.getChildCount(); ++i) {
                        final TableCellElementInfo tableCellElementInfo = (TableCellElementInfo)this.getChild(i);
                        if (tableCellElementInfo.getRowCount() >= n) {
                            n2 += tableCellElementInfo.getColumnCount();
                        }
                    }
                    return n2;
                }
                return 0;
            }
        }
        
        private class TableCellElementInfo extends ElementInfo
        {
            private Accessible accessible;
            private boolean isHeaderCell;
            
            TableCellElementInfo(final Element element, final ElementInfo elementInfo) {
                TableElementInfo.this.this$0.super(element, elementInfo);
                this.isHeaderCell = false;
            }
            
            TableCellElementInfo(final Element element, final ElementInfo elementInfo, final boolean isHeaderCell) {
                TableElementInfo.this.this$0.super(element, elementInfo);
                this.isHeaderCell = isHeaderCell;
            }
            
            public boolean isHeaderCell() {
                return this.isHeaderCell;
            }
            
            public Accessible getAccessible() {
                this.accessible = null;
                this.getAccessible(this);
                return this.accessible;
            }
            
            private void getAccessible(final ElementInfo elementInfo) {
                if (elementInfo instanceof Accessible) {
                    this.accessible = (Accessible)elementInfo;
                }
                else {
                    for (int i = 0; i < elementInfo.getChildCount(); ++i) {
                        this.getAccessible(elementInfo.getChild(i));
                    }
                }
            }
            
            public int getRowCount() {
                if (this.validateIfNecessary()) {
                    return Math.max(1, this.getIntAttr(this.getAttributes(), HTML.Attribute.ROWSPAN, 1));
                }
                return 0;
            }
            
            public int getColumnCount() {
                if (this.validateIfNecessary()) {
                    return Math.max(1, this.getIntAttr(this.getAttributes(), HTML.Attribute.COLSPAN, 1));
                }
                return 0;
            }
            
            @Override
            protected void invalidate(final boolean b) {
                super.invalidate(b);
                this.getParent().invalidate(true);
            }
        }
    }
    
    private class ElementInfo
    {
        private ArrayList<ElementInfo> children;
        private Element element;
        private ElementInfo parent;
        private boolean isValid;
        private boolean canBeValid;
        
        ElementInfo(final AccessibleHTML accessibleHTML, final Element element) {
            this(accessibleHTML, element, null);
        }
        
        ElementInfo(final Element element, final ElementInfo parent) {
            this.element = element;
            this.parent = parent;
            this.isValid = false;
            this.canBeValid = true;
        }
        
        protected void validate() {
            this.isValid = true;
            this.loadChildren(this.getElement());
        }
        
        protected void loadChildren(final Element element) {
            if (!element.isLeaf()) {
                for (int i = 0; i < element.getElementCount(); ++i) {
                    final Element element2 = element.getElement(i);
                    final ElementInfo elementInfo = AccessibleHTML.this.createElementInfo(element2, this);
                    if (elementInfo != null) {
                        this.addChild(elementInfo);
                    }
                    else {
                        this.loadChildren(element2);
                    }
                }
            }
        }
        
        public int getIndexInParent() {
            if (this.parent == null || !this.parent.isValid()) {
                return -1;
            }
            return this.parent.indexOf(this);
        }
        
        public Element getElement() {
            return this.element;
        }
        
        public ElementInfo getParent() {
            return this.parent;
        }
        
        public int indexOf(final ElementInfo elementInfo) {
            final ArrayList<ElementInfo> children = this.children;
            if (children != null) {
                return children.indexOf(elementInfo);
            }
            return -1;
        }
        
        public ElementInfo getChild(final int n) {
            if (this.validateIfNecessary()) {
                final ArrayList<ElementInfo> children = this.children;
                if (children != null && n >= 0 && n < children.size()) {
                    return (ElementInfo)children.get(n);
                }
            }
            return null;
        }
        
        public int getChildCount() {
            this.validateIfNecessary();
            return (this.children == null) ? 0 : this.children.size();
        }
        
        protected void addChild(final ElementInfo elementInfo) {
            if (this.children == null) {
                this.children = new ArrayList<ElementInfo>();
            }
            this.children.add(elementInfo);
        }
        
        protected View getView() {
            if (!this.validateIfNecessary()) {
                return null;
            }
            final Object access$1300 = AccessibleHTML.this.lock();
            try {
                final View access$1301 = AccessibleHTML.this.getRootView();
                final Element element = this.getElement();
                final int startOffset = element.getStartOffset();
                if (access$1301 != null) {
                    return this.getView(access$1301, element, startOffset);
                }
                return null;
            }
            finally {
                AccessibleHTML.this.unlock(access$1300);
            }
        }
        
        public Rectangle getBounds() {
            if (!this.validateIfNecessary()) {
                return null;
            }
            final Object access$1300 = AccessibleHTML.this.lock();
            try {
                final Rectangle access$1301 = AccessibleHTML.this.getRootEditorRect();
                final View access$1302 = AccessibleHTML.this.getRootView();
                final Element element = this.getElement();
                if (access$1301 != null && access$1302 != null) {
                    try {
                        return access$1302.modelToView(element.getStartOffset(), Position.Bias.Forward, element.getEndOffset(), Position.Bias.Backward, access$1301).getBounds();
                    }
                    catch (final BadLocationException ex) {}
                }
            }
            finally {
                AccessibleHTML.this.unlock(access$1300);
            }
            return null;
        }
        
        protected boolean isValid() {
            return this.isValid;
        }
        
        protected AttributeSet getAttributes() {
            if (this.validateIfNecessary()) {
                return this.getElement().getAttributes();
            }
            return null;
        }
        
        protected AttributeSet getViewAttributes() {
            if (!this.validateIfNecessary()) {
                return null;
            }
            final View view = this.getView();
            if (view != null) {
                return view.getElement().getAttributes();
            }
            return this.getElement().getAttributes();
        }
        
        protected int getIntAttr(final AttributeSet set, final Object o, final int n) {
            if (set != null && set.isDefined(o)) {
                final String s = (String)set.getAttribute(o);
                int max;
                if (s == null) {
                    max = n;
                }
                else {
                    try {
                        max = Math.max(0, Integer.parseInt(s));
                    }
                    catch (final NumberFormatException ex) {
                        max = n;
                    }
                }
                return max;
            }
            return n;
        }
        
        protected boolean validateIfNecessary() {
            if (!this.isValid() && this.canBeValid) {
                this.children = null;
                final Object access$1300 = AccessibleHTML.this.lock();
                try {
                    this.validate();
                }
                finally {
                    AccessibleHTML.this.unlock(access$1300);
                }
            }
            return this.isValid();
        }
        
        protected void invalidate(final boolean canBeValid) {
            if (!this.isValid()) {
                if (this.canBeValid && !canBeValid) {
                    this.canBeValid = false;
                }
                return;
            }
            this.isValid = false;
            this.canBeValid = canBeValid;
            if (this.children != null) {
                final Iterator<ElementInfo> iterator = this.children.iterator();
                while (iterator.hasNext()) {
                    iterator.next().invalidate(false);
                }
                this.children = null;
            }
        }
        
        private View getView(final View view, final Element element, final int n) {
            if (view.getElement() == element) {
                return view;
            }
            final int viewIndex = view.getViewIndex(n, Position.Bias.Forward);
            if (viewIndex != -1 && viewIndex < view.getViewCount()) {
                return this.getView(view.getView(viewIndex), element, n);
            }
            return null;
        }
        
        private int getClosestInfoIndex(final int n) {
            for (int i = 0; i < this.getChildCount(); ++i) {
                final ElementInfo child = this.getChild(i);
                if (n < child.getElement().getEndOffset() || n == child.getElement().getStartOffset()) {
                    return i;
                }
            }
            return -1;
        }
        
        private void update(final DocumentEvent documentEvent) {
            if (!this.isValid()) {
                return;
            }
            final ElementInfo parent = this.getParent();
            Element element = this.getElement();
            while (documentEvent.getChange(element) == null) {
                element = element.getParentElement();
                if (parent == null || element == null || element == parent.getElement()) {
                    if (this.getChildCount() > 0) {
                        final Element element2 = this.getElement();
                        final int offset = documentEvent.getOffset();
                        int n = this.getClosestInfoIndex(offset);
                        if (n == -1 && documentEvent.getType() == DocumentEvent.EventType.REMOVE && offset >= element2.getEndOffset()) {
                            n = this.getChildCount() - 1;
                        }
                        final ElementInfo elementInfo = (n >= 0) ? this.getChild(n) : null;
                        if (elementInfo != null && elementInfo.getElement().getStartOffset() == offset && offset > 0) {
                            n = Math.max(n - 1, 0);
                        }
                        int closestInfoIndex;
                        if (documentEvent.getType() != DocumentEvent.EventType.REMOVE) {
                            closestInfoIndex = this.getClosestInfoIndex(offset + documentEvent.getLength());
                            if (closestInfoIndex < 0) {
                                closestInfoIndex = this.getChildCount() - 1;
                            }
                        }
                        else {
                            for (closestInfoIndex = n; closestInfoIndex + 1 < this.getChildCount() && this.getChild(closestInfoIndex + 1).getElement().getEndOffset() == this.getChild(closestInfoIndex + 1).getElement().getStartOffset(); ++closestInfoIndex) {}
                        }
                        for (int max = Math.max(n, 0); max <= closestInfoIndex && this.isValid(); ++max) {
                            this.getChild(max).update(documentEvent);
                        }
                    }
                    return;
                }
            }
            if (element == this.getElement()) {
                this.invalidate(true);
            }
            else if (parent != null) {
                parent.invalidate(parent == AccessibleHTML.this.getRootInfo());
            }
        }
    }
    
    private class DocumentHandler implements DocumentListener
    {
        @Override
        public void insertUpdate(final DocumentEvent documentEvent) {
            AccessibleHTML.this.getRootInfo().update(documentEvent);
        }
        
        @Override
        public void removeUpdate(final DocumentEvent documentEvent) {
            AccessibleHTML.this.getRootInfo().update(documentEvent);
        }
        
        @Override
        public void changedUpdate(final DocumentEvent documentEvent) {
            AccessibleHTML.this.getRootInfo().update(documentEvent);
        }
    }
    
    private class PropertyChangeHandler implements PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            if (propertyChangeEvent.getPropertyName().equals("document")) {
                AccessibleHTML.this.setDocument(AccessibleHTML.this.editor.getDocument());
            }
        }
    }
}
