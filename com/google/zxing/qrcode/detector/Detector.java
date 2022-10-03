package com.google.zxing.qrcode.detector;

import com.google.zxing.common.GridSampler;
import com.google.zxing.common.PerspectiveTransform;
import com.google.zxing.qrcode.decoder.Version;
import com.google.zxing.ResultPoint;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.DecodeHintType;
import java.util.Map;
import com.google.zxing.common.DetectorResult;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.BitMatrix;

public class Detector
{
    private final BitMatrix image;
    private ResultPointCallback resultPointCallback;
    
    public Detector(final BitMatrix image) {
        this.image = image;
    }
    
    protected BitMatrix getImage() {
        return this.image;
    }
    
    protected ResultPointCallback getResultPointCallback() {
        return this.resultPointCallback;
    }
    
    public DetectorResult detect() throws NotFoundException, FormatException {
        return this.detect(null);
    }
    
    public DetectorResult detect(final Map<DecodeHintType, ?> hints) throws NotFoundException, FormatException {
        this.resultPointCallback = ((hints == null) ? null : ((ResultPointCallback)hints.get(DecodeHintType.NEED_RESULT_POINT_CALLBACK)));
        final FinderPatternFinder finder = new FinderPatternFinder(this.image, this.resultPointCallback);
        final FinderPatternInfo info = finder.find(hints);
        return this.processFinderPatternInfo(info);
    }
    
    protected DetectorResult processFinderPatternInfo(final FinderPatternInfo info) throws NotFoundException, FormatException {
        final FinderPattern topLeft = info.getTopLeft();
        final FinderPattern topRight = info.getTopRight();
        final FinderPattern bottomLeft = info.getBottomLeft();
        final float moduleSize = this.calculateModuleSize(topLeft, topRight, bottomLeft);
        if (moduleSize < 1.0f) {
            throw NotFoundException.getNotFoundInstance();
        }
        final int dimension = computeDimension(topLeft, topRight, bottomLeft, moduleSize);
        final Version provisionalVersion = Version.getProvisionalVersionForDimension(dimension);
        final int modulesBetweenFPCenters = provisionalVersion.getDimensionForVersion() - 7;
        AlignmentPattern alignmentPattern = null;
        if (provisionalVersion.getAlignmentPatternCenters().length > 0) {
            final float bottomRightX = topRight.getX() - topLeft.getX() + bottomLeft.getX();
            final float bottomRightY = topRight.getY() - topLeft.getY() + bottomLeft.getY();
            final float correctionToTopLeft = 1.0f - 3.0f / modulesBetweenFPCenters;
            final int estAlignmentX = (int)(topLeft.getX() + correctionToTopLeft * (bottomRightX - topLeft.getX()));
            final int estAlignmentY = (int)(topLeft.getY() + correctionToTopLeft * (bottomRightY - topLeft.getY()));
            int i = 4;
            while (i <= 16) {
                try {
                    alignmentPattern = this.findAlignmentInRegion(moduleSize, estAlignmentX, estAlignmentY, (float)i);
                }
                catch (final NotFoundException re) {
                    i <<= 1;
                    continue;
                }
                break;
            }
        }
        final PerspectiveTransform transform = createTransform(topLeft, topRight, bottomLeft, alignmentPattern, dimension);
        final BitMatrix bits = sampleGrid(this.image, transform, dimension);
        ResultPoint[] points;
        if (alignmentPattern == null) {
            points = new ResultPoint[] { bottomLeft, topLeft, topRight };
        }
        else {
            points = new ResultPoint[] { bottomLeft, topLeft, topRight, alignmentPattern };
        }
        return new DetectorResult(bits, points);
    }
    
    public static PerspectiveTransform createTransform(final ResultPoint topLeft, final ResultPoint topRight, final ResultPoint bottomLeft, final ResultPoint alignmentPattern, final int dimension) {
        final float dimMinusThree = dimension - 3.5f;
        float bottomRightX;
        float bottomRightY;
        float sourceBottomRightX;
        float sourceBottomRightY;
        if (alignmentPattern != null) {
            bottomRightX = alignmentPattern.getX();
            bottomRightY = alignmentPattern.getY();
            sourceBottomRightY = (sourceBottomRightX = dimMinusThree - 3.0f);
        }
        else {
            bottomRightX = topRight.getX() - topLeft.getX() + bottomLeft.getX();
            bottomRightY = topRight.getY() - topLeft.getY() + bottomLeft.getY();
            sourceBottomRightY = (sourceBottomRightX = dimMinusThree);
        }
        return PerspectiveTransform.quadrilateralToQuadrilateral(3.5f, 3.5f, dimMinusThree, 3.5f, sourceBottomRightX, sourceBottomRightY, 3.5f, dimMinusThree, topLeft.getX(), topLeft.getY(), topRight.getX(), topRight.getY(), bottomRightX, bottomRightY, bottomLeft.getX(), bottomLeft.getY());
    }
    
    private static BitMatrix sampleGrid(final BitMatrix image, final PerspectiveTransform transform, final int dimension) throws NotFoundException {
        final GridSampler sampler = GridSampler.getInstance();
        return sampler.sampleGrid(image, dimension, dimension, transform);
    }
    
