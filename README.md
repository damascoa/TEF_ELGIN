# TEF Elgin JAVA

Este projeto foi desenvolvido para facilitar a integração com a solução de TEF (Transferência Eletrônica de Fundos) da Elgin, utilizando a biblioteca nativa E1_Tef01 em ambientes Java. O objetivo é fornecer uma interface Java para a comunicação com terminais de pagamento, permitindo a realização de transações de pagamento de forma eficiente e segura.

## Especificações do Projeto

- **Versão do Java:** 8 (32 Bits)
- **Gestão de Dependências:** Maven
- **Interface Gráfica:** JavaFX

## Bibliotecas Utilizadas

Para o desenvolvimento deste projeto, foram utilizadas as seguintes bibliotecas:

- **Gson:** Uma biblioteca Java que pode ser utilizada para converter objetos Java em sua representação JSON e vice-versa.
- **JNA (Java Native Access):** Permite que código Java chame diretamente funções de bibliotecas compartilhadas nativas (DLLs no Windows) sem a necessidade de escrever uma única linha de código C/C++.

## Requisitos

Devido à necessidade de carregamento da biblioteca nativa `E1_Tef01.dll`, é essencial o uso da versão **32 bits do Java 8**. Isso garante a compatibilidade com a DLL fornecida, que foi compilada para ambientes de 32 bits.

## Configuração do Maven

O arquivo `pom.xml` deve ser configurado com as seguintes dependências para o Gson e JNA:

```xml
<dependencies>
    <!-- Gson -->
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.8.6</version>
    </dependency>

    <!-- JNA -->
    <dependency>
        <groupId>net.java.dev.jna</groupId>
        <artifactId>jna</artifactId>
        <version>5.5.0</version> <!-- Verificar a última versão disponível -->
    </dependency>
</dependencies>
```
# PROCEDIMENTOS ELGIN
### 1º Passo
Para ter início aos primeiros passos acesse o repositório do GIT da <a href=“https://github.com/ElginDeveloperCommunity/TEF-Elgin/“>Elgin Developers Community</a>.
### 2º Passo
Acesse a <a href="https://elgindevelopercommunity.github.io/group__g7.html">Documentação</a> e entenda o funcionamento.

