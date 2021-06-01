/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ali.ouahhabi.dscp.local.mongo.file_manager.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.daos.FilesDao;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.models.FileModel;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Ali Ouahhabi
 * @email ali.ohhb@gmail.com
 */


@Service
public class FilesService {

	private final FilesDao filesDao;

	@Autowired
	FilesService(FilesDao filesDao) {
		this.filesDao = filesDao;
	}

	public String findAll() {
		String UserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String result = this.filesDao.initUserFiles(UserId);
		return result;
	}

	public String upload(MultipartFile[] files, FileModel[] metadata) throws Exception {

		ObjectMapper ow = new ObjectMapper();

		List<GridFSUploadOptions> options = List.of(metadata).stream().map(m -> {
			m.setUserId(this.getUser());
			return new GridFSUploadOptions().metadata(ow.convertValue(m, Document.class));
		}).collect(Collectors.toList());

		String index ="";
		for (int i = 0; i < options.size(); i++) {
			index.concat(filesDao.upload(files[i].getInputStream(), options.get(i)).toHexString()+"\n");
		}		
		return index;
	}

	public GridFsResource downloadOne(String path, String name) throws IOException {
		return this.filesDao.download(this.filesDao.findByPathAndName(path, name, this.getUser()));
	}

	public File downloadAll(String path) throws IOException {
		GridFSFindIterable files = this.filesDao.findByPath(path, this.getUser());
		return zipFiles(files);
	}

	public boolean renameFile(String path, String name, String newName) {
		return filesDao.renameFile(path, name, newName, this.getUser());
	}

	public boolean renameFolder(String path, String newPath) {
		return filesDao.renameFolder(path, newPath, this.getUser());
	}

	public boolean move(String name, String from, String to) {
		return this.filesDao.move(this.getUser(), name, from, to);
	}

	public void removeFile(String path, String name) throws IOException {
		this.filesDao.removeFile(path, name, this.getUser());
	}

	public void removeFolder(String path) throws IOException {
		this.filesDao.removeFolder(path, this.getUser());
	}

	private File zipFiles(GridFSFindIterable files) throws FileNotFoundException, IOException {
		

		Path tempFile = Files.createTempFile("compressesd", ".zip");
		FileOutputStream fos = new FileOutputStream(tempFile.toFile());
		ZipOutputStream zipOut = new ZipOutputStream(fos);
		GridFsResource fsResource;
		for (GridFSFile srcFile : files) {
			fsResource = this.filesDao.download(srcFile);
			Document metta = srcFile.getMetadata();

			InputStream fis = fsResource.getInputStream();
			String path = metta.getString("path")+metta.getString("name");
			try {
				ZipEntry zipEntry = new ZipEntry(path);
				zipOut.putNextEntry(zipEntry);
			} catch (Exception e) {
				ZipEntry zipEntry = new ZipEntry("0_" + srcFile.getFilename());
				zipOut.putNextEntry(zipEntry);
			}

			byte[] bytes = new byte[1024];
			int length;
			while ((length = fis.read(bytes)) >= 0) {
				zipOut.write(bytes, 0, length);
			}
			fis.close();
		}
		zipOut.close();
		fos.close();

		return tempFile.toFile();
	}

	private String getUser() {
		return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

}
