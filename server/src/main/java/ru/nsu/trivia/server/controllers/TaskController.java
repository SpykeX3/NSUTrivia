package ru.nsu.trivia.server.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.trivia.server.model.SelectAnswerTask;
import ru.nsu.trivia.server.task.SelectAnswerTaskRepository;

@RestController
@RequestMapping(value = "/task")
public class TaskController {

    private final SelectAnswerTaskRepository selectAnswerTaskRepository;

    @Autowired
    public TaskController(SelectAnswerTaskRepository selectAnswerTaskRepository) {
        this.selectAnswerTaskRepository = selectAnswerTaskRepository;
    }

    @PostMapping(value = "/select/delete")
    public void deleteSelectTask(@RequestParam long id) {
        selectAnswerTaskRepository.deleteById(id);
    }

    @PostMapping(value = "/select/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addSelectTasks(@RequestBody List<SelectAnswerTask> tasks) {
        selectAnswerTaskRepository.saveAll(tasks);
    }

    @GetMapping(value = "/select/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<SelectAnswerTask> getSelectTasks() {
        return selectAnswerTaskRepository.findAll();
    }

}
