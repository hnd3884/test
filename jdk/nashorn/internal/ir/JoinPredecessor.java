package jdk.nashorn.internal.ir;

public interface JoinPredecessor
{
    JoinPredecessor setLocalVariableConversion(final LexicalContext p0, final LocalVariableConversion p1);
    
    LocalVariableConversion getLocalVariableConversion();
}
