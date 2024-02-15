package com.elgin.tef.interfaces;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;

public interface E1_Tef01 extends StdCallLibrary {


    E1_Tef01 INSTANCE = (E1_Tef01) Native.loadLibrary("C:\\Elgin\\Executavel\\E1_Tef01", E1_Tef01.class);

    public static  String ADM_USUARIO = "";
    public static  String ADM_SENHA = "";
    public static  int OPERACAO_TEF = 0;
    public static  int OPERACAO_ADM = 1;
    public static  int OPERACAO_PIX = 2;
    public static String RetornoUI = "";
    public static String valorTotal = "";
    public static String cancelarColeta = "";
    public static int Operacao = OPERACAO_TEF;



    Pointer SetClientTCP(String ip, int porta);
    Pointer ConfigurarDadosPDV(String textoPinpad, String versaoAC, String nomeEstabelecimento, String loja, String identificadorPontoCaptura);
    Pointer IniciarOperacaoTEF(String dadosCaptura);
    Pointer RealizarPagamentoTEF(int codigoOperacao, String dadosCaptura, boolean novaTransacao);
    Pointer RealizarPixTEF(String dadosCaptura, boolean novaTransacao);
    Pointer RealizarAdmTEF(int codigoOperacao, String dadosCaptura, boolean novaTransacao);
    Pointer ConfirmarOperacaoTEF(int id, int acao);
    Pointer FinalizarOperacaoTEF(int id);
    Pointer RealizarColetaPinPad(int tipoColeta, boolean confirmar);
    Pointer ConfirmarCapturaPinPad(int tipoColeta, String dadosCaptura);
    Pointer GetClientTCP();
    Pointer RecuperarOperacaoTEF(String dadosCaptura);
    int GetProdutoTef();
}
