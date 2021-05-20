/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ali.ouahhabi.dscp.local.mongo.file_manager.api.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.models.FileModel;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.services.FilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Ali Ouahhabi
 */
@RestController
@CrossOrigin
@RequestMapping("${api.prefix}/files")
public class FilesController {

	private final FilesService filesService;

	@Autowired
	public FilesController(FilesService filesService) {
		this.filesService = filesService;
	}

	@RequestMapping("/remove")
	public ResponseEntity<String> remove(@RequestBody Map<String, String> body) {
		boolean isFile = body.get("isFile").equals("true") ? true : false;
		String name = (String) body.get("name");
		String path = (String) body.get("path");

		try {
			if (isFile)
				this.filesService.removeFile(path, name);
			else
				this.filesService.removeFolder(path);
			return ResponseEntity.ok("true");
		} catch (IOException e) {
			
			return ResponseEntity.ok(e.getMessage());
		}

	}

	@RequestMapping("/move")
	public ResponseEntity<Boolean> move(@RequestBody Map<String, String> body) {
		String name = body.get("name");
		String from = body.get("from");
		String to = body.get("to");
		return ResponseEntity.ok(this.filesService.move(name, from, to));
	}

	@GetMapping("/list")
	public ResponseEntity<String> listAll() {
		return ResponseEntity.ok(this.filesService.findAll());
	}

	@RequestMapping(value = "/download")
	public ResponseEntity download(@RequestBody Map<String, String> body) {

		boolean isFile = body.get("isFile").equals("true") ? true : false;
		String name = (String) body.get("name");
		String path = (String) body.get("path");
		if (isFile)
			return this.downloadOne(path, name);
		else
			return this.downloadAll(path, name);
	}

	private ResponseEntity<InputStreamResource> downloadOne(String path, String name) {

		try {

			GridFsResource resp = this.filesService.downloadOne(path, name);

			return ResponseEntity.ok()
					.contentType(MediaType.valueOf(resp.getOptions().getMetadata().get("type", String.class)))
					.header("Access-Control-Expose-Headers", HttpHeaders.CONTENT_DISPOSITION)
					.header(HttpHeaders.CONTENT_DISPOSITION, resp.getFilename())
					.body(new InputStreamResource(resp.getInputStream()));

		} catch (IOException ex) {

			Logger.getLogger(FilesController.class.getName()).log(Level.SEVERE, null, ex);

			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private ResponseEntity downloadAll(String path, String name) {
		try {
			// FIXME instead of fileId use UserId and path+filename
			File resp = this.filesService.downloadAll(path);

			return ResponseEntity.ok().contentType(MediaType.valueOf("application/zip"))
					.header("Access-Control-Expose-Headers", HttpHeaders.CONTENT_DISPOSITION)
					.header(HttpHeaders.CONTENT_DISPOSITION, name + ".zip")
					.body(new InputStreamResource(new FileInputStream(resp)));

		} catch (IOException ex) {

			Logger.getLogger(FilesController.class.getName()).log(Level.SEVERE, null, ex);

			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/uploads", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	// , consumes = )
	public ResponseEntity upload(@RequestPart(value = "files") MultipartFile[] files,
			@RequestPart(value = "metadata") FileModel[] metadata) throws IOException {
		try {
			return new ResponseEntity<>(this.filesService.upload(files, metadata), HttpStatus.OK);
		} catch (Exception ex) {
			Logger.getLogger(FilesController.class.getName()).log(Level.SEVERE, null, ex);
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping("/rename")
	public ResponseEntity<Boolean> rename(@RequestBody Map<String, String> body) {
		boolean isFile = body.get("isFile").equals("true") ? true : false;
		if(isFile) {
			String name = (String) body.get("name");
			String path = (String) body.get("path");
			String newName = (String) body.get("newName");
			return ResponseEntity.ok(filesService.renameFile(path,name,newName));
		}else {
			String path = (String) body.get("path");
			String newPath = (String) body.get("newPath");
			return ResponseEntity.ok(filesService.renameFolder(path,newPath));
		}

		
	}


}
