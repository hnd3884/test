package org.tukaani.xz;

class RawCoder
{
    static void validate(final FilterCoder[] array) throws UnsupportedOptionsException {
        for (int i = 0; i < array.length - 1; ++i) {
            if (!array[i].nonLastOK()) {
                throw new UnsupportedOptionsException("Unsupported XZ filter chain");
            }
        }
        if (!array[array.length - 1].lastOK()) {
            throw new UnsupportedOptionsException("Unsupported XZ filter chain");
        }
        int n = 0;
        for (int j = 0; j < array.length; ++j) {
            if (array[j].changesSize()) {
                ++n;
            }
        }
        if (n > 3) {
            throw new UnsupportedOptionsException("Unsupported XZ filter chain");
        }
    }
}
