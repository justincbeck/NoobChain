package com.beckproduct.noobchain;

import com.beckproduct.utils.StringUtils;

import java.security.*;
import java.util.ArrayList;

public class Transaction {
    public String transactionId; // this is also the hash of the transaction.
    PublicKey sender; // senders address/public key.
    PublicKey reciepient; // Recipients address/public key.
    float value;
    private byte[] signature; // this is to prevent anybody else from spending funds in our wallet.

    ArrayList<TransactionInput> inputs;
    ArrayList<TransactionOutput> outputs = new ArrayList<>();

    private static int sequence = 0; // a rough count of how many transactions have been generated.

    // Constructor:
    Transaction(PublicKey from, PublicKey to, float value,  ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.reciepient = to;
        this.value = value;
        this.inputs = inputs;
    }

    // This Calculates the transaction hash (which will be used as its Id)
    private String calulateHash() {
        sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
        return StringUtils.applySha256(
                StringUtils.getStringFromKey(sender) +
                        StringUtils.getStringFromKey(reciepient) +
                        Float.toString(value) + sequence
        );
    }

    //Signs all the data we dont wish to be tampered with.
    void generateSignature(PrivateKey privateKey) {
        String data = StringUtils.getStringFromKey(sender) + StringUtils.getStringFromKey(reciepient) + Float.toString(value)	;
        signature = StringUtils.applyECDSASig(privateKey,data);
    }

    //Verifies the data we signed hasnt been tampered with
    boolean verifiySignature() {
        String data = StringUtils.getStringFromKey(sender) + StringUtils.getStringFromKey(reciepient) + Float.toString(value)	;
        return !StringUtils.verifyECDSASig(sender, data, signature);
    }

    // Returns true if new transaction could be created.
    boolean processTransaction() {

        if(verifiySignature()) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        // gather transaction inputs (Make sure they are unspent):
        for(TransactionInput i : inputs) {
            i.UTXO = NoobChain.UTXOs.get(i.transactionOutputId);
        }

        // check if transaction is valid:
        if(getInputsValue() < NoobChain.minimumTransaction) {
            System.out.println("#Transaction Inputs to small: " + getInputsValue());
            return false;
        }

        // generate transaction outputs:
        float leftOver = getInputsValue() - value; // get value of inputs then the left over change:
        transactionId = calulateHash();
        outputs.add(new TransactionOutput( this.reciepient, value,transactionId)); // send value to recipient
        outputs.add(new TransactionOutput( this.sender, leftOver,transactionId)); // send the left over 'change' back to sender

        // add outputs to Unspent list
        for(TransactionOutput o : outputs) {
            NoobChain.UTXOs.put(o.id , o);
        }

        // remove transaction inputs from UTXO lists as spent:
        for(TransactionInput i : inputs) {
            if(i.UTXO == null) continue; // if Transaction can't be found skip it
            NoobChain.UTXOs.remove(i.UTXO.id);
        }

        return true;
    }

    // returns sum of inputs(UTXOs) values
    float getInputsValue() {
        float total = 0;
        for(TransactionInput i : inputs) {
            if(i.UTXO == null) continue; // if Transaction can't be found skip it
            total += i.UTXO.value;
        }
        return total;
    }

    // returns sum of outputs:
    float getOutputsValue() {
        float total = 0;
        for(TransactionOutput o : outputs) {
            total += o.value;
        }
        return total;
    }
}