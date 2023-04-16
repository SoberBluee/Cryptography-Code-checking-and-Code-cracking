package sample;

import java.awt.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import java.lang.Math;
import java.util.Arrays;

public class Controller {

    @FXML private Button check;
    @FXML private TextField input;
    @FXML private TextField decode_input;
    @FXML private TextArea output;

    //Name: sqr
    //Parameter: x: Integer
    //Description: Will calculate the square root of x
    //Return: 0
    public Integer square(int x){
        if(x >= 11){
            x = x % 11;
        }
        return (x * x) % 11;
    }

    //Name: sqr
    //Parameter: x: Integer (Digit to get square root of)
    //Description: Will return the square root of x from the array temp
    //Return: 0
    public Integer sqr(int x){
        if(x >= 11){ //check if number is greater than 11
            x = x % 11;
        }

        if(x == 0){//check if number is 0
            return 0;
        }

        int[] temp = {1,-1,5,2,4,-1,-1,-1,3,-1};
        return temp[x - 1];
    }

    //Name: inverse
    //Parameter: a: Integer
    //Description: Will get the inverse of a
    //Return: integer
    public int inverse(int a){
        int[] inverse = {1,6,4,3,9,2,8,7,5,10};
        if(a == 0){//check if number is 0
            a += 1;
        }
        return inverse[a - 1];
    }

    //Name: checkMinus
    //Parameter: x: Integer (Digit to check)
    //Description: Will check if x is minus
    //Return: x: Integer
    public Integer checkMinus(int x){
        if(x < 0){
            x += 11;
        }
        return x;
    }

