import javax.swing.*;

public class ClientPlayer extends ClientGameObject {
    private String role;
    private boolean isSelf = false;
    private int hp = 2;
    private String status;

    private ImageIcon idleImage;
    private ImageIcon attackLeftImage;
    private ImageIcon attackRightImage;
    private ImageIcon currentImage;
    private ImageIcon backImage;
    private ImageIcon frontImage;
    private ImageIcon rightIcon;
    private ImageIcon leftIcon;
    private ImageIcon standImage;
    private ImageIcon bloodImage;
    private ImageIcon downImage;
    private ImageIcon climbleftIcon;
    private ImageIcon climbrightIcon;
    

    private int offsetX = 0;
    private int offsetY = 0;

    public ClientPlayer(int id) {
        super(id);
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return this.role;
    }
    public void  setHp(int hp) {
        this.hp = hp;
        switch (hp) {
            case 0:
                status = "倒地";
                break;
            case 1:
                status = "受傷";
                break;
            case 2:
                status = "健康";
            default:
                System.out.println("未設定status");
                break;
        }
    }
    public int getHp() {
        return this.hp;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return this.status;
    }

    public void setIsSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }

    public boolean getIsSelf() {
        return this.isSelf;
    }
    
    public ImageIcon getCurrentImage() {
        return currentImage;
    }
    public ImageIcon initImage() {
        if (this.role.equals("killer")) {
            idleImage        = new ImageIcon("Graphic/Killer/killer-standFront.png");
            attackLeftImage  = new ImageIcon("Graphic/Killer/killer-attackLeft.png");
            attackRightImage = new ImageIcon("Graphic/Killer/killer-attackRight.png");
            backImage        = new ImageIcon("Graphic/Killer/killer-back.png");
            frontImage       = new ImageIcon("Graphic/Killer/killer-right.png");
            rightIcon        = new ImageIcon("Graphic/Killer/killer-right.png");
            leftIcon         = new ImageIcon("Graphic/Killer/killer-left.png");
            standImage       = new ImageIcon("Graphic/Killer/killer-left.png");
            climbleftIcon    = new ImageIcon("Graphic/Killer/killer-climb.png");
            climbrightIcon   = new ImageIcon("Graphic/Killer/killer-climb.png");
            offsetX = 49;
            offsetY = 98;
            System.out.println("killer image initialized");
        }
        
        else {
            idleImage  = new ImageIcon("Graphic/Human/" + role + "/" + role + "-stand.png");
            backImage  = new ImageIcon("Graphic/Human/" + role + "/" + role + "-back.png");
            frontImage = new ImageIcon("Graphic/Human/" + role + "/" + role + "-front.png");
            rightIcon  = new ImageIcon("Graphic/Human/" + role + "/" + role + "-right.png");
            leftIcon   = new ImageIcon("Graphic/Human/" + role + "/" + role + "-left.png");
            standImage = new ImageIcon("Graphic/Human/" + role + "/" + role + "-stand.png");
            downImage  = new ImageIcon("Graphic/Human/" + role + "/" + role + "-down.png");
            climbleftIcon  = new ImageIcon("Graphic/Human/" + role + "/" + role + "-climbLeft.png");
            climbrightIcon = new ImageIcon("Graphic/Human/" + role + "/" + role + "-climbRight.png");
            offsetX = 30;
            offsetY = 90;
            attackLeftImage = null; // Survivor 沒有攻擊動作
            attackRightImage = null;
            System.out.println(role + " image initialized");
        }
        
        currentImage = idleImage;
        return currentImage;
    }
    public ImageIcon getBloodImage() {
        return bloodImage;
    }
    
    public void updateMovement(String direction) {
        // 根據按下的方向鍵來更新圖片
        currentImage = switch (direction) {
            case "W" -> backImage;
            case "A" -> leftIcon;
            case "S" -> frontImage;
            case "D" -> rightIcon;
            case "" -> standImage;
            default -> idleImage;
        };
    }
    

    public void setAction(String action) {
         if (action.equals("UP")
         ||action.equals("UP_LEFT")
         ||action.equals("DOWN_LEFT")
         ||action.equals("LEFT")) {
            currentImage = attackLeftImage;
        } else if (action.equals("DOWN")
        ||action.equals("DOWN_RIGHT")
        ||action.equals("UP_RIGHT")
        ||action.equals("RIGHT")) {
            currentImage = attackRightImage;
        }

    }

    public void setDownImage() {
        if(status.equals("倒地")) {
            currentImage = downImage;
        }
    }
    // public void setClimbImage(String direction) {
    //      if (direction.equals("UP_LEFT")) {
    //          currentImage = climbleftIcon;
    //      } else if (direction.equals("UP_RIGHT")) {
    //          currentImage = climbrightIcon;
    //      }
    //  }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    // private void resetToIdleAfterDelay() {
    //     Timer timer = new Timer(2000, e -> {
    //         currentImage = idleImage;
    //         ((Timer) e.getSource()).stop(); // 停止計時器
    //     });
    //     timer.setRepeats(false); // 僅執行一次
    //     timer.start();
    // }

    
}
