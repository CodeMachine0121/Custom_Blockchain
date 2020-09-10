package Util;

import io.netty.handler.codec.base64.Base64Encoder;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.json.JSONObject;

import javax.crypto.*;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;


public class StringUtil {
// AES
    public static SecretKey AES_GenerateKey() throws NoSuchAlgorithmException {

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128,new SecureRandom());
        SecretKey secretKey = keyGenerator.generateKey();

        byte[] iv = new byte[16];
        SecureRandom prng = new SecureRandom();
        prng.nextBytes(iv);

        return secretKey;
    }
    public static String AES_Encrypt(SecretKey secretKey, String msg) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE,secretKey);

        byte[] byteCipherText = cipher.doFinal(msg.getBytes());
        byte[] IV = cipher.getIV();

        // Base 64 encode
        String encodeCipherText=Base64.getEncoder().encodeToString(byteCipherText);
        System.out.println("資訊加密結果:\t"+encodeCipherText);
        return encodeCipherText;
    }

    public static String AES_Decrypt(SecretKey secretKey, String encodeCiphertext) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE,secretKey);

        byte[] decodeCipherText = Base64.getDecoder().decode(encodeCiphertext);
        byte[] decryptedText = cipher.doFinal(decodeCipherText);

        String strDecryptedText = new String(decryptedText);
        System.out.println("資訊解密結果\t"+strDecryptedText);

        return strDecryptedText;
    }


//-----------------------------------------------------------------------------------------------------------
    // HASH encryption
//----------------------------------------------------------------------------------------------------------
    // RIPEMD 160 需要外掛
    public static String apply_RIPEMD160(String input){
        byte[] r = input.getBytes(StandardCharsets.UTF_8);
        RIPEMD160Digest d = new RIPEMD160Digest();
        d.update (r, 0, r.length);
        byte[] o = new byte[d.getDigestSize()];
        d.doFinal (o, 0);

        String hex="";
        for(byte i:o){
            hex+= Integer.toHexString(0xff & i);
        }

        return hex;
    }
    public static String applyHASH(String input,String algorithm) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);

        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }



/*
//-----------------------------------------------------------------------------------------------------------
    // BIP39 Data hiding
//----------------------------------------------------------------------------------------------------------
    private static int CL,ENTLength; // 8,256
    public static List<String> Get_Mnemonic(byte[] privateKeyString) throws IOException, NoSuchAlgorithmException {

        //String ENT = string_to_binary(Privatekey); // Binary string
        String ENT = new BigInteger(1,privateKeyString).toString(2);

        ENTLength = ENT.length();
        CL = ENTLength / 32;
        String HASH = applyHASH(ENT,"SHA-256");
        String CS = string_to_binary(HASH).substring(0,CL);

        String ENT_CS = ENT+CS; // 264-bit
        List<String>GENT_CS = group_string(ENT_CS);
        List<Integer> DENT_CS = binary_to_decimal(GENT_CS);
        List<String>Mnemonic = decimal_to_Mnemonic(DENT_CS);

        return Mnemonic;
    }

    private static String string_to_binary(String privatekey){

        String binary="";

        // privatekey is hex
        char [] chrs = privatekey.toCharArray();
        for(char chr:chrs){
            int i = Integer.parseInt(String.valueOf(chr),16);
            String bin= Integer.toBinaryString(i);
            if(bin.length()<4){
                while(bin.length()<4)
                    bin = "0"+bin;
            }
            else if(bin.length()>4){
                while(bin.length()>4)
                    bin = bin.substring(1,bin.length()-1);
            }
            binary+=bin;
        }
        return binary;

    }
    private static List<String > group_string(String ENTCS){
        List<String> group = new ArrayList<>();
        String tmp="";
        char[] centcs = ENTCS.toCharArray();

        for(int i=0;i<centcs.length;i++){

            if(i%11==0 && i!=0){
                group.add(tmp);
                tmp="";
            }
            tmp+=centcs[i];
        }group.add(tmp);
        return group;
    }
    private static List<Integer> binary_to_decimal(List<String> binary){
        List<Integer> decimal = new ArrayList<>();

        for(String bin:binary){
            int de = Integer.parseInt(bin,2);
            decimal.add(de);
        }
        return decimal;
    }
    private static List<String> decimal_to_Mnemonic(List<Integer> integers) throws IOException {
        List<String> mnemonic = new ArrayList<>();
        for(int t:integers){
            String line = Files.readAllLines(Paths.get("bip-0039/english.txt")).get(t);
            mnemonic.add(line);
        }
        return mnemonic;
    }

    public static String Reverse_Mnemonic(List<String> mnemonic) throws FileNotFoundException {

        List<Integer>DENT_CS = Mnemonic_to_decimal(mnemonic);
        List<String>GENT_CS = decimal_to_binary(DENT_CS);

        String ENT_CS = ungroup_string(GENT_CS);
        String ENT = ENT_CS.substring(0,ENTLength);
        String privateKey = binary_to_string(ENT);

        return privateKey;
    }
    private static List<Integer> Mnemonic_to_decimal(List<String>mnemonic) throws FileNotFoundException {
        List<Integer>DEN_CS=new ArrayList<>();
        File englishtxt = new File("bip-0039/english.txt");
        for(String mn:mnemonic){
            Scanner scanner = new Scanner(englishtxt);
            int i=0;
            while(scanner.hasNextLine()){
                String m = scanner.nextLine();
                if(m.equals(mn))
                    DEN_CS.add(i);
                i++;
            }
        }
        return DEN_CS;
    }
    private static List<String> decimal_to_binary(List<Integer>integers){
        List<String> binary = new ArrayList<>();
        for(int t:integers){
            String bin = Integer.toBinaryString(t);
            if(bin.length()<11){
                while(bin.length()<11)
                    bin = "0"+bin;
            }
            else if(bin.length()>11){
                while(bin.length()>11)
                    bin = bin.substring(1,bin.length()-1);
            }
            binary.add(bin);
        }
        return binary;
    }
    private static String ungroup_string(List<String>GENTCS){
        String entcs = "";
        for(String e:GENTCS){
            entcs+=e;
        }
        return entcs;
    }
    private static String binary_to_string(String ENT){
        String privatekey="";
        char[] chrs = ENT.toCharArray();

        String tmp="";
        for(int i=0;i<chrs.length;i++){
            // 4個4個抓
            if(i%3==0 && i!=0){
                privatekey +=  (char) Integer.parseInt(tmp, 2);
                tmp="";
            }
            tmp+=chrs[i];
        }privatekey+=(char)Integer.parseInt(tmp,2);
        return privatekey ;
    }

*/
}
