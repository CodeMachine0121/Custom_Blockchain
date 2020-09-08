package Users.Node;

import BlockChain.Block;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.Timer;

public interface Methods {


    Map<String,Runnable> Setup_Actions();

    void Setup_ServerNode() throws IOException, NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalAccessException, BadPaddingException, SignatureException, InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeySpecException, InterruptedException ;
    void TurnOn_Node_Server() throws IOException, NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalAccessException, BadPaddingException, SignatureException, InvalidAlgorithmParameterException, IllegalBlockSizeException, InterruptedException;
    void Ask_Block() throws InterruptedException, IOException, SignatureException, NoSuchAlgorithmException, InvalidKeyException, IllegalAccessException;
    void Mine_Block() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, SignatureException, InvalidKeyException, IllegalAccessException;
    void Commit_Transaction() throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, IllegalAccessException, InvalidKeyException, SignatureException;
    void Get_Balance() throws IOException, IllegalAccessException;
    void Update_Blockchain()throws IOException, InterruptedException, IllegalAccessException, NoSuchAlgorithmException ;
    void Change_Master() throws UnknownHostException;
    void Get_Blockchain() throws IllegalAccessException, IOException, InterruptedException;
    void Test_Connection();

    Timer SetTimer(InetAddress remotehost);
    void CancelTimer(Timer timer);
    void Connection_to_Node(InetAddress host, int port) throws IOException;
    Block MakeEmptyBlock(String previouhash, int No) throws IOException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalAccessException, InvalidKeyException, BadPaddingException, SignatureException, IllegalBlockSizeException ;
    double CalculateBalance(String address) throws IllegalAccessException;

}
