package com.epam.training.gen.ai.service;


import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.services.chatcompletion.AuthorRole;
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

    public ChatBotService(Kernel kernel, ChatCompletionService chatCompletionService, ChatHistory chatHistory, InvocationContext invocationContext) {
        this.kernel = kernel;
        this.chatCompletionService = chatCompletionService;
        this.chatHistory = chatHistory;
        this.invocationContext = invocationContext;
    }


    public String getChatBotResponse(String input) {
        chatHistory.addUserMessage(input);

        log.info("Received input: {}", input);

        try {
            List<ChatMessageContent<?>> results = chatCompletionService
                    .getChatMessageContentsAsync(chatHistory, kernel, invocationContext)
                    .block();

            Optional.ofNullable(results)
                    .orElseGet(Collections::emptyList)
                    .stream()
                    .filter(result -> result != null && result.getAuthorRole() == AuthorRole.ASSISTANT)
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