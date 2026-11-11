package br.insper.af;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/tarefa")
public class TarefaController {

    @Autowired
    private final TarefaRepository tarefaRepository;

    public TarefaController(TarefaRepository repository, TarefaRepository tarefaRepository) {
        this.tarefaRepository = tarefaRepository;
    }

    @GetMapping
    public List<Tarefa> listar() {
        return tarefaRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Tarefa cadastrar(
            @Valid @RequestBody Tarefa tarefa,
            @AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getClaimAsString(
                "https://api.af.com/email"
        );

        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Email ausente no token"
            );
        }

        Tarefa avaliacao = new Tarefa();
        avaliacao.setEmailCriador(email);
        avaliacao.setDescricao(tarefa.getDescricao());
        avaliacao.setTitulo(tarefa.getTitulo());
        avaliacao.setPrioridade(tarefa.getPrioridade());


        return tarefaRepository.save(avaliacao);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        Tarefa produto = tarefaRepository.findById(Math.toIntExact(id)).orElseThrow();

        tarefaRepository.delete(produto);
    }

}
