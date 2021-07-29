package com.example.booksmart;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.booksmart.helpers.ParseMessageClient;
import com.example.booksmart.models.Conversation;
import com.example.booksmart.models.Message;

import java.util.List;

public class ChatRepository {

    MutableLiveData<Conversation> conversation;
    MutableLiveData<List<Message>> messages;
    ParseMessageClient parseClient;
    Context context;

    public ChatRepository(Application application){
        conversation = new MutableLiveData<>();
        messages = new MutableLiveData<>();
        context = application.getBaseContext();

        setClient();
    }

    private void setClient() {
        parseClient = new ParseMessageClient(context) {

            @Override
            protected void onAllMessagesFetched(List<Message> allMessages) {
                if (allMessages != null){
                    messages.setValue(allMessages);
                }
            }

            @Override
            protected void onMessageSaved(Message message) {
                parseClient.getMessages(conversation.getValue());
            }

        };
    }

    public MutableLiveData<List<Message>> getMessages() {
        return messages;
    }

    public void fetchMessages(){
        if (conversation.getValue() != null){
            parseClient.getMessages(conversation.getValue());
        }
    }

    public MutableLiveData<Conversation> getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation.setValue(conversation);
    }

    public void saveMessage(Message message) {
        parseClient.saveMessage(message);
    }
}
