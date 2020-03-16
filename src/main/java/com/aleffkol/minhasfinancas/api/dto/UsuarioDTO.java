package com.aleffkol.minhasfinancas.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioDTO {
    private String nome;
    private String senha;
    private String email;

}