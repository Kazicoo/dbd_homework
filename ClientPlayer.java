public class ClientPlayer extends ClientGameObject {
    private String role;
    public ClientPlayer(int id) {
        super(id);
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getRole() {
        return this.role;
    }
}
