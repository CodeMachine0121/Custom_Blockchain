package Users.Wallet;

import BlockChain.Miner;
import BlockChain.Transaction;

import Users.SocketAction;
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

import static Users.SocketAction.*;

public class WalletUser {

    static final int EXIT_CODE=-15;

    static Scanner scanner;
    static Miner user;
    static List<Transaction> transactions;
    static String remoteHost;
    static Map<String, Runnable> actions;


    public WalletUser()  {

        user=null;
        transactions = new ArrayList<>();
        scanner = new Scanner(System.in);
        remoteHost="";

        actions = new HashMap<>();

        actions.put("",()-> {
            try {
                Connect_Node();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        actions.put("import",()-> {
            try {
                importWallet();
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | IOException | IllegalAccessException | InvalidAlgorithmParameterException | InvalidKeySpecException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        actions.put("makeTransaction",()-> {
            try {
                MakeTransaction();
            }
            catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | IllegalAccessException | NoSuchPaddingException | BadPaddingException | IOException | NoSuchProviderException | IllegalBlockSizeException | InvalidKeySpecException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        actions.put("commit",()-> {
            try {
                CommitTransaction();
            } catch (InterruptedException | IllegalAccessException | IOException e) {
                e.printStackTrace();
            }
        });
        actions.put("AnonymousCA",()->{
            try {
                Get_AnonymousCA();
            } catch (InterruptedException | IllegalAccessException | IOException | NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException | IllegalBlockSizeException | InvalidKeyException | BadPaddingException | NoSuchPaddingException e) {
                e.printStackTrace();
            }
        });
        actions.put("verifyCA",()->{
            try {
                Verify_CA();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        actions.put("balance",()-> {
            try {
                GetBalance();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        new WalletUser();

        // input ip
        actions.get(remoteHost).run();

        String command="";
        do{
            System.out.print("[*]\t");
            command= scanner.nextLine().strip();
            if("".equals(command))
                continue;
            try{
                actions.get(command).run();
            }catch (Exception e){
                System.out.println("Unknown command");
            }
        }while (true);

    }

    private static void Connect_Node() throws IOException {
        System.out.println("輸入節點:");
        System.out.print("\tip:\t");
        remoteHost = scanner.nextLine().strip();

        if(!SocketAction.TestConnection(remoteHost)){
            System.exit(EXIT_CODE);
        }

    }

    private static void importWallet() throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, IllegalAccessException, InvalidAlgorithmParameterException, InvalidKeySpecException, InterruptedException {
        user = UserFunctions.loadKey();

        if(user == null ) {
            System.out.println("no wallet import");
        }else{
            System.out.println("**** wallet import ****");
            System.out.println("Address:\t"+user.address);
            System.out.println("***********************\n");
            user.balance = SocketAction.getBalance(remoteHost,user.address);
        }

    }

    private static void MakeTransaction() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, IllegalAccessException, NoSuchPaddingException, BadPaddingException, IOException, NoSuchProviderException, IllegalBlockSizeException, InvalidKeySpecException, InterruptedException {

        if(user == null ) {
            System.out.println("no wallet import");
            return;
        }

        // Get user balance
        GetBalance();

        if(user.balance==-1){
            System.out.println("尚未取得餘額");
            return;
        }

        Transaction wannaTransaction = UserFunctions.makeTransaction(user);
        if(wannaTransaction==null){
            System.out.println("餘額不足");
            return;
        }
        transactions.add(wannaTransaction);
        UserFunctions.List_Transaction(transactions);
    }

    private static void CommitTransaction() throws InterruptedException, IllegalAccessException, IOException {
        if(user ==null){
            System.out.println("no wallet import");
            return;
        }
        //user.balance = SocketAction.getBalance(remoteHost,user.address);
        Thread.sleep(100);


        // commit transaction
        String response = SocketAction.commitTransaction(remoteHost,transactions.get(0));

        if("exceed length".equals(response)){
            System.out.println("該區塊交易已滿");
        }else if("signature wrong".equals(response)){
            System.out.println("交易簽章錯誤");
            transactions.remove(0);
        }
        else {
            System.out.println("交易成功提交");
            transactions.remove(0);
        }
    }

    // 取得 匿名CA
    private static void Get_AnonymousCA() throws InterruptedException, IllegalAccessException, IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        if(user ==null){
            System.out.println("no wallet import");
            return;
        }
        //user.balance = SocketAction.getBalance(remoteHost,user.address);
        Thread.sleep(100);


        // commit transaction
        String response = SocketAction.commitTransaction(remoteHost,transactions.get(0));

        if("exceed length".equals(response)){
            System.out.println("該區塊交易已滿");
        }else if("signature wrong".equals(response)){
            System.out.println("交易簽章錯誤");
            transactions.remove(0);
        }
        else {
            System.out.println("交易成功註冊並取得匿名身分");

            JSONObject CA = new JSONObject(KeyGenerater.RSA_Decrypt(response,KeyGenerater.Get_RSA_PrivateKey(user.getRSA_privateKey())));
            UserFunctions.printOutAnonymousID(CA.toString());
            // 儲存 CA
            UserFunctions.saveCertificate(CA);

            transactions.remove(0);
        }
    }

    private static void GetBalance() throws IOException, InterruptedException {
        if(user ==null){
            System.out.println("no wallet import");
            return;
        }
        user.balance = SocketAction.getBalance(remoteHost,user.address);
    }

    // 驗證 匿名CA
    private static void Verify_CA() throws Exception{

        System.out.print("想審核CA之ID:\t");
        String ID = scanner.nextLine().strip();
        JSONObject CA = UserFunctions.loadCertitfiacate(ID);
        if(CA == null){
            System.out.println("no CA import");
            return;
        }
        if(user ==null){
            System.out.println("no wallet import");
            return;
        }
        //user.balance = SocketAction.getBalance(remoteHost,user.address);
        Thread.sleep(100);

        // 把 CA 內容塞進 transaction Message裡面
        Transaction t = user.Make_Transaction(user.address,"CBC",0,0,CA.toString());

        // commit transaction
        Boolean response = Verify_CA(remoteHost,t);

        if(response)
            System.out.println("審核結果: 通過");


    }


    // Wallet -> CBC
    public static Boolean Verify_CA(String remotehost ,Transaction T) throws IOException, IllegalAccessException, InterruptedException {
        String command = "verifyCA";
        Socket socket = new Socket(remotehost,SERVER_PORT);

        // send command
        SocketWrite(command,socket);
        Thread.sleep(100);

        String CA = T.messages;
        // send transaction
        SocketWrite(CA,socket);

        String result = SocketRead(socket);
        System.out.println(result);

        if("exceed length".equals(result)){
            System.out.println("該區塊交易已滿");
            return false;
        }
        if("signature wrong".equals(result)){
            System.out.println("交易簽章錯誤");
            return false;
        }

        return !"Fail".equals(result);
    }

}
