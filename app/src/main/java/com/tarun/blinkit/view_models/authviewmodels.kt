package com.tarun.blinkit.view_models

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.tarun.blinkit.Models.Users
import com.tarun.blinkit.Utils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.TimeUnit

class AuthViewModels:ViewModel() {

    private val verifID= MutableStateFlow<String?>(null)
    private val _otpsent= MutableStateFlow(false)
    val otpsent=_otpsent
    private val _verifsuccess= MutableStateFlow(false)
    val verfisuccess=_verifsuccess
    private val _isUserSignedIn = MutableStateFlow(false)
    val isUserSignedIn: StateFlow<Boolean> get() = _isUserSignedIn


    init {
        checkUserStatus()
    }


    fun checkUserStatus() {
        val user = FirebaseAuth.getInstance().currentUser
        _isUserSignedIn.value = user != null
    }

    fun logOutUser() {
        FirebaseAuth.getInstance().signOut()
        _isUserSignedIn.value = false
    }




    fun sendOtp(userNumber: String, activity: Activity){
        val callbacks= object: PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {

            }

            override fun onVerificationFailed(p0: FirebaseException) {

            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                verifID.value=p0
                _otpsent.value=true
                super.onCodeSent(p0, p1)
            }

        }
        val options = PhoneAuthOptions.newBuilder(Utils.getauthinstance())
            .setPhoneNumber("+91$userNumber") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun signInWithPhoneAuthCredential(otp:String) {
        val credential = PhoneAuthProvider.getCredential(verifID.value.toString(), otp)
        Utils.getauthinstance().signInWithCredential(credential)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {

                    _verifsuccess.value=true
                } else {
                }
            }
    }



    }