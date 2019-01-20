/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package budgetapp;


/*

to improve output: 
don't show total income, expenses and remaining buffer
make so entering number removes zero
ability to delete files
have everything on one screen

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
    
    public static int total = 0; 
    public static String locationAndName = "P:\\budget-"; // easier for working on different computers/file directories
    
    public static Object[][] data;
    public static int numIncome;
    public static int numFixed;
    public static int numVariable;
    public static double totIncome;
    public static double totExpenses;
    
    public static int currentBudget;
    
    public static JTextField titleField;
    public static String titleName;
    
    public static String[] namesArray;
    public static boolean isNewBudget = true;
    
    public static void checkTotal(){
        total = 0;
        while (true){
            File fileName = new File(locationAndName+(total+1)+".txt"); // "P:\\budget-1"
            boolean exists = fileName.exists();
            if (exists){
                total+=1;
            }else{
                break;
            }
        }
        System.out.println("total is "+total);
    }
    
    // works when invoked in main method only
    public static void getNames() { // works when "throws Exception" is removed
        
        try{ // did in try catch
            
            checkTotal();
            String readfirstline;
            File file;
            BufferedReader readline;
            namesArray = new String[total];
        
            for (int i=1; i<=total; i++){
                file = new File(locationAndName+i+".txt");
                readline = new BufferedReader(new FileReader(file));
                namesArray[i-1] = readline.readLine();
            }
        
        }catch (IOException e) {
            System.out.println("couldnt get names");
        } 
    }
    
    public static void newBudgetSetup(){
        checkTotal();
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
        
        titleName = "";
        
        currentBudget = total+1;
        
        editBudget();
    }
    
    
    public static void editBudget(){
        
        String[] columnNames = {"item num", "category", "estimate"};
        
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
        
        JPanel messagePanel = new JPanel();
        JLabel messageLabel = new JLabel("fill in the following table");
        messagePanel.add(messageLabel);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setSize( 100, 100 );
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        table.setFillsViewportHeight(true);
        
        JPanel panel = new JPanel();
        JLabel titleLabel = new JLabel("Title");
        
        titleField = new JTextField(10);
        titleField.setText(titleName);
        panel.add(titleLabel);
        panel.add(titleField);
        
        JButton save = new JButton("Calculate and Save");
        panel.add(save);
        save.addActionListener(new ActionListener() {
            @Override
         public void actionPerformed(java.awt.event.ActionEvent evt) {
             
            boolean isDouble = true;
            String value; 
            String[] categories = new String[numIncome+numFixed+numVariable+3];
            String[] estimate = new String[numIncome+numFixed+numVariable+3];
                
            for (int i=0; i<(numIncome+numFixed+numVariable+3); i++){
                    
                if ((i==0)|| (i==(numIncome+1)) || (i==numIncome+numFixed+2)){ // are the spaces
                    continue;
                }
                
                categories[i] = (String.valueOf (data[i][1]));
                estimate[i] = (String.valueOf (data[i][2]));
                
                if (!isDouble){
                    continue;
                }
                
                isDouble = checkNum(estimate[i], "double");
                    
                // if they are empty
                //if (categories[i]==""){
                //    categories[i] ="null";
                //}
                    
                if (data[i][2]=="null"|| data[i][2]=="" || data[i][2]=="0.00"){ 
                    isDouble=true;
                    data[i][2] = "0.00";
                    estimate[i] ="0.00";
                }  
                
                // trying to debug
                if (!isDouble){
                    System.out.println(i+"*"+data[i][2]+"*");
                    
                }
                
            }
                
            if (isDouble){ // only saves and calculates if all numbers
                    
                    
                // file input/output
                // if table item is empty will save as null
                try(FileWriter filewrite = new FileWriter(locationAndName+currentBudget+".txt", false); // based on location // true will append
                    BufferedWriter bufferwrite = new BufferedWriter(filewrite);
                    PrintWriter printwrite = new PrintWriter(bufferwrite))
                { 
                    totIncome = 0;
                    totExpenses = 0;
                    
                    if (titleField.getText()=="null" || titleField.getText()==""){ // not working
                        printwrite.println("untitled");
                    }else{
                        printwrite.println(titleField.getText());
                    }
                    printwrite.println(numIncome);
                    printwrite.println(numFixed);
                    printwrite.println(numVariable);
                    for (int i=0; i<(numIncome+numFixed+numVariable+3); i++){
                        if ((i==0)|| (i==(numIncome+1)) || (i==numIncome+numFixed+2)){
                            printwrite.println("**********");
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
                    printwrite.println("**********");
                    printwrite.println("total income");
                    printwrite.println(totIncome);
                    printwrite.println("total expenses");
                    printwrite.println(totExpenses);
                    printwrite.println("remaining buffer");
                    printwrite.println(totIncome-totExpenses);
                    
                    data[numIncome+numFixed+numVariable+3][2] = totIncome;
                    data[numIncome+numFixed+numVariable+4][2] = totExpenses;
                    data[numIncome+numFixed+numVariable+5][2] = totIncome-totExpenses;
                    
                    if (messageLabel.getText()=="saved"){
                        messageLabel.setText("resaved");
                    }else{
                        messageLabel.setText("saved");
                    }
                    
                    
                } catch (IOException e) { // exception in case file not found or error in location
                    System.out.println("Exception Occurred.");
                }
                    
            }else{
                if (messageLabel.getText()=="couldn't save, not all positive number values"){
                    messageLabel.setText("still not positve number values");
                }else{
                    messageLabel.setText("couldn't save, not all positive number values");
                }
            }
               
            
         }
        });

        //Creating the Frame
        JFrame frame;
        if (isNewBudget){
            frame = new JFrame("New Budget");
        }else{
            frame = new JFrame("Edit "+titleName);
        }
        frame.setSize(500, 400);
        
        frame.setLayout(new BorderLayout());
        //frame.add(table.getTableHeader(), BorderLayout.PAGE_START);
        //frame.add(table, BorderLayout.CENTER); //needs to be out so scroll pane can work
        
        frame.add(messagePanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);

        
        frame.setVisible(true);
        
    }
    
   
    public static void getCategories(){
        JFrame categoryFrame = new JFrame("Initializing New Budget");
        categoryFrame.setSize(300, 200);
        
        JLabel catMessage = new JLabel("fill in the number of items in each category");
        
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
                 newBudgetSetup();
                 
             }else{
                 // having multiple messages makes sure that the user can see they are still doing something wrong
                 if (catMessage.getText()=="error, enter positve integer values"){
                    catMessage.setText("still not positve integer values");
                 }else{
                    catMessage.setText("error, enter positve integer values");
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
                if (y<0){
                    return false;
                }
            }else if (type=="int"){
                int y = Integer.parseInt(x);
                if (y<0){
                    return false;
                }
            }else{ // anything else
                return false;
            }
            return true;
        }catch (NumberFormatException ex) {
            return false;
        }        
    }
    
    
    
    public static void getBudgetInfo(){
        
        try{ 
            
            String readfirstline;
            File file = new File(locationAndName+currentBudget+".txt");
            BufferedReader readline = new BufferedReader(new FileReader(file));
            namesArray = new String[total];
            
            titleName = (readline.readLine());
            
            numIncome = Integer.parseInt(readline.readLine());
            numFixed = Integer.parseInt(readline.readLine());
            numVariable = Integer.parseInt(readline.readLine());
            
            String extraString; // just to account for space
            
            data = new Object[numIncome+numFixed+numVariable+6][3];
        
            extraString = (readline.readLine());
            data[0][0] = "disposable income";
            for (int i=0; i<numIncome; i++){
                data[i+1][0] = i+1;
                data[i+1][1] = readline.readLine();
                data[i+1][2] = readline.readLine();
            }
            extraString = (readline.readLine());
            data[numIncome+1][0] = "fixed expenses";
            for (int i=0; i<numFixed; i++){
                data[i+numIncome+2][0] = i+1;
                data[i+numIncome+2][1] = readline.readLine();
                data[i+numIncome+2][2] = readline.readLine();
            }
            extraString = (readline.readLine());
            data[numIncome+numFixed+2][0] = "variable expenses";
            for (int i=0; i<numVariable; i++){
                data[i+numIncome+numFixed+3][0] = i+1;
                data[i+numIncome+numFixed+3][1] = readline.readLine();
                data[i+numIncome+numFixed+3][2] = readline.readLine();
            }
            extraString = (readline.readLine());
            for (int i=0; i<3; i++){
                data[numIncome+numFixed+numVariable+3+i][1] = readline.readLine();
                data[numIncome+numFixed+numVariable+3+i][2] = readline.readLine();
            }
            
            editBudget();
            
        }catch (IOException e) {
            System.out.println("couldn't show the file");
        } 

        
    }
    
    
    
    // shows a list of all past saved budgets
    public static void viewBudgets(){
        getNames();
        
        JFrame viewFrame = new JFrame("contents");
        viewFrame.setSize(300, 200);
        
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
                 viewLabel.setText("you didn't select anything");
             }else{
                viewFrame.setVisible(false);
                currentBudget = row+1;
                System.out.println("you selected: "+namesList[row][0]);
                getBudgetInfo();
             }
         }
        });
        
        JScrollPane viewScrollPane = new JScrollPane(contentTable);
        viewScrollPane.setSize( 100, 100 );
        viewScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        contentTable.setFillsViewportHeight(true);
        
        
        viewFrame.add(viewLabel, BorderLayout.NORTH);
        viewFrame.add(viewScrollPane, BorderLayout.CENTER);
        viewFrame.add(selectBtn, BorderLayout.SOUTH);
        viewFrame.setVisible(true);
        
    }
    
    public static void viewInstructions(){
        
        JTextArea textArea = new JTextArea(5, 20);
        
        textArea.append("hello\n this is a test\n put instructions here\n");
        textArea.setEditable(false);
        
        JFrame instFrame = new JFrame("Instructions");
        instFrame.setSize(300, 200);
        instFrame.add(textArea);
        instFrame.setVisible(true);
    }
    
    
    public static void main(String args[])  {//throws Exception
        
        
        JFrame homeFrame = new JFrame("Home");
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        homeFrame.setSize(400, 200);
        homeFrame.setLayout(new GridLayout(3,2));
        homeFrame.setVisible(true);
        
        
        JButton newBtn = new JButton("New Budget");
        newBtn.addActionListener(new ActionListener() {
            @Override
         public void actionPerformed(java.awt.event.ActionEvent evt) { // can only call one method?
             //newBtn.setEnabled(false); // not working
             isNewBudget = true;
             getCategories();
             //newBtn.setEnabled(true);
         }
        });
        
        JButton viewBtn = new JButton("View Budgets");
        viewBtn.addActionListener(new ActionListener() {
            @Override
         public void actionPerformed(java.awt.event.ActionEvent evt) {
             isNewBudget = false; 
             viewBudgets(); 
         }
        });
        
        
        JButton instBtn = new JButton("Instructions");
        instBtn.addActionListener(new ActionListener() {
            @Override
         public void actionPerformed(java.awt.event.ActionEvent evt) {
             viewInstructions();
         }
        });
        
        JPanel homePanel = new JPanel();
        GridLayout homegrid = new GridLayout(3, 1);
        //homePanel.setLayout(homegrid);
        
        homePanel.add(newBtn);
        homePanel.add(viewBtn);
        homePanel.add(instBtn);
        
        homeFrame.add(homePanel, BorderLayout.CENTER);
       
    }
    
    
}
