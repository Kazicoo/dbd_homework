

public class ServerWall extends ServerMapItems{

    public ServerWall() {
        super(0);
    }

    @Override
    public boolean isColliding(ServerPlayer serverplayer) {
        // 檢查玩家與牆壁的碰撞
        return true;
    }
}
