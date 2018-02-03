package com.mp.playcards;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mp.playcards.Database.Database;
import com.mp.playcards.Model.Deck;
import com.mp.playcards.Model.Order;
import com.squareup.picasso.Picasso;

public class DeckDetail extends AppCompatActivity {

    TextView deck_name,deck_price,deck_description;
    ImageView deck_image;

    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;
    ElegantNumberButton numberButton;

    String deckId="";

    FirebaseDatabase database;
    DatabaseReference decks;


    Deck currentDeck;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_detail);

        //firebase again
        database = FirebaseDatabase.getInstance();
        decks = database.getReference("Decks");

        //initiate view page

        numberButton = (ElegantNumberButton) findViewById(R.id.number_button);
        btnCart = (FloatingActionButton) findViewById(R.id.btnCart);

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Database(getBaseContext()).addToCart(new Order(
                        deckId,
                        currentDeck.getName(),
                        numberButton.getNumber(),
                        currentDeck.getPrice(),
                        currentDeck.getDiscount()

                ));
                Toast.makeText(DeckDetail.this, "Item has been added to cart!", Toast.LENGTH_SHORT).show();
            }
        });

        deck_description = (TextView) findViewById(R.id.deck_description);
        deck_name = (TextView) findViewById(R.id.deck_name);
        deck_price = (TextView) findViewById(R.id.deck_price);
        deck_image =(ImageView) findViewById(R.id.img_deck);

        collapsingToolbarLayout =(CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        //Getting the DeckId from the intent

        if(getIntent() != null)
            deckId = getIntent().getStringExtra("DeckId");
        if(!deckId.isEmpty())
        {
            getDetailDeck(deckId);
        }




    }

    private void getDetailDeck(String deckId) {

        decks.child(deckId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentDeck = dataSnapshot.getValue(Deck.class);

                //setting the image form the deckview
                Picasso.with(getBaseContext()).load(currentDeck.getImage())
                        .into(deck_image);

                collapsingToolbarLayout.setTitle(currentDeck.getName());

                deck_price.setText(currentDeck.getPrice());

                deck_name.setText(currentDeck.getName());

                deck_description.setText(currentDeck.getDescription());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
