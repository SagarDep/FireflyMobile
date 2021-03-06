package xyz.hisname.fireflyiii.ui.about

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.preference.PreferenceManager
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.danielstone.materialaboutlibrary.ConvenienceBuilder
import com.danielstone.materialaboutlibrary.MaterialAboutFragment
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard
import com.danielstone.materialaboutlibrary.model.MaterialAboutList
import kotlinx.android.synthetic.main.activity_base.*
import xyz.hisname.fireflyiii.R

class AboutFragment: MaterialAboutFragment() {

    private val sharedPref by lazy { PreferenceManager.getDefaultSharedPreferences(requireContext()) }
    private val serverVersion by lazy { sharedPref.getString("server_version","") ?: ""}
    private val apiVersion by lazy { sharedPref.getString("api_version","") ?: ""}
    private val userOs by lazy { sharedPref.getString("user_os","") ?: ""}

    override fun getMaterialAboutList(context: Context): MaterialAboutList{
        return createMaterialAboutList()
    }

    override fun getTheme() = R.style.AppTheme_MaterialAboutActivity_Fragment

    private fun createMaterialAboutList(): MaterialAboutList{
        val appCardBuilder = MaterialAboutCard.Builder()
        appCardBuilder.addItem(ConvenienceBuilder.createAppTitleItem(getString(R.string.app_name),
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_piggy_bank)))
                .addItem(ConvenienceBuilder.createVersionActionItem(requireContext(),
                ContextCompat.getDrawable(requireContext(),R.drawable.ic_cellphone),
                "Mobile Version",false))
                .addItem(MaterialAboutActionItem.Builder()
                        .text("Server Version")
                        .subText(serverVersion)
                        .icon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_server))
                        .build())
                .addItem(MaterialAboutActionItem.Builder()
                        .text("API Version")
                        .subText(apiVersion)
                        .icon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_web))
                        .build())
                .addItem(MaterialAboutActionItem.Builder()
                        .text("Operating System")
                        .subText(userOs)
                        .icon(setUserOsIcon())
                        .build())
                .build()

        val authorCardBuilder = MaterialAboutCard.Builder()
        authorCardBuilder.title("Author")
        authorCardBuilder.addItem(MaterialAboutActionItem.Builder()
                .text("Daniel Quah")
                .subText("emansih")
                .icon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_perm_identity_black_24dp))
                .setOnClickAction {
                    requireContext().startActivity(Intent(Intent.ACTION_VIEW, "https://github.com/emansih".toUri()))
                }.build())
                .addItem(MaterialAboutActionItem.Builder()
                        .text("View on Github")
                        .icon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_github_circle))
                        .setOnClickAction {
                            requireContext().startActivity(Intent(Intent.ACTION_VIEW, "https://github.com/emansih/FireflyMobile".toUri()))
                        }.build())

        return MaterialAboutList(appCardBuilder.build(), authorCardBuilder.build())
    }

    // Because why not?
    private fun setUserOsIcon(): Drawable?{
        return when {
            userOs.toLowerCase().contains("windows") -> ContextCompat.getDrawable(requireContext(), R.drawable.ic_windows)
            userOs.toLowerCase().contains("linux") -> ContextCompat.getDrawable(requireContext(), R.drawable.ic_linux)
            userOs.toLowerCase().contains("bsd") -> // yea... this is freebsd icon. sorry other BSDs
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_freebsd)
            else -> ContextCompat.getDrawable(requireContext(), R.drawable.ic_server)
        }
    }

    override fun onAttach(context: Context){
        super.onAttach(context)
        activity?.activity_toolbar?.title = "About"
    }

    override fun onResume() {
        super.onResume()
        activity?.activity_toolbar?.title = "About"
    }
}