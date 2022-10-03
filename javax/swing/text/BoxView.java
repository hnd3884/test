package javax.swing.text;

import java.awt.Container;
import javax.swing.event.DocumentEvent;
import java.awt.Shape;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.SizeRequirements;

public class BoxView extends CompositeView
{
    int majorAxis;
    int majorSpan;
    int minorSpan;
    boolean majorReqValid;
    boolean minorReqValid;
    SizeRequirements majorRequest;
    SizeRequirements minorRequest;
    boolean majorAllocValid;
    int[] majorOffsets;
    int[] majorSpans;
    boolean minorAllocValid;
    int[] minorOffsets;
    int[] minorSpans;
    Rectangle tempRect;
    
    public BoxView(final Element element, final int majorAxis) {
        super(element);
        this.tempRect = new Rectangle();
        this.majorAxis = majorAxis;
        this.majorOffsets = new int[0];
        this.majorSpans = new int[0];
        this.majorReqValid = false;
        this.majorAllocValid = false;
        this.minorOffsets = new int[0];
        this.minorSpans = new int[0];
        this.minorReqValid = false;
        this.minorAllocValid = false;
    }
    
    public int getAxis() {
        return this.majorAxis;
    }
    
    public void setAxis(final int majorAxis) {
        final boolean b = majorAxis != this.majorAxis;
        this.majorAxis = majorAxis;
        if (b) {
            this.preferenceChanged(null, true, true);
        }
    }
    
    public void layoutChanged(final int n) {
        if (n == this.majorAxis) {
            this.majorAllocValid = false;
        }
        else {
            this.minorAllocValid = false;
        }
    }
    
    protected boolean isLayoutValid(final int n) {
        if (n == this.majorAxis) {
            return this.majorAllocValid;
        }
        return this.minorAllocValid;
    }
    
    protected void paintChild(final Graphics graphics, final Rectangle rectangle, final int n) {
        this.getView(n).paint(graphics, rectangle);
    }
    
    @Override
    public void replace(final int n, final int n2, final View[] array) {
        super.replace(n, n2, array);
        final int n3 = (array != null) ? array.length : 0;
        this.majorOffsets = this.updateLayoutArray(this.majorOffsets, n, n3);
        this.majorSpans = this.updateLayoutArray(this.majorSpans, n, n3);
        this.majorReqValid = false;
        this.majorAllocValid = false;
        this.minorOffsets = this.updateLayoutArray(this.minorOffsets, n, n3);
        this.minorSpans = this.updateLayoutArray(this.minorSpans, n, n3);
        this.minorReqValid = false;
        this.minorAllocValid = false;
    }
    
    int[] updateLayoutArray(final int[] array, final int n, final int n2) {
        final int viewCount = this.getViewCount();
        final int[] array2 = new int[viewCount];
        System.arraycopy(array, 0, array2, 0, n);
        System.arraycopy(array, n, array2, n + n2, viewCount - n2 - n);
        return array2;
    }
    
    @Override
    protected void forwardUpdate(final DocumentEvent.ElementChange elementChange, final DocumentEvent documentEvent, final Shape shape, final ViewFactory viewFactory) {
        final boolean layoutValid = this.isLayoutValid(this.majorAxis);
        super.forwardUpdate(elementChange, documentEvent, shape, viewFactory);
        if (layoutValid && !this.isLayoutValid(this.majorAxis)) {
            final Container container = this.getContainer();
            if (shape != null && container != null) {
                final int viewIndexAtPosition = this.getViewIndexAtPosition(documentEvent.getOffset());
                final Rectangle insideAllocation = this.getInsideAllocation(shape);
                if (this.majorAxis == 0) {
                    final Rectangle rectangle = insideAllocation;
                    rectangle.x += this.majorOffsets[viewIndexAtPosition];
                    final Rectangle rectangle2 = insideAllocation;
                    rectangle2.width -= this.majorOffsets[viewIndexAtPosition];
                }
                else {
                    final Rectangle rectangle3 = insideAllocation;
                    rectangle3.y += this.minorOffsets[viewIndexAtPosition];
                    final Rectangle rectangle4 = insideAllocation;
                    rectangle4.height -= this.minorOffsets[viewIndexAtPosition];
                }
                container.repaint(insideAllocation.x, insideAllocation.y, insideAllocation.width, insideAllocation.height);
            }
        }
    }
    
