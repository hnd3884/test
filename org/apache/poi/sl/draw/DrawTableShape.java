package org.apache.poi.sl.draw;

import java.awt.Color;
import org.apache.poi.sl.usermodel.TextShape;
import org.apache.poi.sl.usermodel.StrokeStyle;
import java.awt.geom.Rectangle2D;
import java.awt.Paint;
import java.awt.geom.Line2D;
import java.awt.Stroke;
import org.apache.poi.sl.usermodel.TableCell;
import org.apache.poi.sl.usermodel.PlaceableShape;
import org.apache.poi.sl.usermodel.GroupShape;
import java.awt.Graphics2D;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.TableShape;
import org.apache.poi.util.Internal;

public class DrawTableShape extends DrawShape
{
    @Internal
    public static final int borderSize = 2;
    
    public DrawTableShape(final TableShape<?, ?> shape) {
        super(shape);
    }
    
    protected Drawable getGroupShape(final Graphics2D graphics) {
        if (this.shape instanceof GroupShape) {
            final DrawFactory df = DrawFactory.getInstance(graphics);
            return df.getDrawable((GroupShape)this.shape);
        }
        return null;
    }
    
    @Override
    public void applyTransform(final Graphics2D graphics) {
        final Drawable d = this.getGroupShape(graphics);
        if (d != null) {
            d.applyTransform(graphics);
        }
        else {
            super.applyTransform(graphics);
        }
    }
    
    @Override
    public void draw(final Graphics2D graphics) {
        final Drawable d = this.getGroupShape(graphics);
        if (d != null) {
            d.draw(graphics);
            return;
        }
        final TableShape<?, ?> ts = this.getShape();
        final DrawPaint drawPaint = DrawFactory.getInstance(graphics).getPaint(ts);
        final int rows = ts.getNumberOfRows();
        final int cols = ts.getNumberOfColumns();
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                final TableCell<?, ?> tc = ts.getCell(row, col);
                if (tc != null) {
                    if (!tc.isMerged()) {
                        final Paint fillPaint = drawPaint.getPaint(graphics, tc.getFillStyle().getPaint());
                        graphics.setPaint(fillPaint);
                        final Rectangle2D cellAnc = tc.getAnchor();
                        DrawPaint.fillPaintWorkaround(graphics, cellAnc);
                        for (final TableCell.BorderEdge edge : TableCell.BorderEdge.values()) {
                            final StrokeStyle stroke = tc.getBorderStyle(edge);
                            if (stroke != null) {
                                graphics.setStroke(DrawShape.getStroke(stroke));
                                final Paint linePaint = drawPaint.getPaint(graphics, stroke.getPaint());
                                graphics.setPaint(linePaint);
                                final double x = cellAnc.getX();
                                final double y = cellAnc.getY();
                                final double w = cellAnc.getWidth();
                                final double h = cellAnc.getHeight();
                                Line2D line = null;
                                switch (edge) {
                                    default: {
                                        line = new Line2D.Double(x - 2.0, y + h, x + w + 2.0, y + h);
                                        break;
                                    }
                                    case left: {
                                        line = new Line2D.Double(x, y, x, y + h + 2.0);
                                        break;
                                    }
                                    case right: {
                                        line = new Line2D.Double(x + w, y, x + w, y + h + 2.0);
                                        break;
                                    }
                                    case top: {
                                        line = new Line2D.Double(x - 2.0, y, x + w + 2.0, y);
                                        break;
                                    }
                                }
                                graphics.draw(line);
                            }
                        }
                    }
                }
            }
        }
        this.drawContent(graphics);
    }
    
    @Override
    public void drawContent(final Graphics2D graphics) {
        final Drawable d = this.getGroupShape(graphics);
        if (d != null) {
            d.drawContent(graphics);
            return;
        }
        final TableShape<?, ?> ts = this.getShape();
        final DrawFactory df = DrawFactory.getInstance(graphics);
        final int rows = ts.getNumberOfRows();
        final int cols = ts.getNumberOfColumns();
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                final TableCell<?, ?> tc = ts.getCell(row, col);
                if (tc != null) {
                    final DrawTextShape dts = df.getDrawable(tc);
                    dts.drawContent(graphics);
                }
            }
        }
    }
    
    @Override
    protected TableShape<?, ?> getShape() {
        return (TableShape)this.shape;
    }
    
    public void setAllBorders(final Object... args) {
        final TableShape<?, ?> table = this.getShape();
        final int rows = table.getNumberOfRows();
        final int cols = table.getNumberOfColumns();
        final TableCell.BorderEdge[] edges = { TableCell.BorderEdge.top, TableCell.BorderEdge.left, null, null };
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                edges[2] = ((col == cols - 1) ? TableCell.BorderEdge.right : null);
                edges[3] = ((row == rows - 1) ? TableCell.BorderEdge.bottom : null);
                setEdges(table.getCell(row, col), edges, args);
            }
        }
    }
    
    public void setOutsideBorders(final Object... args) {
        if (args.length == 0) {
            return;
        }
        final TableShape<?, ?> table = this.getShape();
        final int rows = table.getNumberOfRows();
        final int cols = table.getNumberOfColumns();
        final TableCell.BorderEdge[] edges = new TableCell.BorderEdge[4];
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                edges[0] = ((col == 0) ? TableCell.BorderEdge.left : null);
                edges[1] = ((col == cols - 1) ? TableCell.BorderEdge.right : null);
                edges[2] = ((row == 0) ? TableCell.BorderEdge.top : null);
                edges[3] = ((row == rows - 1) ? TableCell.BorderEdge.bottom : null);
                setEdges(table.getCell(row, col), edges, args);
            }
        }
    }
    
    public void setInsideBorders(final Object... args) {
        if (args.length == 0) {
            return;
        }
        final TableShape<?, ?> table = this.getShape();
        final int rows = table.getNumberOfRows();
        final int cols = table.getNumberOfColumns();
        final TableCell.BorderEdge[] edges = new TableCell.BorderEdge[2];
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                edges[0] = ((col > 0 && col < cols - 1) ? TableCell.BorderEdge.right : null);
                edges[1] = ((row > 0 && row < rows - 1) ? TableCell.BorderEdge.bottom : null);
                setEdges(table.getCell(row, col), edges, args);
            }
        }
    }
    
    private static void setEdges(final TableCell<?, ?> cell, final TableCell.BorderEdge[] edges, final Object... args) {
        if (cell == null) {
            return;
        }
        for (final TableCell.BorderEdge be : edges) {
            if (be != null) {
                if (args.length == 0) {
                    cell.removeBorder(be);
                }
                else {
                    for (final Object o : args) {
                        if (o instanceof Double) {
                            cell.setBorderWidth(be, (double)o);
                        }
                        else if (o instanceof Color) {
                            cell.setBorderColor(be, (Color)o);
                        }
                        else if (o instanceof StrokeStyle.LineDash) {
                            cell.setBorderDash(be, (StrokeStyle.LineDash)o);
                        }
                        else if (o instanceof StrokeStyle.LineCompound) {
                            cell.setBorderCompound(be, (StrokeStyle.LineCompound)o);
                        }
                    }
                }
            }
        }
    }
}
