package freecell;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

/**
 *
 * @author Yan
 */
public class Cenario extends JFrame {

  private final int quantidadePilhas = 8;
  private Baralho[] baralhosPilha;
  private Baralho[] baralhosAuxiliares;
  private Baralho[] baralhosTerminais;

  public static void main(String[] args) throws Baralho.RequisitoPilhaExcessao {
    new Cenario().setVisible(true);

  }

  public Cenario() throws Baralho.RequisitoPilhaExcessao {
    try {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    iniciarComponentes();
    instanciarTodosBaralhos();
    distribuirCartas();
  }

  private void iniciarComponentes() {
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(1050, 650);
    setLocationRelativeTo(null);
    setLayout(null);
    setResizable(false);
    setIconImage(new ImageIcon(getClass().getResource("/imagens/icone.png")).getImage());
    setTitle("Freecell");

    JMenuBar menu = new JMenuBar();
    setJMenuBar(menu);
    JMenu mjogo = new JMenu("jogo");
    menu.add(mjogo);
    JMenuItem itemNovoJogo = new JMenuItem("Novo Jogo");
    mjogo.add(itemNovoJogo);
    itemNovoJogo.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent ae) {
        try {
          distribuirCartas();
        }
        catch (Baralho.RequisitoPilhaExcessao ex) {
          try {
            limparCartas(baralhosPilha);
            limparCartas(baralhosTerminais);
            limparCartas(baralhosAuxiliares);
            distribuirCartas();
          }
          catch (Baralho.PilhaVaziaException | Baralho.PilhaCrescenteRemoverExcessao | Baralho.RequisitoPilhaExcessao ex1) {
            Logger.getLogger(Cenario.class.getName()).log(Level.SEVERE, null, ex1);
          }
        }
      }
    });

    JLabel fundo = new JLabel();
    fundo.setSize(getSize());
    fundo.setLocation(0, -25);
    fundo.setIcon(new ImageIcon(getClass().getResource("/imagens/background.png")));
    add(fundo);
  }

  private void instanciarTodosBaralhos() {
    baralhosPilha = instanciaBaralho(quantidadePilhas, new Point(25, 200), Baralho.PILHA);
    baralhosAuxiliares = instanciaBaralho(4, new Point(0, 20), Baralho.AUXILIAR);
    baralhosTerminais = instanciaBaralho(4, new Point(540, 20), Baralho.CRESCENTE);
  }

  private void limparCartas(Baralho[] baralhos) throws Baralho.PilhaVaziaException, Baralho.PilhaCrescenteRemoverExcessao {
    for (Baralho baralho : baralhos) {
      baralho.desativarRegra();

      while (!baralho.estaVazio()) {
        Carta carta = baralho.desempilhar();
        remove(carta);
        repaint();
      }
      if (baralho.getTipo() != Baralho.PILHA) {
        baralho.ativarRegra();
      }
    }
  }

  private void distribuirCartas() throws Baralho.RequisitoPilhaExcessao {
    Baralho baralhoCompleto = new Baralho();
    baralhoCompleto.preencherBaralho();
    baralhoCompleto.embaralhar();

    while (!baralhoCompleto.estaVazio()) {
      for (Baralho baralho : baralhosPilha) {
        try {
          Carta carta = baralhoCompleto.desempilhar();
          baralho.empilhar(carta);
          add(carta, 0);
          eventosCarta(carta);
        }
        catch (Baralho.PilhaVaziaException | Baralho.PilhaCrescenteRemoverExcessao ex) {
          break;
        }
      }
    }
    for (Baralho baralho : baralhosPilha) {
      baralho.ativarRegra();
    }
  }

  private Baralho[] instanciaBaralho(int quantidade, Point pontoInicial, int tipo) {
    Baralho[] listaBaralho = new Baralho[quantidade];
    String naipes[] = Carta.listaNaipes();
    for (int i = 0; i < listaBaralho.length; i++) {
      listaBaralho[i] = new Baralho();
      listaBaralho[i].definirTipo(tipo);
      if (tipo == Baralho.CRESCENTE) {
        listaBaralho[i].setNaipe(naipes[i]);
      }
      listaBaralho[i].getRaiz().setLocation(120 * i + 20 + pontoInicial.x, pontoInicial.y);
      add(listaBaralho[i].getRaiz(), 0);
    }
    return listaBaralho;
  }

  private void eventosCarta(final Carta carta) {
    carta.addMouseListener(new MouseListener() {

      @Override
      public void mouseClicked(MouseEvent e) {

      }

      @Override
      public void mousePressed(MouseEvent e) {
        moverParaFrente(carta);
      }

      private void moverParaFrente(Carta carta) {
        try {
          Baralho baralhoDono = procurarDono(carta);
          Carta primeiraCarta = baralhoDono.topo();
          if (carta == primeiraCarta) {
            remove(carta);
            add(carta, 0);
          }
        }
        catch (Baralho.PilhaVaziaException | NullPointerException ex) {

        }
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        Baralho baralhoSelecionado = encontrarBaralhoProximo();
        Baralho baralhoDono = procurarDono(carta);
        moverParaFrente(carta);
        try {
          baralhoDono.desempilhar();
        }
        catch (Baralho.PilhaVaziaException ex) {
          //nao precisa fazer nada. Apenas segue o codigo restante.
        }
        catch (NullPointerException es) {
          return;
        }
        catch (Baralho.PilhaCrescenteRemoverExcessao ex) {
          return;
        }
        try {
          if (baralhoSelecionado == null) {
            throw new Baralho.RequisitoPilhaExcessao("deu erro");
          }
          baralhoSelecionado.empilhar(carta);

        }
        catch (Baralho.RequisitoPilhaExcessao ex) {
          try {
            baralhoDono.desativarRegra();
            baralhoDono.empilhar(carta);
            baralhoDono.ativarRegra();
          }
          catch (Baralho.RequisitoPilhaExcessao ex1) {
            Logger.getLogger(Cenario.class.getName()).log(Level.SEVERE, null, ex1);
          }
        }
      }

      private Baralho encontrarBaralhoProximo() {
        Baralho[] baralhosdaVez = baralhosPilha;
        Baralho baralhoSelecionado = procurarNoConjunto(baralhosdaVez);
        if (baralhoSelecionado == null) {
          baralhosdaVez = baralhosAuxiliares;
          baralhoSelecionado = procurarNoConjunto(baralhosdaVez);
          if (baralhoSelecionado == null) {
            baralhosdaVez = baralhosTerminais;
            baralhoSelecionado = procurarNoConjunto(baralhosdaVez);
          }
        }
        return baralhoSelecionado;
      }

      private Baralho procurarNoConjunto(Baralho[] baralhosdaVez) {
        Baralho baralhoSelecionado = null;
        int valMaior = 0;
        for (Baralho baralho : baralhosdaVez) {
          Carta raiz;
          try {
            raiz = baralho.topo();
          }
          catch (Baralho.PilhaVaziaException ex) {
            raiz = baralho.getRaiz();
          }
          if (carta == raiz) {
            continue;
          }
          int intersecao = carta.qtdPixelsIntersecao(raiz);
          if (intersecao > valMaior) {
            valMaior = intersecao;
            baralhoSelecionado = baralho;
          }
        }
        return baralhoSelecionado;
      }

      @Override
      public void mouseEntered(MouseEvent e) {

      }

      @Override
      public void mouseExited(MouseEvent e) {

      }

    });

  }

  public Baralho procurarDono(Carta carta) {
    Baralho[] baralhosAnalisado = baralhosPilha;
    Baralho baralhoDono = procurarDonoLista(baralhosAnalisado, carta);

    if (baralhoDono == null) {
      baralhosAnalisado = baralhosTerminais;
      baralhoDono = procurarDonoLista(baralhosAnalisado, carta);
      if (baralhoDono == null) {
        baralhosAnalisado = baralhosAuxiliares;
        baralhoDono = procurarDonoLista(baralhosAnalisado, carta);
      }
    }

    return baralhoDono;

  }

  private Baralho procurarDonoLista(Baralho[] baralhosAnalisado, Carta carta) {
    Baralho segundaOpcao = null;
    for (Baralho baralho : baralhosAnalisado) {
      try {
        if (baralho.topo() == carta) {
          return baralho;
        }
      }
      catch (Baralho.PilhaVaziaException ex) {
        segundaOpcao = null;
      }
    }
    return segundaOpcao;
  }

}
