package org.tukaani.xz.lzma;

final class Optimum
{
    private static final int INFINITY_PRICE = 1073741824;
    final State state;
    final int[] reps;
    int price;
    int optPrev;
    int backPrev;
    boolean prev1IsLiteral;
    boolean hasPrev2;
    int optPrev2;
    int backPrev2;
    
    Optimum() {
        this.state = new State();
        this.reps = new int[4];
    }
    
    void reset() {
        this.price = 1073741824;
    }
    
    void set1(final int price, final int optPrev, final int backPrev) {
        this.price = price;
        this.optPrev = optPrev;
        this.backPrev = backPrev;
        this.prev1IsLiteral = false;
    }
    
    void set2(final int price, final int n, final int backPrev) {
        this.price = price;
        this.optPrev = n + 1;
        this.backPrev = backPrev;
        this.prev1IsLiteral = true;
        this.hasPrev2 = false;
    }
    
    void set3(final int price, final int optPrev2, final int backPrev2, final int n, final int backPrev3) {
        this.price = price;
        this.optPrev = optPrev2 + n + 1;
        this.backPrev = backPrev3;
        this.prev1IsLiteral = true;
        this.hasPrev2 = true;
        this.optPrev2 = optPrev2;
        this.backPrev2 = backPrev2;
    }
}
