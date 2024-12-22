package Comm;

public interface TcpClientCallback {
  /// The on message callback.
  public void onMessage(String message);

  /// The on connect callback.
  public void onConnect();

  /// The on disconnect callback.
  public void onDisconnect();
}