    @Override
    public void preferenceChanged(final View view, final boolean b, final boolean b2) {
        final boolean b3 = (this.majorAxis == 0) ? b : b2;
        final boolean b4 = (this.majorAxis == 0) ? b2 : b;
        if (b3) {
            this.majorReqValid = false;
            this.majorAllocValid = false;
        }
        if (b4) {
            this.minorReqValid = false;
            this.minorAllocValid = false;
        }
        super.preferenceChanged(view, b, b2);
    }
    
    @Override
    public int getResizeWeight(final int n) {
        this.checkRequests(n);
        if (n == this.majorAxis) {
            if (this.majorRequest.preferred != this.majorRequest.minimum || this.majorRequest.preferred != this.majorRequest.maximum) {
                return 1;
            }
        }
        else if (this.minorRequest.preferred != this.minorRequest.minimum || this.minorRequest.preferred != this.minorRequest.maximum) {
            return 1;
        }
        return 0;
    }
    
    void setSpanOnAxis(final int n, final float n2) {
        if (n == this.majorAxis) {
            if (this.majorSpan != (int)n2) {
                this.majorAllocValid = false;
            }
            if (!this.majorAllocValid) {
                this.majorSpan = (int)n2;
                this.checkRequests(this.majorAxis);
                this.layoutMajorAxis(this.majorSpan, n, this.majorOffsets, this.majorSpans);
                this.majorAllocValid = true;
                this.updateChildSizes();
            }
        }
        else {
            if ((int)n2 != this.minorSpan) {
                this.minorAllocValid = false;
            }
            if (!this.minorAllocValid) {
                this.minorSpan = (int)n2;
                this.checkRequests(n);
                this.layoutMinorAxis(this.minorSpan, n, this.minorOffsets, this.minorSpans);
                this.minorAllocValid = true;
                this.updateChildSizes();
            }
        }
    }
    
    void updateChildSizes() {
        final int viewCount = this.getViewCount();
        if (this.majorAxis == 0) {
            for (int i = 0; i < viewCount; ++i) {
                this.getView(i).setSize((float)this.majorSpans[i], (float)this.minorSpans[i]);
            }
        }
        else {
            for (int j = 0; j < viewCount; ++j) {
                this.getView(j).setSize((float)this.minorSpans[j], (float)this.majorSpans[j]);
            }
        }
    }
    
    float getSpanOnAxis(final int n) {
        if (n == this.majorAxis) {
            return (float)this.majorSpan;
        }
        return (float)this.minorSpan;
    }
    
    @Override
    public void setSize(final float n, final float n2) {
        this.layout(Math.max(0, (int)(n - this.getLeftInset() - this.getRightInset())), Math.max(0, (int)(n2 - this.getTopInset() - this.getBottomInset())));
    }
    
