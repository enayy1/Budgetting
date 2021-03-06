/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package budgetapp;


/*
    uses table and scroll pane
    can now see the actual names of the budget that were saved
    currently only makes a new budget, cannot actually read from a file yet to view or edit or delete
*/

//import java.awt.Frame;  
import javax.swing.*;
import java.awt.*;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


import javax.swing.JOptionPane;

import java.util.Scanner;

import java.util.concurrent.TimeUnit;

import java.awt.event.*; // not used
import javax.swing.event.*;  // not used

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.*;

//https://docs.oracle.com/javase/tutorial/uiswing/components/table.html

class BudgetApp { //  extends JFrame implements ActionListener 
    
    public static String[] categoriesArray = new String[100]; // not yet used
    public static int total = 0; // not yet used
    public static String locationAndName = "P:\\budget-"; // easier for working on different computers/file directories
    
    public static Object[][] data;
    public static int numIncome;
    public static int numFixed;
    public static int numVariable;
    public static double totIncome;
    public static double totExpenses;
    
    public static int currentBudget;
    
    public static JTextField title;
    
    public static String[] namesArray;
    
    public static void checkTotal(){
        total = 0;
        while (true){
            File fileName = new File(locationAndName+(total+1)+".txt"); // "P:\\budget-1"
            boolean exists = fileName.exists();
            if (exists){
                categoriesArray[total] = (locationAndName+total+".txt");
                total+=1;
            }else{
                break;
            }
        }
        System.out.println("total is "+total);
    }
    
