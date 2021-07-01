package com.botrick.firebaseapp.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.botrick.firebaseapp.R;
import com.botrick.firebaseapp.adapter.UserAdapter;
import com.botrick.firebaseapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    private RecyclerView recyclerContatos;
    private UserAdapter userAdapter;
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ArrayList<User> listaContatos = new ArrayList<>();
    private User userLogged;
    private DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("requests");

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_main, container, false);

        userLogged = new User(auth.getCurrentUser().getUid(), auth.getCurrentUser().getEmail(), auth.getCurrentUser().getDisplayName());

        recyclerContatos = layout.findViewById(R.id.frag_main_recycler_user);

        userAdapter = new UserAdapter(getContext(), listaContatos);

        userAdapter.setListener(new UserAdapter.ClickAdapterUser() {
            @Override
            public void adicionarContato(int position) {
                User u = listaContatos.get(position);

                //request send
                requestRef.child(userLogged.getId()).child("send").child(u.getId()).setValue(u);

                //request receive
                requestRef.child(u.getId()).child("receive").child(userLogged.getId()).setValue(userLogged);

                //atualiza o usuario selecionado
                listaContatos.get(position).setReceiveRequest(true);

                userAdapter.notifyDataSetChanged();
            }

        });


        recyclerContatos.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerContatos.setAdapter(userAdapter);

        /*TextView textView = layout.findViewById(R.id.frag_main_text);
        textView.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Click", Toast.LENGTH_SHORT).show();
        });*/

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        getUsersDatabase();
    }

    public void getUsersDatabase(){
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaContatos.clear();

                for(DataSnapshot filho : snapshot.getChildren()){
                    User u = filho.getValue(User.class);

                    //Comparar com o usuario logado
                    if(!userLogged.equals(u)){

                        /*if (cont%2==0){
                            u.setReceiveRequest(true);
                        }

                        else {
                            u.setReceiveRequest(false);
                        }*/

                        listaContatos.add(u);
                    }

                    //listaContatos.add(u);
                }

                //verificar quais contatos já foram solicitados
                requestRef.child(userLogged.getId()).child("send").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot no_filho : snapshot.getChildren()){
                            User usuarioSolicitado = no_filho.getValue(User.class);

                            for(int i = 0; i < listaContatos.size(); i++){
                                if(listaContatos.get(i).equals(usuarioSolicitado)){
                                    listaContatos.get(i).setReceiveRequest(true);
                                }

                            }

                        }

                        userAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });

                //userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

}
