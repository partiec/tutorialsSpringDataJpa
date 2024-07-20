package ru.frolov.totors.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.frolov.totors.model.Tutorial;
import ru.frolov.totors.repository.TutorialRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class TutorialController {


    TutorialRepository tutorialRepository;

    // Метод должен вернуть список туториалов
    //----------------------------------------
    // Принимает параметр запроса - строку title.
    // создаем пустой список
    // если title отсутствует, то ВСЕ туториалы помещаем в список и возвращаем в виде ResponseEntity
    // а если title есть, то СООТВЕТСТВУЮЩИЙ ему туториал помещаем в список и возвращаем в виде ResponseEntity
    // если список вдруг пуст, то возвращаем ResponseEntity со статусом NO_CONTENT
    // если словили исключение, то возвращаем ResponseEntity c null и со статусом INTERNAL_SERVER_ERROR
    @GetMapping("/tutorials")
    public ResponseEntity<List<Tutorial>> getAllTutorials(@RequestParam(required = false) String title) {
        try {
            List<Tutorial> tutorials = new ArrayList<>();

            if (title == null) {
                tutorialRepository.findAll().forEach(tutorials::add);
            } else {
                tutorialRepository.findByTitleContaining(title).forEach(tutorials::add);
            }

            if (tutorials.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(tutorials, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Метод должен вернуть туториал по id
    //------------------------------------
    // Принимает в качестве аргумента переменную пути и присваивает ее переменной Long id.
    // Получаем по id туториал и заворачиваем в Optional
    // если в Optional есть туториал, то возвращаем его обернутого в ResponseEntity со статусом OK
    // а если Optional пуст, то возвращаем пустой ResponseEntity со статусом NOT_FOUND
    @GetMapping("/tutorials/{id}")
    public ResponseEntity<Tutorial> getTutorialById(@PathVariable("id") Long id) {
        Optional<Tutorial> tutorialData = this.tutorialRepository.findById(id);

        if (tutorialData.isPresent()) {
            return new ResponseEntity<>(tutorialData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    // Метод создает новый туториал (и сохраняет)
    //-------------------------------------------
    // Принимает тело запроса в виде объекта туториал
    // Создаем новый туториал на основе принятого из тела запроса, сохраняем его, а результат работы метода save оборачиваем в ResponseEntity со статусом CREATED и возвращаем
    // Если ловим исключение, то возвращаем null, обернутый в ResponseEntity со статусом INTERNAL_SERVER_ERROR
    @PostMapping("/tutorials")
    public ResponseEntity<Tutorial> createTutorial(@RequestBody Tutorial tutorial) {
        try {
            System.out.println("--->>> " + tutorial);

            Tutorial _tutorial = this.tutorialRepository.save(new Tutorial(tutorial.getId(), tutorial.getTitle(), tutorial.getDescription(), tutorial.isPublished()));
            System.out.println("--->>> " + _tutorial);
            return new ResponseEntity<>(_tutorial, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println("--->>>catch");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Метод обновляет туториал с определенным id
    //----------------------------------------------
    // Принимает переменную пути (id) и тело запроса (tutorial)
    // Получаем по id коткретный туториал в виде Optional
    // Если такой есть, то сетим ему новые значения и сохраняем его, а результат работы метода set заворачиваем в ResponseEntity со статусом OK и возвращаем
    // Если такого нет, то возвращаем просто ResponseEntity со статусом NOT_FOUND
    @PutMapping("/tutorials/{id}")
    public ResponseEntity<Tutorial> updateTutorial(@PathVariable("id") Long id, @RequestBody Tutorial tutorial) {
        Optional<Tutorial> tutorialData = this.tutorialRepository.findById(id);

        if (tutorialData.isPresent()) {
            Tutorial _tutorial = tutorialData.get();
            _tutorial.setTitle(tutorial.getTitle());
            _tutorial.setDescription(tutorial.getDescription());
            _tutorial.setPublished(tutorial.isPublished());
            return new ResponseEntity<>(tutorialRepository.save(_tutorial), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Метод удаляет туториал по id
    //-------------------------------
    // Если удаление прошло без исключений, то возвращаем new ResponseEntity со статусом NO_CONTENT
    // Если словили исключение, то возвращаем new ResponseEntity со статусом INTERNAL_SERVER_ERROR
    @DeleteMapping("/tutorials/{id}")
    public ResponseEntity<Tutorial> deleteTutorial(@PathVariable("id") Long id) {
        try {
            this.tutorialRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Метод удаляет все туториалы
    // -----------------------------
    // Удаляем все и возвращаем new ResponseEntity со статусом NO_CONTENT
    // Если словили исключение, то возвращаем new ResponseEntity со статусом INTERNAL_SERVER_ERROR
    @DeleteMapping("/tutorials")
    public ResponseEntity<HttpStatus> deleteAllTutorials() {
        try {
            this.tutorialRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Метод возвращает все published туториалы
    // ------------------------------------------
    // Формируем список published туториалов
    // Если он пуст, то возвращаем new ResponseEntity со статусом NO_CONTENT
    // А если не пуст, то возвращаем его завернутым в ResponseEntity со статусом OK
    // Если ловим исключение, то возвращаем new ResponseEntity со статусом INTERNAL_SERVER_ERROR
    @GetMapping("/tutorials/published")
    public ResponseEntity<List<Tutorial>> findByPublished() {
        try {
            List<Tutorial> tutorials = this.tutorialRepository.findByPublished(true);

            if (tutorials.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(tutorials, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
