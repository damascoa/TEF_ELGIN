package com.elgin.tef;

import com.elgin.tef.enums.*;
import com.elgin.tef.util.VMCheck;
import com.sun.jna.Pointer;

public class TEFElgin {

    boolean tefIniciado = false;


    public TEFElgin() throws UnsupportedClassVersionError {
        if(VMCheck.checkJvmVersion().equals("64")){
            throw new UnsupportedClassVersionError("Versão do JAVA (64bits) não suportada para a DLL!");
        }
    }

    /**  1º
     * GetProdutoTef
     * @return Retorna o código do produto TEF em uso
     */
    public void GetProdutoTef(){
        int codigo = E1_Tef01.INSTANCE.GetProdutoTef();
        System.out.println(codigo);
    }


    /** 2º
     * GetClientTCP
     * @return Retorna a configuração atual do Client, no formato ip_client|porta_client
     */
    public void GetClientTCP(){
       Pointer pointer =  E1_Tef01.INSTANCE.GetClientTCP();
        System.out.println(pointer.getString(0));
    }

    /** 3º
     * Configura o IP e a porta onde o ElginTEF está operando;.
     * @param ip	string do IP a ser usado pelo ElginTEF
     * @param porta    int da porta a ser usada pelo ElginTEF
     * @return Retorna o resultado da operação (erro ou sucesso). Em caso de sucesso, esses parâmetros serão persistidos no arquivo de configuração.
     */
    public void SetClientTCP(String ip, int porta) {
        Pointer pointer = E1_Tef01.INSTANCE.SetClientTCP(ip, porta);
        System.out.println(pointer.getString(0));
    }

    /** 4º
     * Necessários para operação com ElginTEF e persiste-os no arquivo de configuração
     * @param textoPinpad    O texto que será exibido no visor do Pinpad conectado à AC, normalmente o nome da AC
     * @param versaoAC      String indicando a versão da AC; esse texto também é exibido no visor do Pinpad
     * @param nomeEstabelecimento      O nome do estabelecimento no qual a AC está em execução
     * @param loja          O nome/código (String) da loja (pertencente ao estabelecimento) na qual a AC está em execução
     * @param identificadorPontoCaptura      O nome/código (String) do terminal (pertencente à loja) no qual a AC está em execução
     * @return Retorna sucesso, se o arquivo for criado/atualizado com sucesso, ou erro, caso contrário.
     */
    public void ConfigurarDadosPDV(String textoPinpad, String versaoAC, String nomeEstabelecimento, String loja, String identificadorPontoCaptura) {
        Pointer pointer = E1_Tef01.INSTANCE.ConfigurarDadosPDV(textoPinpad, versaoAC, nomeEstabelecimento, loja, identificadorPontoCaptura);
        System.out.println(pointer.getString(0));
    }



    /** 5º
     * Estabelece uma conexão com o ElginTEF, permitindo que a API realize transações com o Client. Este método deve ser chamado sempre que a API for carregada ou após a execução do método FinalizarOperacaoTEF. Este método lê o arquivo de configuração para obter os dados do Client e do PDV, conforme documentado nas funções SetClientTCP e ConfigurarDadosPDV.
     * Caso a leitura do arquivo falhe e essas funções ainda não foram chamadas no carregamento atual da biblioteca (execução da aplicação), elas deverão ser chamadas antes de prosseguir com a inicialização. Opcionalmente, pode-se passar um JSON com os dados do PDV que serão usados nas operações subsequentes, sobrescrevendo os valores definidos no método ConfigurarDadosPDV (o arquivo de configuração não é alterado nesse caso).
     * @param dadosCaptura    Recebe payload com informações da Automação Comercial. Parâmetro opcional.
     * Se não for passado, a API irá consumir os dados presentes no arquivo de configuração e1_tef_configs.json com as informações dadas às funções SetClientTCP e ConfigurarDadosPDV.
     * Se for passado, a API usará os dados passados no payload com as seguintes chaves: textoPinpad, versaoAC, nomeEstabelecimento, loja, identificadorPontoCaptura.
     * @return Uma string JSON com a seguinte estrutura: https://elgindevelopercommunity.github.io/group__tf.html#ga1bf9edea41af3c30936caf5ce7f8c988
     */
    public void IniciarOperacaoTEF() {
        Pointer pointer = E1_Tef01.INSTANCE.IniciarOperacaoTEF("{}");
        System.out.println(pointer.getString(0));
    }



