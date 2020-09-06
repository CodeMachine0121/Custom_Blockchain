package BlockChain;

import Util.KeyGenerater;
import Util.StringUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.lang.reflect.Array;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class Miner {

    private KeyGenerater keyGenerater;

    public String address;
    public String publicKey;
    private String privateKey;
    public double balance;




    public Miner() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalAccessException, NoSuchPaddingException, IOException {

        KeyGenerater keyGenerater = new KeyGenerater();

        this.publicKey = keyGenerater.Get_PublicKey_String();
        this.privateKey = keyGenerater.Get_PrivateKey_String();

        this.address = keyGenerater.Get_Address(publicKey);
        this.balance = -1;


    }
//-----------------------------------------------------------------------------------------------------------
    // transaction function
//----------------------------------------------------------------------------------------------------------
    public Transaction Make_Transaction(String sender, String receiver, double amount, double fee, String messages) throws NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException, IllegalBlockSizeException, InvalidKeyException, SignatureException, BadPaddingException, IllegalAccessException, NoSuchProviderException, InvalidKeySpecException {
        String signature = KeyGenerater.Sign_Message(messages,this.privateKey);

        Transaction t =  new Transaction(sender,receiver,amount,fee,messages,publicKey,signature);
        return t;
    }
    public String Make_Block_Signature(String data) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, NoSuchProviderException, InvalidKeySpecException {
        return KeyGenerater.Sign_Message(data,this.privateKey);
    }

//-----------------------------------------------------------------------------------------------------------
    // mining function
//----------------------------------------------------------------------------------------------------------

    public double Mining_Mode(Block block) throws UnsupportedEncodingException, NoSuchAlgorithmException, IllegalAccessException, NoSuchPaddingException, SignatureException, InvalidKeyException, NoSuchProviderException, InvalidKeySpecException {
       double t = block.calculateHash(this);
       block.set_MerkelTree_Root(block.transactions);
       return t;
    }


//-----------------------------------------------------------------------------------------------------------
    // restoring function
//----------------------------------------------------------------------------------------------------------

    public  Boolean Save_Keystore (String path) throws IOException {

        File file = new File(path+"/keystore.txt");
        if(file.createNewFile()){
            System.out.println("Create file....");
            FileWriter writer = new FileWriter(path+"/keystore.txt");
            writer.write(this.privateKey+"\n");
            writer.write(this.publicKey);
            writer.close();
            return true;
        }else{
            System.out.println("There's already an file in the folder");
            return false;
        }

    }

    public void  Load_Keystore(String path) throws IOException, IllegalAccessException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException, InvalidKeySpecException {
        File file = new File(path+"/keystore.txt");
        String data="",data2="";
        try{
            Scanner scanner = new Scanner(file);
            data = scanner.nextLine();
            data2 = scanner.nextLine();
        }catch (Exception e){
            System.out.println("No file found");
            return ;
        }

        this.privateKey = data;
        this.publicKey = data2;

        this.address = keyGenerater.Get_Address(this.publicKey);

        System.out.println("Loading user successfully");
    }


}
