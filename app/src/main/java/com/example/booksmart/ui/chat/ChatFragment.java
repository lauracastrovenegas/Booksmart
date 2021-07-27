package com.example.booksmart.ui.chat;

import androidx.fragment.app.FragmentTransaction;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.booksmart.R;
import com.example.booksmart.adapters.ChatAdapter;
import com.example.booksmart.models.Message;
import com.example.booksmart.viewmodels.ChatViewModel;
import com.parse.ParseUser;

import java.util.List;

public class ChatFragment extends Fragment {

    ChatViewModel chatViewModel;
    RecyclerView rvMessages;
    ChatAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    EditText etInput;
    ImageButton ibSend;
    ImageView ivBack;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_chat, container, false);

        etInput = view.findViewById(R.id.etMessage);
        ibSend = view.findViewById(R.id.ibSend);
        ivBack = view.findViewById(R.id.ibChatBack);
        rvMessages = view.findViewById(R.id.rvMessages);

        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        rvMessages.setLayoutManager(linearLayoutManager);

        setViewModel();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFragment(new ConversationsFragment());
            }
        });

        return view;
    }

    private void setViewModel() {
        chatViewModel = new ViewModelProvider((requireActivity())).get(ChatViewModel.class);

        chatViewModel.getSelected().observe(getViewLifecycleOwner(), conversation -> {
            chatViewModel.setMessages(conversation.getMessages());
        });

        chatViewModel.getMessages().observe(getViewLifecycleOwner(), new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                adapter = new ChatAdapter(getContext(), ParseUser.getCurrentUser(), messages);
                rvMessages.setAdapter(adapter);
            }
        });
    }

    private void goToFragment(Fragment fragment){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out);
        transaction.replace(R.id.nav_host_fragment_activity_main, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}