package Users.Node;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class NodeUser{

    public NodeUser() throws IOException, InterruptedException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, IllegalAccessException, NoSuchPaddingException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException, IllegalBlockSizeException, InvalidKeySpecException {

    }

    public static void main(String[] args) throws IOException, NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalAccessException, BadPaddingException, SignatureException, InvalidAlgorithmParameterException, IllegalBlockSizeException, InterruptedException, InvalidKeySpecException, NoSuchProviderException {

        NodeMethod  nodeMethod = new NodeMethod();
        nodeMethod.Setup_ServerNode();
        nodeMethod.TurnOn_Node_Server();
    }


}