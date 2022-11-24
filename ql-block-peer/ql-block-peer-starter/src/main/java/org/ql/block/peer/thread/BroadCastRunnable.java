package org.ql.block.peer.thread;

import lombok.extern.slf4j.Slf4j;
import org.ql.block.peer.context.PeerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA at 2022/5/17 12:07
 * User: @Qi Long
 */
@Service
@Slf4j
public class BroadCastRunnable implements Runnable{

    @Autowired
    private PeerContext peerContext;

    public BroadCastRunnable() {
    }

    /**
     * 假设有A,B,C,D,E....等n个节点
     * 假设A写入消息（msg）到B，B知道是由A发来的msg，所以在gossip时不会像A再发送消息
     * 当B在进行Gossip传播时，可以排除到A
     * 假设B将msg传给C：
     *      C: 1.收到B的消息
     *         2.不会再将消息传给B，带可能会传给A。
     * 解决方法1:
     *      A -> B: A将要发送的消息m，在发送m时，附加上自己的能够证明身份的信息。暂且称之为pa
     *      B -> C: B收到的m和pa,B 发送m时在pa之后追加pb。
     *      -> C: 当C收到B的消息时，就可以确定这条消息A和B都已经知道了。此时C收到的消息：{m,pa,pb}
     *      -> C: A可能在将消息m发送B的同时也发给了C。此时C收到的是：{m,pa}
     *      todo 如何可以做到C之会收到一次消息
     *      方案：前置条件：（网络中的节点并不是全部联通的，如：A->{B,C,D,E,F}; B->{C,E,H,I,K}）
         *      A -> B: A在发送给B消息之前，预先设定自己会将消息发送给哪些节点，并暂且定性为这次发送一定会成功。假设为：{B,C,D}
         *              此时，A将要发送的消息为{m,pa,pb,pc,pd};
         *              todo 假设传播时有失败的情况。如：D并没有收到消息
         *      -> B: B收到{m,pa,pb,pc,pd};当B需要再次传播消息时应该首先排除掉这些节点；并在自己连接到的节点范围内
         *            传播消息。当B传播消息时，应为{m,pa,pb,pc,pd,ph,pi,pk}
         *      -> C: C收到{m,pa,pb,pc,pd}; C也可能会发出{m,pa,pb,pc,pd,ph,pi,pk}，此时消息又会出现冗余，并且最终的消息会成为{m,pa,pb,pc,...,p(n)}
     *          方案：
     *              A->B: 在A向B发送消息之前。和B进行一次沟通，“询问”B是否知晓了m;
     *                    这个询问是很小的信息量（相较于上层方案中在m后追加p）；
     *                    “询问”的方式：可以借助于TCP协议的”握手“过程。也可以是携带在“心跳包”中。
     *                    todo 这个询问，不如就按直接发m，不带p了
     */
    @Override
    public void run() {
//        try {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//            String str = "0";
//            byte[] bytes = new byte[1024];
//            Random random = new Random();
//            while (!str.equals("")){
//                System.out.println("输入你想要传播的消息");
//                str = reader.readLine();
//                long timeMillis = System.currentTimeMillis();
//                String strD = str;
//                bytes = str.getBytes(StandardCharsets.UTF_8);
//                random.setSeed(timeMillis);
//                ArrayList<Peer> peerList = peerInfo.getAddrYou();
//                if (peerList.size()<1){
//                   log.info("current connect 0 peer");
//                   continue;
//                }
//                for (int i = 0; i < 3; i++) {
//                    int j = random.nextInt(peerList.size()-1);
//                    Peer peer = peerList.get(j);
//                    MySocket mySocket = peer.getSocket();
//                    if (mySocket.getMessageFlag(strD)) {
//                        continue;
//                    }
//                    MyOutputStream out = new MyOutputStream(mySocket.getOutputStream());
//                    System.out.printf("传播给%s");
//                    out.write(MessageType.GOSSIPMSG,bytes);
//                    // todo 要及时清理之前用来做判断的内容
//                    mySocket.putMessage(strD);
//                }
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}
