/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author Rizal Ahmad Jabbar
 */
public class ConnectSQLite {
    Connection connect;
    PreparedStatement ps;
    Statement stmt;
    public void InsertData(Object topik, Object header[],Object data[][]){ 
    
        System.out.println("topik = " + topik);
        System.out.println("header = " + header.length);
        System.out.println("data = " + data.length);
    try{
        Class.forName("org.sqlite.JDBC").newInstance();
        connect = DriverManager.getConnection("jdbc:sqlite:D:\\dist\\database.db");
        System.out.println("Opened database successfully");    
        
        // Insert Table Info //
        String query="INSERT INTO table_info(informasi) VALUES(?)"; 
        ps = connect.prepareStatement(query);        
        ps.setString(1, topik.toString());
        ps.addBatch();
        ps.executeBatch();
        ps.close(); 
        System.out.println("Insert table_info done!");
        
        // Get id_table //
        int id_table = 0;
        query = "SELECT id_table from table_info where informasi = '" + topik.toString()+"'"; 
        stmt = connect.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while(rs.next()){
            id_table = rs.getInt("id_table");
        }
        
        for(int i=0;i < header.length;i++){ 
            System.out.println("header ke = " + i);
            query="INSERT INTO table_header(nama_header, id_table) VALUES(?,?)";            
            ps=connect.prepareStatement(query);
            ps.setString(1, header[i].toString());
            ps.setInt(2, id_table); 
            ps.addBatch(); 
            ps.executeBatch();
            ps.close(); 
            System.out.println("header = " + header[i].toString());
        }
        
        System.out.println("Insert table_header done!");
                                                
        for(int i=0; i < header.length;i++){ 
            int id_header = 0;
            query = "SELECT id_header from table_header where nama_header = '" + header[i].toString()+"'"; 
            Statement stmt = connect.createStatement();
            ResultSet rs_header = stmt.executeQuery(query);
            while(rs_header.next()){
                id_header = rs_header.getInt("id_header");
            }
            
            for(int j=0; j < data.length;j++){       
                System.out.println("data["+i+"]["+j+"] = " + data[j][i].toString());
                query="INSERT INTO table_data(id_baris, id_header, value) VALUES(?, ?, ?)";
                ps=connect.prepareStatement(query);
                ps.setInt(1, j);
                ps.setInt(2, id_header);
                ps.setString(3, data[j][i].toString());
                System.out.println("data["+i+"]["+j+"] = " + data[j][i].toString()); 
                ps.addBatch();
                ps.executeBatch();
                ps.close(); 
            }            
        } 
        
        System.out.println("Insert table_header done!");         
                
        JOptionPane.showMessageDialog(null, "Proses integrasi Selesai", "Notifikasi", 1);
    }catch(Exception e){
        JOptionPane.showMessageDialog(null, "Terjadi kesalahan : " + e.toString());  
        System.out.println("Terjadi kesalahan = "+ e.toString());
    }finally{
            try{            
                connect.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    public int getInfoCount(){        
        PreparedStatement ps;
        int count = 0;
        try{
            Class.forName("org.sqlite.JDBC").newInstance();
            connect = DriverManager.getConnection("jdbc:sqlite:D:\\dist\\database.db");
            stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) from table_info");
            while(rs.next()){
                count = rs.getInt("COUNT(*)");
            } 
            System.out.println("count info table = "+ count);
        }catch(Exception e){
             JOptionPane.showMessageDialog(null, "Terjadi kesalahan : " + e.toString());
             System.out.println("Terjadi kesalahan = "+ e.toString());
        }finally{
            try{            
                connect.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
        return count;
    }
    
    public ArrayList<String> getTopic(){
        PreparedStatement ps;
        ArrayList<String> topic = new ArrayList<String>();
        try{
            Class.forName("org.sqlite.JDBC").newInstance();
            connect = DriverManager.getConnection("jdbc:sqlite:D:\\dist\\database.db");
            stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * from table_info");
            while(rs.next()){
                topic.add(rs.getString("informasi"));
            }                                    
        }catch(Exception e){
             JOptionPane.showMessageDialog(null, "Terjadi kesalahan : " + e.toString());
             System.out.println("Terjadi kesalahan = ");
             e.printStackTrace();
        }finally{
            try{            
                connect.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
        return topic;
    }
    
    public ArrayList<String> getHeaderFromTopic(String topik){
        PreparedStatement ps;
        ResultSet rs = null;
        ArrayList<String> header = new ArrayList<String>();
        int id_table = 0;
        try{
            Class.forName("org.sqlite.JDBC").newInstance();
            connect = DriverManager.getConnection("jdbc:sqlite:D:\\dist\\database.db");
            
            // Get IDTable //
            stmt = connect.createStatement();
            rs = stmt.executeQuery("SELECT id_table from table_info where informasi = '"+topik+"'");
            while(rs.next()){
                id_table = rs.getInt("id_table");
            }  
                        
            // Get Header //                        
            stmt = connect.createStatement();
            rs = stmt.executeQuery("SELECT nama_header from table_header where id_table = " + id_table);
            while(rs.next()){
                header.add(rs.getString("nama_header"));
            }                                    
            
        }catch(Exception e){
             JOptionPane.showMessageDialog(null, "Terjadi kesalahan : " + e.toString());
             System.out.println("Terjadi kesalahan = ");
             e.printStackTrace();
        }finally{
            try{
                rs.close();
                connect.close();                
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
        return header;
    }
        
    public ArrayList<String> getIdHeaderFromTopic(String topik){
        PreparedStatement ps;
        ResultSet rs = null;
        ArrayList<String> id_header = new ArrayList<String>();
        int id_table = 0;
        try{
            Class.forName("org.sqlite.JDBC").newInstance();
            connect = DriverManager.getConnection("jdbc:sqlite:D:\\dist\\database.db");
            
            // Get IDTable //
            stmt = connect.createStatement();
            rs = stmt.executeQuery("SELECT id_table from table_info where informasi = '"+topik+"'");
            while(rs.next()){
                id_table = rs.getInt("id_table");
            }  
                        
            // Get Header //                        
            stmt = connect.createStatement();
            rs = stmt.executeQuery("SELECT id_header from table_header where id_table = "+id_table);
            while(rs.next()){
                id_header.add(Integer.toString(rs.getInt("id_header")));
            }                                    
            
        }catch(Exception e){
             JOptionPane.showMessageDialog(null, "Terjadi kesalahan : " + e.toString());
             System.out.println("Terjadi kesalahan = ");
             e.printStackTrace();
        }finally{
            try{
                rs.close();
                connect.close();                
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
        return id_header;
    }
    
    public ArrayList<String> getDataFromHeader(String id_header){
        PreparedStatement ps;
        ResultSet rs = null;
        ArrayList<String> data = new ArrayList<String>();
        int id_table = 0;
        try{
            Class.forName("org.sqlite.JDBC").newInstance();
            connect = DriverManager.getConnection("jdbc:sqlite:D:\\dist\\database.db");
            
            // Get data //
            stmt = connect.createStatement();
            rs = stmt.executeQuery("SELECT value from table_data where id_header = "+id_header);
            while(rs.next()){
                data.add(rs.getString("value"));
            }  
                                                                                 
        }catch(Exception e){
             JOptionPane.showMessageDialog(null, "Terjadi kesalahan : " + e.toString());
             System.out.println("Terjadi kesalahan = ");
             e.printStackTrace();
        }finally{
            try{
                rs.close();
                connect.close();                
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
        return data;
    }
}
