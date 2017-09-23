package com.bairei.springrecipes.controllers

import com.bairei.springrecipes.commands.IngredientCommand
import com.bairei.springrecipes.services.IngredientService
import com.bairei.springrecipes.services.RecipeService
import com.bairei.springrecipes.services.UnitOfMeasureService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
class IngredientController constructor(private val recipeService: RecipeService,
                                       private val ingredientService: IngredientService,
                                       private val unitOfMeasureService: UnitOfMeasureService) {

    private val log: Logger = LoggerFactory.getLogger(IngredientController::class.java)

    @GetMapping
    @RequestMapping("/recipe/{recipeId}/ingredients")
    fun listIngredients(@PathVariable recipeId: String, model: Model): String {
        log.debug("Getting ingredient list for recipe id " + recipeId)

        // use command object to avoid lazy load errors in Thymeleaf.
        model.addAttribute("recipe", recipeService.findCommandById(recipeId.toLong()))
        return "recipe/ingredient/list"
    }

    @GetMapping
    @RequestMapping("/recipe/{recipeId}/ingredient/{id}/show")
    fun showRecipeIngredient(@PathVariable recipeId: String,
                             @PathVariable id: String, model: Model) : String{
        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(recipeId.toLong(), id.toLong()))
        return "recipe/ingredient/show"
    }

    @GetMapping
    @RequestMapping("/recipe/{recipeId}/ingredient/{id}/update")
    fun updateRecipeIngredient(@PathVariable recipeId: String, @PathVariable id: String,
                               model: Model) : String{
        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(recipeId.toLong(), id.toLong()))
        model.addAttribute("uomList", unitOfMeasureService.listAllUoms())

        return "recipe/ingredient/ingredientform"
    }

    @PostMapping
    @RequestMapping("/recipe/{recipeId}/ingredient")
    fun saveOrUpdate(@ModelAttribute command: IngredientCommand) : String{
        val savedCommand = ingredientService.saveIngredientCommand(command)!!

        log.debug("saved recipe id: " + savedCommand.recipeId)
        log.debug("saved ingredient id: " + savedCommand.id)

        return "redirect:/recipe/" + savedCommand.recipeId + "/ingredient/" + savedCommand.id + "/show"
    }

}