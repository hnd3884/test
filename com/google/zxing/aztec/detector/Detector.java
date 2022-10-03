package com.google.zxing.aztec.detector;

import com.google.zxing.common.GridSampler;
import com.google.zxing.common.detector.WhiteRectangleDetector;
import com.google.zxing.common.reedsolomon.ReedSolomonException;
import com.google.zxing.common.reedsolomon.ReedSolomonDecoder;
import com.google.zxing.common.reedsolomon.GenericGF;
import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.aztec.AztecDetectorResult;
import com.google.zxing.common.BitMatrix;

public final class Detector
{
    private final BitMatrix image;
    private boolean compact;
    private int nbLayers;
    private int nbDataBlocks;
    private int nbCenterLayers;
    private int shift;
    
    public Detector(final BitMatrix image) {
        this.image = image;
    }
    
    public AztecDetectorResult detect() throws NotFoundException {
        final Point pCenter = this.getMatrixCenter();
        final Point[] bullEyeCornerPoints = this.getBullEyeCornerPoints(pCenter);
        this.extractParameters(bullEyeCornerPoints);
        final ResultPoint[] corners = this.getMatrixCornerPoints(bullEyeCornerPoints);
        final BitMatrix bits = this.sampleGrid(this.image, corners[this.shift % 4], corners[(this.shift + 3) % 4], corners[(this.shift + 2) % 4], corners[(this.shift + 1) % 4]);
        return new AztecDetectorResult(bits, corners, this.compact, this.nbDataBlocks, this.nbLayers);
    }
    
    private void extractParameters(final Point[] bullEyeCornerPoints) throws NotFoundException {
        final boolean[] resab = this.sampleLine(bullEyeCornerPoints[0], bullEyeCornerPoints[1], 2 * this.nbCenterLayers + 1);
        final boolean[] resbc = this.sampleLine(bullEyeCornerPoints[1], bullEyeCornerPoints[2], 2 * this.nbCenterLayers + 1);
        final boolean[] rescd = this.sampleLine(bullEyeCornerPoints[2], bullEyeCornerPoints[3], 2 * this.nbCenterLayers + 1);
        final boolean[] resda = this.sampleLine(bullEyeCornerPoints[3], bullEyeCornerPoints[0], 2 * this.nbCenterLayers + 1);
        if (resab[0] && resab[2 * this.nbCenterLayers]) {
            this.shift = 0;
        }
        else if (resbc[0] && resbc[2 * this.nbCenterLayers]) {
            this.shift = 1;
        }
        else if (rescd[0] && rescd[2 * this.nbCenterLayers]) {
            this.shift = 2;
        }
        else {
            if (!resda[0] || !resda[2 * this.nbCenterLayers]) {
                throw NotFoundException.getNotFoundInstance();
            }
            this.shift = 3;
        }
        boolean[] parameterData;
        if (this.compact) {
            final boolean[] shiftedParameterData = new boolean[28];
            for (int i = 0; i < 7; ++i) {
                shiftedParameterData[i] = resab[2 + i];
                shiftedParameterData[i + 7] = resbc[2 + i];
                shiftedParameterData[i + 14] = rescd[2 + i];
                shiftedParameterData[i + 21] = resda[2 + i];
            }
            parameterData = new boolean[28];
            for (int i = 0; i < 28; ++i) {
                parameterData[i] = shiftedParameterData[(i + this.shift * 7) % 28];
            }
        }
        else {
            final boolean[] shiftedParameterData = new boolean[40];
            for (int i = 0; i < 11; ++i) {
                if (i < 5) {
                    shiftedParameterData[i] = resab[2 + i];
                    shiftedParameterData[i + 10] = resbc[2 + i];
                    shiftedParameterData[i + 20] = rescd[2 + i];
                    shiftedParameterData[i + 30] = resda[2 + i];
                }
                if (i > 5) {
                    shiftedParameterData[i - 1] = resab[2 + i];
                    shiftedParameterData[i + 10 - 1] = resbc[2 + i];
                    shiftedParameterData[i + 20 - 1] = rescd[2 + i];
                    shiftedParameterData[i + 30 - 1] = resda[2 + i];
                }
            }
            parameterData = new boolean[40];
            for (int i = 0; i < 40; ++i) {
                parameterData[i] = shiftedParameterData[(i + this.shift * 10) % 40];
            }
        }
        correctParameterData(parameterData, this.compact);
        this.getParameters(parameterData);
    }
    
