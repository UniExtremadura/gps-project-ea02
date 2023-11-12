package com.example.fruticion.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fruticion.activity.HomeActivity
import com.example.fruticion.api.APIError
import com.example.fruticion.api.getNetworkService
import com.example.fruticion.database.FruticionDatabase
//import com.example.fruticion.dummy.dummyFruit
import com.example.fruticion.model.Fruit
import com.example.fruticion.databinding.FragmentSearchBinding
import kotlinx.coroutines.launch

/*
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
*/



class SearchFragment : Fragment()   {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    var homeActivity: HomeActivity? = null

    private lateinit var searchAdapter: SearchAdapter
    private var onFruitsLoadedListener: OnFruitsLoadedListener? = null

    private lateinit var db: FruticionDatabase
    interface OnFruitsLoadedListener {
        fun onFruitsLoaded(fruits: List<Fruit>)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)


        return binding.root
    }

    //Este metodo actua despues de que el Fragment ya se ha creado, para añadir cosas extra al Fragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Se invoca a la API para cargar el fragment con las frutas al principio
        lifecycleScope.launch{
            var fruits: List<Fruit> = fetchAllFruits()



            setUpRecyclerView(fruits)
        }
    }

    private suspend fun fetchAllFruits(): List<Fruit> {
        var fruitList = listOf<Fruit>()
        try {
           fruitList = getNetworkService().getAllFruits()
            onFruitsLoadedListener?.onFruitsLoaded(fruitList)
        } catch (cause: Throwable) {
            throw APIError("Unable to fetch data from API", cause)
        }
        return fruitList
    }
    private fun setUpRecyclerView(fruits: List<Fruit>) {
        val recyclerView = binding.rvFruitList // Linkea la RecyclerView del layout con ViewBinding en esta variable
        recyclerView.layoutManager = LinearLayoutManager(context) // Configura el layoutManager de la RecyclerView para que adopte una configuración vertical en lugar de en grid

        // Inicializa searchAdapter con la lista de frutas
        searchAdapter = SearchAdapter(fruits) { fruit -> onItemSelected(fruit) }

        // Asigna el adaptador a la RecyclerView
        recyclerView.adapter = searchAdapter
    }



    fun updateRecyclerView(newData: List<Fruit>) {
        val modifiedData = ArrayList(newData)
        searchAdapter.updateList(modifiedData)
        Log.d("SearchFragment", "updateRecyclerView se llamó con ${modifiedData.size} elementos")
    }


    //Este metodo obtiene la Activity a la que pertenece el Fragment e invoca al startActivity() para mandarle la fruta pinchada con una Intent.
    fun onItemSelected(fruit: Fruit) {
        (requireActivity() as OnShowClickListener).onShowClick(fruit)//En esta linea se esta recuperando la Activity a la que pertenece este Fragment para invocar al override de onShowClick() alli definido
        //requireActivity() obtiene la Activity de este Fragment. Hace un Casting de OnShowClickListener, por tanto, se "asume" que la HomeActivity debe implementar OnShowClickListener o si no, lanzara una excepcion
    }
    interface OnShowClickListener {
        fun onShowClick(fruit: Fruit)//Esta funcion es overrideada en HomeActivity para lanzar una Intent para viajar a la pantalla de detalle de la fruta pinchada
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFruitsLoadedListener) {
            onFruitsLoadedListener = context
        }
    }


}