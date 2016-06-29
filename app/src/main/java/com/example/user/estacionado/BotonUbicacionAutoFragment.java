package com.example.user.estacionado;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.android.multidex.estacionado.R;

public class BotonUbicacionAutoFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    public BotonUbicacionAutoFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_boton_ubicacion_auto, container, false);
        view.findViewById(R.id.botonGuardar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarPosicionFragment();
            }
        });
        return view;
    }

    public void guardarPosicionFragment(){
        if (mListener != null) {
            mListener.guardarPosicion();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }else{
            throw new RuntimeException(context.toString()+ " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener{
        void guardarPosicion();
    }
}