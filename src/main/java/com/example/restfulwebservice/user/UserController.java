package com.example.restfulwebservice.user;

import com.example.restfulwebservice.exception.CustomizedResponseEntityExceptionHandler;
import com.example.restfulwebservice.exception.ExceptionResponse;
import org.aspectj.weaver.patterns.ExactTypePattern;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Date;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class UserController {
    private UserDaoService service;


    public UserController(UserDaoService service) {
        this.service = service;
    }

    @GetMapping("/users")
    public List<User> retrieveAllUsers(){
        return service.findAll();
    }

    // GET /users/1 or /users/10 -> String
    @GetMapping("/users/{id}")
    public EntityModel<User>  retrieveUser(@PathVariable int id){
        User user = service.findOne(id);
        if (user == null){
            throw new UserNotFoundException(String.format("ID[%s] not found",id));
        }

        // HATEOAS
        EntityModel<User> model = EntityModel.of(user);
        WebMvcLinkBuilder linkTo = WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(this.getClass()).retrieveAllUsers());
        model.add(linkTo.withRel("all-users"));

        return model;
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user){
        User savedUser = service.save(user);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();
        return ResponseEntity.created(location).build();
        //http://localhost:8088/users/4
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable int id) {
        User user = service.deleteById(id);
        System.out.println(user);
        if(user == null){
            throw new UserNotFoundException(String.format("ID[%s] not found",id));
        }
    }

    /**
     * 웹 서버에서 url 리퀘스트를 받아서 적절한 컨트롤러의 함수를 실행하고 결과를 반환하는 함수
     * 입력값:  url
     * 출력값:  결과 오브젝트
     * 1. 해당 url와 매핑되는 메소드 실행
         1-1 url 에서 컨트롤러 이름을 찾는다
         1-2 url 에서 메소드 이름을 찾는다.
     * 2. 매핑된 메소드의 로직에 따라 코드 실행
     *   2-1 찾은 컨트롤러 이름에 따라 컨트롤러 객체를 만들거나 가져온다.
     *   2-2 생성된 객체에서 url 에 포함된 메소드를 실행한다.
     * 3. 그 코드에 따라 결과 오브젝트를 클라이언트에 반환
     */
    public void parseRequest(){

    }
}
