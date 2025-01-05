import javax.swing.*;

public class ClientPlayer extends ClientGameObject {
    private String role;
    private boolean isSelf = false;

    private ImageIcon idleImage;
    private ImageIcon attackLeftImage;
    private ImageIcon attackRightImage;
    private ImageIcon currentImage;
    private ImageIcon backImage;
    private ImageIcon frontImage;
    private ImageIcon rightIcon;
    private ImageIcon leftIcon;

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
    
    public ImageIcon getCurrentImage() {
        return currentImage;
    }
    public ImageIcon initImage() {
        if (role.equals("killer")) {
            idleImage = new ImageIcon("Graphic/Killer/killer-left.png");
            attackLeftImage = new ImageIcon("Graphic/Killer/killer-attackLeft.png");
            attackRightImage = new ImageIcon("Graphic/Killer/killer-attackRight.png");
            backImage = new ImageIcon("Graphic/Killer/killer-left.png");
            frontImage = new ImageIcon("Graphic/Killer/killer-right.png");
            rightIcon = new ImageIcon("Graphic/Killer/killer-right.png");
            leftIcon = new ImageIcon("Graphic/Killer/killer-left.png");
            System.out.println("killer image initialized");
        } else if (role.equals("p1")) {
            idleImage = new ImageIcon("Graphic/Human/p1/p1-front.png");
            backImage = new ImageIcon("Graphic/Human/p1-back.png");
            frontImage = new ImageIcon("Graphic/Human/p1-front.png");
            rightIcon = new ImageIcon("Graphic/Human/p1-right.png");
            leftIcon = new ImageIcon("Graphic/Human/p1-left.png");
            attackLeftImage = null; // Survivor 沒有攻擊動作
            attackRightImage = null;
            System.out.println("p1 image initialized");
        } else if (role.equals("p2")) {
            idleImage = new ImageIcon("Graphic/Human/p2/p2-front.png");
            backImage = new ImageIcon("Graphic/Human/p2-back.png");
            frontImage = new ImageIcon("Graphic/Human/p2-front.png");
            rightIcon = new ImageIcon("Graphic/Human/p2-right.png");
            leftIcon = new ImageIcon("Graphic/Human/p2-left.png");
            attackLeftImage = null; // Survivor 沒有攻擊動作
            attackRightImage = null;
            System.out.println("p2 image initialized");
        } else if (role.equals("p3")) {
            idleImage = new ImageIcon("Graphic/Human/p3/p3-front.png");
            backImage = new ImageIcon("Graphic/Human/p3-back.png");
            frontImage = new ImageIcon("Graphic/Human/p3-front.png");
            rightIcon = new ImageIcon("Graphic/Human/p3-right.png");
            leftIcon = new ImageIcon("Graphic/Human/p3-left.png");
            attackLeftImage = null; // Survivor 沒有攻擊動作
            attackRightImage = null;
            System.out.println("p3 image initialized");
        }
        currentImage = idleImage;
        
        return currentImage;
    }
    
    

    // public void updateMovement(String direction) {
    //     // 根據按下的方向鍵來更新圖片
    //     switch (direction) {
    //         case "W":
    //             currentImage = backImage;
    //             break;
    //         case "A":
    //             currentImage = leftIcon;
    //             break;
    //         case "S":
    //             currentImage = frontImage;
    //             break;
    //         case "D":
    //             currentImage = rightIcon;
    //             break;
    //         default:
    //             currentImage = idleImage;
    //             break;
    //     }
    // }
    

    public void setAction(String action) {
        if (action.equals("attack")) {
            currentImage = attackLeftImage;;
        } else if (action.equals("attackLeft")) {
            currentImage = attackLeftImage;
            resetToIdleAfterDelay();  // 攻擊後兩秒恢復閒置
        } else if (action.equals("attackRight")) {
            currentImage = attackRightImage;
            resetToIdleAfterDelay();  // 攻擊後兩秒恢復閒置
        }
    }

    private void resetToIdleAfterDelay() {
        Timer timer = new Timer(2000, e -> {
            currentImage = idleImage;
            ((Timer) e.getSource()).stop(); // 停止計時器
        });
        timer.setRepeats(false); // 僅執行一次
        timer.start();
    }
}
