package com.mercado.mercadinho.controller;

import com.mercado.mercadinho.config.thymeleaf.ThymeleafService;
import com.mercado.mercadinho.domain.dto.ProdutoDTO;
import com.mercado.mercadinho.domain.entity.Produto;
import com.mercado.mercadinho.service.ProdutoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@RestController
@RequiredArgsConstructor
@Tag(name = "Produto")
@RequestMapping("/produto")
public class ProdutoController {

    private final MapperFacade mapper;

    private final ProdutoService service;

    private final ThymeleafService thymeleafService;

    @GetMapping
    public ResponseEntity<Page<Produto>> findByAll(Pageable pageable) {
        return ResponseEntity.ok(service.findByAll(pageable));
    }

    @GetMapping("/{id:[0-9]*}")
    public ResponseEntity<Produto> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping()
    public ResponseEntity<ProdutoDTO> inserir(@RequestBody @Validated ProdutoDTO produto) {
        return ResponseEntity.ok(mapper.map(service.save(mapper.map(produto, Produto.class)), ProdutoDTO.class));
    }

    @PutMapping("/{id:[0-9]*}")
    public ResponseEntity<ProdutoDTO> update(@PathVariable("id") Long id,
                                             @RequestBody @Validated ProdutoDTO produto) {
        return ResponseEntity.ok(mapper.map(service.update(id, mapper.map(produto, Produto.class)), ProdutoDTO.class));
    }

    @DeleteMapping("/{id:[0-9]*}")
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
        ResponseEntity.noContent().build();
    }

    @GetMapping("relatorio")
    public void relatorio(HttpServletResponse response) {
        Context context = new Context();
        context.setVariable("produtos", service.findByAll());
        try (OutputStream outStream = response.getOutputStream()) {
            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "attachment; filename=produtos.pdf");
            outStream.write(thymeleafService.gerarPdf("produtos", context));
            outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
