package sun.awt.geom;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Comparator;

public abstract class AreaOp
{
    public static final int CTAG_LEFT = 0;
    public static final int CTAG_RIGHT = 1;
    public static final int ETAG_IGNORE = 0;
    public static final int ETAG_ENTER = 1;
    public static final int ETAG_EXIT = -1;
    public static final int RSTAG_INSIDE = 1;
    public static final int RSTAG_OUTSIDE = -1;
    private static Comparator YXTopComparator;
    private static CurveLink[] EmptyLinkList;
    private static ChainEnd[] EmptyChainList;
    
    private AreaOp() {
    }
    
    public abstract void newRow();
    
    public abstract int classify(final Edge p0);
    
    public abstract int getState();
    
    public Vector calculate(final Vector vector, final Vector vector2) {
        final Vector vector3 = new Vector();
        addEdges(vector3, vector, 0);
        addEdges(vector3, vector2, 1);
        return this.pruneEdges(vector3);
    }
    
    private static void addEdges(final Vector vector, final Vector vector2, final int n) {
        final Enumeration elements = vector2.elements();
        while (elements.hasMoreElements()) {
            final Curve curve = (Curve)elements.nextElement();
            if (curve.getOrder() > 0) {
                vector.add(new Edge(curve, n));
            }
        }
    }
    
    private Vector pruneEdges(final Vector vector) {
        final int size = vector.size();
        if (size < 2) {
            return vector;
        }
        final Edge[] array = vector.toArray(new Edge[size]);
        Arrays.sort(array, AreaOp.YXTopComparator);
        int i = 0;
        int n = 0;
        final double[] array2 = new double[2];
        final Vector vector2 = new Vector();
        final Vector vector3 = new Vector();
        final Vector vector4 = new Vector();
        while (i < size) {
            double yTop = array2[0];
            int j;
            int n2;
            for (n2 = (j = n - 1); j >= i; --j) {
                final Edge edge = array[j];
                if (edge.getCurve().getYBot() > yTop) {
                    if (n2 > j) {
                        array[n2] = edge;
                    }
                    --n2;
                }
            }
            i = n2 + 1;
            if (i >= n) {
                if (n >= size) {
                    break;
                }
                yTop = array[n].getCurve().getYTop();
                if (yTop > array2[0]) {
                    finalizeSubCurves(vector2, vector3);
                }
                array2[0] = yTop;
            }
            while (n < size && array[n].getCurve().getYTop() <= yTop) {
                ++n;
            }
            array2[1] = array[i].getCurve().getYBot();
            if (n < size) {
                final double yTop2 = array[n].getCurve().getYTop();
                if (array2[1] > yTop2) {
                    array2[1] = yTop2;
                }
            }
            int n3 = 1;
            for (int k = i; k < n; ++k) {
                final Edge edge2 = array[k];
                edge2.setEquivalence(0);
                int l = k;
                while (l > i) {
                    final Edge edge3 = array[l - 1];
                    final int compareTo = edge2.compareTo(edge3, array2);
                    if (array2[1] <= array2[0]) {
                        throw new InternalError("backstepping to " + array2[1] + " from " + array2[0]);
                    }
                    if (compareTo >= 0) {
                        if (compareTo == 0) {
                            int equivalence = edge3.getEquivalence();
                            if (equivalence == 0) {
                                equivalence = n3++;
                                edge3.setEquivalence(equivalence);
                            }
                            edge2.setEquivalence(equivalence);
                            break;
                        }
                        break;
                    }
                    else {
                        array[l] = edge3;
                        --l;
                    }
                }
                array[l] = edge2;
            }
            this.newRow();
            final double n4 = array2[0];
            final double n5 = array2[1];
            for (int n6 = i; n6 < n; ++n6) {
                Edge edge4 = array[n6];
                final int equivalence2 = edge4.getEquivalence();
                int classify;
                if (equivalence2 != 0) {
                    final int state = this.getState();
                    classify = ((state == 1) ? -1 : 1);
                    Edge edge5 = null;
                    Edge edge6 = edge4;
                    double n7 = n5;
                    do {
                        this.classify(edge4);
                        if (edge5 == null && edge4.isActiveFor(n4, classify)) {
                            edge5 = edge4;
                        }
                        final double yBot = edge4.getCurve().getYBot();
                        if (yBot > n7) {
                            edge6 = edge4;
                            n7 = yBot;
                        }
                    } while (++n6 < n && (edge4 = array[n6]).getEquivalence() == equivalence2);
                    --n6;
                    if (this.getState() == state) {
                        classify = 0;
                    }
                    else {
                        edge4 = ((edge5 != null) ? edge5 : edge6);
                    }
                }
                else {
                    classify = this.classify(edge4);
                }
                if (classify != 0) {
                    edge4.record(n5, classify);
                    vector4.add(new CurveLink(edge4.getCurve(), n4, n5, classify));
                }
            }
            if (this.getState() != -1) {
                System.out.println("Still inside at end of active edge list!");
                System.out.println("num curves = " + (n - i));
                System.out.println("num links = " + vector4.size());
                System.out.println("y top = " + array2[0]);
                if (n < size) {
                    System.out.println("y top of next curve = " + array[n].getCurve().getYTop());
                }
                else {
                    System.out.println("no more curves");
                }
                for (int n8 = i; n8 < n; ++n8) {
                    final Edge edge7 = array[n8];
                    System.out.println(edge7);
                    final int equivalence3 = edge7.getEquivalence();
                    if (equivalence3 != 0) {
                        System.out.println("  was equal to " + equivalence3 + "...");
                    }
                }
            }
            resolveLinks(vector2, vector3, vector4);
            vector4.clear();
            array2[0] = n5;
        }
        finalizeSubCurves(vector2, vector3);
        final Vector<Curve> vector5 = new Vector<Curve>();
        final Enumeration elements = vector2.elements();
        while (elements.hasMoreElements()) {
            CurveLink curveLink = (CurveLink)elements.nextElement();
            vector5.add(curveLink.getMoveto());
            CurveLink next = curveLink;
            while ((next = next.getNext()) != null) {
                if (!curveLink.absorb(next)) {
                    vector5.add(curveLink.getSubCurve());
                    curveLink = next;
                }
            }
            vector5.add(curveLink.getSubCurve());
        }
        return vector5;
    }
    
