package com.nubesolucionesdigitales.casitacontrolremoto;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public final class Master {
    static int estadoCocina;

    public static boolean obtenerEstadoCocina(final Context context, DatabaseReference databaseReference){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                estadoCocina=dataSnapshot.getValue(Integer.class);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (estadoCocina==0){
            return false;
        }else {
            return true;
        }
    }

}
