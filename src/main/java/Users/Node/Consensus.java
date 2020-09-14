package Users.Node;

import BlockChain.Block;
import BlockChain.Blockchain;
import Users.UserFunctions;

import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static Users.SocketAction.SocketRead;
import static Users.SocketAction.SocketWrite;

public class Consensus {
    // 連線其他節點取得區塊練
    List<String> nodeList = new LinkedList<>();
    public ArrayList<Block> Request_to_Node_for_Blockchain(Blockchain blockchain) throws IOException, IllegalAccessException, NoSuchAlgorithmException {

        for(String address:nodeList){

            Socket  socket  = new Socket(InetAddress.getByName(address),8000);

            // send command
            SocketWrite("Request_Node",socket);

            // get Block size
            String strBlockSize = SocketRead(socket);
            int blockSize = Integer.parseInt(strBlockSize);

            // compare with own blockchain
            if(blockchain.blockchain.size() > blockSize){
                SocketWrite("client longer",socket);
                // send own blockchain to server
                SocketWrite(blockchain.get_All_Blocks_JSON(),socket);
                return blockchain.blockchain;

            }else if(blockchain.blockchain.size() < blockSize){
                SocketWrite("client shorter",socket);

                // Receive blockchain from server
                String strBlockchain = SocketRead(socket);
                return UserFunctions.Convert2Blockchain(strBlockchain,blockSize);

            }else if(blockchain.blockchain.size() == blockSize){
                SocketWrite("client equal",socket);
                // Do nothing
                System.out.println("Same chain size with Server");
            }

            // After comparing blockchain, then Compare node list
            this.nodeList = Reqeust_to_Node_for_NodeList();
        }

        return blockchain.blockchain;
    }

    public ArrayList<Block> Response_from_Node_for_Blockchain(Blockchain blockchain, Socket socket) throws IOException, NoSuchAlgorithmException, IllegalAccessException {

        int blockSize = blockchain.blockchain.size();
        SocketWrite(String.valueOf(blockSize), socket);

        String compareResponse = SocketRead(socket);

        if("client longer".equals(compareResponse)){

            // receive blockchain from client
            String strBlockchain = SocketRead(socket);
            return UserFunctions.Convert2Blockchain(strBlockchain,blockSize);
        }else if("client shorter".equals(compareResponse)){

            // send Blockchain to client
            SocketWrite(blockchain.get_All_Blocks_JSON(),socket);

        }else if("client equal".equals(compareResponse)){
            // do nothing
            System.out.println("Same chain size with Client");
        }



        return blockchain.blockchain;
    }



    public List<String> Reqeust_to_Node_for_NodeList() throws IOException {
        for(String address: nodeList){
            Socket socket = new Socket(address,8000);
            // send command
            System.out.println("nodeList exchange");

            // using the size of list to determine
            String str_listSize = "nodeListSize:"+String.valueOf(nodeList.size());
            // send list size to server
            SocketWrite(str_listSize,socket);

            // get response from server
            String nodeList_Response = SocketRead(socket);

            if("client longer".equals(nodeList_Response)){
                // parse node list to string
                StringBuilder str_nodeList = new StringBuilder();
                for(String node:nodeList){
                    str_nodeList.append(node);
                    str_nodeList.append("-");
                }
                //send own nodeList string to server
                SocketWrite(str_nodeList.toString(),socket);

            }
            else if("client shorter".equals(nodeList_Response)){
                String str_nodeList_from_server = SocketRead(socket);
                nodeList = Arrays.asList(str_nodeList_from_server.split("-"));

            }
            else if("client equal".equals(socket)){

            }
        }


        System.out.println("目前清單: ");
        nodeList.forEach(node-> System.out.println("node: "+node));

        return nodeList;
    }

    public List<String> Response_from_Node_for_NodeList(Socket socket) throws IOException {
        String str_listSize_from_client = SocketRead(socket);
        int listSize_from_Client = Integer.parseInt(str_listSize_from_client.split("nodeListSize:")[1]);

        if (listSize_from_Client > nodeList.size()){
            SocketWrite("client longer",socket);
            String str_nodeList_from_client = SocketRead(socket);
            nodeList = Arrays.asList(str_nodeList_from_client.split("-"));
        }
        else if( listSize_from_Client < nodeList.size()){
            SocketWrite("client shorter",socket);
            // parse node list to string
            StringBuilder str_nodeList = new StringBuilder();
            for(String node:nodeList){
                str_nodeList.append(node);
                str_nodeList.append("-");
            }
            //send own nodeList string to server
            SocketWrite(str_nodeList.toString(),socket);
        }
        else if(listSize_from_Client == nodeList.size()){
            SocketWrite("client equal",socket);
            // Do nothing
        }

        System.out.println("目前清單: ");
        nodeList.forEach(node-> System.out.println("node: "+node));
        return nodeList;
    }

}
