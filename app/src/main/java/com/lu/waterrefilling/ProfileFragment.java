package com.lu.waterrefilling;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        // Inflate the layout for this fragment

        Button logoutBtn = view.findViewById(R.id.btn_logout);
        logoutBtn.setOnClickListener(v->{
            logoutUser();
        });

        return view;
    }

    private void logoutUser() {
        // Sign out from Firebase
        auth.signOut();

        // Clear shared preferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Redirect to MainActivity
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        startActivity(intent);

        // Finish the current activity
        requireActivity().finish();
    }
}
