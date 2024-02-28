package com.metre.test;

import com.elgin.tef.enums.Acao;
import com.elgin.tef.enums.Operacao;
import com.elgin.tef.enums.OperacaoAdministrativa;
import com.elgin.tef.impl.TEFElgin;
import com.elgin.tef.inputs.DadosPagamentoTef;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class AdministrativoTeste {
    public static JsonObject retorno;
    public static List<String> opcoes = new ArrayList<>();
    public static String mensagemResultado;

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

        retorno = tef.RealizarAdmTEF(OperacaoAdministrativa.SELECIONA, "{}", true);
        int coletaSequencial = 0;

        while(tef.isEmColeta()){
            coletaSequencial = retorno.getAsJsonObject("tef").get("automacao_coleta_sequencial").getAsInt();
          if(tef.emColeta){
              Scanner myObj = new Scanner(System.in);  // Create a Scanner object
              String index = myObj.nextLine();
              String opcao = tef.getOpcoes().size() > 1 ?  tef.getOpcoes().get(Integer.parseInt(index)).trim() :index;
              retorno = tef.RealizarAdmTEF(OperacaoAdministrativa.SELECIONA, "{\"automacao_coleta_informacao\":\""+opcao+"\",\"automacao_coleta_retorno\":\"0\",\"automacao_coleta_sequencial\":\""+coletaSequencial+"\"}", false);
          }else{
              retorno = tef.RealizarAdmTEF(OperacaoAdministrativa.SELECIONA, "{\"automacao_coleta_retorno\":\"0\",\"automacao_coleta_sequencial\":\""+coletaSequencial+"\"}", false);
          }
        }
        System.out.println("FIM DA COLETA! ");
        String mensagemResultado = tef.getMensagemResultado();
                if(mensagemResultado.contains("Transacao em andamento")){
                  String op =  tef.ConfirmarOperacaoTEF(retorno.getAsJsonObject("tef").get("sequencial").getAsInt(), Acao.CANCELAR);
                    System.out.println(op);
                }else{
                    System.err.println(mensagemResultado);
                }

    }

    private static void verificarSeTemOpcoesASelecionar() {
        opcoes.clear();
        if(retorno.getAsJsonObject("tef").toString().contains("automacao_coleta_opcao")){
            opcoes.addAll(Arrays.asList(retorno.getAsJsonObject("tef").get("automacao_coleta_opcao").getAsString().split(";")));
        }

        if(opcoes.size() > 0){
            System.out.println("\nSELECIONE UMA OPÇCAO\n");
            int index = 0;
            for(String s : opcoes){
                System.out.println(index+"-"+s);
                index++;
            }
        }
    }

    private static void capturarMensagemResultado(){
        if(retorno.getAsJsonObject("tef").toString().contains("mensagemResultado")){
            mensagemResultado = retorno.getAsJsonObject("tef").get("mensagemResultado").getAsString();
        }
    }
}