    public static void finalizeSubCurves(final Vector vector, final Vector vector2) {
        final int size = vector2.size();
        if (size == 0) {
            return;
        }
        if ((size & 0x1) != 0x0) {
            throw new InternalError("Odd number of chains!");
        }
        final ChainEnd[] array = new ChainEnd[size];
        vector2.toArray(array);
        for (int i = 1; i < size; i += 2) {
            final CurveLink linkTo = array[i - 1].linkTo(array[i]);
            if (linkTo != null) {
                vector.add(linkTo);
            }
        }
        vector2.clear();
    }
    
    public static void resolveLinks(final Vector vector, final Vector vector2, final Vector vector3) {
        final int size = vector3.size();
        CurveLink[] emptyLinkList;
        if (size == 0) {
            emptyLinkList = AreaOp.EmptyLinkList;
        }
        else {
            if ((size & 0x1) != 0x0) {
                throw new InternalError("Odd number of new curves!");
            }
            emptyLinkList = new CurveLink[size + 2];
            vector3.toArray(emptyLinkList);
        }
        final int size2 = vector2.size();
        ChainEnd[] emptyChainList;
        if (size2 == 0) {
            emptyChainList = AreaOp.EmptyChainList;
        }
        else {
            if ((size2 & 0x1) != 0x0) {
                throw new InternalError("Odd number of chains!");
            }
            emptyChainList = new ChainEnd[size2 + 2];
            vector2.toArray(emptyChainList);
        }
        int n = 0;
        int n2 = 0;
        vector2.clear();
        ChainEnd chainEnd = emptyChainList[0];
        ChainEnd chainEnd2 = emptyChainList[1];
        for (CurveLink curveLink = emptyLinkList[0], curveLink2 = emptyLinkList[1]; chainEnd != null || curveLink != null; chainEnd = chainEnd2, chainEnd2 = emptyChainList[n + 1], ++n2, curveLink = curveLink2, curveLink2 = emptyLinkList[n2 + 1]) {
            boolean b = curveLink == null;
            boolean b2 = chainEnd == null;
            if (!b && !b2) {
                b = ((n & 0x1) == 0x0 && chainEnd.getX() == chainEnd2.getX());
                b2 = ((n2 & 0x1) == 0x0 && curveLink.getX() == curveLink2.getX());
                if (!b && !b2) {
                    final double x = chainEnd.getX();
                    final double x2 = curveLink.getX();
                    b = (chainEnd2 != null && x < x2 && obstructs(chainEnd2.getX(), x2, n));
                    b2 = (curveLink2 != null && x2 < x && obstructs(curveLink2.getX(), x, n2));
                }
            }
            if (b) {
                final CurveLink linkTo = chainEnd.linkTo(chainEnd2);
                if (linkTo != null) {
                    vector.add(linkTo);
                }
                n += 2;
                chainEnd = emptyChainList[n];
                chainEnd2 = emptyChainList[n + 1];
            }
            if (b2) {
                final ChainEnd chainEnd3 = new ChainEnd(curveLink, null);
                final ChainEnd otherEnd = new ChainEnd(curveLink2, chainEnd3);
                chainEnd3.setOtherEnd(otherEnd);
                vector2.add(chainEnd3);
                vector2.add(otherEnd);
                n2 += 2;
                curveLink = emptyLinkList[n2];
                curveLink2 = emptyLinkList[n2 + 1];
            }
            if (!b && !b2) {
                chainEnd.addLink(curveLink);
                vector2.add(chainEnd);
                ++n;
            }
        }
        if ((vector2.size() & 0x1) != 0x0) {
            System.out.println("Odd number of chains!");
        }
    }
    
