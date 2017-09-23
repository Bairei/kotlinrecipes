package com.bairei.springrecipes.controllers

import com.bairei.springrecipes.commands.RecipeCommand
import com.bairei.springrecipes.domain.Recipe
import com.bairei.springrecipes.services.RecipeService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

/**
 * created by bairei on 21/09/17.
 */
@Controller
class RecipeController @Autowired constructor(val recipeService: RecipeService){

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping
    @RequestMapping("/recipe/{id}/show")
    fun showById(@PathVariable id: String, model:Model) : String {
        model.addAttribute("recipe", recipeService.findById(id.toLong()))

        return "recipe/show"
    }

    @GetMapping
    @RequestMapping("recipe/new")
    fun newRecipe(model : Model) : String {
        model.addAttribute("recipe", Recipe())
        return "recipe/recipeform"
    }

    @GetMapping
    @RequestMapping("recipe/{id}/update")
    fun updateRecipe (@PathVariable id: String, model: Model): String {
        model.addAttribute("recipe", recipeService.findCommandById(id.toLong()))
        return "recipe/recipeform"
    }

    @PostMapping
    @RequestMapping("recipe")
    fun saveOrUpdate(@ModelAttribute command : RecipeCommand) : String {
        val savedCommand = recipeService.saveRecipeCommand(command)
        return "redirect:/recipe/" + savedCommand.id + "/show"
    }

    @GetMapping
    @RequestMapping("recipe/{id}/delete")
    fun deleteRecipe(@PathVariable id: String): String{
        recipeService.deleteById(id.toLong())
        log.debug("Deleting id: " + id)
        return "redirect:/"
    }
}