package org.json.simple.parser;

import java.util.List;
import java.util.Map;

@Deprecated
public interface ContainerFactory
{
    Map createObjectContainer();
    
    List creatArrayContainer();
}
