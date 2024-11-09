package id.littlequery.ujian

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle
import id.littlequery.ujian.databinding.FragmentMapBinding
import org.json.JSONObject
import java.io.InputStream
import java.time.Year

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var googleMap: GoogleMap

    private var stateName: String? = null
    private var population: Int = 0
    private var tahun: String? = null

    // Daftar koordinat untuk semua negara bagian
    private val stateCoordinates = mapOf(
        "Alabama" to LatLng(32.806671, -86.791130),
        "Alaska" to LatLng(61.370716, -152.404419),
        "Arizona" to LatLng(33.729759, -111.431221),
        "Arkansas" to LatLng(34.969704, -92.373123),
        "California" to LatLng(36.778259, -119.417931),
        "Colorado" to LatLng(39.550051, -105.782067),
        "Connecticut" to LatLng(41.603221, -73.087749),
        "Delaware" to LatLng(38.910832, -75.527670),
        "Florida" to LatLng(27.766279, -81.686783),
        "Georgia" to LatLng(33.040619, -83.643074),
        "Hawaii" to LatLng(21.094318, -157.498337),
        "Idaho" to LatLng(44.299782, -114.142459),
        "Illinois" to LatLng(40.633125, -89.398528),
        "Indiana" to LatLng(39.717600, -86.181670),
        "Iowa" to LatLng(41.597782, -93.597701),
        "Kansas" to LatLng(38.526600, -96.726486),
        "Kentucky" to LatLng(37.668140, -84.670067),
        "Louisiana" to LatLng(31.169546, -91.867805),
        "Maine" to LatLng(44.693947, -69.381927),
        "Maryland" to LatLng(39.063946, -76.802101),
        "Massachusetts" to LatLng(42.230171, -71.530106),
        "Michigan" to LatLng(42.165726, -84.620213),
        "Minnesota" to LatLng(46.299925, -94.419704),
        "Mississippi" to LatLng(32.741646, -89.678696),
        "Missouri" to LatLng(36.116203, -89.719254),
        "Montana" to LatLng(46.870698, -110.454353),
        "Nebraska" to LatLng(41.125370, -98.268082),
        "Nevada" to LatLng(38.313515, -117.055374),
        "New Hampshire" to LatLng(43.631492, -71.307419),
        "New Jersey" to LatLng(40.298904, -74.521011),
        "New Mexico" to LatLng(34.819394, -106.445356),
        "New York" to LatLng(40.712776, -74.005974),
        "North Carolina" to LatLng(35.630066, -79.806419),
        "North Dakota" to LatLng(47.528912, -99.784012),
        "Ohio" to LatLng(40.388783, -82.764915),
        "Oklahoma" to LatLng(35.565342, -96.928917),
        "Oregon" to LatLng(44.299782, -119.417932),
        "Pennsylvania" to LatLng(40.351030, -75.577919),
        "Rhode Island" to LatLng(41.680893, -71.511780),
        "South Carolina" to LatLng(33.819739, -80.906280),
        "South Dakota" to LatLng(43.933333, -98.396493),
        "Tennessee" to LatLng(35.747845, -86.692345),
        "Texas" to LatLng(31.968599, -99.901810),
        "Utah" to LatLng(40.150032, -111.862434),
        "Vermont" to LatLng(44.045876, -72.710686),
        "Virginia" to LatLng(37.769337, -78.169968),
        "Washington" to LatLng(47.400902, -121.490494),
        "West Virginia" to LatLng(38.491226, -80.954522),
        "Wisconsin" to LatLng(43.784440, -88.787868),
        "Wyoming" to LatLng(42.755966, -107.302490)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stateName = arguments?.getString("STATE_NAME")
        population = arguments?.getInt("POPULATION") ?:0
        tahun = arguments?.getString("TAHUN")

        // Set details in the CardView
        binding.tvStateName.text = stateName
        binding.tvPopulation.text = getString(R.string.population_format, population)
        binding.tvToolbarTitle.text = stateName
        binding.tvTahun.text = tahun

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Cek apakah koordinat untuk negara bagian tersedia
        stateCoordinates[stateName]?.let { stateLocation ->
            // fokus ke lokasi negara bagian
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(stateLocation, 6f))

            // Tambahkan marker untuk negara bagian tersebut
            googleMap.addMarker(MarkerOptions().position(stateLocation).title(stateName))
        } ?: run {
            // Jika tidak ditemukan, tampilkan lokasi default (pusat USA)
            val defaultLocation = LatLng(39.8283, -98.5795) // Pusat USA
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 4f))
        }

        // Load GeoJSON boundaries dan tambahkan overlay untuk negara bagian yang dipilih
        loadStateBoundaryOverlay()
    }

    private fun loadStateBoundaryOverlay() {
        try {
            // Muat file GeoJSON dari resources yang terletak pada res folder
            val geoJsonInputStream: InputStream = resources.openRawResource(R.raw.us_states)
            val geoJsonString = geoJsonInputStream.bufferedReader().use { it.readText() }
            val geoJsonObject = JSONObject(geoJsonString)

            // Buat GeoJsonLayer dari isi file
            val layer = GeoJsonLayer(googleMap, geoJsonObject)

            // Filter dan tambahkan overlay untuk negara bagian yang dipilih
            layer.features.forEach { feature ->
                if (feature.getProperty("name") == stateName) {
                    // Terapkan GeoJsonPolygonStyle pada fitur
                    feature.setPolygonStyle(
                        GeoJsonPolygonStyle().apply {
                            fillColor = 0x7FFF0000  // merah
                            strokeColor = 0xFFFF0000.toInt() // garis merah
                            strokeWidth = 2f
                        }
                    )
                }
            }

            // Tambahkan layer ke peta
            layer.addLayerToMap()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(stateName: String,population : Int,tahun : String) = MapFragment().apply {
            arguments = Bundle().apply {
                putString("STATE_NAME", stateName)
                putInt("POPULATION", population)
                putString("TAHUN","Tahun : "+tahun)
            }
        }
    }
}