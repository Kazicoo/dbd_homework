

public class ServerGenerator extends ServerMapItems {       
    public ServerGenerator(int id) {
        super(id);
    }

    @Override
    public boolean isColliding(ServerPlayer serverPlayer) {
        // 檢查玩家跟發電機的碰撞
        return true;
    }
}
