package br.com.teste.db1.dao;

import java.util.List;

import javax.persistence.EntityManager;

import br.com.teste.db1.modelo.Produto;

public class ProdutoDao {

	private EntityManager em;

	public ProdutoDao(EntityManager em) {
		this.em = em;
	}

	public void cadastrar(Produto produto) {
		this.em.persist(produto);
	}

	public void atualizar(Produto produto) {
		this.em.merge(produto);
	}

	public void remover(Produto produto) {
		produto = em.merge(produto);
		this.em.remove(produto);
	}

	public Produto buscarPorId(Long id) {
		return em.find(Produto.class, id);
	}

	public List<Produto> buscarTodos() {
		String jpql = "SELECT p FROM Produto p";
		return em.createQuery(jpql, Produto.class).getResultList();
	}

	public List<Produto> buscarPorNome(String nome) {
		String jpql = "SELECT p FROM Produto p WHERE p.nome = :nome";
		return em.createQuery(jpql, Produto.class).setParameter("nome", nome).getResultList();
	}

	public void adicionarEstoque(Integer estoqueAtualizado, Produto produtoDoBd) {
		int quantidadeAtual = produtoDoBd.getQuantidadeEstoque();
		quantidadeAtual = quantidadeAtual + estoqueAtualizado;
		if (quantidadeAtual < 0) { 
			throw new RuntimeException("Estoque insulficiente");
		} else {
			produtoDoBd.setQuantidadeEstoque(quantidadeAtual);
			atualizar(produtoDoBd);
		}
	}
}
