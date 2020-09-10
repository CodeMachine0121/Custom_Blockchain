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


        String t= "x00nx5dizhpNFE0y9JudlXDFqPGIoLNwZXc2cP4ttbn8xIOxrwHOLLR8OYwIdXFA/u/CqO1DyY/gHSMbASOZZA==ZZ7bPi6b5r88awv5XnNzEZScyRidmynkozEHOBemoktk7zK52zm/yxSmalBnN1KVbPIMjwzIsV1r9mXc5Li0QA==kGcVcvxLmqJnYwh+0o9tNvVsl+nS0ZfBzhZ0UkkMH8N0sQqaZYbGXLKRWAtlY+CrvJqoF4L+cNCkS0snTtYetQ==HweD63SAeCW+MLqzJmsnewl0MvIz5m89IVFx9r7eXpzccejqW6R6hXAeljnd+4J2Ij3ACvuFe7m2kKtyhSD2oA==fjIqW9U1Mf0cVxvWRrB3+e+NXt7JKmmoRAc3tOZlmOz5flB1aCm6c+OMCWgjqj5/GScY0rbHnmUm486tioUqQw==0m51CHst/hRIRhcT/+vRoejCD2N/cFp8MwxkU2Ay1E1iDdMOCg7VCnQAKW5WxAMFitNf0DaVmlv8X7ham7tr3w==";

        System.out.println("解密:\t"+KeyGenerater.RSA_Decrypt(t,KeyGenerater.Get_RSA_PrivateKey("MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEA2wZ56eGDmkSLeekjOCYS07L8gKZWczRo0gc7kpXyoUPoLvNFe6HsqNPFmg40q1+NRhWQMhuyUNwgrkXvshTBYwIDAQABAkAX1PWRAEvMjmbKxhZh9qqXxGL7MJ45fNtm9wiBY7V53HEvGrU8BfDxYzYPF6y6+zG1oHNykOao9ICY4BbT06g5AiEA7+crRdUQlxNaLawFYXCnV+3HbkPPwnC5+6XSUZsUPS8CIQDpuLB45Kruk362CebxN8dMHJkyKCd9pe8u7YCWhJ86DQIhAOC+nBdrp4MgqhanVNMYCm2hYHe4J2Zs3I8XpZS1x5wZAiAUuEzcytjkgarwFHym0d0XQEnMiobFutozfNazgVpkWQIgCeddImXGnHO+76SywlgGrtLkQpbAPANYIYT7JB6j8nI=")));

    }


}
