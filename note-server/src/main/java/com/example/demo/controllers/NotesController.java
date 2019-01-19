package com.example.demo.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
	// @PreAuthorize("#oauth2.hasScope('toll_report')")
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, path = "/notes")
	public ResponseEntity<?> createNote(@Valid @RequestBody Note note) throws Exception {
		Note savedNote = noteService.create(note);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(savedNote.getId()).toUri());
		ResponseEntity<Void> response = new ResponseEntity<Void>(headers, HttpStatus.CREATED);
		log.info("Response: " + response);
		return response;
	}

	/* Read Notes */
	// @PreAuthorize("#oauth2.hasScope('toll_report')")
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/notes")
	public ResponseEntity<List<Note>> getNotes(@RequestParam(value = "createdBy", required = false) String createdBy) throws Exception {
		
		List<Note> notes = new ArrayList<Note>();
		
		if (createdBy != null && !createdBy.trim().isEmpty()) {
			notes.addAll(noteService.findNotes(createdBy, null));
			if (notes.isEmpty()) {
				return new ResponseEntity(HttpStatus.NOT_FOUND);
			}
		} else {
			notes.addAll(noteService.findNotes(null, null));
		}
		return new ResponseEntity<List<Note>>(notes, HttpStatus.OK);
	}
	
	/* Read Notes by Id */
	//@PreAuthorize("#oauth2.hasScope('toll_read')")
	@RequestMapping(method = RequestMethod.GET, 
			        produces = MediaType.APPLICATION_JSON_VALUE, 
			        path = "/notes/{id}")
	public ResponseEntity<Note> getNotesById(@PathVariable Integer id) throws Exception {
		Note note = noteService.findNotes(null, id).get(0);
		return new ResponseEntity<Note>(note,HttpStatus.OK);
	}
	
	/* Update */
	//@PreAuthorize("#oauth2.hasScope('toll_report')")
	@RequestMapping(method = RequestMethod.PUT, 
			        consumes = MediaType.APPLICATION_JSON_VALUE, 
			        produces = MediaType.APPLICATION_JSON_VALUE, 
			        path = "/notes")
	public ResponseEntity<Note> updateNote(@Valid @RequestBody Note note) throws Exception {
		Note updatedNote = noteService.update(note);
		return new ResponseEntity<Note>(updatedNote,HttpStatus.OK);
	}

	/* Delete */
	//@PreAuthorize("#oauth2.hasScope('toll_report')")
	@RequestMapping(method = RequestMethod.DELETE, 
			        path = "/notes/{id}")
	public ResponseEntity<String> deleteNote(@PathVariable Integer id) throws Exception {
		String response = noteService.delete(id);
		return new ResponseEntity<String>(response,HttpStatus.OK);
	}
}