    @Override
    public void paint(final Graphics graphics, final Shape shape) {
        final Rectangle rectangle = (Rectangle)((shape instanceof Rectangle) ? shape : shape.getBounds());
        final int viewCount = this.getViewCount();
        final int n = rectangle.x + this.getLeftInset();
        final int n2 = rectangle.y + this.getTopInset();
        final Rectangle clipBounds = graphics.getClipBounds();
        for (int i = 0; i < viewCount; ++i) {
            this.tempRect.x = n + this.getOffset(0, i);
            this.tempRect.y = n2 + this.getOffset(1, i);
            this.tempRect.width = this.getSpan(0, i);
            this.tempRect.height = this.getSpan(1, i);
            final int x = this.tempRect.x;
            final int n3 = x + this.tempRect.width;
            final int y = this.tempRect.y;
            final int n4 = y + this.tempRect.height;
            final int x2 = clipBounds.x;
            final int n5 = x2 + clipBounds.width;
            final int y2 = clipBounds.y;
            final int n6 = y2 + clipBounds.height;
            if (n3 >= x2 && n4 >= y2 && n5 >= x && n6 >= y) {
                this.paintChild(graphics, this.tempRect, i);
            }
        }
    }
    
    @Override
    public Shape getChildAllocation(final int n, final Shape shape) {
        if (shape != null) {
            final Shape childAllocation = super.getChildAllocation(n, shape);
            if (childAllocation != null && !this.isAllocationValid()) {
                final Rectangle rectangle = (Rectangle)((childAllocation instanceof Rectangle) ? childAllocation : childAllocation.getBounds());
                if (rectangle.width == 0 && rectangle.height == 0) {
                    return null;
                }
            }
            return childAllocation;
        }
        return null;
    }
    
    @Override
    public Shape modelToView(final int n, final Shape shape, final Position.Bias bias) throws BadLocationException {
        if (!this.isAllocationValid()) {
            final Rectangle bounds = shape.getBounds();
            this.setSize((float)bounds.width, (float)bounds.height);
        }
        return super.modelToView(n, shape, bias);
    }
    
    @Override
    public int viewToModel(final float n, final float n2, final Shape shape, final Position.Bias[] array) {
        if (!this.isAllocationValid()) {
            final Rectangle bounds = shape.getBounds();
            this.setSize((float)bounds.width, (float)bounds.height);
        }
        return super.viewToModel(n, n2, shape, array);
    }
    
    @Override
    public float getAlignment(final int n) {
        this.checkRequests(n);
        if (n == this.majorAxis) {
            return this.majorRequest.alignment;
        }
        return this.minorRequest.alignment;
    }
    
    @Override
    public float getPreferredSpan(final int n) {
        this.checkRequests(n);
        final float n2 = (n == 0) ? ((float)(this.getLeftInset() + this.getRightInset())) : ((float)(this.getTopInset() + this.getBottomInset()));
        if (n == this.majorAxis) {
            return this.majorRequest.preferred + n2;
        }
        return this.minorRequest.preferred + n2;
    }
    
    @Override
    public float getMinimumSpan(final int n) {
        this.checkRequests(n);
        final float n2 = (n == 0) ? ((float)(this.getLeftInset() + this.getRightInset())) : ((float)(this.getTopInset() + this.getBottomInset()));
        if (n == this.majorAxis) {
            return this.majorRequest.minimum + n2;
        }
        return this.minorRequest.minimum + n2;
    }
    
    @Override
    public float getMaximumSpan(final int n) {
        this.checkRequests(n);
        final float n2 = (n == 0) ? ((float)(this.getLeftInset() + this.getRightInset())) : ((float)(this.getTopInset() + this.getBottomInset()));
        if (n == this.majorAxis) {
            return this.majorRequest.maximum + n2;
        }
        return this.minorRequest.maximum + n2;
    }
    
    protected boolean isAllocationValid() {
        return this.majorAllocValid && this.minorAllocValid;
    }
    
    @Override
    protected boolean isBefore(final int n, final int n2, final Rectangle rectangle) {
        if (this.majorAxis == 0) {
            return n < rectangle.x;
        }
        return n2 < rectangle.y;
    }
    
    @Override
    protected boolean isAfter(final int n, final int n2, final Rectangle rectangle) {
        if (this.majorAxis == 0) {
            return n > rectangle.width + rectangle.x;
        }
        return n2 > rectangle.height + rectangle.y;
    }
    
