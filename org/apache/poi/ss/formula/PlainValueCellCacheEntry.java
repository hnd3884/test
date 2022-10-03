package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.eval.ValueEval;

final class PlainValueCellCacheEntry extends CellCacheEntry
{
    public PlainValueCellCacheEntry(final ValueEval value) {
        this.updateValue(value);
    }
}
