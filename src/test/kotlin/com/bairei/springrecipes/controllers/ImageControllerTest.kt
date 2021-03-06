package com.bairei.springrecipes.controllers

import com.bairei.springrecipes.commands.RecipeCommand
import com.bairei.springrecipes.services.ImageService
import com.bairei.springrecipes.services.RecipeService
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders


class ImageControllerTest {

    @Mock
    lateinit var imageService: ImageService

    @Mock
    lateinit var recipeService: RecipeService

    lateinit var controller : ImageController

    lateinit var mockMvc: MockMvc

    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        controller = ImageController(imageService, recipeService)
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(ControllerExceptionHandler()).build()
    }

    @Test
    @Throws(Exception::class)
    fun getImageForm() {
        //given
        val command = RecipeCommand()
        command.id = 1L

        `when`(recipeService.findCommandById(anyLong())).thenReturn(command)

        //when
        mockMvc.perform(get("/recipe/1/image"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("recipe"))

        verify(recipeService, times(1)).findCommandById(anyLong())

    }

    @Test
    @Throws(Exception::class)
    fun handleImagePost() {
        val multipartFile = MockMultipartFile("imagefile", "testing.txt", "text/plain",
                "Spring Framework Guru".toByteArray())

        mockMvc.perform(multipart("/recipe/1/image").file(multipartFile))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/recipe/1/show"))

        verify(imageService, times(1)).saveImageFile(anyLong(), com.nhaarman.mockito_kotlin.any())
    }

    @Test
    fun renderImageFromDB(){
        // given
        val command = RecipeCommand()
        command.id = 1L

        val s = "fake image test"
        val bytesBoxed = ByteArray(s.toByteArray().size)

        var i = 0

        for (primByte in s.toByteArray()){
            bytesBoxed[i++] = primByte
        }

        command.image = bytesBoxed

        `when`(recipeService.findCommandById(anyLong())).thenReturn(command)

        //when
        val response: MockHttpServletResponse = mockMvc.perform(get("/recipe/1/recipeimage"))
                .andExpect(status().isOk)
                .andReturn().response

        val responseBytes = response.contentAsByteArray

        assertEquals(s.toByteArray().size, responseBytes.size)
    }

    @Test
    fun testGetImageNumberFormatException() {
        mockMvc.perform(get("/recipe/asgsa/recipeimage"))
                .andExpect(status().isBadRequest)
                .andExpect(view().name("400error"))
    }
}