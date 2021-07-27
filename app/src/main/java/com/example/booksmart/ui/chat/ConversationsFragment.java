package com.example.booksmart.ui.chat;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.booksmart.R;
import com.example.booksmart.adapters.ChatPreviewAdapter;
import com.example.booksmart.models.Conversation;
import com.example.booksmart.viewmodels.ChatViewModel;
import com.example.booksmart.viewmodels.ConversationsViewModel;

import java.util.List;

public class ConversationsFragment extends Fragment {

    ConversationsViewModel conversationsViewModel;
    ChatViewModel chatViewModel;
    RecyclerView rvConversations;
    ChatPreviewAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    public static ConversationsFragment newInstance() {
        return new ConversationsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversations, container, false);

        rvConversations = view.findViewById(R.id.rvConversations);

        linearLayoutManager = new LinearLayoutManager(getContext());
        rvConversations.setLayoutManager(linearLayoutManager);

        setViewModels();

        return view;
    }

    private void setViewModels() {
        chatViewModel = new ViewModelProvider((requireActivity())).get(ChatViewModel.class);
        conversationsViewModel = new ViewModelProvider(requireActivity()).get(ConversationsViewModel.class);

        conversationsViewModel.getConversations().observe(getViewLifecycleOwner(), new Observer<List<Conversation>>() {
            @Override
            public void onChanged(List<Conversation> conversations) {
                adapter = new ChatPreviewAdapter(getContext(), conversations);
                rvConversations.setAdapter(adapter);
            }
        });
    }

}