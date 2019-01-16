/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*
    uses table and scroll pane
    currently only makes a new budget, cannot read from a file yet to view or edit
*/
package mypanel;

//import java.awt.Frame;  
import javax.swing.*;
import java.awt.*;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.io.File;

import javax.swing.JOptionPane;

import java.util.Scanner;

import java.util.concurrent.TimeUnit;

//https://docs.oracle.com/javase/tutorial/uiswing/components/table.html

class MyPanel{ //  extends JFrame implements ActionListener 
    public static JFrame frame;
    public static String[] categoriesArray = new String[100];
    public static int total = 0;
    public static String location = "P:\\";
    
    public static Object[][] data;
    public static int numIncome;
    public static int numFixed;
    public static int numVariable;
    public static double totIncome;
    public static double totExpenses;
    
    public static JTextField title;
    
    public MyPanel(){
        System.out.println("constructor");
        while (true){
            File tmpDir = new File(location+"tableitems-"+total+".txt");
            boolean exists = tmpDir.exists();
            if (exists){
                System.out.println(total +" it exists");
                this.categoriesArray[total] = (location+"tableitems-"+total+".txt");
                this.total+=1;
            }else{
                System.out.println("doesn't exist");
                break;
            }
        }
    }
    
    public static void main(String args[]) {
        //MyPanel obj = new MyPanel();
        
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("FILE");
        JMenu menuHelp = new JMenu("Help");
        menuBar.add(menuFile); // add to menubar
        menuBar.add(menuHelp);
        JMenuItem menuNew = new JMenuItem(new AbstractAction("New Budget"){ // components of menu
            
            public void actionPerformed(ActionEvent ae) {
                newBudget();
                
            }
          
        });
        JMenuItem menuOpen = new JMenuItem(new AbstractAction("Open"){ // components of menu
            
            public void actionPerformed(ActionEvent ae) {
                JFrame hello = new JFrame("POPUP");
                hello.setSize(200,100);
                //hello.setDefaultCloseOperation(hello.EXIT_ON_CLOSE);
                hello.setVisible(true);
            }
          
        });
        JMenuItem m22 = new JMenuItem("Save as");
        JMenuItem m33 = new JMenuItem("Instructions");
        menuFile.add(menuNew);
        menuFile.add(menuOpen); // add to menu file
        menuFile.add(m22); 
        menuHelp.add(m33); 
        
        
        
        System.out.println("how many dispoasable income sources?");
        Scanner input = new Scanner(System.in);
        numIncome = input.nextInt();
        
        System.out.println("how many fixed expenses?");
        input = new Scanner(System.in);
        numFixed = input.nextInt();
        
        System.out.println("how many variable expenses?");
        input = new Scanner(System.in);
        numVariable = input.nextInt();
        
        
        String[] columnNames = {"item num", "category",
                        "estimate"};
        
        data = new Object[numIncome+numFixed+numVariable+6][3];
        data[0][0] = "disposable income";
        for (int i=0; i<numIncome; i++){
            data[i+1][0] = i+1;
            data[i+1][2] = "0.00";
        }
        data[numIncome+1][0] = "fixed expenses";
        for (int i=0; i<numFixed; i++){
            data[i+numIncome+2][0] = i+1;
            data[i+numIncome+2][2] = "0.00";
        }
        data[numIncome+numFixed+2][0] = "variable expenses";
        for (int i=0; i<numVariable; i++){
            data[i+numIncome+numFixed+3][0] = i+1;
            data[i+numIncome+numFixed+3][2] = "0.00";
        }
        data[numIncome+numFixed+numVariable+3][1] = "total income";
        data[numIncome+numFixed+numVariable+4][1] = "total expenses";
        data[numIncome+numFixed+numVariable+5][1] = "remaining buffer";
        data[numIncome+numFixed+numVariable+3][2] = "0.00";
        data[numIncome+numFixed+numVariable+4][2] = "0.00";
        data[numIncome+numFixed+numVariable+5][2] = "0.00";
        
        
        
        
        //JTable table = new JTable(data, columnNames); // how to make basic table
        JTable table = new JTable(data, columnNames) {
            private static final long serialVersionUID = 1L;

            public boolean isCellEditable(int row, int column) { // disables certain parts of the table
                if ((column==0)|| (row==0) || (row==numIncome+1) || (row==numIncome+numFixed+2)){    
                    return false;   
                }else if (row >=numIncome+numFixed+numVariable+3){
                    return false; 
                }else{
                    return true;
                }
            };
        };
        //table.setBackground(Color.GREEN); // changes whole table
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setSize( 100, 100 );
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        table.setFillsViewportHeight(true);
        
        
        JPanel panel = new JPanel();
        JLabel topLabel = new JLabel("Title");
        title = new JTextField(10);
        panel.add(topLabel);
        panel.add(title);
        
        JLabel label = new JLabel("Press to save");
        JButton save = new JButton("Calculate and Save");
        //panel.add(label);
        panel.add(save);
        save.addActionListener(new ActionListener() {
            @Override
         public void actionPerformed(java.awt.event.ActionEvent evt) {
             
            boolean isInt = true;
            String value; 
            String[] categories = new String[numIncome+numFixed+numVariable+3];
            String[] estimate = new String[numIncome+numFixed+numVariable+3];
                
            for (int i=0; i<(numIncome+numFixed+numVariable+3); i++){
                    
                if ((i==0)|| (i==(numIncome+1)) || (i==numIncome+numFixed+2)){
                    continue;
                }
                categories[i] = (String.valueOf (data[i][1]));
                estimate[i] = (String.valueOf (data[i][2]));
                if (isInt){
                    isInt = checkInt(estimate[i]);
                    if (estimate[i]=="null"|| estimate[i]=="" || estimate[i]=="0.00"){ 
                        isInt=true;
                        data[i][2] = "0.00";
                        estimate[i] ="0.00";
                        
                    }
                }else{
                    
                }
                    
            }
                
            if (isInt){ // only saves and calculates if all numbers
                    
                    
                // file input/output
                // if table item is empty will save as null
                try(FileWriter filewrite = new FileWriter(location+"tableitems.txt", false); // based on location // true will append
                    BufferedWriter bufferwrite = new BufferedWriter(filewrite);
                    PrintWriter printwrite = new PrintWriter(bufferwrite))
                { 
                    totIncome = 0;
                    totExpenses = 0;
                    
                    if (title.getText()=="null" || title.getText()==""){ // not working
                        printwrite.println("untitled");
                    }else{
                        printwrite.println(title.getText());
                    }
                    printwrite.println(numIncome);
                    printwrite.println(numFixed);
                    printwrite.println(numVariable);
                    for (int i=0; i<(numIncome+numFixed+numVariable+3); i++){
                        if ((i==0)|| (i==(numIncome+1)) || (i==numIncome+numFixed+2)){
                            printwrite.println("");
                            continue;
                        }
                        if (i<=numIncome+1){
                            totIncome+= Double.parseDouble(estimate[i]);
                        }else{
                            totExpenses+= Double.parseDouble(estimate[i]);
                        }
                        printwrite.println(categories[i]);
                        printwrite.println(estimate[i]); // need to check if double value
                    }
                    printwrite.println("");
                    printwrite.println("total income");
                    printwrite.println(totIncome);
                    printwrite.println("total expenses");
                    printwrite.println(totExpenses);
                    printwrite.println("remaining buffer");
                    printwrite.println(totIncome-totExpenses);
                    
                    
                    data[numIncome+numFixed+numVariable+3][2] = totIncome;
                    data[numIncome+numFixed+numVariable+4][2] = totExpenses;
                    data[numIncome+numFixed+numVariable+5][2] = totIncome-totExpenses;
                    
                    label.setText("saved");
                    System.out.println("saved data");
                    
                    
                } catch (IOException e) { // exception in case file not found or error in location
                    System.out.println("Exception Occurred:");
                }
                    
            }else{
                label.setText("couln'd save");
                System.out.println("couldn't save, not all number values");
            }
            
            // not neccessary to have this
            try{
                Thread.sleep(2000); // will wait 2 seconds before continuing, not really working
            }catch(InterruptedException ex){
                Thread.currentThread().interrupt();
                System.out.println("thread interrupt");
            }
            label.setText("press to resave");
               
            
         }
        });

        //Creating the Frame
        frame = new JFrame("Budget App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        
        frame.setLayout(new BorderLayout());
        //frame.add(table.getTableHeader(), BorderLayout.PAGE_START);
        //frame.add(table, BorderLayout.CENTER); //needs to be out so scroll pane can work
        
        frame.add(menuBar, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);

        
        frame.setVisible(true);
    }
    
    public static void newBudget(){
        
    }
    
    
    
    // exception handling
    public static boolean checkInt(String x){
        try{
            double y = Double.parseDouble(x);
            return true;
        }catch (NumberFormatException ex) {
            return false;
        }        
    }
    
    
}

