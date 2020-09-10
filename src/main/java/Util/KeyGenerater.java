package Util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

public class KeyGenerater {

    private final KeyPair keypair;
    private final PrivateKey privateKey ;
    private final PublicKey publicKey;


    public KeyGenerater() throws NoSuchAlgorithmException {
        this.keypair = Get_KeyPair();
        this.publicKey = keypair.getPublic();
        this.privateKey = keypair.getPrivate();


        this.RSA_Keypair = Get_RSA_KeyPair();
        this.RSA_PublicKey = this.RSA_Keypair.getPublic();
        this.RSA_PrivateKey = this.RSA_Keypair.getPrivate();
    }


//-----------------------------------------------------------------------------------------------------------
    // ECDSA encryption (簽章)
//----------------------------------------------------------------------------------------------------------

    private KeyPair Get_KeyPair(){
        try{
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
            ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("secp256k1");
            keyPairGenerator.initialize(ecGenParameterSpec,new SecureRandom());
            keyPairGenerator.initialize(256);
            return keyPairGenerator.genKeyPair();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public PublicKey Get_PublicKey(){
        return publicKey;
    }
    public String Get_PublicKey_String(){
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }
    public byte[] Get_PublicKey_Bytes(){
        return Base64.getDecoder().decode(this.Get_PublicKey_String());
    }

    public static PublicKey Get_PublicKey(String publicKeyString) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory kf = KeyFactory.getInstance("EC");

        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString));

        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        return publicKey;
    }
    public static String Get_Address(String publicKeyString) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return StringUtil.apply_RIPEMD160( StringUtil.applyHASH(StringUtil.apply_RIPEMD160(publicKeyString),"SHA-256") );
    }


    public PrivateKey Get_PrivateKey(){
        return privateKey;
    }
    public String Get_PrivateKey_String(){
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }
    public static PrivateKey Get_PrivateKey(String privateKeyString) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
       KeyFactory kf = KeyFactory.getInstance("EC");
       byte[] bytes = Base64.getDecoder().decode(privateKeyString);

       return kf.generatePrivate(new PKCS8EncodedKeySpec(bytes));
    }
    public byte[] Get_PrivateKey_Bytes(){
        return Base64.getDecoder().decode(this.Get_PrivateKey_String());
    }



    // 可單獨使用
    public static String Sign_Message(String message,String privateKeyString) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException, NoSuchProviderException, InvalidKeySpecException {

        Signature ecdsaSign = Signature.getInstance("SHA256withECDSA");
        PrivateKey privateKey = Get_PrivateKey(privateKeyString);
        ecdsaSign.initSign(privateKey);

        ecdsaSign.update(message.getBytes("UTF-8"));
        byte[] signature = ecdsaSign.sign();
        String sig = Base64.getEncoder().encodeToString(signature);

        return sig;
    }
    public static boolean Verify_Signature(String signature,String publicKeyString,String message) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, UnsupportedEncodingException, SignatureException, NoSuchProviderException {

        Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA");
        PublicKey publicKey = Get_PublicKey(publicKeyString);

        ecdsaVerify.initVerify(publicKey);
        ecdsaVerify.update(message.getBytes("UTF-8"));

        return ecdsaVerify.verify(Base64.getDecoder().decode(signature));
    }

//-----------------------------------------------------------------------------------------------------------
    // RSA encryption
//----------------------------------------------------------------------------------------------------------


    private final KeyPair RSA_Keypair;
    private final PrivateKey RSA_PrivateKey ;
    private final PublicKey RSA_PublicKey;

    private KeyPair Get_RSA_KeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator= KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(512);
        return keyPairGenerator.generateKeyPair();
    }

    public PublicKey Get_RSA_PublicKey(){
        return this.RSA_PublicKey;
    }
    public String Get_RSA_PublicKey_String(){
        return Base64.getEncoder().encodeToString(this.RSA_PublicKey.getEncoded());
    }
    public static PublicKey Get_RSA_PublicKey(String publicKeyString) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        byte[] bytes = Base64.getDecoder().decode(publicKeyString);

        return kf.generatePublic(new X509EncodedKeySpec(bytes));
    }

    public PrivateKey Get_RSA_PrivateKey(){return this.RSA_PrivateKey;}
    public String Get_RSA_PrivateKey_String(){return Base64.getEncoder().encodeToString(this.RSA_PrivateKey.getEncoded());}
    public static PrivateKey Get_RSA_PrivateKey(String privateKeyString) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        byte[] bytes = Base64.getDecoder().decode(privateKeyString);

        return kf.generatePrivate(new PKCS8EncodedKeySpec (bytes));
    }


    public static String RSA_Encrypt(String message, PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE,publicKey);


        List<String> splitText = splitText(message,Cipher.ENCRYPT_MODE);

        List<String> encrypText = new LinkedList<>();
        for(String msg: splitText){
            // Byte 分割 -> Base64
            byte[] byteCipherText = cipher.doFinal(msg.getBytes());
            encrypText.add(Base64.getEncoder().encodeToString(byteCipherText));
        }


        return String.join("",encrypText);
    }

    public static String RSA_Decrypt(String encryptText,PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE,privateKey);

        List<String> Context = new LinkedList<>();

        List<String> encryptText_List = splitText(encryptText,Cipher.DECRYPT_MODE);

        for(String cText:encryptText_List){
            byte[] byteEncryptText = Base64.getDecoder().decode(cText);
            byte[] byteContext = cipher.doFinal(byteEncryptText);

            Context.add(new String(byteContext));
        }

        return String.join("",Context);
    }


    private static List<String> splitText(String message,int mode){
        final int SIZE=(mode == Cipher.ENCRYPT_MODE)? 50 : 88;

        char[] charMessage = message.toCharArray();

        List<String> splitedStr = new LinkedList<>();

        StringBuilder tmp = new StringBuilder();

        for(int i=0;i<charMessage.length;i++){
            if(i%(SIZE)==0 && i!=0){
                splitedStr.add(tmp.toString());
                tmp = new StringBuilder();
                tmp.append(charMessage[i]);
            }else{
                tmp.append(charMessage[i]);
            }
        }splitedStr.add(tmp.toString());


        return splitedStr;
    }


}
