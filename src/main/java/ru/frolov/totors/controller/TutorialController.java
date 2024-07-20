package ru.frolov.totors.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.frolov.totors.model.Tutorial;
import ru.frolov.totors.repository.TutorialRepository;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class TutorialController {

    TutorialRepository tutorialRepository;

    @GetMapping("/tutorials")
    // Метод должен вернуть список туториалов, обернутый в ResponseEntity. Принимает в качестве аргумента строку title.
    public ResponseEntity<List<Tutorial>> getAllTutorials(@RequestParam(required = false) String title){
        try{
            // создаем пустой список
            List<Tutorial>tutorials = new ArrayList<>();

            if (title == null){
                // если title не пришел, то ВСЕ туториалы помещаем в список и возвращаем в виде ResponseEntity
                tutorialRepository.findAll().forEach(tutorials::add);
            }else {
                // а если title есть, то СООТВЕТСТВУЮЩИЙ ему туториал помещаем в список и возвращаем в виде ResponseEntity
                tutorialRepository.findByTitleContaining(title).forEach(tutorials::add);
            }

            if (tutorials.isEmpty()){
                // если список вдруг пуст, то возвращаем ResponseEntity со статусом NO_CONTENT
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(tutorials, HttpStatus.OK);
        }catch (Exception e){
            // если словили исключение, то возвращаем ResponseEntity c null и со статусом INTERNAL_SERVER_ERROR
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/tutorial/{id}")
    public ResponseEntity<Tutorial> getTutorialById(@PathVariable("id")Long id){
        return null;
    }

}
