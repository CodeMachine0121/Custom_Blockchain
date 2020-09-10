package Users.Node.Anonymous_Function.Registaration;

import BlockChain.Block;
import BlockChain.Transaction;
import Users.Node.NodeMethod;
import Users.SocketAction;
import Users.UserFunctions;
import Util.KeyGenerater;
import org.json.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import static Users.SocketAction.SocketRead;

public class RegistrationMethod {



    NodeMethod nodeMethod;

    public RegistrationMethod() throws IOException, InterruptedException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, IllegalAccessException, NoSuchPaddingException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException, IllegalBlockSizeException, InvalidKeySpecException {

        nodeMethod = new NodeMethod();

        nodeMethod.Setup_ServerNode();

        nodeMethod.actions.put("commit",()->{
            try {
                this.Commit_Transaction();
            } catch (IOException | NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException | IllegalAccessException | InvalidKeyException | SignatureException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
                e.printStackTrace();
            }
        });

    }
    public void TurnOn_Node_Server() throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, IllegalAccessException, NoSuchPaddingException, BadPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, InterruptedException {
        nodeMethod.TurnOn_Node_Server();
    }
    public void Commit_Transaction() throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, IllegalAccessException, InvalidKeyException, SignatureException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        if(nodeMethod.nodeUser==null)
            System.exit(-15);

        System.out.println("註冊: "+nodeMethod.clientSocket.getInetAddress());

        // get transaction String
        String Stransaction = SocketRead(nodeMethod.clientSocket);
        Transaction t = UserFunctions.Convert2Transaction(Stransaction);

        String result="";


        // Verify the signature of transaction
        if(!Transaction.Is_transactions_valid(t)){
            System.out.println("\t\t交易簽章錯誤");
            result = "signature wrong";
        }
        else if(nodeMethod.bufferChain.get(0).transactions.size() < Block.block_limitation){

            JSONObject userData = new JSONObject();
            userData.put("User_PublicKey",t.publickey);
            userData.put("User_Signature",t.signature);
            userData.put("User_Transaction_Signature",t.messages);

            // encrypt user data
            //String  cipher_User_Data = String.join("",KeyGenerater.RSA_Encrypt(userData.toString(), KeyGenerater.Get_RSA_PublicKey(nodeMethod.nodeUser.RSA_publicKey)));


            Transaction Anonymous = nodeMethod.nodeUser.Make_Transaction(nodeMethod.nodeUser.RSA_publicKey,t.sender,t.amount,t.fee,"");

            // Add transaction to block
            nodeMethod.bufferChain.get(0).Add_Transaction(Anonymous);
            // It mean block is full, needs to be mine
        }
        else{
            System.out.println("\t\t交易已滿 等待挖掘");
            result="exceed length";
        }

        // send result
        SocketAction.SocketWrite(result, nodeMethod.clientSocket);
    }



}
