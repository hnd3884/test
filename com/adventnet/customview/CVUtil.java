package com.adventnet.customview;

import java.util.List;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.Persistence;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class CVUtil
{
    public static final Logger OUT;
    public static final Logger ERR;
    
    private CVUtil() {
    }
    
    public static DataObject getDOForCVName(final String cvName) throws CustomViewException {
        if (cvName == null) {
            throw new CustomViewException("Custom View Name is NULL ");
        }
        DataObject doo = null;
        try {
            final List listOfPers = new ArrayList();
            listOfPers.add("CustomViewConfiguration");
            listOfPers.add("SelectQuery");
            final List listOfDeepRetPers = listOfPers;
            final Criteria cvCondition = new Criteria(Column.getColumn("CustomViewConfiguration", "CVNAME"), (Object)cvName, 0);
            doo = ((Persistence)BeanUtil.lookup("Persistence")).get(listOfPers, listOfDeepRetPers, cvCondition);
        }
        catch (final Exception exp) {
            throw new CustomViewException(exp.getMessage(), exp);
        }
        return doo;
    }
    
    static {
        OUT = Logger.getLogger("CVOUT");
        ERR = Logger.getLogger("CVERR");
    }
}