    //Name: decode
    //Parameter: actionEvent: ActionEvent
    //Description: Will decode and error correct BCH (10, 6) code
    //Return: 0
    public void decode(javafx.event.ActionEvent actionEvent){
        String s;

        int P, Q, R;

        //Where BCH code is stored
        int[] num = new int[10];

        s = decode_input.getText();

        //Converting each number into an integer
        for(int i = 0; i < 10; i++){
            num[i] = Integer.parseInt(String.valueOf(s.charAt(i)));
        }

        //Define syndrome values
        int s1 = 0, s2 = 0, s3 = 0, s4 = 0;

        //Calculate s1
        for(int i = 0; i < num.length; i++){
            s1 += num[i];
        }

        s1 = s1 % 11;
        //Calculate s2
        s2 = (num[0]+2*num[1]+3*num[2]+4*num[3]+5*num[4]+6*num[5]+7*num[6]+8*num[7]+9*num[8]+10*num[9]) % 11;
        //Calculate s3
        s3= (num[0]+4*num[1]+9*num[2]+5*num[3]+3*num[4]+3*num[5]+5*num[6]+9*num[7]+4*num[8]+num[9]) % 11;
        //Calculate s4
        s4= (num[0]+8*num[1]+5*num[2]+9*num[3]+4*num[4]+7*num[5]+2*num[6]+6*num[7]+3*num[8]+10*num[9]) % 11;

        System.out.println("S1 = " + s1 + ", S2 = " + s2 + ", S3 = " + s3 + ", S4 = " + s4);

        //No error
        if(s1 == 0 && s2 == 0 && s3 == 0 && s4 == 0){
            System.out.println("No Error");
            output.appendText("No Error");
            return;
        }

        P = (square(s2) - (s1 * s3)) % 11;
        P = checkMinus(P);

        Q = ((s1 * s4) - (s2 * s3)) % 11;
        Q = checkMinus(Q);

        R = (square(s3) - (s2 * s4)) % 11;
        R = checkMinus(R);

        System.out.println("P = " + P + ", Q = " + Q + ", R =" + R);

        String no_sqr_root = "morethan2_no_sqrt syn(" + s1 + "," + s2 + "," + s3 + "," + s4 + "), pqr(" + P + "," + Q + "," + R + ")\n";

        //Single Error
        if(P == 0 && Q == 0 && R == 0) {
            System.out.println("Single Error");

            int position = (s2 * inverse(s1) % 11);
            System.out.println(position);

            if(position == 0){
                position += 1;
            }

            num[position - 1] = checkMinus(num[position - 1] -= s1);

            System.out.println("Corrected Value: " + Arrays.toString(num));

            output.setText("single_err(i=" + position + ", a=" + s1 + ", syn(" + s1 + "," + s2 + "," + s3 + "," + s4+"))\n");
            output.appendText("Corrected Error :" + Arrays.toString(num) + "\n");

        //Double Error
        }else{
            System.out.println("Double Error");

            //First part to equations
            int i1 =(square(Q) - 4 * P * R) % 11;
            i1 = checkMinus(i1);
            i1 = sqr(i1);

            //Check for 3 error
            if(i1 < 0){
                System.out.println("Three Errors!");
                output.setText("Three Errors\n");
                output.appendText(no_sqr_root);
                return;
            }
            //Seconds part to equations
            int i2 = (2 * P) % 11;
            i2 = checkMinus(i2);

            int tempi = (-Q + i1) % 11;
            tempi = checkMinus(tempi);
            //Calculate i
            int I = (tempi * (inverse(i2))) % 11;
            I = checkMinus(I);

            if(I == 0){
                System.out.println("Three errors");
                output.setText("Three Errors\n");
                output.appendText(no_sqr_root);

                return;
            }

            //First part to equation
            int j1 = (square(Q) - 4 * P * R) % 11;
            j1 = checkMinus(j1);
            j1 = sqr(j1);

            //Check for three errors
            if(j1 < 0){
                System.out.println("Three Errors!\n");
                output.setText("Three Errors\n");
                output.setText(no_sqr_root);
                return;
            }

            //Second part to equation
            int j2 = (2*P) % 11;
            j2 = checkMinus(j2);

            int tempj = (-Q - j1) % 11;
            tempj = checkMinus(tempj);

            int J = (tempj * inverse(j2)) % 11;
            J = checkMinus(J);

            //Check for three errors
            if(J == 0){
                System.out.println("Three errors\n");
                output.setText(no_sqr_root);
                return;
            }

            //Calculating B
            int b = ((I * s1 - s2) * inverse(checkMinus( I - J)) % 11);
            b = checkMinus(b);
            //Calculating A
            int a = checkMinus((s1 - b)) % 11;
            a = checkMinus(a);

            //Correct both errors
            num[I - 1] = (checkMinus(num[I - 1] - a)) % 11;
            checkMinus(num[I - 1]);
            num[J - 1] = (checkMinus(num[J - 1] - b)) % 11;
            checkMinus(num[J - 1]);

            System.out.println("Error magnitude A + B: " + a + ", " + b );
            System.out.println("Location I + J: " + I + ", " + J );

            //Check for 3 errors
            for(int i = 0; i < num.length; i++){
                if(num[i] == 10){
                    System.out.println("Three errors");
                    output.setText(no_sqr_root);
                    return;
                }
            }
            output.setText("double_err(i=" + I + ", a=" + a + ", j=" + J + ", b=" + b + ", syn(" + s1 + "," + s2 + "," + s3 + "," + s4 + "), pqr(" + P + "," + Q + "," + R + ")\n");
            output.appendText("Corrected Error: " + Arrays.toString(num) + "\n");
        }
    }

    //Name: check_number
    //Parameter: actionEvent: ActionEvent
    //Description: Will add parity digits to 6 digit number
    //Return: 0
    public void check_number(javafx.event.ActionEvent actionEvent){

        String s;
        //Takes in the card number into a 16 digit array
        int[] num = new int[10];

        s = input.getText();

        //Converting each number into an integer
        for(int i = 0; i < 6; i++){
            num[i] = Integer.parseInt(String.valueOf(s.charAt(i)));
        }

        int digit7, digit8, digit9, digit10;
        //Calculate parity digits
        digit7 = (4*num[0]+10*num[1]+9*num[2]+2*num[3]+num[4]+7*num[5]) % 11;
        digit8 = (7*num[0]+8*num[1]+7*num[2]+num[3]+9*num[4]+6*num[5]) % 11;
        digit9 = (9*num[0]+num[1]+7*num[2]+8*num[3]+7*num[4]+7*num[5]) % 11;
        digit10 = (num[0]+2*num[1]+9*num[2]+10*num[3]+4*num[4]+num[5]) % 11;

        num[6] = digit7;
        num[7] = digit8;
        num[8] = digit9;
        num[9] = digit10;

        for(int i = 0; i < 10; i++){
            System.out.println(num[i]);
        }

        System.out.println();
    }
}