    @Override
    protected View getViewAtPoint(final int n, final int n2, final Rectangle rectangle) {
        final int viewCount = this.getViewCount();
        if (this.majorAxis == 0) {
            if (n < rectangle.x + this.majorOffsets[0]) {
                this.childAllocation(0, rectangle);
                return this.getView(0);
            }
            for (int i = 0; i < viewCount; ++i) {
                if (n < rectangle.x + this.majorOffsets[i]) {
                    this.childAllocation(i - 1, rectangle);
                    return this.getView(i - 1);
                }
            }
            this.childAllocation(viewCount - 1, rectangle);
            return this.getView(viewCount - 1);
        }
        else {
            if (n2 < rectangle.y + this.majorOffsets[0]) {
                this.childAllocation(0, rectangle);
                return this.getView(0);
            }
            for (int j = 0; j < viewCount; ++j) {
                if (n2 < rectangle.y + this.majorOffsets[j]) {
                    this.childAllocation(j - 1, rectangle);
                    return this.getView(j - 1);
                }
            }
            this.childAllocation(viewCount - 1, rectangle);
            return this.getView(viewCount - 1);
        }
    }
    
    @Override
    protected void childAllocation(final int n, final Rectangle rectangle) {
        rectangle.x += this.getOffset(0, n);
        rectangle.y += this.getOffset(1, n);
        rectangle.width = this.getSpan(0, n);
        rectangle.height = this.getSpan(1, n);
    }
    
    protected void layout(final int n, final int n2) {
        this.setSpanOnAxis(0, (float)n);
        this.setSpanOnAxis(1, (float)n2);
    }
    
    public int getWidth() {
        int n;
        if (this.majorAxis == 0) {
            n = this.majorSpan;
        }
        else {
            n = this.minorSpan;
        }
        return n + (this.getLeftInset() - this.getRightInset());
    }
    
    public int getHeight() {
        int n;
        if (this.majorAxis == 1) {
            n = this.majorSpan;
        }
        else {
            n = this.minorSpan;
        }
        return n + (this.getTopInset() - this.getBottomInset());
    }
    
    protected void layoutMajorAxis(final int n, final int n2, final int[] array, final int[] array2) {
        long n3 = 0L;
        final int viewCount = this.getViewCount();
        for (int i = 0; i < viewCount; ++i) {
            array2[i] = (int)this.getView(i).getPreferredSpan(n2);
            n3 += array2[i];
        }
        final long n4 = n - n3;
        float max = 0.0f;
        int[] array3 = null;
        if (n4 != 0L) {
            long n5 = 0L;
            array3 = new int[viewCount];
            for (int j = 0; j < viewCount; ++j) {
                final View view = this.getView(j);
                int n6;
                if (n4 < 0L) {
                    n6 = (int)view.getMinimumSpan(n2);
                    array3[j] = array2[j] - n6;
                }
                else {
                    n6 = (int)view.getMaximumSpan(n2);
                    array3[j] = n6 - array2[j];
                }
                n5 += n6;
            }
            max = Math.max(Math.min(n4 / (float)Math.abs(n5 - n3), 1.0f), -1.0f);
        }
        int n7 = 0;
        for (int k = 0; k < viewCount; ++k) {
            array[k] = n7;
            if (n4 != 0L) {
                final float n8 = max * array3[k];
                final int n9 = k;
                array2[n9] += Math.round(n8);
            }
            n7 = (int)Math.min(n7 + (long)array2[k], 2147483647L);
        }
    }
    
    protected void layoutMinorAxis(final int n, final int n2, final int[] array, final int[] array2) {
        for (int viewCount = this.getViewCount(), i = 0; i < viewCount; ++i) {
            final View view = this.getView(i);
            final int n3 = (int)view.getMaximumSpan(n2);
            if (n3 < n) {
                array[i] = (int)((n - n3) * view.getAlignment(n2));
                array2[i] = n3;
            }
            else {
                final int n4 = (int)view.getMinimumSpan(n2);
                array[i] = 0;
                array2[i] = Math.max(n4, n);
            }
        }
    }
    
