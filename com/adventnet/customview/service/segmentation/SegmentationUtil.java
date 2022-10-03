package com.adventnet.customview.service.segmentation;

import com.adventnet.customview.CustomViewException;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;

public class SegmentationUtil
{
    public static Logger logger;
    
    public static SelectQuery segmentSelectQuery(final SelectQuery query) throws CustomViewException {
        return query;
    }
    
    static {
        SegmentationUtil.logger = Logger.getLogger(SegmentationUtil.class.getName());
    }
}
