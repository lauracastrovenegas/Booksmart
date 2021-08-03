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
import android.widget.TextView;

import com.example.booksmart.R;
import com.example.booksmart.adapters.ChatPreviewAdapter;
import com.example.booksmart.helpers.ItemClickSupport;
import com.example.booksmart.helpers.ParseMessageClient;
import com.example.booksmart.models.Conversation;
import com.example.booksmart.models.Item;
import com.example.booksmart.models.Message;
import com.example.booksmart.ui.MainActivity;
import com.example.booksmart.ui.listings.ListingsFragment;
import com.example.booksmart.viewmodels.ChatViewModel;
import com.example.booksmart.viewmodels.ConversationsViewModel;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ConversationsFragment extends Fragment {

    ConversationsViewModel conversationsViewModel;
    ChatViewModel chatViewModel;
    RecyclerView rvConversations;
    ChatPreviewAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    TextView tvNoMessages;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversations, container, false);

        rvConversations = view.findViewById(R.id.rvConversations);
        tvNoMessages = view.findViewById(R.id.tvNoMessagesText);

        linearLayoutManager = new LinearLayoutManager(getContext());
        rvConversations.setLayoutManager(linearLayoutManager);

        adapter = new ChatPreviewAdapter(getContext(), new ArrayList<>());
        rvConversations.setAdapter(adapter);

        setViewModels();
        conversationsViewModel.setMessageLiveQuery();
        conversationsViewModel.setConversationLiveQuery();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Click listener for items in the recycler view
        ItemClickSupport.addTo(rvConversations).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Conversation conversation = conversationsViewModel.getConversation(position);

                        ParseMessageClient parseMessageClient = new ParseMessageClient(getContext()){
                            @Override
                            protected void onMessageFetched(Message message) {
                                if (message != null){
                                    if (conversation.isUnread() && !message.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
                                        conversation.setUnread(false);
                                        conversation.saveInBackground();
                                    }
                                }
                            }
                        };

                        parseMessageClient.getLastMessage(conversation);
                        chatViewModel.select(conversation);
                        goChatView();
                    }
                }
        );
    }

    private void setViewModels() {
        chatViewModel = new ViewModelProvider((requireActivity())).get(ChatViewModel.class);
        conversationsViewModel = new ViewModelProvider(requireActivity()).get(ConversationsViewModel.class);

        conversationsViewModel.getConversations().observe(getViewLifecycleOwner(), new Observer<List<Conversation>>() {
            @Override
            public void onChanged(List<Conversation> conversations) {
                adapter.clear();
                adapter.addAll(conversations);

                if (conversations.isEmpty()){
                    tvNoMessages.setVisibility(View.VISIBLE);
                } else {
                    tvNoMessages.setVisibility(View.INVISIBLE);
                }

                allRead(conversations);
            }
        });

        conversationsViewModel.getNotification().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                ((MainActivity) getActivity()).setNotification(aBoolean);
            }
        });
    }

    private void allRead(List<Conversation> conversations) {
        ParseMessageClient parseMessageClient = new ParseMessageClient(getContext()){
            @Override
            protected void onMessageFetched(Message message) {
                if (!message.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
                    ((MainActivity) getActivity()).setNotification(true);
                } else {
                    ((MainActivity) getActivity()).setNotification(false);
                }
            }
        };

        for (int i = 0; i < conversations.size(); i++){
            if (conversations.get(i).isUnread()){
                parseMessageClient.getLastMessage(conversations.get(i));
            }
        }
    }

    private void goChatView() {
        Fragment fragment = new ChatFragment();
        replaceFragment(fragment);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out_left);
        transaction.replace(R.id.nav_host_fragment_activity_main, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}