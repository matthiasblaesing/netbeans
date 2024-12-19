package org.netbeans.modules.svg.toolbar;

import org.netbeans.modules.svg.BackgroundMode;

import java.awt.*;
import javax.swing.*;
import org.netbeans.modules.svg.Utils;

/**
 *
 * @author Christian Lenz
 */
public class BackgroundIcon implements Icon {

    private final BackgroundMode bgMode;
    private final Color defaultColor;

    public BackgroundIcon(BackgroundMode bgMode, Color defaultColor) {
        this.bgMode = bgMode;
        this.defaultColor = defaultColor;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g;

        int width = getIconWidth();
        int height = getIconHeight();

        switch (bgMode) {
            case BLACK:
                g2d.setColor(Color.BLACK);
                g2d.fillRect(x, y, width, height);
                break;
            case WHITE:
                g2d.setColor(Color.WHITE);
                g2d.fillRect(x, y, width, height);
                break;
            case TRANSPARENT:
            case DARK_TRANSPARENT:
                Utils.drawSmallChestTilePattern(g2d, x, y, width, height, bgMode == BackgroundMode.DARK_TRANSPARENT);
                break;
            case DEFAULT:
                g2d.setColor(defaultColor);
                g2d.fillRect(x, y, width, height);
                break;
        }
    }

    @Override
    public int getIconWidth() {
        return 16;
    }

    @Override
    public int getIconHeight() {
        return 16;
    }
}
