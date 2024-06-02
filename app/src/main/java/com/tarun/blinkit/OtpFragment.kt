package com.tarun.blinkit

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.tarun.blinkit.Models.Users
import com.tarun.blinkit.databinding.FragmentOtpBinding
import com.tarun.blinkit.view_models.AuthViewModels
import kotlinx.coroutines.launch


class OtpFragment : Fragment() {

    private val authViewModels:AuthViewModels by viewModels()
    lateinit var binding: FragmentOtpBinding
    lateinit var users: Users
    lateinit var number: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentOtpBinding.inflate(layoutInflater)
        statusbarcolour()
        getusernumber()
        enterOTPCustomization()
        onBackButtonPressed()
        sendOtp()
        verifyotp()
        return binding.root
    }

    private fun verifyotp() {
        binding.loginbtn.setOnClickListener {

            val editTexts= arrayOf(binding.etOtp1,binding.etOtp2,binding.etOtp3,binding.etOtp4,binding.etOtp5,binding.etOtp6)

            val otp=editTexts.joinToString (""){ it.text.toString() }

            if(otp.length<6){
                Utils.showToast(requireContext(),"Enter valid OTP")
            }
            else{
                editTexts.forEach { it.text?.clear();it.clearFocus() }
                Utils.showDialogue(requireContext(),"Sign-in you")
                verifying(otp)
            }

        }
    }

    private fun verifying(otp: String) {

        authViewModels.signInWithPhoneAuthCredential(otp)
        lifecycleScope.launch {
            authViewModels.verfisuccess.collect {
                if(it){
                    users= Users(uid = Utils.getuid(), userNumber = number,userAddress = "",token="")
                    FirebaseMessaging.getInstance().token.addOnCompleteListener {
                        val token=it.result
                        users.token=token
                        FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(users.uid!!).setValue(users)
                        Utils.hideDialogue()
                        Utils.showToast(requireContext(),"Sign-in successful")
                        startActivity(Intent(requireActivity(),UsersMainActivity::class.java))
                        requireActivity().finish()
                    }

                }
            }
        }

    }


    private fun sendOtp() {

        Utils.showDialogue(requireContext(),"Sending OTP....")
        authViewModels.apply {
            authViewModels.sendOtp(number,requireActivity(),)
            lifecycleScope.launch {
                otpsent.collect{
                    if(it){
                        Utils.hideDialogue()
                        Utils.showToast(requireContext(),"OTP sent")
                    }
                }
            }
        }
    }

    private fun statusbarcolour(){
        activity?.window?.apply {
            val statusbarcolour = ContextCompat.getColor(requireContext(),R.color.white_yellow)
            statusBarColor= statusbarcolour
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    private fun getusernumber() {
        val bundel= arguments
        number=bundel?.getString("number").toString()

        binding.usernumbertv.setText(number)

    }

    private fun onBackButtonPressed() {
        binding.tbOtpfragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_otpFragment_to_sign_inFragment)
        }
    }

    private fun enterOTPCustomization() {
        val editTexts= arrayOf(binding.etOtp1,binding.etOtp2,binding.etOtp3,binding.etOtp4,binding.etOtp5,binding.etOtp6)

        for(i in editTexts.indices){
            editTexts[i].addTextChangedListener(object: TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    if(s?.length == 1){
                        if(i<editTexts.size-1){
                            editTexts[i+1].requestFocus()
                        }
                    }
                    else if(s?.length==0){
                        if(i>0){
                            editTexts[i-1].requestFocus()
                        }
                    }
                }
            })
        }
    }




}