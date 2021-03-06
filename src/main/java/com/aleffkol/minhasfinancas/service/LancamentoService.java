package com.aleffkol.minhasfinancas.service;

import com.aleffkol.minhasfinancas.model.entity.Lancamento;
import com.aleffkol.minhasfinancas.model.enums.StatusLancamento;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface LancamentoService {
    Lancamento salvar(Lancamento lancamento);

    Lancamento atualizar(Lancamento lancamento);

    void deletar(Lancamento lancamento);

    List<Lancamento> buscar(Lancamento lancamento);

    void atualizarStatus (Lancamento lancamento, StatusLancamento statusLancamento);

    void validar(Lancamento lancamento);

    Optional<Lancamento> encontrarPorID(Long id);

    BigDecimal obterSaldoPorUsuario(Long id);
}
