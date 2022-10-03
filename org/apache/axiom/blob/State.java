package org.apache.axiom.blob;

enum State
{
    NEW, 
    UNCOMMITTED, 
    COMMITTED, 
    RELEASED;
}
