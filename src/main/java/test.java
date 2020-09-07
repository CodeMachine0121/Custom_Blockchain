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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class test {




    static Map action;
    public test() throws NoSuchAlgorithmException, IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalAccessException {
        action = new HashMap<>();
        action.put("plus",plus());
        action.put(minus(),"minus");
        action.put(time(),"time");
        action.put(divide(),"divide");

    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalAccessException, InvalidKeyException, BadPaddingException, SignatureException, IllegalBlockSizeException, InvalidKeySpecException, NoSuchProviderException {

        new test();
        System.out.println(action.get("plus"));

    }
    static int plus(){
        int a=1;
        return a;
    }
    static int minus(){
        return 1;
    }
    static int time(){
        return 2;
    }
    static int divide(){
        return 3;
    }
}
