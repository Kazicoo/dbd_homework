public class ClientPlayer extends ClientGameObject {
    private String role;
    private boolean isSelf = false;

    public ClientPlayer(int id) {
        super(id);
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getRole() {
        return this.role;
    }

    public void setIsSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }

    public boolean getIsSelf() {
        return this.isSelf;
    }
}