    private ResultPoint[] getMatrixCornerPoints(final Point[] bullEyeCornerPoints) throws NotFoundException {
        final float ratio = (2 * this.nbLayers + ((this.nbLayers > 4) ? 1 : 0) + (this.nbLayers - 4) / 8) / (2.0f * this.nbCenterLayers);
        int dx = bullEyeCornerPoints[0].x - bullEyeCornerPoints[2].x;
        dx += ((dx > 0) ? 1 : -1);
        int dy = bullEyeCornerPoints[0].y - bullEyeCornerPoints[2].y;
        dy += ((dy > 0) ? 1 : -1);
        final int targetcx = round(bullEyeCornerPoints[2].x - ratio * dx);
        final int targetcy = round(bullEyeCornerPoints[2].y - ratio * dy);
        final int targetax = round(bullEyeCornerPoints[0].x + ratio * dx);
        final int targetay = round(bullEyeCornerPoints[0].y + ratio * dy);
        dx = bullEyeCornerPoints[1].x - bullEyeCornerPoints[3].x;
        dx += ((dx > 0) ? 1 : -1);
        dy = bullEyeCornerPoints[1].y - bullEyeCornerPoints[3].y;
        dy += ((dy > 0) ? 1 : -1);
        final int targetdx = round(bullEyeCornerPoints[3].x - ratio * dx);
        final int targetdy = round(bullEyeCornerPoints[3].y - ratio * dy);
        final int targetbx = round(bullEyeCornerPoints[1].x + ratio * dx);
        final int targetby = round(bullEyeCornerPoints[1].y + ratio * dy);
        if (!this.isValid(targetax, targetay) || !this.isValid(targetbx, targetby) || !this.isValid(targetcx, targetcy) || !this.isValid(targetdx, targetdy)) {
            throw NotFoundException.getNotFoundInstance();
        }
        return new ResultPoint[] { new ResultPoint((float)targetax, (float)targetay), new ResultPoint((float)targetbx, (float)targetby), new ResultPoint((float)targetcx, (float)targetcy), new ResultPoint((float)targetdx, (float)targetdy) };
    }
    
    private static void correctParameterData(final boolean[] parameterData, final boolean compact) throws NotFoundException {
        int numCodewords;
        int numDataCodewords;
        if (compact) {
            numCodewords = 7;
            numDataCodewords = 2;
        }
        else {
            numCodewords = 10;
            numDataCodewords = 4;
        }
        final int numECCodewords = numCodewords - numDataCodewords;
        final int[] parameterWords = new int[numCodewords];
        final int codewordSize = 4;
        for (int i = 0; i < numCodewords; ++i) {
            int flag = 1;
            for (int j = 1; j <= codewordSize; ++j) {
                if (parameterData[codewordSize * i + codewordSize - j]) {
                    final int[] array = parameterWords;
                    final int n = i;
                    array[n] += flag;
                }
                flag <<= 1;
            }
        }
        try {
            final ReedSolomonDecoder rsDecoder = new ReedSolomonDecoder(GenericGF.AZTEC_PARAM);
            rsDecoder.decode(parameterWords, numECCodewords);
        }
        catch (final ReedSolomonException rse) {
            throw NotFoundException.getNotFoundInstance();
        }
        for (int i = 0; i < numDataCodewords; ++i) {
            int flag = 1;
            for (int j = 1; j <= codewordSize; ++j) {
                parameterData[i * codewordSize + codewordSize - j] = ((parameterWords[i] & flag) == flag);
                flag <<= 1;
            }
        }
    }
    
