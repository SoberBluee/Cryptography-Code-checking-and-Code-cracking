package com.company;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Scanner;


public class Main extends Thread {

    static Boolean found = false;
    static long end_time;
    static String alphanumeric = "abcdefghijklmnopqrstuvwsyz0123456789"; //charset used in brute force attack
    static int numbers[] = {0,1,2,3,4,5,6,7,8,9}; //numeric charset for bch attack
    static char[] new_char_array;

    //Name: convertToHex
    //Parameter: data: Byte[] (hash value byte array)
    //Description: Converts a hash value byte array to a string
    //Return: buf: String (hashed string)
    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        }
        return buf.toString();
    }

    //Name: SHA1
    //Parameter: text: String (String to be hashed)
    //Description: Will hash a string to a hexadecimal value
    //Return: buf: String (hashed string)
    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] sha1hash = new byte[40];
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    //Name: check_number
    //Parameter: six_digit: String (digit to generate parity bits for )
    //Description: Will hash a string to a hexadecimal value
    //Return: string (six_digit number with parity bits at the end)
    public static String check_number(String six_digit){
        //Takes in the card number into a 16 digit array
        int[] num = new int[10];

        //Converting each number into an integer
        for(int i = 0; i < 6; i++){
            num[i] = Integer.parseInt(String.valueOf(six_digit.charAt(i)));
        }

        int digit7, digit8, digit9, digit10;
        //Generate parity bits for 6 digit number
        digit7 = (4*num[0]+10*num[1]+9*num[2]+2*num[3]+num[4]+7*num[5]) % 11;
        digit8 = (7*num[0]+8*num[1]+7*num[2]+num[3]+9*num[4]+6*num[5]) % 11;
        digit9 = (9*num[0]+num[1]+7*num[2]+8*num[3]+7*num[4]+7*num[5]) % 11;
        digit10 = (num[0]+2*num[1]+9*num[2]+10*num[3]+4*num[4]+num[5]) % 11;
        //appends parity bits
        num[6] = digit7;
        num[7] = digit8;
        num[8] = digit9;
        num[9] = digit10;

        return Arrays.toString(num).replaceAll("[\\[\\],]", "");
    }

    //Name: convert_to_char
    //Parameter: None
    //Description: Will convert a string to a char array
    //Return: charAlphaNumeric: Char[] (returns a alpha numeric char array)
    public static char[] convert_to_char(){
        char[] charAlphaNumeric = new char[alphanumeric.length()];
        for(int i = 0; i < alphanumeric.length(); i++){
            charAlphaNumeric[i] = alphanumeric.charAt(i);
        }
        return charAlphaNumeric;
    }


    //Name: crack
    //Parameter: length: Integer (length password to generate), guess: String (password that will be generated), password: String (password to crack), bch: boolean (used bch crack )
    //Description: Recursive function for brute force attack and bch brute force attack
    //Return: void
    public static void crack(int length, String guess, String password, boolean bch) throws IOException, NoSuchAlgorithmException {
        String appended = "";
        String six_digit_combo = "";
        //Will check if the string has been built for that length of password
        if(length == 0){
            if(bch){
                six_digit_combo = guess;
                guess = check_number(guess);

                guess = guess.replace(" ", "");
            }
            //Hashes the built string
            String hash = Main.SHA1(guess);
            //Check if password is correct
            if(hash.equals(password)){
                //ends timer when hash is cracked
                end_time = System.currentTimeMillis();
                if(!bch){
                    Writer write = new BufferedWriter(new FileWriter("C:\\Users\\ethan\\OneDrive\\Desktop\\handin\\3 BruteForce\\src\\com\\company\\cracked.txt", true));
                    //Appends hash to file
                    write.append(guess + ":" + hash + "\n");
                    write.close();

                    System.out.println("Found Hash: " + hash);

                }else{
                    Writer write = new BufferedWriter(new FileWriter("C:\\Users\\ethan\\OneDrive\\Desktop\\handin\\3 BruteForce\\src\\com\\company\\bch_cracked.txt", true));
                    write.append(guess + ":" + hash + " : 6 digit : " + six_digit_combo + "\n");
                    write.close();
                    System.out.println("Found BCH: " + hash);
                }
                found = true;
                return;
            }
            //If the hash was not found & prevents stack overflow error
            return;
        }

        if(!bch){
            //Will build string from the charset array
            for(int i = 0; i < 36; i++){
                if(found){
                    break;
                }
                //Will build string to the length
                appended = guess + new_char_array[i];
                //Will decrease the length each itteration
                crack(length - 1, appended, password, bch);
            }
        }else{
            //Will build string from the charset array
            for(int i = 0; i < 10; i++){
                if(found){
                    break;
                }
                //Will build string to the length
                appended = guess + numbers[i];
                //Will decrease the length each itteration
                crack(length - 1, appended, password, bch);
            }

        }

        //Will return when it has gone through all permutations of a given length
        return;
    }

    //Name: start
    //Parameter: input: Scanner (hash to crack from file)
    //Description: Will start a brute force attack and output time to a file
    //Return: void
    public static void start(Scanner input) throws IOException, NoSuchAlgorithmException {
        //Gets each new input
        while(input.hasNextLine()){
            String password = input.nextLine();
            //Starts timer in miliseconds
            long start_time = System.currentTimeMillis();
            found = false;
            int length = 1;
            //While the hash is not cracked
            while(!found){
                //start attack
                crack(length, "", password, false);
                length++;
                System.out.println("Length: " + length);
            }
            Writer write = new BufferedWriter(new FileWriter("C:\\Users\\ethan\\OneDrive\\Desktop\\handin\\3 BruteForce\\src\\com\\company\\cracked.txt", true));
            write.append("Time: " + (end_time - start_time) + "ms \n");
            write.close();
        }
    }

    //Name: bch
    //Parameter: input: Scanner (hash to crack from file)
    //Description: Will start a bch brute force attack and output time to a file
    //Return: void
    public static void bch(Scanner input) throws IOException, NoSuchAlgorithmException {
        while(input.hasNextLine()){
            String hashed_bch = input.nextLine();
            long start_time = System.currentTimeMillis();//Starts timer in miliseconds
            found = false;
            int length = 6;//max password length to crack

            //While the hash is not cracked
            while(!found){
                crack(length, "", hashed_bch, true);
            }
            //Outputs time to file
            Writer write = new BufferedWriter(new FileWriter("C:\\Users\\ethan\\OneDrive\\Desktop\\handin\\3 BruteForce\\src\\com\\company\\bch_cracked.txt", true));
            write.append("Time: " + (end_time - start_time) + "ms \n");
            write.close();
        }

    }

    //Name: main
    //Parameter: args: String[]
    //Description: Retrieves passwords from file and will start a brute force attack then bch brute force attack
    //Return: void
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        //Take in hashs to crack
        File hash = new File("C:\\Users\\ethan\\OneDrive\\Desktop\\handin\\3 BruteForce\\src\\com\\company\\hash.txt");
        File bch_hash = new File("C:\\Users\\ethan\\OneDrive\\Desktop\\handin\\3 BruteForce\\src\\com\\company\\bch_hash.txt");

        Scanner input = new Scanner(hash);
        //convert alphanumeric string to char array
        new_char_array = convert_to_char();
        //start brute force attack
        start(input);

        input = new Scanner(bch_hash);
        //Start BCH brute force
        bch(input);

    }
}

