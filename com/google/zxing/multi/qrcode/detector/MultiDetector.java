package com.google.zxing.multi.qrcode.detector;

import java.util.List;
import com.google.zxing.qrcode.detector.FinderPatternInfo;
import com.google.zxing.ReaderException;
import java.util.ArrayList;
import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.DecodeHintType;
import java.util.Map;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DetectorResult;
import com.google.zxing.qrcode.detector.Detector;

public final class MultiDetector extends Detector
{
    private static final DetectorResult[] EMPTY_DETECTOR_RESULTS;
    
    public MultiDetector(final BitMatrix image) {
        super(image);
    }
    
    public DetectorResult[] detectMulti(final Map<DecodeHintType, ?> hints) throws NotFoundException {
        final BitMatrix image = this.getImage();
        final ResultPointCallback resultPointCallback = (hints == null) ? null : ((ResultPointCallback)hints.get(DecodeHintType.NEED_RESULT_POINT_CALLBACK));
        final MultiFinderPatternFinder finder = new MultiFinderPatternFinder(image, resultPointCallback);
        final FinderPatternInfo[] infos = finder.findMulti(hints);
        if (infos.length == 0) {
            throw NotFoundException.getNotFoundInstance();
        }
        final List<DetectorResult> result = new ArrayList<DetectorResult>();
        for (final FinderPatternInfo info : infos) {
            try {
                result.add(this.processFinderPatternInfo(info));
            }
            catch (final ReaderException ex) {}
        }
        if (result.isEmpty()) {
            return MultiDetector.EMPTY_DETECTOR_RESULTS;
        }
        return result.toArray(new DetectorResult[result.size()]);
    }
    
    static {
        EMPTY_DETECTOR_RESULTS = new DetectorResult[0];
    }
}
