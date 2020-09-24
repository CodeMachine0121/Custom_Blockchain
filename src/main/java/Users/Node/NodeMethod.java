package Users.Node;

import BlockChain.Block;
import BlockChain.Blockchain;
import BlockChain.Miner;
import BlockChain.Transaction;
import Users.SocketAction;
import Users.UserFunctions;
import org.json.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

import static Users.SocketAction.*;


public class NodeMethod{

    public  String host;
    public  int port;

    public   InetAddress Remotehost;

    public  Blockchain blockchain;
    public  ArrayList<Block> bufferChain;


    InetAddress master;
    public Timer timer;

    public Miner nodeUser;
    double totalBalance;


    // *****
    public Socket clientSocket;
    public int BlockNo;
    public Map<String,Runnable> actions;

    public Consensus consensus;
    // 初始化 actions
    public  Map<String,Runnable> Setup_Actions(){
        Map<String,Runnable> actions = new HashMap<>();

        actions.put("ask-block",()->{
            try {
                Ask_Block();
            } catch (InterruptedException | IOException | SignatureException | NoSuchAlgorithmException | InvalidKeyException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        actions.put("mine",()->{
            try {
                Mine_Block();
            } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException | SignatureException | InvalidKeyException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        actions.put("commit",()->{
            try {
                Commit_Transaction();
            } catch (IOException | NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException | IllegalAccessException | InvalidKeyException | SignatureException e) {
                e.printStackTrace();
            }
        });
        actions.put("balance",()->{
            try {
                Get_Balance();
            } catch (IOException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        actions.put("get-blockchain",()->{
            try { // Miner ask for blockchain
                Request_Whole_Blockchain();
            } catch (IllegalAccessException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        actions.put("Request_Node",()->{
            try {
                Response_from_Node_for_Blockchain();
            } catch (IllegalAccessException | IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        });


        actions.put("test", ()->{Test_Connection();});

        return actions;
    }
    // 設定 Server Node參數
    public  void Setup_ServerNode() throws IOException, NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalAccessException, BadPaddingException, SignatureException, InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeySpecException, InterruptedException {

        host=null;
        port = 8000;
        Remotehost=null;
        bufferChain = new ArrayList<>();
        master=null;
        timer=new Timer();
        totalBalance=-1.0;

        actions = Setup_Actions();
        blockchain = new Blockchain();

        Scanner scanner = new Scanner(System.in);

        nodeUser = UserFunctions.loadKey();
        if(nodeUser==null)
            System.exit(-15);

        System.out.print("創建區塊鏈 or 繼承區塊練(create / load):\t");
        String option = scanner.nextLine();

        System.out.println("輸入本節點位址:");
        System.out.print("\tip:\t");
        host = scanner.nextLine();
        consensus = new Consensus(host);

        if(option.equals("load")){

            // setting remote node
            System.out.println("須輸入遠端節點:");

            System.out.print("\tip:\t");
            String remotehost = scanner.nextLine();
            Remotehost = InetAddress.getByName(remotehost);

            // 測試連縣
            if(!SocketAction.TestConnection(remotehost))
                System.exit(-15);
            consensus.nodeList.add(remotehost);
        }

        if(option.equals("create")){
            System.out.print("Total money in this chain:\t");
            while(totalBalance==-1.0){
                totalBalance = UserFunctions.setTotalBalance();
            }

            Block block = MakeEmptyBlock("0",blockchain.blockchain.size());

            // make transaction send total to nodeUser
            Transaction firstTransaction = nodeUser.Make_Transaction("System", nodeUser.address, totalBalance, 0, "Total Balance");
            block.Add_Transaction(firstTransaction);
            bufferChain.add(block);
        }

    }
    // 啟動 Server Node
    public void TurnOn_Node_Server() throws IOException, NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalAccessException, BadPaddingException, SignatureException, InvalidAlgorithmParameterException, IllegalBlockSizeException, InterruptedException {

        System.out.println("開啟節點伺服器.....");
        InetAddress addr = InetAddress.getByName(host);
        ServerSocket socket =new ServerSocket(port,50,addr);
        System.out.println("節點伺服器開啟完畢");


        timer = null;

        if(Remotehost!=null)
            timer=SetTimer(Remotehost);

        while(true) {

            clientSocket=socket.accept();


            if(bufferChain.size()==0){
                try{
                    Block block;
                    block = MakeEmptyBlock(blockchain.blockchain.get(blockchain.blockchain.size()-1).hash,blockchain.blockchain.size()+1);
                    bufferChain.add(block);
                }catch (Exception e){
                    System.out.println("由於 該節點區塊鏈為無, 加上該節點並不是創始解點 故發生錯誤");
                }
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    // Get block number
                    BlockNo=blockchain.blockchain.size();

                    System.out.println("接收新連線: " + clientSocket.getInetAddress());
                    String cmd = null;
                    try {
                        // get command
                        cmd = SocketRead(clientSocket);
                        System.out.println("指令: "+cmd);
                        actions.get(cmd).run();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }).start();
            Thread.sleep(1000);
        }
    }

    /* 指令 methods */
    public void Ask_Block() throws InterruptedException, IOException, SignatureException, NoSuchAlgorithmException, InvalidKeyException, IllegalAccessException {
        System.out.print("\t要求節點=>\t");
        if(bufferChain.get(0).transactions.size()==0){
            SocketAction.SocketWrite("No transaction", clientSocket);
            System.out.println("No transaction in block");
        }else {
            System.out.println(bufferChain.get(0).get_Block_to_Json(BlockNo));
        }

        // send block no
        SocketAction.SocketWrite(String.valueOf(BlockNo), clientSocket);


        // send block
        Thread.sleep(100);
        SocketAction.SocketWrite(bufferChain.get(0).get_Block_to_Json(BlockNo), clientSocket);
    }

    // 接收已計算好的區塊
    public void Mine_Block() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, SignatureException, InvalidKeyException, IllegalAccessException {
        SocketAction.SocketWrite(String.valueOf(BlockNo), clientSocket);

        // receive new block
        String jblock = SocketRead(clientSocket);
        if (jblock.equals("no"))
            throw new IOException();

        Block new_block = UserFunctions.Convert2Block(jblock, BlockNo);
        System.out.println("新區塊: " );
        UserFunctions.printOutBlock(jblock,BlockNo);


        // recieve difficulty
        String res_difficulty = SocketRead(clientSocket);
        // set difficulty
        Blockchain.difficulty = Integer.parseInt(res_difficulty);


        Boolean result=false;
        // check if new block valid, Genesis不需要檢查
        if(BlockNo==0)
            result=true;
        else
            result = blockchain.Is_Block_current(new_block);

        if (result) {
            blockchain.Add_Block_to_Chain(new_block);

            bufferChain.remove(0);
            Block newBufferBlock = new Block(new_block.hash,Blockchain.difficulty);
            bufferChain.add(newBufferBlock);

            SocketAction.SocketWrite(blockchain.get_All_Blocks_JSON(), clientSocket);
        } else {
            SocketAction.SocketWrite("no", clientSocket);
        }
    }

    // 接收 Wallet使用者的交易
    public  void Commit_Transaction() throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, IllegalAccessException, InvalidKeyException, SignatureException {
        System.out.println("\t提交交易");

        // get transaction String
        String Stransaction = SocketRead(clientSocket);

        Transaction t = UserFunctions.Convert2Transaction(Stransaction);


        String result="";


        // Verify the signature of transaction
        if(!Transaction.Is_transactions_valid(t)){
            System.out.println("\t\t交易簽章錯誤");
            result = "signature wrong";
        }
        else if(bufferChain.get(0).transactions.size() < Block.block_limitation){
            // Add transaction to block
            bufferChain.get(0).Add_Transaction(t);
            // It mean block is full, needs to be mine
        }
        else{
            System.out.println("\t\t交易已滿 等待挖掘");
            result="exceed length";
        }

        // send result
        SocketAction.SocketWrite(result, clientSocket);
    }

    // 回應Wallet餘額
    public  void Get_Balance() throws IOException, IllegalAccessException {
        System.out.print("\t要求餘額=>\t");

        // get Address
        String Address= SocketRead(clientSocket);

        // send balance
        double balance = CalculateBalance(Address);
        SocketAction.SocketWrite(String.valueOf(balance), clientSocket);

        System.out.println(balance);
    }



    // 其他節點要求 區塊鏈比較
    public void Response_from_Node_for_Blockchain() throws IllegalAccessException, NoSuchAlgorithmException, IOException {
        if(!consensus.nodeList.contains(clientSocket.getInetAddress().toString().split("/")[1]))
            consensus.nodeList.add(clientSocket.getInetAddress().toString().split("/")[1]);
        blockchain.blockchain = consensus.Response_from_Node_for_Blockchain(blockchain,clientSocket);
        // 如何判斷 誰需要定時 發送請求
        // 收指令問題
    }
    // 要求其他節點進行區塊鏈比較
    public void Request_to_Node_for_Blockchain() throws IllegalAccessException, NoSuchAlgorithmException, IOException, InterruptedException {
        System.out.println("開始比對 blockchain....");
        blockchain.blockchain = consensus.Request_to_Node_for_Blockchain(blockchain);
    }




    // 回應Miner目前區塊鏈
    public  void Request_Whole_Blockchain() throws IllegalAccessException, IOException, InterruptedException {
        System.out.println("\t下載區塊鏈\t");
        SocketAction.SocketWrite(blockchain.get_All_Blocks_JSON(), clientSocket);
        Thread.sleep(100);
        SocketAction.SocketWrite(String.valueOf(BlockNo),clientSocket);
    }

    // 測試連線
    public  void Test_Connection(){
        System.out.println("\t測試連線\t");
    }

    TimerTask askChain = new TimerTask() {
        @Override
        public void run() {
            try {
                Request_to_Node_for_Blockchain();
            } catch (IOException | IllegalAccessException | NoSuchAlgorithmException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    };
    /* 指令會用到的 methods */
    public  Timer SetTimer(InetAddress remotehost)  {
        Remotehost=remotehost;
        timer =new Timer();

        System.out.println("*********設定排程********");
        timer.schedule(askChain, 10000, 10000);// 1秒後開始，之後每過30秒再執行
        return timer;
    }

    public  void CancelTimer(Timer timer){
        System.out.println("*********取消排程********");
        timer.cancel();
    }


    public Block MakeEmptyBlock(String previouhash, int No) throws IOException, NoSuchAlgorithmException {
        Block block = new Block(previouhash,Blockchain.difficulty);
        block.No =  No;
        return block;
    }

    public double CalculateBalance(String address) throws IllegalAccessException {
        double balance = 0;
        int BlockchainSize = blockchain.blockchain.size();

        JSONObject Jblockchain = new JSONObject(blockchain.get_All_Blocks_JSON()).getJSONObject("Blockchain");



        for (int i=0;i<BlockchainSize;i++) { // through blockchain
            JSONObject Jblock = Jblockchain.getJSONObject("Block-"+i);
            String miner = Jblock.getString("miner");

            if(miner.equals(address))
                balance+=Block.miner_reward;

            JSONObject Jtransaction = Jblock.getJSONObject("Transactions");
            for(int j=0;j<Block.block_limitation;j++){ // through inside block
                try{
                    JSONObject Jtxn = Jtransaction.getJSONObject("txn-"+j);
                    String receiver = Jtxn.getString("receiver");
                    String sender = Jtxn.getString("sender");
                    double amount = Jtxn.getDouble("amount");
                    double fee = Jtxn.getDouble("fee");

                    if(receiver.equals(address)){
                        balance+=fee*Math.pow(10,-5)+amount ; // 1 fee = 10^-5 btc
                    }
                    if(sender.equals(address)){
                        balance-=fee*Math.pow(10,-5)+amount;
                    }

                }catch (Exception e){
                    break;
                }
            }
        }
        return balance;
    }

}
