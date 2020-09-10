package BlockChain;

import Util.KeyGenerater;
import Util.StringUtil;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class Transaction {

    public String sender;
    public String receiver;
    public double amount;
    public double fee;
    public String messages;

    public String transaction_hash;
    public String ECDSA_Publickey;
    public String RSA_Publickey;
    public String signature;
    public Transaction(String sender, String receiver, double amount, double fee, String messages, String ECDS_Publickey, String signature, String RSA_Publickey ) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.fee = fee;
        this.messages = messages;
        // HASH = RIPEMD160( SHA256( transaction ) )
        this.transaction_hash = StringUtil.apply_RIPEMD160(StringUtil.applyHASH(this.sender+this.receiver+this.amount+this.fee+this.messages,"SHA-256"));

        this.ECDSA_Publickey = ECDS_Publickey;
        this.signature = signature;

        this.RSA_Publickey = RSA_Publickey;
    }


    public JSONObject Transaction_to_JSON() throws IllegalAccessException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("sender",this.sender);
        jsonObject.put("receiver",this.receiver);
        jsonObject.put("amount",this.amount);
        jsonObject.put("fee",this.fee);
        jsonObject.put("messages",this.messages);
        //jsonObject.put("publicKey", StringUtil.Generate_ECDSA_Public_Key(this.publickey));
        jsonObject.put("ECDSA_PublicKey",this.ECDSA_Publickey);
        jsonObject.put("RSA_PublicKey",this.RSA_Publickey);
        jsonObject.put("txnsign",this.signature);
        return jsonObject;
    }
/*
    public String Transaction_to_String() throws NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, IllegalAccessException {
        return this.sender+this.receiver+this.amount+this.fee+this.messages+Util.StringUtil.Generate_ECDSA_Public_Key(this.publickey);
    }*/

    public double Get_amount(){return this.amount;}
    public  String Get_messages(){return this.messages;}


    public static Boolean Is_transactions_valid(Transaction transaction) throws InvalidKeySpecException, SignatureException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, IllegalAccessException, NoSuchProviderException {
        JSONObject  transactionJsonobject = transaction.Transaction_to_JSON();

        String publicKeyString = transactionJsonobject.getString("ECDSA_PublicKey");

        boolean r = KeyGenerater.Verify_Signature(transactionJsonobject.getString("txnsign"),publicKeyString,transactionJsonobject.getString("messages"));
        return r;
    }


}
