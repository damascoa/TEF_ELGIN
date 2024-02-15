package com.metre.test;

import com.elgin.tef.enums.Operacao;
import com.elgin.tef.impl.TEFElgin;
import com.elgin.tef.enums.TipoColeta;
import com.elgin.tef.inputs.DadosPagamentoTef;

import java.math.BigDecimal;
import java.rmi.UnexpectedException;

public class StartTest {
    public static void main(String[] args) {


        TEFElgin tef = new TEFElgin();
        try {
            tef.SetClientTCP("127.0.0.1", 60906);
        } catch (UnexpectedException e) {
            e.printStackTrace();
        }
        try {
            tef.ConfigurarDadosPDV("Metre Sistemas", "v1.06.000", "Metre", "01", "T0004");
        } catch (UnexpectedException e) {
            e.printStackTrace();
        }
        tef.IniciarOperacaoTEF();
        tef.RealizarPagamentoTEF(Operacao.CREDITO, new DadosPagamentoTef("1" , new BigDecimal(10.2)), true);
      //  tef.ConfirmarOperacaoTEF();

    }
}
