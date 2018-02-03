package com.mp.playcards;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mp.playcards.Interface.ItemClickListener;
import com.mp.playcards.Model.Deck;
import com.mp.playcards.ViewHolder.DeckViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DeckList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference deckList;

    String categoryId="";

    FirebaseRecyclerAdapter<Deck,DeckViewHolder> adapter;

    //searchbar

    FirebaseRecyclerAdapter<Deck,DeckViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_list);

        //Firebase init
        database = FirebaseDatabase.getInstance();
        deckList = database.getReference("Decks");

        recyclerView =(RecyclerView)findViewById(R.id.recycler_decks);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //intent from home menu categories
        if(getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");
        if(!categoryId.isEmpty() && categoryId !=null)
        {
            loadListDeck(categoryId);
        }

        //search function
        materialSearchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        materialSearchBar.setHint("Search here...");
        loadSuggest(); //loading suggestions from firebase
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //change after user input
                List<String> suggest = new ArrayList<String>();
                for (String search:suggestList)
                {
                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //restore suggestions after closing searchbar
                if(!enabled)
                    recyclerView.setAdapter(adapter);

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //after finish show result
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

    }

    private void startSearch(CharSequence text) {
        searchAdapter = new FirebaseRecyclerAdapter<Deck, DeckViewHolder>(
                Deck.class,
                R.layout.deck_item,
                DeckViewHolder.class,
                deckList.orderByChild("Name").equalTo(text.toString())
                ) {
            @Override
            protected void populateViewHolder(DeckViewHolder viewHolder, Deck model, int position) {
                viewHolder.deck_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.deck_image);

                final Deck local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Start new activity
                        Intent deckDetail = new Intent(DeckList.this,DeckDetail.class);
                        deckDetail.putExtra("DeckId",searchAdapter.getRef(position).getKey());//sending deckid to the new activity
                        startActivity(deckDetail);

                    }
                });
            }
        };
        recyclerView.setAdapter(searchAdapter);//seting adapter for recycler vie
    }


    private void loadSuggest() {

        deckList.orderByChild("menuId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                        {
                            Deck item = postSnapshot.getValue(Deck.class);
                            suggestList.add(item.getName());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }




    private void loadListDeck(String categoryId)
    {
        adapter = new FirebaseRecyclerAdapter<Deck, DeckViewHolder>(Deck.class,R.layout.deck_item,DeckViewHolder.class,deckList.orderByChild("menuId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(DeckViewHolder viewHolder, Deck model, int position) {
                viewHolder.deck_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.deck_image);

                final Deck local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Start new activity
                        Intent deckDetail = new Intent(DeckList.this,DeckDetail.class);
                        deckDetail.putExtra("DeckId",adapter.getRef(position).getKey());//sending deckid to the new activity
                        startActivity(deckDetail);

                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
    }
}
