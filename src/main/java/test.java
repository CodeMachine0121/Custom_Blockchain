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


        String str= "hello";
        String cipherText = KeyGenerater.RSA_Encrypt(str,KeyGenerater.Get_RSA_PublicKey(publickey));
        System.out.println("加密:\t"+cipherText);
        System.out.println("解密:\t"+KeyGenerater.RSA_Decrypt(cipherText,KeyGenerater.Get_RSA_PrivateKey(privatekey)));


        String t= "oB/sPgZDvGXaeTdBuf+aBcZtoyorYXmHL83KWYoOBfqJrKpRPuNhzyYcV7W7UMhS6hW/iTT532KAZeRCaHZ3MQ==0qXJKeBGD9V1Ovf6nwFeL2kWgsubMJ/4oR5JiMIUb7BvSAWw+0PnIDF0JTMA69UomEsHeORgJ0hzLrMLmRJStw==toZQ2V0DOqF4SvfYyUS0JllxSJ+HftRENcj7+px45/8wfULLFSVGs6aSyLnsqwd8M22vBKmzysgTJJOxjbwHVw==IkZpArVTcrG5qQV202gEeJeLA2z/Vz77QoOiNq13e/Jv6oU08Rs/2+/NpjNmR7xGRacH6DRSikLDsw4gtscaQg==Wpij2QmmYgI94mQM0x8eVPWHqQVGusWJ8VsP06D3fiJkd8Ax4WF0R63O1K6azQjGw9YJTtT/l8IkdIYd5O4xvA==l0B+oacrujP+MzSp+G/nBL2K9nMEt6C5OLL9hZz7Frsn7Wdg1fuNoF6CrSlr/ukXLATe4K4O66Rl/fPBTqp2JA==";
        System.out.println(KeyGenerater.RSA_Decrypt(t,KeyGenerater.Get_RSA_PrivateKey("MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEA2wZ56eGDmkSLeekjOCYS07L8gKZWczRo0gc7kpXyoUPoLvNFe6HsqNPFmg40q1+NRhWQMhuyUNwgrkXvshTBYwIDAQABAkAX1PWRAEvMjmbKxhZh9qqXxGL7MJ45fNtm9wiBY7V53HEvGrU8BfDxYzYPF6y6+zG1oHNykOao9ICY4BbT06g5AiEA7+crRdUQlxNaLawFYXCnV+3HbkPPwnC5+6XSUZsUPS8CIQDpuLB45Kruk362CebxN8dMHJkyKCd9pe8u7YCWhJ86DQIhAOC+nBdrp4MgqhanVNMYCm2hYHe4J2Zs3I8XpZS1x5wZAiAUuEzcytjkgarwFHym0d0XQEnMiobFutozfNazgVpkWQIgCeddImXGnHO+76SywlgGrtLkQpbAPANYIYT7JB6j8nI=")));
    }


}
