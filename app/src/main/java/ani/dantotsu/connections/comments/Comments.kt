package ani.dantotsu.connections.comments

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ani.dantotsu.R
import ani.dantotsu.currContext
import ani.dantotsu.databinding.ActivityComBinding
import ani.dantotsu.initActivity
import ani.dantotsu.themes.ThemeManager

class FAQActivity : AppCompatActivity() {
    private lateinit var binding: ActivityComBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ThemeManager(this).applyTheme()
        binding = ActivityComBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initActivity(this)

        binding.devsTitle3.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.1devsRecyclerView.adapter = FAQAdapter(faqs, supportFragmentManager)
        binding.1devsRecyclerView.layoutManager = LinearLayoutManager(this)
    }
}
