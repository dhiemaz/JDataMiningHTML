/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/**
 *
 * @author Dimas
 */
abstract class ChartPainter extends ComponentUI{
    protected Font textFont = new Font("Serif", Font.PLAIN, 12);

  protected Color textColor = Color.black;

  protected Color colors[] = new Color[] { Color.red, Color.blue,
      Color.yellow, Color.black, Color.green, Color.white, Color.gray,
      Color.cyan, Color.magenta, Color.darkGray };

  protected double values[] = new double[0];

  protected String labels[] = new String[0];

  public void setTextFont(Font f) {
    textFont = f;
  }

  public Font getTextFont() {
    return textFont;
  }

  public void setColor(Color[] clist) {
    colors = clist;
  }

  public Color[] getColor() {
    return colors;
  }

  public void setColor(int index, Color c) {
    colors[index] = c;
  }

  public Color getColor(int index) {
    return colors[index];
  }

  public void setTextColor(Color c) {
    textColor = c;
  }

  public Color getTextColor() {
    return textColor;
  }

  public void setLabels(String[] l) {
    labels = l;
  }

  public void setValues(double[] v) {
    values = v;
  }

  public abstract int indexOfEntryAt(MouseEvent me);
  public abstract void paint(Graphics g, JComponent c);    
}
