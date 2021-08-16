/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ali.ouahhabi.dscp.local.mongo.file_manager.api.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.models.FileModel;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.models.request.DownloadReq;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.models.request.MoveReq;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.models.request.RemoveReq;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.models.request.RenameReq;
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
import org.springframework.web.bind.annotation.PostMapping;
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
 * @email ali.ohhb@gmail.com
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

	@GetMapping("/list")
	public ResponseEntity<String> listAll() {
		return ResponseEntity.ok(this.filesService.findAll());
	}
	
	//TODO: check for duplicate name or path

	@RequestMapping(value = "/uploads", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String>upload(@RequestPart(value = "files") MultipartFile[] files,
			@RequestPart(value = "metadata") FileModel[] metadata) throws IOException {
		try {
			return new ResponseEntity<String>(this.filesService.upload(files, metadata), HttpStatus.OK);
		} catch (Exception ex) {
			Logger.getLogger(FilesController.class.getName()).log(Level.SEVERE, null, ex);
			return new ResponseEntity<String>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/download")
	public ResponseEntity<InputStreamResource> download(@RequestBody DownloadReq req) {
		if (req.isFile())
			return this.downloadOne(req.getPath(), req.getName());
		else
			return this.downloadAll(req.getPath(), req.getName());
	}

	//TODO: check for duplicate name or path

	
	@PostMapping("/rename")
	public ResponseEntity<Boolean> rename(@RequestBody RenameReq req) {
		if(req.isFile()) 
			return ResponseEntity.ok(filesService.renameFile(req.getPath(),req.getName(),req.getNewName()));
		return ResponseEntity.ok(filesService.renameFolder(req.getPath(),req.getNewPath()));
	}
	
	//TODO: check for duplicate name or path

	
	@PostMapping("/move")
	public ResponseEntity<Boolean> move(@RequestBody MoveReq req) {
		return ResponseEntity.ok(this.filesService.move(req.getName(), req.getFrom(), req.getTo()));
	}
	
	@PostMapping("/remove")
 	public ResponseEntity<String> remove(@RequestBody RemoveReq req) {
		try {
			if (req.isFile())
				this.filesService.removeFile(req.getPath(), req.getName());
			else
				this.filesService.removeFolder(req.getPath());
			return ResponseEntity.ok("true");
		} catch (IOException e) {
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}

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

	private ResponseEntity<InputStreamResource> downloadAll(String path, String name) {
		try {
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



}