    /** 6º
     * Configura o IP e a porta onde o ElginTEF está operando;.
     * @param dadosCaptura    Recebe payload com informações da Automação Comercial. Parâmetro opcional.
     * Se não for passado, a API irá consumir os dados presentes no arquivo de configuração e1_tef_configs.json com as informações dadas às funções SetClientTCP e ConfigurarDadosPDV.
     * Se for passado, a API usará os dados passados no payload com as seguintes chaves: textoPinpad, versaoAC, nomeEstabelecimento, loja, identificadorPontoCaptura.
     * @return  porta    Uma string JSON com a seguinte estrutura:
     */
    public void RecuperarOperacaoTEF(String dadosCaptura){
        Pointer pointer = E1_Tef01.INSTANCE.RecuperarOperacaoTEF(dadosCaptura);
    }

    /** 7º
     * Inicia uma operação de pagamento.
     * @param codigoOperacao	Indica o tipo de cartão a ser utilizado no pagamento, conforme lista a seguir esse parâmetro é ignorado caso a operação esteja em estado de coleta. 0 - Ignora cartão (o tipo de cartão será perguntado durante o processo de coleta) 1 - Cartão de crédito 2 - Cartão de débito 3 - Voucher (débito) 4 - Frota (débito) 5 - Private label (crédito)
     * @param dadosCaptura    	JSON com as informações iniciais do pagamento ou os dados de resposta da coleta. Para detalhes sobre os valores possíveis conferir a seção Payloads em Modo Ativo
     * @param novaTransacao    Informa à função em qual estado encontra-se o processo de pagamento:
     * true - indica o início de uma nova operação de pagamento
     * false - indica uma operação já iniciada, em estado de coleta em Modo Ativo
     * @return Uma string JSON com a seguinte estrutura: https://elgindevelopercommunity.github.io/group__tf.html#ga09d2bd1b1adb661b08e35f983b412d6a
     */
    public void RealizarPagamentoTEF(Operacao codigoOperacao, String dadosCaptura, boolean novaTransacao){
        Pointer pointer = E1_Tef01.INSTANCE.RealizarPagamentoTEF(codigoOperacao.getCodigo(), dadosCaptura, novaTransacao);
    }


    /** 8º
     * Inicia uma operação administrativa.
     *
     * @param codigoOperacao	Indica o tipo de operação administrativa a ser realizada, conforme lista a seguir Esse parâmetro é ignorado caso a operação esteja em estado de coleta. 0 - Perguntar operação no processo de coleta 1 - Cancelamento 2 - Pendências 3 - Reimpressão
     * @param dadosCaptura      JSON com as informações iniciais da operação administrativa ou os dados de resposta da coleta. Para detalhes sobre os valores possíveis conferir a seção Payloads em Modo Ativo
     * @param novaTransacao     Informa à função em qual estado encontra-se o processo administrativo:
     * true - indica o início de uma nova operação administrativa
     * false - indica uma operação já iniciada, em estado de coleta
     * @return Uma string JSON com a seguinte estrutura:
     */
    public void RealizarAdmTEF(OperacaoAdministrativa codigoOperacao, String dadosCaptura, boolean novaTransacao){
        Pointer pointer = E1_Tef01.INSTANCE.RealizarAdmTEF(codigoOperacao.getCodigo(), dadosCaptura, novaTransacao);

    }

    /** 9º
     * Realiza uma operação de PIX. Essa função segue o mesmo fluxo da função RealizarPagamentoTEF e depende do serviço habilitado na operadora de TEF. ​​ * ​​​​​É importante que após a finalização da transação a automação realize a confirmação da transação usando a função de confirmação.
     * @param dadosCaptura		Usado para enviar o valor da transação em centavos. Ex: 1000 = R$10,00
     * @param novaTransacao     Usado para indicar o inicio de uma transação.
     * @return O Retorno será um JSON no padrão da API onde serão retornadas as informações de acordo com o processamento da transação. Durante o processamento os dados utilizados para gerar a imagem do QRCode em tela devem ser retornados na chave "mensagemResultado" do obj "tef". Tal mensagem tem a seguinte estrutura: QRCODE;[dadosEmHexadecimal];[infoComplementaresEmBase64] sendo que o index 1 dessa estrutura pode ser usado para gerar a imagem de apresentação.
     */
    public void RealizarPixTEF(String dadosCaptura, boolean novaTransacao){
        Pointer pointer = E1_Tef01.INSTANCE.RealizarPixTEF(dadosCaptura, novaTransacao);
    }



    /** 10º
     * Configura o IP e a porta onde o ElginTEF está operando;.
     *
     * @param id	Indica o sequencial utilizado na operação a ser confirmada/cancelada. Esse é o sequencial opcionalmente informado no payload no início da operação, que também é retornado ao usuário ao término da coleta.
     * @param acao  Indica a ação a ser realizada com a operação em questão: 0 - Cancelar 1 - Confirmar
     *
     * @return Uma string JSON com a seguinte estrutura:
     */
    public void ConfirmarOperacaoTEF(int id, Acao acao){
        Pointer pointer = E1_Tef01.INSTANCE.ConfirmarOperacaoTEF(id, acao.getCodigo());
    }

