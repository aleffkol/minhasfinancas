package com.aleffkol.minhasfinancas.service.impl;

import com.aleffkol.minhasfinancas.exception.RegraDeNegocioException;
import com.aleffkol.minhasfinancas.model.entity.Lancamento;
import com.aleffkol.minhasfinancas.model.enums.StatusLancamento;
import com.aleffkol.minhasfinancas.model.repository.LancamentoRepository;
import com.aleffkol.minhasfinancas.service.LancamentoService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LancamentoServiceImpl implements LancamentoService {

    private LancamentoRepository lancamentoRepository;

    public LancamentoServiceImpl(LancamentoRepository lancamentoRepository){
        this.lancamentoRepository = lancamentoRepository;
    }

    @Override
    @Transactional
    public Lancamento salvar(Lancamento lancamento) {
        validar(lancamento);
        lancamento.setDataCadastro(LocalDate.now());
        lancamento.setStatusLancamento(StatusLancamento.PENDENTE);
        return lancamentoRepository.save(lancamento);
    }

    @Override
    @Transactional
    public Lancamento atualizar(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        validar(lancamento);
        return lancamentoRepository.save(lancamento);
    }

    @Override
    @Transactional
    public void deletar(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        lancamentoRepository.delete(lancamento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lancamento> buscar(Lancamento lancamento) {
        Example example = Example.of(lancamento, ExampleMatcher.matching().withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return lancamentoRepository.findAll(example);
    }

    @Override
    @Transactional
    public void atualizarStatus(Lancamento lancamento, StatusLancamento statusLancamento) {
        lancamento.setStatusLancamento(statusLancamento);
        atualizar(lancamento);
    }

    @Override
    public void validar(Lancamento lancamento) {
        if(lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals("")){
            throw new RegraDeNegocioException("Informe uma Descrição válida");
        }

        if(lancamento.getMes() == null || lancamento.getMes()<1 || lancamento.getMes()>12){
            throw new RegraDeNegocioException("Informe um Mês válido.");
        }
        if (lancamento.getAno() == null || lancamento.getAno().toString().length()!=4){
            throw new RegraDeNegocioException("Informe um Ano válido.");
        }
        if(lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null){
            throw new RegraDeNegocioException("Faça login!");
        }
        if (lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO)<1){
            throw new RegraDeNegocioException("Informe um valor válido");
        }

        if (lancamento.getTipoLancamento() == null){
            throw new RegraDeNegocioException("È preciso informa o tipo de lançamento");
        }
    }

    @Override
    public Optional<Lancamento> encontrarPorID(Long id) {
        return lancamentoRepository.findById(id);
    }
}
