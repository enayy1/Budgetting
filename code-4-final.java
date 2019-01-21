/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package budgetapp;


/*

to improve output of budget app: 
make so entering number removes zero
ability to delete files
changing colour of certain cells in table
have everything on one screen

*/


import javax.swing.*;
import java.awt.*;

import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;

import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.DecimalFormat;

// unused imports
//import java.awt.Frame;  
//import java.awt.FlowLayout;
//import java.awt.event.ActionEvent;
//import java.io.FileReader;
//import java.io.IOException;
//import javax.swing.JOptionPane;
//import java.rmi.RemoteException;
//import java.util.logging.Level;
//import java.util.logging.Logger;

//import java.io.*;

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
    
    public static String titleName;
    
    public static String[] namesArray;
    public static boolean isNewBudget = true;
    
    public static void checkTotal(){
        total = 0;
        while (true){
            File fileName = new File(locationAndName+(total+1)+".txt"); 
            boolean exists = fileName.exists();
            if (exists){
                total+=1;
            }else{
                break;
            }
        }
    }
    
    
    public static void getNames() { 
        try{ 
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
    
    // gets number of disposable income sources, fixed expenses and variable expenses
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
        
        JPanel catPanel = new JPanel(); // category panel
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
    
    public static void newBudgetSetup(){
        checkTotal();
        data = new Object[numIncome+numFixed+numVariable+3][3];
        
        int count = 0;
        for (int i=0; i<numIncome+numFixed+numVariable+3; i++){
            if (i==0){
                data[i][0] = "disposable income";
                count = 0;
                continue;
            }else if (i== (numIncome+1) ){
                data[i][0] = "fixed expenses";
                count = 0;
                continue;
            }else if (i == (numIncome+numFixed+2) ){
                data[i][0] = "variable expenses";
                count = 0;
                continue;
            }
            data[i][0] = ++count;
            data[i][2] = "0.00";
           
        }
        
        titleName = "";
        currentBudget = total+1;
                
        totIncome = 0.00;
        totExpenses = 0.00;
        
        editBudget();
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
            public boolean isCellEditable(int row, int column) { 
                return false; // disables the whole table
            };
        };
        contentTable.setBackground(new Color(230, 243, 255)); // changes whole table colour
             
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
    
    public static void getBudgetInfo(){
        
        try{ 
            // reading a file
            String readfirstline;
            File file = new File(locationAndName+currentBudget+".txt");
            BufferedReader readline = new BufferedReader(new FileReader(file));
            namesArray = new String[total];
            
            titleName = (readline.readLine());
            
            numIncome = Integer.parseInt(readline.readLine());
            numFixed = Integer.parseInt(readline.readLine());
            numVariable = Integer.parseInt(readline.readLine());
            
            String extraString; // just to account for space
            
            data = new Object[numIncome+numFixed+numVariable+3][3];
            
            int count = 0;
            for (int i=0; i<numIncome+numFixed+numVariable+3; i++){
                if (i==0){
                    data[i][0] = "disposable income";
                    extraString = (readline.readLine());
                    count = 0;
                    continue;
                }else if (i== (numIncome+1) ){
                    data[i][0] = "fixed expenses";
                    extraString = (readline.readLine());
                    count = 0;
                    continue;
                }else if (i == (numIncome+numFixed+2) ){
                    data[i][0] = "variable expenses";
                    extraString = (readline.readLine());
                    count = 0;
                    continue;
                }
                data[i][0] = ++count;
                data[i][1] = readline.readLine();
                data[i][2] = readline.readLine();
           
            }
            
            extraString = (readline.readLine());
            totIncome = Double.valueOf(readline.readLine());
            totExpenses = Double.valueOf(readline.readLine());
            
            editBudget();
            
        }catch (IOException e) {
            System.out.println("couldn't show the file");
        } 

        
    }
    
    public static void editBudget(){
        
        String[] columnNames = {"category", "name", "estimate"};
        
        //JTable table = new JTable(data, columnNames); // how to make basic table
        JTable budgetTable = new JTable(data, columnNames) {
            //private static final long serialVersionUID = 1L; // not needed
            public boolean isCellEditable(int row, int column) { // isCellEditable disables certain parts of the table
                if ((column==0)|| (row==0) || (row==numIncome+1) || (row==numIncome+numFixed+2)){    
                    return false;   
                }else{
                    return true;
                }
            };
        };
        
        JPanel messagePanel = new JPanel();
        JLabel messageLabel = new JLabel("fill in the following table");
        messagePanel.add(messageLabel);
        
        JPanel mainPanel = new JPanel();
        mainPanel.add(budgetTable);
        
        JScrollPane scrollPane = new JScrollPane(budgetTable);
        scrollPane.setSize( 100, 100 );
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        budgetTable.setFillsViewportHeight(true);
        
        
        ///// Bottom panel start
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Title");
        JTextField titleField = new JTextField(10);
        titleField.setText(titleName);
        titlePanel.add(titleLabel);
        titlePanel.add(titleField);
        
        // will show total income, total expenses, and remaining buffer //
        JPanel bufferPanel = new JPanel();
        JLabel incomeLabel = new JLabel("Total Income: ");
        JLabel expensesLabel = new JLabel("Total Expenses: ");
        JLabel bufferLabel = new JLabel("Remaining Buffer: ");
        DecimalFormat decimalF = new DecimalFormat("##.00");
        JLabel incomeNumLabel = new JLabel("$ " + decimalF.format(totIncome) );
        JLabel expensesNumLabel = new JLabel("$ " + decimalF.format(totExpenses) );
        JLabel bufferNumLabel = new JLabel("$ " + decimalF.format(totIncome-totExpenses ));
        
        GridLayout bufferGrid = new GridLayout(3, 2);
        bufferPanel.setLayout(bufferGrid);
        bufferPanel.add(incomeLabel);
        bufferPanel.add(incomeNumLabel);
        bufferPanel.add(expensesLabel);
        bufferPanel.add(expensesNumLabel);
        bufferPanel.add(bufferLabel);
        bufferPanel.add(bufferNumLabel);
                
        
        JPanel bottomPanel = new JPanel();
        GridLayout bottomGrid = new GridLayout(2, 1);
        bottomPanel.setLayout(bottomGrid);
        bottomPanel.add(bufferPanel);
        bottomPanel.add(titlePanel);
        
        JButton save = new JButton("Calculate and Save");
        titlePanel.add(save);
        save.addActionListener(new ActionListener() {
            @Override
         public void actionPerformed(java.awt.event.ActionEvent evt) {
             
            boolean isDouble = true;
            String value; 
            String[] categories = new String[numIncome+numFixed+numVariable+3];
            String[] estimate = new String[numIncome+numFixed+numVariable+3];
                
            for (int i=0; i<(numIncome+numFixed+numVariable+3); i++){
                    
                if ((i==0)|| (i==(numIncome+1)) || (i==numIncome+numFixed+2) || (!isDouble) ){ // continues if at spaces or if not all numbers
                    continue;
                }
                
                categories[i] = (String.valueOf (data[i][1]));
                estimate[i] = (String.valueOf (data[i][2]));
                
                isDouble = checkNum(estimate[i], "double"); // if not double, will not check the other numbers
                    
                // if they are empty
                if (categories[i]=="null"){
                    categories[i] ="";
                }
                    
                if ((data[i][2]).equals("null") || (data[i][2]).equals("") ){ 
                    isDouble=true;
                    estimate[i] ="0.00"; // will allow it to be calculated to determine remaining buffer
                } 
                
            }
                
            if (isDouble){ // only saves and calculates if all number values are inputted in estimate section
                    
                    
                // writing in a file
                // if table item is empty will save as null
                try(FileWriter filewrite = new FileWriter(locationAndName+currentBudget+".txt", false); // based on location // true will append
                    BufferedWriter bufferwrite = new BufferedWriter(filewrite);
                    PrintWriter printwrite = new PrintWriter(bufferwrite))
                { 
                    totIncome = 0;
                    totExpenses = 0;
                    
                    if ((titleField.getText()).equals("null") || (titleField.getText()).equals("") ){
                        printwrite.println("untitled budget " + currentBudget); // if the user didn't give a name
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
                    
                    printwrite.println(decimalF.format(totIncome));
                    printwrite.println(decimalF.format(totExpenses));
                    printwrite.println(decimalF.format(totIncome-totExpenses));
                    
                    incomeNumLabel.setText("$ " + decimalF.format(totIncome) );
                    expensesNumLabel.setText("$ " + decimalF.format(totExpenses) );
                    bufferNumLabel.setText("$ " + decimalF.format(totIncome-totExpenses ));
                    
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
        
        ///// Bottom panel end ///// 

        // Creating the Frame //
        JFrame frame;
        if (isNewBudget){
            frame = new JFrame("New Budget");
        }else{
            frame = new JFrame("Edit "+titleName);
        }
        frame.setSize(500, 400);
        
        frame.setLayout(new BorderLayout());
        //frame.add(table.getTableHeader(), BorderLayout.PAGE_START);
        //frame.add(table, BorderLayout.CENTER); // needs to be out so scroll pane can work
        
        frame.add(messagePanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        
        frame.setVisible(true);
        
    }
    
   
    // exception handling to check if valid integer (number of each cateogory) or double (amount of money)
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
            }else{ // anything else, in case error when calling the method
                return false;
            }
            return true;
        }catch (NumberFormatException ex) {
            return false;
        }        
    }
    
    public static void viewInstructions(){
        
        JTextArea textArea = new JTextArea(5, 20);
        
        textArea.append("Create new budgets by pressing 'new budget'\n" +
        "\n" +
        "Will only save and calculate if number values \n" +
        "are inputted in all the estimate sections\n" +
        "\n" +
        "Need to press save to update the budget and \n" +
        "save it to the file\n" +
        "\n" +
        "Can view and edit saved budgets");
        textArea.setEditable(false);
        
        JFrame instFrame = new JFrame("Instructions");
        instFrame.setSize(300, 200);
        instFrame.add(textArea);
        instFrame.setVisible(true);
    }
    
    
    public static void main(String args[])  {
        
        JFrame homeFrame = new JFrame("Home");
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        homeFrame.setSize(250, 150);
        homeFrame.setLayout(new GridLayout(3,2));
        homeFrame.setVisible(true);
        
        JButton newBtn = new JButton("New Budget");
        newBtn.addActionListener(new ActionListener() {
            @Override
         public void actionPerformed(java.awt.event.ActionEvent evt) { 
             isNewBudget = true;
             getCategories();
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
        
        GridLayout homegrid = new GridLayout(3, 1);
        homeFrame.setLayout(homegrid);
        
        homeFrame.add(newBtn);
        homeFrame.add(viewBtn);
        homeFrame.add(instBtn);
    }
    
    
}
