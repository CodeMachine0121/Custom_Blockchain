import BlockChain.Miner;
import BlockChain.Transaction;
import Util.KeyGenerater;
import Util.StringUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

public class test {

    public test() throws NoSuchAlgorithmException, IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalAccessException {

    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalAccessException, InvalidKeyException, BadPaddingException, SignatureException, IllegalBlockSizeException, InvalidKeySpecException, NoSuchProviderException {


        KeyGenerater keyGenerater = new KeyGenerater();

        PublicKey publicKey = keyGenerater.Get_PublicKey();



        System.out.print("Public key:\t");
        System.out.println(keyGenerater.Get_PublicKey_String());
        System.out.print("Public key binary:\t");
        System.out.println(new BigInteger( 1,keyGenerater.Get_PublicKey_Bytes()).toString(2) );


        PrivateKey privateKey = keyGenerater.Get_PrivateKey();
        System.out.print("Private Key:\t");
        System.out.println(keyGenerater.Get_PrivateKey_String());
        System.out.print("Private Key binary:\t");
        System.out.println(new BigInteger( 1,keyGenerater.Get_PrivateKey_Bytes()).toString(2));


        String message = "hello";
        String signature = KeyGenerater.Sign_Message(message,keyGenerater.Get_PrivateKey_String());

        System.out.print("Verify signature:\t");
        System.out.println(KeyGenerater.Verify_Signature(signature,keyGenerater.Get_PublicKey_String(),message));

    }
}
