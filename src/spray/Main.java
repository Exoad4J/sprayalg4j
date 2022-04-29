package spray;

import com.exoad.main.UIFactory;

public class Main {
  public static void main(String[] args) {
    Trajectory.Bullet[] bullets = new Trajectory.Bullet[]{new Trajectory.Bullet(10, 9), new Trajectory.Bullet(10, 34), new Trajectory.Bullet(5, 40)};
    UIFactory ui = new UIFactory(false, null, new Trajectory(30, 3, true, bullets));
    ui.run();
  }
}