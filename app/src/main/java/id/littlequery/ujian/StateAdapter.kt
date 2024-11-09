package id.littlequery.ujian

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.littlequery.ujian.databinding.StateItemBinding

class StateAdapter(
    private val states: List<State>,
    private val onItemClick: (State) -> Unit
) : RecyclerView.Adapter<StateAdapter.StateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StateViewHolder {
        val binding = StateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StateViewHolder, position: Int) {
        holder.bind(states[position])
    }

    override fun getItemCount(): Int = states.size

    inner class StateViewHolder(private val binding: StateItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(state: State) {
            // Set state name and population
            binding.stateName.text = state.State
            binding.statePopulation.text = "Population: ${state.Population}"

            // Load thumbnail image with Glide
            Glide.with(binding.stateIcon.context)
                .load("https://picsum.photos/seed/${state.State}/600/400")
                .placeholder(R.drawable.states)
                .error(R.drawable.states)
                .centerCrop()
                .into(binding.stateIcon)

            // Set click listener on card
            binding.root.setOnClickListener { onItemClick(state) }
        }
    }
}
