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






    public static void main(String[] args) throws Exception{

       String ecdsa = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEmO2cN+v/5gLCkwYwaukeVkLA5DSLd5CDrF9do42KThS4qzFB8cR/zID/xytKqGkTZM0bhAQBzcKz+tfwbddQOw==";

       String signature = "MEQCIHQxKHIWtpcbbMnkuwTnNZa4O55EI17S5LyW2FrydVwsAiBjWhwtI2LbhVL2hL06ia9jvwXThPpDG73phMJ/ymQhBQ==";
       String message = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKOKavFsR4drYL7UksT4a+Th5eGz7+e+m/r0EJEfLLIUNQNyy1U5ID+XF0QD22Pz8/GNkHdNM6VLwzbKgIvjdAcCAwEAAQ==";

        System.out.println( KeyGenerater.Verify_Signature(signature,ecdsa,message));
    }


}
