package com.mainapp.openai.controller;

import com.mainapp.openai.model.CountryCities;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/structured")
@Tag(name = "Structured Output", description = "Endpoints for structured AI responses (POJO, List, Map)")
public class StructuredOutputController {

    private final ChatClient chatClient;


    public StructuredOutputController(@Qualifier("openAiChatClientWithOptions") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/pojo")
    @Operation(summary = "POJO response", description = "Get AI response as structured Java object")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful response with POJO object",
                    content = @Content(schema = @Schema(implementation = CountryCities.class))),
        @ApiResponse(responseCode = "400", description = "Invalid message parameter"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CountryCities> strucutred(
            @Parameter(description = "Message to generate structured response", required = true, example = "Tell me about France and its cities")
            @RequestParam("message") String message) {

        CountryCities cities = chatClient.prompt().user(message).call().entity(CountryCities.class);
        return ResponseEntity.ok(cities);
    }
    //OR SIMILARLY:
    @GetMapping("/bean")
    @Operation(summary = "Bean converter response", description = "Convert AI response to bean using BeanOutputConverter")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful response with bean object",
                    content = @Content(schema = @Schema(implementation = CountryCities.class))),
        @ApiResponse(responseCode = "400", description = "Invalid message parameter"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CountryCities> bean(
            @Parameter(description = "Message to generate bean response", required = true, example = "Countries and capitals")
            @RequestParam("message") String message) {

        CountryCities cities = chatClient.prompt().user(message).call().entity(new BeanOutputConverter<>(CountryCities.class));
        return ResponseEntity.ok(cities);

    }

    @GetMapping("/list")
    @Operation(summary = "List response", description = "Get AI response as string list")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful response with string list",
                    content = @Content(schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "400", description = "Invalid message parameter"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<String>> listOnly(
            @Parameter(description = "Message to generate list response", required = true, example = "List 5 programming languages")
            @RequestParam("message") String message) {

        List<String> cities = chatClient.prompt().user(message).call().entity(new ListOutputConverter());
        return ResponseEntity.ok(cities);
    //NOTE: NO POJO definition is given but still the list is provided
    }

    @GetMapping("/map")
    @Operation(summary = "Map response", description = "Get AI response as key-value map")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful response with map",
                    content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "400", description = "Invalid message parameter"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> map(
            @Parameter(description = "Message to generate map response", required = true, example = "Key facts about AI")
            @RequestParam("message") String message) {

        Map<String, Object> cities = chatClient.prompt().user(message).call().entity(new MapOutputConverter());
        return ResponseEntity.ok(cities);

    }

    @GetMapping("/object-list")
    @Operation(summary = "Object list response", description = "Get AI response as list of complex objects")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful response with object list",
                    content = @Content(schema = @Schema(implementation = CountryCities.class))),
        @ApiResponse(responseCode = "400", description = "Invalid message parameter"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<CountryCities>> objectList(
            @Parameter(description = "Message to generate object list response", required = true, example = "Multiple countries and their cities")
            @RequestParam("message") String message) {

        List<CountryCities> cities = chatClient.prompt()
                .user(message).call().entity(new ParameterizedTypeReference<List<CountryCities>>() {
                });
        return ResponseEntity.ok(cities);
        //NOTE: Here you want list of a complex Obj
        //This case you have an obj which has properties : (String , List<String>)
    }

}
