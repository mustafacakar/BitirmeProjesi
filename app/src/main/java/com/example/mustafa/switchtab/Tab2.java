package com.example.mustafa.switchtab;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Tab2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Tab2 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //*********** Değişkenler **************************

    KullaniciClass insan;

    /*ArrayList<String> fullNameList;
    ArrayList<String> kullaniciAdiList;*/
    ArrayList<KullaniciClass> insanListesi;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    RecyclerView recyclerView;
    public static EditText searchBar;
    FirebaseDatabase database;
    ListView KisilerList;
    FirebaseAdapter firebaseAdapter;
    ArkadasGosterAdapter arkadasGosterAdapter;
    int secilenArkadas;

    searchAdapter searchAdapterM;
    //**************************************************


    private OnFragmentInteractionListener mListener;

    public Tab2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Tab2.
     */
    // TODO: Rename and change types and number of parameters
    public static Tab2 newInstance(String param1, String param2) {
        Tab2 fragment = new Tab2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        arkadasGosterAdapter = new ArkadasGosterAdapter(getActivity());
        firebaseAdapter = new FirebaseAdapter();
        database = FirebaseDatabase.getInstance();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.arkadas_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.context_arkadasa_not:
                //Toast.makeText(getActivity(),"Düzenle -> "+duzenlenecekNot,Toast.LENGTH_SHORT).show();
                //Intent duzenle = new Intent(getActivity(),NotDuzenle.class);
                //duzenle.putExtra("NotSira",duzenlenecekNot);
                //startActivity(duzenle);
                return true;
            case R.id.context_arkadas_sil:
                //Toast.makeText(getActivity(),"Sil -> "+secilenArkadas,Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(),FirstActivity.kullanici.arkadasiBul(secilenArkadas).getKullaniciAdi()+" Arkadaşlıktan Çıkarıldı!",Toast.LENGTH_SHORT).show();
                firebaseAdapter.arkadasSil(FirstActivity.kullanici.arkadasiBul(secilenArkadas));
                FirstActivity.kullanici.arkadasiSil(secilenArkadas);
                Tab3.arkadasSayisi.setText(FirstActivity.kullanici.arkadasSayisi()+"");
                arkadasGosterAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab2, container, false);
        insanListesi = new ArrayList<KullaniciClass>();
        KisilerList =  (ListView) view.findViewById(R.id.kisiler_lvArkadasList);
        recyclerView  = (RecyclerView) view.findViewById(R.id.recyclerView);
        //final ListView arkadasListesi = (ListView) view.findViewById(R.id.kisiler_lvArkadasList);
        if(FirstActivity.kullanici.getKullaniciAdi()!=null){
            //arkadasOlustur();
            firebaseAdapter.arkadaslariCek(getActivity(),KisilerList, arkadasGosterAdapter);
            registerForContextMenu(KisilerList);
        }


        KisilerList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                secilenArkadas = position;
                return false;
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),LinearLayoutManager.VERTICAL));

        KisilerList.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);

         searchBar = (EditText) view.findViewById(R.id.search_edit_text);
        // Set drawables for left, top, right, and bottom - send 0 for nothing
        searchBar.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.search_icon, 0);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==0){
                    arkadasGosterAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                /*fullNameList=new ArrayList<String>();
                kullaniciAdiList=new ArrayList<String>();*/
                Log.i("LogX","girdi");
                recyclerView.setVisibility(View.VISIBLE);
                KisilerList.setVisibility(View.INVISIBLE);
                if(!s.toString().isEmpty()){
                    setAdapter(s.toString());

                }else{
                  recyclerView.setVisibility(View.INVISIBLE);
                    KisilerList.setVisibility(View.VISIBLE);


                }

            }
        });


        return view;
    }

    private void setAdapter(final String searchedString){

        databaseReference=database.getReference("KeepNoteApp");
        databaseReference.child("Kullanicilar").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Her aramadan önce arrayListleri sıfırlıyoruz.
                /*fullNameList.clear();
                kullaniciAdiList.clear();*/
                //profileFotoList.clear();
                insanListesi.clear();

                if(searchedString.toString().isEmpty()){

                    /*fullNameList.clear();
                    kullaniciAdiList.clear();*/
                    insanListesi.clear();
                    recyclerView.removeAllViews();

                }



                int counter = 0;


                for(DataSnapshot snapshot :dataSnapshot.getChildren()){

                    /*String uid =snapshot.getKey();
                    String full_name = snapshot.child("Isim").getValue(String.class);
                    String kullaniciAdi = snapshot.child("KullaniciAdi").getValue(String.class);
                    String fotograf = snapshot.child("Fotograf").getValue(String.class);*/

                    if(!FirstActivity.kullanici.arkadasMi(snapshot.child("KullaniciAdi").getValue(String.class)) && !FirstActivity.kullanici.getKullaniciAdi().equals(snapshot.child("KullaniciAdi").getValue(String.class))){
                        insan = new KullaniciClass();
                        insan.setAdSoyad(snapshot.child("Isim").getValue(String.class));
                        insan.setKullaniciAdi(snapshot.child("KullaniciAdi").getValue(String.class));
                        insan.setProfilFotografi(snapshot.child("Fotograf").getValue(String.class));

                        if(insan.getAdSoyad().toLowerCase().contains(searchedString.toLowerCase())){

                            insanListesi.add(insan);
                        /*fullNameList.add(full_name);
                        kullaniciAdiList.add(kullaniciAdi);*/
                            counter++;

                        }else if (insan.getKullaniciAdi().toLowerCase().contains(searchedString.toLowerCase())){


                        /*fullNameList.add(full_name);
                        kullaniciAdiList.add(kullaniciAdi);*/
                            insanListesi.add(insan);
                            counter++;
                        }

                        if(counter == 15 )
                            break;
                    }




                }

                searchAdapterM = new searchAdapter(getActivity(),insanListesi);
                recyclerView.setAdapter(searchAdapterM);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
