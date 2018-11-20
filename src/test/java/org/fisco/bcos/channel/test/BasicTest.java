package org.fisco.bcos.channel.test;

import org.fisco.bcos.channel.test.contract.Ok;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.core.DefaultBlockParameter;
import org.fisco.bcos.web3j.protocol.core.methods.response.*;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BasicTest extends TestBase {
    private static BigInteger gasPrice = new BigInteger("300000000");
    private static BigInteger gasLimit = new BigInteger("300000000");
    private static BigInteger initialWeiValue = new BigInteger("0");

    @Test
    public void  pbftViewTest() throws Exception {
       int i =  web3j.ethPbftView().send().getEthPbftView().intValue();
        System.out.println(i);
       assertNotNull(i>0);
    }

    @Ignore
    @Test //todo
    public void  consensusStatusTest() throws Exception {
        assertNotNull(web3j.consensusStatus().send());
    }

    @Test
    public void  syncTest() throws Exception {
        assertNotNull(web3j.ethSyncing().send().isSyncing());
    }

    @Test
    public void  versionTest() throws Exception {
        assertNotNull(web3j.web3ClientVersion().send().getWeb3ClientVersion());
    }

    //getPeers
    @Ignore
    @Test
    public void  peersTest() throws Exception {
        NetPeerCount peerCount = web3j.netPeerCount().send();
        assertNotNull(web3j.netPeerCount().send().getResult());
    }

    @Test
    public void  groupPeersTest() throws Exception {
         EthPeerList ethPeerList = web3j.ethGroupPeers().send();
         ethPeerList.getPeerList().stream().forEach(System.out::println);
        assertNotNull(ethPeerList.getResult());
    }


    @Test
    public void  groupListTest() throws Exception {
        GroupList groupList = web3j.ethGroupList().send();
        groupList.getGroupList().stream().forEach(System.out::println);
        assertTrue((groupList.getGroupList().size()>0));
    }

    @Test
    public void  pendingTransactionsTest() throws Exception {
        assertTrue(web3j.ethPendingTransaction().send().getPendingTransactions().size()>=0);
    }

    @Test
    public void getTransactionByBlockNumberAndIndexTest() throws IOException {
       Transaction transaction =  web3j.ethGetTransactionByBlockNumberAndIndex(DefaultBlockParameter.valueOf(new BigInteger("1")),new BigInteger("0")).send().getTransaction().get();
        assertTrue(transaction.getBlockNumber().intValue()==1);
    }

    @Test
    public void  basicTest() throws Exception {
        try {
            testDeployContract(web3j, credentials);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Execute basic test failed");
        }
    }

    private void testDeployContract(Web3j web3j, Credentials credentials) throws Exception {
        Ok okDemo = Ok.deploy(web3j, credentials, gasPrice, gasLimit, initialWeiValue).send();
        if (okDemo != null) {
            System.out.println("####get nonce from Block: " + web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(new BigInteger("0")), true).send().getBlock().getNonce());
            System.out.println("####get block number by index from Block: " + web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(new BigInteger("1")), true).send().getBlock().getNumber());

            System.out.println("####contract address is: " + okDemo.getContractAddress());
            TransactionReceipt receipt = okDemo.trans(new BigInteger("4")).sendAsync().get(60000, TimeUnit.MILLISECONDS);
            System.out.println("###callback trans success");

            System.out.println("####get block number from TransactionReceipt: " + receipt.getBlockNumber());
            System.out.println("####get transaction index from TransactionReceipt: " + receipt.getTransactionIndex());
            System.out.println("####get gas used from TransactionReceipt: " + receipt.getGasUsed());
       //   System.out.println("####get cumulative gas used from TransactionReceipt: " + receipt.getCumulativeGasUsed());

            BigInteger toBalance = okDemo.get().send();
            System.out.println("============to balance:" + toBalance.intValue());
        }
    }
}
