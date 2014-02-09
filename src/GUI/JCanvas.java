/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.awt.Dimension;
import javax.swing.JComponent;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class JCanvas extends JComponent {

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(300, 400);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }
}
