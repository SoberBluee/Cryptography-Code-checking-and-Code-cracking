package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Controller {

    @FXML private TextArea output;
    @FXML private TextField input;

    //Name: clearText
    //Parameter: actionEvent: ActionEvent - event listener
    //Description: Will clear the text in the textArea
    //Return: 0
    public void clearText(javafx.event.ActionEvent actionEvent) {
        output.setText("");
    }

    //Name: verify_card
    //Parameter: actionEvent: ActionEvent - event listener
    //Description: Will verify if a given number is a valid credit card number (luhn check)
    //Return: 0
    public void luhn_check(javafx.event.ActionEvent actionEvent){
        String s;
        //Takes in the card number into a 16 digit array
        int[] card = new int[16];

        s = input.getText();

        try{
            //Converting each number into an integer
            for(int i = 0; i < 16; i++){
                card[i] = Integer.parseInt(String.valueOf(s.charAt(i)));
            }
        //Check if code is 16 digits
        }catch(StringIndexOutOfBoundsException e){
            System.out.println("Error: " + e);
            output.setText("Code is not 16 digits");
            return;
        //Check if a non-numeric value exists
        }catch(NumberFormatException e){
            System.out.println("Error: " + e);
            output.setText("Code contains a non-numeric value");
            return;
        }

        //Check if card length is correct
        if(card.length == 16){
            int result = 0;
            //Boolean for getting every second digit
            boolean second = false;

            //Loops from right to left
            for(int i = card.length - 1; i >= 0; i--){

                int digit = card[i];
                //If second is true then double the second digit
                if(second) digit = digit * 2;
                //Dividing digigit by 10
                result += digit / 10;
                result += digit % 10;
                //changing value to either skip or get second integer
                second = !second;
            }
            //Checking if the result is a multiple of 10
            if((result % 10) == 0){
                output.setText(" Valid Credit Card number");
                return;
            }
            output.setText("Invalid Credit Card ISBN number");
            return;
        }else{
            output.setText("Code is not 16 digits");
            return;
        }
    }
    //Name: verify_isbn
    //Parameter: actionEvent: ActionEvent - event listener
    //Description: Will verify if a given number is an isbn number
    //Return: 0
    public void verify_isbn(javafx.event.ActionEvent actionEvent) {
        String s;
        int result;
        int[] card = new int[10];

        s = input.getText();

        try{
            for(int i = 0; i < 10; i++){ //loop over each number from input
                card[i] = Integer.parseInt(String.valueOf(s.charAt(i)));
            }

            //Calculation to see if card is valid ISBN
            result = (card[0]+2*card[1]+3*card[2]+4*card[3]+5*card[4]+6*card[5]+7*card[6]+8*card[7]+9*card[8]+10*card[9]) % 11;
            if(result == 0){
                output.setText(" Valid ISBN number");
                return;
            }
            System.out.println("Invalid ISBN number");
            return;

        }catch(NumberFormatException e){
            int count = 0;

            System.out.println("Error: " + e);

            String[] check = s.split("");

            for(int i = 0; i < 10; i++){
                System.out.println(check[i]);
                if(!isNumeric(check[i])){
                    count++;
                }
            }
            output.setText("Number has been transposed by " + count);
        }
    }
    //Checks if a given string is a Integer or not
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}
