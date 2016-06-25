package com.example.user.estacionado;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Timer;
import java.util.TimerTask;

public class botonesFragment extends Fragment {
    private View v;
    private OnFragmentInteractionListener mListener;
    private Timer timer;

    public botonesFragment() {
        // Required empty public constructor
    }


    public static botonesFragment newInstance(String param1, String param2) {
        botonesFragment fragment = new botonesFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    public void mostrarPosicionFragment(){
        if (mListener != null) {
            mListener.mostrarPosicion();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_blank, container, false);


        //funcion del timer
        timer = new Timer();
        TimerTask t1 = new TimerTask() {

            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        //update ui
                        mostrarPosicionFragment();

                    }
                });
            }
        };
        timer.schedule(t1,0,5000);

        v.findViewById(R.id.nuevaPos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NuevaPosicionFragment();
            }
        });

        return v;

    }

    public void NuevaPosicionFragment(){
        if (mListener != null) {
            mListener.NuevaPosicion();
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



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void mostrarPosicion();
        void NuevaPosicion();
    }

    @Override
    public void onPause(){
        //debemos detener el timer
        super.onPause();
        timer.cancel();

    }
}
