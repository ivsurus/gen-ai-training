package com.epam.training.gen.ai.controller;

import com.epam.training.gen.ai.service.ChatBotService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatBotController {

    private final ChatBotService chatBotService;
    private final ObjectMapper objectMapper;


    public ChatBotController(ChatBotService chatBotService) {
        this.chatBotService = chatBotService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @GetMapping
    public ResponseEntity<String> chat(@RequestParam String input) {
        Map<String, String> responseMap = new HashMap<>();

        try {
            String response = chatBotService.getChatBotResponse(input);
            responseMap.put("input", input);
            responseMap.put("response", response);
            return ResponseEntity.ok(objectMapper.writeValueAsString(responseMap));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
}