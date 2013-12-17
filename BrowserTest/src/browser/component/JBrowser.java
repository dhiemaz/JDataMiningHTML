package browser.component;

import com.html.util.parser.ParserHTML;
import com.nwoods.jgo.JGoBrush;
import com.nwoods.jgo.JGoDocument;
import com.nwoods.jgo.JGoDocumentEvent;
import com.nwoods.jgo.JGoDocumentListener;
import com.nwoods.jgo.JGoLink;
import com.nwoods.jgo.JGoObject;
import com.nwoods.jgo.JGoPen;
import com.nwoods.jgo.JGoUndoManager;
import com.nwoods.jgo.JGoView;
import com.nwoods.jgo.JGoViewEvent;
import com.nwoods.jgo.JGoViewListener;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import org.jsoup.Jsoup;

import org.mozilla.interfaces.nsIRequest;
import org.mozilla.interfaces.nsIWebProgress;

import ru.atomation.jbrowser.impl.JBrowserBuilder;
import ru.atomation.jbrowser.impl.JComponentFactory;
import ru.atomation.jbrowser.interfaces.BrowserManager;
import ru.atomation.jbrowser.impl.JBrowserCanvas;
import ru.atomation.jbrowser.impl.JBrowserComponent;
import ru.atomation.jbrowser.interfaces.BrowserAdapter;


// Catatan : Tambahkan filter length < 30 untuk header
public class JBrowser extends JFrame implements Runnable, JGoViewListener, JGoDocumentListener{

    BrowserManager browserManager;
    final JBrowserComponent<?> browser;
    private Object column;
    private final JComponentFactory<Canvas> canvasFactory;
    Object header[];
    Object data[][];
    Object col_name[];
    private String array_row[][];
    private JGoView myView;
    private int myNodeCounter = 0;
    private int TNcounter = 23;
    private int Flag_select = 0;
    private int number_header = 0;
    String temp[][] = new String[500][500];
    String arrayColHeader[] = new String[500];
    protected JLabel label;
    protected JTextField textfield;
    protected JList list;
    protected JScrollPane scrollPane;
    
