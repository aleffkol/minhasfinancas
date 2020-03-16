package com.aleffkol.minhasfinancas.service.impl;

import com.aleffkol.minhasfinancas.exception.AutenticaoException;
import com.aleffkol.minhasfinancas.exception.RegraDeNegocioException;
import com.aleffkol.minhasfinancas.model.repository.UsuarioRepository;
import com.aleffkol.minhasfinancas.service.UsuarioService;
import com.aleffkol.minhasfinancas.model.entity.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if(!usuario.isPresent()){
            throw new AutenticaoException("Usuário não cadastrado com este e-mail");
        }
        if(!usuario.get().getSenha().equals(senha)){
            throw new AutenticaoException("A senha está incorreta.");
        }
        return usuario.get();

    }

    @Override
    @Transactional
    public Usuario salvarUsuario(Usuario usuario) {
        validarEmail(usuario.getEmail());
        return usuarioRepository.save(usuario);
    }

    @Override
    public void validarEmail(String email) {
        boolean existe = usuarioRepository.existsByEmail(email);
        if(existe){
            throw new RegraDeNegocioException("Já existe um usuário cadastrado com este e-mail.");
        }
    }

    @Override
    public Optional<Usuario> encontrarPorID(long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public List<Usuario> listarUsuario() {
        return usuarioRepository.findAll();
    }
}
