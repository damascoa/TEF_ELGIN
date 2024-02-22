package com.metre.controllers;

import com.elgin.tef.enums.Acao;
import com.elgin.tef.enums.Operacao;
import com.elgin.tef.enums.OperacaoAdministrativa;
import com.elgin.tef.impl.TEFElgin;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.json.JSONObject;

import java.net.URL;
import java.rmi.UnexpectedException;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeController implements Initializable {
    @FXML
    public TextField iptValor;
    @FXML
    public TextArea txtLog;
    @FXML
    public ListView<String> list;

    JsonObject retorno;

    TEFElgin tef = new TEFElgin();

    private static final int OPERACAO_ADM = 0;
    private static final int OPERACAO_VENDER = 1; // Adicione constantes conforme necessário
    private int operacaoAtual = OPERACAO_VENDER;

    // Simulação do Executor para rodar tarefas em background
    private ExecutorService executor = Executors.newSingleThreadExecutor();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tef = new TEFElgin();
        try {
            tef.SetClientTCP("127.0.0.1", 60906);
            tef.ConfigurarDadosPDV("Metre Sistemas", "v1.06.000", "Metre", "01", "T0004");
        } catch (UnexpectedException e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void iniciarTEF(ActionEvent ae) {
        testeApiElginTEF();
    }

    @FXML
    public void ok(ActionEvent ae) {

    }


    public void testeApiElginTEF() {
        try {

            String start = iniciar();
            String retorno = getRetorno(start);
//            if (retorno.isEmpty() || !retorno.equals("1")) {
//                finalizar();
//                return;
//            }

            String sequencial = getSequencial(start);
            sequencial = incrementarSequencial(sequencial);

            String resp = vender(Operacao.SELECIONA, sequencial);

            retorno = getRetorno(resp);
            if (retorno.isEmpty()) {
                resp = coletar(0, new JSONObject(resp));
                retorno = getRetorno(resp);
            }

            if (retorno.isEmpty()) {
                writeLogs("ERRO AO COLETAR DADOS");
                print("ERRO AO COLETAR DADOS");
            } else if (retorno.equals("0")) {
                String comprovanteLoja = getComprovante(resp, "loja");
                String comprovanteCliente = getComprovante(resp, "cliente");
                writeLogs(comprovanteLoja);
                writeLogs(comprovanteCliente);
                writeLogs("TRANSAÇÃO OK, INICIANDO CONFIRMAÇÃO...");
                print("TRANSAÇÃO OK, INICIANDO CONFIRMAÇÃO...");
                sequencial = getSequencial(resp);
                String cnf = confirmar(sequencial);
                retorno = getRetorno(cnf);
                if (retorno.isEmpty() || !retorno.equals("1")) {
                    finalizar();
                }
            } else if (retorno.equals("1")) {
                writeLogs("TRANSAÇÃO OK");
                print("TRANSAÇÃO OK");
            } else {
                writeLogs("ERRO NA TRANSAÇÃO");
                print("ERRO NA TRANSAÇÃO");
            }

            String end = finalizar();
            retorno = getRetorno(end);
            if (retorno.isEmpty() || !retorno.equals("1")) {
                finalizar();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String iniciar() {
        JSONObject payload = new JSONObject();
        String start = tef.IniciarOperacaoTEFS();
        writeLogs("INICIAR " + start);
        return start;
    }


    public String adm(OperacaoAdministrativa opcao, String sequencial) {
        writeLogs("ADM SEQUENCIAL UTILIZADO NA VENDA: " + sequencial);
        JSONObject payload = new JSONObject();
        payload.put("sequencial", sequencial);

        // Comentei as linhas abaixo pois elas parecem estar relacionadas à autenticação,
        // que pode não ser necessária ou deve ser adaptada para sua implementação específica.
        // payload.put("transacao_administracao_usuario", ADM_USUARIO);
        // payload.put("transacao_administracao_senha", ADM_SENHA);
        // payload.put("admUsuario", ADM_USUARIO);
        // payload.put("admSenha", ADM_SENHA);

        String adm = tef.RealizarAdmTEF(opcao, payload.toString(), true);
        writeLogs("ADM " + adm);
        return adm;
    }

    public String vender(Operacao cartao, String sequencial) {
        writeLogs("VENDER SEQUENCIAL UTILIZADO NA VENDA: " + sequencial);

        JSONObject payload = new JSONObject();
        payload.put("sequencial", sequencial);

        // Remove todos os caracteres não numéricos do valorTotal antes de adicionar ao payload
        if (!iptValor.getText().isEmpty()) {
            String valorTotalCentavos = iptValor.getText().replaceAll("[^\\d]", "");
            payload.put("valorTotal", valorTotalCentavos);
        }

        String pgto;
//        if (operacao == OPERACAO_TEF) {
            String resp = tef.RealizarPagamentoTEF(cartao, payload.toString(), true);
            pgto = resp;
//        } else {
//            String resp = tef.RealizarPixTEF(payload.toString(), true);
//            pgto = resp;
//        }
        writeLogs("VENDER " + pgto);
        return pgto;
    }


    public String coletar(int operacao, JSONObject root) {
        String coletaRetorno = root.optString("tef.automacao_coleta_retorno");
        String coletaSequencial = root.optString("tef.automacao_coleta_sequencial");
        String coletaMensagem = root.optString("tef.mensagemResultado");
        String coletaTipo = root.optString("tef.automacao_coleta_tipo");
        String coletaOpcao = root.optString("tef.automacao_coleta_opcao");

        writeLogs("COLETAR: " + coletaMensagem.toUpperCase());
        print(coletaMensagem.toUpperCase());

        if (!coletaRetorno.equals("0")) {
            return root.toString();
        }

        JSONObject payload = new JSONObject();
        payload.put("automacao_coleta_retorno", coletaRetorno);
        payload.put("automacao_coleta_sequencial", coletaSequencial);

        String coletaInformacao = "";
        if (!coletaTipo.isEmpty() && coletaOpcao.isEmpty()) {
            writeLogs("INFORME O VALOR SOLICITADO: ");
            coletaInformacao = readInput();

            payload.put("automacao_coleta_informacao", coletaInformacao);
        } else if (!coletaTipo.isEmpty()) {
            String[] opcoes = coletaOpcao.split(";");
            for (int i = 0; i < opcoes.length; i++) {
                writeLogs("[" + i + "] " + opcoes[i].toUpperCase());
            }

            print(Arrays.toString(opcoes));
            writeLogs("\nDIGITE A OPÇÃO DESEJADA: ");
            coletaInformacao = opcoes[Integer.parseInt(readInput())];

            payload.put("automacao_coleta_informacao", coletaInformacao);
        }

        String resp = tef.RealizarAdmTEF(OperacaoAdministrativa.REIMPRESSAO, payload.toString(), false);
        String retorno = getRetorno(resp);
        if (!retorno.isEmpty()) {
            return resp;
        }

        return coletar(operacao, new JSONObject(resp));
    }



    public String confirmar(String sequencial) {
        writeLogs("CONFIRMAR OPERAÇÃO COM SEQUENCIAL: " + sequencial);
        print("AGUARDE, CONFIRMANDO OPERAÇÃO...");
        String cnf = tef.ConfirmarOperacaoTEF(Integer.parseInt(sequencial), Acao.CONFIRMAR);
        writeLogs(cnf);
        return cnf;
    }



    public String finalizar() {
      JsonObject objeto = tef.FinalizarOperacaoTEF(1);
        Integer resultado = objeto.get("codigo").getAsInt();
        writeLogs("Finalizar: " + resultado);
        print("OPERAÇÃO FINALIZADA!");
        return resultado+"";
    }

    private void writeLogs(String message) {
        Platform.runLater(() -> {
            String divLogs  = "\n==============================================\n";
            txtLog.appendText(divLogs + message);
        });
    }

    private void print(String message) {
        // Implementação depende do contexto da aplicação, exemplo:
        Platform.runLater(() -> System.out.println(message));
    }

    public String getSequencial(String resp) {
        JSONObject jsonDic = new JSONObject(resp);
      try {
          return jsonDic.getJSONObject("tef").getString("sequencial");
      }catch (Exception e){
          System.out.println(resp);
          e.printStackTrace();
          return "";
      }
    }

    public String incrementarSequencial(String sequencial) {
        try {
            double value = Double.parseDouble(sequencial);
            if (!Double.isNaN(value) && !Double.isInfinite(value)) {
                value++;
                return String.valueOf((int) value);
            }
        } catch (NumberFormatException e) {
            // sequencial informado não numérico
        }
        return ""; // Retorna string vazia se a entrada não for numérica ou houver erro
    }

    public String getRetorno(String resp) {
        JSONObject jsonDic = new JSONObject(resp);
        return jsonDic.optInt("tef.retorno")+"";
    }

    private String readInput() {
        Scanner scanner = new Scanner(System.in); // Cria um Scanner que lê do console
        String input = scanner.nextLine(); // Lê uma linha de entrada do console
        return input;
    }

    public String getComprovante(String resp, String via) {
        JSONObject jsonDic = new JSONObject(resp);
        if ("loja".equals(via)) {
            return jsonDic.optString("tef.comprovanteDiferenciadoLoja", "");
        } else if ("cliente".equals(via)) {
            return jsonDic.optString("tef.comprovanteDiferenciadoPortador", "");
        } else {
            return "";
        }
    }
}