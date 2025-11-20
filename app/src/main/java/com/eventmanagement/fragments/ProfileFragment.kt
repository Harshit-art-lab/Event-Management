package com.eventmanagement.fragments

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.eventmanagement.LoginActivity
import com.eventmanagement.R
import com.eventmanagement.data.MockDataProvider

class ProfileFragment : Fragment() {

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvUsn: TextView
    private lateinit var tvRewardPoints: TextView
    private lateinit var btnEditProfile: Button
    private lateinit var btnLogout: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        tvName = view.findViewById(R.id.tvName)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvUsn = view.findViewById(R.id.tvUsn)
        tvRewardPoints = view.findViewById(R.id.tvRewardPoints)
        btnEditProfile = view.findViewById(R.id.btnEditProfile)
        btnLogout = view.findViewById(R.id.btnLogout)

        val repository = com.eventmanagement.data.FirebaseRepository()

        // Load user data from Firebase
        lifecycleScope.launch {
            val userId = repository.getCurrentUser()?.uid ?: return@launch
            val result = repository.getUserData(userId)

            if (result.isSuccess) {
                val user = result.getOrNull()
                tvName.text = user?.name
                tvEmail.text = user?.email
                tvUsn.text = "USN: ${user?.usn}"
                tvRewardPoints.text = user?.rewardPoints.toString()
            }
        }

        btnEditProfile.setOnClickListener {
            Toast.makeText(context, "Edit profile coming soon", Toast.LENGTH_SHORT).show()
        }

        btnLogout.setOnClickListener {
            repository.logout()
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.finish()
        }
    }
}