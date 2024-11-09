package id.littlequery.ujian

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import id.littlequery.ujian.databinding.FragmentStateListBinding
import kotlinx.coroutines.launch

class StateListFragment : Fragment() {

    private var _binding: FragmentStateListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStateListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        fetchData()
    }

    private fun fetchData() {
        // Tampilkan ProgressBar sebelum memulai pengambilan data
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getStates()

                // Sembunyikan ProgressBar setelah data berhasil diambil
                binding.progressBar.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE

                binding.recyclerView.adapter = StateAdapter(response.data) { state ->
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, MapFragment.newInstance(state.State, state.Population,state.Year))
                        .addToBackStack(null)
                        .commit()
                }
            } catch (e: Exception) {
                e.printStackTrace()
         binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