    private Point[] getBullEyeCornerPoints(final Point pCenter) throws NotFoundException {
        Point pina = pCenter;
        Point pinb = pCenter;
        Point pinc = pCenter;
        Point pind = pCenter;
        boolean color = true;
        this.nbCenterLayers = 1;
        while (this.nbCenterLayers < 9) {
            final Point pouta = this.getFirstDifferent(pina, color, 1, -1);
            final Point poutb = this.getFirstDifferent(pinb, color, 1, 1);
            final Point poutc = this.getFirstDifferent(pinc, color, -1, 1);
            final Point poutd = this.getFirstDifferent(pind, color, -1, -1);
            if (this.nbCenterLayers > 2) {
                final float q = distance(poutd, pouta) * this.nbCenterLayers / (distance(pind, pina) * (this.nbCenterLayers + 2));
                if (q < 0.75 || q > 1.25) {
                    break;
                }
                if (!this.isWhiteOrBlackRectangle(pouta, poutb, poutc, poutd)) {
                    break;
                }
            }
            pina = pouta;
            pinb = poutb;
            pinc = poutc;
            pind = poutd;
            color = !color;
            ++this.nbCenterLayers;
        }
        if (this.nbCenterLayers != 5 && this.nbCenterLayers != 7) {
            throw NotFoundException.getNotFoundInstance();
        }
        this.compact = (this.nbCenterLayers == 5);
        final float ratio = 1.5f / (2 * this.nbCenterLayers - 3);
        int dx = pina.x - pinc.x;
        int dy = pina.y - pinc.y;
        final int targetcx = round(pinc.x - ratio * dx);
        final int targetcy = round(pinc.y - ratio * dy);
        final int targetax = round(pina.x + ratio * dx);
        final int targetay = round(pina.y + ratio * dy);
        dx = pinb.x - pind.x;
        dy = pinb.y - pind.y;
        final int targetdx = round(pind.x - ratio * dx);
        final int targetdy = round(pind.y - ratio * dy);
        final int targetbx = round(pinb.x + ratio * dx);
        final int targetby = round(pinb.y + ratio * dy);
        if (!this.isValid(targetax, targetay) || !this.isValid(targetbx, targetby) || !this.isValid(targetcx, targetcy) || !this.isValid(targetdx, targetdy)) {
            throw NotFoundException.getNotFoundInstance();
        }
        final Point pa = new Point(targetax, targetay);
        final Point pb = new Point(targetbx, targetby);
        final Point pc = new Point(targetcx, targetcy);
        final Point pd = new Point(targetdx, targetdy);
        return new Point[] { pa, pb, pc, pd };
    }
    
    private Point getMatrixCenter() {
        ResultPoint pointA;
        ResultPoint pointB;
        ResultPoint pointC;
        ResultPoint pointD;
        try {
            final ResultPoint[] cornerPoints = new WhiteRectangleDetector(this.image).detect();
            pointA = cornerPoints[0];
            pointB = cornerPoints[1];
            pointC = cornerPoints[2];
            pointD = cornerPoints[3];
        }
        catch (final NotFoundException e) {
            final int cx = this.image.getWidth() / 2;
            final int cy = this.image.getHeight() / 2;
            pointA = this.getFirstDifferent(new Point(cx + 7, cy - 7), false, 1, -1).toResultPoint();
            pointB = this.getFirstDifferent(new Point(cx + 7, cy + 7), false, 1, 1).toResultPoint();
            pointC = this.getFirstDifferent(new Point(cx - 7, cy + 7), false, -1, 1).toResultPoint();
            pointD = this.getFirstDifferent(new Point(cx - 7, cy - 7), false, -1, -1).toResultPoint();
        }
        int cx2 = round((pointA.getX() + pointD.getX() + pointB.getX() + pointC.getX()) / 4.0f);
        int cy2 = round((pointA.getY() + pointD.getY() + pointB.getY() + pointC.getY()) / 4.0f);
        try {
            final ResultPoint[] cornerPoints2 = new WhiteRectangleDetector(this.image, 15, cx2, cy2).detect();
            pointA = cornerPoints2[0];
            pointB = cornerPoints2[1];
            pointC = cornerPoints2[2];
            pointD = cornerPoints2[3];
        }
        catch (final NotFoundException e2) {
            pointA = this.getFirstDifferent(new Point(cx2 + 7, cy2 - 7), false, 1, -1).toResultPoint();
            pointB = this.getFirstDifferent(new Point(cx2 + 7, cy2 + 7), false, 1, 1).toResultPoint();
            pointC = this.getFirstDifferent(new Point(cx2 - 7, cy2 + 7), false, -1, 1).toResultPoint();
            pointD = this.getFirstDifferent(new Point(cx2 - 7, cy2 - 7), false, -1, -1).toResultPoint();
        }
        cx2 = round((pointA.getX() + pointD.getX() + pointB.getX() + pointC.getX()) / 4.0f);
        cy2 = round((pointA.getY() + pointD.getY() + pointB.getY() + pointC.getY()) / 4.0f);
        return new Point(cx2, cy2);
    }
    
    private BitMatrix sampleGrid(final BitMatrix image, final ResultPoint topLeft, final ResultPoint bottomLeft, final ResultPoint bottomRight, final ResultPoint topRight) throws NotFoundException {
        int dimension;
        if (this.compact) {
            dimension = 4 * this.nbLayers + 11;
        }
        else if (this.nbLayers <= 4) {
            dimension = 4 * this.nbLayers + 15;
        }
        else {
            dimension = 4 * this.nbLayers + 2 * ((this.nbLayers - 4) / 8 + 1) + 15;
        }
        final GridSampler sampler = GridSampler.getInstance();
        return sampler.sampleGrid(image, dimension, dimension, 0.5f, 0.5f, dimension - 0.5f, 0.5f, dimension - 0.5f, dimension - 0.5f, 0.5f, dimension - 0.5f, topLeft.getX(), topLeft.getY(), topRight.getX(), topRight.getY(), bottomRight.getX(), bottomRight.getY(), bottomLeft.getX(), bottomLeft.getY());
    }
    
