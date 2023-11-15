package com.ufcg.psoft.commerce.service.estabelecimento;

import com.ufcg.psoft.commerce.exception.CodigoInvalidoException;
import com.ufcg.psoft.commerce.exception.estabelecimento.EstabelecimentoNotFound;
import com.ufcg.psoft.commerce.model.estabelecimento.Estabelecimento;
import com.ufcg.psoft.commerce.repository.EstabelecimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstabelecimentoV1LoginService implements IEstabelecimentoLoginService {

    @Autowired
    EstabelecimentoRepository estabelecimentoRepository;

    @Override
    public Estabelecimento verificarLogin(Long estabelecimentoId, String codigoAcesso) {
        Estabelecimento estabelecimento = estabelecimentoRepository.findEstabelecimentoById(estabelecimentoId);

        if (estabelecimento == null) {
            throw new EstabelecimentoNotFound();
        }

        if (!estabelecimento.getCodigoAcesso().equals(codigoAcesso)) {
            throw new CodigoInvalidoException();
        }

        return estabelecimento;
    }
}
