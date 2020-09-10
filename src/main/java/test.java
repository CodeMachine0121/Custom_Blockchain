import BlockChain.Miner;
import BlockChain.Transaction;
import Util.KeyGenerater;
import Util.StringUtil;

import javax.crypto.*;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class test {






    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalAccessException, InvalidKeyException, BadPaddingException, SignatureException, IllegalBlockSizeException, InvalidKeySpecException, NoSuchProviderException {

        KeyGenerater keyGenerater = new KeyGenerater();

        String publickey = keyGenerater.Get_RSA_PublicKey_String();
        PublicKey publicKey = KeyGenerater.Get_RSA_PublicKey(publickey);


        System.out.println("public key:\t"+publickey);


        String privatekey = keyGenerater.Get_RSA_PrivateKey_String();
        PrivateKey privateKey = KeyGenerater.Get_RSA_PrivateKey(privatekey);
        System.out.println("private key:\t"+ privatekey);


        String str= "01234567890123456789012345678901345678901234567890123456789";
        String cipherText = KeyGenerater.RSA_Encrypt(str,KeyGenerater.Get_RSA_PublicKey(publickey));
        System.out.println("加密:\t"+String.join("",cipherText));
        System.out.println("解密:\t"+KeyGenerater.RSA_Decrypt(String.join("",cipherText),KeyGenerater.Get_RSA_PrivateKey(privatekey)));
    }


}
