package com.geekbrains.io;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;

import java.io.DataOutputStream;
import java.net.Socket;

public class Handler  implements Runnable{

    private final Socket socket;
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    public Handler(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        try (DataOutputStream os= new DataOutputStream(socket.getOutputStream());
             DataInputStream is = new DataInputStream(socket.getInputStream())
        ) {
            while (true) {
                String s = is.readUTF();
                LOG.debug("Received: {}", s);
                os.writeUTF(s);
                os.flush();
            }

        } catch (Exception e) {
            LOG.error("stacktrace", e);
        }

    }
}
