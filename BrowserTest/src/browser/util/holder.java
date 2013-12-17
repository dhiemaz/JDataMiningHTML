/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package browser.util;

/**
 *
 * @author Dimas
 */
public class holder {
    
    private String[][] data_row;
    
    public holder(int i, int j){
        data_row = new String[i][j];
    }

    public String[][] getData_row() {
        return this.data_row;
    }
    
    public String getData_row(int i, int j) {
        return this.data_row[i][j];
    }

    public void setData_row(String[][] data_row) {
        this.data_row = data_row;
    }        
    
    public void setData_row(int i, int j, String data) {
        this.data_row[i][j] = data;
    }  
}
