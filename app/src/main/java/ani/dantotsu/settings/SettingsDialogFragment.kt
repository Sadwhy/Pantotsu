package ani.dantotsu.settings

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ani.dantotsu.BottomSheetDialogFragment
import ani.dantotsu.MainActivity
import ani.dantotsu.R
import ani.dantotsu.connections.anilist.Anilist
import ani.dantotsu.databinding.BottomSheetSettingsBinding
import ani.dantotsu.download.anime.OfflineAnimeFragment
import ani.dantotsu.download.manga.OfflineMangaFragment
import ani.dantotsu.home.AnimeFragment
import ani.dantotsu.home.HomeFragment
import ani.dantotsu.home.LoginFragment
import ani.dantotsu.home.MangaFragment
import ani.dantotsu.home.NoInternet
import ani.dantotsu.incognitoNotification
import ani.dantotsu.loadImage
import ani.dantotsu.offline.OfflineFragment
import ani.dantotsu.openLinkInBrowser
import ani.dantotsu.others.imagesearch.ImageSearchActivity
import ani.dantotsu.setSafeOnClickListener
import ani.dantotsu.startMainActivity

class SettingsDialogFragment : BottomSheetDialogFragment() {
    private var _binding: BottomSheetSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var pageType: PageType
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageType = arguments?.getSerializable("pageType") as? PageType ?: PageType.HOME
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val window = dialog?.window
        window?.statusBarColor = Color.CYAN
        val typedValue = TypedValue()
        val theme = requireContext().theme
        theme.resolveAttribute(com.google.android.material.R.attr.colorSurface, typedValue, true)
        window?.navigationBarColor = typedValue.data

        if (Anilist.token != null) {
            binding.settingsLogin.setText(R.string.logout)
            binding.settingsLogin.setOnClickListener {
                Anilist.removeSavedToken(it.context)
                dismiss()
                startMainActivity(requireActivity())
            }
            binding.settingsUsername.text = Anilist.username
            binding.settingsUserAvatar.loadImage(Anilist.avatar)
        } else {
            binding.settingsUsername.visibility = View.GONE
            binding.settingsLogin.setText(R.string.login)
            binding.settingsLogin.setOnClickListener {
                dismiss()
                Anilist.loginIntent(requireActivity())
            }
        }

        if (requireContext().getSharedPreferences("Dantotsu", Context.MODE_PRIVATE)
                .getBoolean("incognito", false)) {
            binding.incognitoView1.visibility = View.GONE
            binding.incognitoView2.visibility = View.VISIBLE
        }
        else {
            binding.incognitoView1.visibility = View.VISIBLE
            binding.incognitoView2.visibility = View.GONE
        }
        binding.incognito1.setOnClickListener {
            context?.getSharedPreferences("Dantotsu", Context.MODE_PRIVATE)?.edit()
                ?.putBoolean("incognito", true)?.apply()
            incognitoNotification(requireContext())
            binding.incognitoView1.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction {
                    binding.incognitoView1.visibility = View.GONE
                    binding.incognitoView2.alpha = 0f
                    binding.incognitoView2.visibility = View.VISIBLE
                    binding.incognitoView2.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .start()
                }
                .start()
        }
        binding.incognito2.setOnClickListener {
            context?.getSharedPreferences("Dantotsu", Context.MODE_PRIVATE)?.edit()
                ?.putBoolean("incognito", false)?.apply()
            incognitoNotification(requireContext())
            binding.incognitoView2.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction {
                    binding.incognitoView2.visibility = View.GONE
                    binding.incognitoView1.alpha = 0f
                    binding.incognitoView1.visibility = View.VISIBLE
                    binding.incognitoView1.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .start()
                }
                .start()
        }
        binding.settingsExtensionSettings.setSafeOnClickListener {
            startActivity(Intent(activity, ExtensionsActivity::class.java))
            dismiss()
        }
        binding.settingsSettings.setSafeOnClickListener {
            startActivity(Intent(activity, SettingsActivity::class.java))
            dismiss()
        }
        binding.settingsAnilistSettings.setOnClickListener {
            openLinkInBrowser("https://anilist.co/settings/lists")
            dismiss()
        }
        binding.imageSearch.setOnClickListener {
            startActivity(Intent(activity, ImageSearchActivity::class.java))
            dismiss()
        }

        if (requireContext().getSharedPreferences("Dantotsu", Context.MODE_PRIVATE)
                .getBoolean("offlineMode", false)) {
            binding.downloadview1.visibility = View.GONE
            binding.downloadview2.visibility = View.VISIBLE
        }
        else {
            binding.downloadview1.visibility = View.VISIBLE
            binding.downloadview2.visibility = View.GONE
        }
        binding.download1.setOnClickListener {
            context?.getSharedPreferences("Dantotsu", Context.MODE_PRIVATE)?.edit()
                ?.putBoolean("offlineMode", true)?.apply()
                    binding.downloadview1.visibility = View.GONE
                    binding.downloadview2.visibility = View.VISIBLE
            offline()
        }
        binding.download2.setOnClickListener {
            context?.getSharedPreferences("Dantotsu", Context.MODE_PRIVATE)?.edit()
                ?.putBoolean("offlineMode", false)?.apply()
                    binding.downloadview2.visibility = View.GONE
                    binding.downloadview1.visibility = View.VISIBLE
            offline()
        }
    }
    fun offline() {
        when (pageType) {
            PageType.MANGA -> {
                val intent = Intent(activity, NoInternet::class.java)
                intent.putExtra(
                    "FRAGMENT_CLASS_NAME",
                    OfflineMangaFragment::class.java.name
                )
                startActivity(intent)
            }

            PageType.ANIME -> {
                val intent = Intent(activity, NoInternet::class.java)
                intent.putExtra(
                    "FRAGMENT_CLASS_NAME",
                    OfflineAnimeFragment::class.java.name
                )
                startActivity(intent)
            }

            PageType.HOME -> {
                val intent = Intent(activity, NoInternet::class.java)
                intent.putExtra("FRAGMENT_CLASS_NAME", OfflineFragment::class.java.name)
                startActivity(intent)
            }

            PageType.OfflineMANGA -> {
                val intent = Intent(activity, MainActivity::class.java)
                intent.putExtra("FRAGMENT_CLASS_NAME", MangaFragment::class.java.name)
                startActivity(intent)
            }

            PageType.OfflineHOME -> {
                val intent = Intent(activity, MainActivity::class.java)
                intent.putExtra(
                    "FRAGMENT_CLASS_NAME",
                    if (Anilist.token != null) HomeFragment::class.java.name else LoginFragment::class.java.name
                )
                startActivity(intent)
            }

            PageType.OfflineANIME -> {
                val intent = Intent(activity, MainActivity::class.java)
                intent.putExtra("FRAGMENT_CLASS_NAME", AnimeFragment::class.java.name)
                startActivity(intent)
            }
        }

        dismiss()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        enum class PageType {
            MANGA, ANIME, HOME, OfflineMANGA, OfflineANIME, OfflineHOME
        }

        fun newInstance(pageType: PageType): SettingsDialogFragment {
            val fragment = SettingsDialogFragment()
            val args = Bundle()
            args.putSerializable("pageType", pageType)
            fragment.arguments = args
            return fragment
        }
    }
}