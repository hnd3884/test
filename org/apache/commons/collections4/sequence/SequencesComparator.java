package org.apache.commons.collections4.sequence;

import org.apache.commons.collections4.functors.DefaultEquator;
import org.apache.commons.collections4.Equator;
import java.util.List;

public class SequencesComparator<T>
{
    private final List<T> sequence1;
    private final List<T> sequence2;
    private final Equator<? super T> equator;
    private final int[] vDown;
    private final int[] vUp;
    
    public SequencesComparator(final List<T> sequence1, final List<T> sequence2) {
        this(sequence1, sequence2, DefaultEquator.defaultEquator());
    }
    
    public SequencesComparator(final List<T> sequence1, final List<T> sequence2, final Equator<? super T> equator) {
        this.sequence1 = sequence1;
        this.sequence2 = sequence2;
        this.equator = equator;
        final int size = sequence1.size() + sequence2.size() + 2;
        this.vDown = new int[size];
        this.vUp = new int[size];
    }
    
    public EditScript<T> getScript() {
        final EditScript<T> script = new EditScript<T>();
        this.buildScript(0, this.sequence1.size(), 0, this.sequence2.size(), script);
        return script;
    }
    
    private Snake buildSnake(final int start, final int diag, final int end1, final int end2) {
        int end3;
        for (end3 = start; end3 - diag < end2 && end3 < end1 && this.equator.equate((Object)this.sequence1.get(end3), (Object)this.sequence2.get(end3 - diag)); ++end3) {}
        return new Snake(start, end3, diag);
    }
    
    private Snake getMiddleSnake(final int start1, final int end1, final int start2, final int end2) {
        final int m = end1 - start1;
        final int n = end2 - start2;
        if (m == 0 || n == 0) {
            return null;
        }
        final int delta = m - n;
        final int sum = n + m;
        final int offset = ((sum % 2 == 0) ? sum : (sum + 1)) / 2;
        this.vDown[1 + offset] = start1;
        this.vUp[1 + offset] = end1 + 1;
        for (int d = 0; d <= offset; ++d) {
            for (int k = -d; k <= d; k += 2) {
                final int i = k + offset;
                if (k == -d || (k != d && this.vDown[i - 1] < this.vDown[i + 1])) {
                    this.vDown[i] = this.vDown[i + 1];
                }
                else {
                    this.vDown[i] = this.vDown[i - 1] + 1;
                }
                for (int x = this.vDown[i], y = x - start1 + start2 - k; x < end1 && y < end2 && this.equator.equate((Object)this.sequence1.get(x), (Object)this.sequence2.get(y)); this.vDown[i] = ++x, ++y) {}
                if (delta % 2 != 0 && delta - d <= k && k <= delta + d && this.vUp[i - delta] <= this.vDown[i]) {
                    return this.buildSnake(this.vUp[i - delta], k + start1 - start2, end1, end2);
                }
            }
            for (int k = delta - d; k <= delta + d; k += 2) {
                final int i = k + offset - delta;
                if (k == delta - d || (k != delta + d && this.vUp[i + 1] <= this.vUp[i - 1])) {
                    this.vUp[i] = this.vUp[i + 1] - 1;
                }
                else {
                    this.vUp[i] = this.vUp[i - 1];
                }
                for (int x = this.vUp[i] - 1, y = x - start1 + start2 - k; x >= start1 && y >= start2 && this.equator.equate((Object)this.sequence1.get(x), (Object)this.sequence2.get(y)); this.vUp[i] = x--, --y) {}
                if (delta % 2 == 0 && -d <= k && k <= d && this.vUp[i] <= this.vDown[i + delta]) {
                    return this.buildSnake(this.vUp[i], k + start1 - start2, end1, end2);
                }
            }
        }
        throw new RuntimeException("Internal Error");
    }
    
    private void buildScript(final int start1, final int end1, final int start2, final int end2, final EditScript<T> script) {
        final Snake middle = this.getMiddleSnake(start1, end1, start2, end2);
        if (middle == null || (middle.getStart() == end1 && middle.getDiag() == end1 - end2) || (middle.getEnd() == start1 && middle.getDiag() == start1 - start2)) {
            int i = start1;
            int j = start2;
            while (i < end1 || j < end2) {
                if (i < end1 && j < end2 && this.equator.equate((Object)this.sequence1.get(i), (Object)this.sequence2.get(j))) {
                    script.append(new KeepCommand<T>(this.sequence1.get(i)));
                    ++i;
                    ++j;
                }
                else if (end1 - start1 > end2 - start2) {
                    script.append(new DeleteCommand<T>(this.sequence1.get(i)));
                    ++i;
                }
                else {
                    script.append(new InsertCommand<T>(this.sequence2.get(j)));
                    ++j;
                }
            }
        }
        else {
            this.buildScript(start1, middle.getStart(), start2, middle.getStart() - middle.getDiag(), script);
            for (int i = middle.getStart(); i < middle.getEnd(); ++i) {
                script.append(new KeepCommand<T>(this.sequence1.get(i)));
            }
            this.buildScript(middle.getEnd(), end1, middle.getEnd() - middle.getDiag(), end2, script);
        }
    }
    
    private static class Snake
    {
        private final int start;
        private final int end;
        private final int diag;
        
        public Snake(final int start, final int end, final int diag) {
            this.start = start;
            this.end = end;
            this.diag = diag;
        }
        
        public int getStart() {
            return this.start;
        }
        
        public int getEnd() {
            return this.end;
        }
        
        public int getDiag() {
            return this.diag;
        }
    }
}
