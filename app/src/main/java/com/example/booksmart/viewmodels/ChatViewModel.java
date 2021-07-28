package com.example.booksmart.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.booksmart.ChatRepository;
import com.example.booksmart.models.Conversation;
import com.example.booksmart.models.Listing;
import com.example.booksmart.models.Message;

import java.util.List;

public class ChatViewModel extends AndroidViewModel {

    MutableLiveData<Conversation> conversation;
    MutableLiveData<List<Message>> messages;
    ChatRepository chatRepository;

    public ChatViewModel(Application application){
        super(application);

        conversation = new MutableLiveData<>();
        chatRepository = new ChatRepository(application);
        messages = chatRepository.getMessages();
    }

    public void select(Conversation conversation) {
        chatRepository.setConversation(conversation);
        this.conversation = chatRepository.getConversation();
        chatRepository.fetchMessages();
    }

    public MutableLiveData<Conversation> getSelected() {
        return conversation;
    }

    public void setMessages(List<Message> messages) {
        this.messages.setValue(messages);
    }

    public MutableLiveData<List<Message>> getMessages() {
        return messages;
    }
}