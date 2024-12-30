public class ServerGameObject {
    private int id;

    public ServerGameObject(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
    public int setId(int id) {
        return this.id = id;
    }
}