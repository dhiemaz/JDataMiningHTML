/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author Dimas
 */
public class TableChart extends JComponent implements TableModelListener{
    
      protected TableModel model;
      protected ChartPainter cp;
      protected double[] percentages; // pie slices
      protected String[] labels; // labels for slices
      protected String[] tips; // tooltips for slices
    
      protected java.text.NumberFormat formatter = java.text.NumberFormat.getPercentInstance();

      public TableChart(TableModel tm) {
        setUI(cp = new PieChartPainter());
        setModel(tm);
      }

      public void setTextFont(Font f) {
        cp.setTextFont(f);
      }

      public Font getTextFont() {
        return cp.getTextFont();
      }

      public void setTextColor(Color c) {
        cp.setTextColor(c);
      }

      public Color getTextColor() {
        return cp.getTextColor();
      }

      public void setColor(Color[] clist) {
        cp.setColor(clist);
      }

      public Color[] getColor() {
        return cp.getColor();
      }

      public void setColor(int index, Color c) {
        cp.setColor(index, c);
      }

      public Color getColor(int index) {
        return cp.getColor(index);
      }

      public String getToolTipText(MouseEvent me) {
        if (tips != null) {
          int whichTip = cp.indexOfEntryAt(me);
          if (whichTip != -1) {
            return tips[whichTip];
          }
        }
        return null;
      }

    @Override
    public void tableChanged(TableModelEvent e) {
        updateLocalValues(e.getType() != TableModelEvent.UPDATE);
    }
    
    public void setModel(TableModel tm) {
        // get listener code correct.
        if (tm != model) {
          if (model != null) {
            model.removeTableModelListener(this);
          }
          model = tm;
          model.addTableModelListener(this);
          updateLocalValues(true);
        }
     }
    
     public TableModel getModel() {
        return model;
     }
     
     protected void calculatePercentages() {
        double runningTotal = 0.0;
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
          percentages[i] = 0.0;
          for (int j = model.getColumnCount() - 1; j >= 0; j--) {

            // First try the cell as a Number object.
            Object val = model.getValueAt(i, j);
            if (val instanceof Number) {
              percentages[i] += ((Number) val).doubleValue();
            } else if (val instanceof String) {             
              try {
                percentages[i] += Double.valueOf(val.toString()).doubleValue();
              } catch (Exception e) {
                // not a numeric string...give up.
              }
            }
          }
          runningTotal += percentages[i];
        }
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
          percentages[i] /= runningTotal;
        }
    }
    
    protected void createLabelsAndTips() {
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
          labels[i] = (String) model.getValueAt(i, 0);
          tips[i] = formatter.format(percentages[i]);
        }
    }
    
    protected void updateLocalValues(boolean freshStart) {
        if (freshStart) {
          int count = model.getRowCount();
          if ((tips == null) || (count != tips.length)) {
            percentages = new double[count];
            labels = new String[count];
            tips = new String[count];
          }
        }
        calculatePercentages();
        createLabelsAndTips();
    
        cp.setValues(percentages);
        cp.setLabels(labels);
        // Finally, repaint the chart.
        repaint();
   }
}
