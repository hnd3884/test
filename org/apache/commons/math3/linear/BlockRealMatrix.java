package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import java.util.Arrays;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import java.io.Serializable;

public class BlockRealMatrix extends AbstractRealMatrix implements Serializable
{
    public static final int BLOCK_SIZE = 52;
    private static final long serialVersionUID = 4991895511313664478L;
    private final double[][] blocks;
    private final int rows;
    private final int columns;
    private final int blockRows;
    private final int blockColumns;
    
    public BlockRealMatrix(final int rows, final int columns) throws NotStrictlyPositiveException {
        super(rows, columns);
        this.rows = rows;
        this.columns = columns;
        this.blockRows = (rows + 52 - 1) / 52;
        this.blockColumns = (columns + 52 - 1) / 52;
        this.blocks = createBlocksLayout(rows, columns);
    }
    
    public BlockRealMatrix(final double[][] rawData) throws DimensionMismatchException, NotStrictlyPositiveException {
        this(rawData.length, rawData[0].length, toBlocksLayout(rawData), false);
    }
    
    public BlockRealMatrix(final int rows, final int columns, final double[][] blockData, final boolean copyArray) throws DimensionMismatchException, NotStrictlyPositiveException {
        super(rows, columns);
        this.rows = rows;
        this.columns = columns;
        this.blockRows = (rows + 52 - 1) / 52;
        this.blockColumns = (columns + 52 - 1) / 52;
        if (copyArray) {
            this.blocks = new double[this.blockRows * this.blockColumns][];
        }
        else {
            this.blocks = blockData;
        }
        int index = 0;
        for (int iBlock = 0; iBlock < this.blockRows; ++iBlock) {
            final int iHeight = this.blockHeight(iBlock);
            for (int jBlock = 0; jBlock < this.blockColumns; ++jBlock, ++index) {
                if (blockData[index].length != iHeight * this.blockWidth(jBlock)) {
                    throw new DimensionMismatchException(blockData[index].length, iHeight * this.blockWidth(jBlock));
                }
                if (copyArray) {
                    this.blocks[index] = blockData[index].clone();
                }
            }
        }
    }
    
    public static double[][] toBlocksLayout(final double[][] rawData) throws DimensionMismatchException {
        final int rows = rawData.length;
        final int columns = rawData[0].length;
        final int blockRows = (rows + 52 - 1) / 52;
        final int blockColumns = (columns + 52 - 1) / 52;
        for (int i = 0; i < rawData.length; ++i) {
            final int length = rawData[i].length;
            if (length != columns) {
                throw new DimensionMismatchException(columns, length);
            }
        }
        final double[][] blocks = new double[blockRows * blockColumns][];
        int blockIndex = 0;
        for (int iBlock = 0; iBlock < blockRows; ++iBlock) {
            final int pStart = iBlock * 52;
            final int pEnd = FastMath.min(pStart + 52, rows);
            final int iHeight = pEnd - pStart;
            for (int jBlock = 0; jBlock < blockColumns; ++jBlock) {
                final int qStart = jBlock * 52;
                final int qEnd = FastMath.min(qStart + 52, columns);
                final int jWidth = qEnd - qStart;
                final double[] block = new double[iHeight * jWidth];
                blocks[blockIndex] = block;
                int index = 0;
                for (int p = pStart; p < pEnd; ++p) {
                    System.arraycopy(rawData[p], qStart, block, index, jWidth);
                    index += jWidth;
                }
                ++blockIndex;
            }
        }
        return blocks;
    }
    
    public static double[][] createBlocksLayout(final int rows, final int columns) {
        final int blockRows = (rows + 52 - 1) / 52;
        final int blockColumns = (columns + 52 - 1) / 52;
        final double[][] blocks = new double[blockRows * blockColumns][];
        int blockIndex = 0;
        for (int iBlock = 0; iBlock < blockRows; ++iBlock) {
            final int pStart = iBlock * 52;
            final int pEnd = FastMath.min(pStart + 52, rows);
            final int iHeight = pEnd - pStart;
            for (int jBlock = 0; jBlock < blockColumns; ++jBlock) {
                final int qStart = jBlock * 52;
                final int qEnd = FastMath.min(qStart + 52, columns);
                final int jWidth = qEnd - qStart;
                blocks[blockIndex] = new double[iHeight * jWidth];
                ++blockIndex;
            }
        }
        return blocks;
    }
    
    @Override
    public BlockRealMatrix createMatrix(final int rowDimension, final int columnDimension) throws NotStrictlyPositiveException {
        return new BlockRealMatrix(rowDimension, columnDimension);
    }
    
    @Override
    public BlockRealMatrix copy() {
        final BlockRealMatrix copied = new BlockRealMatrix(this.rows, this.columns);
        for (int i = 0; i < this.blocks.length; ++i) {
            System.arraycopy(this.blocks[i], 0, copied.blocks[i], 0, this.blocks[i].length);
        }
        return copied;
    }
    
    @Override
    public BlockRealMatrix add(final RealMatrix m) throws MatrixDimensionMismatchException {
        try {
            return this.add((BlockRealMatrix)m);
        }
        catch (final ClassCastException cce) {
            MatrixUtils.checkAdditionCompatible(this, m);
            final BlockRealMatrix out = new BlockRealMatrix(this.rows, this.columns);
            int blockIndex = 0;
            for (int iBlock = 0; iBlock < out.blockRows; ++iBlock) {
                for (int jBlock = 0; jBlock < out.blockColumns; ++jBlock) {
                    final double[] outBlock = out.blocks[blockIndex];
                    final double[] tBlock = this.blocks[blockIndex];
                    final int pStart = iBlock * 52;
                    final int pEnd = FastMath.min(pStart + 52, this.rows);
                    final int qStart = jBlock * 52;
                    final int qEnd = FastMath.min(qStart + 52, this.columns);
                    int k = 0;
                    for (int p = pStart; p < pEnd; ++p) {
                        for (int q = qStart; q < qEnd; ++q) {
                            outBlock[k] = tBlock[k] + m.getEntry(p, q);
                            ++k;
                        }
                    }
                    ++blockIndex;
                }
            }
            return out;
        }
    }
    
