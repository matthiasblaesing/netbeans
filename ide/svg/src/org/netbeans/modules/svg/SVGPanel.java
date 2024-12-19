/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.netbeans.modules.svg;

import com.github.weisj.jsvg.SVGDocument;

import java.awt.*;
import javax.swing.*;

/**
 *
 * @author Christian Lenz
 */
public class SVGPanel extends JPanel {

    private SVGDocument svgDocument;
    private double scale = 1.0D;
    private BackgroundMode bgMode = BackgroundMode.DEFAULT;

    public void setSvgDocument(SVGDocument doc) {
        this.svgDocument = doc;

        repaint();
    }

    public void setScale(double scale) {
        this.scale = scale;

        repaint();
    }

    public void setBackgroundMode(BackgroundMode mode) {
        this.bgMode = mode;

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Rectangle visibleRect = getVisibleRectangle();

        switch (bgMode) {
            case BLACK:
                g.setColor(Color.BLACK);
                g.fillRect(visibleRect.x, visibleRect.y, visibleRect.width, visibleRect.height);
                break;

            case WHITE:
                g.setColor(Color.WHITE);
                g.fillRect(visibleRect.x, visibleRect.y, visibleRect.width, visibleRect.height);
                break;

            case TRANSPARENT:
            case DARK_TRANSPARENT:
                Utils.drawChestTilePattern(g, visibleRect, 20, bgMode == BackgroundMode.DARK_TRANSPARENT);
                break;

            default:
                g.setColor(getBackground());
                g.fillRect(visibleRect.x, visibleRect.y, visibleRect.width, visibleRect.height);
                break;
        }

        if (svgDocument == null) {
            return;
        }

        Graphics2D g2d = (Graphics2D) g.create();

        try {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2d.scale(scale, scale);

            svgDocument.render(this, g2d);
        } finally {
            g2d.dispose();
        }
    }

    /**
     * Gets the visible portion of the panel. If this panel is inside a
     * JViewport, it returns the visible part, otherwise, it returns the full
     * panel size.
     *
     * @return The visible rectangle for the panel.
     */
    private Rectangle getVisibleRectangle() {
        JViewport viewport = getViewport();
        if (viewport != null) {
            return viewport.getViewRect();
        }

        return new Rectangle(0, 0, getWidth(), getHeight());
    }

    /**
     * Finds the parent JViewport, if any, to get the visible part of this
     * panel.
     *
     * @return The JViewport that contains this panel, or null if none is found.
     */
    private JViewport getViewport() {
        Container parent = getParent();
        while (parent != null) {
            if (parent instanceof JViewport) {
                return (JViewport) parent;
            }

            parent = parent.getParent();
        }

        return null;
    }
}