    protected SizeRequirements calculateMajorAxisRequirements(final int n, SizeRequirements sizeRequirements) {
        float n2 = 0.0f;
        float n3 = 0.0f;
        float n4 = 0.0f;
        for (int viewCount = this.getViewCount(), i = 0; i < viewCount; ++i) {
            final View view = this.getView(i);
            n2 += view.getMinimumSpan(n);
            n3 += view.getPreferredSpan(n);
            n4 += view.getMaximumSpan(n);
        }
        if (sizeRequirements == null) {
            sizeRequirements = new SizeRequirements();
        }
        sizeRequirements.alignment = 0.5f;
        sizeRequirements.minimum = (int)n2;
        sizeRequirements.preferred = (int)n3;
        sizeRequirements.maximum = (int)n4;
        return sizeRequirements;
    }
    
    protected SizeRequirements calculateMinorAxisRequirements(final int n, SizeRequirements sizeRequirements) {
        int max = 0;
        long max2 = 0L;
        int max3 = Integer.MAX_VALUE;
        for (int viewCount = this.getViewCount(), i = 0; i < viewCount; ++i) {
            final View view = this.getView(i);
            max = Math.max((int)view.getMinimumSpan(n), max);
            max2 = Math.max((int)view.getPreferredSpan(n), max2);
            max3 = Math.max((int)view.getMaximumSpan(n), max3);
        }
        if (sizeRequirements == null) {
            sizeRequirements = new SizeRequirements();
            sizeRequirements.alignment = 0.5f;
        }
        sizeRequirements.preferred = (int)max2;
        sizeRequirements.minimum = max;
        sizeRequirements.maximum = max3;
        return sizeRequirements;
    }
    
    void checkRequests(final int n) {
        if (n != 0 && n != 1) {
            throw new IllegalArgumentException("Invalid axis: " + n);
        }
        if (n == this.majorAxis) {
            if (!this.majorReqValid) {
                this.majorRequest = this.calculateMajorAxisRequirements(n, this.majorRequest);
                this.majorReqValid = true;
            }
        }
        else if (!this.minorReqValid) {
            this.minorRequest = this.calculateMinorAxisRequirements(n, this.minorRequest);
            this.minorReqValid = true;
        }
    }
    
    protected void baselineLayout(final int n, final int n2, final int[] array, final int[] array2) {
        final int n3 = (int)(n * this.getAlignment(n2));
        final int n4 = n - n3;
        for (int viewCount = this.getViewCount(), i = 0; i < viewCount; ++i) {
            final View view = this.getView(i);
            final float alignment = view.getAlignment(n2);
            float n5;
            if (view.getResizeWeight(n2) > 0) {
                final float minimumSpan = view.getMinimumSpan(n2);
                final float maximumSpan = view.getMaximumSpan(n2);
                if (alignment == 0.0f) {
                    n5 = Math.max(Math.min(maximumSpan, (float)n4), minimumSpan);
                }
                else if (alignment == 1.0f) {
                    n5 = Math.max(Math.min(maximumSpan, (float)n3), minimumSpan);
                }
                else {
                    n5 = Math.max(Math.min(maximumSpan, Math.min(n3 / alignment, n4 / (1.0f - alignment))), minimumSpan);
                }
            }
            else {
                n5 = view.getPreferredSpan(n2);
            }
            array[i] = n3 - (int)(n5 * alignment);
            array2[i] = (int)n5;
        }
    }
    