    public BlockRealMatrix add(final BlockRealMatrix m) throws MatrixDimensionMismatchException {
        MatrixUtils.checkAdditionCompatible(this, m);
        final BlockRealMatrix out = new BlockRealMatrix(this.rows, this.columns);
        for (int blockIndex = 0; blockIndex < out.blocks.length; ++blockIndex) {
            final double[] outBlock = out.blocks[blockIndex];
            final double[] tBlock = this.blocks[blockIndex];
            final double[] mBlock = m.blocks[blockIndex];
            for (int k = 0; k < outBlock.length; ++k) {
                outBlock[k] = tBlock[k] + mBlock[k];
            }
        }
        return out;
    }
    
    @Override
    public BlockRealMatrix subtract(final RealMatrix m) throws MatrixDimensionMismatchException {
        try {
            return this.subtract((BlockRealMatrix)m);
        }
        catch (final ClassCastException cce) {
            MatrixUtils.checkSubtractionCompatible(this, m);
            final BlockRealMatrix out = new BlockRealMatrix(this.rows, this.columns);
            int blockIndex = 0;
            for (int iBlock = 0; iBlock < out.blockRows; ++iBlock) {
                for (int jBlock = 0; jBlock < out.blockColumns; ++jBlock) {
                    final double[] outBlock = out.blocks[blockIndex];
                    final double[] tBlock = this.blocks[blockIndex];
                    final int pStart = iBlock * 52;
                    final int pEnd = FastMath.min(pStart + 52, this.rows);
                    final int qStart = jBlock * 52;
                    final int qEnd = FastMath.min(qStart + 52, this.columns);
                    int k = 0;
                    for (int p = pStart; p < pEnd; ++p) {
                        for (int q = qStart; q < qEnd; ++q) {
                            outBlock[k] = tBlock[k] - m.getEntry(p, q);
                            ++k;
                        }
                    }
                    ++blockIndex;
                }
            }
            return out;
        }
    }
    
    public BlockRealMatrix subtract(final BlockRealMatrix m) throws MatrixDimensionMismatchException {
        MatrixUtils.checkSubtractionCompatible(this, m);
        final BlockRealMatrix out = new BlockRealMatrix(this.rows, this.columns);
        for (int blockIndex = 0; blockIndex < out.blocks.length; ++blockIndex) {
            final double[] outBlock = out.blocks[blockIndex];
            final double[] tBlock = this.blocks[blockIndex];
            final double[] mBlock = m.blocks[blockIndex];
            for (int k = 0; k < outBlock.length; ++k) {
                outBlock[k] = tBlock[k] - mBlock[k];
            }
        }
        return out;
    }
    
    @Override
    public BlockRealMatrix scalarAdd(final double d) {
        final BlockRealMatrix out = new BlockRealMatrix(this.rows, this.columns);
        for (int blockIndex = 0; blockIndex < out.blocks.length; ++blockIndex) {
            final double[] outBlock = out.blocks[blockIndex];
            final double[] tBlock = this.blocks[blockIndex];
            for (int k = 0; k < outBlock.length; ++k) {
                outBlock[k] = tBlock[k] + d;
            }
        }
        return out;
    }
    
    @Override
    public RealMatrix scalarMultiply(final double d) {
        final BlockRealMatrix out = new BlockRealMatrix(this.rows, this.columns);
        for (int blockIndex = 0; blockIndex < out.blocks.length; ++blockIndex) {
            final double[] outBlock = out.blocks[blockIndex];
            final double[] tBlock = this.blocks[blockIndex];
            for (int k = 0; k < outBlock.length; ++k) {
                outBlock[k] = tBlock[k] * d;
            }
        }
        return out;
    }
    
    @Override
    public BlockRealMatrix multiply(final RealMatrix m) throws DimensionMismatchException {
        try {
            return this.multiply((BlockRealMatrix)m);
        }
        catch (final ClassCastException cce) {
            MatrixUtils.checkMultiplicationCompatible(this, m);
            final BlockRealMatrix out = new BlockRealMatrix(this.rows, m.getColumnDimension());
            int blockIndex = 0;
            for (int iBlock = 0; iBlock < out.blockRows; ++iBlock) {
                final int pStart = iBlock * 52;
                final int pEnd = FastMath.min(pStart + 52, this.rows);
                for (int jBlock = 0; jBlock < out.blockColumns; ++jBlock) {
                    final int qStart = jBlock * 52;
                    final int qEnd = FastMath.min(qStart + 52, m.getColumnDimension());
                    final double[] outBlock = out.blocks[blockIndex];
                    for (int kBlock = 0; kBlock < this.blockColumns; ++kBlock) {
                        final int kWidth = this.blockWidth(kBlock);
                        final double[] tBlock = this.blocks[iBlock * this.blockColumns + kBlock];
                        final int rStart = kBlock * 52;
                        int k = 0;
                        for (int p = pStart; p < pEnd; ++p) {
                            final int lStart = (p - pStart) * kWidth;
                            final int lEnd = lStart + kWidth;
                            for (int q = qStart; q < qEnd; ++q) {
                                double sum = 0.0;
                                int r = rStart;
                                for (int l = lStart; l < lEnd; ++l) {
                                    sum += tBlock[l] * m.getEntry(r, q);
                                    ++r;
                                }
                                final double[] array = outBlock;
                                final int n = k;
                                array[n] += sum;
                                ++k;
                            }
                        }
                    }
                    ++blockIndex;
                }
            }
            return out;
        }
    }
    
