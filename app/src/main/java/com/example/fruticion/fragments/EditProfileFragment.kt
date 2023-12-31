package com.example.fruticion.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.fruticion.R
import com.example.fruticion.activity.LoginActivity
import com.example.fruticion.database.FruticionDatabase
import com.example.fruticion.databinding.FragmentEditProfileBinding
import com.example.fruticion.model.User
import kotlinx.coroutines.launch

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var db: FruticionDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = FragmentEditProfileBinding.inflate(layoutInflater)


        // Se obtiene la instancia de la base de datos. La sintaxis siempre debe ser asi.
        db = FruticionDatabase.getInstance(requireContext().applicationContext)!!

        setUpListeners()

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun setUpListeners() {
        with(binding) {

            buttonSaveChanges.setOnClickListener {

                //Hay que usar toString() porque .text (getText() por detras) devuelve de tipo CharSequence!
                var newName = newUsernameEditText.text.toString()
                var newPassword = newPasswordEditText.text.toString()

                //corrutina obligatoria para hacer operaciones con la BD o con la API (tareas pesadas)
                lifecycleScope.launch {
                    val userDB = db.userDao().getUserById(LoginActivity.currentUserId!!)
                    val userEdit =
                        db.userDao().getUserByUsername(newUsernameEditText.text.toString())

                    if(userDB == userEdit){
                         newName = ""
                    }

                    if (db.userDao().getUserByUsername(newName) == null) {

                        if (newName.isEmpty())
                            newName = db.userDao().getUserById(LoginActivity.currentUserId).username
                        if (newPassword.isEmpty())
                            newPassword =
                                db.userDao().getUserById(LoginActivity.currentUserId).password

                        db.userDao().updateUser(
                            User(
                                LoginActivity.currentUserId, //este es un companion object que viene cargado desde el login
                                newName,
                                newPassword
                            )
                        )
                        //Tras pulsar Guardar Cambios se vuelve a ProfileFragment
                        val action = EditProfileFragmentDirections.actionEditProfileFragmentToProfileFragment()
                        findNavController().navigate(action)
                        //Descomentar para añadir un leve retraso de 50 milisegundos y que de tiempo de actualizar el usuario antes de que se muestren sus datos en el profileFragment
                        //Handler().postDelayed({ findNavController().navigate(action)}, 50)
                    }
                    else
                        Toast.makeText(binding.root.context, R.string.username_chosen, Toast.LENGTH_SHORT).show()
                }
            }
            // Al pulsar el boton Cancelar, se vuelve al ProfileFragment
            buttonCancelChanges.setOnClickListener {
                val action =
                    EditProfileFragmentDirections.actionEditProfileFragmentToProfileFragment()
                findNavController().navigate(action)
            }
        }
    }

}