package com.metre.test;

import com.elgin.tef.enums.Acao;
import com.elgin.tef.enums.Operacao;
import com.elgin.tef.enums.OperacaoAdministrativa;
import com.elgin.tef.impl.TEFElgin;
import com.elgin.tef.inputs.DadosPagamentoTef;
import com.google.gson.JsonObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.rmi.UnexpectedException;
import java.util.Random;
import java.util.Scanner;

public class PagamentoTeste {
    public static JsonObject retorno;
    public static BigDecimal valor = generateRandomBigDecimal();
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
        retorno =  tef.IniciarOperacaoTEF();

        Integer sequencia = retorno.getAsJsonObject("tef").get("sequencial").getAsInt()+1;




        retorno =  tef.RealizarPagamentoTEF(Operacao.CREDITO, new DadosPagamentoTef(sequencia+"", valor), true);

        //VALIDAR SE NAO TEM TRANSACAO PENDENTE!
        System.out.println(retorno);
        if(retorno.get("tef").getAsJsonObject().asMap().containsKey("mensagemResultado") &&
                retorno.get("tef").getAsJsonObject().get("mensagemResultado").getAsString().contains("Erro ao verificar se existem transações pendentes")){
            tef.ConfirmarTodasTransacoesPendentes();
            retorno =  tef.RealizarPagamentoTEF(Operacao.CREDITO, new DadosPagamentoTef(sequencia+"", valor), false);
        }

        int coletaSequencial = 0;

        while(tef.isEmColeta()){
            coletaSequencial = retorno.getAsJsonObject("tef").get("automacao_coleta_sequencial").getAsInt();
            if(tef.emColeta){
                Scanner myObj = new Scanner(System.in);  // Create a Scanner object
                String index = myObj.nextLine();
                String opcao = tef.getOpcoes().size() > 1 ?  tef.getOpcoes().get(Integer.parseInt(index)).trim() :index;
                retorno = tef.RealizarPagamentoTEF2(Operacao.SELECIONA, "{\"automacao_coleta_informacao\":\""+opcao+"\",\"automacao_coleta_retorno\":\"0\",\"automacao_coleta_sequencial\":\""+coletaSequencial+"\"}", false);
            }else{
                retorno = tef.RealizarPagamentoTEF2(Operacao.SELECIONA, "{\"automacao_coleta_retorno\":\"0\",\"automacao_coleta_sequencial\":\""+coletaSequencial+"\"}", false);
            }
        }
        System.out.println("FIM DA COLETA! ");
        String mensagemResultado = tef.getMensagemResultado();
        Integer sequencial = retorno.getAsJsonObject("tef").asMap().containsKey("sequencial") ? retorno.getAsJsonObject("tef").get("sequencial").getAsInt()  : null;
        if(mensagemResultado.contains("Transacao em andamento")){
            System.out.println("CANCELAR AÇAO");
            String op =  tef.ConfirmarOperacaoTEF(sequencial, Acao.CANCELAR);
            System.out.println(op);
        }else{
            System.err.println(mensagemResultado);
            tef.ConfirmarOperacaoTEF(sequencial, Acao.CONFIRMAR);
            tef.FinalizarOperacaoTEF(sequencial);
        }
       JsonObject o = tef.FinalizarOperacaoTEF(sequencial);
        System.out.println(o);

        if(tef.dadosAprovacao != null){
            System.out.println("COMPROVANTE VIA CLIENTE");
            System.out.println(tef.dadosAprovacao.getComprovanteDiferenciadoPortador());
        }



    }

    public static BigDecimal generateRandomBigDecimal() {
        Random random = new Random();
        // Gera um valor aleatório double entre 0.01 e 99.99
        double randomValue = 0.01 + (99.99 - 0.01) * random.nextDouble();
        // Converte o valor double para BigDecimal e arredonda para 2 casas decimais
        return BigDecimal.valueOf(randomValue).setScale(2, RoundingMode.HALF_UP);
    }
}
