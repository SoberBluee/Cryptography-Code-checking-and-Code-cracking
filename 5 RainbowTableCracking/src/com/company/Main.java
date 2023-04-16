package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    ////////////////////////////////////////
    //Rainbow table configuration variables/
    ////////////////////////////////////////
    static int chain_length = 800;
    static String alphanumeric = "1234567890";
    static BigInteger p = new BigInteger("111111113");
    long end_time;

    ArrayList<String> table_data = new ArrayList<String>();
    static boolean found = false;

    //String to integer
    static HashMap<Integer, String> alphabet = new HashMap<Integer, String>(){{
        put(1, "1");
        put(4, "2");
        put(5, "3");
        put(2, "4");
        put(3, "5");
        put(6, "6");
        put(9, "7");
        put(7, "8");
        put(10, "9");
        put(8, "0");
    }};

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

    //Name: getLetter
    //Parameter: r: BigInteger (value to get from HashMap)
    //Description: Will retrieve a string value using integer value from hashmap
    //Return: alphabet: String (returns a single character)
    public static String getLetter(BigInteger r){
        int s = r.intValue();
        return alphabet.get(s);
    }

    //Name: int_to_string
    //Parameter: n: BigInteger (value to be reduced)
    //Description: Will reduce an integer value to a string
    //Return: s: String (reduced string)
    public String int_to_string(BigInteger n){
        char[] alphaChar = convert_to_char();
        int base = alphaChar.length;
        BigInteger r;
        String s = "";

        while(n.compareTo(BigInteger.ZERO) >= 0){
            r = n.remainder(BigInteger.valueOf(base));
            n = n.divide(BigInteger.valueOf(base));
            r = r.add(BigInteger.valueOf(1));
            s = getLetter(r) + s;
            n = n.subtract(BigInteger.valueOf(1));
        }
        return s;
    }

    //Name: reduce
    //Parameter: hash: String (Hash value to be reduced), position: Integer (position in chain), table_pos: Integer (position in table)
    //Description: Will generate a random string with a given length
    //Return: s: String (reduced string)
    public String reduce(String hash, int position){
        //Puts hash value into BigInteger
        BigInteger hash_int = new BigInteger(hash, 16);
        hash_int = hash_int.remainder(p);
        hash_int.add(BigInteger.valueOf(position));
        hash_int = hash_int.remainder(p);


        return int_to_string(hash_int);
    }


    //Name: chainReduce
    //Parameter: hash:String (hash to generate chain from), position:String (position in chain)
    //Description: Will compare 2 hash values to see if hash is found
    //Exceptions: None
    //Return: password:String (end_point to search for in table)
    public String chainReduce(String hash, int position) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String password = reduce(hash, position);
        while(position != chain_length){
            position++;
            hash = SHA1(password);
            password = reduce(hash,position);
        }
        return password;
    }

    //Name: checkHash
    //Parameter: hash_to_check: String (hash that will be checked against the cracked one), hash_to_crack: String (hash the user wants to crack)
    //Description:Will compare 2 hash values to see if hash is found
    //Exceptions: None
    //Return: Boolean
    private boolean checkHash(String hash_to_check, String hash_to_crack){
        if(hash_to_check.equals(hash_to_crack)) {
            System.out.println("Found Password");
            end_time = System.currentTimeMillis();
            found = true;
            return true;
        }else{
            return false;
        }
    }

    //Name: checkTable
    //Parameter: end_point: String (end_point to check in table), hash_to_crack:String (hash the user wants to crack)
    //Description: Will check all endpoints in a table
    //Exceptions:  UnsupportedEncodingException, NoSuchAlgorithmException
    //Return: None
    public void checkTable(String end_point, String hash_to_crack) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        for (String data : table_data) {//Loop over rainbow table

            String[] chain = data.split(":");

            if (chain[1].equals(end_point)) { //Compare end point from table to chainReduce endpoint

                String hash_to_check = SHA1(chain[0]);//Initial hash

                if (checkHash(hash_to_check, hash_to_crack)) {//Compare hash_to_crack with hash from chain
                    System.out.println(hash_to_crack + ":" + chain[0]);
                    return;

                }
                String reduce = reduce(hash_to_check, 1);

                //Loop through length of chain
                for (int x = 0; x < chain_length; x++) {
                    //get hash
                    hash_to_check = SHA1(reduce);
                    if (checkHash(hash_to_check, hash_to_crack)) {//Compare hash_to_crack with hash from chain
                        System.out.println(hash_to_crack + ":" + chain[0]);
                        return;

                    } else {
                        //get next value in the chain
                        reduce = reduce(hash_to_check, x);
                    }
                }
            }
        }
        System.out.println("Not in table");

    }

    //Name: getTableData
    //Parameter: None
    //Description: Will get all chains from a table file and put in ArrayList<String>
    //Exceptions: FileNotFoundException (File does not exist)
    //Return: None
    private void getTableData() throws FileNotFoundException {
        //C:\Users\ethan\Downloads\backupTable.txt
        File table = new File("C:\\Users\\ethan\\OneDrive\\Desktop\\handin\\4 RainbowTable\\src\\com\\company\\MainTable.txt");
        Scanner input = new Scanner(table);

        while(input.hasNextLine()){//Gets next chain in table
            String chain = input.nextLine();
            table_data.add(chain);
        }
    }

    //Name: main
    //Parameter: String[] args
    //Description: Will get all chains from a table file and put in ArrayList<String>
    //Exceptions:  UnsupportedEncodingException, NoSuchAlgorithmException, FileNotFoundException (File does not exist)
    //Return: None
    public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException, FileNotFoundException {
        String hash_to_crack = "911a45dec90e5ee1d4f5acc8ce17c8068c9512b0".toLowerCase(); //Password we will use the rainbow table against

        Main main = new Main();
        main.getTableData();

        long start_time = System.currentTimeMillis();

        for(int i = 0; i <= chain_length; i++){//Loops through length of chain_length from the end
            if(found){ break;}
            System.out.println(i);
            String end_point = main.chainReduce(hash_to_crack, i);//Creates chain from hash_to_crack and get end_point
            main.checkTable(end_point, hash_to_crack);//Will check if end_point is present in table
        }
        System.out.println("Time: " + (main.end_time - start_time) + "ms");
    }
}
