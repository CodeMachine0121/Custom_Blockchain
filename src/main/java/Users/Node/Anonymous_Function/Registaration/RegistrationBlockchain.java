package Users.Node.Anonymous_Function.Registaration;


import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class RegistrationBlockchain {


    public static void main(String[] args) throws IOException, InterruptedException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, IllegalAccessException, NoSuchPaddingException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException, IllegalBlockSizeException, InvalidKeySpecException {
        RegistrationMethod registrationMethod = new RegistrationMethod();
        registrationMethod.TurnOn_Node_Server();
    }


}