    public JBrowser() {
        initComponents();
        
        JBrowserBuilder builder = new JBrowserBuilder();
        builder.setProfilePath(new File("C:\\profile"));
        builder.setXulRunnerPath(new File("C:\\"));
         
        browserManager = builder.buildBrowserManager();
        canvasFactory = browserManager.getComponentFactory(JBrowserCanvas.class);
        browser = canvasFactory.createBrowser();        
        browser.addBrowserListener(new BrowserAdapter() {
            @Override
            public void onSetUrlbarText(String url) {
                jTextField1.setText(url);
            }

            @Override
            public void onSetStatus(String text) {
                jLabel2.setText(text);
            }

            @Override
            public void onProgressChange(nsIWebProgress aWebProgress, nsIRequest aRequest, long aCurSelfProgress, long aMaxSelfProgress, long aCurTotalProgress, long aMaxTotalProgress) {
                jProgressBar1.setMaximum((int) aMaxTotalProgress);
                jProgressBar1.setValue((int) aCurTotalProgress);
            }
        });
                
        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jButton1.setEnabled(browser.back());
            }
        });
        jButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jButton2.setEnabled(browser.forward());
            }
        });
        jButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jButton3.setEnabled(browser.refresh());
            }
        });
        jButton4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jButton4.setEnabled(browser.stop());
            }
        });

        jButton5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jButton5.setEnabled(browser.setUrl(jTextField1.getText()));
            }
        });

        jButton6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {                
            }
        });

        jButton7.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Flag_select = 1;
                //browser.setUrl("javascript:(function(){var s=document.createElement('div');s.innerHTML='Loading...';s.style.color='black';s.style.padding='20px';s.style.position='fixed';s.style.zIndex='9999';s.style.fontSize='3.0em';s.style.border='2px solid black';s.style.right='40px';s.style.top='40px';s.setAttribute('class','selector_gadget_loading');s.style.background='white';document.body.appendChild(s);s=document.createElement('script');s.setAttribute('type','text/javascript');s.setAttribute('src','http://localhost/selectorgadget/lib/selectorgadget.js?raw=true');document.body.appendChild(s);})();");
                browser.setUrl("javascript:(function(){var s=document.createElement('div');s.innerHTML='Loading...';s.style.color='black';s.style.padding='20px';s.style.position='fixed';s.style.zIndex='9999';s.style.fontSize='3.0em';s.style.border='2px solid black';s.style.right='40px';s.style.top='40px';s.setAttribute('class','selector_gadget_loading');s.style.background='white';document.body.appendChild(s);s=document.createElement('script');s.setAttribute('type','text/javascript');s.setAttribute('src','http://localhost:8080/selectorgadget/lib/selectorgadget.js?raw=true');document.body.appendChild(s);})();");                               
            }
        });

        jButton8.addActionListener(new ActionListener() {                        
            @Override
            public void actionPerformed(ActionEvent e) { 
                if(Flag_select == 1){
                    ParserHTML html = new ParserHTML();
                    BufferedWriter writer = null;
                    try {                                                            
                        org.w3c.dom.Document doc = browser.getDocument();                                         
                        writer = new BufferedWriter(new FileWriter("C:\\browser_test\\temp.html"));
                        org.jsoup.nodes.Document jsoup_document = Jsoup.parse(html.parseHtml(doc));
                        writer.write(jsoup_document.outerHtml());                    
                    }catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error : " + ex.getMessage());
                    }finally{
                        try{
                            if ( writer != null) writer.close( );
                        }catch (IOException ioe){
                        //Skip
                        }
                    }                
                    
                    ExtractHTML("C:\\browser_test\\temp.html");
                }else{
                    JOptionPane.showMessageDialog(null, "Silahkan klik tombol select untuk memilih tabel yang akan diekstrak..");            
                }                
            }
        });
                
        final DefaultTableCellRenderer header = ((DefaultTableCellRenderer) jTable2.getTableHeader().getDefaultRenderer());
        header.setHorizontalAlignment(SwingConstants.CENTER);
        final DefaultTableCellRenderer header1 = ((DefaultTableCellRenderer) jTable1.getTableHeader().getDefaultRenderer());
        header1.setHorizontalAlignment(SwingConstants.CENTER);
        jTable2.setRowHeight(20);
        RowLineNumberTable lineTable = new RowLineNumberTable(jTable2);
        //SheetAdapter sheetCell = new SheetAdapter(jTable2);

        lineTable.setDragEnabled(false);
        jScrollPane1.setRowHeaderView(lineTable);
        jPanel2.add(jScrollPane1);
        jTabbedPane1.add("Loading Page", browser.getComponent());
        jTabbedPane1.add("Result", jPanel1);
        
        jTable2.addKeyListener(new KeyListener() {
            int rowcell;
            int colcell;

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                ambilCell();
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }

            public void keyDown(KeyEvent e) {
            }

            public void keyRight(KeyEvent e) {
            }

            public void keyLeft(KeyEvent e) {
            }

            public void keyUp(KeyEvent e) {
            }

            public void ambilCell() {                
                rowcell = jTable2.getSelectedRow() + 1;
                colcell = jTable2.getSelectedColumn() + 1;                              
                jLabel3.setText("Row : " + rowcell + "   | Column : " + colcell);
            }
        });

        jTable2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int rowcell;
                int colcell;
                if (e.getClickCount() == 1) {
                    rowcell = jTable2.getSelectedRow() + 1;
                    colcell = jTable2.getSelectedColumn() + 1;
                    jLabel3.setText("Row : " + rowcell + "    | Column : " + colcell);
                }
            }
        });
        TableColumn tc = jTable1.getColumnModel().getColumn(0);
        tc.setCellEditor(jTable1.getDefaultEditor(Boolean.class));
        tc.setCellRenderer(jTable1.getDefaultRenderer(Boolean.class));
        tc.setHeaderRenderer(new CheckBoxHeader(new MyItemListener()));
        tc.setPreferredWidth(40);
    }

    @Override
    public void run() {
        myView.initializeDragDropHandling();
    }

    @Override
    public void viewChanged(JGoViewEvent evt) {
        if (evt.getHint() == JGoViewEvent.INSERTED) {
            if (evt.getJGoObject() instanceof JGoLink) {
                JGoLink link = (JGoLink)evt.getJGoObject();
                link.setPen(JGoPen.make(JGoPen.SOLID, 2, JGoBrush.ColorGray));
                link.setBrush(JGoBrush.gray);
                link.setArrowHeads(false, true);
            }
        }
    }

    @Override
    public void documentChanged(JGoDocumentEvent evt) {
        if (evt.getHint() == JGoDocumentEvent.INSERTED && (evt.getJGoObject() instanceof JGoLink)) {
            JGoLink link = (JGoLink)evt.getJGoObject();
            // pen controls the line and the outline of the arrowhead
            link.setPen(JGoPen.make(JGoPen.SOLID, 2, link.getBrush().getColor()));  
            // have an arrowhead at the "to" end
            link.setArrowHeads(false, true);  
        }
    }

    class MyItemListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            Object source = e.getSource();
            if (source instanceof AbstractButton == false) {
                return;
            }
            boolean checked = e.getStateChange() == ItemEvent.SELECTED;
            for (int x = 0, y = jTable1.getRowCount(); x < y; x++) {
                jTable1.setValueAt(new Boolean(checked), x, 0);                
            }
        }
    }
                                                  
    public static void deleteAllRows(final DefaultTableModel model) {
        for( int i = model.getRowCount() - 1; i >= 0; i-- ) {
            model.removeRow(i);
        }
    }
    
    private void ExtractHTML(String file){        
        int FLAG = 0, number_row = 0, number_col = 0;
        int header_col = 0;
        //String temp[][] = new String[500][500];
        StringWriter temp_content = new StringWriter();
        BufferedWriter writer = null;
        String lineToRemove = "<tr></tr>";        
        try {         
            
            // Clean the Files //
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {                
                String trimmedLine = line.trim();                
                if(trimmedLine.equals(lineToRemove)){                    
                    continue;
                }else{
                    temp_content.append(line);                    
                }                               
            }
            br.close();
            
            // Update the File //
            org.jsoup.nodes.Document dok = Jsoup.parse(temp_content.toString());            
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(dok.outerHtml());
            writer.close();
            
            org.jsoup.nodes.Document doc = Jsoup.parse(new File(file), "UTF-8");
            org.jsoup.select.Elements tableElements = doc.select("table");
            org.jsoup.select.Elements tableHeaderEles = tableElements.select("thead tr");                        
            if(!tableHeaderEles.isEmpty()){
                FLAG = 1;
                for (int i = 0; i < tableHeaderEles.size(); i++) {
                    number_row = i;
                    org.jsoup.nodes.Element row = tableHeaderEles.get(i);                
                    org.jsoup.select.Elements rowItems = row.select("th");
                    for (int j = 0; j < rowItems.size(); j++) {                         
                        if(tableHeaderEles.get(i).text() == null && tableHeaderEles.get(i).text().equals("")){
                        } else {                            
                            temp[i][j] = rowItems.get(j).text();                            
                            jTable1.getModel().setValueAt(rowItems.get(j).text(), header_col, 1);
                        }
                        header_col++;
                    }          
                }
            }else{
                FLAG = 1;                
                tableHeaderEles = tableElements.select("tbody tr");
                if(!tableHeaderEles.isEmpty()){                    
                    for (int i = 0; i < tableHeaderEles.size(); i++) { 
                        number_row = i;
                        org.jsoup.nodes.Element row = tableHeaderEles.get(i);                
                        org.jsoup.select.Elements rowItems = row.select("th");                        
                        for (int j = 0; j < rowItems.size(); j++) {                                                            
                            if(tableHeaderEles.get(i).text() == null && tableHeaderEles.get(i).text().equals("")){
                            } else {                                   
                                temp[i][j] = rowItems.get(j).text();                            
                                jTable1.getModel().setValueAt(rowItems.get(j).text(), header_col, 1);
                                arrayColHeader[j] = rowItems.get(j).text();
                            }
                            header_col++;
                        }                       
                    }                    
                }else{
                    FLAG = 0;                    
                }                
            }
            
            int z = 0;
            org.jsoup.select.Elements tableRowElements = tableElements.select(":Not(thead) tr");                        
            for (int i = 0; i < tableRowElements.size(); i++) {
                number_row = i + 1;
                org.jsoup.nodes.Element row = tableRowElements.get(i);                
                org.jsoup.select.Elements colItems = row.select("td");                
                for (int j = 0; j < colItems.size(); j++) { 
                    //number_col = colItems.size();                    
                    if(number_col < colItems.size()){
                       number_col = colItems.size();                     
                    }                    
                    if(FLAG == 0){
                        if(colItems.get(j).text() != null || colItems.get(j).text() != ""){
                            temp[i][j] = colItems.get(j).text();                            
                        }                      
                    }else{                        
                        if(colItems.get(j).text() != null || colItems.get(j).text() != ""){     
                            temp[i][j] = colItems.get(j).text();                            
                        }
                    }
                }                
            }
            
            JTable table = new JTable();
            DefaultTableModel dtm = (DefaultTableModel)jTable2.getModel();
            
            Object rows[] = new Object[number_row];
            // Add Column  // 
            number_header = number_col;
            col_name = new Object[number_col];
            for(int col = 0; col < number_col; col++){
                // Set Column Header //
                char col_header = (char) ('A' + col);                
                col_name[col] = col_header;
                //System.out.println("create column = "+ col_header);
                dtm.addColumn(col_header);                                 
            }
            
            // Insert data into table //
            for(int row = 0; row < number_row; row++){
                for(int col = 0; col < number_col; col++){
                   rows[col] = temp[row][col];                                        
                } 
                if(rows.length > 1){
                    dtm.addRow(rows);
                }                
            }                                    
            table.setModel(dtm);            
            JOptionPane.showMessageDialog(null, "Proses ekstraksi selesai!");            
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "error : " + e.getMessage());            
        }
   } 
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        jButton6 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButton13 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        jComboBox1 = new javax.swing.JComboBox();
        jComboBox2 = new javax.swing.JComboBox();
        jComboBox3 = new javax.swing.JComboBox();
        jComboBox4 = new javax.swing.JComboBox();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jToolBar2 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jButton21 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jButton24 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTextField2 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jButton25 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel2 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        exit = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jToolBar1.setRollover(true);
        jToolBar1.setPreferredSize(new java.awt.Dimension(25, 30));

        jButton6.setText("Klik");
        jButton6.setFocusable(false);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton6);
        jToolBar1.add(jSeparator1);

        jButton13.setText("Print");
        jButton13.setFocusable(false);
        jButton13.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton13.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton13);

        jButton9.setText("Save");
        jButton9.setFocusable(false);
        jButton9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton9.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton9);
        jToolBar1.add(jSeparator3);

        jButton10.setText("Copy");
        jButton10.setFocusable(false);
        jButton10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton10.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton10);

        jButton11.setText("Paste");
        jButton11.setFocusable(false);
        jButton11.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton11.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton11);

        jButton12.setText("Cut");
        jButton12.setFocusable(false);
        jButton12.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton12.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton12);

        jButton20.setText("Clear");
        jButton20.setFocusable(false);
        jButton20.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton20.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton20);
        jToolBar1.add(jSeparator2);

        jButton14.setText("Bold");
        jButton14.setFocusable(false);
        jButton14.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton14.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton14);

        jButton15.setText("Italic");
        jButton15.setFocusable(false);
        jButton15.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton15.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton15);

        jButton16.setText("Underline");
        jButton16.setFocusable(false);
        jButton16.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton16.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton16);
        jToolBar1.add(jSeparator4);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.setName(""); // NOI18N
        jToolBar1.add(jComboBox1);

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jToolBar1.add(jComboBox2);

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jToolBar1.add(jComboBox3);

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jToolBar1.add(jComboBox4);
        jToolBar1.add(jSeparator5);

        jButton17.setText("Left");
        jButton17.setFocusable(false);
        jButton17.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton17.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton17);

        jButton18.setText("Center");
        jButton18.setFocusable(false);
        jButton18.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton18.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton18);

        jButton19.setText("right");
        jButton19.setFocusable(false);
        jButton19.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton19.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton19);

        jToolBar2.setRollover(true);
        jToolBar2.setPreferredSize(new java.awt.Dimension(753, 30));

        jButton1.setText("back");
        jButton1.setToolTipText("Go back one page");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(jButton1);

        jButton2.setText("next");
        jButton2.setToolTipText("Go forward one page");
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(jButton2);

        jButton3.setText("refresh");
        jButton3.setToolTipText("Reload this page");
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(jButton3);

        jButton4.setText("stop");
        jButton4.setToolTipText("Stop loading page");
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(jButton4);

        jLabel1.setText("   Address     ");
        jToolBar2.add(jLabel1);

        jTextField1.setToolTipText("");
        jTextField1.setPreferredSize(new java.awt.Dimension(480, 28));
        jToolBar2.add(jTextField1);

        jButton5.setText("load");
        jButton5.setToolTipText("Load this page");
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton5);

        jButton7.setText("select");
        jButton7.setFocusable(false);
        jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton7);

        jButton8.setText("extract");
        jButton8.setFocusable(false);
        jButton8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton8.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(jButton8);

        jTabbedPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jTabbedPane1.setName(""); // NOI18N
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(650, 650));

        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(jTable2);

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel4.setLayout(new java.awt.BorderLayout());

        jLabel3.setText("Row:       | Column:     ");
        jPanel4.add(jLabel3, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel4, java.awt.BorderLayout.PAGE_START);

        jPanel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel4.setText("Custom Field");

        jButton21.setText("Up");

        jButton22.setText("Down");

        jButton23.setText("Delete");

        jButton24.setText("AddField");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "", "FieldName", "Type", "Size of Value"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane2.setViewportView(jTable1);

        jLabel5.setText("FieldName   : ");

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("You can change selected field name above!");

        jButton25.setText("Submit");
        jButton25.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tes(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jButton21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton24))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(109, 109, 109)
                        .addComponent(jButton25)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton21)
                    .addComponent(jButton22)
                    .addComponent(jButton23)
                    .addComponent(jButton24))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton25)
                .addContainerGap(23, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 512, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 543, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Result", jPanel1);

        jPanel5.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jProgressBar1.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 717, Short.MAX_VALUE)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 488, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 375, Short.MAX_VALUE)))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE))
        );

        jMenu1.setText("File");

        jMenuItem1.setText("Open");
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Save");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem4.setText("Visualisasi");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        exit.setText("Exit");
        exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitActionPerformed(evt);
            }
        });
        jMenu1.add(exit);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");

        jMenuItem3.setText("Undo");
        jMenu2.add(jMenuItem3);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("View");
        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 869, Short.MAX_VALUE)
            .addComponent(jToolBar2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 869, Short.MAX_VALUE)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 869, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 575, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("Integrasi");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    
    private void tes(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tes
        int header_count = 0;
        try{
            for(int x = 0; x < jTable1.getRowCount() ; x++){
                if(jTable1.getValueAt(x, 1) != null){
                    header_count++;
                }
            }

            // Validate if data in Table header is < 1 //
            if(header_count < 1){
                JOptionPane.showMessageDialog(null, "Data header kosong, silahkan isi terlebih dahulu data header.");
            }else{
                String str = JOptionPane.showInputDialog(null, "Masukan topik tabel : ","Notifikasi", 1);
                if(str != null){                                        
                    String[] head = new String[number_header];
                    Object[][] data = new Object[jTable2.getRowCount()][number_header];
                    
                    final ConnectSQLite connect = new ConnectSQLite();
                    int count = connect.getInfoCount();
                    if(count < 1){
                        
                        // Get Header //
                        for(int x = 0; x < number_header; x++){
                        //for(int x = 0; x < 1 ; x++){
                            if(jTable1.getValueAt(x, 1) != null){
                                head[x] = jTable1.getValueAt(x, 1).toString();
                            }
                            
                            // Get Data //
                            for(int y = 0; y < jTable2.getRowCount(); y++){                                
                                data[y][x] = jTable2.getValueAt(y, x);
                            }   
                        }                                                
                        connect.InsertData(str, head, data);                        
                    }else{
                        
                        // Get Topik From database //
                        final JFrame frame = new JFrame("Topik yang akan diintegrasikan");                                               
                        String value = new String();
                        JPanel contentPane = new JPanel();
                        contentPane.setLayout(new BorderLayout());
                        contentPane.setBorder(new EmptyBorder(5, 5, 35, 5));
                        DefaultListModel model = new DefaultListModel();

                        final JList list = new JList(new AbstractListModel() {
                            //String[] topik_sample = {"Sample Topik 1", "Sample Topik 2"};
                            String[] topic_array = connect.getTopic().toArray(new String[0]);
                            
                            @Override
                            public int getSize() {
                                return topic_array.length;
                            }

                            @Override
                            public Object getElementAt(int index) {                            
                                return topic_array[index];
                            }
                        });   

                        JScrollPane pane = new JScrollPane(list);
                        JButton btn_ok = new JButton("Pilih Topik");
                        btn_ok.setBounds(5, 145, 110, 20);                    
                        btn_ok.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {                                        
                                String value = list.getSelectedValue().toString();
                                frame.dispose();
                                show_integrasi(value);
                            }              
                        });

                        contentPane.add(pane,BorderLayout.NORTH);
                        contentPane.add(btn_ok,BorderLayout.WEST);
                        
                        frame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                        frame.setContentPane(contentPane);
                        frame.setSize(350, 240);
                        frame.setLocationRelativeTo(null);
                        frame.setVisible(true);
                    }                                                                                                                                                                
                }else{
                    JOptionPane.showMessageDialog(null, "Proses integrasi dibatalkan", "Notifikasi", 1);
                }                                                                      
            }
        }catch(Exception e){            
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan : " + e.toString());            
            System.out.println("Terjadi kesalahan : ");
            e.printStackTrace();
        }
    }//GEN-LAST:event_tes

    public void show_integrasi(String topik){
        try{
            JFrame mainFrame = new JFrame();
            mainFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);  
            
            mainFrame.setTitle("Integrasi");
            mainFrame.setSize(700, 400);
            myView = new JGoView();

            Container contentPane = mainFrame.getContentPane();
            contentPane.setLayout(new BorderLayout());
                        
            JButton btn_integrasi = new JButton("INTEGRASI");                               
            btn_integrasi.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    ShowVisualisasi(null, null);
                }              
            });
            
            contentPane.add(myView, BorderLayout.CENTER);
            contentPane.add(btn_integrasi, BorderLayout.SOUTH);
            
            contentPane.validate();
            mainFrame.setVisible(true);

            myView.setPrimarySelectionColor(JGoBrush.ColorMagenta);
            myView.setIncludingNegativeCoords(true);
            myView.setHidingDisabledScrollbars(true);
            myView.addKeyListener(new KeyAdapter() {
              public void keyPressed(KeyEvent evt) {
                handleKeyPressed(evt);
              }
            });
            myView.addViewListener(this);

            // initialize the document
            JGoDocument doc = myView.getDocument();
            // detect whenever a link is added to the document so we can modify its appearance
            doc.addDocumentListener(this);

            GoTreeNode treenode = new GoTreeNode();

            for(int x = 0; x < jTable1.getRowCount() ; x++){
                if(jTable1.getValueAt(x, 1) != null){
                    DefaultMutableTreeNode root = new DefaultMutableTreeNode(jTable1.getValueAt(x, 1));
                    DefaultTreeModel model = new DefaultTreeModel(root);

                    for(int y = 0; y < jTable2.getRowCount(); y++){
                        root.add(new DefaultMutableTreeNode(jTable2.getValueAt(y, x)));
                    }
                    
                    treenode.initialize(model, true, true, myView);
                    treenode.setBoundingRect(new Point(50, 100), new Dimension(300, 350));
                    doc.addObjectAtTail(treenode);                       
                }
            }
                        
            ConnectSQLite connect = new ConnectSQLite();
            ArrayList<String> dh = new ArrayList<String>();
            ArrayList<String> header = connect.getHeaderFromTopic(topik);
            ArrayList<String> id_header = connect.getIdHeaderFromTopic(topik);
            for(int x = 0; x < header.size(); x++){
                dh = connect.getDataFromHeader(id_header.get(x));
            }
                        
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(topik);
            DefaultTreeModel model = new DefaultTreeModel(root);
            
            for(int y = 0; y < dh.size(); y++){
                root.add(new DefaultMutableTreeNode(dh.get(y)));
            }
            
            treenode = new GoTreeNode();
            treenode.initialize(model, true, true, myView);
            treenode.setBoundingRect(new Point(550, 100), new Dimension(300, 350));
            doc.addObjectAtTail(treenode);                        
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan : " + e.toString());
            System.out.println("Terjadi kesalahan :");
            e.printStackTrace();
        }
    }
    
    public void ShowVisualisasi(final String rowData[][], final String columnNames[]){        
        //final JTable table = new JTable(rowData, columnNames);
        final JFrame frame = new JFrame("Visualisasi");
        /*
        final String data[][] = { { "Ron", "0.00", "68.68", "77.34", "78.02" },
                              { "Ravi", "0.00", "70.89", "64.17", "75.00" },
                              { "Maria", "76.52", "71.12", "75.68", "74.14" },
                              { "James", "70.00", "15.72", "26.40", "38.32" },
                              { "Ellen", "80.32", "78.16", "83.80", "85.72" } };

        final String headers[] = { "", "Q1", "Q2", "Q3", "Q4" };
        */
        TableModel tm_visualisasi = new AbstractTableModel() {
                                                
            @Override
            public int getRowCount() {
                return rowData.length;
                //return data.length;
            }

            @Override
            public int getColumnCount() {
                return columnNames.length;
                //return headers.length;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {                
                return rowData[rowIndex][columnIndex];
            }
            
            @Override
            public Class getColumnClass(int col) {
                return (col == 0) ? String.class : Number.class;
            }
        };
        
        JTable jt_visualisasi = new JTable(tm_visualisasi);
        JScrollPane jsp = new JScrollPane(jt_visualisasi);
        frame.getContentPane().add(jsp, BorderLayout.CENTER);
        final TableChartPopup tcp = new TableChartPopup(tm_visualisasi);
                                                                                     
        JButton btn_visualisasi = new JButton("Visualisasi");                               
        btn_visualisasi.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                tcp.setVisible(true);
            }              
        });
        
        frame.getContentPane().add(btn_visualisasi, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(450, 340);
        frame.setVisible(true);
    }
    
    public void writeCSVfile(JTable table) throws IOException, ClassNotFoundException, SQLException{
        Writer writer = null;
        DefaultTableModel dtm = (DefaultTableModel) table.getModel();
        int nRow = dtm.getRowCount();
        int nCol = dtm.getColumnCount();
        try {
            
            JFileChooser c = new JFileChooser();                      
            if (c.showSaveDialog(JBrowser.this) == JFileChooser.APPROVE_OPTION) {
                File file = c.getSelectedFile();
                
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
                                
                //write data information
                for (int i = 0 ; i < nRow ; i++){
                     StringBuffer buffer = new StringBuffer();
                    for (int j = 0 ; j < nCol ; j++){
                        buffer.append(dtm.getValueAt(i,j));
                        if (j!=nCol) buffer.append(", ");
                    }
                    writer.write(buffer.toString() + "\r\n");                
                } 
                writer.close();
                JOptionPane.showMessageDialog(null, "Data berhasil disimpan di : "+file);                
            }
            
            if (c.showSaveDialog(JBrowser.this) == JFileChooser.CANCEL_OPTION) {                                
            }                                                                        
                               
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan : "+e.toString());
            System.out.println("Terjadi Kesalahan : ");
            e.printStackTrace();
        }
    }

    
    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        String[] head = new String[number_header];
        String[][] data = new String[jTable2.getRowCount()][number_header];
        
        for(int x = 0; x < number_header; x++){
        //for(int x = 0; x < 1 ; x++){
            if(jTable1.getValueAt(x, 1) != null){
                head[x] = jTable1.getValueAt(x, 1).toString();
            }

            // Get Data //
            for(int y = 0; y < jTable2.getRowCount(); y++){                                
                data[y][x] = jTable2.getValueAt(y, x).toString();
            }   
        }   
        ShowVisualisasi(data, head);        
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitActionPerformed
        System.exit(1);
    }//GEN-LAST:event_exitActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // Simpan //
        try{
            if(jTable2.getRowCount() < 1){
                JOptionPane.showMessageDialog(null, "Data Table Kosong, Silahkan lakukan proses ekstraksi terlebih dahulu!");
            }else{
                writeCSVfile(jTable2);
            }            
        }catch(Exception e){
            System.out.println("Terjadi Kesalahan : ");
            e.printStackTrace();
        }        
    }//GEN-LAST:event_jMenuItem2ActionPerformed
    
    void modifyTree(int modifiers) {
        JGoObject primsel = myView.getSelection().getPrimarySelection();
        if (primsel == null) return;
        if (!(primsel instanceof GoTreeNode)) return;

        GoTreeNode gonode = (GoTreeNode)primsel;
        JTree tree = gonode.getTree().getJTree(myView);
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        Object selobj = tree.getLastSelectedPathComponent();
        if (selobj != null && selobj instanceof MutableTreeNode) {
            MutableTreeNode tn = (MutableTreeNode)selobj;
            if ((modifiers & InputEvent.SHIFT_MASK) != 0 &&
                selobj != model.getRoot()) {                
                int[] indices = new int[1];
                Object[] objects = new Object[1];
                TreeNode parent = tn.getParent();
                indices[0] = parent.getIndex(tn);
                objects[0] = tn;
                tn.removeFromParent();
                model.nodesWereRemoved(parent, indices, objects);
            } else {
                int numchildren = tn.getChildCount();
                DefaultMutableTreeNode newtn = new DefaultMutableTreeNode(Integer.toString(TNcounter++));
                tn.insert(newtn, numchildren);
                int[] indices = new int[1];
                indices[0] = numchildren;
                model.nodesWereInserted(tn, indices);
            }
        }
    }
    
    void handleKeyPressed(KeyEvent evt) {
    int code = evt.getKeyCode();
    if (code == KeyEvent.VK_DELETE) {  
      myView.deleteSelection();
    } else if (code == KeyEvent.VK_HOME) {  
      Rectangle docbounds = myView.getDocument().computeBounds();
      myView.setViewPosition(docbounds.x, docbounds.y);
    } else if (code == KeyEvent.VK_END) {  
      Rectangle docbounds = myView.getDocument().computeBounds();
      Dimension viewsize = myView.getExtentSize();
      myView.setViewPosition(Math.max(docbounds.x, docbounds.x+docbounds.width-viewsize.width),
                             Math.max(docbounds.y, docbounds.y+docbounds.height-viewsize.height));
    } else if (evt.isControlDown() && code == KeyEvent.VK_A) {  
      myView.selectAll();
    } else if (evt.isControlDown() && code == KeyEvent.VK_X) {  
      myView.cut();
    } else if (evt.isControlDown() && code == KeyEvent.VK_C) {  
      myView.copy();
    } else if (evt.isControlDown() && code == KeyEvent.VK_V) {  
      myView.paste();
    } else if (evt.isControlDown() && code == KeyEvent.VK_Z) {  
      myView.getDocument().undo();
    } else if (evt.isControlDown() && code == KeyEvent.VK_Y) {  // redo
      myView.getDocument().redo();
    } else if (evt.isControlDown() && code == KeyEvent.VK_T) {  // redo
      modifyTree(evt.getModifiers());
    }
  }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem exit;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JComboBox jComboBox4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    // End of variables declaration//GEN-END:variables
}
