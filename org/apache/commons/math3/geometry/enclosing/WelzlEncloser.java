package org.apache.commons.math3.geometry.enclosing;

import java.util.Iterator;
import org.apache.commons.math3.exception.MathInternalError;
import java.util.List;
import java.util.ArrayList;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;

public class WelzlEncloser<S extends Space, P extends Point<S>> implements Encloser<S, P>
{
    private final double tolerance;
    private final SupportBallGenerator<S, P> generator;
    
    public WelzlEncloser(final double tolerance, final SupportBallGenerator<S, P> generator) {
        this.tolerance = tolerance;
        this.generator = generator;
    }
    
    public EnclosingBall<S, P> enclose(final Iterable<P> points) {
        if (points == null || !points.iterator().hasNext()) {
            return this.generator.ballOnSupport(new ArrayList<P>());
        }
        return this.pivotingBall(points);
    }
    
    private EnclosingBall<S, P> pivotingBall(final Iterable<P> points) {
        final P first = points.iterator().next();
        final List<P> extreme = new ArrayList<P>(first.getSpace().getDimension() + 1);
        final List<P> support = new ArrayList<P>(first.getSpace().getDimension() + 1);
        extreme.add(first);
        EnclosingBall<S, P> ball = this.moveToFrontBall(extreme, extreme.size(), support);
        while (true) {
            final P farthest = this.selectFarthest(points, ball);
            if (ball.contains(farthest, this.tolerance)) {
                return ball;
            }
            support.clear();
            support.add(farthest);
            final EnclosingBall<S, P> savedBall = ball;
            ball = this.moveToFrontBall(extreme, extreme.size(), support);
            if (ball.getRadius() < savedBall.getRadius()) {
                throw new MathInternalError();
            }
            extreme.add(0, farthest);
            extreme.subList(ball.getSupportSize(), extreme.size()).clear();
        }
    }
    
    private EnclosingBall<S, P> moveToFrontBall(final List<P> extreme, final int nbExtreme, final List<P> support) {
        EnclosingBall<S, P> ball = this.generator.ballOnSupport(support);
        if (ball.getSupportSize() <= ball.getCenter().getSpace().getDimension()) {
            for (int i = 0; i < nbExtreme; ++i) {
                final P pi = extreme.get(i);
                if (!ball.contains(pi, this.tolerance)) {
                    support.add(pi);
                    ball = this.moveToFrontBall(extreme, i, support);
                    support.remove(support.size() - 1);
                    for (int j = i; j > 0; --j) {
                        extreme.set(j, extreme.get(j - 1));
                    }
                    extreme.set(0, pi);
                }
            }
        }
        return ball;
    }
    
    public P selectFarthest(final Iterable<P> points, final EnclosingBall<S, P> ball) {
        final P center = ball.getCenter();
        P farthest = null;
        double dMax = -1.0;
        for (final P point : points) {
            final double d = point.distance(center);
            if (d > dMax) {
                farthest = point;
                dMax = d;
            }
        }
        return farthest;
    }
}
