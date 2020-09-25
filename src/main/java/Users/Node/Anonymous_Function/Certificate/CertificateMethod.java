package Users.Node.Anonymous_Function.Certificate;

import BlockChain.Block;
import BlockChain.Blockchain;
import BlockChain.Transaction;
import Users.Node.NodeMethod;
import Users.Node.NodeUser;
import Users.SocketAction;
import Users.UserFunctions;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import static Users.SocketAction.*;

public class CertificateMethod {
    NodeMethod nodeMethod;
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

        nodeMethod = new NodeMethod();
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

        nodeMethod.actions.put("Get_CBC_Node_List",()->{
            try {
                Send_CBC_Node_List();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
    public void TurnOn_Node_Server() throws Exception{

        nodeMethod.TurnOn_Node_Server();
    }

    public void Test_connection_RBC()throws Exception{
        Socket socket = new Socket(RBCNode,8000);
        SocketWrite("Test_for_CBC",socket);
        socket.close();
    }

    public void Send_CBC_Node_List() throws IOException, InterruptedException {

        // parse node list to string
        StringBuilder str_nodeList = new StringBuilder();
        for(String node:nodeMethod.consensus.nodeList){
            str_nodeList.append(node);
            str_nodeList.append("-");
        }
        SocketWrite(str_nodeList.toString(), nodeMethod.clientSocket);
        Thread.sleep(TIME_DELAY);

    }


    public void Save_Anonymous_Data() throws Exception{
        // receive transaction
        String strTransaction = SocketRead(nodeMethod.clientSocket);
        System.out.println("transaction\n"+strTransaction);
        Transaction t = UserFunctions.Convert2Transaction(strTransaction);

        // add Transaction to buffer chain
        nodeMethod.bufferChain.get(0).Add_Transaction(t);

        String nodeAddress = nodeMethod.clientSocket.getInetAddress().toString().split("/")[1];
        if (!RBC_nodes_List.contains(nodeAddress)){
            System.out.println("node: "+nodeAddress);
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
    public static String Request_Search_AnonymousID(String remoteHost,String CA) throws Exception{

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
    public void Revoke_Anonymous_CA(String str_Anonymous_CA,String signature)throws Exception{


         /* Anonymous CA 內容
         *  ID
         *  ECDSA_PublicKey
         *  ECDSA_PrivateKey
         *  RSA_PublicKey
         *  RSA_PrivateKey

         * Transaction 內容
         *   UserID:
         *       USER_ECDSA_PublicKey
         *       USER_RSA_PublicKey
         *       USER_Signature
         *       USER_Signature_Message
         **/

        JSONObject AnonymousCA = new JSONObject(str_Anonymous_CA);





    }





}
