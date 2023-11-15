package com.ufcg.psoft.commerce.service.Notificacao;


@FunctionalInterface
public interface NotificacaoEnviarService {
    public void enviarEmail(String para, String assunto, String texto);
}
