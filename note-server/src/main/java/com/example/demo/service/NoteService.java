package com.example.demo.service;

import java.util.List;

import com.example.demo.model.Note;

//@Service
public interface NoteService {

	/*
	 * public Note create(@Valid Note note) {
	 * 
	 * Note tempNote = new Note(1, note.getTopic(), note.getSubject(),
	 * note.getBody());
	 * 
	 * return tempNote; }
	 */
	Note create(Note note) throws Exception;

	Note update(Note note) throws Exception;

	String delete(Integer noteId) throws Exception;

	List<Note> findNotes(String createdBy, Integer id) throws Exception;

}
