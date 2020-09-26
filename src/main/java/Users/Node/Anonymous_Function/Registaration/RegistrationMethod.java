package Users.Node.Anonymous_Function.Registaration;

import BlockChain.Block;
import BlockChain.Transaction;
import Users.Node.NodeMethod;
import Users.UserFunctions;
import Util.KeyGenerater;
import org.json.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.List;

import static Users.SocketAction.*;

public class RegistrationMethod {



    NodeMethod nodeMethod;
    // 紀錄 憑證狀態
    Dictionary<String,Integer> domain_Status = new Hashtable<>();
    // 存放CBC節點的表
    List<String> CBC_Nodes = new LinkedList<>();

    public RegistrationMethod() throws IOException, InterruptedException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, IllegalAccessException, NoSuchPaddingException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException, IllegalBlockSizeException, InvalidKeySpecException {

        nodeMethod = new NodeMethod();

        nodeMethod.Setup_ServerNode();

        nodeMethod.actions.put("registerCA",()->{
            try {
                RegisterAnonymousCA();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        nodeMethod.actions.put("verifyCA",()->{
            try {
                Verify_Anonymous_CA();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        nodeMethod.actions.put("Test_for_CBC",()->{
            try {
                this.Test_Connection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    public void TurnOn_Node_Server() throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, IllegalAccessException, NoSuchPaddingException, BadPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, InterruptedException {
        nodeMethod.TurnOn_Node_Server();
    }
    // 測試連線 紀錄單一CBC節點
    String CBC_Single_node="";
    public void Test_Connection() throws Exception{
        System.out.println("CBC Node: "+nodeMethod.clientSocket.getInetAddress()+" 新增節點");
        CBC_Single_node = nodeMethod.clientSocket.getInetAddress().toString().split("/")[1];
        //CBC_Nodes.add(CBC_Single_node);

        // Get CBC LIST
        // receive string list
        String strnodeList = SocketRead(nodeMethod.clientSocket);
        // parse list
        CBC_Nodes =  Arrays.asList(strnodeList.split("_"));

        System.out.println("目前清單 CBC: ");
        CBC_Nodes.forEach(node-> System.out.println("node: "+node));

    }
    // 取得刷新 CBC全部節點
    public void Update_CBC_Node_List() throws Exception{
        Socket socket = new Socket(CBC_Single_node,SERVER_PORT);
        // send command
        SocketWrite("Get_CBC_Node_List",socket);
        // receive string list
        String strnodeList = SocketRead(socket);
        // parse list
        CBC_Nodes =  Arrays.asList(strnodeList.split("-"));

        System.out.println("目前清單 CBC: ");
        CBC_Nodes.forEach(node-> System.out.println("node: "+node));

        socket.close();
    }

    public void RegisterAnonymousCA() throws Exception {
        if(nodeMethod.nodeUser==null)
            System.exit(-15);

        System.out.println("註冊: "+nodeMethod.clientSocket.getInetAddress());

        // get transaction String from wallet
        String Stransaction = SocketRead(nodeMethod.clientSocket);
        Transaction t = UserFunctions.Convert2Transaction(Stransaction);

        // verify signature
        String signature = SocketRead(nodeMethod.clientSocket);
        if(!KeyGenerater.Verify_Signature(signature,t.ECDSA_Publickey,"registerCA") ){
            System.out.println("\t申請簽章錯誤");
            SocketWrite("簽章錯誤: 申請簽章錯誤", nodeMethod.clientSocket);
            return;
        }else {
            System.out.println("\t簽章正確");
            SocketWrite("pass", nodeMethod.clientSocket);
        }

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
        Thread.sleep(TIME_DELAY);


        // 創建 匿名ID 用使用者真實key加密
        KeyGenerater AnonymousKeyGenerator = new KeyGenerater();


        JSONObject AnonymousID = new JSONObject();
        AnonymousID.put("ID",t.receiver);
        AnonymousID.put("ECDSA_PublicKey",AnonymousKeyGenerator.Get_PublicKey_String());
        AnonymousID.put("ECDSA_PrivateKey",AnonymousKeyGenerator.Get_PrivateKey_String());
        AnonymousID.put("RSA_PublicKey",AnonymousKeyGenerator.Get_RSA_PublicKey_String());
        AnonymousID.put("RSA_PrivateKey",AnonymousKeyGenerator.Get_RSA_PrivateKey_String());

        // 把 Anonymous CA 加密傳過去給使用者
        result = KeyGenerater.RSA_Encrypt(AnonymousID.toString(),KeyGenerater.Get_RSA_PublicKey(t.RSA_Publickey));
        SocketWrite(result, nodeMethod.clientSocket);
        Thread.sleep(TIME_DELAY);

        // 要留在RBC的真實使用者資料 用匿名使用者的KEY
        JSONObject userData = new JSONObject();
        userData.put("User_ECDSA_PublicKey",t.ECDSA_Publickey);
        userData.put("User_Signature",t.signature);
        userData.put("User_RSA_PublicKey",t.RSA_Publickey);
        userData.put("User_Transaction_Signature",t.messages);

        // 用匿名私鑰 對 UserData 做簽章 存在RBC裡面
        String Anonymous_Signature  = KeyGenerater.Sign_Message(userData.toString(),AnonymousKeyGenerator.Get_PrivateKey_String());

        // 用JSON搭配ID值封裝起來
        JSONObject UserID = new JSONObject();
        UserID.put("ID",t.receiver);
        UserID.put("Signature",Anonymous_Signature);
        UserID.put("UserData",userData.toString());

        // 啟動憑證
        domain_Status.put(t.receiver,1);

        // 把 USERID 放進 交易訊息中
        Transaction Anonymous = nodeMethod.nodeUser.Make_Transaction(nodeMethod.nodeUser.ECDSA_publicKey,t.sender,t.amount,t.fee,UserID.toString());

        // Add transaction to block
        nodeMethod.bufferChain.get(0).Add_Transaction(Anonymous);

        try{
            // CBC 也需要存放
            JSONObject Data_CBC = new JSONObject();
            Data_CBC.put("ID",t.receiver);
            Data_CBC.put("Anonymous_ECDSA_PublicKey",AnonymousKeyGenerator.Get_PublicKey_String());
            Data_CBC.put("Anonymous_RSA_PublicKey",AnonymousKeyGenerator.Get_RSA_PublicKey_String());

            // 用匿名私鑰 做簽章
            String sign = KeyGenerater.Sign_Message(t.receiver,AnonymousKeyGenerator.Get_PrivateKey_String());
            Data_CBC.put("Signature",sign);


            // send anonymous data to CBC
            Send_AnonymousCA_to_CBC(Data_CBC);

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("未與CBC連結");
        }
    }
    private void Send_AnonymousCA_to_CBC(JSONObject data) throws Exception{


        for(String node: CBC_Nodes){

            Socket socket = new Socket(node,SERVER_PORT);

            // send command
            String command = "SaveData";
            SocketWrite(command, socket);
            Thread.sleep(TIME_DELAY);

            // send data of Anonymous CA null
            SocketWrite(data.toString(),socket);
            Thread.sleep(TIME_DELAY);

            socket.close();
        }


    }
    public void Verify_Anonymous_CA()throws Exception{

        //  從 CBC收到的匿名CA
        String strCA = SocketRead(nodeMethod.clientSocket);
        JSONObject CA = new JSONObject(strCA);
        System.out.println(strCA);
        String ID = CA.getString("ID");



        boolean flag =false;
        if(nodeMethod.blockchain.blockchain.size() == 1){
            System.out.println("CA 資料庫尚未建起");
            SocketWrite("Fail",nodeMethod.clientSocket);
            return;
        }


        // run throw All transaction in blockchain
        for(Block block:nodeMethod.blockchain.blockchain){
            for(Transaction t:block.transactions){
                //System.out.println(t.messages);
                JSONObject UserID;
                try{
                    UserID = new JSONObject(t.messages);
                }catch (Exception e){
                    continue;
                }

                if(ID.equals(UserID.getString("ID"))){
                    System.out.println("取得: ");
                    String signature = UserID.getString("Signature");
                    String message = UserID.getString("UserData");

                    String publickey = CA.getString("ECDSA_PublicKey");

                    // 檢驗簽章
                    flag = KeyGenerater.Verify_Signature(signature,publickey,message);

                    if(flag){
                        break;
                    }

                }
            }
            if(flag)
                break;
        }
        System.out.println("完成檢驗: "+flag);
        if(flag){
            if(domain_Status.get(ID) == 0){
                System.out.println("out of date");
                SocketWrite("out of date",nodeMethod.clientSocket);
            }else{
                SocketWrite("Success",nodeMethod.clientSocket);
            }
        }else{
            SocketWrite("Fail",nodeMethod.clientSocket);
        }

        nodeMethod.clientSocket.close();

    }


}
