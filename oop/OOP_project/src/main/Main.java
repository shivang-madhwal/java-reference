package main;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

import static java.lang.System.exit;

public class Main{
    public static void main(String[] args){
        // Open input file
        JFileChooser inputFile = new JFileChooser("./src/data");

        if (inputFile.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
            File file = inputFile.getSelectedFile();

            try {
                Scanner scan = new Scanner(file);
                // creating objects of Stock Exchange
                StockExchange nse = new StockExchange("NSE");
                StockExchange bse = new StockExchange("BSE");
                // creating object of the Brokerage Firm
                BrokerageFirm yadavBroking = new BrokerageFirm();
                // creating order book
                OrderBook orders = new OrderBook();

                while (scan.hasNext()) {
                    String line = scan.nextLine();

                    // Extracting the command to execute from the line
                    String command = line.split("[:,]")[0];

                    // ignoring empty lines
                    if(!command.equals("")){
                        // extracting parameters from the line using regular expressions
                        String[] params = DataExtract.paramsRegex(line);
                        // sample params: {"scrip","M&M","sector","auto","O","610","H","610","L","610","C","610"}
                        // sample params: {"user","Mimi","funds","1000","INFY","10","TCS","5","SBI","20"}

                        // Dealing with Add scrip command
                        if(command.equals("Add scrip")){
                            // randomly allocate stock to NSE or BSE
                            Random rand = new Random();
                            // randomly generate 0 or 1
                            int choice = rand.nextInt(2);
                            if(choice == 0){
                                nse.addStock(params);
                            }
                            else{
                                bse.addStock(params);
                            }
//                            nse.queryPrice(params[1]);
//                            bse.queryPrice(params[1]);
                        }
                        // dealing with delete scrip command
                        else if(command.equals("Delete scrip")){
                            // remove stock from stock exchanges
                            // params[1] is the name of the stock
                            nse.stockDeList(params[1]);
                            bse.stockDeList(params[1]);
                            // remove stock from broking firm costumer's profile
                            yadavBroking.deleteUserStock(params[1]);
                        }
                        // dealing with add user command
                        else if(command.equals("Add user")){
                            yadavBroking.addClient(new Trader(params));
                        }
                        // deleting user
                        else if(command.equals("Delete User")){
                            // params[1] is the name of the user
                            yadavBroking.deleteClient(params[1]);
                        }
                        // dealing with show sector command
                        else if (command.equals("Show sector")){
                            System.out.println("----- " + params[1] + " STOCKS -----");
                            // params[1] is the name of the sector
                            nse.querySector(params[1]);
                            bse.querySector(params[1]);
                        }
                        // placing orders
                        else if (command.equals("Place order")){
                            // check if user exists
                            // params[1] is tha name of the user
                            Trader t = yadavBroking.getUser(params[1]);

                            // check if stock exists
                            // param[5] is the name of stock
                            // check stock in NSE
                            StockData s = nse.getStock(params[5]);
                            // if stock not present in nse search BSE
                            if (s == null)
                                s = bse.getStock(params[5]);
                            // if user and stock exists then place order
                            if(t != null && s != null)
                                orders.addOrder(new Order(params,t,s));
                        }
                        // showing order book
                        else if (command.equals("Show Orderbook")){
                            System.out.println("----- ORDER BOOK -----");
                            orders.showOrderBook();
                        }
                        // exiting the program
                        else if (command.equals("Exit")){
                            System.out.println("Thanks for using the system!");
                            exit(0);
                        }
                    }
                }
            }
            catch (FileNotFoundException e){
                System.out.println("File not found");
                exit(1);
            }
        }
    }
}