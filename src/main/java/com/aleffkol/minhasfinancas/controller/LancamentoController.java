package com.aleffkol.minhasfinancas.controller;

import com.aleffkol.minhasfinancas.api.dto.LancamentoDTO;
import com.aleffkol.minhasfinancas.exception.RegraDeNegocioException;
import com.aleffkol.minhasfinancas.model.entity.Lancamento;
import com.aleffkol.minhasfinancas.model.entity.Usuario;
import com.aleffkol.minhasfinancas.model.enums.StatusLancamento;
import com.aleffkol.minhasfinancas.model.enums.TipoLancamento;
import com.aleffkol.minhasfinancas.service.LancamentoService;
import com.aleffkol.minhasfinancas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoController {

    private final LancamentoService lancamentoService;
    private final UsuarioService usuarioService;



    @PostMapping
    public ResponseEntity salvar(@RequestBody LancamentoDTO lancamentoDTO){
        try {
            Lancamento lancamentoConvertido = converter(lancamentoDTO);
            lancamentoConvertido = lancamentoService.salvar(lancamentoConvertido);
            return new ResponseEntity(lancamentoConvertido, HttpStatus.CREATED);
        }
        catch (RegraDeNegocioException e){
            return ResponseEntity.badRequest().body("Erro ao salvar");
        }
    }

    @SneakyThrows
    @GetMapping
    public ResponseEntity buscar(@RequestParam(value = "descricao", required = false) String descricao,
                                 @RequestParam(value = "mes", required = false) Integer mes,
                                 @RequestParam(value = "ano", required = false) Integer ano,
                                 @RequestParam("usuario") Long idUsuario
                                 ){
        Lancamento lancamentoFiltro = new Lancamento();
        lancamentoFiltro.setMes(mes);
        lancamentoFiltro.setAno(ano);
        lancamentoFiltro.setDescricao(descricao);

        Optional<Usuario> usuario = usuarioService.encontrarPorID(idUsuario);
        if(!usuario.isPresent()){
            return ResponseEntity.badRequest().body("Usuário não encontrado para o id informado.");
        }else {
            lancamentoFiltro.setUsuario(usuario.get());
        }
        List<Lancamento> lancamentos = lancamentoService.buscar(lancamentoFiltro);
        return ResponseEntity.ok(lancamentos);

    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO lancamentoDTO){
        return lancamentoService.encontrarPorID(id).map(entity -> {
            try {
                Lancamento lancamento = converter(lancamentoDTO);
                lancamento.setId(entity.getId());
                lancamentoService.atualizar(lancamento);
                return ResponseEntity.ok(lancamento);
            }catch (RegraDeNegocioException e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }

        }).orElseGet(()-> new ResponseEntity("Lançamento não encotrado no banco de dados", HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("{id}")
    public ResponseEntity deletar(@PathVariable("id") Long id){
        return lancamentoService.encontrarPorID(id).map(entity ->{
            lancamentoService.deletar(entity);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }).orElseGet( () -> new ResponseEntity("Lançamento não encontrado", HttpStatus.BAD_REQUEST));

    }
    @SneakyThrows
    private Lancamento converter(LancamentoDTO lancamentoDTO)  {
        Lancamento lancamento = new Lancamento();
        lancamento.setId(lancamentoDTO.getId());
        lancamento.setDescricao(lancamentoDTO.getDescricao());
        lancamento.setAno(lancamentoDTO.getAno());
        lancamento.setMes(lancamentoDTO.getMes());
        lancamento.setValor(lancamentoDTO.getValor());
        Optional<Usuario> usuario = usuarioService.encontrarPorID(lancamentoDTO.getUsuario());
        //System.out.println(usuario);
        //System.out.println(usuario.get());
        //if(lancamentoDTO.getUsuario()==null){
         //   throw new RegraDeNegocioException("erro usuário");
        //}
        lancamento.setUsuario(usuario.get());
        if(lancamentoDTO.getTipoLancamento()!=null){
            lancamento.setTipoLancamento(TipoLancamento.valueOf(lancamentoDTO.getTipoLancamento()));}
        if(lancamentoDTO.getStatusLancamento()!=null){
            lancamento.setStatusLancamento(StatusLancamento.valueOf(lancamentoDTO.getStatusLancamento()));
        }
        return lancamento;



    }
}