    public static boolean obstructs(final double n, final double n2, final int n3) {
        return ((n3 & 0x1) == 0x0) ? (n <= n2) : (n < n2);
    }
    
    static {
        AreaOp.YXTopComparator = new Comparator() {
            @Override
            public int compare(final Object o, final Object o2) {
                final Curve curve = ((Edge)o).getCurve();
                final Curve curve2 = ((Edge)o2).getCurve();
                double n = curve.getYTop();
                double n2;
                if (n == (n2 = curve2.getYTop()) && (n = curve.getXTop()) == (n2 = curve2.getXTop())) {
                    return 0;
                }
                if (n < n2) {
                    return -1;
                }
                return 1;
            }
        };
        AreaOp.EmptyLinkList = new CurveLink[2];
        AreaOp.EmptyChainList = new ChainEnd[2];
    }
    
    public abstract static class CAGOp extends AreaOp
    {
        boolean inLeft;
        boolean inRight;
        boolean inResult;
        
        public CAGOp() {
            super(null);
        }
        
        @Override
        public void newRow() {
            this.inLeft = false;
            this.inRight = false;
            this.inResult = false;
        }
        
        @Override
        public int classify(final Edge edge) {
            if (edge.getCurveTag() == 0) {
                this.inLeft = !this.inLeft;
            }
            else {
                this.inRight = !this.inRight;
            }
            final boolean classification = this.newClassification(this.inLeft, this.inRight);
            if (this.inResult == classification) {
                return 0;
            }
            this.inResult = classification;
            return classification ? 1 : -1;
        }
        
        @Override
        public int getState() {
            return this.inResult ? 1 : -1;
        }
        
        public abstract boolean newClassification(final boolean p0, final boolean p1);
    }
    
    public static class AddOp extends CAGOp
    {
        @Override
        public boolean newClassification(final boolean b, final boolean b2) {
            return b || b2;
        }
    }
    
    public static class SubOp extends CAGOp
    {
        @Override
        public boolean newClassification(final boolean b, final boolean b2) {
            return b && !b2;
        }
    }
    
    public static class IntOp extends CAGOp
    {
        @Override
        public boolean newClassification(final boolean b, final boolean b2) {
            return b && b2;
        }
    }
    
    public static class XorOp extends CAGOp
    {
        @Override
        public boolean newClassification(final boolean b, final boolean b2) {
            return b != b2;
        }
    }
    
    public static class NZWindOp extends AreaOp
    {
        private int count;
        
        public NZWindOp() {
            super(null);
        }
        
        @Override
        public void newRow() {
            this.count = 0;
        }
        
        @Override
        public int classify(final Edge edge) {
            final int count = this.count;
            final boolean b = count == 0;
            final int count2 = count + edge.getCurve().getDirection();
            this.count = count2;
            return (count2 == 0) ? -1 : b;
        }
        
        @Override
        public int getState() {
            return (this.count == 0) ? -1 : 1;
        }
    }
    
    public static class EOWindOp extends AreaOp
    {
        private boolean inside;
        
        public EOWindOp() {
            super(null);
        }
        
        @Override
        public void newRow() {
            this.inside = false;
        }
        
        @Override
        public int classify(final Edge edge) {
            final boolean inside = !this.inside;
            this.inside = inside;
            return inside ? 1 : -1;
        }
        
        @Override
        public int getState() {
            return this.inside ? 1 : -1;
        }
    }
}
