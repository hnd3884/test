package com.adventnet.persistence;

import java.sql.Connection;
import com.adventnet.mfw.bean.Initializable;

public interface SlaveDataSource extends Initializable
{
    Connection getConnection() throws Exception;
}
