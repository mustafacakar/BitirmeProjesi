package com.example.mustafa.switchtab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatCallback;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Tab1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Tab1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Tab1 extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView notListesi;
    FirebaseAdapter firebaseAdapter;
    int duzenlenecekNot;
    NotGosterAdapter notGosterAdapter;

    /*String baslik[]={"Birinci","İkinci","Üçüncü"};
    String icerik[]={"Bir","İki","Üç"};*/

    ArrayList<String> baslik;
    ArrayList<String> icerik;

    private OnFragmentInteractionListener mListener;

    public Tab1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Tab1.
     */
    // TODO: Rename and change types and number of parameters
    public static Tab1 newInstance(String param1, String param2) {
        Tab1 fragment = new Tab1();
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
        //recreate = false;
        //getActivity().recreate();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        this.getActivity().getMenuInflater().inflate(R.menu.not_context_menu,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.context_menu_duzenle:
                //Toast.makeText(getActivity(),"Düzenle -> "+duzenlenecekNot,Toast.LENGTH_SHORT).show();
                Intent duzenle = new Intent(getActivity(),NotDuzenle.class);
                duzenle.putExtra("NotSira",duzenlenecekNot);
                startActivity(duzenle);
                return true;
            case R.id.context_menu_sil:
                //Toast.makeText(getActivity(),"Sil -> "+duzenlenecekNot,Toast.LENGTH_SHORT).show();
                firebaseAdapter.notuSil(FirstActivity.kullanici.notuBul(duzenlenecekNot));
                FirstActivity.kullanici.notlariAl().remove(duzenlenecekNot);
                notGosterAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab1, container, false);
        notGosterAdapter = new NotGosterAdapter(getActivity());
        firebaseAdapter = new FirebaseAdapter();
        notListesi = (ListView) view.findViewById(R.id.tab1_lvNotlar);
        if(FirstActivity.kullanici.getKullaniciAdi()!=null){
            firebaseAdapter.notlarıDownloadEt(this.getActivity(),notListesi,notGosterAdapter);
            registerForContextMenu(notListesi);
        }

        notListesi.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //duzenlenecekNot = (position-FirstActivity.kullanici.notSayisi())+1;
                duzenlenecekNot = position;
                return false;
            }
        });




        return view;
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

    @Override
    public void onStop() {
        super.onStop();

    }
}
