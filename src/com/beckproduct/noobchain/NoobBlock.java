package com.beckproduct.noobchain;

import com.beckproduct.utils.StringUtils;

import java.util.ArrayList;
import java.util.Date;

class NoobBlock {

    String hash;
    String previousHash;
    private String merkleRoot;
    ArrayList<Transaction> transactions = new ArrayList<>(); // our data will be a simple message.
    private long timeStamp; // as number of milliseconds since 1/1/1970.
    private int nonce;

    // NoobBlock Constructor.
    NoobBlock(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    String calculateHash() {
        return StringUtils.applySha256(
                previousHash +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                        merkleRoot
        );
    }

    void mineBlock(int difficulty) {
        merkleRoot = StringUtils.getMerkleRoot(transactions);
        String target = new String(new char[difficulty]).replace('\0', '0'); //Create a string with difficulty * "0"
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!!! : " + hash);
    }

    // Add transactions to this block
    boolean addTransaction(Transaction transaction) {
        // process transaction and check if valid, unless block is genesis block then ignore.
        if(transaction == null) return false;

        if((!previousHash.equals("0"))) {
            if((!transaction.processTransaction())) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }

        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");

        return true;
    }
}