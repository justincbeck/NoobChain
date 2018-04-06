package com.beckproduct.noobchain;

class TransactionInput {
    String transactionOutputId; // Reference to TransactionOutputs -> transactionId
    TransactionOutput UTXO; // Contains the Unspent transaction output

    TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}
