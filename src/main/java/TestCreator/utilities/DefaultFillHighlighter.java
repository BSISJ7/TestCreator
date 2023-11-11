/*
 * Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package TestCreator.utilities;

import javax.swing.*;
import javax.swing.plaf.TextUI;
import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Vector;


public class DefaultFillHighlighter extends DefaultHighlighter {

    /**
     * Default implementation of LayeredHighlighter.LayerPainter that can
     * be used for painting highlights.
     * <p>
     * As of 1.4 this field is final.
     */
    public static final LayeredHighlighter.LayerPainter DefaultPainter = new DefaultHighlightPainter(null);
    private final static HighlightInfo[] noHighlights =
            new HighlightInfo[0];
    private ArrayList<HighlightInfo> highlights = new ArrayList<HighlightInfo>();
    private JTextComponent component;
    private boolean drawsLayeredHighlights;
    private SafeDamager safeDamager = new SafeDamager();

    public void removeHighlightAt(int offset) {

//        highlights.forEach(highlightInfo -> {
//            if (highlightInfo.getStartOffset() == offset)
//                removeHighlight(highlightInfo);
//
//        });


        Iterator<HighlightInfo> highlightIterator = highlights.iterator();
        while (highlightIterator.hasNext()) {
            HighlightInfo iterator = highlightIterator.next();
            if (iterator.getStartOffset() == offset) {
                highlightIterator.remove();
            }
        }

    }

    public ArrayList<HighlightInfo> getHighlightList() {
        return highlights;
    }

    /**
     * Renders the highlights.
     *
     * @param g the graphics context
     */
    public void paint(Graphics g) {
        // PENDING(prinz) - should cull ranges not visible
        int len = highlights.size();
        for (int i = 0; i < len; i++) {
            HighlightInfo info = highlights.get(i);
            if (!(info instanceof LayeredHighlightInfo)) {
                // Avoid allocing unless we need it.
                Rectangle a = component.getBounds();
                Insets insets = component.getInsets();
                a.x = insets.left;
                a.y = insets.top;
                a.width -= insets.left + insets.right;
                a.height -= insets.top + insets.bottom;
                for (; i < len; i++) {
                    info = highlights.get(i);
                    if (!(info instanceof LayeredHighlightInfo)) {
                        HighlightPainter p = info.getPainter();
                        p.paint(g, info.getStartOffset(), info.getEndOffset(),
                                a, component);
                    }
                }
            }
        }
    }

    /**
     * Called when the UI is being installed into the
     * interface of a JTextComponent.  Installs the editor, and
     * removes any existing highlights.
     *
     * @param component the editor component
     * @see Highlighter#install
     */
    public void install(JTextComponent component) {
        this.component = component;
        removeAllHighlights();
    }

    /**
     * Called when the UI is being removed from the interface of
     * a JTextComponent.
     *
     * @param component the component
     * @see Highlighter#deinstall
     */
    public void deinstall(JTextComponent component) {
        this.component = null;
    }

    /**
     * Adds a highlight to the view.  Returns a tag that can be used
     * to refer to the highlight.
     *
     * @param startOffset      the start offset of the range to highlight &gt;= 0
     * @param endOffset        the end offset of the range to highlight &gt;= p0
     * @param highlightPainter the painter to use to actually render the highlight
     * @return an object that can be used as a tag
     * to refer to the highlight
     * @throws BadLocationException if the specified location is invalid
     */
    public Object addHighlight(int startOffset, int endOffset, HighlightPainter highlightPainter)
            throws BadLocationException, NullPointerException {
//        if (Objects.isNull(component.getDocument())) throw new NullPointerException();

        if (startOffset < 0)
            throw new BadLocationException("Invalid start offset", startOffset);

        if (endOffset < startOffset)
            throw new BadLocationException("Invalid end offset", endOffset);

        if (Objects.nonNull(component)) {
            Document doc = component.getDocument();
            HighlightInfo highlightInfo = (getDrawsLayeredHighlights() &&
                    (highlightPainter instanceof LayeredHighlighter.LayerPainter)) ?
                    new LayeredHighlightInfo() : new HighlightInfo();
            highlightInfo.painter = highlightPainter;
            highlightInfo.p0 = doc.createPosition(startOffset);
            highlightInfo.p1 = doc.createPosition(endOffset);
            highlights.add(highlightInfo);
            safeDamageRange(startOffset, endOffset);
            return highlightInfo;
        } else
            return new HighlightInfo();
    }

    /**
     * Removes a highlight from the view.
     *
     * @param highlight the reference to the highlight
     */
    public void removeHighlight(Object highlight) {
        if (highlight instanceof LayeredHighlightInfo) {
            LayeredHighlightInfo lhi = (LayeredHighlightInfo) highlight;
            if (lhi.width > 0 && lhi.height > 0) {
                component.repaint(lhi.x, lhi.y, lhi.width, lhi.height);
            }
        } else {
            HighlightInfo info = (HighlightInfo) highlight;
            safeDamageRange(info.p0, info.p1);
        }

        //Removes the highlight from the list
        //Causes ConcurrentModificationException
//        highlights.remove(highlight);

        // Removes the highlight from the list
        // Copilot suggestion
        Iterator<HighlightInfo> highlightIterator = highlights.iterator();
        while (highlightIterator.hasNext()) {
            HighlightInfo iterator = highlightIterator.next();
            if (iterator.equals(highlight)) {
                highlightIterator.remove();
            }
        }
    }

    /**
     * Removes all highlights.
     */
    public void removeAllHighlights() {
        TextUI mapper = component.getUI();
        if (getDrawsLayeredHighlights()) {
            int len = highlights.size();
            if (len != 0) {
                int minX = 0;
                int minY = 0;
                int maxX = 0;
                int maxY = 0;
                int p0 = -1;
                int p1 = -1;
                for (int i = 0; i < len; i++) {
                    HighlightInfo hi = highlights.get(i);
                    if (hi instanceof LayeredHighlightInfo) {
                        LayeredHighlightInfo info = (LayeredHighlightInfo) hi;
                        minX = Math.min(minX, info.x);
                        minY = Math.min(minY, info.y);
                        maxX = Math.max(maxX, info.x + info.width);
                        maxY = Math.max(maxY, info.y + info.height);
                    } else {
                        if (p0 == -1) {
                            p0 = hi.p0.getOffset();
                            p1 = hi.p1.getOffset();
                        } else {
                            p0 = Math.min(p0, hi.p0.getOffset());
                            p1 = Math.max(p1, hi.p1.getOffset());
                        }
                    }
                }
                if (minX != maxX && minY != maxY) {
                    component.repaint(minX, minY, maxX - minX, maxY - minY);
                }
                if (p0 != -1) {
                    try {
                        safeDamageRange(p0, p1);
                    } catch (BadLocationException e) {
                    }
                }
                highlights.clear();
            }
        } else if (mapper != null) {
            int len = highlights.size();
            if (len != 0) {
                int p0 = Integer.MAX_VALUE;
                int p1 = 0;
                for (int i = 0; i < len; i++) {
                    HighlightInfo info = highlights.get(i);
                    p0 = Math.min(p0, info.p0.getOffset());
                    p1 = Math.max(p1, info.p1.getOffset());
                }
                try {
                    safeDamageRange(p0, p1);
                } catch (BadLocationException e) {
                }

                highlights.clear();
            }
        }
    }

    /**
     * Changes a highlight.
     *
     * @param highlightTag the highlight tag
     * @param startOffset  the beginning of the range &gt;= 0
     * @param endOffset    the end of the range &gt;= p0
     * @throws BadLocationException if the specified location is invalid
     */
    public void changeHighlight(Object highlightTag, int startOffset, int endOffset) throws BadLocationException {
        if (Objects.isNull(component)) return;

        if (startOffset < 0) throw new BadLocationException("Invalid beginning of the range", startOffset);

        if (endOffset < startOffset) throw new BadLocationException("Invalid end of the range", endOffset);

        Document doc = component.getDocument();
        if (highlightTag instanceof LayeredHighlightInfo) {
            LayeredHighlightInfo lhi = (LayeredHighlightInfo) highlightTag;
            if (lhi.width > 0 && lhi.height > 0) {
                component.repaint(lhi.x, lhi.y, lhi.width, lhi.height);
            }
            // Mark the highlights region as invalid, it will reset itself
            // next time asked to paint.
            lhi.width = lhi.height = 0;
            lhi.p0 = doc.createPosition(startOffset);
            lhi.p1 = doc.createPosition(endOffset);
            safeDamageRange(Math.min(startOffset, endOffset), Math.max(startOffset, endOffset));
        } else {
            HighlightInfo info = (HighlightInfo) highlightTag;
            int oldP0 = info.p0.getOffset();
            int oldP1 = info.p1.getOffset();
            if (startOffset == oldP0) {
                safeDamageRange(Math.min(oldP1, endOffset),
                        Math.max(oldP1, endOffset));
            } else if (endOffset == oldP1) {
                safeDamageRange(Math.min(startOffset, oldP0),
                        Math.max(startOffset, oldP0));
            } else {
                safeDamageRange(oldP0, oldP1);
                safeDamageRange(startOffset, endOffset);
            }
            info.p0 = doc.createPosition(startOffset);
            info.p1 = doc.createPosition(endOffset);
        }
    }

    // ---- member variables --------------------------------------------

    /**
     * Makes a copy of the highlights.  Does not actually clone each highlight,
     * but only makes references to them.
     *
     * @return the copy
     * @see Highlighter#getHighlights
     */
    @Override
    public HighlightInfo[] getHighlights() {
        int size = highlights.size();
        if (size == 0) {
            return noHighlights;
        }
        return highlights.toArray(new HighlightInfo[highlights.size()]);
    }

    /**
     * When leaf Views (such as LabelView) are rendering they should
     * call into this method. If a highlight is in the given region it will
     * be drawn immediately.
     *
     * @param g          Graphics used to draw
     * @param p0         starting offset of view
     * @param p1         ending offset of view
     * @param viewBounds Bounds of View
     * @param editor     JTextComponent
     * @param view       View instance being rendered
     */
    public void paintLayeredHighlights(Graphics g, int p0, int p1,
                                       Shape viewBounds,
                                       JTextComponent editor, View view) {
        for (int counter = highlights.size() - 1; counter >= 0; counter--) {
            HighlightInfo tag = highlights.get(counter);
            if (tag instanceof LayeredHighlightInfo) {
                LayeredHighlightInfo lhi = (LayeredHighlightInfo) tag;
                int start = lhi.getStartOffset();
                int end = lhi.getEndOffset();
                if ((p0 < start && p1 > start) ||
                        (p0 >= start && p0 < end)) {
                    lhi.paintLayeredHighlights(g, p0, p1, viewBounds,
                            editor, view);
                }
            }
        }
    }

    /**
     * Queues damageRange() call into event dispatch thread
     * to be sure that views are in consistent state.
     */
    private void safeDamageRange(final Position p0, final Position p1) {
        safeDamager.damageRange(p0, p1);
    }

    /**
     * Queues damageRange() call into event dispatch thread
     * to be sure that views are in consistent state.
     */
    private void safeDamageRange(int a0, int a1) throws BadLocationException {
        Document doc = component.getDocument();
        safeDamageRange(doc.createPosition(a0), doc.createPosition(a1));
    }

    public boolean getDrawsLayeredHighlights() {
        return drawsLayeredHighlights;
    }

    /**
     * If true, highlights are drawn as the Views draw the text. That is
     * the Views will call into <code>paintLayeredHighlight</code> which
     * will result in a rectangle being drawn before the text is drawn
     * (if the offsets are in a highlighted region that is). For this to
     * work the painter supplied must be an instance of
     * LayeredHighlightPainter.
     */
    public void setDrawsLayeredHighlights(boolean newValue) {
        drawsLayeredHighlights = newValue;
    }

    /**
     * Simple highlight painter that fills a highlighted area with
     * a solid color.
     */
    public static class DefaultHighlightPainter extends LayeredHighlighter.LayerPainter {

        private Color color;

        /**
         * Constructs a new highlight painter. If <code>c</code> is null,
         * the JTextComponent will be queried for its selection color.
         *
         * @param c the color for the highlight
         */
        public DefaultHighlightPainter(Color c) {
            color = c;
        }

        // --- HighlightPainter methods ---------------------------------------

        /**
         * Returns the color of the highlight.
         *
         * @return the color
         */
        public Color getColor() {
            return color;
        }

        // --- LayerPainter methods ----------------------------

        /**
         * Paints a highlight.
         *
         * @param g      the graphics context
         * @param offs0  the starting model offset &gt;= 0
         * @param offs1  the ending model offset &gt;= offs1
         * @param bounds the bounding box for the highlight
         * @param c      the editor
         */
        public void paint(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c) {
            Rectangle alloc = bounds.getBounds();
            try {
                // --- determine locations ---
                TextUI mapper = c.getUI();
                Rectangle p0 = mapper.modelToView(c, offs0);
                Rectangle p1 = mapper.modelToView(c, offs1);

                // --- render ---
                Color color = getColor();

                if (color == null) {
                    g.setColor(c.getSelectionColor());
                } else {
                    g.setColor(color);
                }
                if (p0.y == p1.y) {
                    // same line, render a rectangle
                    Rectangle r = p0.union(p1);
                    g.fillRect(r.x, r.y, r.width, r.height);
                } else {
                    // different lines
                    int p0ToMarginWidth = alloc.x + alloc.width - p0.x;
                    g.fillRect(p0.x, p0.y, p0ToMarginWidth, p0.height);
                    if ((p0.y + p0.height) != p1.y) {
                        g.fillRect(alloc.x, p0.y + p0.height, alloc.width,
                                p1.y - (p0.y + p0.height));
                    }
                    g.fillRect(alloc.x, p1.y, (p1.x - alloc.x), p1.height);
                }
            } catch (BadLocationException e) {
                // can't render
            }
        }

        /**
         * Paints a portion of a highlight.
         *
         * @param g      the graphics context
         * @param offs0  the starting model offset &gt;= 0
         * @param offs1  the ending model offset &gt;= offs1
         * @param bounds the bounding box of the view, which is not
         *               necessarily the region to paint.
         * @param c      the editor
         * @param view   View painting for
         * @return region drawing occurred in
         */
        public Shape paintLayer(Graphics g, int offs0, int offs1,
                                Shape bounds, JTextComponent c, View view) {
            Color color = getColor();

            if (color == null) {
                g.setColor(c.getSelectionColor());
            } else {
                g.setColor(color);
            }

            Rectangle r;

            if (offs0 == view.getStartOffset() &&
                    offs1 == view.getEndOffset()) {
                // Contained in view, can just use bounds.
                if (bounds instanceof Rectangle) {
                    r = (Rectangle) bounds;
                } else {
                    r = bounds.getBounds();
                }
            } else {
                // Should only render part of View.
                try {
                    // --- determine locations ---
                    Shape shape = view.modelToView(offs0, Position.Bias.Forward,
                            offs1, Position.Bias.Backward,
                            bounds);
                    r = (shape instanceof Rectangle) ?
                            (Rectangle) shape : shape.getBounds();
                } catch (BadLocationException e) {
                    // can't render
                    r = null;
                }
            }

            if (r != null) {
                // If we are asked to highlight, we should draw something even
                // if the model-to-view projection is of zero width (6340106).
                r.width = Math.max(r.width, 1);

                g.fillRect(r.x, r.y, r.width, r.height);
            }

            return r;
        }

    }


    /**
     * LayeredHighlightPainter is used when a drawsLayeredHighlights is
     * true. It maintains a rectangle of the region to paint.
     */
    class LayeredHighlightInfo extends HighlightInfo {

        int x;
        int y;
        int width;
        int height;

        void union(Shape bounds) {
            if (bounds == null)
                return;

            Rectangle alloc;
            if (bounds instanceof Rectangle) {
                alloc = (Rectangle) bounds;
            } else {
                alloc = bounds.getBounds();
            }
            if (width == 0 || height == 0) {
                x = alloc.x;
                y = alloc.y;
                width = alloc.width;
                height = alloc.height;
            } else {
                width = Math.max(x + width, alloc.x + alloc.width);
                height = Math.max(y + height, alloc.y + alloc.height);
                x = Math.min(x, alloc.x);
                width -= x;
                y = Math.min(y, alloc.y);
                height -= y;
            }
        }

        /**
         * Restricts the region based on the receivers offsets and messages
         * the painter to paint the region.
         */
        void paintLayeredHighlights(Graphics g, int p0, int p1,
                                    Shape viewBounds, JTextComponent editor,
                                    View view) {
            int start = getStartOffset();
            int end = getEndOffset();
            // Restrict the region to what we represent
            p0 = Math.max(start, p0);
            p1 = Math.min(end, p1);
            // Paint the appropriate region using the painter and union
            // the effected region with our bounds.
            union(((LayeredHighlighter.LayerPainter) painter).paintLayer
                    (g, p0, p1, viewBounds, editor, view));
        }
    }

    /**
     * This class invokes <code>mapper.damageRange</code> in
     * EventDispatchThread. The only one instance per Highlighter
     * is cretaed. When a number of ranges should be damaged
     * it collects them into queue and damages
     * them in consecutive order in <code>run</code>
     * call.
     */
    class SafeDamager implements Runnable {
        private Vector<Position> p0 = new Vector<Position>(10);
        private Vector<Position> p1 = new Vector<Position>(10);
        private Document lastDoc = null;

        /**
         * Executes range(s) damage and cleans range queue.
         */
        public synchronized void run() {
            if (component != null) {
                TextUI mapper = component.getUI();
                if (mapper != null && lastDoc == component.getDocument()) {
                    // the Document should be the same to properly
                    // display highlights
                    int len = p0.size();
                    for (int i = 0; i < len; i++) {
                        mapper.damageRange(component,
                                p0.get(i).getOffset(),
                                p1.get(i).getOffset());
                    }
                }
            }
            p0.clear();
            p1.clear();

            // release reference
            lastDoc = null;
        }

        /**
         * Adds the range to be damaged into the range queue. If the
         * range queue is empty (the first call or run() was already
         * invoked) then adds this class instance into EventDispatch
         * queue.
         * <p>
         * The method also tracks if the current document changed or
         * component is null. In this case it removes all ranges added
         * before from range queue.
         */
        public synchronized void damageRange(Position pos0, Position pos1) {
            if (component == null) {
                p0.clear();
                lastDoc = null;
                return;
            }

            boolean addToQueue = p0.isEmpty();
            Document curDoc = component.getDocument();
            if (curDoc != lastDoc) {
                if (!p0.isEmpty()) {
                    p0.clear();
                    p1.clear();
                }
                lastDoc = curDoc;
            }
            p0.add(pos0);
            p1.add(pos1);

            if (addToQueue) {
                SwingUtilities.invokeLater(this);
            }
        }
    }

    public class HighlightInfo implements Highlight {

        Position p0;
        Position p1;
        HighlightPainter painter;
        private int startOffset;
        private int endOffset;

        public HighlightInfo() {
        }

        public HighlightInfo(int p0, int p1, HighlightPainter painter) {
            try {
                this.p0 = component.getDocument().createPosition(p0);
                this.p1 = component.getDocument().createPosition(p1);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
            startOffset = p0;
            endOffset = p1;
            System.out.printf("p0: %d   :   p1: %d%n", p0, p1);
            this.painter = painter;
        }

        public int getIntStart() {
            return startOffset;
        }

        public int getIntEnd() {
            return endOffset;
        }

        public int getStartOffset() {
            return p0.getOffset();
        }

        public int getEndOffset() {
            return p1.getOffset();
        }

        public HighlightPainter getPainter() {
            return painter;
        }
    }
}