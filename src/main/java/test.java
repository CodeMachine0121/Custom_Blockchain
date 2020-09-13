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


       String ecdsa = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEW8FwCR2UKxN1Je0hbZhTbIcdWwGYryJF1d4gIECDift9r4ObDSL8beZSE8YGFZgAL9T/cg+SBMOS2D4ohvaAiw==";

       String signature = "MEUCIQC4bulRCYjqyZUyUH0953i/kBMWt8zhspBkuY7O/sU43wIgbBBR4eaPJD88N8pdaLBYvrSYip8ivsfG28mYIwznJhQ=";
       String message = "{\"ECDSA_PublicKey\":\"MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEW8FwCR2UKxN1Je0hbZhTbIcdWwGYryJF1d4gIECDift9r4ObDSL8beZSE8YGFZgAL9T/cg+SBMOS2D4ohvaAiw==\",\"RSA_PublicKey\":\"MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIGi7FFqGGyy/vJhqy2+81N+CSCKiulAfsfsATXofUwOjgVPBoWFknr2DDKtpuAoH+EzJNsMubl1k2vsmPsl9kkCAwEAAQ==\",\"RSA_PrivateKey\":\"MIIBUwIBADANBgkqhkiG9w0BAQEFAASCAT0wggE5AgEAAkEAgaLsUWoYbLL+8mGrLb7zU34JIIqK6UB+x+wBNeh9TA6OBU8GhYWSevYMMq2m4Cgf4TMk2wy5uXWTa+yY+yX2SQIDAQABAkA9JFiNNZr7eZjhPMt9sfgVwvYPEQjfZnnlVRxlDpg6DF7+pPvNTsMJeem7CVgPoekvopjap0sWuudo05QuryLZAiEAxG7ggYJDDhbGKJkNkKxM47t5mi1e+tG/Asrr3NNGbY8CIQCo8pydNezT3VA2YVTLE2QINKrg2glU2CBTitTuSyCipwIgJ23mLLjcAuD5Z77JJUggqvm/v/3peclOYR4gWBQkp+sCIGX3V3LC0xbjob/Qap7OKzeQoflnXu5oKsqBJhZUHXRNAiBZuMkd7j/xU4uQjfyt10Z5FNAGCbvNcqbV6VWGkhIH1A==\",\"ID\":\"ksz54213\",\"ECDSA_PrivateKey\":\"MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCACRol5VHt5uvl3vryjpuR1AQJhlC35xhwWrvbCvhwoCQ==\"}";

       String privatekey = "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCACRol5VHt5uvl3vryjpuR1AQJhlC35xhwWrvbCvhwoCQ==";
        System.out.println(message.equals(KeyGenerater.Sign_Message(message,privatekey)));

        System.out.println( KeyGenerater.Verify_Signature(signature,ecdsa,message));
    }


}
