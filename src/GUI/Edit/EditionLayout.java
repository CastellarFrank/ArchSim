/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Edit;

import java.awt.Choice;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Scrollbar;
import javax.swing.JButton;
import javax.swing.JLabel;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class EditionLayout implements LayoutManager {

    @Override
    public void addLayoutComponent(String name, Component c) {
    }

    @Override
    public void removeLayoutComponent(Component c) {
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return new Dimension(500, 500);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        return new Dimension(100, 100);
    }

    @Override
    public void layoutContainer(Container target) {
        Insets insets = new Insets(15, 20, 20, 15);
        int targetw = target.getSize().width - insets.left - insets.right;
        int targeth = target.getSize().height - (insets.top + insets.bottom);
        int i;
        int h = insets.top;
        int pw = 300;
        int x = 0;
        for (i = 0; i < target.getComponentCount(); i++) {
            Component m = target.getComponent(i);
            boolean newline = true;
            if (m.isVisible()) {
                Dimension d = m.getPreferredSize();
                if (pw < d.width) {
                    pw = d.width;
                }
                if (m instanceof Scrollbar) {
                    h += 10;
                    d.width = targetw - x;
                }
                if (m instanceof Choice && d.width > targetw) {
                    d.width = targetw - x;
                }
                if (m instanceof JLabel) {
                    Dimension d2 =
                            target.getComponent(i + 1).getPreferredSize();
                    if (d.height < d2.height) {
                        d.height = d2.height;
                    }
                    h += d.height / 5;
                    newline = false;
                }
                if (m instanceof JButton) {
                    if (x == 0) {
                        h += 20;
                    }
                    if (i != target.getComponentCount() - 1) {
                        newline = false;
                    }
                }
                m.setLocation(insets.left + x, h);
                m.setSize(d.width, d.height);
                if (newline) {
                    h += d.height;
                    x = 0;
                } else {
                    x += d.width;
                }
            }
        }
        if (target.getSize().height < h) {
            target.setSize(pw + insets.right, h + insets.bottom);
        }
    }
}
