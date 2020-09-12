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

        for(int i=1;i<10;i++){
            for(int k=1;k<10;k++){
                if(k==5)
                    break;
                System.out.println(k);
            }
        }

    }


}
