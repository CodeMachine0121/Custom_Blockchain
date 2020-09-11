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
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import java.awt.*;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import static Users.SocketAction.SocketRead;
import static Users.SocketAction.SocketWrite;

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

        nodeMethod.actions.put("verify",()->{
            try {
                Verify_Anonymous_CA();
            } catch (Exception e) {
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


        // Verify the amount of transaction in one block
        if(nodeMethod.bufferChain.get(0).transactions.size()>= Block.block_limitation){
            System.out.println("\t\t交易已滿 等待挖掘");
            result="exceed length";
            SocketWrite(result, nodeMethod.clientSocket);
            return;
        }
        // Verify the signature of transaction
        if(!Transaction.Is_transactions_valid(t)){
            System.out.println("\t\t交易簽章錯誤");
            result = "signature wrong";
            SocketWrite(result, nodeMethod.clientSocket);
            return ;
        }



        // 創建 匿名ID 用使用者真實key加密
        KeyGenerater AnonymousKeyGenerator = new KeyGenerater();


        JSONObject AnonymousID = new JSONObject();
        AnonymousID.put("ID",t.receiver);
        AnonymousID.put("ECDSA_PublicKey",AnonymousKeyGenerator.Get_PublicKey_String());
        AnonymousID.put("ECDSA_PrivateKey",AnonymousKeyGenerator.Get_PrivateKey_String());
        AnonymousID.put("RSA_PublicKey",AnonymousKeyGenerator.Get_RSA_PublicKey_String());
        AnonymousID.put("RSA_PrivateKey",AnonymousKeyGenerator.Get_RSA_PrivateKey_String());

        result = KeyGenerater.RSA_Encrypt(AnonymousID.toString(),KeyGenerater.Get_RSA_PublicKey(t.RSA_Publickey));


        // 要留在RBC的真實使用者資料 用匿名使用者的KEY 加密
        JSONObject userData = new JSONObject();
        userData.put("User_ECDSA_PublicKey",t.ECDSA_Publickey);
        userData.put("User_Signature",t.signature);
        userData.put("User_RSA_PublicKey",t.RSA_Publickey);
        userData.put("User_Transaction_Signature",t.messages);

        // encrypt user data
        String signature  = KeyGenerater.Sign_Message(t.RSA_Publickey,AnonymousKeyGenerator.Get_PrivateKey_String());

        // 用JSON搭配ID值封裝起來
        JSONObject UserID = new JSONObject();
        UserID.put("ID",t.receiver);
        UserID.put("Data",signature);

        Transaction Anonymous = nodeMethod.nodeUser.Make_Transaction(nodeMethod.nodeUser.ECDSA_publicKey,t.sender,t.amount,t.fee,UserID.toString());

        // Add transaction to block
        nodeMethod.bufferChain.get(0).Add_Transaction(Anonymous);

        SocketWrite(result, nodeMethod.clientSocket);

    }

    public void Verify_Anonymous_CA()throws Exception{

        JSONObject CA = new JSONObject(SocketRead(nodeMethod.clientSocket));
        String ID = CA.getString("ID");
        String server_ECDSA_PublicKey = CA.getString("ECDSA_PublicKey");
        String server_Signature = CA.getString("Signature");
        String server_RSA_PublicKey = CA.getString("RSA_PublicKey");
        String server_message = CA.getString("Message");


        boolean flag =false;
        // run throw All transaction in blockchain
        for(Block block:nodeMethod.blockchain.blockchain){
            for(Transaction t:block.transactions){
                JSONObject AnonymousData = new JSONObject(t.messages);
                if(ID.equals(AnonymousData.getString("ID"))){
                    flag = KeyGenerater.Verify_Signature(AnonymousData.getString("Data"), server_ECDSA_PublicKey,server_RSA_PublicKey);
                    break;
                }
            }
            if(flag)
                break;
        }
        if(flag){
            SocketWrite("Success",nodeMethod.clientSocket);
        }else{
            SocketWrite("Fail",nodeMethod.clientSocket);
        }

    }

}
