package com.example.demo.service;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import com.example.demo.model.Note;

@Service
public class NoteService {

	public Note create(@Valid Note note) {
		
		Note tempNote = new Note(1, note.getTopic(), note.getSubject(), note.getBody());
		
		return tempNote;
	}

}
