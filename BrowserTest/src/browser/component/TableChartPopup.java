/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.component;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.ToolTipManager;
import javax.swing.table.TableModel;

/**
 *
 * @author Dimas
 */
public class TableChartPopup extends JFrame{
    public TableChartPopup(TableModel tm) {
        super("Table Chart");
        setSize(300, 200);
        TableChart tc = new TableChart(tm);
        getContentPane().add(tc, BorderLayout.CENTER);

        // Use the next line to turn on tooltips:
        ToolTipManager.sharedInstance().registerComponent(tc);
    }  
}
