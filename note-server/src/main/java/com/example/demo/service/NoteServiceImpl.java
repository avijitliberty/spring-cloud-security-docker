package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.example.demo.model.Note;
import com.example.demo.repository.NoteRepository;

@Service
public class NoteServiceImpl implements NoteService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private NoteRepository noteRepository;

	@Override
	public Note create(Note note) throws Exception {
		// TODO Auto-generated method stub
		log.info("new note has been created: {}", note.toString());
		return noteRepository.save(note);
	}

	@Override
	public Note update(Note note) throws Exception {
		// TODO Auto-generated method stub
		Note existingNote = noteRepository.findOne(note.getId());

		if (existingNote == null) {
			log.info("note does not exist: " + note.getId());
			throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "note does not exist");
		} else {

			existingNote.setSubject(note.getSubject());
			existingNote.setTopic(note.getTopic());
			existingNote.setBody(note.getBody());

			log.info("note updated: {}", existingNote.toString());
			return noteRepository.save(existingNote);
		}
	}

	@Override
	public String delete(Integer noteId) throws Exception {

		Note existingNote = noteRepository.findOne(noteId);

		if (existingNote == null) {
			log.info("user does not exist: " + noteId);
			throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "user does not exist");
		} else {

			noteRepository.delete(noteId);
			log.info("deleted note: {}", noteId);
			return "deleted note: " + noteId;
		}
	}

	@Override
	public List<Note> findNotes(String createdBy, Integer id) throws Exception {

		List<Note> notes = new ArrayList<Note>();

		if (createdBy != null && !createdBy.trim().isEmpty()) {

			for (Note note : noteRepository.findAll()) {
				if (note.getCreatedBy().equalsIgnoreCase(createdBy)) {
					notes.add(note);
				}
			}
		} else if (id != null) {
			Note noteById = noteRepository.findOne(id);
			if (noteById == null) {
				throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "note does not exist");
			} else {
				notes.add(noteById);
			}
		} else {
			for (Note note : noteRepository.findAll()) {
				notes.add(note);
			}
		}
		return notes;
	}
}
