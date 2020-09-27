package Users.Node.Anonymous_Function.Certificate;

import BlockChain.Block;
import BlockChain.Blockchain;
import BlockChain.Transaction;
import Users.Node.NodeMethod;
import Users.Node.NodeUser;
import Users.SocketAction;
import Users.UserFunctions;
import Util.KeyGenerater;
import Util.StringUtil;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;

import static Users.SocketAction.*;

public class CertificateMethod {
    NodeMethod nodeMethod=new NodeMethod();
    String RBCNode;
    Scanner scanner = new Scanner(System.in);

    List<String> RBC_nodes_List = new LinkedList<>();


    public CertificateMethod() throws Exception{
        System.out.print("輸入RBC節點IP:\t");
        RBCNode = scanner.nextLine();
        Test_connection_RBC();

        if(!TestConnection(RBCNode)){
            System.out.println("連線失敗");
            System.exit(-15);
        }


        nodeMethod.Setup_ServerNode();

        nodeMethod.actions.put("verifyCA",()->{
            try {
                VerificationCertificate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        nodeMethod.actions.put("SaveData",()->{
            try{
                Save_Anonymous_Data();
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        nodeMethod.actions.put("get-CBC",()->{
            try {
                Get_CBC_Node_List_String();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        nodeMethod.actions.put("revoke",()->{
            try {
                RevokeCA();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    public void TurnOn_Node_Server() throws Exception{

        nodeMethod.TurnOn_Node_Server();
    }

    public void Test_connection_RBC()throws Exception{
        Socket socket = new Socket(RBCNode,SERVER_PORT);
        SocketWrite("Test_for_CBC",socket);
    }


    private void Get_CBC_Node_List_String() throws Exception {

        // parse node list to string
        StringBuilder str_nodeList = new StringBuilder();
        List<String> nodes = nodeMethod.consensus.nodeList;

        for(String node:nodes){
            str_nodeList.append(node);
            str_nodeList.append("-");
        }

        SocketWrite(str_nodeList.toString(), nodeMethod.clientSocket);
    }


    public void Save_Anonymous_Data() throws Exception{
        // receive transaction from RBC
        JSONObject data = new JSONObject(SocketRead(nodeMethod.clientSocket));

        Transaction t = nodeMethod.nodeUser.Make_Transaction(nodeMethod.host,data.getString("ID"),0,0,data.toString());

        // add Transaction to buffer chain
        nodeMethod.bufferChain.get(0).Add_Transaction(t);

        String nodeAddress = nodeMethod.clientSocket.getInetAddress().toString().split("/")[1];

        if (!RBC_nodes_List.contains(nodeAddress)){
            System.out.println("RBC node: "+nodeAddress+" 加入清單");
            RBC_nodes_List.add(nodeAddress);
        }

    }

    public void VerificationCertificate() throws Exception{
        if(nodeMethod.nodeUser == null)
            System.exit(-15);
        System.out.println("審核Certificate:\t"+nodeMethod.clientSocket.getInetAddress());


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

       // JSONObject CA = new JSONObject(t.messages);
        try {
            result = Request_Search_AnonymousID(RBCNode,t.messages);
        }catch (SocketException e){
            System.out.println("分支連線至RBC");
        }
        System.out.println("審核結果:\t"+result);

        SocketWrite(result, nodeMethod.clientSocket);
        nodeMethod.clientSocket.close();

    }



    // CBC -> RBC
    // 驗證CA
    private String Request_Search_AnonymousID(String remoteHost,String CA) throws Exception{

        Socket socket = new Socket(remoteHost,SocketAction.SERVER_PORT);
        Thread.sleep(SocketAction.TIME_DELAY);

        // send command
        SocketWrite("verifyCA",socket);
        Thread.sleep(TIME_DELAY);
        // send CA
        SocketWrite(CA,socket);
        Thread.sleep(TIME_DELAY);
        String result = SocketRead(socket);


        socket.close();
        return result;
    }



    // CBC-> wallet

    private  void RevokeCA() throws  Exception{

        // get ID from wallet
        String ID = SocketRead(nodeMethod.clientSocket);

        // to find ID in chain
        Transaction ID_txn = Find_Data_by_ID(ID);
        if(ID_txn==null){
            SocketWrite("noExist", nodeMethod.clientSocket);
            Thread.sleep(TIME_DELAY);
            return;
        }
        SocketWrite("pass", nodeMethod.clientSocket);
        Thread.sleep(TIME_DELAY);


        // verify identify of current user
        String signature = SocketRead(nodeMethod.clientSocket);
        // get public key in transaction
        JSONObject msg = new JSONObject(ID_txn.messages);
        String publickey = msg.getString("Anonymous_ECDSA_PublicKey");
        String ID_hash = StringUtil.applyHASH(ID,"SHA-256");

        if(!KeyGenerater.Verify_Signature(signature,publickey,ID_hash )){
            System.out.println("簽章辨識錯誤");
            SocketWrite("errorVerify", nodeMethod.clientSocket);
            Thread.sleep(TIME_DELAY);
            return ;
        }

        // request RBC to update status
        if(Update_CA_Status()){
            SocketWrite("revoked", nodeMethod.clientSocket);
            Thread.sleep(TIME_DELAY);
        }
    }

    private Transaction Find_Data_by_ID(String ID){
        for (Block block: nodeMethod.blockchain.blockchain) {
            for(Transaction t: block.transactions){
                if(t.receiver.equals(ID)){
                    return t;
                }
            }
        }
        return null;
    }

    private boolean Update_CA_Status()throws Exception{

        Socket socket = new Socket(RBCNode,SERVER_PORT);

        // send command to RBC
        SocketWrite("revoke",socket);
        Thread.sleep(TIME_DELAY);

        String response = SocketRead(socket);

        socket.close();

        if(!response.equals("pass")){
            System.out.println(response);
            return false;
        }
        System.out.println("RBC 完成註銷");
        return true;
    }
}
