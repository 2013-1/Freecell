package freecell;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author Yan
 */
public class Carta extends JLabel {

  public static final String PAUS = "paus";
  public static final String COPAS = "copas";
  public static final String ESPADAS = "espadas";
  public static final String OUROS = "ouros";
  public static final String VERMELHO = "vermelho";
  public static final String PRETO = "preto";
  public static final int VALETE = 11;
  public static final int DAMAS = 12;
  public static final int REIS = 13;
  public static final int AS = 1;

  private final int numero;
  private final String naipe;
  private final String cor;
  private Icon icone;
  private boolean movimentoHabilitado;

  public Carta(int numero, String naipe) {
    this.numero = numero;
    this.naipe = naipe;
    this.cor = naipe.equals(COPAS) || naipe.equals(OUROS) ? VERMELHO : PRETO;
    this.movimentoHabilitado = false;
    montarVisual();
    eventosMouse();
  }

  private void montarVisual() {
    icone = new ImageIcon(getClass().getResource("/imagens/" + naipe + '/' + numero + ".png"));
    setIcon(icone);
    setSize(icone.getIconWidth(), icone.getIconHeight());
  }

  public static Carta cartaNeutra(String naipe) {
    return new Carta(0, naipe);
  }

  public static Carta cartaNeutra() {
    return new Carta(-1, PAUS);
  }

  public int getNumero() {
    return numero;
  }

  public String getNaipe() {
    return naipe;
  }

  public String getCor() {
    return cor;
  }

  private void eventosMouse() {
    addMouseMotionListener(new MouseMotionListener() {

      @Override
      public void mouseDragged(MouseEvent e) {
        if (movimentoHabilitado) {
          Point mouse = MouseInfo.getPointerInfo().getLocation();
          Point tela = getLocationOnScreen();

          tela.x -= getLocation().x;
          tela.y -= getLocation().y;

          mouse.x -= tela.x + icone.getIconWidth() / 2;
          mouse.y -= tela.y + icone.getIconHeight() / 2;

          setLocation(mouse);
        }
      }

      @Override
      public void mouseMoved(MouseEvent e) {
      }
    });
  }

  public void habilitarMovimento() {
    this.movimentoHabilitado = true;
  }

  public void desabilitarMovimento() {
    this.movimentoHabilitado = false;
  }

  public int qtdPixelsIntersecao(Carta carta) {
    Rectangle caixa1 = this.getBounds();
    Rectangle caixa2 = carta.getBounds();
    Rectangle intersecao = caixa1.intersection(caixa2);
    int encontrou = intersecao.width <= 0 || intersecao.height <= 0 ? 0 : 1;
    return (int) (intersecao.getHeight() * intersecao.getWidth() * encontrou);
  }

  public static String[] listaNaipes() {
    String naipes[] = {OUROS, ESPADAS, COPAS, PAUS};
    return naipes;
  }

  @Override
  public String toString() {
    return "carta=[" + numero + ',' + naipe + ']';
  }
  
  /**
   *
   */
  @Override
  public void enable() {
    System.out.println("fui usada");
  }
}
