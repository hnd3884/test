package com.adventnet.mfw.bean;

import com.adventnet.persistence.DataObject;

public interface Initializable
{
    void initialize(final DataObject p0) throws Exception;
}
