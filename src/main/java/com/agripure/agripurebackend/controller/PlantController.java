package com.agripure.agripurebackend.controller;

import com.agripure.agripurebackend.entities.Plant;
import com.agripure.agripurebackend.service.IPlantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/plants")
public class PlantController {

    private final IPlantService plantService;

    public PlantController(IPlantService plantService) {
        this.plantService = plantService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List Plants", description = "Method for list all plants")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Plants found"),
            @ApiResponse(responseCode = "404", description = "Plants not found"),
            @ApiResponse(responseCode = "501", description = "Internal Server Error")
    })
    public ResponseEntity<List<Plant>> findAllPlants(){
        try{
            List<Plant> plants = plantService.getAll();
            return new ResponseEntity<>(plants, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value="/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Find plants by Id", description = "Method for list one plants by Id")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Plant found"),
            @ApiResponse(responseCode = "404", description = "Plant not found"),
            @ApiResponse(responseCode = "501", description = "Internal Server Error")
    })
    public ResponseEntity<Plant> findPlantsById(@PathVariable("id") Long id){
        try{
            Optional<Plant> plant = plantService.getById(id);
            if(!plant.isPresent())
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(plant.get(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/searchByName/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Found plant by Name", description = "Method for list one plant by Id")
    public ResponseEntity<Plant> findPlantsByName(@PathVariable("name") String name){
        try{
            Plant plant = plantService.findByName(name);
            if(plant != null)
                return  new ResponseEntity<>(plant, HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Insert Plant", description = "Method for insert new plant")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Plant created"),
            @ApiResponse(responseCode = "404", description = "Plant not created"),
            @ApiResponse(responseCode = "501", description = "Internal Server Error")
    })
    public ResponseEntity<Plant> insertPlant(@Valid @RequestBody Plant plant){
        try{
            Plant plantNew = plantService.save(plant);
            return ResponseEntity.status(HttpStatus.CREATED).body(plantNew);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update data for Plant", description = "Method for update data for plant")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Data for Plant updated"),
            @ApiResponse(responseCode = "404", description = "Plant not found"),
            @ApiResponse(responseCode = "501", description = "Internal Server Error")
    })
    public ResponseEntity<Plant> updatePlant(@PathVariable("id") Long id, @Valid @RequestBody Plant plant){
        try{
            Optional<Plant> plantOld = plantService.getById(id);
            if(!plantOld.isPresent())
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            else{
                plant.setId(id);
                plantService.save(plant);
                return new ResponseEntity<>(plant, HttpStatus.OK);
            }
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete Plant by Id", description = "Method for delete plant")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Plant deleted"),
            @ApiResponse(responseCode = "404", description = "Plant not found"),
            @ApiResponse(responseCode = "501", description = "Internal Server Error")
    })
    public ResponseEntity<Plant> deletePlant(@PathVariable("id") Long id){
        try{
            Optional<Plant> bookingDelete = plantService.getById(id);
            if(bookingDelete.isPresent()){
                plantService.delete(id);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception ex){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}