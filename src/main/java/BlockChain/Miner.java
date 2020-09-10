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


    public String address;
    public String ECDSA_publicKey;
    private String ECDSA_privateKey;
    public String RSA_publicKey;
    private String RSA_privateKey;

    public double balance;




    public Miner() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalAccessException, NoSuchPaddingException, IOException {

        KeyGenerater keyGenerater = new KeyGenerater();

        this.ECDSA_publicKey = keyGenerater.Get_PublicKey_String();
        this.ECDSA_privateKey = keyGenerater.Get_PrivateKey_String();

        this.RSA_publicKey = keyGenerater.Get_RSA_PublicKey_String();
        this.RSA_privateKey = keyGenerater.Get_RSA_PrivateKey_String();


        this.address = KeyGenerater.Get_Address(this.ECDSA_publicKey);
        this.balance = -1;



    }

    public String getRSA_privateKey() {
        return RSA_privateKey;
    }

    //-----------------------------------------------------------------------------------------------------------
    // transaction function
//----------------------------------------------------------------------------------------------------------
    public Transaction Make_Transaction(String sender, String receiver, double amount, double fee, String messages) throws NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException, IllegalBlockSizeException, InvalidKeyException, SignatureException, BadPaddingException, IllegalAccessException, NoSuchProviderException, InvalidKeySpecException {
        String signature = KeyGenerater.Sign_Message(messages,this.ECDSA_privateKey);

        Transaction t =  new Transaction(sender,receiver,amount,fee,messages,this.ECDSA_publicKey,signature,this.RSA_publicKey);
        return t;
    }
    public String Make_Block_Signature(String data) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, NoSuchProviderException, InvalidKeySpecException {
        return KeyGenerater.Sign_Message(data,this.ECDSA_privateKey);
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
            writer.write(this.ECDSA_privateKey+"\n");
            writer.write(this.ECDSA_publicKey+"\n");
            writer.write(this.RSA_privateKey+"\n");
            writer.write(this.RSA_publicKey+"\n");

            writer.close();
            return true;
        }else{
            System.out.println("There's already an file in the folder");
            return false;
        }

    }

    public void  Load_Keystore(String path) throws IOException, IllegalAccessException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException, InvalidKeySpecException {
        File file = new File(path+"/keystore.txt");
        String data="",data2="",data3="",data4="";
        try{
            Scanner scanner = new Scanner(file);
            data = scanner.nextLine();
            data2 = scanner.nextLine();
            data3 = scanner.nextLine();
            data4 = scanner.nextLine();
        }catch (Exception e){
            System.out.println("No file found");
            return ;
        }

        this.ECDSA_privateKey = data;
        this.ECDSA_publicKey = data2;
        this.RSA_privateKey = data3;
        this.RSA_publicKey = data4;

        this.address = KeyGenerater.Get_Address(this.ECDSA_publicKey);
        System.out.println("Loading user successfully");
    }


}
