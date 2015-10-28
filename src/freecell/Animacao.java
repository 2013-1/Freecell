package freecell;

import java.awt.Point;
import javax.swing.JComponent;

/**
 * Classe para movimentacao de qualquer JComponent (JPanel, JLabel, etc.) de uma
 * forma generica e rapida. Possui algumas funcoes estaticas para deixa-la
 * pronta pra uso.
 *
 * @author Yan Kaic
 * @since 2015
 */
public class Animacao extends Thread {

  private final JComponent objeto;
  private final Point pontoPartida;
  private final Point distancia;
  private final double seno;
  private final double cosseno;
  private final double hipotenusa;
  private static int velocidade = 27;

  /**
   * Construtor padrao para a animacao de um objeto. <br>
   * Este construtor apenas configura como a animacao sera realizada. Para que a
   * animacao comece, basta utilizar a funcao start() de Thread.
   *
   * @param objeto objeto a ser movimentado
   * @param pontoChegada ponto que o objeto vai ficar quando a animacao
   * terminar.
   */
  public Animacao(JComponent objeto, Point pontoChegada) {
    this.objeto = objeto;
    pontoPartida = objeto.getLocation();

    distancia = new Point();
    distancia.x = pontoChegada.x - pontoPartida.x;
    distancia.y = pontoChegada.y - pontoPartida.y;

    hipotenusa = pontoPartida.distance(pontoChegada);
    seno = distancia.y / hipotenusa;
    cosseno = distancia.x / hipotenusa;
  }

  /**
   * Define o valor do salto da velocidade da animacao. <br>
   * Eh a medida do passo da animacao - nao eh o tempo do sleep da thread.
   * Quanto maior o valor inserido, maior sera a velocidade da animacao.
   *
   * @param velocidade passo da animacao.
   */
  public static void definirVelocidade(int velocidade) {
    Animacao.velocidade = velocidade;
  }

  @Override
  public void run() {

    //Vai aumentando a distancia e descobrindo seus pontos x e y.
    for (int hipotenusaVariavel = 0; hipotenusaVariavel < hipotenusa; hipotenusaVariavel += velocidade) {
      try {
        int catetoAdjacente = (int) (cosseno * hipotenusaVariavel) + pontoPartida.x;
        int catetoOposto = (int) (seno * hipotenusaVariavel) + pontoPartida.y;
        objeto.setLocation(catetoAdjacente, catetoOposto);
        sleep(17);
      }
      catch (InterruptedException ex) {
        ex.printStackTrace();
      }
    }
    //coloca o objeto no ponto final que foi pedido.
    int catetoAdjacente = (int) (cosseno * hipotenusa) + pontoPartida.x;
    int catetoOposto = (int) (seno * hipotenusa) + pontoPartida.y;
    objeto.setLocation(catetoAdjacente, catetoOposto);
  }

  /**
   * Esta funcao permite que um objeto seja movimento sem precisar instanciar o
   * objeto animacao dentro do seu codigo. Basta inserir o objeto e ponto final
   * desejado. Nota: observe a velocidade antes de usar esta funcao.
   *
   * @param objeto objeto a ser movido
   * @param destino ponto que deseja que o objeto fique depois da animacao.
   */
  public static void moverObjeto(JComponent objeto, Point destino) {
    Animacao animacao = new Animacao(objeto, destino);
    animacao.start();
  }

}
