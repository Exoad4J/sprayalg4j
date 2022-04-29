package spray;

import java.awt.Point;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.geom.Ellipse2D;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.exoad.main.UIFactory;

import javax.swing.JPanel;

public class Trajectory extends JPanel implements MouseListener {
  public static final int MAX_POINTS = 15;
  private static final Random r = new Random();

  public static class Bullet extends Point {
    /**
     * @param x The amount of x from the origin of the Mouse
     * @param y The amount of y from the origin of the Mouse
     */
    public Bullet(int x, int y) {
      super(x, y);
    }

    public Ellipse2D getShape() {
      return new Ellipse2D.Double(x, y, 5, 5);
    }
  }

  protected Bullet[] mainSpray;
  private int radi;
  private int min_radi;
  private Thread worker = new Thread();
  private boolean deviateFirst;

  private static Bullet[] generateRandom(int size, int bound) {
    Bullet[] spray = new Bullet[size];
    for (int i = 0; i < size; i++) {
      spray[i] = new Bullet(r.nextInt(bound), r.nextInt(bound));
    }
    return spray;
  }

  private ArrayList<Point> spray;

  public Trajectory(int radi, int min_radi, boolean deviateFirst, Bullet[] mainSpray) {
    super();
    setPreferredSize(UIFactory.DEFAULT_SIZE);
    addMouseListener(this);
    addMouseListener(this);
    setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    this.radi = radi;
    this.min_radi = min_radi;
    this.mainSpray = mainSpray;
    this.deviateFirst = deviateFirst;
    spray = new ArrayList<>();
  }

  public Trajectory(int radi, int min_radi, boolean deviateFirst, int mainSpraySize, int sprayBound) {
    this(radi, min_radi, deviateFirst, generateRandom(mainSpraySize, sprayBound));
  }

  public Bullet deviate(Bullet p, boolean isFirst) {
    return isFirst ? p : new Bullet(p.x + r.nextInt(radi) - min_radi, p.y + r.nextInt(radi) - min_radi);
  }

  private Point current = null;

  /**
   * Original Implementation:
   * int i = 0;
   * if (!deviateFirst) {
   * g2.fillRect(current.x, current.y, 10, 10);
   * i++;
   * }
   * for (; i < mainSpray.length; i++) {
   * g2.fillRect(deviate(new Bullet(current.x - mainSpray[i].x, current.y -
   * mainSpray[i].y), false).x,
   * deviate(new Bullet(current.x - mainSpray[i].x, current.y - mainSpray[i].y),
   * false).y,
   * 10, 10);
   * }
   */
  @Override
  public synchronized void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (current != null) {
      Graphics2D g2 = (Graphics2D) g;
      g2.setColor(new Color(0, 0, 0, 150));
      int i = 0 ;
      if (!deviateFirst) {
        g2.fillRect(spray.get(i).x, spray.get(i).y, 10, 10);
        i++;
      }
      for (int j = 0; i < spray.size() && j < mainSpray.length; i++, j++) {
        System.out.println(spray.get(i));
        g2.fillRect(deviate(new Bullet(spray.get(i).x - mainSpray[j].x, spray.get(i).y -
            mainSpray[j].y), false).x,
            deviate(new Bullet(spray.get(i).x - mainSpray[j].x, spray.get(i).y - mainSpray[j].y),
                false).y,
            10, 10);
      }
      worker = new Thread(() -> {
        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        for(Point p : spray) {
          g2.clearRect((int) p.getX(), (int) p.getY(), 10, 10);
        }
        spray.clear();
        repaint();
      });
      worker.start();
    }
  }

  @Override
  public void mouseClicked(MouseEvent e) {
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }
  @Override
  public synchronized void mousePressed(MouseEvent e) {
    if(!worker.isInterrupted()) {
      worker.interrupt();
    }
    current = new Point(e.getX(), e.getY());
    spray.add(current);
    repaint();
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    System.out.println(spray.size());
    repaint();
  }
}
