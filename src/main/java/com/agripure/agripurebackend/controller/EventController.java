package com.agripure.agripurebackend.controller;

import com.agripure.agripurebackend.entities.Event;
import com.agripure.agripurebackend.entities.User;
import com.agripure.agripurebackend.service.IEventService;
import com.agripure.agripurebackend.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/events")
public class EventController {
    private final IEventService eventService;
    private final IUserService userService;

    public EventController(IEventService eventService, IUserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List Events By Date", description = "Method for list all Events by Date")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Events found"),
            @ApiResponse(responseCode = "404", description = "Events not found"),
            @ApiResponse(responseCode = "501", description = "Internal Server Error")
    })
    public ResponseEntity<List<Event>> findAllByDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> req) {
        try {
            if (req.isPresent()) {
                LocalDate date = req.get();
                List<Event> events = eventService.findAllByDate(date);
                return new ResponseEntity<>(events, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Add Events", description = "Method for add new events")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Events created"),
            @ApiResponse(responseCode = "404", description = "Plants not created"),
            @ApiResponse(responseCode = "501", description = "Internal Server Error")
    })
    public ResponseEntity<Event> insertEvent(@PathVariable("userId") Long userId, @Valid @RequestBody Event event){
        try{
            Optional<User> user = userService.getById(userId);
            if (user.isPresent()) {
                event.setUser(user.get());
                Event newEvent = eventService.save(event);
                return ResponseEntity.status(HttpStatus.CREATED).body(newEvent);
            }
            else
                return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
        }catch (Exception ex){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete Event by Id", description = "Method for delete event")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Event deleted"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "501", description = "Internal Server Error")
    })
    public ResponseEntity<Event> deleteEvent (@PathVariable("id") Long id){
        try{
            Optional<Event> eventDelete = eventService.getById(id);
            if(!eventDelete.isPresent())
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            eventService.delete(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception ex){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}