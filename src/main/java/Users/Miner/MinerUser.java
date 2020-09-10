package Users.Miner;

import BlockChain.Block;

import BlockChain.Miner;


import Users.SocketAction;
import Users.UserFunctions;


import javax.crypto.NoSuchPaddingException;
import java.io.*;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MinerUser {


    static final int EXIT_CODE=-15;

    static Miner miner;
    static Scanner scanner;
    static String remoteHost;
    static Block block;
    static String jno;

    static Map<String,Runnable> actions;

    public MinerUser() throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, IllegalAccessException, InvalidAlgorithmParameterException, InvalidKeySpecException {

        scanner = new Scanner(System.in);
        miner = UserFunctions.loadKey();
        remoteHost ="";
// if miner can't load
        if(miner==null){
            System.exit(EXIT_CODE);
        }

        block = null;
        jno="";

        //  define actions
        actions = new HashMap<>();
        actions.put("",()->{
            try {
                Connect_Node();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        actions.put("ask-block",()->{
            try {
                AskBlock();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        actions.put("mine",()->{
            try {
                Mine();
            } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | InterruptedException | IllegalAccessException | NoSuchPaddingException | NoSuchProviderException | SignatureException | InvalidKeySpecException e) {
                e.printStackTrace();
            }
        });
        actions.put("get-blockchain",()->{
            try {
                Get_Blockchain();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }



    public static void main(String[] args) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, IllegalAccessException, InvalidAlgorithmParameterException, InvalidKeySpecException {

        new MinerUser();

        // input ip
        actions.get(remoteHost).run();

        String command="";
        do {
            System.out.print("[*] ");
            command = scanner.nextLine().strip();
            actions.get(command).run();
        } while (true);

    }

    public static void Connect_Node() throws IOException {
        System.out.println("輸入節點:");
        System.out.print("\tip:\t");
        remoteHost = scanner.nextLine().strip();

        if(!SocketAction.TestConnection(remoteHost)) {
            System.exit(EXIT_CODE);
        }
    }

    public static void AskBlock() throws IOException, InterruptedException {
        block = SocketAction.getBlock(remoteHost);
    }

    public static void Mine() throws IOException, NoSuchAlgorithmException, InvalidKeyException, InterruptedException, IllegalAccessException, NoSuchPaddingException, NoSuchProviderException, SignatureException, InvalidKeySpecException {
        try{
            SocketAction.mineBlock(remoteHost, block, miner);
            block=null;
        }catch (Exception e){
            System.out.println("目前並無緩衝區塊");
        }
    }
    public static void Get_Blockchain() throws IOException, InterruptedException {
        SocketAction.getBlockchain(remoteHost);
    }
}
