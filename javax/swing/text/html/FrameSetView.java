package javax.swing.text.html;

import javax.swing.text.View;
import javax.swing.SizeRequirements;
import javax.swing.text.AttributeSet;
import java.util.StringTokenizer;
import javax.swing.text.Element;
import javax.swing.text.BoxView;

class FrameSetView extends BoxView
{
    String[] children;
    int[] percentChildren;
    int[] absoluteChildren;
    int[] relativeChildren;
    int percentTotals;
    int absoluteTotals;
    int relativeTotals;
    
    public FrameSetView(final Element element, final int n) {
        super(element, n);
        this.children = null;
    }
    
    private String[] parseRowColSpec(final HTML.Attribute attribute) {
        final AttributeSet attributes = this.getElement().getAttributes();
        String s = "*";
        if (attributes != null && attributes.getAttribute(attribute) != null) {
            s = (String)attributes.getAttribute(attribute);
        }
        final StringTokenizer stringTokenizer = new StringTokenizer(s, ",");
        final int countTokens = stringTokenizer.countTokens();
        final String[] array = new String[Math.max(countTokens, this.getViewCount())];
        int i;
        for (i = 0; i < countTokens; ++i) {
            array[i] = stringTokenizer.nextToken().trim();
            if (array[i].equals("100%")) {
                array[i] = "*";
            }
        }
        while (i < array.length) {
            array[i] = "*";
            ++i;
        }
        return array;
    }
    
    private void init() {
        if (this.getAxis() == 1) {
            this.children = this.parseRowColSpec(HTML.Attribute.ROWS);
        }
        else {
            this.children = this.parseRowColSpec(HTML.Attribute.COLS);
        }
        this.percentChildren = new int[this.children.length];
        this.relativeChildren = new int[this.children.length];
        this.absoluteChildren = new int[this.children.length];
        for (int i = 0; i < this.children.length; ++i) {
            this.percentChildren[i] = -1;
            this.relativeChildren[i] = -1;
            this.absoluteChildren[i] = -1;
            if (this.children[i].endsWith("*")) {
                if (this.children[i].length() > 1) {
                    this.relativeChildren[i] = Integer.parseInt(this.children[i].substring(0, this.children[i].length() - 1));
                    this.relativeTotals += this.relativeChildren[i];
                }
                else {
                    this.relativeChildren[i] = 1;
                    ++this.relativeTotals;
                }
            }
            else if (this.children[i].indexOf(37) != -1) {
                this.percentChildren[i] = this.parseDigits(this.children[i]);
                this.percentTotals += this.percentChildren[i];
            }
            else {
                this.absoluteChildren[i] = Integer.parseInt(this.children[i]);
            }
        }
        if (this.percentTotals > 100) {
            for (int j = 0; j < this.percentChildren.length; ++j) {
                if (this.percentChildren[j] > 0) {
                    this.percentChildren[j] = this.percentChildren[j] * 100 / this.percentTotals;
                }
            }
            this.percentTotals = 100;
        }
    }
    
    @Override
    protected void layoutMajorAxis(final int n, final int n2, final int[] array, final int[] array2) {
        if (this.children == null) {
            this.init();
        }
        SizeRequirements.calculateTiledPositions(n, null, this.getChildRequests(n, n2), array, array2);
    }
    
    protected SizeRequirements[] getChildRequests(final int n, final int n2) {
        final int[] array = new int[this.children.length];
        this.spread(n, array);
        final int viewCount = this.getViewCount();
        final SizeRequirements[] array2 = new SizeRequirements[viewCount];
        int i = 0;
        int n3 = 0;
        while (i < viewCount) {
            final View view = this.getView(i);
            if (view instanceof FrameView || view instanceof FrameSetView) {
                array2[i] = new SizeRequirements((int)view.getMinimumSpan(n2), array[n3], (int)view.getMaximumSpan(n2), 0.5f);
                ++n3;
            }
            else {
                array2[i] = new SizeRequirements((int)view.getMinimumSpan(n2), (int)view.getPreferredSpan(n2), (int)view.getMaximumSpan(n2), view.getAlignment(n2));
            }
            ++i;
        }
        return array2;
    }
    
    private void spread(final int n, final int[] array) {
        if (n == 0) {
            return;
        }
        int n2 = n;
        for (int i = 0; i < array.length; ++i) {
            if (this.absoluteChildren[i] > 0) {
                array[i] = this.absoluteChildren[i];
                n2 -= array[i];
            }
        }
        final int n3 = n2;
        for (int j = 0; j < array.length; ++j) {
            if (this.percentChildren[j] > 0 && n3 > 0) {
                array[j] = this.percentChildren[j] * n3 / 100;
                n2 -= array[j];
            }
            else if (this.percentChildren[j] > 0 && n3 <= 0) {
                array[j] = n / array.length;
                n2 -= array[j];
            }
        }
        if (n2 > 0 && this.relativeTotals > 0) {
            for (int k = 0; k < array.length; ++k) {
                if (this.relativeChildren[k] > 0) {
                    array[k] = n2 * this.relativeChildren[k] / this.relativeTotals;
                }
            }
        }
        else if (n2 > 0) {
            final float n4 = (float)(n - n2);
            final float[] array2 = new float[array.length];
            int l = n;
            for (int n5 = 0; n5 < array.length; ++n5) {
                array2[n5] = array[n5] / n4 * 100.0f;
                array[n5] = (int)(n * array2[n5] / 100.0f);
                l -= array[n5];
            }
            int n6 = 0;
            while (l != 0) {
                if (l < 0) {
                    final int n7 = n6++;
                    --array[n7];
                    ++l;
                }
                else {
                    final int n8 = n6++;
                    ++array[n8];
                    --l;
                }
                if (n6 == array.length) {
                    n6 = 0;
                }
            }
        }
    }
    
    private int parseDigits(final String s) {
        int n = 0;
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            if (Character.isDigit(char1)) {
                n = n * 10 + Character.digit(char1, 10);
            }
        }
        return n;
    }
}
