package com.fzoo.zoomanagementsystem.service;

import com.fzoo.zoomanagementsystem.dto.AnimalUpdatingDTO;
import com.fzoo.zoomanagementsystem.model.Animal;
import com.fzoo.zoomanagementsystem.model.AnimalSpecies;
import com.fzoo.zoomanagementsystem.model.Cage;
import com.fzoo.zoomanagementsystem.repository.AnimalRepository;
import com.fzoo.zoomanagementsystem.repository.CageRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AnimalService {
    private final AnimalRepository animalRepository;
    private final CageRepository cageRepository;

    public List<Animal> getAnimalCageName(List<Animal> animalList){
        for(Animal animal : animalList){
            animal.setCageName(cageRepository.findCageNameByCageId(animal.getCageId()));
        }
        return animalList;
    }
    public List<Animal> getAllAnimals() {
        List<Animal> animalList = animalRepository.findAllAlive(Sort.by(Sort.Direction.ASC, "cageId"));
        if (animalList.isEmpty()) throw new IllegalStateException("There are no animals");
        animalList = getAnimalCageName(animalList);
        return animalList;
    }
    public List<Animal> getAllDeadAnimal() {
        List<Animal> animalList = animalRepository.findAllDeadAnimal();
        if (animalList.isEmpty()) throw new IllegalStateException("There are no animals");
        animalList = getAnimalCageName(animalList);
        return animalList;
    }
    public Animal searchAnimalByID(int animalID){
        Animal animal = animalRepository.findById(animalID).orElseThrow(() -> new IllegalStateException("This animal is not exists!"));
        animal.setCageName(cageRepository.findCageNameByCageId(animalID));
        return animal;
    }
    public List<Animal> searchAnimalByName(String animalName) {
        List<Animal> animalList = animalRepository.findByName(animalName);
        if (animalList.isEmpty()) throw new IllegalStateException("There are no animals");
        animalList = getAnimalCageName(animalList);
        return animalList;
    }
    public List<Animal> searchAnimalByCageId(int cageID){
        List<Animal> animalList = animalRepository.findByCageId(cageID);
        if(animalList.isEmpty()) throw new IllegalStateException("There are no animals in this cage !");
        animalList = getAnimalCageName(animalList);
        return animalList;
    }
    public List<Animal> searchAnimalByCageName(String cageName) {
        List<Animal> animalList = animalRepository.findByCageName(cageName);
        if(animalList.isEmpty()) throw new IllegalStateException("Search result return empty !");
        animalList = getAnimalCageName(animalList);
        return animalList;
    }


    public void createNewAnimal(Animal animal) {
        Cage cage = cageRepository.findCageById(animal.getCageId());
        int cageQuantity = 0;
        animal.setDez(LocalDate.now());
        if (cage != null) {
            animalRepository.save(animal);
            for (Animal animalInCage : animalRepository.findByCageId(animal.getCageId())) {
                if (!animalInCage.getStatus().equals("Dead")) {
                    cageQuantity++;
                }
            }
            cage.setQuantity(cageQuantity);
        } else {
            throw new IllegalStateException("Cage not found");
        }
        cageRepository.save(cage);
    }


    public void updateAnimalInformation(int id, AnimalUpdatingDTO request) {
        Animal animal = animalRepository.findById(id).orElseThrow(() -> new IllegalStateException("Animal with " + id + " is not found"));
        Cage cage = cageRepository.findCageByName(request.getCageName());
        if (cage != null) {
            animal.setCage(cage);
            if (request.getName() != null) animal.setName(request.getName());
            if (request.getDob() != null) animal.setDob(request.getDob());
            if (request.getDez() != null) animal.setDez(request.getDez());
            if (request.getGender() != null) animal.setGender(request.getGender());
            if (request.getSpecie() != null && !request.getSpecie().equals("Dead"))
                animal.setSpecie(request.getSpecie());
            if (request.getStatus() != null) throw new IllegalStateException("Can not update Animal Status !");
            animal.setCageId(cageRepository.findCageIdByCageName(request.getCageName()));
        }
        animalRepository.save(animal);
    }

    public void deleteAnimal(int id) {
        Animal animal = animalRepository.findById(id).orElseThrow(() -> new IllegalStateException("Animal with " + id + " is not found"));
        animal.setStatus("Dead");
        animalRepository.save(animal);
        Cage cage = cageRepository.findCageById(animal.getCageId());
        int aliveAnimalInCage = animalRepository.findAllAliveInCage(animal.getCageId()).size();
        if (cage != null) {
            cage.setQuantity(aliveAnimalInCage);
            cageRepository.save(cage);
        }
    }

}

