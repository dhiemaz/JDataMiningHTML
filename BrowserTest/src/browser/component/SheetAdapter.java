/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.component;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.StringTokenizer;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

/**
 *
 * @author Rizal Ahmad Jabbar
 */
class SheetAdapter implements ActionListener{
    private String rowstring,value;
    private Clipboard system;
    private StringSelection stsel;
    private JTable table;
    public SheetAdapter(JTable myJTable)
   {
       table = myJTable;
       KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.CTRL_MASK,false);
       KeyStroke paste = KeyStroke.getKeyStroke(KeyEvent.VK_V,ActionEvent.CTRL_MASK,false);
       KeyStroke cut = KeyStroke.getKeyStroke(KeyEvent.VK_X,ActionEvent.CTRL_MASK,false);
       table.registerKeyboardAction(this,"Copy",copy,JComponent.WHEN_FOCUSED);
       table.registerKeyboardAction(this,"Paste",paste,JComponent.WHEN_FOCUSED);
       table.registerKeyboardAction(this,"Cut",cut,JComponent.WHEN_FOCUSED);
       system = Toolkit.getDefaultToolkit().getSystemClipboard();
   }
 public JTable getJTable() {
     return table;
 }
 
 public void setJTable(JTable table) {
     this.table=table;
 }
 
public void copyCell()
{
	 StringBuffer sbf=new StringBuffer();
         int numcols=table.getSelectedColumnCount();
         int numrows=table.getSelectedRowCount();
         int[] rowsselected=table.getSelectedRows();
         int[] colsselected=table.getSelectedColumns();
         if (!((numrows-1==rowsselected[rowsselected.length-1]-rowsselected[0] &&
          numrows==rowsselected.length) && (numcols-1==colsselected[colsselected.length-1]-colsselected[0] &&
          numcols==colsselected.length)))
         {
            JOptionPane.showMessageDialog(null, "Invalid Copy Selection","Invalid Copy Selection",JOptionPane.ERROR_MESSAGE);
            return;
         }
         for (int i=0;i<numrows;i++)
         {
            for (int j=0;j<numcols;j++)
            {
                sbf.append(table.getValueAt(rowsselected[i],colsselected[j]));
                if (j<numcols-1) sbf.append("\t");
            }
            sbf.append("\n");
         }
         stsel  = new StringSelection(sbf.toString());
         system = Toolkit.getDefaultToolkit().getSystemClipboard();
         system.setContents(stsel,stsel);
	}

public void pasteCell()
{
          int startRow=(table.getSelectedRows())[0];
          int startCol=(table.getSelectedColumns())[0];
          try
          {
             String trstring= (String)(system.getContents(this).getTransferData(DataFlavor.stringFlavor));
             StringTokenizer st1=new StringTokenizer(trstring,"\n");
             for(int i=0;st1.hasMoreTokens();i++)
             {
                rowstring=st1.nextToken();
                StringTokenizer st2=new StringTokenizer(rowstring,"\t");
                for(int j=0;st2.hasMoreTokens();j++)
                {
                   value=(String)st2.nextToken();
                    if (startRow+i< table.getRowCount()  &&
                       startCol+j< table.getColumnCount())                 
                       table.setValueAt(value,startRow+i,startCol+j);
                }
            }
         }
         catch(Exception ex){ex.printStackTrace();}
	
	}

public void cutCell()
{
         int numcols=table.getSelectedColumnCount();
         int numrows=table.getSelectedRowCount();
         int[] rowsselected=table.getSelectedRows();
         int[] colsselected=table.getSelectedColumns();

           try
          {
             for (int i=0;i<numrows;i++)
            {
            for (int j=0;j<numcols;j++)
            {
                  table.setValueAt("",rowsselected[i],colsselected[j]);
             }
            
            }
         }
         catch(Exception ex){ex.printStackTrace();}
	}
public void actionPerformed(ActionEvent e)
   {
      if (e.getActionCommand().compareTo("Copy")==0)
      {
         copyCell();
      }
      if (e.getActionCommand().compareTo("Paste")==0)
      {
          pasteCell();
      }
      if (e.getActionCommand().compareTo("Cut")==0)
      {
          cutCell();
      }
   }
}
