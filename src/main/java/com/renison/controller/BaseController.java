package com.renison.controller;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.renison.exception.NotFoundException;

@RequestMapping(consumes = { MediaType.APPLICATION_JSON_VALUE })
@Transactional
public abstract class BaseController<T> {
	private Logger logger = Logger.getLogger(BaseController.class);

	protected SessionFactory sessionFactory;

	protected abstract Class<T> getResourceType();

	public BaseController() {
	}

	@RequestMapping(method = RequestMethod.GET, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody List<T> findAll() {
		return sessionFactory.getCurrentSession().createCriteria(getResourceType()).list();
	}

	@RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody T create(@RequestBody T entity) {
		logger.debug(String.format("create() with body %s of type %s", entity, entity.getClass().getName()));
		sessionFactory.getCurrentSession().save(entity);
		return entity;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody T get(@PathVariable Long id) {
		T entity = sessionFactory.getCurrentSession().get(getResourceType(), id);
		if (entity == null) {
			throw new NotFoundException(883002141l, "entity not found", "");
		}
		return entity;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody T update(@PathVariable Long id, @RequestBody T json) {
		logger.debug(String.format("update() of id#%s with body %s", id, json));
		logger.debug(String.format("T json is of type %s", json.getClass()));

		T entity = this.get(id);
		try {
			BeanUtils.copyProperties(entity, json);
		} catch (Exception e) {
			logger.warn("while copying properties", e);
			throw Throwables.propagate(e);
		}

		logger.debug(String.format("merged entity: %s", entity));

		sessionFactory.getCurrentSession().update(entity);
		logger.debug(String.format("updated enitity: %s", entity));
		return entity;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public @ResponseBody Map<String, Object> delete(@PathVariable Long id) {
		sessionFactory.getCurrentSession().delete(this.get(id));
		Map<String, Object> m = Maps.newHashMap();
		m.put("success", true);
		return m;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
