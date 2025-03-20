package com.epam.training.gen.ai.service;

import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.InvocationContext;

import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ChatBotService {

    @Value("${client-openai-deployment-name}")
    private String openAiDeploymentName;

    private final Kernel kernel;
    private final ChatCompletionService chatCompletionService;
    private final ChatHistory chatHistory;
    private final InvocationContext invocationContext;

    public ChatBotService(Kernel kernel,
                          ChatCompletionService chatCompletionService,
                          ChatHistory chatHistory,
                          InvocationContext invocationContext) {
        this.kernel = kernel;
        this.chatCompletionService = chatCompletionService;
        this.chatHistory = chatHistory;
        this.invocationContext = invocationContext;
    }

    public String getChatBotResponse(String input) {
        // Add system message only once
        if (chatHistory.getMessages().isEmpty()) {
            chatHistory.addSystemMessage(
                    "You have access to a function called TimePlugin.getCurrentTime. " +
                            "If the user asks for the current time or date, ALWAYS call this function " +
                            "Do NOT answer the time yourself."
            );
            chatHistory.addSystemMessage("Include light humor in responses while keeping them informative.");
        }

        log.info("Received input: {}", input);
        chatHistory.addUserMessage(input);


        try {
            List<ChatMessageContent<?>> results = chatCompletionService
                    .getChatMessageContentsAsync(chatHistory, kernel, invocationContext)
                    .block();

            Optional.ofNullable(results)
                    .orElseGet(Collections::emptyList)
                    .forEach(result -> {
                        Optional.ofNullable(result.getContent())
                                .ifPresent(content -> {
                                    log.info("Assistant > {}", result);
                                    chatHistory.addMessage(result);
                                });
                    });

            return results.toString();
        } catch (Exception e) {
            log.error("Error while creating chatbot message: {}", e.getMessage(), e);
            throw new RuntimeException("Error occurred while getting chatbot response", e);
        }
    }

}