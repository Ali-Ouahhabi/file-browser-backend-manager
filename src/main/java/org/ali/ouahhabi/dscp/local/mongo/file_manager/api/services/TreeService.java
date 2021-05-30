package org.ali.ouahhabi.dscp.local.mongo.file_manager.api.services;

import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.daos.TreeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 *
 * @author Ali Ouahhabi
 * @email ali.ohhb@gmail.com
 */


@Service
public class TreeService {
	
	private TreeDao treeDao;
	
	@Autowired
	public TreeService(TreeDao treeDao) {
		this.treeDao = treeDao;
	}
	
	public boolean upsertTree(String tree) {
		return this.treeDao.upsertTree(tree,this.getUser());
	}
	
	public String getTree() {
		return this.treeDao.getTree(this.getUser());
	}
	
	private String getUser() {
		return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

}
