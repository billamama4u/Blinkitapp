package com.tarun.blinkit

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tarun.blinkit.databinding.AddresslayoutuserBinding
import com.tarun.blinkit.databinding.FragmentPeofileBinding
import com.tarun.blinkit.view_models.AuthViewModels
import com.tarun.blinkit.view_models.Productviewmodel


class PeofileFragment : Fragment() {

    lateinit var binding:FragmentPeofileBinding
    private val viewmodel: Productviewmodel by viewModels()
    private val authViewModels: AuthViewModels by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentPeofileBinding.inflate(layoutInflater)
        onBAckButtonPressed()
        onOrderpressed()
        onAddressbuttonClicked()
        onLogOutClicked()
        return binding.root
    }

    private fun onLogOutClicked() {
        binding.llogout.setOnClickListener {
            val dialog=AlertDialog.Builder(requireContext())
            val alertDialog=dialog.create()
            dialog.setTitle("Log Out")
                .setMessage("Are you sure you want to log out").setPositiveButton("Yes",
                    DialogInterface.OnClickListener { dialog, which ->
                        startActivity(Intent(requireContext(),AuthMainActivity::class.java))
                        requireActivity().finish()
                        authViewModels.logOutUser()
                    }).setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                        alertDialog.dismiss()
                }).show().setCancelable(false)
        }
    }

    private fun onAddressbuttonClicked() {
        binding.lladdress.setOnClickListener {
            val addressbook=AddresslayoutuserBinding.inflate(LayoutInflater.from(requireContext()))
            viewmodel.getaddress {
                addressbook.editaddress.setText(it.toString())
            }
            val alertDialog=AlertDialog.Builder(requireContext()).setView(addressbook.root).show()
            alertDialog.show()
            addressbook.editbtn.setOnClickListener {
                addressbook.editaddress.isEnabled=true
            }
            addressbook.savebtn.setOnClickListener {
                viewmodel.saveUserAddressNew(addressbook.editaddress.text.toString())
                alertDialog.dismiss()
                Utils.showToast(requireContext(),"Address Updated....")
            }
        }
    }

    private fun onOrderpressed() {
        binding.llorders.setOnClickListener{
            findNavController().navigate(R.id.action_peofileFragment_to_orderFragment)
        }
    }

    private fun onBAckButtonPressed() {
        binding.tbProfileFrogment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_peofileFragment_to_homepage)
        }
    }


}