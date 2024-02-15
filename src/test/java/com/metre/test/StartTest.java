package com.metre.test;

import com.elgin.tef.TEFElgin;
import com.elgin.tef.enums.TipoColeta;
import com.elgin.tef.util.VMCheck;

public class StartTest {
    public static void main(String[] args) {


        TEFElgin tef = new TEFElgin();
        tef.SetClientTCP("127.0.0.1", 60906);
        tef.ConfigurarDadosPDV("Metre Sistemas", "v1.06.000", "Metre", "01", "T0004");
        tef.GetProdutoTef();
        tef.IniciarOperacaoTEF();

        tef.RealizarColetaPinPad(TipoColeta.RG, true);
    }
}
