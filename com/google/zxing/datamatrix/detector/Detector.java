package com.google.zxing.datamatrix.detector;

import java.io.Serializable;
import com.google.zxing.common.GridSampler;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.google.zxing.ResultPoint;
import java.util.HashMap;
import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;
import com.google.zxing.common.DetectorResult;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.detector.WhiteRectangleDetector;
import com.google.zxing.common.BitMatrix;

public final class Detector
{
    private final BitMatrix image;
    private final WhiteRectangleDetector rectangleDetector;
    
    public Detector(final BitMatrix image) throws NotFoundException {
        this.image = image;
        this.rectangleDetector = new WhiteRectangleDetector(image);
    }
    
    public DetectorResult detect() throws NotFoundException {
        final ResultPoint[] cornerPoints = this.rectangleDetector.detect();
        final ResultPoint pointA = cornerPoints[0];
        final ResultPoint pointB = cornerPoints[1];
        final ResultPoint pointC = cornerPoints[2];
        final ResultPoint pointD = cornerPoints[3];
        final List<ResultPointsAndTransitions> transitions = new ArrayList<ResultPointsAndTransitions>(4);
        transitions.add(this.transitionsBetween(pointA, pointB));
        transitions.add(this.transitionsBetween(pointA, pointC));
        transitions.add(this.transitionsBetween(pointB, pointD));
        transitions.add(this.transitionsBetween(pointC, pointD));
        Collections.sort(transitions, new ResultPointsAndTransitionsComparator());
        final ResultPointsAndTransitions lSideOne = transitions.get(0);
        final ResultPointsAndTransitions lSideTwo = transitions.get(1);
        final Map<ResultPoint, Integer> pointCount = new HashMap<ResultPoint, Integer>();
        increment(pointCount, lSideOne.getFrom());
        increment(pointCount, lSideOne.getTo());
        increment(pointCount, lSideTwo.getFrom());
        increment(pointCount, lSideTwo.getTo());
        ResultPoint maybeTopLeft = null;
        ResultPoint bottomLeft = null;
        ResultPoint maybeBottomRight = null;
        for (final Map.Entry<ResultPoint, Integer> entry : pointCount.entrySet()) {
            final ResultPoint point = entry.getKey();
            final Integer value = entry.getValue();
            if (value == 2) {
                bottomLeft = point;
            }
            else if (maybeTopLeft == null) {
                maybeTopLeft = point;
            }
            else {
                maybeBottomRight = point;
            }
        }
        if (maybeTopLeft == null || bottomLeft == null || maybeBottomRight == null) {
            throw NotFoundException.getNotFoundInstance();
        }
        final ResultPoint[] corners = { maybeTopLeft, bottomLeft, maybeBottomRight };
        ResultPoint.orderBestPatterns(corners);
        final ResultPoint bottomRight = corners[0];
        bottomLeft = corners[1];
        final ResultPoint topLeft = corners[2];
        ResultPoint topRight;
        if (!pointCount.containsKey(pointA)) {
            topRight = pointA;
        }
        else if (!pointCount.containsKey(pointB)) {
            topRight = pointB;
        }
        else if (!pointCount.containsKey(pointC)) {
            topRight = pointC;
        }
        else {
            topRight = pointD;
        }
        int dimensionTop = this.transitionsBetween(topLeft, topRight).getTransitions();
        int dimensionRight = this.transitionsBetween(bottomRight, topRight).getTransitions();
        if ((dimensionTop & 0x1) == 0x1) {
            ++dimensionTop;
        }
        dimensionTop += 2;
        if ((dimensionRight & 0x1) == 0x1) {
            ++dimensionRight;
        }
        dimensionRight += 2;
        ResultPoint correctedTopRight;
        BitMatrix bits;
        if (4 * dimensionTop >= 7 * dimensionRight || 4 * dimensionRight >= 7 * dimensionTop) {
            correctedTopRight = this.correctTopRightRectangular(bottomLeft, bottomRight, topLeft, topRight, dimensionTop, dimensionRight);
            if (correctedTopRight == null) {
                correctedTopRight = topRight;
            }
            dimensionTop = this.transitionsBetween(topLeft, correctedTopRight).getTransitions();
            dimensionRight = this.transitionsBetween(bottomRight, correctedTopRight).getTransitions();
            if ((dimensionTop & 0x1) == 0x1) {
                ++dimensionTop;
            }
            if ((dimensionRight & 0x1) == 0x1) {
                ++dimensionRight;
            }
            bits = sampleGrid(this.image, topLeft, bottomLeft, bottomRight, correctedTopRight, dimensionTop, dimensionRight);
        }
        else {
            final int dimension = Math.min(dimensionRight, dimensionTop);
            correctedTopRight = this.correctTopRight(bottomLeft, bottomRight, topLeft, topRight, dimension);
            if (correctedTopRight == null) {
                correctedTopRight = topRight;
            }
            int dimensionCorrected = Math.max(this.transitionsBetween(topLeft, correctedTopRight).getTransitions(), this.transitionsBetween(bottomRight, correctedTopRight).getTransitions());
            if ((++dimensionCorrected & 0x1) == 0x1) {
                ++dimensionCorrected;
            }
            bits = sampleGrid(this.image, topLeft, bottomLeft, bottomRight, correctedTopRight, dimensionCorrected, dimensionCorrected);
        }
        return new DetectorResult(bits, new ResultPoint[] { topLeft, bottomLeft, bottomRight, correctedTopRight });
    }
    
