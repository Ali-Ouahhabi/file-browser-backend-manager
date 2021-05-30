package org.ali.ouahhabi.dscp.local.mongo.file_manager.api.controllers;

import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.services.TreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Ali Ouahhabi
 * @email ali.ohhb@gmail.com
 */


@RestController
@CrossOrigin
@RequestMapping("${api.prefix}/tree")
public class TreeController {

	private TreeService treeService;

	@Autowired
	public TreeController(TreeService treeService) {
		this.treeService = treeService;
	}

	@RequestMapping("/update")
	ResponseEntity<String> upsert(@RequestBody String tree) {
		if (this.treeService.upsertTree(tree))
			return ResponseEntity.ok(tree);
		else
			return ResponseEntity.badRequest().build();
	}

	@GetMapping
	ResponseEntity<String> getTree(){
		return ResponseEntity.ok(this.treeService.getTree());
	}
}
