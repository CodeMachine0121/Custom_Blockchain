package Util;

import java.awt.image.Kernel;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class KeyGenerater {

    private KeyPair keypair;
    private PrivateKey privateKey ;
    private PublicKey publicKey;


    public KeyGenerater(){
        this.keypair = Get_KeyPair();
        publicKey = keypair.getPublic();
        privateKey = keypair.getPrivate();
    }

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



}
