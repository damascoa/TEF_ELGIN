package com.elgin.tef.interfaces;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;

public interface E1_Tef01 extends StdCallLibrary {


    E1_Tef01 INSTANCE = (E1_Tef01) Native.loadLibrary("C:\\Chef\\Lib\\elgin\\x64\\E1_Tef01", E1_Tef01.class);

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
