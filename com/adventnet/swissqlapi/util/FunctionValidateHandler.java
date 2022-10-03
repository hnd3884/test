package com.adventnet.swissqlapi.util;

import java.util.List;
import java.util.Map;

public interface FunctionValidateHandler
{
    Map getWhiteListedFunctions();
    
    List<String> getBlackListedFunctions();
}
