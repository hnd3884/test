package javax.swing.text;

import java.awt.Graphics;
import java.awt.Shape;
import javax.swing.event.DocumentEvent;
import java.util.Vector;

public class ZoneView extends BoxView
{
    int maxZoneSize;
    int maxZonesLoaded;
    Vector<View> loadedZones;
    
    public ZoneView(final Element element, final int n) {
        super(element, n);
        this.maxZoneSize = 8192;
        this.maxZonesLoaded = 3;
        this.loadedZones = new Vector<View>();
    }
    
    public int getMaximumZoneSize() {
        return this.maxZoneSize;
    }
    
    public void setMaximumZoneSize(final int maxZoneSize) {
        this.maxZoneSize = maxZoneSize;
    }
    
    public int getMaxZonesLoaded() {
        return this.maxZonesLoaded;
    }
    
    public void setMaxZonesLoaded(final int maxZonesLoaded) {
        if (maxZonesLoaded < 1) {
            throw new IllegalArgumentException("ZoneView.setMaxZonesLoaded must be greater than 0.");
        }
        this.maxZonesLoaded = maxZonesLoaded;
        this.unloadOldZones();
    }
    
    protected void zoneWasLoaded(final View view) {
        this.loadedZones.addElement(view);
        this.unloadOldZones();
    }
    
    void unloadOldZones() {
        while (this.loadedZones.size() > this.getMaxZonesLoaded()) {
            final View view = this.loadedZones.elementAt(0);
            this.loadedZones.removeElementAt(0);
            this.unloadZone(view);
        }
    }
    
    protected void unloadZone(final View view) {
        view.removeAll();
    }
    
    protected boolean isZoneLoaded(final View view) {
        return view.getViewCount() > 0;
    }
    
    protected View createZone(final int n, final int n2) {
        final Document document = this.getDocument();
        Zone zone;
        try {
            zone = new Zone(this.getElement(), document.createPosition(n), document.createPosition(n2));
        }
        catch (final BadLocationException ex) {
            throw new StateInvariantError(ex.getMessage());
        }
        return zone;
    }
    
    @Override
    protected void loadChildren(final ViewFactory viewFactory) {
        this.getDocument();
        final int startOffset = this.getStartOffset();
        final int endOffset = this.getEndOffset();
        this.append(this.createZone(startOffset, endOffset));
        this.handleInsert(startOffset, endOffset - startOffset);
    }
    
    @Override
    protected int getViewIndexAtPosition(final int n) {
        final int viewCount = this.getViewCount();
        if (n == this.getEndOffset()) {
            return viewCount - 1;
        }
        for (int i = 0; i < viewCount; ++i) {
            final View view = this.getView(i);
            if (n >= view.getStartOffset() && n < view.getEndOffset()) {
                return i;
            }
        }
        return -1;
    }
    
    void handleInsert(final int n, final int n2) {
        final int viewIndex = this.getViewIndex(n, Position.Bias.Forward);
        final View view = this.getView(viewIndex);
        final int startOffset = view.getStartOffset();
        final int endOffset = view.getEndOffset();
        if (endOffset - startOffset > this.maxZoneSize) {
            this.splitZone(viewIndex, startOffset, endOffset);
        }
    }
    
    void handleRemove(final int n, final int n2) {
    }
    
    void splitZone(final int n, int n2, final int n3) {
        this.getElement().getDocument();
        final Vector vector = new Vector();
        int i = n2;
        do {
            n2 = i;
            i = Math.min(this.getDesiredZoneEnd(n2), n3);
            vector.addElement(this.createZone(n2, i));
        } while (i < n3);
        this.getView(n);
        final View[] array = new View[vector.size()];
        vector.copyInto(array);
        this.replace(n, 1, array);
    }
    
    int getDesiredZoneEnd(final int n) {
        final Element element = this.getElement();
        final Element element2 = element.getElement(element.getElementIndex(n + this.maxZoneSize / 2));
        final int startOffset = element2.getStartOffset();
        final int endOffset = element2.getEndOffset();
        if (endOffset - n > this.maxZoneSize && startOffset > n) {
            return startOffset;
        }
        return endOffset;
    }
    
    @Override
    protected boolean updateChildren(final DocumentEvent.ElementChange elementChange, final DocumentEvent documentEvent, final ViewFactory viewFactory) {
        return false;
    }
    
