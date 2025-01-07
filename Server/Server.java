package Server;

import java.util.*;
import Comm.TcpServer;


public class Server implements Comm.TcpServerCallback {
  /// The server instance.
  public static Server instance;

  public static void main(String[] args) {
    try {
      instance = new Server();
    }

    catch (Exception e) {
      e.printStackTrace();
    }
  }


  /// The server's ticks per second.
  public static final double SERVER_TPS = 20;

  /// The server's frame time in milliseconds.
  public static final double SERVER_FT = 1000 / SERVER_TPS;


  public enum Status {
    Initializing,
    WaitingConnection,
  }


  private final TcpServer comm;
  private Status status = Status.Initializing;

  private Server() throws Exception {
    comm = new TcpServer(8080, 4, this);
    status = Status.WaitingConnection;

    long lastTime = System.nanoTime();
    while (true) {
      long currentTime = System.nanoTime();
      update((double)(currentTime - lastTime) / 1000000.0);
      Thread.sleep((long)(SERVER_FT));
    }
  }


  private void update(double elapsedMs) throws Exception {
    switch (status) {
      case Initializing      -> {}
      case WaitingConnection -> updateWaitingConnection(elapsedMs);
    }
  }

  private void updateWaitingConnection(double elapsedMs) {

  }


  public void sendMessage(int id, String message) throws Exception {
    comm.send(id, message);
  }

  public void broadcast(String message) throws Exception {
    comm.broadcast(message);
  }


  @Override
  public void onMessage(int id, String message) {
    System.out.println("Server received: " + message);
  }

  @Override
  public void onConnect(int id) {
    System.out.println("Server connected: " + id);
  }

  @Override
  public void onDisconnect(int id) {
    System.out.println("Server disconnected: " + id);
  }
}
