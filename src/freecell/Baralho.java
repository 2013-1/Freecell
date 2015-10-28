package freecell;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Yan
 */
public class Baralho {

  public static final int PILHA = 0;
  public static final int AUXILIAR = 1;
  public static final int CRESCENTE = 2;

  private final List<Carta> baralho;
  private Carta cartaRaiz;
  private boolean regraAtivada;
  private int tipo;

  public Baralho() {
    baralho = new ArrayList<>();
    cartaRaiz = Carta.cartaNeutra();
    regraAtivada = false;
    tipo = PILHA;
  }

  public void empilhar(Carta novaCarta) throws RequisitoPilhaExcessao {
    if (regraAtivada) {
      verificarIntegridade(novaCarta);
    }
    baralho.add(novaCarta);
    adicionarPosicao(novaCarta);
    if (tipo != CRESCENTE) {
      novaCarta.habilitarMovimento();
    }
  }

  private void verificarIntegridade(Carta novaCarta) throws RequisitoPilhaExcessao {
    switch (tipo) {
      case PILHA:
        try {
          boolean coresIguais = novaCarta.getCor().equals(topo().getCor());
          boolean decrescente = novaCarta.getNumero() == topo().getNumero() - 1;
          if (coresIguais || !decrescente) {
            throw new RequisitoPilhaExcessao("As cores sao iguais ou a ordem nao esta decrecente");
          }
        }
        catch (PilhaVaziaException ex) {
          //se a pilha estiver vazia, simplismente continua, nao ha regras
        }
        break;
      case AUXILIAR:
        if (!estaVazio()) {
          throw new RequisitoPilhaExcessao("Esta pilha suporta apenas uma carta");
        }
        break;
      case CRESCENTE:
        boolean crescente;
        boolean naipesIguais = novaCarta.getNaipe().equals(cartaRaiz.getNaipe());
        try {
          crescente = novaCarta.getNumero() == topo().getNumero() + 1;
        }
        catch (PilhaVaziaException ex) {
          crescente = novaCarta.getNumero() == Carta.AS;
        }
        if (!naipesIguais || !crescente) {
          throw new RequisitoPilhaExcessao("Esta pilha suporta ordem crescente de mesmo naipe");
        }
        break;
    }
  }

  private void adicionarPosicao(Carta novaCarta) {
    Point novaPosicao;
    novaPosicao = cartaRaiz.getLocation();
    if (tipo == PILHA) {
      novaPosicao.y += 30 * baralho.indexOf(novaCarta);
    }
    novaPosicao.x++;
    try {
      Carta penultimaCarta = baralho.get(baralho.size() - 2);
      penultimaCarta.desabilitarMovimento();
    }
    catch (ArrayIndexOutOfBoundsException e) {
    }

    Animacao.moverObjeto(novaCarta, novaPosicao);
  }

  public Carta desempilhar() throws PilhaVaziaException, PilhaCrescenteRemoverExcessao {
    if (estaVazio()) {
      throw new PilhaVaziaException("pilha vazia");
    }
    if (tipo == CRESCENTE && regraAtivada) {
      throw new PilhaCrescenteRemoverExcessao("nao se pode retirar dessa pilha");
    }
    Carta ultimaCarta = baralho.remove(baralho.size() - 1);
    ultimaCarta.desabilitarMovimento();

    try {
      Carta penultimaCarta = topo();
      penultimaCarta.habilitarMovimento();
    }
    catch (PilhaVaziaException ex) {

    }

    return ultimaCarta;
  }

  public Carta topo() throws PilhaVaziaException {
    if (estaVazio()) {
      throw new PilhaVaziaException("pilha vazia");
    }
    return baralho.get(baralho.size() - 1);
  }

  public void embaralhar() {
    Collections.shuffle(baralho);
  }

  public void setNaipe(String naipe) {
    cartaRaiz = Carta.cartaNeutra(naipe);
  }

  public Carta getRaiz() {
    return cartaRaiz;
  }

  public boolean estaVazio() {
    return baralho.isEmpty();
  }

  public void preencherBaralho() {
    String[] naipes = Carta.listaNaipes();
    for (String naipe : naipes) {
      for (int numero = 1; numero <= 13; numero++) {
        Carta carta = new Carta(numero, naipe);
        baralho.add(carta);
      }
    }
  }

  boolean contem(Carta carta) {
    return baralho.contains(carta);
  }

  public void ativarRegra() {
    this.regraAtivada = true;
  }

  public void desativarRegra() {
    this.regraAtivada = false;
  }

  void definirTipo(int tipo) {
    this.tipo = tipo;
    if (tipo != PILHA) {
      ativarRegra();
    }
  }

  public int qdtCartas() {
    return baralho.size();
  }

  public int getTipo() {
    return tipo;
  }

  public static class PilhaVaziaException extends Exception {

    public PilhaVaziaException(String mensagem) {
      super(mensagem);
    }
  }

  public static class PilhaCheiaException extends Exception {

    public PilhaCheiaException(String mensagem) {
      super(mensagem);
    }
  }

  public static class RequisitoPilhaExcessao extends Exception {

    public RequisitoPilhaExcessao(String mensagem) {
      super(mensagem);
    }
  }

  public static class PilhaCrescenteRemoverExcessao extends Exception {

    public PilhaCrescenteRemoverExcessao(String mensagem) {
      super(mensagem);
    }
  }
}