    public BlockRealMatrix multiply(final BlockRealMatrix m) throws DimensionMismatchException {
        MatrixUtils.checkMultiplicationCompatible(this, m);
        final BlockRealMatrix out = new BlockRealMatrix(this.rows, m.columns);
        int blockIndex = 0;
        for (int iBlock = 0; iBlock < out.blockRows; ++iBlock) {
            final int pStart = iBlock * 52;
            final int pEnd = FastMath.min(pStart + 52, this.rows);
            for (int jBlock = 0; jBlock < out.blockColumns; ++jBlock) {
                final int jWidth = out.blockWidth(jBlock);
                final int jWidth2 = jWidth + jWidth;
                final int jWidth3 = jWidth2 + jWidth;
                final int jWidth4 = jWidth3 + jWidth;
                final double[] outBlock = out.blocks[blockIndex];
                for (int kBlock = 0; kBlock < this.blockColumns; ++kBlock) {
                    final int kWidth = this.blockWidth(kBlock);
                    final double[] tBlock = this.blocks[iBlock * this.blockColumns + kBlock];
                    final double[] mBlock = m.blocks[kBlock * m.blockColumns + jBlock];
                    int k = 0;
                    for (int p = pStart; p < pEnd; ++p) {
                        final int lStart = (p - pStart) * kWidth;
                        final int lEnd = lStart + kWidth;
                        for (int nStart = 0; nStart < jWidth; ++nStart) {
                            double sum = 0.0;
                            int l;
                            int n;
                            for (l = lStart, n = nStart; l < lEnd - 3; l += 4, n += jWidth4) {
                                sum += tBlock[l] * mBlock[n] + tBlock[l + 1] * mBlock[n + jWidth] + tBlock[l + 2] * mBlock[n + jWidth2] + tBlock[l + 3] * mBlock[n + jWidth3];
                            }
                            while (l < lEnd) {
                                sum += tBlock[l++] * mBlock[n];
                                n += jWidth;
                            }
                            final double[] array = outBlock;
                            final int n2 = k;
                            array[n2] += sum;
                            ++k;
                        }
                    }
                }
                ++blockIndex;
            }
        }
        return out;
    }
    
    @Override
    public double[][] getData() {
        final double[][] data = new double[this.getRowDimension()][this.getColumnDimension()];
        final int lastColumns = this.columns - (this.blockColumns - 1) * 52;
        for (int iBlock = 0; iBlock < this.blockRows; ++iBlock) {
            final int pStart = iBlock * 52;
            final int pEnd = FastMath.min(pStart + 52, this.rows);
            int regularPos = 0;
            int lastPos = 0;
            for (int p = pStart; p < pEnd; ++p) {
                final double[] dataP = data[p];
                int blockIndex = iBlock * this.blockColumns;
                int dataPos = 0;
                for (int jBlock = 0; jBlock < this.blockColumns - 1; ++jBlock) {
                    System.arraycopy(this.blocks[blockIndex++], regularPos, dataP, dataPos, 52);
                    dataPos += 52;
                }
                System.arraycopy(this.blocks[blockIndex], lastPos, dataP, dataPos, lastColumns);
                regularPos += 52;
                lastPos += lastColumns;
            }
        }
        return data;
    }
    
    @Override
    public double getNorm() {
        final double[] colSums = new double[52];
        double maxColSum = 0.0;
        for (int jBlock = 0; jBlock < this.blockColumns; ++jBlock) {
            final int jWidth = this.blockWidth(jBlock);
            Arrays.fill(colSums, 0, jWidth, 0.0);
            for (int iBlock = 0; iBlock < this.blockRows; ++iBlock) {
                final int iHeight = this.blockHeight(iBlock);
                final double[] block = this.blocks[iBlock * this.blockColumns + jBlock];
                for (int j = 0; j < jWidth; ++j) {
                    double sum = 0.0;
                    for (int i = 0; i < iHeight; ++i) {
                        sum += FastMath.abs(block[i * jWidth + j]);
                    }
                    final double[] array = colSums;
                    final int n = j;
                    array[n] += sum;
                }
            }
            for (int k = 0; k < jWidth; ++k) {
                maxColSum = FastMath.max(maxColSum, colSums[k]);
            }
        }
        return maxColSum;
    }
    
    @Override
    public double getFrobeniusNorm() {
        double sum2 = 0.0;
        for (int blockIndex = 0; blockIndex < this.blocks.length; ++blockIndex) {
            for (final double entry : this.blocks[blockIndex]) {
                sum2 += entry * entry;
            }
        }
        return FastMath.sqrt(sum2);
    }
    
    @Override
    public BlockRealMatrix getSubMatrix(final int startRow, final int endRow, final int startColumn, final int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
        final BlockRealMatrix out = new BlockRealMatrix(endRow - startRow + 1, endColumn - startColumn + 1);
        final int blockStartRow = startRow / 52;
        final int rowsShift = startRow % 52;
        final int blockStartColumn = startColumn / 52;
        final int columnsShift = startColumn % 52;
        int pBlock = blockStartRow;
        for (int iBlock = 0; iBlock < out.blockRows; ++iBlock) {
            final int iHeight = out.blockHeight(iBlock);
            int qBlock = blockStartColumn;
            for (int jBlock = 0; jBlock < out.blockColumns; ++jBlock) {
                final int jWidth = out.blockWidth(jBlock);
                final int outIndex = iBlock * out.blockColumns + jBlock;
                final double[] outBlock = out.blocks[outIndex];
                final int index = pBlock * this.blockColumns + qBlock;
                final int width = this.blockWidth(qBlock);
                final int heightExcess = iHeight + rowsShift - 52;
                final int widthExcess = jWidth + columnsShift - 52;
                if (heightExcess > 0) {
                    if (widthExcess > 0) {
                        final int width2 = this.blockWidth(qBlock + 1);
                        this.copyBlockPart(this.blocks[index], width, rowsShift, 52, columnsShift, 52, outBlock, jWidth, 0, 0);
                        this.copyBlockPart(this.blocks[index + 1], width2, rowsShift, 52, 0, widthExcess, outBlock, jWidth, 0, jWidth - widthExcess);
                        this.copyBlockPart(this.blocks[index + this.blockColumns], width, 0, heightExcess, columnsShift, 52, outBlock, jWidth, iHeight - heightExcess, 0);
                        this.copyBlockPart(this.blocks[index + this.blockColumns + 1], width2, 0, heightExcess, 0, widthExcess, outBlock, jWidth, iHeight - heightExcess, jWidth - widthExcess);
                    }
                    else {
                        this.copyBlockPart(this.blocks[index], width, rowsShift, 52, columnsShift, jWidth + columnsShift, outBlock, jWidth, 0, 0);
                        this.copyBlockPart(this.blocks[index + this.blockColumns], width, 0, heightExcess, columnsShift, jWidth + columnsShift, outBlock, jWidth, iHeight - heightExcess, 0);
                    }
                }
                else if (widthExcess > 0) {
                    final int width2 = this.blockWidth(qBlock + 1);
                    this.copyBlockPart(this.blocks[index], width, rowsShift, iHeight + rowsShift, columnsShift, 52, outBlock, jWidth, 0, 0);
                    this.copyBlockPart(this.blocks[index + 1], width2, rowsShift, iHeight + rowsShift, 0, widthExcess, outBlock, jWidth, 0, jWidth - widthExcess);
                }
                else {
                    this.copyBlockPart(this.blocks[index], width, rowsShift, iHeight + rowsShift, columnsShift, jWidth + columnsShift, outBlock, jWidth, 0, 0);
                }
                ++qBlock;
            }
            ++pBlock;
        }
        return out;
    }
    
