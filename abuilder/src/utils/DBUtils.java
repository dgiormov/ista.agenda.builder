package utils;

import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;

public class DBUtils {
	private static EntityManagerFactory emf = null;

	public static EntityManagerFactory getEMF() {
		if (emf == null) {
			try {
				InitialContext ctx = new InitialContext();
				DataSource ds = (DataSource) ctx
						.lookup("java:comp/env/jdbc/DefaultDB");
				Map properties = new HashMap();
				properties
						.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, ds);
				emf = Persistence.createEntityManagerFactory("persistence",
						properties);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return emf;
	}

}
