package com.example.demo.controllers;

import java.net.URI;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demo.model.Note;
import com.example.demo.service.NoteService;

@RestController
public class NotesController {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private NoteService noteService;
	
	/* Create Note */
	//@PreAuthorize("#oauth2.hasScope('toll_report')")
	@RequestMapping(method = RequestMethod.POST, 
			        consumes = MediaType.APPLICATION_JSON_VALUE, 
			        path = "/notes")
	public ResponseEntity<Void> createNote(@Valid @RequestBody Note note) throws Exception {
		Note savedNote = noteService.create(note);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(savedNote.getId()).toUri();
		return ResponseEntity.created(location).build();
	}

}
