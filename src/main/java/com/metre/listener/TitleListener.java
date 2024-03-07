package com.metre.listener;

import java.util.List;

public interface TitleListener {
    void change(String string);

    void changePalavraChave(String chave);

    void changeOptions(List<String> options);
}
