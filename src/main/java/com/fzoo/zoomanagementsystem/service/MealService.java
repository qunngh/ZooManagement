package com.fzoo.zoomanagementsystem.service;

import com.fzoo.zoomanagementsystem.model.Food;
import com.fzoo.zoomanagementsystem.model.FoodStorage;
import com.fzoo.zoomanagementsystem.model.Meal;
import com.fzoo.zoomanagementsystem.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

@Service
public class MealService {
    @Autowired
    private MealRepository mealRepository;
    @Autowired
    private CageRepository cageRepository;
    @Autowired
    private FoodService foodService;
    @Autowired
    private FoodStorageRepository foodStorageRepository;
    @Autowired
    private FoodInMealRepository foodInMealRepository;
    @Autowired
    private FoodRepository foodRepository;
    public Meal currentMeal;
    public void createMeal(String name) {

        Optional<Meal> optionalMeal = mealRepository.findByName(name);
        if(optionalMeal.isPresent()){
            throw new IllegalStateException("Meal was created");
        }
        Meal meal = Meal
                .builder()
                .cageInfo(cageRepository.findByName(name).get())
                .name(name + " meal")
                .build();
        currentMeal = meal;
    }

    public void saveMeal() {

        for (Food food:foodService.getListFood()
             ) {
            Optional<FoodStorage> foodStorage = foodStorageRepository.findAvailableByName(food.getName());
            if(food.getWeight()>foodStorage.get().getAvailable()){
                throw new IllegalStateException("does not have enough quantity");
            }
            foodStorage.get().setAvailable(foodStorage.get().getAvailable() - food.getWeight());
        }
        currentMeal.setHaveFood(foodService.getListFood());
        mealRepository.save(currentMeal);
        foodService.clear();
    }

    @Transactional
    public void update(int id, String name, float weight) {
//        Optional<Food> food = foodRepository.findById(id);
        Food food = foodRepository.findById(id).orElseThrow(() ->
                new IllegalStateException("food with "+ id+ " does not exits"));
        FoodStorage foodStorage = foodStorageRepository.findByName(name).orElseThrow(() ->
                new IllegalStateException("foodStorage with "+ name+ " does not exits"));
        if(weight>foodStorage.getAvailable()){
            throw new IllegalStateException("does not have enough food");
        }
        food.setWeight(weight);
        foodRepository.save(food);
    }


}
