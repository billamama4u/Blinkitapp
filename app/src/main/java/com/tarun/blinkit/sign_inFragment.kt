package com.tarun.blinkit

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.tarun.blinkit.databinding.FragmentSignInBinding


class sign_inFragment : Fragment() {

    private lateinit var signInBinding:FragmentSignInBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        signInBinding= FragmentSignInBinding.inflate(layoutInflater)
        statusbarcolour()

        userNumberChange()

        onContinueClicked()

        return signInBinding.root
    }

    private fun onContinueClicked() {
        signInBinding.continuebtn.setOnClickListener{
            val number=signInBinding.usernumber.text.toString()

            if(number.isEmpty() || number.length<10){
                Utils.showToast(requireContext(),"Enter a valid phone number")
            }else{
                val bundle= Bundle()
                bundle.putString("number",number)
                findNavController().navigate(R.id.action_sign_inFragment_to_otpFragment,bundle)
            }
        }
    }

    private fun userNumberChange() {
        signInBinding.continuebtn.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val number=s?.length
                println("hellow")
                if(number==10){
                    signInBinding.continuebtn.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.green))
                    Log.d("tarunerror","ok")
                }else{
                    signInBinding.continuebtn.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.greyblue))
                    Log.d("tarunerrror","ok")
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun statusbarcolour(){
        activity?.window?.apply {
            val statusbarcolour = ContextCompat.getColor(requireContext(),R.color.yellow)
            statusBarColor= statusbarcolour
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}