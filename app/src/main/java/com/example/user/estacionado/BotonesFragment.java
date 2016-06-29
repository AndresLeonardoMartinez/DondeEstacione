package com.example.user.estacionado;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.android.multidex.estacionado.R;
import java.util.Timer;
import java.util.TimerTask;


public class BotonesFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private Timer timer;

    public BotonesFragment() {}

    public static BotonesFragment newInstance() {
        return new BotonesFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_nuevo_estacionamiento, container, false);
        timer = new Timer();
        TimerTask t1 = new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable(){
                    public void run(){
                       mostrarPosicionFragment();
                    }
                });
            }
        };
        timer.schedule(t1,0,6000);
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
            throw new RuntimeException(context.toString()+ " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void mostrarPosicion();
        void NuevaPosicion();
    }

    @Override
    public void onPause(){
        super.onPause();
        timer.cancel();
    }
}