    private ResultPoint correctTopRightRectangular(final ResultPoint bottomLeft, final ResultPoint bottomRight, final ResultPoint topLeft, final ResultPoint topRight, final int dimensionTop, final int dimensionRight) {
        float corr = distance(bottomLeft, bottomRight) / (float)dimensionTop;
        int norm = distance(topLeft, topRight);
        float cos = (topRight.getX() - topLeft.getX()) / norm;
        float sin = (topRight.getY() - topLeft.getY()) / norm;
        final ResultPoint c1 = new ResultPoint(topRight.getX() + corr * cos, topRight.getY() + corr * sin);
        corr = distance(bottomLeft, topLeft) / (float)dimensionRight;
        norm = distance(bottomRight, topRight);
        cos = (topRight.getX() - bottomRight.getX()) / norm;
        sin = (topRight.getY() - bottomRight.getY()) / norm;
        final ResultPoint c2 = new ResultPoint(topRight.getX() + corr * cos, topRight.getY() + corr * sin);
        if (!this.isValid(c1)) {
            if (this.isValid(c2)) {
                return c2;
            }
            return null;
        }
        else {
            if (!this.isValid(c2)) {
                return c1;
            }
            final int l1 = Math.abs(dimensionTop - this.transitionsBetween(topLeft, c1).getTransitions()) + Math.abs(dimensionRight - this.transitionsBetween(bottomRight, c1).getTransitions());
            final int l2 = Math.abs(dimensionTop - this.transitionsBetween(topLeft, c2).getTransitions()) + Math.abs(dimensionRight - this.transitionsBetween(bottomRight, c2).getTransitions());
            if (l1 <= l2) {
                return c1;
            }
            return c2;
        }
    }
    
    private ResultPoint correctTopRight(final ResultPoint bottomLeft, final ResultPoint bottomRight, final ResultPoint topLeft, final ResultPoint topRight, final int dimension) {
        float corr = distance(bottomLeft, bottomRight) / (float)dimension;
        int norm = distance(topLeft, topRight);
        float cos = (topRight.getX() - topLeft.getX()) / norm;
        float sin = (topRight.getY() - topLeft.getY()) / norm;
        final ResultPoint c1 = new ResultPoint(topRight.getX() + corr * cos, topRight.getY() + corr * sin);
        corr = distance(bottomLeft, bottomRight) / (float)dimension;
        norm = distance(bottomRight, topRight);
        cos = (topRight.getX() - bottomRight.getX()) / norm;
        sin = (topRight.getY() - bottomRight.getY()) / norm;
        final ResultPoint c2 = new ResultPoint(topRight.getX() + corr * cos, topRight.getY() + corr * sin);
        if (!this.isValid(c1)) {
            if (this.isValid(c2)) {
                return c2;
            }
            return null;
        }
        else {
            if (!this.isValid(c2)) {
                return c1;
            }
            final int l1 = Math.abs(this.transitionsBetween(topLeft, c1).getTransitions() - this.transitionsBetween(bottomRight, c1).getTransitions());
            final int l2 = Math.abs(this.transitionsBetween(topLeft, c2).getTransitions() - this.transitionsBetween(bottomRight, c2).getTransitions());
            return (l1 <= l2) ? c1 : c2;
        }
    }
    
