package com.generation.farmacia.controller;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

import com.generation.farmacia.model.Produto;
import com.generation.farmacia.repository.CategoriaRepository;
import com.generation.farmacia.repository.ProdutoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/produtos")
@CrossOrigin(origins = "*", allowedHeaders = "*")

public class ProdutoController {

	@Autowired
	private ProdutoRepository repository;
	
	@Autowired
	private CategoriaRepository categoriaRepository;

	@GetMapping
	public ResponseEntity<List<Produto>> getAllProdutos() {
		List<Produto> produtos = repository.findAll();
		return ResponseEntity.ok(produtos);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Produto> getByID(@PathVariable Long id){
		return repository.findById(id)
				.map(resposta -> ResponseEntity.ok(resposta))
				.orElse(ResponseEntity.notFound().build());
	}
	
	@GetMapping("/nome/{nome}")
	public ResponseEntity<List<Produto>> getByNome(@PathVariable String nome) {
		return ResponseEntity.ok(repository.findAllByNomeContainingIgnoreCase(nome));
	}
	@PostMapping
    public ResponseEntity<Produto> post(@Valid @RequestBody Produto produto) {
		if (categoriaRepository.existsById(produto.getCategoria().getId()))
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(repository.save(produto));
		
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produto não existe!", null);
	}
	@PutMapping 
    public ResponseEntity<Produto> put(@Valid @RequestBody Produto produto) {
		if (categoriaRepository.existsById(produto.getId())) {
			
			if(categoriaRepository.existsById(produto.getCategoria().getId()))
				return ResponseEntity.status(HttpStatus.OK)
						.body(repository.save(produto));
			
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome de Categoria não existe!", null);
			
		}
        return repository.findById(produto.getId())
        		.map(resposta -> ResponseEntity.status(HttpStatus.OK)
        		.body(repository.save(produto)))
.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        
	}
	 @ResponseStatus(HttpStatus.NO_CONTENT)
		@DeleteMapping("/{id}")
		public void delete(@PathVariable Long id) {
			Optional<Produto> produto = repository.findById(id);
			if (produto.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND);
			}
			repository.deleteById(id);
		}
	  
}