    /** 11º
     * Finaliza a conexão com o ElginTEF.
     * Após essa chamada a API não realizará novas transações com o Client até que uma nova chamada de IniciarOperacaoTEF() seja feita.
     *
     * @param id	Indica o sequencial a ser utilizado na finalização, devendo ser o valor do sequencial usado na última operação somado com 1. Pode-se passar o valor 1, uma vez que a API resolve com o ElginTEF o sequencial correto a ser utilizado.
     *
     * @return Uma string JSON com a seguinte estrutura:
     */
    public void FinalizarOperacaoTEF(int id){
        Pointer pointer = E1_Tef01.INSTANCE.FinalizarOperacaoTEF(id);
    }


    /** 12º
     * Finaliza a conexão com o ElginTEF.
     * Após essa chamada a API não realizará novas transações com o Client até que uma nova chamada de IniciarOperacaoTEF() seja feita.
     *
     * @param tipoColeta	Identifica qual coleta deve ser realizada. Os valores possíveis são: 1(RG), 2(CPF), 3(CNPJ) e 4(Telefone
     * @param confirmar		Indica que ao fim da operação deve ser realizada a confirmação do dado pelo pinpad.
     *
     * @return O retorno será um JSON no padrão da API onde a informação coletada esta preenchida na chave "resultadoCapturaPinPad" do obj "tef". A automação deve validar o valor de "retorno", quando igual a 1 a operação pode ser considerada como sucesso. Quando igual a 9 indica que o usuário não cancelou a operação.
     * Exemplo Sucesso
     * {
     *     "codigo": 0,
     *     "mensagem": "Sucesso",
     *     "tef": {
     *         "mensagemResultado": "CNPJC+691980880001",
     *         "resultadoCapturaPinPad": "691980880001",
     *         "retorno": "1",
     *         "sequencial": "101",
     *         "servico": "perguntar"
     *     }
     * }
     * Exemplo Cancelamento
     * {
     *     "codigo": 0,
     *     "mensagem": "Sucesso",
     *     "tef": {
     *         "mensagemResultado": "Operacao cancelada pelo cliente",
     *         "retorno": "9",
     *         "sequencial": "8",
     *         "servico": "coletar"
     *     }
     * }
     */
    public void RealizarColetaPinPad(TipoColeta tipoColeta, boolean confirmar){
        Pointer pointer = E1_Tef01.INSTANCE.RealizarColetaPinPad(tipoColeta.getCodigo(), confirmar);
        System.out.println(pointer.getString(0));
    }


    /** 13º
     * Finaliza a conexão com o ElginTEF.
     * Após essa chamada a API não realizará novas transações com o Client até que uma nova chamada de IniciarOperacaoTEF() seja feita.
     *
     * @param tipoCaptura	Indica qual o tipo da confirmação a ser realizada. Os valores possíveis são: 1(RG), 2(CPF), 3(CNPJ), 4(Telefone), 5(seleção) ou 6(Operadora)
     * @param dadosCaptura  Dados a serem apresentados no display do pinpad.
     *
     * @return O retorno será um JSON no padrão da API onde a informação coletada esta preenchida na chave "resultadoCapturaPinPad" do obj "tef". A automação deve validar o valor de "retorno", quando igual a 1 a operação pode ser considerada como sucesso. Quando igual a 9 indica que o usuário não confirmou a operação.
     * Exemplo Sucesso
     * {
     *     "codigo": 0,
     *     "mensagem": "Sucesso",
     *     "tef": {
     *         "mensagemResultado": "CNPJC+691980880001",
     *         "resultadoCapturaPinPad": "691980880001",
     *         "retorno": "1",
     *         "sequencial": "101",
     *         "servico": "perguntar"
     *     }
     * }
     *
     * Exemplo Cancelamento
     * {
     *     "codigo": 0,
     *     "mensagem": "Sucesso",
     *     "tef": {
     *         "mensagemResultado": "RGC+000000000",
     *         "retorno": "9",
     *         "sequencial": "9",
     *         "servico": "perguntar"
     *     }
     * }
     *
     */
    public void ConfirmarCapturaPinPad(TipoCaptura tipoCaptura, String dadosCaptura){
        Pointer pointer = E1_Tef01.INSTANCE.ConfirmarCapturaPinPad(tipoCaptura.getCodigo(), dadosCaptura);
    }






}