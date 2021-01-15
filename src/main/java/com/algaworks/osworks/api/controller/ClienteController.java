package com.algaworks.osworks.api.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.osworks.domain.model.Cliente;
import com.algaworks.osworks.domain.repository.ClienteRepository;
import com.algaworks.osworks.domain.service.CadastroClienteService;

//Controller - receber e responder requisições
@RestController
@RequestMapping("/clientes") //responder tudo que estiver em /clientes
public class ClienteController {

	@PersistenceContext
	private EntityManager manager;//interface do javax persistence, usado para fazer operações no bd
	
	@Autowired
	private CadastroClienteService cadastroCliente;
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	//@GetMapping("/clientes") não precisa por causa do request mapping
	@GetMapping
	public List<Cliente> listar() {	
		return clienteRepository.findAll();
	}
	
	@GetMapping("{clienteId}")
	public ResponseEntity<Cliente> buscar(@PathVariable Long clienteId) {
		Optional<Cliente> cliente = clienteRepository.findById(clienteId);
		
		if (cliente.isPresent()) { //Caso exista esse cliente, retorne um status ok e o cliente
			return ResponseEntity.ok(cliente.get());
		}
		return ResponseEntity.notFound().build(); //Caso não exista, retorna status 404
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED) //Retorna o status 201
	public Cliente adicoinar(@Valid @RequestBody Cliente cliente) { // Valid ativa a validação do been Validation (as restrições feitas na classe Cliente 
																	//RequestBody pega o corpo que vem da requisição e transforma em cliente
		return cadastroCliente.salvar(cliente);
	}
	
	@PutMapping("{clienteId}")
	public ResponseEntity<Cliente> atualizar(@PathVariable Long clienteId,
			@RequestBody Cliente cliente){
		
		if (!clienteRepository.existsById(clienteId)) { //Caso não existe o cliente, retorna 404
			return ResponseEntity.notFound().build();
		}
		
		cliente.setId(clienteId); //Define o Id para que o banco não receba um Id vazio e pense que é um cliente novo
		cliente = cadastroCliente.salvar(cliente);
		
		return ResponseEntity.ok(cliente);
	}
	
	@DeleteMapping("{clienteId}")
	public ResponseEntity<Void> remover(@PathVariable Long clienteId){
		if (!clienteRepository.existsById(clienteId)) {
			return ResponseEntity.notFound().build();
		}
		
		cadastroCliente.excluir(clienteId);
		
		return ResponseEntity.noContent().build(); // retornar status 204, poderia ser 200, mas 204 é mais específico.
	}
}
