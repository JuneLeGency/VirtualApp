package com.minhui.vpn;

import java.io.FileDescriptor;
import java.net.DatagramSocket;
import java.net.Socket;

/**
 * @author legency
 * @date 2018/05/12.
 */
public interface IVpnService {

    boolean protect(int socket);

    boolean protect(Socket socket);

    boolean protect(DatagramSocket socket);

    FileDescriptor getInterceptFd();
}
