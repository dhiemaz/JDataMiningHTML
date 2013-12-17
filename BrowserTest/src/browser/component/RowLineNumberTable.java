/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.SystemColor;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

/**
 *
 * @author Rizal Ahmad Jabbar
 */
class RowLineNumberTable extends JTable{
    private JTable mainTable;
    private TableColumn tableColumn=new TableColumn();
    public RowLineNumberTable(JTable table){
        super();
        mainTable=table;
        setAutoCreateColumnsFromModel( false );
        setModel( mainTable.getModel() );
	setSelectionModel( mainTable.getSelectionModel() );
        setAutoscrolls( false );
 
  	addColumn( tableColumn );
	getColumnModel().getColumn(0).setCellRenderer(mainTable.getTableHeader().getDefaultRenderer());
	getColumnModel().getColumn(0).setPreferredWidth(40);
	setPreferredScrollableViewportSize(getPreferredSize());
	setRowHeight(mainTable.getRowHeight());
	setSelectionBackground(mainTable.getBackground());

    }
    @Override
    public boolean isCellEditable(int row, int column){
	return false;
    }
 
    @Override
    public Object getValueAt(int row, int column){		
	return new Integer(row + 1);
    }
    
    @Override
    public Color getBackground(){
	return SystemColor.controlHighlight;
    }
	
    public void update(Graphics g)   {
	paint(g);   
    }
}

