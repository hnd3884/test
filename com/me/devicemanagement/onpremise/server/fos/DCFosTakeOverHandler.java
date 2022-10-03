package com.me.devicemanagement.onpremise.server.fos;

import com.adventnet.persistence.fos.FOSException;
import com.adventnet.persistence.fos.TakeOverHandler;

public class DCFosTakeOverHandler implements TakeOverHandler
{
    public void onTakeover() throws FOSException {
        FosUtil.incrementTakeOverCount();
    }
}
