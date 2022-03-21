package br.com.teste.db1.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUtil {
	
	private static final EntityManagerFactory FACTORY = Persistence
			.createEntityManagerFactory("db1");

	public static EntityManager getEntityManager() { 
		return FACTORY.createEntityManager();
	}
	
}
