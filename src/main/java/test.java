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

       PublicKey ecdsa = KeyGenerater.Get_PublicKey("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEmO2cN+v/5gLCkwYwaukeVkLA5DSLd5CDrF9do42KThS4qzFB8cR/zID/xytKqGkTZM0bhAQBzcKz+tfwbddQOw==");

       String message = "";
    }


}
