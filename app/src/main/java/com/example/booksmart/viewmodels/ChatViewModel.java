package com.example.booksmart.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.booksmart.models.Conversation;
import com.example.booksmart.models.Listing;
import com.example.booksmart.models.Message;

import java.util.List;

public class ChatViewModel extends AndroidViewModel {

    MutableLiveData<Conversation> conversation;
    MutableLiveData<List<Message>> messages;

    public ChatViewModel(Application application){
        super(application);

        conversation = new MutableLiveData<>();
        messages = new MutableLiveData<>();
    }

    public void select(Conversation conversation) {
        this.conversation.setValue(conversation);
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