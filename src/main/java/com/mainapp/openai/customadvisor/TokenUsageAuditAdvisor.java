package com.mainapp.openai.customadvisor;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.ILoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;


@Slf4j
public class TokenUsageAuditAdvisor implements CallAdvisor {
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        //MANDATORY: So that next advisor has access to advise in the chain
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        ChatResponse chatResponse =  chatClientResponse.chatResponse();
        if(chatResponse.getMetadata()!=null){
            //EACH AI MODEL SENDS USAGE IN SOME NODE IN THE RESPONSE . nothing for local ai model
            Usage usage = chatResponse.getMetadata().getUsage();
            log.info("Total token used for this chat {}", usage.getTotalTokens());
        }
        //return should be the chat client response
        return  chatClientResponse;

    }

    @Override
    public String getName() {
        return "TokenUsageAuditAdvisor";
    }

    //Default order of the advisor
    @Override
    public int getOrder() {
        return 1;
    }
}
