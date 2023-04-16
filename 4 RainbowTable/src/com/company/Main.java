package com.company;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.lang.Math;

//Name: Task
//Dependencies: Thread
//Parameter: None
//Description: Thread class to achieve multi-threading (not used for this task)
class Task extends Thread{
    //Configuration values
    long pass_space_size;
    int max_pass_len;
    String numeric;
    int chain_length;

    //Prime number bigger than password search space
    BigInteger p = new BigInteger("111111113");

    //Name: Task
    //Parameter: pass_space: Long (password space), max_len: Integer (max password length), numeric: String (alphanumeric), chain_length: integer (length of chain)
    //Description: Converts a hash value byte array to a string
    //Return: buf: String (hashed string)
    public Task(long pass_space, int max_len, String numeric, int chain_length){
        this.pass_space_size = pass_space;
        this.max_pass_len = max_len;
        this.numeric = numeric;
        this.chain_length = chain_length;
    }

    //Hashmap to map an integer value to string
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
    public char[] convert_to_char(){
        char[] charNumeric = new char[this.numeric.length()];
        //Loops over length of charset
        for(int i = 0; i < this.numeric.length(); i++){
            charNumeric[i] = this.numeric.charAt(i);
        }
        return charNumeric;
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

    //Name: generate_chain
    //Parameter: chain_length: integer (How long a chain will be), start: String (Starting point for chain), table_pos: integer (position in the table)
    //Description: Will generate a chain then append the start and end point to a file
    //Return: None
    public void generate_chain(int chain_length, String start) throws IOException, NoSuchAlgorithmException {
        String reduced_hash = "";
        String hash = "";

        hash = SHA1(start);
        reduced_hash = reduce(hash, 1);

        //Creates chains based on chain length
        for(int i = 0; i < chain_length; i++){
            hash = SHA1(reduced_hash);
            reduced_hash = reduce(hash, i);
        }

        //Appends start and end point to chain
        BufferedWriter table = new BufferedWriter(new FileWriter("C:\\Users\\ethan\\OneDrive\\Desktop\\handin\\4 RainbowTable\\src\\com\\company\\MainTable.txt", true));
        table.append(start + ":" + reduced_hash + "\n");
        table.close();

        return;
    }

    //Name: random_string
    //Parameter: None
    //Description: Will generate a random string with a given length
    //Return: None
    private String random_string(){
        Random rand = new Random();
        StringBuilder rand_string = new StringBuilder();
        //Build random string from max password length
        for(int x = 0; x < max_pass_len; x++){
            int pos = rand.nextInt(numeric.length());
            char character = numeric.charAt(pos);
            rand_string.append(character);
        }

        return rand_string.toString();
    }

    //Name: run
    //Parameter: None
    //Description: Will create a random string and generate chains
    //Return: None
    public void run(){
        //Loop over the calculated password search space
        for(int x = 0; x < pass_space_size; x++){
            try {
                generate_chain(chain_length, random_string());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }
}

//Name: Main
//Dependencies: None
//Parameter: None
//Description: Main class that calls thread class
public class Main {

    ////////////////////////////////////////
    //Rainbow table configuration variables/
    ////////////////////////////////////////
    static int chain_length = 2500;
    //Numeric character set
    static String numeric = "1234567890";
    //Max password length
    static int max_pass_len = 8;
    static long pass_space_size = 0;

    //Name: calc_pass_space
    //Parameter: None
    //Description: Will calculate the password space with a given charset length and password length
    //Return: None
    private void calc_pass_space(){
        for(int i = max_pass_len; i != 0; i--){
            int result = (int) Math.pow(numeric.length(),i); //table size
            pass_space_size = pass_space_size + result;
        }
        System.out.println("True Password Space :" + pass_space_size);

        //Makes table bigger to compensate for collisions
        pass_space_size = (long) (pass_space_size * 1.3);

        System.out.println("Enlarged Password Space :" + pass_space_size);
    }

    //Name: main
    //Parameter: args: String[]
    //Description: Starts main program
    //Return: None
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        Main main = new Main();
        main.calc_pass_space();
        //Creates thread class an passes in configuration variables
        Task t1 = new Task(pass_space_size, max_pass_len, numeric, chain_length );
        t1.start();

        checkForCollision();

    }

    //Name: checkForCollision
    //Parameter: None
    //Description: Will check for collisions in a table
    //Return: None
    private static void checkForCollision() throws IOException {
        HashMap<String, String> table = new HashMap<String, String>();
        int collisions = 0;

        //Reads in file and puts chains into a list
        BufferedReader reader = null;
        try{
            reader = new BufferedReader(new FileReader(new File("C:\\Users\\ethan\\OneDrive\\Desktop\\handin\\4 RainbowTable\\src\\com\\company\\MainTable.txt")));
            String chain;
            while((chain = reader.readLine()) != null){
                String[] new_chain = chain.split(":");
                table.put(new_chain[1], new_chain[0]);
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            reader.close();
        }

        Iterator<Map.Entry<String, String>> loop = table.entrySet().iterator();
        while(loop.hasNext()){
            Map.Entry<String, String> entry = loop.next();
            //Gets
            String end_point = entry.getKey();

            if(end_point == entry.getKey()){
                collisions++;
            }
        }

        System.out.println("Amount of Chains: " + table.size());
        System.out.println("Number of ColisionsL: " + collisions );

    }
}
