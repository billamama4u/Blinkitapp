package com.tarun.blinkit

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tarun.blinkit.view_models.AuthViewModels
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private val authViewModels: AuthViewModels by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_splash, container, false)
        statusbarcolour()
        Handler(Looper.getMainLooper()).postDelayed({
            lifecycleScope.launch {
                authViewModels.isUserSignedIn.collect { isSignedIn ->
                    if (isSignedIn) {
                        // User is signed in, navigate to UsersMainActivity
                        startActivity(Intent(requireActivity(), UsersMainActivity::class.java))
                        requireActivity().finish()
                    } else {
                        // User is not signed in, navigate to the sign-in fragment
                        findNavController().navigate(R.id.action_splashFragment_to_sign_inFragment)
                    }
                }
            }
        }, 3000L)
        return view
    }

    private fun statusbarcolour(){
        activity?.window?.apply {
            val statusbarcolour = ContextCompat.getColor(requireContext(),R.color.yellow)
            statusBarColor= statusbarcolour
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }



}