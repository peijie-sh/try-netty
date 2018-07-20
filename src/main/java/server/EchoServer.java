package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author: LPJ
 * @date: 2018/7/20
 */
public class EchoServer {

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        new EchoServer(9999).start();
    }

    public void start() throws Exception {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        // 1.创建EventLoopGroup
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            // 2.创建ServerBootstrap
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    // 3.指定使用的NIO传输Channel
                    .channel(NioServerSocketChannel.class)
                    // 4.指定地址端口
                    .localAddress(new InetSocketAddress(port))
                    // 5.添加EchoServerHandler到子channel的pipeline
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //EchoServerHandler被注解@Sharable修饰，使用单例
                            ch.pipeline().addLast(serverHandler);
                        }
                    });

            // 6.异步地绑定服务器；调用 sync()方法阻塞等待直到绑定完成
            ChannelFuture f = b.bind().sync();
            // 7.获取 Channel 的CloseFuture，并且阻塞当前线程直到它完成
            f.channel().closeFuture().sync();
        } finally {
            // 8.关闭 EventLoopGroup，释放所有的资源
            group.shutdownGracefully().sync();
        }
    }

}