    // works when invoked in main method only
    public static void getNamesNew() { // works when "throws Exception" is removed
        
        try{ // did in try catch
            
        checkTotal();
        String readfirstline;
        File file;
        BufferedReader readline;
        namesArray = new String[total];
        
        for (int i=1; i<=total; i++){
            file = new File("P:\\budget-"+i+".txt");
            readline = new BufferedReader(new FileReader(file));
            namesArray[i-1] = readline.readLine();
        }
        
        }catch (IOException e) {
            System.out.println("couldnt get names");
        } 
    }
    
    
    public static void newBudget(){
        
        checkTotal();
        
        String[] columnNames = {"item num", "category", "estimate"};
        
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
            private static final long serialVersionUID = 1L; // not needed
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
             
            boolean isDouble = true;
            String value; 
            String[] categories = new String[numIncome+numFixed+numVariable+3];
            String[] estimate = new String[numIncome+numFixed+numVariable+3];
                
            for (int i=0; i<(numIncome+numFixed+numVariable+3); i++){
                    
                if ((i==0)|| (i==(numIncome+1)) || (i==numIncome+numFixed+2)){
                    continue;
                }
                categories[i] = (String.valueOf (data[i][1]));
                estimate[i] = (String.valueOf (data[i][2]));
                
                if (!isDouble){
                    continue;
                }
                
                isDouble = checkNum(estimate[i], "double");
                    
                // if they are empty
                if (categories[i]==""){
                    categories[i] ="null";
                }
                    
                if (estimate[i]=="null"|| estimate[i]=="" || estimate[i]=="0.00"){ 
                    isDouble=true;
                    data[i][2] = "0.00";
                    estimate[i] ="0.00";
                }  
            }
                
            if (isDouble){ // only saves and calculates if all numbers
                    
                    
                // file input/output
                // if table item is empty will save as null
                currentBudget = total+1;
                try(FileWriter filewrite = new FileWriter(locationAndName+currentBudget+".txt", false); // based on location // true will append
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
                        printwrite.println(estimate[i]); 
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
                    System.out.println("Exception Occurred.");
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
        JFrame frame = new JFrame("New Budget");
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        
        frame.setLayout(new BorderLayout());
        //frame.add(table.getTableHeader(), BorderLayout.PAGE_START);
        //frame.add(table, BorderLayout.CENTER); //needs to be out so scroll pane can work
        
        //frame.add(menuBar, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);

        
        frame.setVisible(true);
        
    }
    
   
    public static void getCategories(){
        JFrame categoryFrame = new JFrame("Initializing New Budget");
        categoryFrame.setSize(300, 200);
        
        JLabel catMessage = new JLabel("fill in these fields");
        
        JLabel incomeLabel = new JLabel("disposable income");
        JLabel fixedLabel = new JLabel("fixed expenses");
        JLabel varLabel = new JLabel("variable expenses");
        
        JTextField incomeText = new JTextField(10);
        JTextField fixedText = new JTextField(10);
        JTextField varText = new JTextField(10);
        
        JButton submitCategories = new JButton("submit");
        submitCategories.addActionListener(new ActionListener() {
            @Override
         public void actionPerformed(java.awt.event.ActionEvent evt) {
             
             if (checkNum(incomeText.getText(), "int") && checkNum(fixedText.getText(), "int") && checkNum(varText.getText(), "int") ){
                 categoryFrame.setVisible(false);
                 numIncome = Integer.parseInt(incomeText.getText());
                 numFixed = Integer.parseInt(fixedText.getText());
                 numVariable = Integer.parseInt(varText.getText());
                 categoryFrame.setVisible(false); // removes this frame
                 newBudget();
                 
             }else{
                 // having multiple messages makes sure that the user can see they are still doing something wrong
                 if (catMessage.getText()=="error, enter integer values"){
                    catMessage.setText("still not integer values");
                 }else{
                    catMessage.setText("error, enter integer values");
                 }
             }
             
             
         }
        });
        
        JPanel catPanel = new JPanel();
        GridLayout grid = new GridLayout(5, 1);
        catPanel.setLayout(grid);
        
        catPanel.add(incomeLabel);
        catPanel.add(incomeText);
        catPanel.add(fixedLabel);
        catPanel.add(fixedText);
        catPanel.add(varLabel);
        catPanel.add(varText);
        
        categoryFrame.add(catMessage, BorderLayout.NORTH);
        categoryFrame.add(catPanel, BorderLayout.CENTER);
        categoryFrame.add(submitCategories, BorderLayout.SOUTH);
        categoryFrame.setVisible(true);
    }
   
    
    // exception handling
    public static boolean checkNum(String x, String type){
        try{
            if (type=="double"){
                double y = Double.parseDouble(x);
            }else if (type=="int"){
                int y = Integer.parseInt(x);
            }else{ // anything else
                return false;
            }
            return true;
        }catch (NumberFormatException ex) {
            return false;
        }        
    }
    
    // DOESN'T WORK YET
    // NEED TO ADD ON TO AND MERGE W/ NEWBUDGET
    public static void seeBudget(){
        JFrame frame = new JFrame("View budget "+currentBudget);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        
        frame.setLayout(new BorderLayout());
        
        //frame.add(menuBar, BorderLayout.NORTH);
        //frame.add(scrollPane, BorderLayout.CENTER);
        //frame.add(panel, BorderLayout.SOUTH);

        
        frame.setVisible(true);
    }
    
    
    
    // shows a list of all past saved budgets
    public static void viewBudgets(){
        getNamesNew();
        
        JLabel viewLabel = new JLabel("select a file to view it");
             
        String[] header = {"budget names"};
        Object[][] namesList = new Object[total][1];
        for (int i=0; i<total; i++){
            namesList[i][0] = namesArray[i];
        }
        
        JTable contentTable = new JTable(namesList, header){
            public boolean isCellEditable(int row, int column) { // disables certain parts of the table
                return false; 
            };
        };
             
        JButton selectBtn = new JButton("select");
        selectBtn.addActionListener(new ActionListener() {
            @Override
         public void actionPerformed(java.awt.event.ActionEvent evt) {
             int row = contentTable.getSelectedRow(); // see's what is selected
             if (row==-1){
                 System.out.println("you didn't select anything");
             }else{
                currentBudget = row+1;
                System.out.println("you selected: "+namesList[row][0]);
                seeBudget();
             }
         }
        });
        
        JScrollPane viewScrollPane = new JScrollPane(contentTable);
        viewScrollPane.setSize( 100, 100 );
        viewScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        contentTable.setFillsViewportHeight(true);
        
        JFrame viewFrame = new JFrame("contents");
        viewFrame.setSize(300, 200);
        viewFrame.add(viewLabel, BorderLayout.NORTH);
        viewFrame.add(viewScrollPane, BorderLayout.CENTER);
        viewFrame.add(selectBtn, BorderLayout.SOUTH);
        viewFrame.setVisible(true);
        
    }
    
    
    public static void main(String args[])  {//throws Exception
        
        getNamesNew(); // only works here
        
        JFrame homeFrame = new JFrame("Home");
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        homeFrame.setSize(500, 400);
        homeFrame.setLayout(new GridLayout(3,2));
        homeFrame.setVisible(true);
        
        
        JButton newBtn = new JButton("New Budget");
        JPanel homePanel = new JPanel();
        
        homePanel.add(newBtn);
        newBtn.addActionListener(new ActionListener() {
            @Override
         public void actionPerformed(java.awt.event.ActionEvent evt) { // can only call one method?
             //newBtn.setEnabled(false); // not working
             getCategories();
             //newBtn.setEnabled(true);
         }
        });
        
        JButton viewBtn = new JButton("View Budgets");
        viewBtn.addActionListener(new ActionListener() {
            @Override
         public void actionPerformed(java.awt.event.ActionEvent evt) {
             //getNamesNew();
             viewBudgets();
         }
        });
        
        homePanel.add(viewBtn);
        homeFrame.add(homePanel, BorderLayout.CENTER);
       
    }
    
    
}
