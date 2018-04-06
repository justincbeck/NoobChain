package com.beckproduct.noobchain;

import com.beckproduct.utils.StringUtils;

import java.security.PublicKey;

class TransactionOutput {
    String id;
    PublicKey reciepient; //also known as the new owner of these coins.
    float value; //the amount of coins they own

    //Constructor
    TransactionOutput(PublicKey reciepient, float value, String parentTransactionId) {
        this.reciepient = reciepient;
        this.value = value;

        //the id of the transaction this output was created in
        this.id = StringUtils.applySha256(StringUtils.getStringFromKey(reciepient)+Float.toString(value)+parentTransactionId);
    }

    //Check if coin belongs to you
    boolean isMine(PublicKey publicKey) {
        return (publicKey == reciepient);
    }
}
