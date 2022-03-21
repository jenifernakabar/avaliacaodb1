package br.com.teste.db1.controller.api;

import javax.persistence.EntityManager;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.teste.db1.dao.ProdutoDao;
import br.com.teste.db1.modelo.ApiMessage;
import br.com.teste.db1.modelo.Produto;
import br.com.teste.db1.util.JPAUtil;

@RestController
@RequestMapping("/api")
public class ProdutoRest {

	@PostMapping(path = { "/produtos" })
	public ResponseEntity cadastrarProduto(@RequestBody @Validated Produto produto) {

		if (produto.getNome().isEmpty() || produto.getNome().isBlank()) {
			return new ResponseEntity<>(new ApiMessage("Campo nome invalido", 400), HttpStatus.BAD_REQUEST);
		}

		if (produto.getDescricao().isEmpty() || produto.getDescricao().isBlank()) {
			return new ResponseEntity<>(new ApiMessage("Campo descrição invalido", 400), HttpStatus.BAD_REQUEST);
		}
		if (produto.getPreco().intValue() <= 0 || produto.getPreco() == null) {
			return new ResponseEntity<>(new ApiMessage("Campo Preço invalido", 400), HttpStatus.BAD_REQUEST);
		}
		if (produto.getQuantidadeEstoque() < 0) {
			return new ResponseEntity<>(new ApiMessage("Campo quantidade invalido", 400), HttpStatus.BAD_REQUEST);
		}

		EntityManager em = JPAUtil.getEntityManager();
		ProdutoDao produtoDao = new ProdutoDao(em);

		em.getTransaction().begin();

		produtoDao.cadastrar(produto);

		em.getTransaction().commit();

		em.close();

		return new ResponseEntity<>(new ApiMessage("Produto " + produto.getNome() + " criado", 201),
				HttpStatus.CREATED);

	}

	@GetMapping(path = { "/produtos/{id}" })
	public ResponseEntity buscarProduto(@PathVariable String id) {
		try {
			long id1 = Long.valueOf(id);
			EntityManager em = JPAUtil.getEntityManager();
			ProdutoDao produtoDao = new ProdutoDao(em);
			Produto produto = produtoDao.buscarPorId(id1);
			if (produto != null) {
				return ResponseEntity.ok(produto);
			} else {
				return new ResponseEntity<>(new ApiMessage("Produto Não Encontrado", 400), HttpStatus.BAD_REQUEST);
			}

		} catch (NumberFormatException e) {
			return new ResponseEntity<>(new ApiMessage("O id deve ser numerico", 400), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/produtos")
	public ResponseEntity getListaTodosProdutos() {
		EntityManager em = JPAUtil.getEntityManager();
		ProdutoDao produtoDao = new ProdutoDao(em);
		return ResponseEntity.ok(produtoDao.buscarTodos());
	}

	@PutMapping(path = { "/produtos/{idRequest}" })
	public ResponseEntity atualizarProduto(@PathVariable String idRequest, @RequestBody Produto produto) {
		try {
			long id = Long.valueOf(idRequest);
			EntityManager em = JPAUtil.getEntityManager();
			ProdutoDao produtoDao = new ProdutoDao(em);
			produto.setId(id);
			em.getTransaction().begin();
			Produto produtoDoBd = produtoDao.buscarPorId(id);
			if (produtoDoBd != null) {
				produtoDao.atualizar(produto);

				em.getTransaction().commit();

				em.close();
				return new ResponseEntity<>(new ApiMessage("Produto " + produtoDoBd.getNome() + " Atualizado", 204),
						HttpStatus.ACCEPTED);
			} else {
				return new ResponseEntity<>(new ApiMessage("Produto Não Encontrado", 400), HttpStatus.BAD_REQUEST);
			}

		} catch (NumberFormatException e) {
			return new ResponseEntity<>(new ApiMessage("O id deve ser numerico", 400), HttpStatus.BAD_REQUEST);
		}

	}

	@PatchMapping(path = { "/produtos/{idRequest}/{quantidadeEstoqueRequest}" })
	public ResponseEntity atualizarEstoque(@PathVariable String idRequest,
			@PathVariable String quantidadeEstoqueRequest) {
		try {
			long id = Long.valueOf(idRequest);
			Integer quantidadeEstoque = Integer.valueOf(quantidadeEstoqueRequest);
			EntityManager em = JPAUtil.getEntityManager();
			ProdutoDao produtoDao = new ProdutoDao(em);
			em.getTransaction().begin();
			Produto produtoDoBd = produtoDao.buscarPorId(id);
			if (produtoDoBd != null) {
				produtoDao.adicionarEstoque(quantidadeEstoque, produtoDoBd);

				em.getTransaction().commit();

				em.close();
				return new ResponseEntity<>(new ApiMessage("Estoque do produto " + produtoDoBd.getNome()
						+ " atualizado para " + produtoDoBd.getQuantidadeEstoque(), 204), HttpStatus.ACCEPTED);
			} else {
				return new ResponseEntity<>(new ApiMessage("Produto Não Encontrado", 400), HttpStatus.BAD_REQUEST);
			}

		} catch (NumberFormatException e) {
			return new ResponseEntity<>(new ApiMessage("Ambos valores precisam ser numericos", 400),
					HttpStatus.BAD_REQUEST);
		} catch (RuntimeException e) {
			return new ResponseEntity<>(new ApiMessage("Estoque insulficiente", 400), HttpStatus.BAD_REQUEST);
		}

	}

	@DeleteMapping(path = { "/produtos/{id}" })
	public ResponseEntity deletarProduto(@PathVariable String id) {
		try {
			long id1 = Long.valueOf(id);
			EntityManager em = JPAUtil.getEntityManager();
			ProdutoDao produtoDao = new ProdutoDao(em);
			em.getTransaction().begin();
			Produto produto = produtoDao.buscarPorId(id1);
			if (produto != null) {
				produtoDao.remover(produto);

				em.getTransaction().commit();

				em.close();
				return new ResponseEntity<>(new ApiMessage("Produto Deletado", 204), HttpStatus.ACCEPTED);
			} else {
				return new ResponseEntity<>(new ApiMessage("Produto Não Encontrado", 400), HttpStatus.BAD_REQUEST);
			}

		} catch (NumberFormatException e) {
			return new ResponseEntity<>(new ApiMessage("O id deve ser numerico", 400), HttpStatus.BAD_REQUEST);
		}

	}
}