    @Override
    public void insertUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        this.handleInsert(documentEvent.getOffset(), documentEvent.getLength());
        super.insertUpdate(documentEvent, shape, viewFactory);
    }
    
    @Override
    public void removeUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        this.handleRemove(documentEvent.getOffset(), documentEvent.getLength());
        super.removeUpdate(documentEvent, shape, viewFactory);
    }
    
    class Zone extends AsyncBoxView
    {
        private Position start;
        private Position end;
        
        public Zone(final Element element, final Position start, final Position end) {
            super(element, ZoneView.this.getAxis());
            this.start = start;
            this.end = end;
        }
        
        public void load() {
            if (!this.isLoaded()) {
                this.setEstimatedMajorSpan(true);
                final Element element = this.getElement();
                final ViewFactory viewFactory = this.getViewFactory();
                final int elementIndex = element.getElementIndex(this.getStartOffset());
                final int elementIndex2 = element.getElementIndex(this.getEndOffset());
                final View[] array = new View[elementIndex2 - elementIndex + 1];
                for (int i = elementIndex; i <= elementIndex2; ++i) {
                    array[i - elementIndex] = viewFactory.create(element.getElement(i));
                }
                this.replace(0, 0, array);
                ZoneView.this.zoneWasLoaded(this);
            }
        }
        
        public void unload() {
            this.setEstimatedMajorSpan(true);
            this.removeAll();
        }
        
        public boolean isLoaded() {
            return this.getViewCount() != 0;
        }
        
        @Override
        protected void loadChildren(final ViewFactory viewFactory) {
            this.setEstimatedMajorSpan(true);
            final Element element = this.getElement();
            final int elementIndex = element.getElementIndex(this.getStartOffset());
            final int n = element.getElementIndex(this.getEndOffset()) - elementIndex;
            final View create = viewFactory.create(element.getElement(elementIndex));
            create.setParent(this);
            float preferredSpan = create.getPreferredSpan(0);
            float preferredSpan2 = create.getPreferredSpan(1);
            if (this.getMajorAxis() == 0) {
                preferredSpan *= n;
            }
            else {
                preferredSpan2 += n;
            }
            this.setSize(preferredSpan, preferredSpan2);
        }
        
        @Override
        protected void flushRequirementChanges() {
            if (this.isLoaded()) {
                super.flushRequirementChanges();
            }
        }
        
        @Override
        public int getViewIndex(int n, final Position.Bias bias) {
            n = ((bias == Position.Bias.Backward) ? Math.max(0, n - 1) : n);
            final Element element = this.getElement();
            return element.getElementIndex(n) - element.getElementIndex(this.getStartOffset());
        }
        
        @Override
        protected boolean updateChildren(final DocumentEvent.ElementChange elementChange, final DocumentEvent documentEvent, final ViewFactory viewFactory) {
            final Element[] childrenRemoved = elementChange.getChildrenRemoved();
            final Element[] childrenAdded = elementChange.getChildrenAdded();
            final Element element = this.getElement();
            final int elementIndex = element.getElementIndex(this.getStartOffset());
            final int elementIndex2 = element.getElementIndex(this.getEndOffset() - 1);
            final int index = elementChange.getIndex();
            if (index >= elementIndex && index <= elementIndex2) {
                final int n = index - elementIndex;
                final int min = Math.min(elementIndex2 - elementIndex + 1, childrenAdded.length);
                final int min2 = Math.min(elementIndex2 - elementIndex + 1, childrenRemoved.length);
                final View[] array = new View[min];
                for (int i = 0; i < min; ++i) {
                    array[i] = viewFactory.create(childrenAdded[i]);
                }
                this.replace(n, min2, array);
            }
            return true;
        }
        
        @Override
        public AttributeSet getAttributes() {
            return ZoneView.this.getAttributes();
        }
        
        @Override
        public void paint(final Graphics graphics, final Shape shape) {
            this.load();
            super.paint(graphics, shape);
        }
        
        @Override
        public int viewToModel(final float n, final float n2, final Shape shape, final Position.Bias[] array) {
            this.load();
            return super.viewToModel(n, n2, shape, array);
        }
        
        @Override
        public Shape modelToView(final int n, final Shape shape, final Position.Bias bias) throws BadLocationException {
            this.load();
            return super.modelToView(n, shape, bias);
        }
        
        @Override
        public int getStartOffset() {
            return this.start.getOffset();
        }
        
        @Override
        public int getEndOffset() {
            return this.end.getOffset();
        }
        
        @Override
        public void insertUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
            if (this.isLoaded()) {
                super.insertUpdate(documentEvent, shape, viewFactory);
            }
        }
        
        @Override
        public void removeUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
            if (this.isLoaded()) {
                super.removeUpdate(documentEvent, shape, viewFactory);
            }
        }
        
        @Override
        public void changedUpdate(final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
            if (this.isLoaded()) {
                super.changedUpdate(documentEvent, shape, viewFactory);
            }
        }
    }
}
