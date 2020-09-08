package Anonymous_Function.Registaration;

import BlockChain.Block;
import Users.Node.Methods;
import Users.Node.NodeMethod;

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

public class RegistrationMethod implements Methods {

    NodeMethod nodeMethod = new NodeMethod();

    @Override
    public Map<String, Runnable> Setup_Actions() {
        return null;
    }

    // 需修改
    @Override
    public void Setup_ServerNode() throws IOException, NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalAccessException, BadPaddingException, SignatureException, InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchProviderException, InvalidKeySpecException, InterruptedException {

    }

    @Override
    public void TurnOn_Node_Server() throws IOException, NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalAccessException, BadPaddingException, SignatureException, InvalidAlgorithmParameterException, IllegalBlockSizeException, InterruptedException {
        nodeMethod.TurnOn_Node_Server();
    }

    @Override
    public void Ask_Block() throws InterruptedException, IOException, SignatureException, NoSuchAlgorithmException, InvalidKeyException, IllegalAccessException {
        nodeMethod.Ask_Block();
    }

    @Override
    public void Mine_Block() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, SignatureException, InvalidKeyException, IllegalAccessException {
        nodeMethod.Mine_Block();
    }

    @Override
    public void Commit_Transaction() throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, IllegalAccessException, InvalidKeyException, SignatureException {
        nodeMethod.Commit_Transaction();
    }

    @Override
    public void Get_Balance() throws IOException, IllegalAccessException {
        nodeMethod.Get_Balance();
    }

    @Override
    public void Update_Blockchain() throws IOException, InterruptedException, IllegalAccessException, NoSuchAlgorithmException {
        nodeMethod.Update_Blockchain();
    }

    @Override
    public void Change_Master() throws UnknownHostException {
        nodeMethod.Change_Master();
    }

    @Override
    public void Get_Blockchain() throws IllegalAccessException, IOException, InterruptedException {
        nodeMethod.Get_Blockchain();
    }

    @Override
    public void Test_Connection() {
        nodeMethod.Test_Connection();
    }

    @Override
    public Timer SetTimer(InetAddress remotehost) {
        return nodeMethod.SetTimer(remotehost);
    }

    @Override
    public void CancelTimer(Timer timer) {
        nodeMethod.CancelTimer(timer);
    }

    @Override
    public void Connection_to_Node(InetAddress host, int port) throws IOException {
        nodeMethod.Connection_to_Node(host,port);
    }

    @Override
    public Block MakeEmptyBlock(String previouhash, int No) throws IOException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalAccessException, InvalidKeyException, BadPaddingException, SignatureException, IllegalBlockSizeException {
        return nodeMethod.MakeEmptyBlock(previouhash,No);
    }

    @Override
    public double CalculateBalance(String address) throws IllegalAccessException {
        return nodeMethod.CalculateBalance(address);
    }
}