    protected SizeRequirements baselineRequirements(final int n, SizeRequirements sizeRequirements) {
        final SizeRequirements sizeRequirements2 = new SizeRequirements();
        final SizeRequirements sizeRequirements3 = new SizeRequirements();
        if (sizeRequirements == null) {
            sizeRequirements = new SizeRequirements();
        }
        sizeRequirements.alignment = 0.5f;
        for (int viewCount = this.getViewCount(), i = 0; i < viewCount; ++i) {
            final View view = this.getView(i);
            final float alignment = view.getAlignment(n);
            final float preferredSpan = view.getPreferredSpan(n);
            final int n2 = (int)(alignment * preferredSpan);
            final int n3 = (int)(preferredSpan - n2);
            sizeRequirements2.preferred = Math.max(n2, sizeRequirements2.preferred);
            sizeRequirements3.preferred = Math.max(n3, sizeRequirements3.preferred);
            if (view.getResizeWeight(n) > 0) {
                final float minimumSpan = view.getMinimumSpan(n);
                final int n4 = (int)(alignment * minimumSpan);
                final int n5 = (int)(minimumSpan - n4);
                sizeRequirements2.minimum = Math.max(n4, sizeRequirements2.minimum);
                sizeRequirements3.minimum = Math.max(n5, sizeRequirements3.minimum);
                final float maximumSpan = view.getMaximumSpan(n);
                final int n6 = (int)(alignment * maximumSpan);
                final int n7 = (int)(maximumSpan - n6);
                sizeRequirements2.maximum = Math.max(n6, sizeRequirements2.maximum);
                sizeRequirements3.maximum = Math.max(n7, sizeRequirements3.maximum);
            }
            else {
                sizeRequirements2.minimum = Math.max(n2, sizeRequirements2.minimum);
                sizeRequirements3.minimum = Math.max(n3, sizeRequirements3.minimum);
                sizeRequirements2.maximum = Math.max(n2, sizeRequirements2.maximum);
                sizeRequirements3.maximum = Math.max(n3, sizeRequirements3.maximum);
            }
        }
        sizeRequirements.preferred = (int)Math.min(sizeRequirements2.preferred + (long)sizeRequirements3.preferred, 2147483647L);
        if (sizeRequirements.preferred > 0) {
            sizeRequirements.alignment = sizeRequirements2.preferred / (float)sizeRequirements.preferred;
        }
        if (sizeRequirements.alignment == 0.0f) {
            sizeRequirements.minimum = sizeRequirements3.minimum;
            sizeRequirements.maximum = sizeRequirements3.maximum;
        }
        else if (sizeRequirements.alignment == 1.0f) {
            sizeRequirements.minimum = sizeRequirements2.minimum;
            sizeRequirements.maximum = sizeRequirements2.maximum;
        }
        else {
            sizeRequirements.minimum = Math.round(Math.max(sizeRequirements2.minimum / sizeRequirements.alignment, sizeRequirements3.minimum / (1.0f - sizeRequirements.alignment)));
            sizeRequirements.maximum = Math.round(Math.min(sizeRequirements2.maximum / sizeRequirements.alignment, sizeRequirements3.maximum / (1.0f - sizeRequirements.alignment)));
        }
        return sizeRequirements;
    }
    
    protected int getOffset(final int n, final int n2) {
        return ((n == this.majorAxis) ? this.majorOffsets : this.minorOffsets)[n2];
    }
    
    protected int getSpan(final int n, final int n2) {
        return ((n == this.majorAxis) ? this.majorSpans : this.minorSpans)[n2];
    }
    
    @Override
    protected boolean flipEastAndWestAtEnds(final int n, final Position.Bias bias) {
        if (this.majorAxis == 1) {
            final int viewIndexAtPosition = this.getViewIndexAtPosition((bias == Position.Bias.Backward) ? Math.max(0, n - 1) : n);
            if (viewIndexAtPosition != -1) {
                final View view = this.getView(viewIndexAtPosition);
                if (view != null && view instanceof CompositeView) {
                    return ((CompositeView)view).flipEastAndWestAtEnds(n, bias);
                }
            }
        }
        return false;
    }
}
