/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.component;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;

/**
 *
 * @author Dimas
 */
public class SelectedValues extends JFrame implements ActionListener {
    
        private static final long serialVersionUID = 1L;

	private JList list;
	private JButton button;
        private Object[] data = {"Value 1", "Value 2", "Value 3", "Value 4", "Value 5"};
        
        public SelectedValues(){            
            this.getContentPane().setLayout(new FlowLayout());
		
            list = new JList(data);
            button = new JButton("Check");

            button.addActionListener(this);

            // add list to frame
            add(list);
            add(button);
        }
                
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Check")) {
            int index = list.getSelectedIndex();
            System.out.println("Index Selected: " + index);
            String s = (String) list.getSelectedValue();
            System.out.println("Value Selected: " + s);
        }
    }    
}
