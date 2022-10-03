package com.adventnet.sym.server.mdm.ios;

import java.util.List;
import com.dd.plist.NSObject;
import com.dd.plist.NSArray;

public class MDMNSArray extends NSArray
{
    public MDMNSArray() {
        super(new NSObject[0]);
    }
    
    public static NSArray getNSArrayFromList(final List list) {
        final NSArray array = new NSArray(list.size());
        for (int i = 0; i < list.size(); ++i) {
            array.setValue(i, list.get(i));
        }
        return array;
    }
}
