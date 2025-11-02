package com.mainapp.openai.controller;

import com.mainapp.openai.model.CountryCities;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/structured")
public class StructuredOutputController {

    private final ChatClient chatClient;


    public StructuredOutputController(@Qualifier("openAiChatClientWithOptions") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/pojo")
    public org.springframework.http.ResponseEntity<CountryCities> strucutred(@RequestParam("message") String message) {

        CountryCities cities = chatClient.prompt().user(message).call().entity(CountryCities.class);
        return ResponseEntity.ok(cities);
    }
    //OR SIMILARLY:
    @GetMapping("/bean")
    public ResponseEntity<CountryCities> bean(@RequestParam("message") String message) {

        CountryCities cities = chatClient.prompt().user(message).call().entity(new BeanOutputConverter<>(CountryCities.class));
        return ResponseEntity.ok(cities);

    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> listOnly(@RequestParam("message") String message) {

        List<String> cities = chatClient.prompt().user(message).call().entity(new ListOutputConverter());
        return ResponseEntity.ok(cities);
    //NOTE: NO POJO definition is given but still the list is provided
    }

    @GetMapping("/map")
    public ResponseEntity<Map<String, Object>> map(@RequestParam("message") String message) {

        Map<String, Object> cities = chatClient.prompt().user(message).call().entity(new MapOutputConverter());
        return ResponseEntity.ok(cities);

    }

    @GetMapping("/object-list")
    public ResponseEntity<List<CountryCities>> objectList(@RequestParam("message") String message) {

        List<CountryCities> cities = chatClient.prompt()
                .user(message).call().entity(new ParameterizedTypeReference<List<CountryCities>>() {
                });
        return ResponseEntity.ok(cities);
        //NOTE: Here you want list of a complex Obj
        //This case you have an obj which has properties : (String , List<String>)
    }

}