    private void getParameters(final boolean[] parameterData) {
        int nbBitsForNbLayers;
        int nbBitsForNbDatablocks;
        if (this.compact) {
            nbBitsForNbLayers = 2;
            nbBitsForNbDatablocks = 6;
        }
        else {
            nbBitsForNbLayers = 5;
            nbBitsForNbDatablocks = 11;
        }
        for (int i = 0; i < nbBitsForNbLayers; ++i) {
            this.nbLayers <<= 1;
            if (parameterData[i]) {
                ++this.nbLayers;
            }
        }
        for (int i = nbBitsForNbLayers; i < nbBitsForNbLayers + nbBitsForNbDatablocks; ++i) {
            this.nbDataBlocks <<= 1;
            if (parameterData[i]) {
                ++this.nbDataBlocks;
            }
        }
        ++this.nbLayers;
        ++this.nbDataBlocks;
    }
    
    private boolean[] sampleLine(final Point p1, final Point p2, final int size) {
        final boolean[] res = new boolean[size];
        final float d = distance(p1, p2);
        final float moduleSize = d / (size - 1);
        final float dx = moduleSize * (p2.x - p1.x) / d;
        final float dy = moduleSize * (p2.y - p1.y) / d;
        float px = (float)p1.x;
        float py = (float)p1.y;
        for (int i = 0; i < size; ++i) {
            res[i] = this.image.get(round(px), round(py));
            px += dx;
            py += dy;
        }
        return res;
    }
    
    private boolean isWhiteOrBlackRectangle(Point p1, Point p2, Point p3, Point p4) {
        final int corr = 3;
        p1 = new Point(p1.x - corr, p1.y + corr);
        p2 = new Point(p2.x - corr, p2.y - corr);
        p3 = new Point(p3.x + corr, p3.y - corr);
        p4 = new Point(p4.x + corr, p4.y + corr);
        final int cInit = this.getColor(p4, p1);
        if (cInit == 0) {
            return false;
        }
        int c = this.getColor(p1, p2);
        if (c != cInit) {
            return false;
        }
        c = this.getColor(p2, p3);
        if (c != cInit) {
            return false;
        }
        c = this.getColor(p3, p4);
        return c == cInit;
    }
    
    private int getColor(final Point p1, final Point p2) {
        final float d = distance(p1, p2);
        final float dx = (p2.x - p1.x) / d;
        final float dy = (p2.y - p1.y) / d;
        int error = 0;
        float px = (float)p1.x;
        float py = (float)p1.y;
        final boolean colorModel = this.image.get(p1.x, p1.y);
        for (int i = 0; i < d; ++i) {
            px += dx;
            py += dy;
            if (this.image.get(round(px), round(py)) != colorModel) {
                ++error;
            }
        }
        final float errRatio = error / d;
        if (errRatio > 0.1 && errRatio < 0.9) {
            return 0;
        }
        if (errRatio <= 0.1) {
            return colorModel ? 1 : -1;
        }
        return colorModel ? -1 : 1;
    }
    
    private Point getFirstDifferent(final Point init, final boolean color, final int dx, final int dy) {
        int x;
        int y;
        for (x = init.x + dx, y = init.y + dy; this.isValid(x, y) && this.image.get(x, y) == color; x += dx, y += dy) {}
        for (x -= dx, y -= dy; this.isValid(x, y) && this.image.get(x, y) == color; x += dx) {}
        for (x -= dx; this.isValid(x, y) && this.image.get(x, y) == color; y += dy) {}
        y -= dy;
        return new Point(x, y);
    }
    
    private boolean isValid(final int x, final int y) {
        return x >= 0 && x < this.image.getWidth() && y > 0 && y < this.image.getHeight();
    }
    
    private static int round(final float d) {
        return (int)(d + 0.5f);
    }
    
    private static float distance(final Point a, final Point b) {
        return (float)Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
    }
    
    private static class Point
    {
        public final int x;
        public final int y;
        
        public ResultPoint toResultPoint() {
            return new ResultPoint((float)this.x, (float)this.y);
        }
        
        private Point(final int x, final int y) {
            this.x = x;
            this.y = y;
        }
    }
}