    private boolean isValid(final ResultPoint p) {
        return p.getX() >= 0.0f && p.getX() < this.image.getWidth() && p.getY() > 0.0f && p.getY() < this.image.getHeight();
    }
    
    private static int round(final float d) {
        return (int)(d + 0.5f);
    }
    
    private static int distance(final ResultPoint a, final ResultPoint b) {
        return round((float)Math.sqrt((a.getX() - b.getX()) * (a.getX() - b.getX()) + (a.getY() - b.getY()) * (a.getY() - b.getY())));
    }
    
    private static void increment(final Map<ResultPoint, Integer> table, final ResultPoint key) {
        final Integer value = table.get(key);
        table.put(key, (value == null) ? 1 : (value + 1));
    }
    
    private static BitMatrix sampleGrid(final BitMatrix image, final ResultPoint topLeft, final ResultPoint bottomLeft, final ResultPoint bottomRight, final ResultPoint topRight, final int dimensionX, final int dimensionY) throws NotFoundException {
        final GridSampler sampler = GridSampler.getInstance();
        return sampler.sampleGrid(image, dimensionX, dimensionY, 0.5f, 0.5f, dimensionX - 0.5f, 0.5f, dimensionX - 0.5f, dimensionY - 0.5f, 0.5f, dimensionY - 0.5f, topLeft.getX(), topLeft.getY(), topRight.getX(), topRight.getY(), bottomRight.getX(), bottomRight.getY(), bottomLeft.getX(), bottomLeft.getY());
    }
    
    private ResultPointsAndTransitions transitionsBetween(final ResultPoint from, final ResultPoint to) {
        int fromX = (int)from.getX();
        int fromY = (int)from.getY();
        int toX = (int)to.getX();
        int toY = (int)to.getY();
        final boolean steep = Math.abs(toY - fromY) > Math.abs(toX - fromX);
        if (steep) {
            int temp = fromX;
            fromX = fromY;
            fromY = temp;
            temp = toX;
            toX = toY;
            toY = temp;
        }
        final int dx = Math.abs(toX - fromX);
        final int dy = Math.abs(toY - fromY);
        int error = -dx >> 1;
        final int ystep = (fromY < toY) ? 1 : -1;
        final int xstep = (fromX < toX) ? 1 : -1;
        int transitions = 0;
        boolean inBlack = this.image.get(steep ? fromY : fromX, steep ? fromX : fromY);
        int x = fromX;
        int y = fromY;
        while (x != toX) {
            final boolean isBlack = this.image.get(steep ? y : x, steep ? x : y);
            if (isBlack != inBlack) {
                ++transitions;
                inBlack = isBlack;
            }
            error += dy;
            if (error > 0) {
                if (y == toY) {
                    break;
                }
                y += ystep;
                error -= dx;
            }
            x += xstep;
        }
        return new ResultPointsAndTransitions(from, to, transitions);
    }
    
    private static class ResultPointsAndTransitions
    {
        private final ResultPoint from;
        private final ResultPoint to;
        private final int transitions;
        
        private ResultPointsAndTransitions(final ResultPoint from, final ResultPoint to, final int transitions) {
            this.from = from;
            this.to = to;
            this.transitions = transitions;
        }
        
        ResultPoint getFrom() {
            return this.from;
        }
        
        ResultPoint getTo() {
            return this.to;
        }
        
        public int getTransitions() {
            return this.transitions;
        }
        
        @Override
        public String toString() {
            return this.from + "/" + this.to + '/' + this.transitions;
        }
    }
    
    private static class ResultPointsAndTransitionsComparator implements Comparator<ResultPointsAndTransitions>, Serializable
    {
        @Override
        public int compare(final ResultPointsAndTransitions o1, final ResultPointsAndTransitions o2) {
            return o1.getTransitions() - o2.getTransitions();
        }
    }
}
