package org.strangeforest.orm.db;

import java.util.*;
import javax.sql.*;

import org.strangeforest.db.gateway.*;
import org.strangeforest.orm.*;

public class DBDetailDAO<PI, D extends DomainValue<D>> extends ORMDBDAO {
	
	private final String detailName;
	private final ObjectMapper<D> mapper;
	private final DBEntityDAO<PI, ? extends DomainEntity<PI, ?>> parentDAO;

	public DBDetailDAO(String detailName, ObjectMapper<D> mapper, DataSource dataSource, DBEntityDAO<PI, ? extends DomainEntity<PI, ?>> parentDAO) {
		super(dataSource, parentDAO.entityClass(), parentDAO.getClass());
		this.detailName = detailName;
		this.mapper = mapper;
		this.parentDAO = parentDAO;
		parentDAO.registerDetailDAO(detailName, this);
	}


	// Basic CRUD

	public List<D> fetch(PI entityId) {
		return db.fetchDetailList(detailName + "FetchAll", entityId, parentDAO.getMapper(), mapper);
	}

	public void create(PI entityId, List<D> details) {
		if (details != null)
			db.saveDetailCollection(detailName + "Create", entityId, details, parentDAO.getMapper(), mapper);
	}

	//TODO Optimize detail collection saving
	public void save(PI entityId, List<D> details, List<D> oldDetails) {
		if (oldDetails == null || !EqualsByValueUtil.equalByValue(details, oldDetails)) {
			if (oldDetails != null && !oldDetails.isEmpty())
				delete(entityId);
			create(entityId, details);
		}
//		if (oldDetails != null) {
//			List<D> forCreate = new ArrayList();
//			List<D> forUpdate = new ArrayList();
//			List<D> forDelete = new ArrayList(oldDetails);
//			for (D detail : details) {
//				if (EqualsByValueUtil.containsByValue(oldDetails, detail))
//					forUpdate.add(detail);
//				else
//					forCreate.add(detail);
//				forDelete.remove(detail);
//			}
//			create(entityId, forCreate);
//			db.saveDetailCollection(detailName + "Create", entityId, forCreate, parentDAO.getMapper(), mapper);
//			db.saveDetailCollection(detailName + "Save", entityId, forUpdate, parentDAO.getMapper(), mapper);
//			db.deleteDetailCollection(detailName + "Delete", entityId, forDelete, parentDAO.getMapper(), mapper);
//		}
//		else {
//		   delete(entityId);
//			create(entityId, details);
//    }
	}

	public void delete(PI entityId) {
		db.deleteAllDetails(detailName + "DeleteAll", entityId, parentDAO.getMapper());
	}
}
