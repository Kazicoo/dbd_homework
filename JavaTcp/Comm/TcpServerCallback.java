package Comm;

public interface TcpServerCallback {
  /// The on message callback.
  public void onMessage(int id, String message);

  /// The on connect callback.
  public void onConnect(int id);

  /// The on disconnect callback.
  public void onDisconnect(int id);
}
