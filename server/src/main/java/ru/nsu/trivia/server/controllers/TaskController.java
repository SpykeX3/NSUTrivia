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
import ru.nsu.trivia.server.model.SetNearestAnswerTask;
import ru.nsu.trivia.server.task.SelectAnswerTaskRepository;
import ru.nsu.trivia.server.task.SetNearestAnswerTaskRepository;

@RestController
@RequestMapping(value = "/task")
public class TaskController {

    private final SelectAnswerTaskRepository selectAnswerTaskRepository;
    private final SetNearestAnswerTaskRepository setNearestAnswerTaskRepository;

    @Autowired
    public TaskController(SelectAnswerTaskRepository selectAnswerTaskRepository,
                          SetNearestAnswerTaskRepository setNearestAnswerTaskRepository) {
        this.selectAnswerTaskRepository = selectAnswerTaskRepository;
        this.setNearestAnswerTaskRepository = setNearestAnswerTaskRepository;
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

    @PostMapping(value = "/nearest/delete")
    public void deleteNearestTask(@RequestParam long id) {
        setNearestAnswerTaskRepository.deleteById(id);
    }

    @PostMapping(value = "/nearest/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addNearestTasks(@RequestBody List<SetNearestAnswerTask> tasks) {
        setNearestAnswerTaskRepository.saveAll(tasks);
    }

    @GetMapping(value = "/nearest/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<SetNearestAnswerTask> getNearestTasks() {
        return setNearestAnswerTaskRepository.findAll();
    }

}
