package org.apache.poi.ss.formula.function;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;

final class FunctionDataBuilder
{
    private int _maxFunctionIndex;
    private final Map<String, FunctionMetadata> _functionDataByName;
    private final Map<Integer, FunctionMetadata> _functionDataByIndex;
    private final Set<Integer> _mutatingFunctionIndexes;
    
    public FunctionDataBuilder(final int sizeEstimate) {
        this._maxFunctionIndex = -1;
        this._functionDataByName = new HashMap<String, FunctionMetadata>(sizeEstimate * 3 / 2);
        this._functionDataByIndex = new HashMap<Integer, FunctionMetadata>(sizeEstimate * 3 / 2);
        this._mutatingFunctionIndexes = new HashSet<Integer>();
    }
    
    public void add(final int functionIndex, final String functionName, final int minParams, final int maxParams, final byte returnClassCode, final byte[] parameterClassCodes, final boolean hasFootnote) {
        final FunctionMetadata fm = new FunctionMetadata(functionIndex, functionName, minParams, maxParams, returnClassCode, parameterClassCodes);
        final Integer indexKey = functionIndex;
        if (functionIndex > this._maxFunctionIndex) {
            this._maxFunctionIndex = functionIndex;
        }
        FunctionMetadata prevFM = this._functionDataByName.get(functionName);
        if (prevFM != null) {
            if (!hasFootnote || !this._mutatingFunctionIndexes.contains(indexKey)) {
                throw new RuntimeException("Multiple entries for function name '" + functionName + "'");
            }
            this._functionDataByIndex.remove(prevFM.getIndex());
        }
        prevFM = this._functionDataByIndex.get(indexKey);
        if (prevFM != null) {
            if (!hasFootnote || !this._mutatingFunctionIndexes.contains(indexKey)) {
                throw new RuntimeException("Multiple entries for function index (" + functionIndex + ")");
            }
            this._functionDataByName.remove(prevFM.getName());
        }
        if (hasFootnote) {
            this._mutatingFunctionIndexes.add(indexKey);
        }
        this._functionDataByIndex.put(indexKey, fm);
        this._functionDataByName.put(functionName, fm);
    }
    
    public FunctionMetadataRegistry build() {
        final FunctionMetadata[] jumbledArray = new FunctionMetadata[this._functionDataByName.size()];
        this._functionDataByName.values().toArray(jumbledArray);
        final FunctionMetadata[] fdIndexArray = new FunctionMetadata[this._maxFunctionIndex + 1];
        for (final FunctionMetadata fd : jumbledArray) {
            fdIndexArray[fd.getIndex()] = fd;
        }
        return new FunctionMetadataRegistry(fdIndexArray, this._functionDataByName);
    }
}
