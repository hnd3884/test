package com.adventnet.persistence;

public interface DeepCloneable extends Cloneable
{
    Object clone() throws CloneNotSupportedException;
}
