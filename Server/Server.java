package Server;

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
  public static final double SERVER_TPS = 60;

  /// The server's frame time in milliseconds.
  public static final double SERVER_FT = 1000 / SERVER_TPS;

  /// The grid size in pixels.
  public static final int GRID_SIZE = 60;

  /// The board width in grid units.
  public static final int BOARD_WIDTH = 100;

  /// The board height in grid units.
  public static final int BOARD_HEIGHT = 60;


  public enum Status {
    Initializing,
    WaitingConnection,
  }


  private final TcpServer comm;

  private Status  status = Status.Initializing;
  private MapItem board[][] = new MapItem[BOARD_WIDTH][BOARD_HEIGHT];
  private Player  players[] = new Player[4];

  private Server() throws Exception {
    comm = new TcpServer(8080, 4, this);
    status = Status.WaitingConnection;

    long lastTime = System.nanoTime();
    while (true) {
      long currentTime = System.nanoTime();
      update((double)(currentTime - lastTime) / 1e9);
      Thread.sleep((long)SERVER_FT);
    }
  }


  private void update(double elapsed) throws Exception {
    switch (status) {
      case Initializing      -> {}
      case WaitingConnection -> updateWaitingConnection(elapsed);
    }
  }

  private void updateWaitingConnection(double elapsed) {

  }


  public MapItem getMapItem(int x, int y) {
    if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) {
      return null;
    }

    return board[x][y];
  }

  public Player[] getPlayers() {
    return players;
  }

  public Human[] getHumans() {
    Human humans[] = new Human[4];
    int count = 0;

    for (Player p : players)
      if (p != null && p instanceof Human h)
        humans[count++] = h;

    return humans;
  }

  public Killer getKiller() {
    for (Player p : players)
      if (p != null && p instanceof Killer k)
        return k;

    return null;
  }


  public void sendMessage(int id, String message) {
    comm.send(id, message);
  }

  public void sendMessage(String message) {
    comm.broadcast(message);
  }


  private void handleKeyDown(int id, String parts[]) {
    if (parts.length != 2)
      return;

    Player player = players[id];
    if (player == null)
      return;

    switch (parts[1].toLowerCase()) {
      case "w" -> player.updateDirection(Player.Direction.Up   , true);
      case "s" -> player.updateDirection(Player.Direction.Down , true);
      case "a" -> player.updateDirection(Player.Direction.Left , true);
      case "d" -> player.updateDirection(Player.Direction.Right, true);
    }
  }

  private void handleKeyUp(int id, String parts[]) {
    if (parts.length != 2)
      return;

      Player player = players[id];
      if (player == null)
        return;

    switch (parts[1].toLowerCase()) {
      case "w" -> player.updateDirection(Player.Direction.Up   , false);
      case "s" -> player.updateDirection(Player.Direction.Down , false);
      case "a" -> player.updateDirection(Player.Direction.Left , false);
      case "d" -> player.updateDirection(Player.Direction.Right, false);
    }
  }

  private void handleAttack(int id, String parts[]) {
    Player player = players[id];
    if (player == null)
      return;

    if (player instanceof Killer k)
      k.attack();
  }

  private void handleHealing(int id, String parts[]) {
    if (parts.length != 2)
      return;

    Player player = players[id];
    if (player == null || !(player instanceof Human))
      return;

    int targetId = Integer.parseInt(parts[1]);
    if (players[targetId] == null || !(players[targetId] instanceof Human))
      return;

    Human healer = (Human)player;
    Human target = (Human)players[targetId];

    if (target.isFallen())
      healer.healing(target);
  }

  private void handleStopHealing(int id, String parts[]) {
    Player player = players[id];
    if (player == null || !(player instanceof Human))
      return;

    Human healer = (Human)player;
    healer.stopHealing();
  }


  @Override
  public void onMessage(int id, String message) {
    System.out.println("Server received: " + message);
    String parts[] = message.split(";");

    if (parts.length == 0)
      return;

    switch (parts[0].toLowerCase()) {
      case "keydown"  -> handleKeyDown(id, parts);
      case "keyup"    -> handleKeyUp(id, parts);
      case "attack"   -> handleAttack(id, parts);
      case "heal"     -> handleHealing(id, parts);
      case "stopheal" -> handleStopHealing(id, parts);
    }
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
