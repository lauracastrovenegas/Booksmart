package com.example.booksmart.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.example.booksmart.R;
import com.example.booksmart.helpers.ParseMessageClient;
import com.example.booksmart.models.Conversation;
import com.example.booksmart.models.Message;
import com.example.booksmart.viewmodels.ChatViewModel;
import com.example.booksmart.viewmodels.ConversationsViewModel;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    public static final int REMOVE_REQUEST = 202;
    BadgeDrawable badge;
    ParseMessageClient parseMessageClient;
    ConversationsViewModel conversationsViewModel;
    ChatViewModel chatViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.white));

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        navView.setItemIconTintList(null);
        NavigationUI.setupWithNavController(navView, navController);

        badge = navView.getOrCreateBadge(R.id.navigation_conversation);
        badge.setBackgroundColor(getResources().getColor(R.color.orange_500));
        badge.setVisible(false);

        // prevent toolbar from hiding when keyboard opens
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        conversationsViewModel = new ViewModelProvider(this).get(ConversationsViewModel.class);
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        setUpMessageClient();
    }

    private void setUpMessageClient() {
        parseMessageClient = new ParseMessageClient(getBaseContext()){
            @Override
            protected void onNewMessageFound(Message message) {
                Log.i("MainActivity", "onNewMessageFound");
                conversationsViewModel.refreshConversations();
                chatViewModel.refreshMessages();
            }

            @Override
            protected void onConversationsUpdated() {
                conversationsViewModel.refreshConversations();
            }

            @Override
            protected void onConversationsRemoved() {
                conversationsViewModel.refreshConversations();
            }
        };

        parseMessageClient.setMessageLiveQuery();
        parseMessageClient.setConversationLiveQuery();
    }

    public void setNotification(Boolean visible){
        badge.setVisible(visible);
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            for (Fragment childFragment : fragment.getChildFragmentManager().getFragments()){
                childFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

}