    private void copyBlockPart(final double[] srcBlock, final int srcWidth, final int srcStartRow, final int srcEndRow, final int srcStartColumn, final int srcEndColumn, final double[] dstBlock, final int dstWidth, final int dstStartRow, final int dstStartColumn) {
        final int length = srcEndColumn - srcStartColumn;
        int srcPos = srcStartRow * srcWidth + srcStartColumn;
        int dstPos = dstStartRow * dstWidth + dstStartColumn;
        for (int srcRow = srcStartRow; srcRow < srcEndRow; ++srcRow) {
            System.arraycopy(srcBlock, srcPos, dstBlock, dstPos, length);
            srcPos += srcWidth;
            dstPos += dstWidth;
        }
    }
    
    @Override
    public void setSubMatrix(final double[][] subMatrix, final int row, final int column) throws OutOfRangeException, NoDataException, NullArgumentException, DimensionMismatchException {
        MathUtils.checkNotNull(subMatrix);
        final int refLength = subMatrix[0].length;
        if (refLength == 0) {
            throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_COLUMN);
        }
        final int endRow = row + subMatrix.length - 1;
        final int endColumn = column + refLength - 1;
        MatrixUtils.checkSubMatrixIndex(this, row, endRow, column, endColumn);
        for (final double[] subRow : subMatrix) {
            if (subRow.length != refLength) {
                throw new DimensionMismatchException(refLength, subRow.length);
            }
        }
        final int blockStartRow = row / 52;
        final int blockEndRow = (endRow + 52) / 52;
        final int blockStartColumn = column / 52;
        final int blockEndColumn = (endColumn + 52) / 52;
        for (int iBlock = blockStartRow; iBlock < blockEndRow; ++iBlock) {
            final int iHeight = this.blockHeight(iBlock);
            final int firstRow = iBlock * 52;
            final int iStart = FastMath.max(row, firstRow);
            final int iEnd = FastMath.min(endRow + 1, firstRow + iHeight);
            for (int jBlock = blockStartColumn; jBlock < blockEndColumn; ++jBlock) {
                final int jWidth = this.blockWidth(jBlock);
                final int firstColumn = jBlock * 52;
                final int jStart = FastMath.max(column, firstColumn);
                final int jEnd = FastMath.min(endColumn + 1, firstColumn + jWidth);
                final int jLength = jEnd - jStart;
                final double[] block = this.blocks[iBlock * this.blockColumns + jBlock];
                for (int i = iStart; i < iEnd; ++i) {
                    System.arraycopy(subMatrix[i - row], jStart - column, block, (i - firstRow) * jWidth + (jStart - firstColumn), jLength);
                }
            }
        }
    }
    
    @Override
    public BlockRealMatrix getRowMatrix(final int row) throws OutOfRangeException {
        MatrixUtils.checkRowIndex(this, row);
        final BlockRealMatrix out = new BlockRealMatrix(1, this.columns);
        final int iBlock = row / 52;
        final int iRow = row - iBlock * 52;
        int outBlockIndex = 0;
        int outIndex = 0;
        double[] outBlock = out.blocks[outBlockIndex];
        for (int jBlock = 0; jBlock < this.blockColumns; ++jBlock) {
            final int jWidth = this.blockWidth(jBlock);
            final double[] block = this.blocks[iBlock * this.blockColumns + jBlock];
            final int available = outBlock.length - outIndex;
            if (jWidth > available) {
                System.arraycopy(block, iRow * jWidth, outBlock, outIndex, available);
                outBlock = out.blocks[++outBlockIndex];
                System.arraycopy(block, iRow * jWidth, outBlock, 0, jWidth - available);
                outIndex = jWidth - available;
            }
            else {
                System.arraycopy(block, iRow * jWidth, outBlock, outIndex, jWidth);
                outIndex += jWidth;
            }
        }
        return out;
    }
    
    @Override
    public void setRowMatrix(final int row, final RealMatrix matrix) throws OutOfRangeException, MatrixDimensionMismatchException {
        try {
            this.setRowMatrix(row, (BlockRealMatrix)matrix);
        }
        catch (final ClassCastException cce) {
            super.setRowMatrix(row, matrix);
        }
    }
    
    public void setRowMatrix(final int row, final BlockRealMatrix matrix) throws OutOfRangeException, MatrixDimensionMismatchException {
        MatrixUtils.checkRowIndex(this, row);
        final int nCols = this.getColumnDimension();
        if (matrix.getRowDimension() != 1 || matrix.getColumnDimension() != nCols) {
            throw new MatrixDimensionMismatchException(matrix.getRowDimension(), matrix.getColumnDimension(), 1, nCols);
        }
        final int iBlock = row / 52;
        final int iRow = row - iBlock * 52;
        int mBlockIndex = 0;
        int mIndex = 0;
        double[] mBlock = matrix.blocks[mBlockIndex];
        for (int jBlock = 0; jBlock < this.blockColumns; ++jBlock) {
            final int jWidth = this.blockWidth(jBlock);
            final double[] block = this.blocks[iBlock * this.blockColumns + jBlock];
            final int available = mBlock.length - mIndex;
            if (jWidth > available) {
                System.arraycopy(mBlock, mIndex, block, iRow * jWidth, available);
                mBlock = matrix.blocks[++mBlockIndex];
                System.arraycopy(mBlock, 0, block, iRow * jWidth, jWidth - available);
                mIndex = jWidth - available;
            }
            else {
                System.arraycopy(mBlock, mIndex, block, iRow * jWidth, jWidth);
                mIndex += jWidth;
            }
        }
    }
    
    @Override
    public BlockRealMatrix getColumnMatrix(final int column) throws OutOfRangeException {
        MatrixUtils.checkColumnIndex(this, column);
        final BlockRealMatrix out = new BlockRealMatrix(this.rows, 1);
        final int jBlock = column / 52;
        final int jColumn = column - jBlock * 52;
        final int jWidth = this.blockWidth(jBlock);
        int outBlockIndex = 0;
        int outIndex = 0;
        double[] outBlock = out.blocks[outBlockIndex];
        for (int iBlock = 0; iBlock < this.blockRows; ++iBlock) {
            final int iHeight = this.blockHeight(iBlock);
            final double[] block = this.blocks[iBlock * this.blockColumns + jBlock];
            for (int i = 0; i < iHeight; ++i) {
                if (outIndex >= outBlock.length) {
                    outBlock = out.blocks[++outBlockIndex];
                    outIndex = 0;
                }
                outBlock[outIndex++] = block[i * jWidth + jColumn];
            }
        }
        return out;
    }
    
    @Override
    public void setColumnMatrix(final int column, final RealMatrix matrix) throws OutOfRangeException, MatrixDimensionMismatchException {
        try {
            this.setColumnMatrix(column, (BlockRealMatrix)matrix);
        }
        catch (final ClassCastException cce) {
            super.setColumnMatrix(column, matrix);
        }
    }
    
    void setColumnMatrix(final int column, final BlockRealMatrix matrix) throws OutOfRangeException, MatrixDimensionMismatchException {
        MatrixUtils.checkColumnIndex(this, column);
        final int nRows = this.getRowDimension();
        if (matrix.getRowDimension() != nRows || matrix.getColumnDimension() != 1) {
            throw new MatrixDimensionMismatchException(matrix.getRowDimension(), matrix.getColumnDimension(), nRows, 1);
        }
        final int jBlock = column / 52;
        final int jColumn = column - jBlock * 52;
        final int jWidth = this.blockWidth(jBlock);
        int mBlockIndex = 0;
        int mIndex = 0;
        double[] mBlock = matrix.blocks[mBlockIndex];
        for (int iBlock = 0; iBlock < this.blockRows; ++iBlock) {
            final int iHeight = this.blockHeight(iBlock);
            final double[] block = this.blocks[iBlock * this.blockColumns + jBlock];
            for (int i = 0; i < iHeight; ++i) {
                if (mIndex >= mBlock.length) {
                    mBlock = matrix.blocks[++mBlockIndex];
                    mIndex = 0;
                }
                block[i * jWidth + jColumn] = mBlock[mIndex++];
            }
        }
    }
    
    @Override
    public RealVector getRowVector(final int row) throws OutOfRangeException {
        MatrixUtils.checkRowIndex(this, row);
        final double[] outData = new double[this.columns];
        final int iBlock = row / 52;
        final int iRow = row - iBlock * 52;
        int outIndex = 0;
        for (int jBlock = 0; jBlock < this.blockColumns; ++jBlock) {
            final int jWidth = this.blockWidth(jBlock);
            final double[] block = this.blocks[iBlock * this.blockColumns + jBlock];
            System.arraycopy(block, iRow * jWidth, outData, outIndex, jWidth);
            outIndex += jWidth;
        }
        return new ArrayRealVector(outData, false);
    }
    
    @Override
    public void setRowVector(final int row, final RealVector vector) throws OutOfRangeException, MatrixDimensionMismatchException {
        try {
            this.setRow(row, ((ArrayRealVector)vector).getDataRef());
        }
        catch (final ClassCastException cce) {
            super.setRowVector(row, vector);
        }
    }
    
    @Override
    public RealVector getColumnVector(final int column) throws OutOfRangeException {
        MatrixUtils.checkColumnIndex(this, column);
        final double[] outData = new double[this.rows];
        final int jBlock = column / 52;
        final int jColumn = column - jBlock * 52;
        final int jWidth = this.blockWidth(jBlock);
        int outIndex = 0;
        for (int iBlock = 0; iBlock < this.blockRows; ++iBlock) {
            final int iHeight = this.blockHeight(iBlock);
            final double[] block = this.blocks[iBlock * this.blockColumns + jBlock];
            for (int i = 0; i < iHeight; ++i) {
                outData[outIndex++] = block[i * jWidth + jColumn];
            }
        }
        return new ArrayRealVector(outData, false);
    }
    
    @Override
    public void setColumnVector(final int column, final RealVector vector) throws OutOfRangeException, MatrixDimensionMismatchException {
        try {
            this.setColumn(column, ((ArrayRealVector)vector).getDataRef());
        }
        catch (final ClassCastException cce) {
            super.setColumnVector(column, vector);
        }
    }
    
    @Override
    public double[] getRow(final int row) throws OutOfRangeException {
        MatrixUtils.checkRowIndex(this, row);
        final double[] out = new double[this.columns];
        final int iBlock = row / 52;
        final int iRow = row - iBlock * 52;
        int outIndex = 0;
        for (int jBlock = 0; jBlock < this.blockColumns; ++jBlock) {
            final int jWidth = this.blockWidth(jBlock);
            final double[] block = this.blocks[iBlock * this.blockColumns + jBlock];
            System.arraycopy(block, iRow * jWidth, out, outIndex, jWidth);
            outIndex += jWidth;
        }
        return out;
    }
    
    @Override
    public void setRow(final int row, final double[] array) throws OutOfRangeException, MatrixDimensionMismatchException {
        MatrixUtils.checkRowIndex(this, row);
        final int nCols = this.getColumnDimension();
        if (array.length != nCols) {
            throw new MatrixDimensionMismatchException(1, array.length, 1, nCols);
        }
        final int iBlock = row / 52;
        final int iRow = row - iBlock * 52;
        int outIndex = 0;
        for (int jBlock = 0; jBlock < this.blockColumns; ++jBlock) {
            final int jWidth = this.blockWidth(jBlock);
            final double[] block = this.blocks[iBlock * this.blockColumns + jBlock];
            System.arraycopy(array, outIndex, block, iRow * jWidth, jWidth);
            outIndex += jWidth;
        }
    }
    
    @Override
    public double[] getColumn(final int column) throws OutOfRangeException {
        MatrixUtils.checkColumnIndex(this, column);
        final double[] out = new double[this.rows];
        final int jBlock = column / 52;
        final int jColumn = column - jBlock * 52;
        final int jWidth = this.blockWidth(jBlock);
        int outIndex = 0;
        for (int iBlock = 0; iBlock < this.blockRows; ++iBlock) {
            final int iHeight = this.blockHeight(iBlock);
            final double[] block = this.blocks[iBlock * this.blockColumns + jBlock];
            for (int i = 0; i < iHeight; ++i) {
                out[outIndex++] = block[i * jWidth + jColumn];
            }
        }
        return out;
    }
    
    @Override
    public void setColumn(final int column, final double[] array) throws OutOfRangeException, MatrixDimensionMismatchException {
        MatrixUtils.checkColumnIndex(this, column);
        final int nRows = this.getRowDimension();
        if (array.length != nRows) {
            throw new MatrixDimensionMismatchException(array.length, 1, nRows, 1);
        }
        final int jBlock = column / 52;
        final int jColumn = column - jBlock * 52;
        final int jWidth = this.blockWidth(jBlock);
        int outIndex = 0;
        for (int iBlock = 0; iBlock < this.blockRows; ++iBlock) {
            final int iHeight = this.blockHeight(iBlock);
            final double[] block = this.blocks[iBlock * this.blockColumns + jBlock];
            for (int i = 0; i < iHeight; ++i) {
                block[i * jWidth + jColumn] = array[outIndex++];
            }
        }
    }
    
    @Override
    public double getEntry(final int row, final int column) throws OutOfRangeException {
        MatrixUtils.checkMatrixIndex(this, row, column);
        final int iBlock = row / 52;
        final int jBlock = column / 52;
        final int k = (row - iBlock * 52) * this.blockWidth(jBlock) + (column - jBlock * 52);
        return this.blocks[iBlock * this.blockColumns + jBlock][k];
    }
    
    @Override
    public void setEntry(final int row, final int column, final double value) throws OutOfRangeException {
        MatrixUtils.checkMatrixIndex(this, row, column);
        final int iBlock = row / 52;
        final int jBlock = column / 52;
        final int k = (row - iBlock * 52) * this.blockWidth(jBlock) + (column - jBlock * 52);
        this.blocks[iBlock * this.blockColumns + jBlock][k] = value;
    }
    
    @Override
    public void addToEntry(final int row, final int column, final double increment) throws OutOfRangeException {
        MatrixUtils.checkMatrixIndex(this, row, column);
        final int iBlock = row / 52;
        final int jBlock = column / 52;
        final int k = (row - iBlock * 52) * this.blockWidth(jBlock) + (column - jBlock * 52);
        final double[] array = this.blocks[iBlock * this.blockColumns + jBlock];
        final int n = k;
        array[n] += increment;
    }
    
    @Override
    public void multiplyEntry(final int row, final int column, final double factor) throws OutOfRangeException {
        MatrixUtils.checkMatrixIndex(this, row, column);
        final int iBlock = row / 52;
        final int jBlock = column / 52;
        final int k = (row - iBlock * 52) * this.blockWidth(jBlock) + (column - jBlock * 52);
        final double[] array = this.blocks[iBlock * this.blockColumns + jBlock];
        final int n = k;
        array[n] *= factor;
    }
    
    @Override
    public BlockRealMatrix transpose() {
        final int nRows = this.getRowDimension();
        final int nCols = this.getColumnDimension();
        final BlockRealMatrix out = new BlockRealMatrix(nCols, nRows);
        int blockIndex = 0;
        for (int iBlock = 0; iBlock < this.blockColumns; ++iBlock) {
            for (int jBlock = 0; jBlock < this.blockRows; ++jBlock) {
                final double[] outBlock = out.blocks[blockIndex];
                final double[] tBlock = this.blocks[jBlock * this.blockColumns + iBlock];
                final int pStart = iBlock * 52;
                final int pEnd = FastMath.min(pStart + 52, this.columns);
                final int qStart = jBlock * 52;
                final int qEnd = FastMath.min(qStart + 52, this.rows);
                int k = 0;
                for (int p = pStart; p < pEnd; ++p) {
                    final int lInc = pEnd - pStart;
                    int l = p - pStart;
                    for (int q = qStart; q < qEnd; ++q) {
                        outBlock[k] = tBlock[l];
                        ++k;
                        l += lInc;
                    }
                }
                ++blockIndex;
            }
        }
        return out;
    }
    
    @Override
    public int getRowDimension() {
        return this.rows;
    }
    
    @Override
    public int getColumnDimension() {
        return this.columns;
    }
    
    @Override
    public double[] operate(final double[] v) throws DimensionMismatchException {
        if (v.length != this.columns) {
            throw new DimensionMismatchException(v.length, this.columns);
        }
        final double[] out = new double[this.rows];
        for (int iBlock = 0; iBlock < this.blockRows; ++iBlock) {
            final int pStart = iBlock * 52;
            final int pEnd = FastMath.min(pStart + 52, this.rows);
            for (int jBlock = 0; jBlock < this.blockColumns; ++jBlock) {
                final double[] block = this.blocks[iBlock * this.blockColumns + jBlock];
                final int qStart = jBlock * 52;
                final int qEnd = FastMath.min(qStart + 52, this.columns);
                int k = 0;
                for (int p = pStart; p < pEnd; ++p) {
                    double sum = 0.0;
                    int q;
                    for (q = qStart; q < qEnd - 3; q += 4) {
                        sum += block[k] * v[q] + block[k + 1] * v[q + 1] + block[k + 2] * v[q + 2] + block[k + 3] * v[q + 3];
                        k += 4;
                    }
                    while (q < qEnd) {
                        sum += block[k++] * v[q++];
                    }
                    final double[] array = out;
                    final int n = p;
                    array[n] += sum;
                }
            }
        }
        return out;
    }
    
    @Override
    public double[] preMultiply(final double[] v) throws DimensionMismatchException {
        if (v.length != this.rows) {
            throw new DimensionMismatchException(v.length, this.rows);
        }
        final double[] out = new double[this.columns];
        for (int jBlock = 0; jBlock < this.blockColumns; ++jBlock) {
            final int jWidth = this.blockWidth(jBlock);
            final int jWidth2 = jWidth + jWidth;
            final int jWidth3 = jWidth2 + jWidth;
            final int jWidth4 = jWidth3 + jWidth;
            final int qStart = jBlock * 52;
            final int qEnd = FastMath.min(qStart + 52, this.columns);
            for (int iBlock = 0; iBlock < this.blockRows; ++iBlock) {
                final double[] block = this.blocks[iBlock * this.blockColumns + jBlock];
                final int pStart = iBlock * 52;
                final int pEnd = FastMath.min(pStart + 52, this.rows);
                for (int q = qStart; q < qEnd; ++q) {
                    int k = q - qStart;
                    double sum = 0.0;
                    int p;
                    for (p = pStart; p < pEnd - 3; p += 4) {
                        sum += block[k] * v[p] + block[k + jWidth] * v[p + 1] + block[k + jWidth2] * v[p + 2] + block[k + jWidth3] * v[p + 3];
                        k += jWidth4;
                    }
                    while (p < pEnd) {
                        sum += block[k] * v[p++];
                        k += jWidth;
                    }
                    final double[] array = out;
                    final int n = q;
                    array[n] += sum;
                }
            }
        }
        return out;
    }
    
    @Override
    public double walkInRowOrder(final RealMatrixChangingVisitor visitor) {
        visitor.start(this.rows, this.columns, 0, this.rows - 1, 0, this.columns - 1);
        for (int iBlock = 0; iBlock < this.blockRows; ++iBlock) {
            final int pStart = iBlock * 52;
            for (int pEnd = FastMath.min(pStart + 52, this.rows), p = pStart; p < pEnd; ++p) {
                for (int jBlock = 0; jBlock < this.blockColumns; ++jBlock) {
                    final int jWidth = this.blockWidth(jBlock);
                    final int qStart = jBlock * 52;
                    final int qEnd = FastMath.min(qStart + 52, this.columns);
                    final double[] block = this.blocks[iBlock * this.blockColumns + jBlock];
                    int k = (p - pStart) * jWidth;
                    for (int q = qStart; q < qEnd; ++q) {
                        block[k] = visitor.visit(p, q, block[k]);
                        ++k;
                    }
                }
            }
        }
        return visitor.end();
    }
    
    @Override
    public double walkInRowOrder(final RealMatrixPreservingVisitor visitor) {
        visitor.start(this.rows, this.columns, 0, this.rows - 1, 0, this.columns - 1);
        for (int iBlock = 0; iBlock < this.blockRows; ++iBlock) {
            final int pStart = iBlock * 52;
            for (int pEnd = FastMath.min(pStart + 52, this.rows), p = pStart; p < pEnd; ++p) {
                for (int jBlock = 0; jBlock < this.blockColumns; ++jBlock) {
                    final int jWidth = this.blockWidth(jBlock);
                    final int qStart = jBlock * 52;
                    final int qEnd = FastMath.min(qStart + 52, this.columns);
                    final double[] block = this.blocks[iBlock * this.blockColumns + jBlock];
                    int k = (p - pStart) * jWidth;
                    for (int q = qStart; q < qEnd; ++q) {
                        visitor.visit(p, q, block[k]);
                        ++k;
                    }
                }
            }
        }
        return visitor.end();
    }
    
    @Override
    public double walkInRowOrder(final RealMatrixChangingVisitor visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
        visitor.start(this.rows, this.columns, startRow, endRow, startColumn, endColumn);
        for (int iBlock = startRow / 52; iBlock < 1 + endRow / 52; ++iBlock) {
            final int p0 = iBlock * 52;
            final int pStart = FastMath.max(startRow, p0);
            for (int pEnd = FastMath.min((iBlock + 1) * 52, 1 + endRow), p2 = pStart; p2 < pEnd; ++p2) {
                for (int jBlock = startColumn / 52; jBlock < 1 + endColumn / 52; ++jBlock) {
                    final int jWidth = this.blockWidth(jBlock);
                    final int q0 = jBlock * 52;
                    final int qStart = FastMath.max(startColumn, q0);
                    final int qEnd = FastMath.min((jBlock + 1) * 52, 1 + endColumn);
                    final double[] block = this.blocks[iBlock * this.blockColumns + jBlock];
                    int k = (p2 - p0) * jWidth + qStart - q0;
                    for (int q2 = qStart; q2 < qEnd; ++q2) {
                        block[k] = visitor.visit(p2, q2, block[k]);
                        ++k;
                    }
                }
            }
        }
        return visitor.end();
    }
    
    @Override
    public double walkInRowOrder(final RealMatrixPreservingVisitor visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
        visitor.start(this.rows, this.columns, startRow, endRow, startColumn, endColumn);
        for (int iBlock = startRow / 52; iBlock < 1 + endRow / 52; ++iBlock) {
            final int p0 = iBlock * 52;
            final int pStart = FastMath.max(startRow, p0);
            for (int pEnd = FastMath.min((iBlock + 1) * 52, 1 + endRow), p2 = pStart; p2 < pEnd; ++p2) {
                for (int jBlock = startColumn / 52; jBlock < 1 + endColumn / 52; ++jBlock) {
                    final int jWidth = this.blockWidth(jBlock);
                    final int q0 = jBlock * 52;
                    final int qStart = FastMath.max(startColumn, q0);
                    final int qEnd = FastMath.min((jBlock + 1) * 52, 1 + endColumn);
                    final double[] block = this.blocks[iBlock * this.blockColumns + jBlock];
                    int k = (p2 - p0) * jWidth + qStart - q0;
                    for (int q2 = qStart; q2 < qEnd; ++q2) {
                        visitor.visit(p2, q2, block[k]);
                        ++k;
                    }
                }
            }
        }
        return visitor.end();
    }
    
    @Override
    public double walkInOptimizedOrder(final RealMatrixChangingVisitor visitor) {
        visitor.start(this.rows, this.columns, 0, this.rows - 1, 0, this.columns - 1);
        int blockIndex = 0;
        for (int iBlock = 0; iBlock < this.blockRows; ++iBlock) {
            final int pStart = iBlock * 52;
            final int pEnd = FastMath.min(pStart + 52, this.rows);
            for (int jBlock = 0; jBlock < this.blockColumns; ++jBlock) {
                final int qStart = jBlock * 52;
                final int qEnd = FastMath.min(qStart + 52, this.columns);
                final double[] block = this.blocks[blockIndex];
                int k = 0;
                for (int p = pStart; p < pEnd; ++p) {
                    for (int q = qStart; q < qEnd; ++q) {
                        block[k] = visitor.visit(p, q, block[k]);
                        ++k;
                    }
                }
                ++blockIndex;
            }
        }
        return visitor.end();
    }
    
    @Override
    public double walkInOptimizedOrder(final RealMatrixPreservingVisitor visitor) {
        visitor.start(this.rows, this.columns, 0, this.rows - 1, 0, this.columns - 1);
        int blockIndex = 0;
        for (int iBlock = 0; iBlock < this.blockRows; ++iBlock) {
            final int pStart = iBlock * 52;
            final int pEnd = FastMath.min(pStart + 52, this.rows);
            for (int jBlock = 0; jBlock < this.blockColumns; ++jBlock) {
                final int qStart = jBlock * 52;
                final int qEnd = FastMath.min(qStart + 52, this.columns);
                final double[] block = this.blocks[blockIndex];
                int k = 0;
                for (int p = pStart; p < pEnd; ++p) {
                    for (int q = qStart; q < qEnd; ++q) {
                        visitor.visit(p, q, block[k]);
                        ++k;
                    }
                }
                ++blockIndex;
            }
        }
        return visitor.end();
    }
    
    @Override
    public double walkInOptimizedOrder(final RealMatrixChangingVisitor visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
        visitor.start(this.rows, this.columns, startRow, endRow, startColumn, endColumn);
        for (int iBlock = startRow / 52; iBlock < 1 + endRow / 52; ++iBlock) {
            final int p0 = iBlock * 52;
            final int pStart = FastMath.max(startRow, p0);
            final int pEnd = FastMath.min((iBlock + 1) * 52, 1 + endRow);
            for (int jBlock = startColumn / 52; jBlock < 1 + endColumn / 52; ++jBlock) {
                final int jWidth = this.blockWidth(jBlock);
                final int q0 = jBlock * 52;
                final int qStart = FastMath.max(startColumn, q0);
                final int qEnd = FastMath.min((jBlock + 1) * 52, 1 + endColumn);
                final double[] block = this.blocks[iBlock * this.blockColumns + jBlock];
                for (int p2 = pStart; p2 < pEnd; ++p2) {
                    int k = (p2 - p0) * jWidth + qStart - q0;
                    for (int q2 = qStart; q2 < qEnd; ++q2) {
                        block[k] = visitor.visit(p2, q2, block[k]);
                        ++k;
                    }
                }
            }
        }
        return visitor.end();
    }
    
    @Override
    public double walkInOptimizedOrder(final RealMatrixPreservingVisitor visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
        visitor.start(this.rows, this.columns, startRow, endRow, startColumn, endColumn);
        for (int iBlock = startRow / 52; iBlock < 1 + endRow / 52; ++iBlock) {
            final int p0 = iBlock * 52;
            final int pStart = FastMath.max(startRow, p0);
            final int pEnd = FastMath.min((iBlock + 1) * 52, 1 + endRow);
            for (int jBlock = startColumn / 52; jBlock < 1 + endColumn / 52; ++jBlock) {
                final int jWidth = this.blockWidth(jBlock);
                final int q0 = jBlock * 52;
                final int qStart = FastMath.max(startColumn, q0);
                final int qEnd = FastMath.min((jBlock + 1) * 52, 1 + endColumn);
                final double[] block = this.blocks[iBlock * this.blockColumns + jBlock];
                for (int p2 = pStart; p2 < pEnd; ++p2) {
                    int k = (p2 - p0) * jWidth + qStart - q0;
                    for (int q2 = qStart; q2 < qEnd; ++q2) {
                        visitor.visit(p2, q2, block[k]);
                        ++k;
                    }
                }
            }
        }
        return visitor.end();
    }
    
    private int blockHeight(final int blockRow) {
        return (blockRow == this.blockRows - 1) ? (this.rows - blockRow * 52) : 52;
    }
    
    private int blockWidth(final int blockColumn) {
        return (blockColumn == this.blockColumns - 1) ? (this.columns - blockColumn * 52) : 52;
    }
}