    protected static int computeDimension(final ResultPoint topLeft, final ResultPoint topRight, final ResultPoint bottomLeft, final float moduleSize) throws NotFoundException {
        final int tltrCentersDimension = round(ResultPoint.distance(topLeft, topRight) / moduleSize);
        final int tlblCentersDimension = round(ResultPoint.distance(topLeft, bottomLeft) / moduleSize);
        int dimension = (tltrCentersDimension + tlblCentersDimension >> 1) + 7;
        switch (dimension & 0x3) {
            case 0: {
                ++dimension;
                break;
            }
            case 2: {
                --dimension;
                break;
            }
            case 3: {
                throw NotFoundException.getNotFoundInstance();
            }
        }
        return dimension;
    }
    
    protected float calculateModuleSize(final ResultPoint topLeft, final ResultPoint topRight, final ResultPoint bottomLeft) {
        return (this.calculateModuleSizeOneWay(topLeft, topRight) + this.calculateModuleSizeOneWay(topLeft, bottomLeft)) / 2.0f;
    }
    
    private float calculateModuleSizeOneWay(final ResultPoint pattern, final ResultPoint otherPattern) {
        final float moduleSizeEst1 = this.sizeOfBlackWhiteBlackRunBothWays((int)pattern.getX(), (int)pattern.getY(), (int)otherPattern.getX(), (int)otherPattern.getY());
        final float moduleSizeEst2 = this.sizeOfBlackWhiteBlackRunBothWays((int)otherPattern.getX(), (int)otherPattern.getY(), (int)pattern.getX(), (int)pattern.getY());
        if (Float.isNaN(moduleSizeEst1)) {
            return moduleSizeEst2 / 7.0f;
        }
        if (Float.isNaN(moduleSizeEst2)) {
            return moduleSizeEst1 / 7.0f;
        }
        return (moduleSizeEst1 + moduleSizeEst2) / 14.0f;
    }
    
    private float sizeOfBlackWhiteBlackRunBothWays(final int fromX, final int fromY, final int toX, final int toY) {
        float result = this.sizeOfBlackWhiteBlackRun(fromX, fromY, toX, toY);
        float scale = 1.0f;
        int otherToX = fromX - (toX - fromX);
        if (otherToX < 0) {
            scale = fromX / (float)(fromX - otherToX);
            otherToX = 0;
        }
        else if (otherToX >= this.image.getWidth()) {
            scale = (this.image.getWidth() - 1 - fromX) / (float)(otherToX - fromX);
            otherToX = this.image.getWidth() - 1;
        }
        int otherToY = (int)(fromY - (toY - fromY) * scale);
        scale = 1.0f;
        if (otherToY < 0) {
            scale = fromY / (float)(fromY - otherToY);
            otherToY = 0;
        }
        else if (otherToY >= this.image.getHeight()) {
            scale = (this.image.getHeight() - 1 - fromY) / (float)(otherToY - fromY);
            otherToY = this.image.getHeight() - 1;
        }
        otherToX = (int)(fromX + (otherToX - fromX) * scale);
        result += this.sizeOfBlackWhiteBlackRun(fromX, fromY, otherToX, otherToY);
        return result - 1.0f;
    }
    
    private float sizeOfBlackWhiteBlackRun(int fromX, int fromY, int toX, int toY) {
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
        final int xstep = (fromX < toX) ? 1 : -1;
        final int ystep = (fromY < toY) ? 1 : -1;
        int state = 0;
        final int xLimit = toX + xstep;
        int x = fromX;
        int y = fromY;
        while (x != xLimit) {
            final int realX = steep ? y : x;
            final int realY = steep ? x : y;
            if (state == 1 == this.image.get(realX, realY)) {
                if (state == 2) {
                    final int diffX = x - fromX;
                    final int diffY = y - fromY;
                    return (float)Math.sqrt(diffX * diffX + diffY * diffY);
                }
                ++state;
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
        if (state == 2) {
            final int diffX2 = toX + xstep - fromX;
            final int diffY2 = toY - fromY;
            return (float)Math.sqrt(diffX2 * diffX2 + diffY2 * diffY2);
        }
        return Float.NaN;
    }
    
    protected AlignmentPattern findAlignmentInRegion(final float overallEstModuleSize, final int estAlignmentX, final int estAlignmentY, final float allowanceFactor) throws NotFoundException {
        final int allowance = (int)(allowanceFactor * overallEstModuleSize);
        final int alignmentAreaLeftX = Math.max(0, estAlignmentX - allowance);
        final int alignmentAreaRightX = Math.min(this.image.getWidth() - 1, estAlignmentX + allowance);
        if (alignmentAreaRightX - alignmentAreaLeftX < overallEstModuleSize * 3.0f) {
            throw NotFoundException.getNotFoundInstance();
        }
        final int alignmentAreaTopY = Math.max(0, estAlignmentY - allowance);
        final int alignmentAreaBottomY = Math.min(this.image.getHeight() - 1, estAlignmentY + allowance);
        if (alignmentAreaBottomY - alignmentAreaTopY < overallEstModuleSize * 3.0f) {
            throw NotFoundException.getNotFoundInstance();
        }
        final AlignmentPatternFinder alignmentFinder = new AlignmentPatternFinder(this.image, alignmentAreaLeftX, alignmentAreaTopY, alignmentAreaRightX - alignmentAreaLeftX, alignmentAreaBottomY - alignmentAreaTopY, overallEstModuleSize, this.resultPointCallback);
        return alignmentFinder.find();
    }
    
    private static int round(final float d) {
        return (int)(d + 0.5f);
    }
}
